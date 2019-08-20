package com.elitecore.corenetvertex.spr;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Stopwatch;
import com.elitecore.commons.base.TimeSource;
import com.elitecore.commons.threads.EliteThreadFactory;
import com.elitecore.corenetvertex.constants.ActionType;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.MonetaryBalanceType;
import com.elitecore.corenetvertex.core.alerts.AlertConstants;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.alerts.Alerts;
import com.elitecore.corenetvertex.core.transaction.Transaction;
import com.elitecore.corenetvertex.core.transaction.TransactionException;
import com.elitecore.corenetvertex.core.transaction.TransactionFactory;
import com.elitecore.corenetvertex.core.transaction.exception.TransactionErrorCode;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.spr.params.MonetaryRechargeData;
import com.elitecore.corenetvertex.spr.util.SPRUtil;

import javax.annotation.Nonnull;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import static com.elitecore.commons.base.DBUtility.closeQuietly;
import static com.elitecore.commons.logging.LogManager.getLogger;
import static java.util.Objects.nonNull;

public class MonetaryABMFOperationImpl implements MonetaryABMFOperation {

    private static final String MODULE = "MON-ABMF-OPR";
    protected static final int QUERY_TIMEOUT_ERROR_CODE = 1013;
    private static final String TABLE_NAME = "TBLM_MONETARY_BALANCE";
    private static final String SELECT_BALANCE_QUERY = "SELECT * FROM "+TABLE_NAME+" WHERE SUBSCRIBER_ID = ? ";
    private static final String INSERT_MONETARY_BALANCE_QUERY = "INSERT INTO TBLM_MONETARY_BALANCE (ID,SUBSCRIBER_ID,SERVICE_ID," +
            "AVAILABLE_BALANCE,INITIAL_BALANCE,TOTAL_RESERVATION,CREDIT_LIMIT,NEXT_BILL_CYCLE_CREDIT_LIMIT,VALID_FROM_DATE,VALID_TO_DATE,CURRENCY,TYPE,CREDIT_LIMIT_UPDATE_TIME,LAST_UPDATE_TIME,PARAM1,PARAM2) " +
            "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,CURRENT_TIMESTAMP,?,?)";
    private static final String UPDATE_MONETARY_BALANCE_QUERY = "UPDATE TBLM_MONETARY_BALANCE SET AVAILABLE_BALANCE = AVAILABLE_BALANCE + ?, " +
            "INITIAL_BALANCE = INITIAL_BALANCE + ?, VALID_TO_DATE = ?, LAST_UPDATE_TIME = CURRENT_TIMESTAMP WHERE SUBSCRIBER_ID = ? AND ID = ?";
    private static final String UPDATE_CREDIT_LIMIT_QUERY = "UPDATE TBLM_MONETARY_BALANCE SET CREDIT_LIMIT = ?, NEXT_BILL_CYCLE_CREDIT_LIMIT = ?, LAST_UPDATE_TIME = CURRENT_TIMESTAMP, CREDIT_LIMIT_UPDATE_TIME = ? WHERE SUBSCRIBER_ID = ? AND ID = ?";
    private static final String RECHARGE_MONETARY_BALANCE_QUERY = "UPDATE TBLM_MONETARY_BALANCE SET " +
            "AVAILABLE_BALANCE = AVAILABLE_BALANCE -? + ?, VALID_TO_DATE = ? WHERE SUBSCRIBER_ID = ? AND ID = ?";
    private static final String RESERVE_BALANCE_QUERY = "UPDATE TBLM_MONETARY_BALANCE SET TOTAL_RESERVATION = TOTAL_RESERVATION + ?, " +
            "LAST_UPDATE_TIME = CURRENT_TIMESTAMP WHERE SUBSCRIBER_ID = ? AND ID = ?";
    private static final String DIRECT_DEBIT_QUERY = "UPDATE TBLM_MONETARY_BALANCE SET AVAILABLE_BALANCE = AVAILABLE_BALANCE - ?, " +
            "LAST_UPDATE_TIME = CURRENT_TIMESTAMP WHERE SUBSCRIBER_ID = ? AND ID = ?";
    private static final String REPORT_AND_RESERVE_BALANCE_QUERY = "UPDATE TBLM_MONETARY_BALANCE SET TOTAL_RESERVATION = TOTAL_RESERVATION + ?, " +
            "AVAILABLE_BALANCE = AVAILABLE_BALANCE - ?, LAST_UPDATE_TIME = CURRENT_TIMESTAMP WHERE SUBSCRIBER_ID = ? AND ID = ?";
    private static final String DELETE_MONETARY_BALANCE_QUERY = "DELETE FROM TBLM_MONETARY_BALANCE WHERE SUBSCRIBER_ID = ?";
    private static final String MONETARY_BALANCE_HISTORY_QUERY = "INSERT INTO " +
            "TBLM_MONETARY_BALANCE_HISTORY " +
            "(CREATE_DATE, ID," +
            "SUBSCRIBER_ID," +
            "SERVICE_ID," +
            "AVAILABLE_BALANCE," +
            "INITIAL_BALANCE," +
            "TOTAL_RESERVATION," +
            "CREDIT_LIMIT," +
            "NEXT_BILL_CYCLE_CREDIT_LIMIT," +
            "VALID_FROM_DATE," +
            "VALID_TO_DATE," +
            "CURRENCY," +
            "TYPE," +
            "LAST_UPDATE_TIME," +
            "CREDIT_LIMIT_UPDATE_TIME," +
            "PARAM1," +
            "PARAM2)" +
            "SELECT " +
            "CURRENT_TIMESTAMP, " +
            "?," +
            "SUBSCRIBER_ID," +
            "SERVICE_ID," +
            "AVAILABLE_BALANCE," +
            "INITIAL_BALANCE," +
            "TOTAL_RESERVATION," +
            "CREDIT_LIMIT," +
            "NEXT_BILL_CYCLE_CREDIT_LIMIT," +
            "VALID_FROM_DATE," +
            "VALID_TO_DATE," +
            "CURRENCY," +
            "TYPE," +
            "LAST_UPDATE_TIME," +
            "CREDIT_LIMIT_UPDATE_TIME," +
            "PARAM1," +
            "PARAM2" +
            " FROM TBLM_MONETARY_BALANCE" +
            " WHERE SUBSCRIBER_ID = ?";
    private AlertListener alertListener;
    private int queryTimeout;
    private int maxQueryTimeoutCount;
    private AtomicInteger iTotalQueryTimeoutCount;
    private EDRListener balanceEDRListener;


    private CSVFailOverOperation<MonetaryOperationData> csvfailOverOperation;


    public MonetaryABMFOperationImpl(AlertListener alertListener, int queryTimeout, RecordProcessor<MonetaryOperationData> recordProcessor, EDRListener balanceEDRListener){
        this.alertListener = alertListener;
        this.queryTimeout = queryTimeout;
        this.maxQueryTimeoutCount = 200;
        this.iTotalQueryTimeoutCount = new AtomicInteger(0);
        this.balanceEDRListener = balanceEDRListener;
        csvfailOverOperation = new CSVFailOverOperation<>(recordProcessor, new ScheduledThreadPoolExecutor(1, new EliteThreadFactory("MONETARY-ABMF-FAILOVER-CSV",
                "MONETARY-ABMF-FAILOVER-CSV", Thread.NORM_PRIORITY)));
    }

    @Nonnull
    public SubscriberMonetaryBalance getMonetaryBalance(String subscriberId, Predicate<MonetaryBalance> predicate, TransactionFactory transactionFactory) throws OperationFailedException {
        SubscriberMonetaryBalance subscriberMonetaryBalance = new SubscriberMonetaryBalance(TimeSource.systemTimeSource());

        List<MonetaryBalance> serviceMonitoryBalances = getMonetaryBalances(subscriberId, predicate, transactionFactory);

        if(serviceMonitoryBalances != null) {
            for(MonetaryBalance monetaryBalance : serviceMonitoryBalances) {
                subscriberMonetaryBalance.addMonitoryBalances(monetaryBalance);
            }
        }

        return subscriberMonetaryBalance;
    }

    private List<MonetaryBalance> getMonetaryBalances(String subscriberId, Predicate<MonetaryBalance> predicate, TransactionFactory transactionFactory) throws OperationFailedException {
        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Fetching monitory balance information for subscriber ID: " + subscriberId);
        }


        if (transactionFactory.isAlive() == false) {
            throw new OperationFailedException("Unable to fetch balance for subscriber ID: " + subscriberId
                    + " Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        Transaction transaction = transactionFactory.createTransaction();

        if (transaction == null) {
            throw new OperationFailedException("Unable to fetch balance for subscriber ID: " + subscriberId
                    + " Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        PreparedStatement psforGetBalance = null;
        ResultSet resultSet = null;
        List<MonetaryBalance> monetaryBalances = null;
        try {
            transaction.begin();
            psforGetBalance = transaction.prepareStatement(SELECT_BALANCE_QUERY);
            psforGetBalance.setString(1, subscriberId);
            psforGetBalance.setQueryTimeout(queryTimeout);

            long currentTime = getCurrentTime();
            resultSet = psforGetBalance.executeQuery();
            long queryExecutionTime = getCurrentTime() - currentTime;

            if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

                alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
                        "DB Query execution time is high while usage information. "
                                + "Last Query execution time: " + queryExecutionTime + " ms");

                if (getLogger().isWarnLogLevel()) {
                    getLogger().warn(MODULE, "DB select query execution time getting high, Last Query execution time = " + queryExecutionTime
                            + " ms.");
                }
            }

            while (resultSet.next()) {
                String id = resultSet.getString("ID");
                String serviceId = resultSet.getString("SERVICE_ID");
                double availBalance = resultSet.getDouble("AVAILABLE_BALANCE");
                double totalBalance = resultSet.getDouble("INITIAL_BALANCE");
                double totalReservation = resultSet.getDouble("TOTAL_RESERVATION");
                long creditLimit = resultSet.getLong("CREDIT_LIMIT");
                long nextBillingCycleCreditLimit = resultSet.getLong("NEXT_BILL_CYCLE_CREDIT_LIMIT");
                Timestamp validFromDateInTimeStamp = resultSet.getTimestamp("VALID_FROM_DATE");
                Timestamp validToDateInTimeStamp = resultSet.getTimestamp("VALID_TO_DATE");
                String currency = resultSet.getString("CURRENCY");
                String type = resultSet.getString("TYPE");
                long lastUpdateTime = resultSet.getTimestamp("LAST_UPDATE_TIME").getTime();
                long creditLimitUpdateTime = nonNull(resultSet.getTimestamp("CREDIT_LIMIT_UPDATE_TIME"))?resultSet.getTimestamp("CREDIT_LIMIT_UPDATE_TIME").getTime():0;
                String parameter1 = resultSet.getString("PARAM1");
                String parameter2 = resultSet.getString("PARAM2");

                long validFromDate = 0;
                if(validFromDateInTimeStamp != null) {
                    validFromDate = validFromDateInTimeStamp.getTime();
                }

                long validToDate = 0;
                if(validToDateInTimeStamp != null) {
                    validToDate = validToDateInTimeStamp.getTime();
                }

                MonetaryBalance monetaryBalance = new MonetaryBalance(id,
                        subscriberId,
                        serviceId,
                        availBalance,
                        totalBalance,
                        totalReservation,
                        creditLimit,
                        nextBillingCycleCreditLimit,
                        validFromDate,
                        validToDate,
                        currency,
                        type,
                        lastUpdateTime,
                        creditLimitUpdateTime,
                        parameter1,
                        parameter2);

                if(predicate.test(monetaryBalance)==false){
                    continue;
                }

                if (monetaryBalances == null) {
                    monetaryBalances = new ArrayList<>();
                }

                monetaryBalances.add(monetaryBalance);
            }

            if (Collectionz.isNullOrEmpty(monetaryBalances) && getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Balance not exist with subscriber ID: " + subscriberId);
            }

        } catch (TransactionException e) {
            transaction.rollback();
            handleTransactionException("getting monetary balance", e, subscriberId, transaction);
        } catch (SQLException e) {
            transaction.rollback();
            handleSQLException("getting monetary balance", e, transaction);
        } catch (Exception e) {
            transaction.rollback();
            handleOperationalError(e);
        } finally {
            closeQuietly(resultSet);
            closeQuietly(psforGetBalance);
            endTransaction(transaction);
        }

        if (monetaryBalances!=null){
            updateNextBillCycleCreditLimit(monetaryBalances, transactionFactory);
        }

        return monetaryBalances;
    }

    private void updateNextBillCycleCreditLimit(List<MonetaryBalance> monetaryBalances, TransactionFactory transactionFactory) throws OperationFailedException{
        for(MonetaryBalance monetaryBalance : monetaryBalances){
            if(MonetaryBalanceType.DEFAULT.name().equals(monetaryBalance.getType())==false){
                return;
            }

            if(monetaryBalance.getNextBillingCycleCreditLimit()<=0){
                return;
            }

            if (monetaryBalance.getCreditLimitUpdateTime() > System.currentTimeMillis()
                    || monetaryBalance.getCreditLimitUpdateTime() == 0) {
                return;
            }

            if (monetaryBalance.getNextBillingCycleCreditLimit() == monetaryBalance.getCreditLimit()) {
                return;
            }

            monetaryBalance.setCreditLimit(monetaryBalance.getNextBillingCycleCreditLimit());
            monetaryBalance.setCreditLimitUpdateTime(0);
            monetaryBalance.setNextBillingCycleCreditLimit(0);

            this.updateCreditLimit(monetaryBalance.getSubscriberId(), monetaryBalance, transactionFactory);
            MonetaryBalance previousMonetaryBalance = monetaryBalance.copy();
            monetaryBalance.setAvailBalance(0);
            if(nonNull(balanceEDRListener)){
                balanceEDRListener.updateMonetaryEDR(new SubscriberMonetaryBalanceWrapper(monetaryBalance, previousMonetaryBalance,
                        null, CommonConstants.UPDATE_CREDIT_LIMIT, ActionType.UPDATE.name(),null), null, CommonConstants.UPDATE_CREDIT_LIMIT);
            }
            return;
        }
    }

    @Nonnull
    public void addBalance(String subscriberId, MonetaryBalance monetaryBalance, TransactionFactory transactionFactory) throws OperationFailedException {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Adding monetary balance for subscriber-identifier : " + subscriberId);
        }

        if (transactionFactory.isAlive() == false) {
            throw new OperationFailedException("Unable to perform add balance operation. Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        Transaction transaction = transactionFactory.createTransaction();

        try {
            transaction.begin();

            addBalance(monetaryBalance, transaction);
        } catch (SQLException e) {
            handleSQLException("Adding monetary balance",e, transaction);
        } catch (TransactionException tE) {
            handleOperationalError(tE, transaction);
        } catch (Exception e) {
            handleOperationalError(e);
        } finally {
            endTransaction(transaction);
        }

    }

    public void addBalance(MonetaryBalance monetaryBalance, Transaction transaction) throws OperationFailedException, SQLException, TransactionException {
        if (transaction == null) {
            throw new OperationFailedException("Unable to perform add balance operation. Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }
        PreparedStatement psforInsert = null;
        try {
            psforInsert = transaction.prepareStatement(INSERT_MONETARY_BALANCE_QUERY);
            psforInsert.setQueryTimeout(queryTimeout);
            psforInsert.setString(1, monetaryBalance.getId());
            psforInsert.setString(2, monetaryBalance.getSubscriberId());
            psforInsert.setString(3, monetaryBalance.getServiceId());
            psforInsert.setDouble(4, monetaryBalance.getAvailBalance());
            psforInsert.setDouble(5, monetaryBalance.getInitialBalance());
            psforInsert.setDouble(6, monetaryBalance.getTotalReservation());
            psforInsert.setLong(7, monetaryBalance.getCreditLimit());
            psforInsert.setLong(8, monetaryBalance.getNextBillingCycleCreditLimit());
            psforInsert.setTimestamp(9, new Timestamp(monetaryBalance.getValidFromDate()));
            psforInsert.setTimestamp(10, new Timestamp(monetaryBalance.getValidToDate()));
            psforInsert.setString(11, monetaryBalance.getCurrency());
            psforInsert.setString(12, monetaryBalance.getType());
            psforInsert.setTimestamp(13, new Timestamp(monetaryBalance.getCreditLimitUpdateTime()));
            psforInsert.setString(14, monetaryBalance.getParameter1());
            psforInsert.setString(15, monetaryBalance.getParameter2());

            long queryExecutionTime = System.currentTimeMillis();
            psforInsert.executeUpdate();
            queryExecutionTime = System.currentTimeMillis() - queryExecutionTime;
            if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

                alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
                        "DB Query execution time is high while inserting monetary balance information. "
                                + "Last Query execution time: " + queryExecutionTime + " ms");

                if (getLogger().isWarnLogLevel()) {
                    getLogger().warn(MODULE, "OPEN-DB update query execution time getting high, Last Query execution time = "
                            + queryExecutionTime + " milliseconds.");
                }
            }
            psforInsert.clearParameters();
            iTotalQueryTimeoutCount.set(0);

            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Add monetary balance operation successfully completed.");
            }
        } finally {
            closeQuietly(psforInsert);
        }
    }

    public void addMonetaryBalance(MonetaryBalance monetaryBalance, Transaction transaction) throws OperationFailedException {
        if (transaction == null) {
            throw new OperationFailedException("Unable to perform add balance operation. Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }
        PreparedStatement psforInsert = null;
        try {
            psforInsert = transaction.prepareStatement(INSERT_MONETARY_BALANCE_QUERY);
            psforInsert.setQueryTimeout(queryTimeout);
            psforInsert.setString(1, monetaryBalance.getId());
            psforInsert.setString(2, monetaryBalance.getSubscriberId());
            psforInsert.setString(3, monetaryBalance.getServiceId());
            psforInsert.setDouble(4, monetaryBalance.getAvailBalance());
            psforInsert.setDouble(5, monetaryBalance.getInitialBalance());
            psforInsert.setDouble(6, monetaryBalance.getTotalReservation());
            psforInsert.setLong(7, monetaryBalance.getCreditLimit());
            psforInsert.setLong(8, monetaryBalance.getNextBillingCycleCreditLimit());
            psforInsert.setTimestamp(9, new Timestamp(monetaryBalance.getValidFromDate()));
            psforInsert.setTimestamp(10, new Timestamp(monetaryBalance.getValidToDate()));
            psforInsert.setString(11, monetaryBalance.getCurrency());
            psforInsert.setString(12, monetaryBalance.getType());
            psforInsert.setTimestamp(13, new Timestamp(monetaryBalance.getCreditLimitUpdateTime()));
            psforInsert.setString(14, monetaryBalance.getParameter1());
            psforInsert.setString(15, monetaryBalance.getParameter2());

            long queryExecutionTime = System.currentTimeMillis();
            psforInsert.executeUpdate();
            queryExecutionTime = System.currentTimeMillis() - queryExecutionTime;
            if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

                alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
                        "DB Query execution time is high while inserting monetary balance information. "
                                + "Last Query execution time: " + queryExecutionTime + " ms");

                if (getLogger().isWarnLogLevel()) {//NOSONAR
                    getLogger().warn(MODULE, "OPEN-DB update query execution time getting high, Last Query execution time = "
                            + queryExecutionTime + " milliseconds.");
                }
            }
            psforInsert.clearParameters();
            iTotalQueryTimeoutCount.set(0);

            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Add monetary balance operation successfully completed.");
            }
        } catch (SQLException e) {
            handleOperationalError(e, transaction);
        } catch (TransactionException tE) {
            handleOperationalError(tE, transaction);
        } catch (Exception e) {
            handleOperationalError(e);
        } finally {
            closeQuietly(psforInsert);
        }
    }

    public void insert(String subscriberId, Collection<MonetaryBalance> monetaryBalances, Transaction transaction) throws TransactionException, OperationFailedException {

        PreparedStatement psforInsert = null;

        try {

            psforInsert = transaction.prepareStatement(INSERT_MONETARY_BALANCE_QUERY);
            psforInsert.setQueryTimeout(queryTimeout);

            for (MonetaryBalance monetaryBalance : monetaryBalances) {

                setBalanceToPSForInsert(psforInsert, monetaryBalance, subscriberId);

                long queryExecutionTime = getCurrentTime();
                psforInsert.executeUpdate();
                queryExecutionTime = getCurrentTime() - queryExecutionTime;
                if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

                    alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
                            "DB Query execution time is high while inserting monetary balance information. "
                                    + "Last Query execution time: " + queryExecutionTime + " ms");

                    if (getLogger().isWarnLogLevel()) {//NOSONAR
                        getLogger().warn(MODULE, "OPEN-DB update query execution time getting high, Last Query execution time = "
                                + queryExecutionTime + " milliseconds.");
                    }
                }
            }

            iTotalQueryTimeoutCount.set(0);
            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Insert usage operation successfully completed");
            }

        } catch (SQLException e) {
            handleOperationalError(e, transaction);
        } catch (Exception e) {
            handleOperationalError(e);
        } finally {
            closeQuietly(psforInsert);
        }
    }

    protected  void setBalanceToPSForInsert(PreparedStatement psforInsert, MonetaryBalance monetaryBalance, String subscriberId ) throws SQLException {

        psforInsert.setString(1, generateId());
        psforInsert.setString(2, subscriberId);
        psforInsert.setString(3, monetaryBalance.getServiceId());
        psforInsert.setDouble(4, monetaryBalance.getAvailBalance());
        psforInsert.setDouble(5, monetaryBalance.getInitialBalance());
        psforInsert.setDouble(6, monetaryBalance.getTotalReservation());
        psforInsert.setLong(7, monetaryBalance.getCreditLimit());
        psforInsert.setLong(8, monetaryBalance.getNextBillingCycleCreditLimit());
        psforInsert.setTimestamp(9, new Timestamp(monetaryBalance.getValidFromDate()));
        psforInsert.setTimestamp(10, new Timestamp(monetaryBalance.getValidToDate()));
        psforInsert.setString(11, monetaryBalance.getCurrency());
        psforInsert.setString(12, monetaryBalance.getType());
        psforInsert.setTimestamp(13, new Timestamp(monetaryBalance.getCreditLimitUpdateTime()));
        psforInsert.setString(14, monetaryBalance.getParameter1());
        psforInsert.setString(15, monetaryBalance.getParameter2());

    }

    protected String generateId() {
        return UUID.randomUUID().toString();
    }

    public void  updateBalance(String subscriberId, MonetaryBalance monetaryBalance, TransactionFactory transactionFactory) throws OperationFailedException{

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Update monetary balance for subscriber-identifier : " + subscriberId);
        }

        if (transactionFactory.isAlive() == false) {
            throw new OperationFailedException("Unable to perform update balance operation. Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        Transaction transaction = transactionFactory.createTransaction();

        if (transaction == null) {
            throw new OperationFailedException("Unable to perform update balance operation. Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        try {
            transaction.begin();

            updateBalance( monetaryBalance, transaction);

        } catch (TransactionException tE) {
            transaction.rollback();
            handleOperationalError(tE, transaction);
        } catch (SQLException e) {
            transaction.rollback();
            handleSQLException("updating balance", e, transaction);
        } catch (Exception e) {
            transaction.rollback();
            handleOperationalError(e);
        } finally {
            endTransaction(transaction);
        }
    }

    public void updateBalance(MonetaryBalance monetaryBalance, Transaction transaction) throws OperationFailedException, SQLException, TransactionException {
        if (transaction == null) {
            throw new OperationFailedException("Unable to perform add balance operation. Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }
        PreparedStatement psforUpdate = null;
        try {
            psforUpdate = transaction.prepareStatement(UPDATE_MONETARY_BALANCE_QUERY);
            psforUpdate.setQueryTimeout(queryTimeout);

            psforUpdate.setDouble(1, monetaryBalance.getAvailBalance());
            psforUpdate.setDouble(2, monetaryBalance.getInitialBalance());
            psforUpdate.setTimestamp(3, new Timestamp(monetaryBalance.getValidToDate()));
            psforUpdate.setString(4, monetaryBalance.getSubscriberId());
            psforUpdate.setString(5, monetaryBalance.getId());

            long queryExecutionTime = System.currentTimeMillis();
            psforUpdate.executeUpdate();
            queryExecutionTime = System.currentTimeMillis() - queryExecutionTime;
            if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

                alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
                        "DB Query execution time is high while updating monetary balance information. "
                                + "Last Query execution time: " + queryExecutionTime + " ms");

                if (getLogger().isWarnLogLevel()) {//NOSONAR
                    getLogger().warn(MODULE, "OPEN-DB update query execution time getting high, Last Query execution time = "
                            + queryExecutionTime + " milliseconds.");
                }
            }
            psforUpdate.clearParameters();
            iTotalQueryTimeoutCount.set(0);
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Update monetary balance operation successfully completed for subscriber-identifier : " + monetaryBalance.getSubscriberId());
            }
        } finally {
            closeQuietly(psforUpdate);
        }
    }

    public void  updateCreditLimit(String subscriberId, MonetaryBalance monetaryBalance, TransactionFactory transactionFactory) throws OperationFailedException{
        PreparedStatement psforUpdate = null;

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Update credit limit for subscriber-identifier : " + subscriberId);
        }

        if (transactionFactory.isAlive() == false) {
            throw new OperationFailedException("Unable to perform update credit limit operation. Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        Transaction transaction = transactionFactory.createTransaction();

        if (transaction == null) {
            throw new OperationFailedException("Unable to perform credit limit operation. Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        try {
            transaction.begin();

            psforUpdate = transaction.prepareStatement(UPDATE_CREDIT_LIMIT_QUERY);
            psforUpdate.setQueryTimeout(queryTimeout);

            psforUpdate.setLong(1, monetaryBalance.getCreditLimit());
            psforUpdate.setLong(2, monetaryBalance.getNextBillingCycleCreditLimit());
            psforUpdate.setTimestamp(3, new Timestamp(monetaryBalance.getCreditLimitUpdateTime()));
            psforUpdate.setString(4, monetaryBalance.getSubscriberId());
            psforUpdate.setString(5, monetaryBalance.getId());

            long queryExecutionTime = System.currentTimeMillis();
            psforUpdate.executeUpdate();
            queryExecutionTime = System.currentTimeMillis() - queryExecutionTime;
            if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

                alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
                        "DB Query execution time is high while updating credit limit information. "
                                + "Last Query execution time: " + queryExecutionTime + " ms");

                if (getLogger().isWarnLogLevel()) {//NOSONAR
                    getLogger().warn(MODULE, "OPEN-DB update query execution time getting high, Last Query execution time = "
                            + queryExecutionTime + " milliseconds.");
                }
            }
            psforUpdate.clearParameters();
            iTotalQueryTimeoutCount.set(0);
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Update credit limit operation successfully completed for subscriber-identifier : " + monetaryBalance.getSubscriberId());
            }
        } catch (SQLException e) {
            transaction.rollback();
            handleOperationalError(e, transaction);
        } catch (TransactionException tE) {
            transaction.rollback();
            handleOperationalError(tE, transaction);
        } catch (Exception e) {
            transaction.rollback();
            handleOperationalError(e);
        } finally {
            closeQuietly(psforUpdate);
            endTransaction(transaction);
        }
    }

    public void rechargeMonetaryBalance(MonetaryRechargeData monetaryRechargeData, TransactionFactory transactionFactory) throws OperationFailedException{
        PreparedStatement psforUpdate = null;

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Recharge monetary balance for subscriber-identifier : " + monetaryRechargeData.getSubscriberId());
        }

        if (transactionFactory.isAlive() == false) {
            throw new OperationFailedException("Unable to perform Recharge monetary balance operation. Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        Transaction transaction = transactionFactory.createTransaction();

        if (transaction == null) {
            throw new OperationFailedException("Unable to perform Recharge monetary balance operation. Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        try {
            transaction.begin();

            psforUpdate = transaction.prepareStatement(RECHARGE_MONETARY_BALANCE_QUERY);
            psforUpdate.setQueryTimeout(queryTimeout);

            psforUpdate.setDouble(1, monetaryRechargeData.getPrice().doubleValue());
            psforUpdate.setDouble(2, monetaryRechargeData.getAmount().doubleValue());
            psforUpdate.setTimestamp(3, new Timestamp(monetaryRechargeData.getExtendedValidity()));
            psforUpdate.setString(4, monetaryRechargeData.getMonetaryBalance().getSubscriberId());
            psforUpdate.setString(5, monetaryRechargeData.getMonetaryBalance().getId());

            long queryExecutionTime = System.currentTimeMillis();
            psforUpdate.executeUpdate();
            queryExecutionTime = System.currentTimeMillis() - queryExecutionTime;
            if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

                alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
                        "DB Query execution time is high while Recharging monetary balance. "
                                + "Last Query execution time: " + queryExecutionTime + " ms");

                if (getLogger().isWarnLogLevel()) {//NOSONAR
                    getLogger().warn(MODULE, "OPEN-DB update query execution time getting high, Last Query execution time = "
                            + queryExecutionTime + " milliseconds.");
                }
            }
            psforUpdate.clearParameters();
            iTotalQueryTimeoutCount.set(0);
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Recharge monetary balance operation successfully completed for subscriber-identifier : " + monetaryRechargeData.getMonetaryBalance().getSubscriberId());
            }
        } catch (SQLException e) {
            transaction.rollback();
            handleOperationalError(e, transaction);
        } catch (TransactionException e) {
            transaction.rollback();
            handleOperationalError(e, transaction);
        } catch (Exception e) {
            transaction.rollback();
            handleOperationalError(e);
        } finally {
            closeQuietly(psforUpdate);
            endTransaction(transaction);
        }
    }

    public void rechargeMonetaryBalance(MonetaryRechargeData monetaryRechargeData, Transaction transaction) throws OperationFailedException{
        PreparedStatement psforUpdate = null;

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Recharge monetary balance for subscriber-identifier : " + monetaryRechargeData.getSubscriberId());
        }

        try {

            psforUpdate = transaction.prepareStatement(RECHARGE_MONETARY_BALANCE_QUERY);
            psforUpdate.setQueryTimeout(queryTimeout);

            psforUpdate.setDouble(1, monetaryRechargeData.getPrice().doubleValue());
            psforUpdate.setDouble(2, monetaryRechargeData.getAmount().doubleValue());
            psforUpdate.setTimestamp(3, new Timestamp(monetaryRechargeData.getExtendedValidity()));
            psforUpdate.setString(4, monetaryRechargeData.getMonetaryBalance().getSubscriberId());
            psforUpdate.setString(5, monetaryRechargeData.getMonetaryBalance().getId());

            long queryExecutionTime = System.currentTimeMillis();
            psforUpdate.executeUpdate();
            queryExecutionTime = System.currentTimeMillis() - queryExecutionTime;
            if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

                alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
                        "DB Query execution time is high while Recharging monetary balance. "
                                + "Last Query execution time: " + queryExecutionTime + " ms");

                if (getLogger().isWarnLogLevel()) {//NOSONAR
                    getLogger().warn(MODULE, "OPEN-DB update query execution time getting high, Last Query execution time = "
                            + queryExecutionTime + " milliseconds.");
                }
            }
            psforUpdate.clearParameters();
            iTotalQueryTimeoutCount.set(0);
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Recharge monetary balance operation successfully completed for subscriber-identifier : " + monetaryRechargeData.getMonetaryBalance().getSubscriberId());
            }
        } catch (SQLException e) {
            handleOperationalError(e, transaction);
        } catch (Exception e) {
            handleOperationalError(e);
        } finally {
            closeQuietly(psforUpdate);
        }
    }

    public void reserveBalance(String subscriberId, List<MonetaryBalance> lstmonetaryBalance, TransactionFactory transactionFactory) throws OperationFailedException{
        PreparedStatement psforReserve = null;

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Reserve monetary balance for subscriber-identifier : " + subscriberId);
        }

        if (transactionFactory.isAlive() == false) {
            csvfailOverOperation.doFailover(new MonetaryOperationData(lstmonetaryBalance, subscriberId, ABMFOperationType.RESERVE));
            throw new OperationFailedException("Unable to perform reserve balance operation. Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        Transaction transaction = transactionFactory.createTransaction();

        if (transaction == null) {
            csvfailOverOperation.doFailover(new MonetaryOperationData(lstmonetaryBalance, subscriberId, ABMFOperationType.RESERVE));
            throw new OperationFailedException("Unable to perform reserve balance operation. Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        try {
            transaction.begin();
            psforReserve = transaction.prepareStatement(RESERVE_BALANCE_QUERY);

            for (MonetaryBalance monetaryBalance : lstmonetaryBalance) {

                psforReserve.setQueryTimeout(queryTimeout);

                psforReserve.setDouble(1, monetaryBalance.getTotalReservation());
                psforReserve.setString(2, monetaryBalance.getSubscriberId());
                psforReserve.setString(3, monetaryBalance.getId());

                long queryExecutionTime = System.currentTimeMillis();
                psforReserve.executeUpdate();
                queryExecutionTime = System.currentTimeMillis() - queryExecutionTime;
                if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

                    alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
                            "DB Query execution time is high while reserving monetary balance information. "
                                    + "Last Query execution time: " + queryExecutionTime + " ms");

                    if (getLogger().isWarnLogLevel()) {//NOSONAR
                        getLogger().warn(MODULE, "OPEN-DB update query execution time getting high, Last Query execution time = "
                                + queryExecutionTime + " milliseconds.");
                    }
                }
                psforReserve.clearParameters();
            }
            iTotalQueryTimeoutCount.set(0);
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Reserve balance operation successfully completed.");
            }
        } catch (SQLException e) {
            csvfailOverOperation.doFailover(new MonetaryOperationData(lstmonetaryBalance, subscriberId, ABMFOperationType.RESERVE));
            transaction.rollback();
            handleOperationalError(e, transaction);
        } catch (TransactionException tE) {
            csvfailOverOperation.doFailover(new MonetaryOperationData(lstmonetaryBalance, subscriberId, ABMFOperationType.RESERVE));
            transaction.rollback();
            handleOperationalError(tE, transaction);
        } catch (Exception e) {
            csvfailOverOperation.doFailover(new MonetaryOperationData(lstmonetaryBalance, subscriberId, ABMFOperationType.RESERVE));
            transaction.rollback();
            handleOperationalError(e);
        } finally {
            closeQuietly(psforReserve);
            endTransaction(transaction);
        }
    }

    public void directDebitBalance(String subscriberId, List<MonetaryBalance> lstmonetaryBalance, TransactionFactory transactionFactory) throws OperationFailedException{
        PreparedStatement psforDebit = null;

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Direct debit monetary balance for subscriber-identifier : " + subscriberId);
        }

        if (transactionFactory.isAlive() == false) {
            csvfailOverOperation.doFailover(new MonetaryOperationData(lstmonetaryBalance, subscriberId, ABMFOperationType.DIRECT_DEBIT));
            throw new OperationFailedException("Unable to perform direct debit balance operation. Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        Transaction transaction = transactionFactory.createTransaction();

        if (transaction == null) {
            csvfailOverOperation.doFailover(new MonetaryOperationData(lstmonetaryBalance, subscriberId, ABMFOperationType.DIRECT_DEBIT));
            throw new OperationFailedException("Unable to perform direct debit balance operation. Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        try {
            transaction.begin();
            psforDebit = transaction.prepareStatement(DIRECT_DEBIT_QUERY);

            for (MonetaryBalance monetaryBalance : lstmonetaryBalance) {

                executeDirectDebit(psforDebit,
                        monetaryBalance.getAvailBalance(),
                        monetaryBalance.getSubscriberId(),
                        monetaryBalance.getId());
            }
            iTotalQueryTimeoutCount.set(0);
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Direct debit balance operation successfully completed.");
            }
        } catch (SQLException e) {
            csvfailOverOperation.doFailover(new MonetaryOperationData(lstmonetaryBalance, subscriberId, ABMFOperationType.DIRECT_DEBIT));
            transaction.rollback();
            handleOperationalError(e, transaction);
        } catch (TransactionException tE) {
            csvfailOverOperation.doFailover(new MonetaryOperationData(lstmonetaryBalance, subscriberId, ABMFOperationType.DIRECT_DEBIT));
            transaction.rollback();
            handleOperationalError(tE, transaction);
        } catch (Exception e) {
            csvfailOverOperation.doFailover(new MonetaryOperationData(lstmonetaryBalance, subscriberId, ABMFOperationType.DIRECT_DEBIT));
            transaction.rollback();
            handleOperationalError(e);
        } finally {
            closeQuietly(psforDebit);
            endTransaction(transaction);
        }
    }

    private void executeDirectDebit(PreparedStatement psforDebit, double availBalance, String subscriberId2, String id) throws SQLException {
        psforDebit.setQueryTimeout(queryTimeout);

        psforDebit.setDouble(1, availBalance);
        psforDebit.setString(2, subscriberId2);
        psforDebit.setString(3, id);

        long queryExecutionTime = System.currentTimeMillis();
        psforDebit.executeUpdate();
        queryExecutionTime = System.currentTimeMillis() - queryExecutionTime;
        if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

            alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
                    "DB Query execution time is high while debiting monetary balance information. "
                            + "Last Query execution time: " + queryExecutionTime + " ms");

            if (getLogger().isWarnLogLevel()) {//NOSONAR
                getLogger().warn(MODULE, "OPEN-DB update query execution time getting high, Last Query execution time = "
                        + queryExecutionTime + " milliseconds.");
            }
        }
        psforDebit.clearParameters();
    }

    public void directDebitBalance(String subscriberId, String id, double amount,Transaction transaction) throws OperationFailedException{
        PreparedStatement psforDebit = null;

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Direct debit monetary balance for subscriber-identifier : " + subscriberId);
        }

        if (transaction == null) {
            throw new OperationFailedException("Unable to perform direct debit balance operation. Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        try {
            psforDebit = transaction.prepareStatement(DIRECT_DEBIT_QUERY);

            executeDirectDebit(psforDebit, amount, subscriberId, id);

            iTotalQueryTimeoutCount.set(0);
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Direct debit balance operation successfully completed.");
            }
        } catch (SQLException e) {
            transaction.rollback();
            handleOperationalError(e, transaction);
        } catch (TransactionException tE) {
            transaction.rollback();
            handleOperationalError(tE, transaction);
        } catch (Exception e) {
            transaction.rollback();
            handleOperationalError(e);
        } finally {
            closeQuietly(psforDebit);
            endTransaction(transaction);
        }
    }



    public void reportAndReserveBalance(String subscriberId, List<MonetaryBalance> lstmonetaryBalance, TransactionFactory transactionFactory) throws OperationFailedException{
        PreparedStatement psforReportAndReserve = null;

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Report and reserve monetary balance for subscriber-identifier : " + subscriberId);
        }

        if (transactionFactory.isAlive() == false) {
            csvfailOverOperation.doFailover(new MonetaryOperationData(lstmonetaryBalance, subscriberId, ABMFOperationType.REPORT_AND_RESERVE));
            throw new OperationFailedException("Unable to perform report and reserve balance operation. Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        Transaction transaction = transactionFactory.createTransaction();

        if (transaction == null) {
            csvfailOverOperation.doFailover(new MonetaryOperationData(lstmonetaryBalance, subscriberId, ABMFOperationType.REPORT_AND_RESERVE));
            throw new OperationFailedException("Unable to perform report and reserve balance operation. Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        try {
            transaction.begin();
            psforReportAndReserve = transaction.prepareStatement(REPORT_AND_RESERVE_BALANCE_QUERY);

            for (MonetaryBalance monetaryBalance : lstmonetaryBalance) {

                psforReportAndReserve.setQueryTimeout(queryTimeout);

                psforReportAndReserve.setDouble(1, monetaryBalance.getTotalReservation());
                psforReportAndReserve.setDouble(2, monetaryBalance.getAvailBalance());
                psforReportAndReserve.setString(3, monetaryBalance.getSubscriberId());
                psforReportAndReserve.setString(4, monetaryBalance.getId());

                long queryExecutionTime = System.currentTimeMillis();
                psforReportAndReserve.executeUpdate();
                queryExecutionTime = System.currentTimeMillis() - queryExecutionTime;
                if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

                    alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
                            "DB Query execution time is high while debiting monetary balance information. "
                                    + "Last Query execution time: " + queryExecutionTime + " ms");

                    if (getLogger().isWarnLogLevel()) {//NOSONAR
                        getLogger().warn(MODULE, "OPEN-DB update query execution time getting high, Last Query execution time = "
                                + queryExecutionTime + " milliseconds.");
                    }
                }
                psforReportAndReserve.clearParameters();
            }
            iTotalQueryTimeoutCount.set(0);
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Report and reserve balance operation successfully completed.");
            }
        } catch (SQLException e) {
            csvfailOverOperation.doFailover(new MonetaryOperationData(lstmonetaryBalance, subscriberId, ABMFOperationType.REPORT_AND_RESERVE));
            transaction.rollback();
            handleOperationalError(e, transaction);
        } catch (TransactionException tE) {
            csvfailOverOperation.doFailover(new MonetaryOperationData(lstmonetaryBalance, subscriberId, ABMFOperationType.REPORT_AND_RESERVE));
            transaction.rollback();
            handleOperationalError(tE, transaction);
        } catch (Exception e) {
            csvfailOverOperation.doFailover(new MonetaryOperationData(lstmonetaryBalance, subscriberId, ABMFOperationType.REPORT_AND_RESERVE));
            transaction.rollback();
            handleOperationalError(e);
        } finally {
            closeQuietly(psforReportAndReserve);
            endTransaction(transaction);
        }
    }

    public int deleteMonetaryBalance(String subscriberIdentity, Transaction transaction) throws OperationFailedException, TransactionException {

        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Delete moentary balance operation for subscriber(" + subscriberIdentity + ") started");
        }

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Delete monetary balance query: " + DELETE_MONETARY_BALANCE_QUERY);
        }

        PreparedStatement psDeleteUsageStatement = null;

        try {

            psDeleteUsageStatement = transaction.prepareStatement(DELETE_MONETARY_BALANCE_QUERY);
            psDeleteUsageStatement.setString(1, subscriberIdentity);
            psDeleteUsageStatement.setQueryTimeout(CommonConstants.QUERY_TIMEOUT_DEFAULT);

            Stopwatch watch = new Stopwatch();
            watch.start();
            setMonetaryBalanceToMonetaryBalanceHistory(transaction, subscriberIdentity);
            int deleteCount = psDeleteUsageStatement.executeUpdate();
            watch.stop();
            long queryExecutionTime = watch.elapsedTime(TimeUnit.MILLISECONDS);

            if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

                alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
                        "DB Query execution time is high for datasource: " + transaction.getDataSourceName() +
                                ". Last Query execution time: " + queryExecutionTime + " ms");

                if (getLogger().isWarnLogLevel()) {
                    getLogger().warn(MODULE, "DB Query execution time getting high, Last query execution time = "
                            + queryExecutionTime + " ms.");
                }
            }

            if (getLogger().isInfoLogLevel()) {
                if (deleteCount > 0) {
                    getLogger().info(MODULE, "Total(" + deleteCount + ") monetary balances deleted for subscriber(" + subscriberIdentity + ")");
                } else {
                    getLogger().info(MODULE, "Monetary balance not deleted for subscriber(" + subscriberIdentity
                            + "). Reason. Subscriber or monetary balance not found");
                }

            }
            iTotalQueryTimeoutCount.set(0);
            return deleteCount;
        } catch (SQLException e) {
            handleSQLException("deleting monetary balance", e, transaction);
        } finally {
            closeQuietly(psDeleteUsageStatement);
        }

        return 0;
    }

    private void setMonetaryBalanceToMonetaryBalanceHistory(Transaction transaction, String subscriberId) throws TransactionException, SQLException {

        PreparedStatement psForUsageHistory = null;
        try{
            psForUsageHistory = transaction.prepareStatement(MONETARY_BALANCE_HISTORY_QUERY);
            psForUsageHistory.setString(1, generateId());
            psForUsageHistory.setString(2, subscriberId);
            long queryExecutionTime = getCurrentTime();
            psForUsageHistory.executeUpdate();
            queryExecutionTime = getCurrentTime() - queryExecutionTime;
            if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

                alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
                        "DB Query execution time is high while adding monetary balance information in history table. "
                                + "Last Query execution time: " + queryExecutionTime + " ms");

                if (getLogger().isWarnLogLevel()) {
                    getLogger().warn(MODULE, "OPEN-DB update query execution time getting high, Last Query execution time = "
                            + queryExecutionTime + " milliseconds.");
                }
            }

        }finally{
            closeQuietly(psForUsageHistory);
        }
    }

    private void handleSQLException(String operationName, SQLException e, Transaction transaction)
            throws OperationFailedException {
        ResultCode resultCode = ResultCode.INTERNAL_ERROR;
        if (e.getErrorCode() == QUERY_TIMEOUT_ERROR_CODE) {
            if (iTotalQueryTimeoutCount.incrementAndGet() > maxQueryTimeoutCount) {
                if (getLogger().isWarnLogLevel()) {
                    getLogger()
                            .warn(MODULE, "total number of query timeouts exceeded then configured max number of query timeouts,so SPR marked as DEAD");
                }
                transaction.markDead();
            }
            alertListener.generateSystemAlert("", Alerts.QUERY_TIME_OUT, MODULE,
                    "DB Query Timeout while " + operationName + " information in table name: "
                            + TABLE_NAME, 0, "table name: " + TABLE_NAME);
            resultCode = ResultCode.SERVICE_UNAVAILABLE;
        } else if (transaction.isDBDownSQLException(e)) {
            transaction.markDead();
            if (getLogger().isWarnLogLevel()) {
                getLogger().warn(MODULE, "Database with data source name: " + transaction.getDataSourceName()
                        + " is Down, SPR marked as dead. Reason: " + e.getMessage());
            }

            resultCode = ResultCode.SERVICE_UNAVAILABLE;
        }

        throw new OperationFailedException(SPRUtil.toStringSQLException(operationName, e), resultCode, e);
    }

    private void handleTransactionException(String operationName, TransactionException e, String subscriberIdentity, Transaction transaction) throws OperationFailedException {
        ResultCode resultCode = ResultCode.INTERNAL_ERROR;

        if (e.getErrorCode() == TransactionErrorCode.CONNECTION_NOT_FOUND) {

            alertListener.generateSystemAlert("", Alerts.DATABASE_CONNECTION_NOT_AVAILABLE, MODULE,
                    "Unable to " + operationName + " information in " + transaction.getDataSourceName() +
                            " database. Reason: connection not available");
            resultCode = ResultCode.SERVICE_UNAVAILABLE;
        }

        throw new OperationFailedException("Error while " + operationName + "operation for subscriber ID: " + subscriberIdentity
                + " . Reason: " + e.getMessage(), resultCode, e);
    }

    private void handleOperationalError(SQLException e, Transaction transaction)
            throws OperationFailedException {

        if (e.getErrorCode() == QUERY_TIMEOUT_ERROR_CODE) {
            if (iTotalQueryTimeoutCount.incrementAndGet() > maxQueryTimeoutCount) {
                if (getLogger().isWarnLogLevel()) {
                    getLogger()
                            .warn(MODULE, "total number of query timeouts exceeded then configured max number of query timeouts,so SPR marked as DEAD");
                }
                transaction.markDead();
                iTotalQueryTimeoutCount.set(0);
            }
            alertListener.generateSystemAlert("", Alerts.QUERY_TIME_OUT, MODULE,
                    "DB Query Timeout while inserting monetary balance into table name: "
                            + TABLE_NAME, 0, "fetching data from table name: " + TABLE_NAME);
        } else if (transaction.isDBDownSQLException(e)) {
            transaction.markDead();
            if (getLogger().isWarnLogLevel()) {
                getLogger().warn(MODULE, "Database with data source name: " + transaction.getDataSourceName()
                        + " is Down, SPR marked as dead. Reason: " + e.getMessage());
            }
        }

        handleOperationalError(e);
    }


    private void handleOperationalError(TransactionException e, Transaction transaction) throws OperationFailedException {
        if (e.getErrorCode() == TransactionErrorCode.CONNECTION_NOT_FOUND) {
            alertListener.generateSystemAlert("", Alerts.DATABASE_CONNECTION_NOT_AVAILABLE, MODULE,"Unable to reserve balance into " + transaction.getDataSourceName() + " database. Reason: connection not available");

        }
        handleOperationalError(e);
    }

    private void handleOperationalError(Exception e)
            throws OperationFailedException {
        if (getLogger().isErrorLogLevel()) {
            getLogger().error(MODULE, "Error while subscriber monetary ABMF operation, Reason: " + e.getMessage());
        }
        getLogger().trace(MODULE, e);
        throw new OperationFailedException("Error while subscriber monetary ABMF operation, Reason. " + e.getMessage(), e);
    }

    private void endTransaction(Transaction transaction) {
        try {
            if (transaction != null) {
                transaction.end();
            }
        } catch (TransactionException e) {
            getLogger().error(MODULE, "Error in ending transaction while monetary balance operation. Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);
        }
    }

    protected long getCurrentTime() {
        return System.currentTimeMillis();
    }



}
