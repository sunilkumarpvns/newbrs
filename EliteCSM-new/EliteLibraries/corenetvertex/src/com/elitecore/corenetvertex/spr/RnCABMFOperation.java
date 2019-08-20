package com.elitecore.corenetvertex.spr;

import com.elitecore.commons.base.Stopwatch;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.base.TimeSource;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.RateCardType;
import com.elitecore.corenetvertex.constants.RenewalIntervalUnit;
import com.elitecore.corenetvertex.core.alerts.AlertConstants;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.alerts.Alerts;
import com.elitecore.corenetvertex.core.transaction.Transaction;
import com.elitecore.corenetvertex.core.transaction.TransactionException;
import com.elitecore.corenetvertex.core.transaction.TransactionFactory;
import com.elitecore.corenetvertex.core.transaction.exception.TransactionErrorCode;
import com.elitecore.corenetvertex.data.ResetBalanceStatus;
import com.elitecore.corenetvertex.pkg.ChargingType;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.rnc.pkg.RnCPackage;
import com.elitecore.corenetvertex.pm.rnc.ratecard.NonMonetaryRateCard;
import com.elitecore.corenetvertex.pm.rnc.ratecard.RateCard;
import com.elitecore.corenetvertex.pm.rnc.rcgroup.RateCardGroup;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.spr.util.RenewalIntervalUtility;
import com.elitecore.corenetvertex.spr.util.ResetTimeUtility;

import javax.annotation.Nonnull;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.elitecore.commons.base.DBUtility.closeQuietly;
import static com.elitecore.commons.logging.LogManager.getLogger;

public class RnCABMFOperation implements SingleRecordOperation<RnCABMFBatchOperation.BatchOperationData>{

    private static final String MODULE = "RNC-ABMF-OPR";
    private static final int ZERO_VALUE = 0;
    protected static final int QUERY_TIMEOUT_ERROR_CODE = 1013;

    protected AlertListener alertListener;
    private int queryTimeout;
    private int maxQueryTimeoutCount;
    private AtomicInteger iTotalQueryTimeoutCount;
    private TimeSource timeSource;

    private static final String EXPIRE_NON_MONETARY_BALANCE = "Expire non monetary balance";
    private static final String RESET_NON_MONETARY_BALANCE = "Reset non monetary balance";
    private static final String REFUND_NON_MONETARY_BALANCE = "Refund non monetary balance";

    private static final String TABLE_NAME = "TBLM_RNC_BALANCE";

    private static final String SELECT_BALANCE_QUERY = "SELECT * FROM " + TABLE_NAME + " WHERE SUBSCRIBER_ID = ? ";

    protected static final String RESERVE_RNC_BALANCE_QUERY = "UPDATE TBLM_RNC_BALANCE SET"
            + " RESERVATION_TIME = RESERVATION_TIME + ? ,"
            + " LAST_UPDATE_TIME = CURRENT_TIMESTAMP "
            + " WHERE ID=?";


    protected static final String REPORT_RNC_BALANCE_QUERY = "UPDATE TBLM_RNC_BALANCE SET"
            + " DAILY_LIMIT = DAILY_LIMIT + ?,"
            + " WEEKLY_LIMIT = WEEKLY_LIMIT + ?,"
            + " BILLING_CYCLE_AVAIL = BILLING_CYCLE_AVAIL - ?,"
            + " DAILY_RESET_TIME = ?,"
            + " WEEKLY_RESET_TIME = ?,"
            + " LAST_UPDATE_TIME = CURRENT_TIMESTAMP "
            + " WHERE ID=?";

    protected static final String REFUND_RNC_BALANCE_QUERY = "UPDATE TBLM_RNC_BALANCE SET"
            + " DAILY_LIMIT = DAILY_LIMIT - ?,"
            + " WEEKLY_LIMIT = WEEKLY_LIMIT - ?,"
            + " BILLING_CYCLE_AVAIL = BILLING_CYCLE_AVAIL + ?,"
            + " DAILY_RESET_TIME = ?,"
            + " WEEKLY_RESET_TIME = ?,"
            + " LAST_UPDATE_TIME = CURRENT_TIMESTAMP "
            + " WHERE ID=?";


    protected static final String REPORT_AND_RESERVE_RNC_BALANCE_QUERY = "UPDATE TBLM_RNC_BALANCE SET"
            + " DAILY_LIMIT = DAILY_LIMIT + ?,"
            + " WEEKLY_LIMIT = WEEKLY_LIMIT + ?,"
            + " BILLING_CYCLE_AVAIL = BILLING_CYCLE_AVAIL - ?,"
            + " RESERVATION_TIME = RESERVATION_TIME + ? ,"
            //+ " DAILY_RESET_TIME = ?,"
            //+ " WEEKLY_RESET_TIME = ?,"
            + " LAST_UPDATE_TIME = CURRENT_TIMESTAMP "
            + " WHERE ID=?";

    private static final String INSERT_BALANCE_QUERY = "INSERT INTO TBLM_RNC_BALANCE (" +
            "ID," +
            "SUBSCRIBER_ID," +
            "PACKAGE_ID," +
            "SUBSCRIPTION_ID," +
            "RATECARD_ID," +
            "BALANCE_EXPIRY_TIME," +
            "BILLING_CYCLE_TOTAL," +
            "BILLING_CYCLE_AVAIL," +
            "DAILY_RESET_TIME," +
            "DAILY_LIMIT," +
            "WEEKLY_RESET_TIME," +
            "WEEKLY_LIMIT," +
            "RESERVATION_TIME," +
            "START_TIME," +
            "STATUS," +
            "RENEWAL_INTERVAL," +
            "LAST_UPDATE_TIME," +
            "PRODUCT_OFFER_ID," +
            "CHARGING_TYPE) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,CURRENT_TIMESTAMP,?,?)";

    private static final String DELETE_RNC_BALANCE_QUERY = "DELETE FROM TBLM_RNC_BALANCE WHERE SUBSCRIBER_ID = ?";

    private static final String RNC_BALANCE_HISTORY_QUERY = "INSERT INTO " +
            "TBLM_RNC_BALANCE_HISTORY " +
            "(CREATE_DATE, ID," +
            " SUBSCRIBER_ID," +
            " PACKAGE_ID," +
            " SUBSCRIPTION_ID," +
            " PRODUCT_OFFER_ID," +
            " CHARGING_TYPE," +
            " RATECARD_ID," +
            " BALANCE_EXPIRY_TIME," +
            " BILLING_CYCLE_TOTAL," +
            " BILLING_CYCLE_AVAIL," +
            " DAILY_RESET_TIME," +
            " DAILY_LIMIT," +
            " WEEKLY_RESET_TIME," +
            " WEEKLY_LIMIT," +
            " RESERVATION_TIME," +
            " LAST_UPDATE_TIME," +
            " START_TIME," +
            " STATUS," +
            " RENEWAL_INTERVAL)" +
            "SELECT " +
            "CURRENT_TIMESTAMP, " +
            "?," +
            " SUBSCRIBER_ID," +
            " PACKAGE_ID," +
            " SUBSCRIPTION_ID," +
            " PRODUCT_OFFER_ID," +
            " CHARGING_TYPE," +
            " RATECARD_ID," +
            " BALANCE_EXPIRY_TIME," +
            " BILLING_CYCLE_TOTAL," +
            " BILLING_CYCLE_AVAIL," +
            " DAILY_RESET_TIME," +
            " DAILY_LIMIT," +
            " WEEKLY_RESET_TIME," +
            " WEEKLY_LIMIT," +
            " RESERVATION_TIME," +
            " LAST_UPDATE_TIME," +
            " START_TIME," +
            " STATUS," +
            " RENEWAL_INTERVAL" +
            " FROM TBLM_RNC_BALANCE" +
            " WHERE SUBSCRIBER_ID = ?";

    protected static final String RESET_RNC_NON_MONETARY_BALANCE =  "UPDATE TBLM_RNC_BALANCE"
            + " SET"
            + " DAILY_LIMIT=?,"
            + " WEEKLY_LIMIT = ?,"
            + " BILLING_CYCLE_TOTAL = ?,"
            + " BILLING_CYCLE_AVAIL = ?,"
            + " DAILY_RESET_TIME = ?,"
            + " WEEKLY_RESET_TIME = ?,"
            + " BALANCE_EXPIRY_TIME = ?,"
            + " LAST_UPDATE_TIME = CURRENT_TIMESTAMP "
            + " WHERE SUBSCRIBER_ID=? AND PRODUCT_OFFER_ID=? AND RATECARD_ID=?";

    protected static final String EXPIRE_RNC_NON_MONETARY_BALANCE_QUERY =  "UPDATE TBLM_RNC_BALANCE"
            + " SET"
            + " BALANCE_EXPIRY_TIME = ?,"
            + " LAST_UPDATE_TIME = CURRENT_TIMESTAMP "
            + " WHERE SUBSCRIBER_ID=? AND PRODUCT_OFFER_ID=?";


    public RnCABMFOperation(AlertListener alertListener, PolicyRepository policyRepository, int queryTimeout){
        this(alertListener, policyRepository, queryTimeout, 200);
    }

    public RnCABMFOperation(AlertListener alertListener, PolicyRepository policyRepository, int queryTimeout, int maxQueryTimeoutCount){
        this(alertListener,policyRepository,queryTimeout,maxQueryTimeoutCount,TimeSource.systemTimeSource());
    }

    public RnCABMFOperation(AlertListener alertListener, PolicyRepository policyRepository, int queryTimeout, int maxQueryTimeoutCount, TimeSource timeSource){
        this.alertListener = alertListener;
        this.queryTimeout = queryTimeout;
        this.maxQueryTimeoutCount = maxQueryTimeoutCount;
        this.iTotalQueryTimeoutCount = new AtomicInteger(0);
        this.timeSource = timeSource;
    }

    public void init() {
        ///no need to initialize. this method is required by RnCABMFBatchOperation
    }

    public void resetNonMonetaryBalance(@Nonnull SPRInfo sprInfo, String productOfferId, RnCPackage rncPackage, @Nonnull TransactionFactory transactionFactory) throws OperationFailedException {
        String subscriberIdentity = sprInfo.getSubscriberIdentity();

        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Performing operation to reset RnC non monetary balance for subscriber ID: " + subscriberIdentity);
        }

        if (transactionFactory.isAlive() == false) {
            throw new OperationFailedException("Unable to perform reset RnC non monetary balance operation for subscriber ID: " + subscriberIdentity
                    + ". Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        Transaction transaction = transactionFactory.createTransaction();

        if (transaction == null) {
            throw new OperationFailedException("Unable to perform reset RnC non monetary balance operation for subscriber ID: " + subscriberIdentity
                    + ". Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        try {

            transaction.begin();

            resetNonMonetaryBalance(sprInfo, productOfferId, rncPackage, transaction);

        } catch (SQLException e) {
            transaction.rollback();
            handleSQLError("reset balance", e, transaction);
        } catch (TransactionException e) {
            transaction.rollback();
            handleTransactionError("reset balance", e, transaction);
        } catch (Exception e) {
            transaction.rollback();
            handleBalanceError("reset balance", e);
        } finally {
            endTransaction(transaction);
        }
    }

    private void reset(PreparedStatement psForReset, String productOfferId, String subscriberIdentity, NonMonetaryRateCard nonMonetaryRateCard, ChargingType chargingType, Integer billDate) throws SQLException, OperationFailedException {

        Timestamp dailyResetTime = new Timestamp(ResetTimeUtility.calculateDailyResetTime());
        Timestamp weeklyResetTime = new Timestamp(ResetTimeUtility.calculateWeeklyResetTime());
        Timestamp startTime = new Timestamp(timeSource.currentTimeInMillis());
        Timestamp resetTime = new Timestamp(ResetTimeUtility.calculateQuotaResetTime());

        if(nonMonetaryRateCard.getRenewalInterval() != 0) {
            resetTime = new Timestamp(nonMonetaryRateCard.getRenewalIntervalUnit().addTime(startTime.getTime(), nonMonetaryRateCard.getRenewalInterval()));
        }

        Timestamp billingCycleExpiryTime = RenewalIntervalUtility.calculateExpiryTime(nonMonetaryRateCard.getRenewalInterval(),nonMonetaryRateCard.getRenewalIntervalUnit(), resetTime, startTime, billDate);

        long balance;
        if (ChargingType.EVENT == chargingType) {
            balance = nonMonetaryRateCard.getEvent();
        } else {
            balance = nonMonetaryRateCard.getTimeMinorUnit();
        }

        if(nonMonetaryRateCard.getProration()){
            balance = RenewalIntervalUtility.getProratedQuota(balance, startTime, billingCycleExpiryTime, nonMonetaryRateCard.getRenewalIntervalUnit(), nonMonetaryRateCard.getRenewalInterval());
        }

        psForReset.setLong(1, ZERO_VALUE);
        psForReset.setLong(2, ZERO_VALUE);

        psForReset.setLong(3, balance);
        psForReset.setLong(4, balance);

        psForReset.setTimestamp(5, dailyResetTime);
        psForReset.setTimestamp(6, weeklyResetTime);
        psForReset.setTimestamp(7, billingCycleExpiryTime);

        psForReset.setString(8, subscriberIdentity);
        psForReset.setString(9, productOfferId);
        psForReset.setString(10, nonMonetaryRateCard.getId());

        long queryExecutionTime = getCurrentTime();
        psForReset.executeUpdate();
        queryExecutionTime = getCurrentTime() - queryExecutionTime;
        checkForHighResponseTime(queryExecutionTime, RESET_NON_MONETARY_BALANCE);

        psForReset.clearParameters();

    }

    public void resetNonMonetaryBalance(@Nonnull SPRInfo sprInfo, String productOfferId, RnCPackage rncPackage, Transaction transaction) throws OperationFailedException, TransactionException, SQLException {
        PreparedStatement psForReset = null;
        String subscriberIdentity = sprInfo.getSubscriberIdentity();
        try {
            psForReset = transaction.prepareStatement(RESET_RNC_NON_MONETARY_BALANCE);

            List<RateCardGroup> rateCardGroups = rncPackage.getRateCardGroups();

            for(RateCardGroup rateCardGroup: rateCardGroups){

                RateCard peakRateCard = rateCardGroup.getPeakRateCard();
                if ( Objects.isNull(peakRateCard) == false && peakRateCard.getType().equals(RateCardType.NON_MONETARY)) {
                    reset(psForReset, productOfferId, subscriberIdentity, (NonMonetaryRateCard) peakRateCard, rncPackage.getChargingType(), sprInfo.getBillingDate());
                }

                RateCard offPeakRateCard = rateCardGroup.getOffPeakRateCard();
                if ( Objects.isNull(offPeakRateCard) == false && offPeakRateCard.getType().equals(RateCardType.NON_MONETARY)) {
                    reset(psForReset, productOfferId, subscriberIdentity, (NonMonetaryRateCard) offPeakRateCard, rncPackage.getChargingType(), sprInfo.getBillingDate());
                }
            }
        } finally {
            closeQuietly(psForReset);
        }
    }

    public void expireNonMonetaryBalance(String subscriberIdentity, String productOfferId, @Nonnull TransactionFactory transactionFactory) throws OperationFailedException {

        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Started operation to expire balance for subscriber ID: "
                    + subscriberIdentity + " with product offer Id: " + productOfferId);
        }

        Transaction transaction = transactionFactory.createTransaction();
        if (transaction == null) {
            throw new OperationFailedException("Unable to expire balance for subscriber ID: "
                    + subscriberIdentity + " with product offer Id: " + productOfferId
                    + ". Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        PreparedStatement psForUpdate = null;
        try {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Executing Query: " + EXPIRE_RNC_NON_MONETARY_BALANCE_QUERY);
            }
            transaction.begin();

            psForUpdate = transaction.prepareStatement(EXPIRE_RNC_NON_MONETARY_BALANCE_QUERY);
            Timestamp balanceExpiryTime = new Timestamp(getCurrentTime());
            psForUpdate.setTimestamp(1, balanceExpiryTime);
            psForUpdate.setString(2, subscriberIdentity);
            psForUpdate.setString(3, productOfferId);
            long queryExecutionTime = getCurrentTime();
            psForUpdate.executeUpdate();
            queryExecutionTime = getCurrentTime() - queryExecutionTime;

            checkForHighResponseTime(queryExecutionTime, EXPIRE_NON_MONETARY_BALANCE);

            iTotalQueryTimeoutCount.set(0);
            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Expire balance operation successfully completed for subscriber ID: "
                        + subscriberIdentity + " with product offer Id: " + productOfferId);
            }

        } catch (SQLException e) {
            transaction.rollback();
            handleSQLError("expire balance", e, transaction);
        } catch (TransactionException e) {
            transaction.rollback();
            handleTransactionError("expire balance", e, transaction);
        } finally {
            closeQuietly(psForUpdate);
            endTransaction(transaction);
        }
    }

    private void checkForHighResponseTime(long queryExecutionTime, String operation) {
        if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {
            alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
                    "DB Query execution time is high while " + operation + ". "
                            + "Last Query execution time: " + queryExecutionTime + " ms");

            if (getLogger().isLogLevel(LogLevel.WARN)) {
                getLogger().warn(MODULE, "DB Query execution time is high while " + operation + ". "
                        + "Last Query execution time: " + queryExecutionTime + " ms");
            }
        }
    }

    public SubscriberRnCNonMonetaryBalance getNonMonetaryBalance(@Nonnull String subscriberId, String reqPackageId, String reqSubscriptionId, @Nonnull TransactionFactory transactionFactory) throws OperationFailedException {

        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Fetching RnC balance information for subscriber ID: " + subscriberId);
        }


        if (transactionFactory == null || transactionFactory.isAlive() == false) {
            throw new OperationFailedException("Unable to fetch balance for subscriber ID: " + subscriberId
                    + " Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        Transaction transaction = transactionFactory.createTransaction();

        if (transaction == null) {
            throw new OperationFailedException("Unable to fetch balance for subscriber ID: " + subscriberId
                    + " Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ArrayList<RnCNonMonetaryBalance> nonMonetoryBalances = new ArrayList<>(4);

        try {
            transaction.begin();
            preparedStatement = transaction.prepareStatement(SELECT_BALANCE_QUERY);
            preparedStatement.setString(1, subscriberId);
            preparedStatement.setQueryTimeout(queryTimeout);

            long currentTime = getCurrentTime();
            resultSet = preparedStatement.executeQuery();
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
                String packageId = resultSet.getString("PACKAGE_ID");
                String subscriptionId =resultSet.getString("SUBSCRIPTION_ID");
                String ratecardId =  resultSet.getString("RATECARD_ID");
                Timestamp billingCycleResetTime = resultSet.getTimestamp("BALANCE_EXPIRY_TIME");
                long billingCycleTotalTime = resultSet.getLong("BILLING_CYCLE_TOTAL");
                long billingCycleAvailableTime = resultSet.getLong("BILLING_CYCLE_AVAIL");
                Timestamp dailyResetTime = resultSet.getTimestamp("DAILY_RESET_TIME");
                long dailyTime = resultSet.getLong("DAILY_LIMIT");
                Timestamp weeklyResetTime = resultSet.getTimestamp("WEEKLY_RESET_TIME");
                long weeklyTime = resultSet.getLong("WEEKLY_LIMIT");
                long reservationTime = resultSet.getLong("RESERVATION_TIME");
                String status = resultSet.getString("STATUS");
                String renewalInterval = resultSet.getString("RENEWAL_INTERVAL");
                String productOfferId = resultSet.getString("PRODUCT_OFFER_ID");
                String chargingType = resultSet.getString("CHARGING_TYPE");

                RnCNonMonetaryBalance balance = new RnCNonMonetaryBalance(
                        id, packageId, ratecardId, productOfferId, subscriberId,
                        subscriptionId, billingCycleTotalTime, billingCycleAvailableTime, dailyTime,
                        weeklyTime, reservationTime,
                        dailyResetTime.getTime(), weeklyResetTime.getTime(), billingCycleResetTime.getTime(), ResetBalanceStatus.fromValue(status), renewalInterval, ChargingType.fromName(chargingType));
                getLogger().debug(MODULE, "Non Monetary Balance : " + balance);
                if(nonMonetoryBalances == null){
                    nonMonetoryBalances = new ArrayList<>();
                }


                if(Strings.isNullOrEmpty(reqPackageId) == false){
                    if(reqPackageId.equals(packageId) == false){
                        continue;
                    }
                }

                if(Strings.isNullOrEmpty(reqSubscriptionId) == false){
                    if(reqSubscriptionId.equals(subscriptionId) == false){
                        continue;
                    }
                }

                nonMonetoryBalances.add(balance);
            }

            if (nonMonetoryBalances.isEmpty() && getLogger().isInfoLogLevel()) {
                getLogger().debug(MODULE, "Balance not exist with subscriber ID: " + subscriberId);
            }

            return new SubscriberRnCNonMonetaryBalance(nonMonetoryBalances);
        } catch (SQLException e) {
            handleGetBalanceError(e, transaction);
        } catch (TransactionException e) {
            handleGetBalanceError(e, transaction);
        } catch (Exception e) {
            handleGetBalanceError(e);
        } finally {
            closeQuietly(resultSet);
            closeQuietly(preparedStatement);
            endTransaction(transaction);
        }
        return null;
    }

    private void handleGetBalanceError(SQLException e, Transaction transaction)
            throws OperationFailedException {

        if (e.getErrorCode() == QUERY_TIMEOUT_ERROR_CODE) {
            if (iTotalQueryTimeoutCount.incrementAndGet() > maxQueryTimeoutCount) {
                if (getLogger().isWarnLogLevel()) {
                    getLogger()
                            .warn(MODULE, "total number of query timeouts exceeded then configured max number of query timeouts,so SPR marked as DEAD");
                }
                markTransactionDead(transaction);
            }
            alertListener.generateSystemAlert("", Alerts.QUERY_TIME_OUT, MODULE,
                    "DB Query Timeout while fetching RnC balance information from table name: "
                            + TABLE_NAME, 0, "fetching data from table name: " + TABLE_NAME);
        } else if (transaction.isDBDownSQLException(e)) {
            markTransactionDead(transaction);
            if (getLogger().isWarnLogLevel()) {
                getLogger().warn(MODULE, "Database with data source name: " + transaction.getDataSourceName()
                        + " is Down, SPR marked as dead. Reason: " + e.getMessage());
            }
        }

        handleGetBalanceError(e);
    }

    private void handleDeleteBalanceError(SQLException e, Transaction transaction)
            throws OperationFailedException {

        if (e.getErrorCode() == QUERY_TIMEOUT_ERROR_CODE) {
            if (iTotalQueryTimeoutCount.incrementAndGet() > maxQueryTimeoutCount) {
                if (getLogger().isWarnLogLevel()) {
                    getLogger()
                            .warn(MODULE, "total number of query timeouts exceeded then configured max number of query timeouts,so SPR marked as DEAD");
                }
                markTransactionDead(transaction);
            }
            alertListener.generateSystemAlert("", Alerts.QUERY_TIME_OUT, MODULE,
                    "DB Query Timeout while deleting RnC balance information from table name: "
                            + TABLE_NAME, 0, "deleting data from table name: " + TABLE_NAME);
        } else if (transaction.isDBDownSQLException(e)) {
            markTransactionDead(transaction);
            if (getLogger().isWarnLogLevel()) {
                getLogger().warn(MODULE, "Database with data source name: " + transaction.getDataSourceName()
                        + " is Down, SPR marked as dead. Reason: " + e.getMessage());
            }
        }

        handleDeleteBalanceError(e);
    }

    private void handleGetBalanceError(TransactionException e, Transaction transaction)
            throws OperationFailedException {
        if (e.getErrorCode() == TransactionErrorCode.CONNECTION_NOT_FOUND) {

            alertListener.generateSystemAlert("", Alerts.DATABASE_CONNECTION_NOT_AVAILABLE, MODULE,
                    "Unable to get RnC balance information from " + transaction.getDataSourceName() +
                            " database. Reason: connection not available");

        }

        handleGetBalanceError(e);
    }

    private void handleGetBalanceError(Exception e)
            throws OperationFailedException {
        if (getLogger().isErrorLogLevel()) {
            getLogger().error(MODULE, "Error while getting RnC balance info, Reason: " + e.getMessage());
        }
        getLogger().trace(MODULE, e);
        throw new OperationFailedException("Error while getting RnC balance info. Reason. " + e.getMessage(), e);
    }

    private void handleDeleteBalanceError(Exception e)
            throws OperationFailedException {
        if (getLogger().isErrorLogLevel()) {
            getLogger().error(MODULE, "Error while deleting RnC balance info, Reason: " + e.getMessage());
        }
        getLogger().trace(MODULE, e);
        throw new OperationFailedException("Error while deleting RnC balance info. Reason. " + e.getMessage(), e);
    }


    @Nonnull
    public void reserveBalance(String subscriberIdentity, List<RnCNonMonetaryBalance> rncNonMonetaryBalances, TransactionFactory transactionFactory) throws OperationFailedException {

        PreparedStatement psforReserve = null;

        if (transactionFactory.isAlive() == false) {
            throw new OperationFailedException("Unable to perform reserve rnc balance operation for subscriber ID: " + subscriberIdentity + ". Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        Transaction transaction = transactionFactory.createTransaction();

        if (transaction == null) {
            throw new OperationFailedException("Unable to perform reserve rnc balance operation for subscriber ID: " + subscriberIdentity + ". Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        try {

            transaction.begin();

            psforReserve = transaction.prepareStatement(RESERVE_RNC_BALANCE_QUERY);

            for (RnCNonMonetaryBalance rncNonMonetaryBalance : rncNonMonetaryBalances) {

                psforReserve.setQueryTimeout(queryTimeout);

                setBalanceToPsForReserve(psforReserve, rncNonMonetaryBalance);

                long queryExecutionTime = getCurrentTime();
                psforReserve.executeUpdate();
                queryExecutionTime = getCurrentTime() - queryExecutionTime;
                if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

                    alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
                            "DB Query execution time is high while reserving balance information. "
                                    + "Last Query execution time: " + queryExecutionTime + " ms");

                    if (getLogger().isWarnLogLevel()) {//NOSONAR
                        getLogger().warn(MODULE, "OPEN-DB update query execution time getting high, Last Query execution time = "
                                + queryExecutionTime + " milliseconds.");
                    }
                }

                psforReserve.clearParameters();

            }
            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Reserve balance operation successfully completed");
            }
            iTotalQueryTimeoutCount.set(0);

        } catch (SQLException e) {
            transaction.rollback();
            handleReservationUsageError(e, transaction);
        }catch (TransactionException e) {
            transaction.rollback();
            handleReservationUsageError(e, transaction);
        } catch (Exception e) {
            transaction.rollback();
            handleReservationUsageError(e);
        } finally {
            closeQuietly(psforReserve);
            endTransaction(transaction);
        }
    }

    protected void setBalanceToPsForReserve(PreparedStatement psForReserve, RnCNonMonetaryBalance rncNonMonetaryBalance) throws SQLException {

    	psForReserve.setLong(1,rncNonMonetaryBalance.getReservationTime());
        psForReserve.setString(2,rncNonMonetaryBalance.getId());
    }

    private void handleReservationUsageError(TransactionException e, Transaction transaction) throws OperationFailedException {

        if (e.getErrorCode() == TransactionErrorCode.CONNECTION_NOT_FOUND) {

            alertListener.generateSystemAlert("", Alerts.DATABASE_CONNECTION_NOT_AVAILABLE, MODULE,
                    "Unable to reserve rnc balance into " + transaction.getDataSourceName() +
                            " database. Reason: connection not available");

        }

        handleReservationUsageError(e);
    }

    private void handleReservationUsageError(SQLException e, Transaction transaction) throws OperationFailedException {

        if (e.getErrorCode() == QUERY_TIMEOUT_ERROR_CODE) {
            if (iTotalQueryTimeoutCount.incrementAndGet() > maxQueryTimeoutCount) {
                if (getLogger().isWarnLogLevel()) {
                    getLogger()
                            .warn(MODULE, "total number of query timeouts exceeded then configured max number of query timeouts,so SPR marked as DEAD");
                }
                iTotalQueryTimeoutCount.set(0);
                transaction.markDead();
            }
            alertListener.generateSystemAlert("", Alerts.QUERY_TIME_OUT,MODULE,
                    "DB Query Timeout while reserving rnc balance into table name: "
                            + TABLE_NAME, 0, "reserving rnc balance into table name: " + TABLE_NAME);
        } else if (transaction.isDBDownSQLException(e)) {
            transaction.markDead();
            if (getLogger().isWarnLogLevel()) {
                getLogger().warn(MODULE, "Database with data source name: " + transaction.getDataSourceName()
                        + " is Down, SPR marked as dead. Reason: " + e.getMessage());
            }
        }

        handleReservationUsageError(e);
    }

    private void handleReservationUsageError(Exception e) throws OperationFailedException {

        if (getLogger().isErrorLogLevel()) {
            getLogger().error(MODULE, "Error while reserving rnc balance. Reason: " + e.getMessage());
        }
        getLogger().trace(MODULE, e);
        throw new OperationFailedException("Error while reserving rnc balance. Reason. " + e.getMessage(), e);
    }

    @Nonnull
    public void reportBalance(String subscriberIdentity, List<RnCNonMonetaryBalance> rncNonMonetaryBalances, TransactionFactory transactionFactory) throws OperationFailedException{

    	PreparedStatement psForReport = null;

        if (transactionFactory.isAlive() == false) {
            throw new OperationFailedException("Unable to perform report rnc balance operation for subscriber ID: " + subscriberIdentity + ". Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        Transaction transaction = transactionFactory.createTransaction();

        if (transaction == null) {
            throw new OperationFailedException("Unable to perform report rnc balance operation for subscriber ID: " + subscriberIdentity + ". Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }
        try {

            transaction.begin();
            psForReport = transaction.prepareStatement(REPORT_RNC_BALANCE_QUERY);

            for (RnCNonMonetaryBalance rnCNonMonetaryBalance : rncNonMonetaryBalances) {

                psForReport.setQueryTimeout(queryTimeout);

                setBalanceToPsForReport(psForReport, rnCNonMonetaryBalance);

                long queryExecutionTime = getCurrentTime();
                psForReport.executeUpdate();
                queryExecutionTime = getCurrentTime() - queryExecutionTime;
                if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

                    alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
                            "DB Query execution time is high while reporting rnc balance information. "
                                    + "Last Query execution time: " + queryExecutionTime + " ms");

                    if (getLogger().isWarnLogLevel()) {//NOSONAR
                        getLogger().warn(MODULE, "OPEN-DB update query execution time getting high, Last Query execution time = "
                                + queryExecutionTime + " milliseconds.");
                    }
                }
                psForReport.clearParameters();
            }
            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Report rnc balance operation successfully completed");
            }
            iTotalQueryTimeoutCount.set(0);
        } catch (SQLException e) {
            transaction.rollback();
            handleReportUsageError(e, transaction);
        }catch (TransactionException e) {
            transaction.rollback();
            handleReportUsageError(e, transaction);
        } catch (Exception e) {
            transaction.rollback();
            handleReportUsageError(e);
        } finally {
            closeQuietly(psForReport);
            endTransaction(transaction);
        }

    }

    protected void setBalanceToPsForReport(PreparedStatement psForReport, RnCNonMonetaryBalance rncNonMonetaryBalance) throws SQLException {

        psForReport.setLong(1,rncNonMonetaryBalance.getDailyLimit());
        psForReport.setLong(2,rncNonMonetaryBalance.getWeeklyLimit());
        psForReport.setLong(3,rncNonMonetaryBalance.getBillingCycleAvailable());
        psForReport.setTimestamp(4, new Timestamp(rncNonMonetaryBalance.getDailyResetTime()));
        psForReport.setTimestamp(5, new Timestamp(rncNonMonetaryBalance.getWeeklyResetTime()));
        psForReport.setString(6,rncNonMonetaryBalance.getId());
    }

    @Nonnull
    public void refundBalance(String subscriberIdentity, List<RnCNonMonetaryBalance> rncNonMonetaryBalances, TransactionFactory transactionFactory) throws OperationFailedException{

        if (transactionFactory.isAlive() == false) {
            throw new OperationFailedException("Unable to perform refund rnc balance operation for subscriber ID: " + subscriberIdentity + ". Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        Transaction transaction = transactionFactory.createTransaction();

        if (transaction == null) {
            throw new OperationFailedException("Unable to perform refund rnc balance operation for subscriber ID: " + subscriberIdentity + ". Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        PreparedStatement psForRefund = null;
        try {

            transaction.begin();
            psForRefund = transaction.prepareStatement(REFUND_RNC_BALANCE_QUERY);

            for (RnCNonMonetaryBalance rnCNonMonetaryBalance : rncNonMonetaryBalances) {

                psForRefund.setQueryTimeout(queryTimeout);

                setBalanceToPsForRefund(psForRefund, rnCNonMonetaryBalance);

                long queryExecutionTime = getCurrentTime();
                psForRefund.executeUpdate();
                queryExecutionTime = getCurrentTime() - queryExecutionTime;
                checkForHighResponseTime(queryExecutionTime, REFUND_NON_MONETARY_BALANCE);
                psForRefund.clearParameters();
            }
            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Refund rnc balance operation successfully completed for subscriber ID: " + subscriberIdentity);
            }
            iTotalQueryTimeoutCount.set(0);
        } catch (SQLException e) {
            transaction.rollback();
            handleSQLError("refund balance", e, transaction);
        } catch (TransactionException e) {
            transaction.rollback();
            handleTransactionError("refund balance", e, transaction);
        } catch (Exception e) {
            transaction.rollback();
            handleBalanceError("refund balance", e);
        } finally {
            closeQuietly(psForRefund);
            endTransaction(transaction);
        }

    }

    protected void setBalanceToPsForRefund(PreparedStatement psForReport, RnCNonMonetaryBalance rncNonMonetaryBalance) throws SQLException {

        psForReport.setLong(1,rncNonMonetaryBalance.getDailyLimit());
        psForReport.setLong(2,rncNonMonetaryBalance.getWeeklyLimit());
        psForReport.setLong(3,rncNonMonetaryBalance.getBillingCycleAvailable());
        psForReport.setTimestamp(4, new Timestamp(rncNonMonetaryBalance.getDailyResetTime()));
        psForReport.setTimestamp(5, new Timestamp(rncNonMonetaryBalance.getWeeklyResetTime()));
        psForReport.setString(6,rncNonMonetaryBalance.getId());
    }

    private void handleReportUsageError(TransactionException e, Transaction transaction) throws OperationFailedException {

        if (e.getErrorCode() == TransactionErrorCode.CONNECTION_NOT_FOUND) {

            alertListener.generateSystemAlert("", Alerts.DATABASE_CONNECTION_NOT_AVAILABLE, MODULE,
                    "Unable to report rnc balance into " + transaction.getDataSourceName() +
                            " database. Reason: connection not available");

        }

        handleReportUsageError(e);
    }

    private void handleReportUsageError(SQLException e, Transaction transaction) throws OperationFailedException {

        if (e.getErrorCode() == QUERY_TIMEOUT_ERROR_CODE) {
            if (iTotalQueryTimeoutCount.incrementAndGet() > maxQueryTimeoutCount) {
                if (getLogger().isWarnLogLevel()) {
                    getLogger()
                            .warn(MODULE, "total number of query timeouts exceeded then configured max number of query timeouts,so SPR marked as DEAD");
                }
                iTotalQueryTimeoutCount.set(0);
                transaction.markDead();
            }
            alertListener.generateSystemAlert("", Alerts.QUERY_TIME_OUT, MODULE,
                    "DB Query Timeout while reporting rnc balance into table name: "
                            + TABLE_NAME, 0, "reporting rnc balance into table name: " + TABLE_NAME);
        } else if (transaction.isDBDownSQLException(e)) {
            transaction.markDead();
            if (getLogger().isWarnLogLevel()) {
                getLogger().warn(MODULE, "Database with data source name: " + transaction.getDataSourceName()
                        + " is Down, SPR marked as dead. Reason: " + e.getMessage());
            }
        }

        handleReportUsageError(e);
    }

    private void handleReportUsageError(Exception e) throws OperationFailedException {

        if (getLogger().isErrorLogLevel()) {
            getLogger().error(MODULE, "Error while reporting balance. Reason: " + e.getMessage());
        }
        getLogger().trace(MODULE, e);
        throw new OperationFailedException("Error while reporting. Reason. " + e.getMessage(), e);
    }


    @Nonnull
    public void reportAndReserveBalance(String subscriberIdentity, List<RnCNonMonetaryBalance> rncNonMonetaryBalances, TransactionFactory transactionFactory) throws OperationFailedException {

    	PreparedStatement psForReportAndReserve = null;

        if (transactionFactory.isAlive() == false) {
            throw new OperationFailedException("Unable to perform report and reserve rnc balance operation for subscriber ID: " + subscriberIdentity + ". Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        Transaction transaction = transactionFactory.createTransaction();

        if (transaction == null) {
            throw new OperationFailedException("Unable to perform report and reserve rnc balance operation for subscriber ID: " + subscriberIdentity + ". Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        try {
            transaction.begin();

            psForReportAndReserve = transaction.prepareStatement(REPORT_AND_RESERVE_RNC_BALANCE_QUERY);

            for (RnCNonMonetaryBalance rncNonMonetaryBalance : rncNonMonetaryBalances) {

                psForReportAndReserve.setQueryTimeout(queryTimeout);
                setBalanceToPsForReportAndReserve(psForReportAndReserve, rncNonMonetaryBalance);

                long queryExecutionTime = getCurrentTime();
                psForReportAndReserve.executeUpdate();
                queryExecutionTime = getCurrentTime() - queryExecutionTime;
                if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

                    alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
                            "DB Query execution time is high while reporting and reserving rnc balance information. "
                                    + "Last Query execution time: " + queryExecutionTime + " ms");

                    if (getLogger().isWarnLogLevel()) {//NOSONAR
                        getLogger().warn(MODULE, "OPEN-DB update query execution time getting high, Last Query execution time = "
                                + queryExecutionTime + " milliseconds.");
                    }
                }
                psForReportAndReserve.clearParameters();
            }
            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Report and reserve rnc balance operation successfully completed");
            }
            iTotalQueryTimeoutCount.set(0);
        } catch (SQLException e) {
            transaction.rollback();
            handleReportAndReserveUsageError(e, transaction);
        }catch (TransactionException e) {
            transaction.rollback();
            handleReportAndReserveUsageError(e, transaction);
        } catch (Exception e) {
            transaction.rollback();
            handleReportAndReserveUsageError(e);
        } finally {
            closeQuietly(psForReportAndReserve);
            endTransaction(transaction);
        }

    }

    protected void setBalanceToPsForReportAndReserve(PreparedStatement psForReportAndReserve, RnCNonMonetaryBalance rncNonMonetaryBalance) throws SQLException {

        psForReportAndReserve.setLong(1,rncNonMonetaryBalance.getDailyLimit());
        psForReportAndReserve.setLong(2,rncNonMonetaryBalance.getWeeklyLimit());
        psForReportAndReserve.setLong(3,rncNonMonetaryBalance.getBillingCycleAvailable());
        psForReportAndReserve.setLong(4,rncNonMonetaryBalance.getReservationTime());
        psForReportAndReserve.setString(5,rncNonMonetaryBalance.getId());
    }

    private void handleReportAndReserveUsageError(TransactionException e, Transaction transaction)
            throws OperationFailedException {
        if (e.getErrorCode() == TransactionErrorCode.CONNECTION_NOT_FOUND) {

            alertListener.generateSystemAlert("", Alerts.DATABASE_CONNECTION_NOT_AVAILABLE, MODULE,
                    "Unable to report and reserve balance into " + transaction.getDataSourceName() +
                            " database. Reason: connection not available");

        }

        handleReportAndReserveUsageError(e);
    }

    private void handleReportAndReserveUsageError(SQLException e, Transaction transaction)
            throws OperationFailedException {

        if (e.getErrorCode() == QUERY_TIMEOUT_ERROR_CODE) {
            if (iTotalQueryTimeoutCount.incrementAndGet() > maxQueryTimeoutCount) {
                if (getLogger().isWarnLogLevel()) {
                    getLogger()
                            .warn(MODULE, "total number of query timeouts exceeded then configured max number of query timeouts,so SPR marked as DEAD");
                }
                iTotalQueryTimeoutCount.set(0);
                transaction.markDead();
            }
            alertListener.generateSystemAlert("", Alerts.QUERY_TIME_OUT, MODULE,
                    "DB Query Timeout while reporting and reservig balance into table name: "
                            + TABLE_NAME, 0, "reporting and reserving balance into table name: " + TABLE_NAME);
        } else if (transaction.isDBDownSQLException(e)) {
            transaction.markDead();
            if (getLogger().isWarnLogLevel()) {
                getLogger().warn(MODULE, "Database with data source name: " + transaction.getDataSourceName()
                        + " is Down, SPR marked as dead. Reason: " + e.getMessage());
            }
        }

        handleReportAndReserveUsageError(e);
    }

    private void handleReportAndReserveUsageError(Exception e)
            throws OperationFailedException {
        if (getLogger().isErrorLogLevel()) {
            getLogger().error(MODULE, "Error while reporting and reserving balance. Reason: " + e.getMessage());
        }
        getLogger().trace(MODULE, e);
        throw new OperationFailedException("Error while reporting and reserving balance. Reason. " + e.getMessage(), e);
    }



    @Override
    public void process(RnCABMFBatchOperation.BatchOperationData batchOperationData, TransactionFactory transactionFactory) throws OperationFailedException {

    	if (batchOperationData.getOperation() == RnCABMFBatchOperation.BatchOperation.REPORT) {

            reportBalance(batchOperationData.getSubscriberIdentity(), Arrays.asList(batchOperationData.getRnCNonMonetaryBalance()), transactionFactory);

        } else  if (batchOperationData.getOperation() == RnCABMFBatchOperation.BatchOperation.RESERVE) {

            reserveBalance(batchOperationData.getSubscriberIdentity(), Arrays.asList(batchOperationData.getRnCNonMonetaryBalance()), transactionFactory);

        } else  if (batchOperationData.getOperation() == RnCABMFBatchOperation.BatchOperation.REPORT_AND_RESERVE) {

            reportAndReserveBalance(batchOperationData.getSubscriberIdentity(), Arrays.asList(batchOperationData.getRnCNonMonetaryBalance()), transactionFactory);

        }
    }

    private void markTransactionDead(Transaction transaction){
        iTotalQueryTimeoutCount.set(0);
        transaction.markDead();
    }

    private void endTransaction(Transaction transaction) {
        try {
            if (transaction != null) {
                transaction.end();
            }
        } catch (TransactionException e) {
            getLogger().error(MODULE, "Error in ending transaction. Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);
        }
    }

    public void stop() {
        ///Not required here. Override by RnCABMFBatchOperation
    }


    protected long getCurrentTime() {
        return timeSource.currentTimeInMillis();
    }

    public SubscriptionRnCNonMonetaryBalance addBalance(String subscriberIdentity, Subscription subscription, RnCPackage rncPackage, Transaction transaction, String productOfferId,Integer billDate) throws OperationFailedException {

        PreparedStatement psforInsert = null;
        try {

            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Adding non-monetary balance for Subscriber Identity : " + subscriberIdentity);
            }

            if(Objects.isNull(rncPackage)) {
                throw new OperationFailedException("RnC Package is null");
            }

            psforInsert = transaction.prepareStatement(INSERT_BALANCE_QUERY);

            List<RateCardGroup> rateCardGroups = rncPackage.getRateCardGroups();

            Set<String> nonMonetaryRateCardList = new HashSet<>();

            SubscriptionRnCNonMonetaryBalance subscriptionRnCNonMonetaryBalance = new SubscriptionRnCNonMonetaryBalance(rncPackage.getId());

            for (RateCardGroup rateCardGroup : rateCardGroups) {

                if ( !Objects.isNull(rateCardGroup.getPeakRateCard()) && rateCardGroup.getPeakRateCard().getType().equals(RateCardType.NON_MONETARY)) {

                    NonMonetaryRateCard card = (NonMonetaryRateCard) rateCardGroup.getPeakRateCard();

                    if (nonMonetaryRateCardList.add(card.getId()) == true) {

                        subscriptionRnCNonMonetaryBalance.addBalance(insert(subscriberIdentity, subscription, psforInsert, rncPackage.getId(), rateCardGroup.getPeakRateCard(), productOfferId, rncPackage.getChargingType(),billDate));
                    }
                }

                if ( !Objects.isNull(rateCardGroup.getOffPeakRateCard()) && rateCardGroup.getOffPeakRateCard().getType().equals(RateCardType.NON_MONETARY)) {

                    NonMonetaryRateCard card = (NonMonetaryRateCard) rateCardGroup.getOffPeakRateCard();

                    if (nonMonetaryRateCardList.add(card.getId()) == true) {

                        subscriptionRnCNonMonetaryBalance.addBalance(insert(subscriberIdentity, subscription, psforInsert, rncPackage.getId(), rateCardGroup.getOffPeakRateCard(), productOfferId, rncPackage.getChargingType(),billDate));
                    }
                }
            }

            if(subscriptionRnCNonMonetaryBalance.getAllRateCardBalance().isEmpty()) {
                return null;
            }

            iTotalQueryTimeoutCount.set(0);
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Insert balance operation successfully completed for Subscriber Identity : " + subscriberIdentity);
            }

            return subscriptionRnCNonMonetaryBalance;
        } catch (SQLException e) {
            handleGetUsageError(e, transaction);
        } catch (Exception e) {
            handleGetUsageError(e);
        } finally {
            closeQuietly(psforInsert);
        }

        throw new AssertionError("Control should not be reach here");
    }

    private RnCNonMonetaryBalance insert(String subscriberIdentity,
                        Subscription subscription,
                        PreparedStatement psforInsert,
                        String packageId,
                        RateCard rateCard,
                        String productOfferId,
                        ChargingType chargingType, Integer billDate) throws Exception {

        NonMonetaryRateCard nonMonetaryRateCard = (NonMonetaryRateCard) rateCard;

        Timestamp dailyResetTime;
        Timestamp weeklyResetTime;
        Timestamp startTime;
        Timestamp resetTime;

        if (Objects.isNull(subscription)) {
            startTime = new Timestamp(timeSource.currentTimeInMillis());
            if(nonMonetaryRateCard.getRenewalInterval() != 0) {
                resetTime = new Timestamp(nonMonetaryRateCard.getRenewalIntervalUnit().addTime(startTime.getTime(), nonMonetaryRateCard.getRenewalInterval()));
            }else{
                resetTime = new Timestamp(ResetTimeUtility.calculateQuotaResetTime());
            }
            dailyResetTime = new Timestamp(ResetTimeUtility.calculateDailyResetTime());
            weeklyResetTime = new Timestamp(ResetTimeUtility.calculateWeeklyResetTime());
        } else {
            startTime= subscription.getStartTime();
            resetTime = subscription.getEndTime();
            dailyResetTime =  new Timestamp(ResetTimeUtility.calculateDailyResetTime(subscription.getStartTime().getTime()));
            weeklyResetTime =  new Timestamp(ResetTimeUtility.calculateWeeklyResetTime(subscription.getStartTime().getTime()));

        }
        Timestamp billingCycleExpiryTime = RenewalIntervalUtility.calculateExpiryTime(nonMonetaryRateCard.getRenewalInterval(),nonMonetaryRateCard.getRenewalIntervalUnit(), resetTime, startTime, billDate);
        String status = nonMonetaryRateCard.getRenewalInterval() == 0 ? ResetBalanceStatus.NOT_RESET.name() : ResetBalanceStatus.RESET.name();

        String id = UUID.randomUUID().toString();
        psforInsert.setString(1, id);
        psforInsert.setString(2, subscriberIdentity);
        psforInsert.setString(3, packageId);
        psforInsert.setString(4,subscription!=null?subscription.getId():null);
        psforInsert.setString(5, nonMonetaryRateCard.getId());
        psforInsert.setTimestamp(6, billingCycleExpiryTime);
        long balance;
        if (ChargingType.EVENT == chargingType) {
			balance = nonMonetaryRateCard.getEvent();
		} else {
			balance = nonMonetaryRateCard.getTimeMinorUnit();
		}

		if(nonMonetaryRateCard.getProration()){
            balance = RenewalIntervalUtility.getProratedQuota(balance, startTime, billingCycleExpiryTime,nonMonetaryRateCard.getRenewalIntervalUnit(), nonMonetaryRateCard.getRenewalInterval());
        }

        psforInsert.setLong(7, balance);
        psforInsert.setLong(8, balance);

        psforInsert.setTimestamp(9, dailyResetTime);
        psforInsert.setLong(10, 0);
        psforInsert.setTimestamp(11, weeklyResetTime);
        psforInsert.setLong(12, 0);
        psforInsert.setLong(13, 0);
        psforInsert.setTimestamp(14, startTime);
        psforInsert.setString(15, status);
        Integer renewalInterval = nonMonetaryRateCard.getRenewalInterval();
        if(RenewalIntervalUnit.TILL_BILL_DATE == nonMonetaryRateCard.getRenewalIntervalUnit()){
            renewalInterval = 1;
        }
        String renewalIntervalVal =renewalInterval + ":" + nonMonetaryRateCard.getRenewalIntervalUnit();
        psforInsert.setString(16, renewalIntervalVal);
        psforInsert.setString(17, productOfferId);
        psforInsert.setString(18, chargingType.name());

        long queryExecutionTime = getCurrentTime();
        psforInsert.executeUpdate();
        queryExecutionTime = getCurrentTime() - queryExecutionTime;
        if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

            alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
                    "DB Query execution time is high while inserting balance information. "
                            + "Last Query execution time: " + queryExecutionTime + " ms");

            if (getLogger().isWarnLogLevel()) {//NOSONAR
                getLogger().warn(MODULE, "OPEN-DB update query execution time getting high, Last Query execution time = "
                        + queryExecutionTime + " milliseconds.");
            }
        }

        psforInsert.clearParameters();

        return new RnCNonMonetaryBalance(id,
                packageId,
                nonMonetaryRateCard.getId(),
                productOfferId,
                subscriberIdentity,
                Objects.nonNull(subscription) ? subscription.getId(): null,
                balance,
                balance,
                0,
                0,
                0,
                dailyResetTime.getTime(),
                weeklyResetTime.getTime(),
                billingCycleExpiryTime.getTime(),
                null,
                nonMonetaryRateCard.getRenewalIntervalUnit().name(),
                chargingType
        );
    }

    public void insert(String subscriberId, String subscriptionId, Collection<RnCNonMonetaryBalance> rncNonMonetaryBalances, Transaction transaction) throws TransactionException, OperationFailedException {

        PreparedStatement psforInsert = null;

        try {

            psforInsert = transaction.prepareStatement(INSERT_BALANCE_QUERY);
            psforInsert.setQueryTimeout(queryTimeout);

            for (RnCNonMonetaryBalance rncNonMonetaryBalance : rncNonMonetaryBalances) {

                setBalanceToPSForInsert(psforInsert, rncNonMonetaryBalance, subscriberId, subscriptionId);

                long queryExecutionTime = getCurrentTime();
                psforInsert.executeUpdate();
                queryExecutionTime = getCurrentTime() - queryExecutionTime;
                if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

                    alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
                            "DB Query execution time is high while inserting balance information. "
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
            handleGetUsageError(e, transaction);
        } catch (Exception e) {
            handleGetUsageError(e);
        } finally {
            closeQuietly(psforInsert);
        }

    }

    protected  void setBalanceToPSForInsert(PreparedStatement psforInsert, RnCNonMonetaryBalance rncNonMonetaryBalance, String subscriberId, String subscriptionId ) throws SQLException {

        psforInsert.setString(1, generateId());
        psforInsert.setString(2, subscriberId);
        psforInsert.setString(3, rncNonMonetaryBalance.getPackageId());
        psforInsert.setString(4, subscriptionId);
        psforInsert.setString(5, rncNonMonetaryBalance.getRatecardId());
        psforInsert.setTimestamp(6, new Timestamp(rncNonMonetaryBalance.getBalanceExpiryTime()));
        psforInsert.setLong(7,rncNonMonetaryBalance.getBillingCycleTotal());
        psforInsert.setLong(8, rncNonMonetaryBalance.getBillingCycleAvailable());
        psforInsert.setTimestamp(9, new Timestamp(rncNonMonetaryBalance.getDailyResetTime()));
        psforInsert.setLong(10,rncNonMonetaryBalance.getDailyLimit());
        psforInsert.setTimestamp(11, new Timestamp(rncNonMonetaryBalance.getWeeklyResetTime()));
        psforInsert.setLong(12, rncNonMonetaryBalance.getWeeklyLimit());
        psforInsert.setLong(13, 0);
        psforInsert.setTimestamp(14, new Timestamp(timeSource.currentTimeInMillis()));
        psforInsert.setString(15, (rncNonMonetaryBalance.getStatus() == null ? null : rncNonMonetaryBalance.getStatus().name()));
        psforInsert.setString(16, rncNonMonetaryBalance.getRenewalInterval());
        psforInsert.setString(17, rncNonMonetaryBalance.getProductOfferId());

    }

    public int deleteRnCBalance(String subscriberIdentity, Transaction transaction) throws OperationFailedException, TransactionException {

        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Delete RnC balance operation for subscriber(" + subscriberIdentity + ") started");
        }

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Delete RnC balance query: " + DELETE_RNC_BALANCE_QUERY);
        }

        PreparedStatement psDeleteUsageStatement = null;

        try {

            psDeleteUsageStatement = transaction.prepareStatement(DELETE_RNC_BALANCE_QUERY);
            psDeleteUsageStatement.setString(1, subscriberIdentity);
            psDeleteUsageStatement.setQueryTimeout(CommonConstants.QUERY_TIMEOUT_DEFAULT);

            Stopwatch watch = new Stopwatch();
            watch.start();
            setRnCBalanceToRnCBalanceHistory(transaction, subscriberIdentity);
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
                    getLogger().info(MODULE, "Total(" + deleteCount + ") RnC balances deleted for subscriber(" + subscriberIdentity + ")");
                } else {
                    getLogger().info(MODULE, "RnC balance not deleted for subscriber(" + subscriberIdentity
                            + "). Reason. Subscriber or RnC balance not found");
                }

            }
            iTotalQueryTimeoutCount.set(0);
            return deleteCount;
        } catch (SQLException e) {
            handleDeleteBalanceError(e, transaction);
        } finally {
            closeQuietly(psDeleteUsageStatement);
        }

        return 0;
    }

    private void setRnCBalanceToRnCBalanceHistory(Transaction transaction, String subscriberId) throws TransactionException, SQLException {

        PreparedStatement psForUsageHistory = null;
        try{
            psForUsageHistory = transaction.prepareStatement(RNC_BALANCE_HISTORY_QUERY);
            psForUsageHistory.setString(1, generateId());
            psForUsageHistory.setString(2, subscriberId);
            long queryExecutionTime = getCurrentTime();
            psForUsageHistory.executeUpdate();
            queryExecutionTime = getCurrentTime() - queryExecutionTime;
            if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

                alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
                        "DB Query execution time is high while adding RnC balance information in history table. "
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

    protected String generateId() {
        return UUID.randomUUID().toString();
    }

    private void handleGetUsageError(SQLException e, Transaction transaction)
            throws OperationFailedException {

        if (e.getErrorCode() == QUERY_TIMEOUT_ERROR_CODE) {
            if (iTotalQueryTimeoutCount.incrementAndGet() > maxQueryTimeoutCount) {
                if (getLogger().isWarnLogLevel()) {
                    getLogger()
                            .warn(MODULE, "total number of query timeouts exceeded then configured max number of query timeouts,so SPR marked as DEAD");
                }
                iTotalQueryTimeoutCount.set(0);
                transaction.markDead();
            }
            alertListener.generateSystemAlert("", Alerts.QUERY_TIME_OUT, MODULE,
                    "DB Query Timeout while inserting balance into table name: "
                            + TABLE_NAME, 0, "fetching data from table name: " + TABLE_NAME);
        } else if (transaction.isDBDownSQLException(e)) {
            transaction.markDead();
            if (getLogger().isWarnLogLevel()) {
                getLogger().warn(MODULE, "Database with data source name: " + transaction.getDataSourceName()
                        + " is Down, SPR marked as dead. Reason: " + e.getMessage());
            }
        }

        handleGetUsageError(e);
    }

    private void handleGetUsageError(Exception e)
            throws OperationFailedException {
        if (getLogger().isErrorLogLevel()) {
            getLogger().error(MODULE, "Error while subscriber ABMF Operation. Reason: " + e.getMessage());
        }
        getLogger().trace(MODULE, e);
        throw new OperationFailedException("Error while subscriber ABMF Operation. Reason. " + e.getMessage(), e);
    }

    private void handleTransactionError(String operationName, TransactionException e, Transaction transaction) throws OperationFailedException {

        if (e.getErrorCode() == TransactionErrorCode.CONNECTION_NOT_FOUND) {

            alertListener.generateSystemAlert("", Alerts.DATABASE_CONNECTION_NOT_AVAILABLE, MODULE,
                    "Unable to "+ operationName +" into " + transaction.getDataSourceName() +
                            " database. Reason: connection not available");

        }

        handleBalanceError(operationName, e);
    }

    private void handleSQLError(String operationName, SQLException e, Transaction transaction)
            throws OperationFailedException {

        if (e.getErrorCode() == QUERY_TIMEOUT_ERROR_CODE) {
            if (iTotalQueryTimeoutCount.incrementAndGet() > maxQueryTimeoutCount) {
                if (getLogger().isWarnLogLevel()) {
                    getLogger()
                            .warn(MODULE, "total number of query timeouts exceeded then configured max number of query timeouts,so SPR marked as DEAD");
                }
                iTotalQueryTimeoutCount.set(0);
                transaction.markDead();
            }
            alertListener.generateSystemAlert("", Alerts.QUERY_TIME_OUT, MODULE,
                    "DB Query Timeout while "+ operationName +" into table name: "
                            + TABLE_NAME, 0, operationName + " into table name: " + TABLE_NAME);
        } else if (transaction.isDBDownSQLException(e)) {
            transaction.markDead();
            if (getLogger().isWarnLogLevel()) {
                getLogger().warn(MODULE, "Database with data source name: " + transaction.getDataSourceName()
                        + " is Down, SPR marked as dead. Reason: " + e.getMessage());
            }
        }

        handleBalanceError(operationName, e);
    }

    private void handleBalanceError(String operationName, Exception e)
            throws OperationFailedException {
        if (getLogger().isErrorLogLevel()) {
            getLogger().error(MODULE, "Error while "+operationName+". Reason: " + e.getMessage());
        }
        getLogger().trace(MODULE, e);
        throw new OperationFailedException("Error while "+operationName+". Reason. " + e.getMessage(), e);
    }

}
