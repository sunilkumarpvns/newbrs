package com.elitecore.corenetvertex.spr.voltdb;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.base.TimeSource;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.commons.threads.EliteThreadFactory;
import com.elitecore.corenetvertex.constants.ActionType;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.MonetaryBalanceType;
import com.elitecore.corenetvertex.core.alerts.AlertConstants;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.alerts.Alerts;
import com.elitecore.corenetvertex.core.db.VoltDBClient;
import com.elitecore.corenetvertex.spr.ABMFOperationType;
import com.elitecore.corenetvertex.spr.CSVFailOverOperation;
import com.elitecore.corenetvertex.spr.EDRListener;
import com.elitecore.corenetvertex.spr.MonetaryABMFOperation;
import com.elitecore.corenetvertex.spr.MonetaryBalance;
import com.elitecore.corenetvertex.spr.RecordProcessor;
import com.elitecore.corenetvertex.spr.SubscriberMonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberMonetaryBalanceWrapper;
import com.elitecore.corenetvertex.spr.data.SPRFields;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.spr.params.MonetaryRechargeData;
import com.elitecore.corenetvertex.spr.voltdb.util.VoltDBUtil;
import org.apache.commons.collections.MapUtils;
import org.voltdb.VoltTable;
import org.voltdb.client.ClientResponse;
import org.voltdb.client.ProcCallException;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class VoltMonetaryABMFOperation implements MonetaryABMFOperation {
    private static final String MODULE = "VOLT-MON-ABMF-OPR";
    public static final String MONETARY_BALANCE_ADD_STORED_PROCEDURE = "MonetaryBalanceAddStoredProcedure";
    private static final String MONETARY_BALANCE_UPDATE_STORED_PROCEDURE = "MonetaryBalanceUpdateStoredProcedure";
    private static final String MONETARY_BALANCE_SELECT_STORED_PROCEDURE = "MonetaryBalanceSelectStoredProcedure";
    private static final String MONETARY_BALANCE_RESERVE_STORED_PROCEDURE = "MonetaryBalanceReserveStoredProcedure";
    private static final String MONETARY_BALANCE_DIRECT_DEBIT_STORED_PROCEDURE = "MonetaryBalanceDirectDebitStoredProcedure";
    private static final String MONETARY_BALANCE_REPORT_AND_RESERVE_STORED_PROCEDURE = "MonetaryBalanceReportAndReserveStoredProcedure";
    private static final String MONETARY_BALANCE_UPDATE_CREDIT_LIMIT_STORED_PROCEDURE = "MonetaryBalanceUpdateCreditLimitStoredProcedure";
    public static final String RECHARGE_MONETARY_BALANCE_STORED_PROCEDURE = "RechargeMonetaryBalanceStoredProcedure";
    public static final String RECHARGE_AND_UPDATE_SUBSCRIBER_VALIDITY_STORED_PROCEDURE = "RechargeMonetaryBalanceAndUpdateSubscriberValidityStoredProcedure";
    public static final String ADD_MONETARY_BALANCE_AND_UPDATE_SUBSCRIBER_VALIDITY_STORED_PROCEDURE = "AddMonetaryBalanceAndUpdateSubscriberValidityStoredProcedure";

    public static final String ADD_MONETARY_BALANCE = "adding monetary balance";
    public static final String UPDATE_MONETARY_BALANCE = "updating monetary balance";
    public static final String RECHARGE_MONETARY_BALANCE = "rechrging monetary balance";
    public static final String SELECT_MONETARY_BALANCE = "selecting monetary balance";
    public static final String RESERVE_MONETARY_BALANCE = "reserving monetary balance";
    public static final String DIRECT_DEBIT_MONETARY_BALANCE = "direct debiting monetary balance";
    public static final String REPORT_AND_RESERVE_MONETARY_BALANCE = "reporting and reserving monetary balance";
    public static final String UPDATE_CREDIT_LIMIT = "updating credit limit";

    private AlertListener alertListener;
    private AtomicInteger iTotalQueryTimeoutCount;
    private TimeSource timeSource;
    private EDRListener balanceEDRListener;

    private CSVFailOverOperation<MonetaryOperationData> csvfailOverOperation;


    public VoltMonetaryABMFOperation(AlertListener alertListener, RecordProcessor<MonetaryOperationData> recordProcessor, TimeSource timeSource, EDRListener balanceEDRListener){
        this.alertListener = alertListener;
        this.iTotalQueryTimeoutCount = new AtomicInteger(0);
        this.timeSource = timeSource;
        this.balanceEDRListener = balanceEDRListener;
        csvfailOverOperation = new CSVFailOverOperation<>(recordProcessor, new ScheduledThreadPoolExecutor(1, new EliteThreadFactory("VOLT-MONETARY-ABMF-FAILOVER-CSV",
                "VOLT-MONETARY-ABMF-FAILOVER-CSV", Thread.NORM_PRIORITY)));
    }

    @Nonnull
    public SubscriberMonetaryBalance getMonetaryBalance(String subscriberId, Predicate<MonetaryBalance> predicate, VoltDBClient voltDBClient)
            throws OperationFailedException {
        SubscriberMonetaryBalance subscriberMonetaryBalance = new SubscriberMonetaryBalance(TimeSource.systemTimeSource());

        List<MonetaryBalance> serviceMonitoryBalances = getMonetaryBalances(subscriberId, predicate, voltDBClient);

        if(serviceMonitoryBalances != null) {
            for(MonetaryBalance monetaryBalance : serviceMonitoryBalances) {
                subscriberMonetaryBalance.addMonitoryBalances(monetaryBalance);
            }
        }

        return subscriberMonetaryBalance;

    }

    private List<MonetaryBalance> getMonetaryBalances(String subscriberId, Predicate<MonetaryBalance> predicate, VoltDBClient voltDBClient)
            throws OperationFailedException {
        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Fetching monitory balance information for subscriber ID: " + subscriberId);
        }


        if (voltDBClient.isAlive() == false) {
            throw new OperationFailedException("Unable to perform get monetary balance operation for subscriber ID: " + subscriberId
                    + ". Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        List<MonetaryBalance> monetaryBalances = null;
        try {
            long currentTime = timeSource.currentTimeInMillis();
            ClientResponse clientResponse = voltDBClient.callProcedure(MONETARY_BALANCE_SELECT_STORED_PROCEDURE, subscriberId);
            long queryExecutionTime = timeSource.currentTimeInMillis() - currentTime;

            checkForHighResponseTime(queryExecutionTime, SELECT_MONETARY_BALANCE);
            VoltTable vt = clientResponse.getResults()[0];

            while (vt.advanceRow()) {
                String id = vt.getString("ID");
                String serviceId = vt.getString("SERVICE_ID");
                double availBalance = vt.getDouble("AVAILABLE_BALANCE");
                double totalBalance = vt.getDouble("INITIAL_BALANCE");
                double totalReservation = vt.getDouble("TOTAL_RESERVATION");
                long creditLimit = vt.getLong("CREDIT_LIMIT");
                long nextBillingCycleCreditLimit = vt.getLong("NEXT_BILL_CYCLE_CREDIT_LIMIT");
                Timestamp validFromDateInTimeStamp = vt.getTimestampAsSqlTimestamp("VALID_FROM_DATE");
                Timestamp validToDateInTimeStamp = vt.getTimestampAsSqlTimestamp("VALID_TO_DATE");
                String currency = vt.getString("CURRENCY");
                String type = vt.getString("TYPE");
                long lastUpdateTime = vt.getTimestampAsLong("LAST_UPDATE_TIME");
                Timestamp creditLimitUpdateTime = vt.getTimestampAsSqlTimestamp("CREDIT_LIMIT_UPDATE_TIME");
                String parameter1 = vt.getString("PARAM1");
                String parameter2 = vt.getString("PARAM2");

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
                        //0,
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
                        creditLimitUpdateTime==null?0:creditLimitUpdateTime.getTime(),
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

        } catch (IOException e) {
            throw new OperationFailedException("Error while get monetary balance for ID: " + subscriberId + ". Reason: " + e.getMessage(), e);
        } catch (ProcCallException e){
            VoltDBUtil.handleProcedureCallException(e, "get monetary balance for ID: " + subscriberId, MODULE, alertListener, iTotalQueryTimeoutCount, voltDBClient);
        }

        if (monetaryBalances!=null){
            updateNextBillCycleCreditLimit(monetaryBalances, voltDBClient);
        }

        return monetaryBalances;
    }

    private void updateNextBillCycleCreditLimit(List<MonetaryBalance> monetaryBalances,  VoltDBClient voltDBClient) throws OperationFailedException{
        for(MonetaryBalance monetaryBalance: monetaryBalances){
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

            this.updateCreditLimit(monetaryBalance.getSubscriberId(), monetaryBalance, voltDBClient);
            MonetaryBalance previousMonetaryBalance = monetaryBalance.copy();
            monetaryBalance.setAvailBalance(0);
            balanceEDRListener.updateMonetaryEDR(new SubscriberMonetaryBalanceWrapper(monetaryBalance, previousMonetaryBalance, null, CommonConstants.UPDATE_CREDIT_LIMIT, ActionType.UPDATE.name(),null), null, CommonConstants.UPDATE_CREDIT_LIMIT);
            return;
        }
    }

    public void addBalance(String subscriberId, MonetaryBalance monetaryBalance, VoltDBClient voltDBClient) throws OperationFailedException {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Add monetary balance for subscriber-identifier : " + subscriberId);
        }

        if (voltDBClient.isAlive() == false) {
            throw new OperationFailedException("Unable to perform add monetary balance operation for subscriber ID: " + subscriberId
                    + ". Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        try {

            Object addMonetaryBalanceArray = VoltDBUtil.createAddMonetaryBalanceArray(monetaryBalance);
            long queryExecutionTime = timeSource.currentTimeInMillis();
            voltDBClient.callProcedure(MONETARY_BALANCE_ADD_STORED_PROCEDURE, subscriberId, addMonetaryBalanceArray, new Timestamp(monetaryBalance.getValidFromDate()),
                    new Timestamp(monetaryBalance.getValidToDate()), new Timestamp(monetaryBalance.getCreditLimitUpdateTime()));
            queryExecutionTime = timeSource.currentTimeInMillis() - queryExecutionTime;

            checkForHighResponseTime(queryExecutionTime, ADD_MONETARY_BALANCE);

            iTotalQueryTimeoutCount.set(0);

            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Add monetary balance operation successfully completed.");
            }

        } catch (IOException e) {
            throw new OperationFailedException("Error while adding monetary balance for ID: " + subscriberId + ". Reason: " + e.getMessage(), e);
        } catch (ProcCallException e){
            VoltDBUtil.handleProcedureCallException(e, "adding monetary balance for ID: " + subscriberId, MODULE, alertListener, iTotalQueryTimeoutCount, voltDBClient);
        }
    }

    public void updateBalance(String subscriberId, MonetaryBalance monetaryBalance, VoltDBClient voltDBClient) throws OperationFailedException{
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Update monetary balance for subscriber-identifier : " + subscriberId);
        }

        if (voltDBClient.isAlive() == false) {
            throw new OperationFailedException("Unable to perform update monetary balance operation for subscriber ID: " + subscriberId
                    + ". Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        try {
            long queryExecutionTime = timeSource.currentTimeInMillis();
            voltDBClient.callProcedure(MONETARY_BALANCE_UPDATE_STORED_PROCEDURE, subscriberId,
                    monetaryBalance.getId(),
                    monetaryBalance.getAvailBalance(),
                    monetaryBalance.getInitialBalance(),
                    new Timestamp(monetaryBalance.getValidToDate()));

            queryExecutionTime = timeSource.currentTimeInMillis() - queryExecutionTime;

            checkForHighResponseTime(queryExecutionTime, UPDATE_MONETARY_BALANCE);

            iTotalQueryTimeoutCount.set(0);
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Update monetary balance operation successfully completed for subscriber-identifier : " + monetaryBalance.getSubscriberId());
            }

        } catch (IOException e) {
            throw new OperationFailedException("Error while updating monetary balance for ID: " + subscriberId + ". Reason: " + e.getMessage(), e);
        } catch (ProcCallException e){
            VoltDBUtil.handleProcedureCallException(e, "updating monetary balance for ID: " + subscriberId, MODULE, alertListener, iTotalQueryTimeoutCount, voltDBClient);
        }
    }

    public void updateCreditLimit(String subscriberId, MonetaryBalance monetaryBalance, VoltDBClient voltDBClient) throws OperationFailedException{
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Update Credit Limit for subscriber-identifier : " + subscriberId);
        }

        if (voltDBClient.isAlive() == false) {
            throw new OperationFailedException("Unable to perform update credit limit operation for subscriber ID: " + subscriberId
                    + ". Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        try {
            long queryExecutionTime = timeSource.currentTimeInMillis();
            voltDBClient.callProcedure(MONETARY_BALANCE_UPDATE_CREDIT_LIMIT_STORED_PROCEDURE, subscriberId,
                    monetaryBalance.getId(),
                    monetaryBalance.getCreditLimit(),
                    monetaryBalance.getNextBillingCycleCreditLimit(),
                    new Timestamp(monetaryBalance.getCreditLimitUpdateTime()));

            queryExecutionTime = timeSource.currentTimeInMillis() - queryExecutionTime;

            checkForHighResponseTime(queryExecutionTime, UPDATE_CREDIT_LIMIT);

            iTotalQueryTimeoutCount.set(0);
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Update credit limit operation successfully completed for subscriber-identifier : " + monetaryBalance.getSubscriberId());
            }

        } catch (IOException e) {
            throw new OperationFailedException("Error while updating credit limit for ID: " + subscriberId + ". Reason: " + e.getMessage(), e);
        } catch (ProcCallException e){
            VoltDBUtil.handleProcedureCallException(e, "updating credit limit for ID: " + subscriberId, MODULE, alertListener, iTotalQueryTimeoutCount, voltDBClient);
        }
    }

    public void reserveBalance(String subscriberId, List<MonetaryBalance> lstmonetaryBalance, VoltDBClient voltDBClient) throws OperationFailedException{
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Reserve monetary balance for subscriber-identifier : " + subscriberId);
        }

        if (voltDBClient.isAlive() == false) {
            csvfailOverOperation.doFailover(new MonetaryOperationData(lstmonetaryBalance, subscriberId, ABMFOperationType.RESERVE));

            throw new OperationFailedException("Unable to perform reserve monetary balance operation for subscriber ID: " + subscriberId
                    + ". Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        try {
            for (MonetaryBalance monetaryBalance : lstmonetaryBalance) {
                long queryExecutionTime = timeSource.currentTimeInMillis();

                voltDBClient.callProcedure(MONETARY_BALANCE_RESERVE_STORED_PROCEDURE, subscriberId,
                        monetaryBalance.getId(),
                        monetaryBalance.getTotalReservation());

                queryExecutionTime = timeSource.currentTimeInMillis() - queryExecutionTime;

                checkForHighResponseTime(queryExecutionTime, RESERVE_MONETARY_BALANCE);
            }

            iTotalQueryTimeoutCount.set(0);

            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Reserve balance operation successfully completed.");
            }

        } catch (IOException e) {
            csvfailOverOperation.doFailover(new MonetaryOperationData(lstmonetaryBalance, subscriberId, ABMFOperationType.RESERVE));
            throw new OperationFailedException("Error while reserving monetary balance for ID: " + subscriberId + ". Reason: " + e.getMessage(), e);
        } catch (ProcCallException e){
            csvfailOverOperation.doFailover(new MonetaryOperationData(lstmonetaryBalance, subscriberId, ABMFOperationType.RESERVE));
            VoltDBUtil.handleProcedureCallException(e, "reserving monetary balance for ID: " + subscriberId, MODULE, alertListener, iTotalQueryTimeoutCount, voltDBClient);
        }
    }

    public void directDebitBalance(String subscriberId, List<MonetaryBalance> lstmonetaryBalance, VoltDBClient voltDBClient) throws OperationFailedException{
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "direct debit monetary balance for subscriber-identifier : " + subscriberId);
        }

        if (voltDBClient.isAlive() == false) {
            csvfailOverOperation.doFailover(new MonetaryOperationData(lstmonetaryBalance, subscriberId, ABMFOperationType.DIRECT_DEBIT));
            throw new OperationFailedException("Unable to perform direct debit monetary balance operation for subscriber ID: " + subscriberId
                    + ". Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        try {
            for (MonetaryBalance monetaryBalance : lstmonetaryBalance) {
                long queryExecutionTime = timeSource.currentTimeInMillis();

                voltDBClient.callProcedure(MONETARY_BALANCE_DIRECT_DEBIT_STORED_PROCEDURE, subscriberId,
                        monetaryBalance.getId(),
                        monetaryBalance.getAvailBalance());

                queryExecutionTime = timeSource.currentTimeInMillis() - queryExecutionTime;

                checkForHighResponseTime(queryExecutionTime, DIRECT_DEBIT_MONETARY_BALANCE);
            }

            iTotalQueryTimeoutCount.set(0);

            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Direct debit balance operation successfully completed.");
            }

        } catch (IOException e) {
            csvfailOverOperation.doFailover(new MonetaryOperationData(lstmonetaryBalance, subscriberId, ABMFOperationType.DIRECT_DEBIT));
            throw new OperationFailedException("Error while direct debit monetary balance for ID: " + subscriberId + ". Reason: " + e.getMessage(), e);
        } catch (ProcCallException e){
            csvfailOverOperation.doFailover(new MonetaryOperationData(lstmonetaryBalance, subscriberId, ABMFOperationType.DIRECT_DEBIT));
            VoltDBUtil.handleProcedureCallException(e, "Direct debit monetary balance for ID: " + subscriberId, MODULE, alertListener, iTotalQueryTimeoutCount, voltDBClient);
        }
    }

    public void reportAndReserveBalance(String subscriberId, List<MonetaryBalance> lstmonetaryBalance, VoltDBClient voltDBClient) throws OperationFailedException{
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Report and reserve monetary balance for subscriber-identifier : " + subscriberId);
        }

        if (voltDBClient.isAlive() == false) {
            csvfailOverOperation.doFailover(new MonetaryOperationData(lstmonetaryBalance, subscriberId, ABMFOperationType.REPORT_AND_RESERVE));
            throw new OperationFailedException("Unable to perform report and reserve monetary balance operation for subscriber ID: " + subscriberId
                    + ". Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        try {
            for (MonetaryBalance monetaryBalance : lstmonetaryBalance) {
                long queryExecutionTime = timeSource.currentTimeInMillis();

                voltDBClient.callProcedure(MONETARY_BALANCE_REPORT_AND_RESERVE_STORED_PROCEDURE, subscriberId,
                        monetaryBalance.getId(),
                        monetaryBalance.getTotalReservation(),
                        monetaryBalance.getAvailBalance());

                queryExecutionTime = timeSource.currentTimeInMillis() - queryExecutionTime;

                checkForHighResponseTime(queryExecutionTime, REPORT_AND_RESERVE_MONETARY_BALANCE);
            }

            iTotalQueryTimeoutCount.set(0);

            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Report and reserve balance operation successfully completed.");
            }

        } catch (IOException e) {
            csvfailOverOperation.doFailover(new MonetaryOperationData(lstmonetaryBalance, subscriberId, ABMFOperationType.REPORT_AND_RESERVE));
            throw new OperationFailedException("Error while report and reserve monetary balance for ID: " + subscriberId + ". Reason: " + e.getMessage(), e);
        } catch (ProcCallException e){
            csvfailOverOperation.doFailover(new MonetaryOperationData(lstmonetaryBalance, subscriberId, ABMFOperationType.REPORT_AND_RESERVE));
            VoltDBUtil.handleProcedureCallException(e, "Report and reserve monetary balance for ID: " + subscriberId, MODULE, alertListener, iTotalQueryTimeoutCount, voltDBClient);
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




    public void rechargeMonetaryBalance(MonetaryRechargeData monetaryRechargeData, EnumMap<SPRFields, String> updatedProfile, VoltDBClient voltDBClient) throws OperationFailedException {

        String subscriberId = monetaryRechargeData.getMonetaryBalance().getSubscriberId();

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Recharge monetary balance for subscriber-identifier : " + monetaryRechargeData.getSubscriberId());
        }

        if (voltDBClient.isAlive() == false) {
            throw new OperationFailedException("Unable to perform recharge monetary balance operation for subscriber ID: " + subscriberId
                    + ". Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        try {
            long queryExecutionTime = timeSource.currentTimeInMillis();
            if (MapUtils.isNotEmpty(updatedProfile)) {
                Timestamp subscriberExpiryDate = null;
                for (Map.Entry<SPRFields, String> entry : updatedProfile.entrySet()) {
                    if (entry.getKey() == SPRFields.EXPIRY_DATE) {
                        subscriberExpiryDate = getTimestampValue(entry.getValue());
                    }
                }
                voltDBClient.callProcedure(RECHARGE_AND_UPDATE_SUBSCRIBER_VALIDITY_STORED_PROCEDURE, subscriberId,
                        monetaryRechargeData.getMonetaryBalance().getId(), monetaryRechargeData.getPrice(), monetaryRechargeData.getAmount(),
                        new Timestamp(monetaryRechargeData.getExtendedValidity()), subscriberExpiryDate);
            } else {
                voltDBClient.callProcedure(RECHARGE_MONETARY_BALANCE_STORED_PROCEDURE, subscriberId,
                        monetaryRechargeData.getMonetaryBalance().getId(), monetaryRechargeData.getPrice(), monetaryRechargeData.getAmount(),
                        new Timestamp(monetaryRechargeData.getExtendedValidity()));
            }

            queryExecutionTime = timeSource.currentTimeInMillis() - queryExecutionTime;

            checkForHighResponseTime(queryExecutionTime, RECHARGE_MONETARY_BALANCE);

            iTotalQueryTimeoutCount.set(0);
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Recharge monetary balance operation successfully completed for subscriberIdentity : " + subscriberId);
            }

        } catch (IOException e) {
            throw new OperationFailedException("Error while recharging monetary balance for ID: " + subscriberId + ". Reason: " + e.getMessage(), e);
        } catch (ProcCallException e) {
            VoltDBUtil.handleProcedureCallException(e, "recharging monetary balance for ID: " + subscriberId, MODULE, alertListener, iTotalQueryTimeoutCount, voltDBClient);
        }
    }

    private Timestamp getTimestampValue(String expiryDate) throws OperationFailedException {
        Timestamp dateToTimestamp = null;
        Long longExpDate;
        if(Strings.isNullOrEmpty(expiryDate)==false){
            try{
                longExpDate = Long.parseLong(expiryDate.trim());
                dateToTimestamp = new Timestamp(longExpDate);

            }catch(NumberFormatException e){
                LogManager.getLogger().error(MODULE, "Error while converting "+ expiryDate +" to timestamp");
                throw new OperationFailedException("Error while converting "+ expiryDate +" to timestamp",ResultCode.INVALID_INPUT_PARAMETER);
            }catch(Exception e){
                LogManager.getLogger().error(MODULE, "Error while converting "+ expiryDate +" to timestamp");
                throw new OperationFailedException("Error while converting "+ expiryDate +" to timestamp",ResultCode.INVALID_INPUT_PARAMETER);
            }
        }
        return dateToTimestamp;
    }

    public void addMonetaryBalance(String subscriberId, MonetaryBalance monetaryBalance, String remark, String requestIPAddress, EnumMap<SPRFields, String> updatedProfile, VoltDBClient voltDBClient) throws OperationFailedException {

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Add monetary balance for subscriber-identifier : " + subscriberId);
        }

        if (voltDBClient.isAlive() == false) {

            throw new OperationFailedException("Unable to perform Add monetary balance operation for subscriber ID: " + subscriberId
                    + ". Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        try {
            long queryExecutionTime = timeSource.currentTimeInMillis();
            Object addMonetaryBalanceArray = VoltDBUtil.createAddMonetaryBalanceArray(monetaryBalance);
            if (MapUtils.isNotEmpty(updatedProfile)) {
                Timestamp subscriberExpiryDate = null;
                for (Map.Entry<SPRFields, String> entry : updatedProfile.entrySet()) {
                    if (entry.getKey() == SPRFields.EXPIRY_DATE) {
                        subscriberExpiryDate = getTimestampValue(entry.getValue());
                    }
                }
                voltDBClient.callProcedure(ADD_MONETARY_BALANCE_AND_UPDATE_SUBSCRIBER_VALIDITY_STORED_PROCEDURE, subscriberId, addMonetaryBalanceArray, new Timestamp(monetaryBalance.getValidFromDate()),
                        new Timestamp(monetaryBalance.getValidToDate()), new Timestamp(monetaryBalance.getCreditLimitUpdateTime()), subscriberExpiryDate);
            } else {

                voltDBClient.callProcedure(MONETARY_BALANCE_ADD_STORED_PROCEDURE, subscriberId, addMonetaryBalanceArray, new Timestamp(monetaryBalance.getValidFromDate()),
                        new Timestamp(monetaryBalance.getValidToDate()), new Timestamp(monetaryBalance.getCreditLimitUpdateTime()));
            }

            queryExecutionTime = timeSource.currentTimeInMillis() - queryExecutionTime;

            checkForHighResponseTime(queryExecutionTime, RECHARGE_MONETARY_BALANCE);

            iTotalQueryTimeoutCount.set(0);
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Add monetary balance operation successfully completed for subscriberIdentity : " + subscriberId);
            }

        } catch (IOException e) {
            throw new OperationFailedException("Error while Add monetary balance for ID: " + subscriberId + ". Reason: " + e.getMessage(), e);
        } catch (ProcCallException e) {
            VoltDBUtil.handleProcedureCallException(e, "Adding monetary balance for ID: " + subscriberId, MODULE, alertListener, iTotalQueryTimeoutCount, voltDBClient);
        }
    }
}
