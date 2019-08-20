package com.elitecore.corenetvertex.spr;

import com.elitecore.commons.annotations.VisibleForTesting;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.base.Maps;
import com.elitecore.commons.base.Numbers;
import com.elitecore.commons.base.Stopwatch;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.corenetvertex.constants.ActionType;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.SubscriptionState;
import com.elitecore.corenetvertex.constants.ValidityPeriodUnit;
import com.elitecore.corenetvertex.core.alerts.AlertConstants;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.alerts.Alerts;
import com.elitecore.corenetvertex.core.transaction.DBTransaction;
import com.elitecore.corenetvertex.core.transaction.Transaction;
import com.elitecore.corenetvertex.core.transaction.TransactionException;
import com.elitecore.corenetvertex.core.transaction.TransactionFactory;
import com.elitecore.corenetvertex.core.transaction.exception.TransactionErrorCode;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.pkg.datapackage.QuotaTopUp;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.data.SubscriptionType;
import com.elitecore.corenetvertex.spr.exceptions.DBOperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.util.util.Predicates;

import javax.annotation.Nullable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.elitecore.commons.base.DBUtility.closeQuietly;
import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.corenetvertex.constants.CommonConstants.SUBSCRIBE_TOPUP;
import static com.elitecore.corenetvertex.spr.util.SPRUtil.toStringSQLException;
import static java.util.Objects.nonNull;

public class TopUpSubscriptionOperation {


    private static final String MODULE = "TOPUP-SUBSCRIPTION-OPR";
    private static final String TABLE_TBLT_SUBSCRIPTION = "TBLT_SUBSCRIPTION";
    private static final String SEVERITY = "";

    //queries
    private final String queryForSubscribeQuotaTopUp;
    private final String queryForCheckSubscriptionExistBySubscriberIdAndQuotaTopUpId;
    private final String queryForUpdateSubscriptionStatusBySubscriptionId;
    private final String queryForUpdateSubscriptionDetailBySubscriptionId;
    private final String queryForSelectSubscriptionBySubscriptionId;
    private final String queryForUnsubscribeBySubscriberId;

    private AtomicInteger totalQueryTimeoutCount;
    private AlertListener alertListener;
    private PolicyRepository policyRepository;
    private ABMFOperation abmfOperation;
    private MonetaryABMFOperationImpl monetaryABMFOperationImpl;
    private EDRListener balanceEDRListener;

    private static final LinkedHashMap<String, Subscription> EMPTY_MAP = new LinkedHashMap<String, Subscription>(1, 1);

    public TopUpSubscriptionOperation(AlertListener alertListener, PolicyRepository policyRepository, ABMFOperation abmfOperation,MonetaryABMFOperationImpl monetaryABMFOperationImpl, EDRListener balanceEDRListener) {
        this.alertListener = alertListener;
        this.policyRepository = policyRepository;
        this.abmfOperation = abmfOperation;
        this.monetaryABMFOperationImpl=monetaryABMFOperationImpl;
        this.totalQueryTimeoutCount = new AtomicInteger(0);
        this.queryForSubscribeQuotaTopUp = buildQueryForSubscriberQuotaTopUp();
        this.queryForCheckSubscriptionExistBySubscriberIdAndQuotaTopUpId = buildQueryForCheckSubscriptionExistBySubscriberIdAndQuotaTopUpId();
        this.queryForUpdateSubscriptionStatusBySubscriptionId = buildQueryForUpdateSubscriptionStatusBySubscriptionId();
        this.queryForUpdateSubscriptionDetailBySubscriptionId = buildQueryForUpdateSubscriptionDetailBySubscriptionId();
        this.queryForSelectSubscriptionBySubscriptionId = builQueryForSelectSubscriptionBySubscriptionId();
        this.queryForUnsubscribeBySubscriberId = buildQueryForUnsubscribeBySubscriberId();
        this.balanceEDRListener = balanceEDRListener;
    }

    private String buildQueryForUnsubscribeBySubscriberId() {
        return "UPDATE " + TABLE_TBLT_SUBSCRIPTION + " SET STATUS='" + SubscriptionState.UNSUBSCRIBED.state
                + "', LAST_UPDATE_TIME=CURRENT_TIMESTAMP" + " WHERE SUBSCRIBER_ID=?";
    }

    private String builQueryForSelectSubscriptionBySubscriptionId() {
        return "SELECT * FROM " + TABLE_TBLT_SUBSCRIPTION + " WHERE SUBSCRIPTION_ID=?";
    }

    private String buildQueryForUpdateSubscriptionDetailBySubscriptionId() {
        return "UPDATE " + TABLE_TBLT_SUBSCRIPTION + " SET START_TIME=?, END_TIME=?, PRIORITY= ?, LAST_UPDATE_TIME=CURRENT_TIMESTAMP" +
                " WHERE SUBSCRIPTION_ID=?";
    }

    private String buildQueryForUpdateSubscriptionStatusBySubscriptionId() {
        return "UPDATE " + TABLE_TBLT_SUBSCRIPTION + " SET STATUS=?, REJECT_REASON=?, LAST_UPDATE_TIME=CURRENT_TIMESTAMP" +
                " WHERE SUBSCRIPTION_ID=?";
    }

    private String buildQueryForCheckSubscriptionExistBySubscriberIdAndQuotaTopUpId() {
        return "SELECT SUBSCRIPTION_ID, STATUS, END_TIME FROM " + TABLE_TBLT_SUBSCRIPTION + " WHERE SUBSCRIBER_ID=? AND PACKAGE_ID=?";
    }

    private String buildQueryForSubscriberQuotaTopUp() {
        return new StringBuilder("INSERT INTO ")
                .append(TABLE_TBLT_SUBSCRIPTION)
                .append("(SUBSCRIPTION_ID, PACKAGE_ID, PRODUCT_OFFER_ID, PARENT_IDENTITY, SUBSCRIBER_ID, SUBSCRIPTION_TIME, SERVER_INSTANCE_ID,")
                .append(" START_TIME, END_TIME, LAST_UPDATE_TIME, STATUS, PRIORITY, PARAM1, PARAM2,TYPE)")
                .append(" VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?, ?, ?, CURRENT_TIMESTAMP, ?, ?, ?, ?,?)")
                .toString();
    }


    public Subscription subscribeQuotaTopUpById(SPRInfo sprInfo, String parentId, String quotaTopUpId,
                                                Integer subscriptionStatusValue,
                                                Long startTime, Long endTime, Integer priority
                                                ,double subscriptionPrice, String monetaryBalanceId
                                                , String param1, String param2,
                                                TransactionFactory transactionFactory) throws OperationFailedException {
        String subscriberIdentity = sprInfo.getSubscriberIdentity();

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Subscribing package(" + quotaTopUpId + ") for subscriber ID: " + subscriberIdentity);
        }

        QuotaTopUp quotaTopUp = policyRepository.getActiveQuotaTopUpById(quotaTopUpId);

        if (quotaTopUp == null) {
            throw new OperationFailedException("Unable to subscribe package(" + quotaTopUpId + ") for subscriber ID: " + subscriberIdentity
                    + ". Reason: Package not found for ID: " + quotaTopUpId, ResultCode.NOT_FOUND);
        }

        if (quotaTopUp.getStatus() == PolicyStatus.FAILURE) {
            throw new OperationFailedException("Unable to subscribe Quota TopUp(" + quotaTopUpId + ") for subscriber ID: " + subscriberIdentity
                    + ". Reason: Quota TopUp(" + quotaTopUp.getName() + ") is failed addOn", ResultCode.INVALID_INPUT_PARAMETER);
        }

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Package(" + quotaTopUp.getName() + ") found for ID: " + quotaTopUp.getId());
        }

        return subscribeQuotaTopUp(quotaTopUp, sprInfo, parentId, subscriptionStatusValue, startTime, endTime, transactionFactory, priority, subscriptionPrice, monetaryBalanceId, param1, param2, null);
    }

    private void validateStartTimeAndEndTime(long startTimeMs, long endTimeMs, long currentTimeMs) throws OperationFailedException {
        if (endTimeMs <= currentTimeMs) {
            throw new OperationFailedException("End time(" + new Timestamp(endTimeMs).toString() + ") is less or equal to current time", ResultCode.INVALID_INPUT_PARAMETER);
        }

        if (startTimeMs >= endTimeMs) {
            throw new OperationFailedException("Start time(" + new Timestamp(startTimeMs).toString() + ") is more or equal to end time("
                    + new Timestamp(endTimeMs).toString() + ")", ResultCode.INVALID_INPUT_PARAMETER);
        }
    }

    /**
     * IF subscription state is not received THEN take SubscriptionState.STARTED
     * <p>
     * IF invalid state received THEN throws OperationFailedException
     *
     * @param subscriberIdentity
     * @param subscriptionStatusValue
     * @return SubscriptionState
     * @throws OperationFailedException
     */
    private SubscriptionState getSubscriptionStateFromValue(String subscriberIdentity, @Nullable Integer subscriptionStatusValue)
            throws OperationFailedException {
        if (subscriptionStatusValue == null) {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Considering Subscription Status: " + SubscriptionState.STARTED
                        + " for Subscriber ID: " + subscriberIdentity
                        + ". Reason: Subscription status not received");
            }

            return SubscriptionState.STARTED;
        }

        SubscriptionState subscriptionState = SubscriptionState.fromValue(subscriptionStatusValue.intValue());
        if (subscriptionState == null) {
            throw new OperationFailedException("Invalid subscription status value: " + subscriptionStatusValue + " received", ResultCode.INVALID_INPUT_PARAMETER);
        }

        if (subscriptionState != SubscriptionState.STARTED) {
            throw new OperationFailedException("Invalid subscription status: " + subscriptionState.name + " received", ResultCode.INVALID_INPUT_PARAMETER);
        }
        return subscriptionState;
    }

    private Subscription subscribeQuotaTopUp(QuotaTopUp subscriptionPackage,
                                             SPRInfo sprInfo,
                                             String parentId,
                                             Integer subscriptionStatusValue,
                                             Long startTime,
                                             Long endTime,
                                             TransactionFactory transactionFactory,
                                             Integer priority,
                                             double subscriptionPrice,
                                             String monetaryBalanceId,
                                             String param1,
                                             String param2,
                                             @Nullable String subscriptionId)
            throws OperationFailedException {



        String subscriberIdentity = sprInfo.getSubscriberIdentity();
        long currentTimeMS = System.currentTimeMillis();

        startTime = Numbers.POSITIVE_LONG.apply(startTime) ? startTime.longValue() : currentTimeMS;

        endTime = Numbers.POSITIVE_LONG.apply(endTime) ? endTime.longValue()
                : addTime(startTime, subscriptionPackage.getValidity(), subscriptionPackage.getValidityPeriodUnit());

        if(Objects.nonNull(priority) && priority < 1) {
            throw new OperationFailedException("Unable to subscribe quota topup(ID: " + subscriptionPackage.getId() + ", Name: " + subscriptionPackage.getName()
                    + ") for subscriber ID: " + subscriberIdentity
                    + " Reason: Invalid priority:" + priority);
        }

        validateStartTimeAndEndTime(startTime, endTime, currentTimeMS);

        if (transactionFactory.isAlive() == false) {
            throw new OperationFailedException("Unable to subscribe quota topup(ID: " + subscriptionPackage.getId() + ", Name: " + subscriptionPackage.getName()
                    + ") for subscriber ID: " + subscriberIdentity
                    + " Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        Transaction transaction = transactionFactory.createTransaction();

        if (transaction == null) {
            throw new OperationFailedException("Unable to subscribe Quota TopUp(ID: " + subscriptionPackage.getId() + ", Name: " + subscriptionPackage.getName()
                    + ") for subscriber ID: " + subscriberIdentity
                    + " Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        try {
            transaction.begin();

            if (subscriptionPackage.isMultipleSubscription() == false) {
                checkExistingSubscription(subscriberIdentity, subscriptionPackage, transaction);
            }

            Subscription quotaTopUpSubscription = addSubscriptionDetails(subscriptionPackage, subscriberIdentity
                    , parentId, subscriptionStatusValue, startTime, endTime, priority, transaction, param1, param2, subscriptionId);


            abmfOperation.addBalance(subscriberIdentity, quotaTopUpSubscription, subscriptionPackage, transaction);

            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Package(" + subscriptionPackage.getName() + ") subscription successful for subscriber ID: "
                        + subscriberIdentity + " with subscription ID(" + quotaTopUpSubscription.getId() + ")");
            }

            if(subscriptionPrice > 0) {
                monetaryABMFOperationImpl.directDebitBalance(subscriberIdentity, monetaryBalanceId, subscriptionPrice, transaction);
                if(nonNull(balanceEDRListener)) {
                    MonetaryBalance monetaryBalance = sprInfo.getMonetaryBalance(Predicates.ALL_MONETARY_BALANCE).getBalanceById(monetaryBalanceId);
                    MonetaryBalance previousMonetaryBalance = monetaryBalance.copy();
                    monetaryBalance.setAvailBalance(subscriptionPrice*-1);
                    balanceEDRListener.updateMonetaryEDR(new SubscriberMonetaryBalanceWrapper(monetaryBalance, previousMonetaryBalance, sprInfo, SUBSCRIBE_TOPUP, ActionType.UPDATE.name(),null),
                            null, SUBSCRIBE_TOPUP);
                }
            }

            return quotaTopUpSubscription;

        } catch (SQLException e) {
            ResultCode resultCode = ResultCode.INTERNAL_ERROR;
            if (isQueryTimeoutException(e)) {
                if (isTotalQueryTimeOutCountExceed()) {
                    if (getLogger().isWarnLogLevel()) {
                        getLogger().warn(MODULE, "total number of query timeouts(" + totalQueryTimeoutCount.intValue()
                                + ") exceeded then configured max number "
                                + "of query timeouts(" + CommonConstants.MAX_QUERY_TIMEOUT_COUNT_DEFAULT + "), so System marked as DEAD");
                    }
                    transaction.markDead();
                }
                alertListener.generateSystemAlert(SEVERITY, Alerts.QUERY_TIME_OUT, MODULE,
                        "DB query timeout while subscribing package from DB with data source name: "
                                + transaction.getDataSourceName());

                resultCode = ResultCode.SERVICE_UNAVAILABLE;

            } else if (transaction.isDBDownSQLException(e)) {
                if (getLogger().isWarnLogLevel()) {
                    getLogger().warn(MODULE, "Database with data source name: " + transaction.getDataSourceName()
                            + " is Down, System marked as dead. Reason: " + e.getMessage());
                }
                transaction.markDead();

                resultCode = ResultCode.SERVICE_UNAVAILABLE;
            }

            transaction.rollback();

            throw new OperationFailedException(toStringSQLException("subscribing package: " + subscriptionPackage.getName(),subscriberIdentity, e), resultCode, e);

        } catch (TransactionException e) {

            ResultCode sprErrorCode = ResultCode.INTERNAL_ERROR;

            if (isConnectionNotFoundException(e)) {

                alertListener.generateSystemAlert(SEVERITY, Alerts.DATABASE_CONNECTION_NOT_AVAILABLE, MODULE,
                        "Unable to subscribe package for subscriber ID: " + subscriberIdentity + " from DB with data source name: "
                                + transaction.getDataSourceName() + ". Reason: Connection not available");
                sprErrorCode = ResultCode.SERVICE_UNAVAILABLE;
            }

            transaction.rollback();
            throw new OperationFailedException("Unable to subscribe package(" + subscriptionPackage.getName() + ") for subscriber ID: " + subscriberIdentity
                    + ". Reason: " + e.getMessage(), sprErrorCode, e);
        } catch (OperationFailedException e) {

            transaction.rollback();

            throw new OperationFailedException("Unable to subscribe package(" + subscriptionPackage.getName() + ") for subscriber ID: " + subscriberIdentity
                    + ". Reason: " + e.getMessage(), e.getErrorCode(), e);
        } finally {

            endTransaction(transaction);
        }

    }

    private void checkExistingSubscription(String subscriberIdentity, QuotaTopUp subscriptionPackage, Transaction transaction)
            throws SQLException, OperationFailedException, TransactionException {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Multiple subscription is not allowed so checking existing subscription for subscriber ID("
                    + subscriberIdentity + "), package(" + subscriptionPackage.getName() + ")");
        }

        PreparedStatement psForSubscriptionExistCheck = null;
        ResultSet rsForSubscriptionExistCheck = null;

        try {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Query: " + queryForCheckSubscriptionExistBySubscriberIdAndQuotaTopUpId);
            }
            psForSubscriptionExistCheck = transaction.prepareStatement(queryForCheckSubscriptionExistBySubscriberIdAndQuotaTopUpId);
            psForSubscriptionExistCheck.setQueryTimeout(CommonConstants.QUERY_TIMEOUT_DEFAULT);
            psForSubscriptionExistCheck.setString(1, subscriberIdentity);
            psForSubscriptionExistCheck.setString(2, subscriptionPackage.getId());

            long queryExecutionTime = System.currentTimeMillis();
            rsForSubscriptionExistCheck = psForSubscriptionExistCheck.executeQuery();
            queryExecutionTime = System.currentTimeMillis() - queryExecutionTime;

            if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

                alertListener.generateSystemAlert(SEVERITY, Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
                        "DB Query execution time is high for datasource: " + transaction.getDataSourceName() +
                                ". Last Query execution time: " + queryExecutionTime + " ms");

                if (getLogger().isWarnLogLevel()) {
                    getLogger().warn(MODULE, "DB Query execution time getting high, Last query execution time = "
                            + queryExecutionTime + " milliseconds.");
                }
            }

            long currentTime = System.currentTimeMillis();

            while (rsForSubscriptionExistCheck.next()) {

                String subscriptionId = rsForSubscriptionExistCheck.getString("SUBSCRIPTION_ID");
                Timestamp endTime = rsForSubscriptionExistCheck.getTimestamp("END_TIME");
                int status = rsForSubscriptionExistCheck.getInt("STATUS");
                SubscriptionState subscriptionState = SubscriptionState.fromValue(status);

                if (subscriptionState == SubscriptionState.UNSUBSCRIBED || subscriptionState == SubscriptionState.REJECTED) {
                    continue;
                }

                if (endTime.getTime() <= currentTime) {
                    continue;
                }

                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "Subscription(" + subscriptionId + ") exist for subscriber ID(" + subscriberIdentity + "), package("
                            + subscriptionPackage.getName() + ")");
                }

                throw new OperationFailedException("Subscription(" + subscriptionId + ") already exist for subscriber ID(" + subscriberIdentity
                        + "), package(" + subscriptionPackage.getName() + ")", ResultCode.ALREADY_EXIST);
            }
        } finally {
            closeQuietly(rsForSubscriptionExistCheck);
            closeQuietly(psForSubscriptionExistCheck);
        }

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Subscription not exist for subscriber ID(" + subscriberIdentity + ") and package(" + subscriptionPackage.getName() + ")");
        }
    }

    @VisibleForTesting
    String getNextSubscriptionId() {
        return UUID.randomUUID().toString();
    }

    private boolean isConnectionNotFoundException(TransactionException e) {
        return e.getErrorCode() == TransactionErrorCode.CONNECTION_NOT_FOUND;
    }

    private boolean isTotalQueryTimeOutCountExceed() {
        boolean result = false;
        if (totalQueryTimeoutCount.incrementAndGet() > CommonConstants.MAX_QUERY_TIMEOUT_COUNT_DEFAULT) {
            result = true;
            resetTotalQueryTimeoutCount();
        }
        return result;
    }

    private boolean isQueryTimeoutException(SQLException e) {
        return e.getErrorCode() == CommonConstants.QUERY_TIMEOUT_ERRORCODE;
    }

    /*
     * gives future time (time + validity period according to
     * validityPeriodUnit)
     */
    private long addTime(long startTime, int validityPeriod, ValidityPeriodUnit validityPeriodUnit) {
        return validityPeriodUnit.addTime(startTime, validityPeriod);
    }

    /**
     * <PRE>
     * if no addOn subscription found then
     * returns EMPTY_LIST
     * </PRE>
     *
     * @throws DBOperationFailedException
     */

    public LinkedHashMap<String, Subscription> getQuotaTopUpSubscriptions(
            SPRInfo sprInfo, TransactionFactory transactionFactory) throws OperationFailedException {
        String subscriberIdentity = sprInfo.getSubscriberIdentity();

        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Fetching active subscriptions for subscriber ID: " + subscriberIdentity);
        }

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        if (transactionFactory.isAlive() == false) {
            throw new OperationFailedException("Unable to fetch active subscription for subscriber ID: " + subscriberIdentity
                    + " Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        DBTransaction dbTransaction = transactionFactory.createReadOnlyTransaction();
        if (dbTransaction == null) {
            throw new OperationFailedException("Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        try {
            dbTransaction.begin();
            preparedStatement = dbTransaction.prepareStatement(getQueryForQuotaTopUp());
            preparedStatement.setString(1, subscriberIdentity);
            preparedStatement.setQueryTimeout(CommonConstants.QUERY_TIMEOUT_DEFAULT);

            Stopwatch stopwatch = new Stopwatch();
            stopwatch.start();
            resultSet = preparedStatement.executeQuery();
            stopwatch.stop();
            long queryExecutionTime = stopwatch.elapsedTime(TimeUnit.MILLISECONDS);
            sprInfo.setSubscriptionsLoadTime(queryExecutionTime);
            if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

                alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
                        "DB Query execution time is high while fetching active subscription. "
                                + "Last Query execution time: " + queryExecutionTime + " ms");

                if (getLogger().isLogLevel(LogLevel.WARN)) {
                    getLogger().warn(MODULE, "DB Query execution time is high while fetching active subscription. "
                            + "Last Query execution time: " + queryExecutionTime + " ms");
                }
            }


            long queryReadStartTime = getCurrentTime();
            LinkedHashMap<String,Subscription> addOnSubscriptions = createSubscriptions(subscriberIdentity, resultSet);
            long queryReadTime = getCurrentTime() - queryReadStartTime;
            sprInfo.setSubscriptionsReadTime(queryReadTime);

            resetTotalQueryTimeoutCount();

            if ((addOnSubscriptions == null || addOnSubscriptions.isEmpty()) == false) {
                return addOnSubscriptions;
            }
        } catch (SQLException e) {
            handlequotaTopUpException(e, subscriberIdentity, dbTransaction);
        } catch (TransactionException e) {
            handleAddonTransactionException(e, subscriberIdentity, dbTransaction);
        } catch (Exception e) {
            throw new OperationFailedException("Error while fetching addOn subscription for subscriber: " + subscriberIdentity
                    + ". Reason. " + e.getMessage(), ResultCode.INTERNAL_ERROR, e);
        } finally {
            DBUtility.closeQuietly(resultSet);
            DBUtility.closeQuietly(preparedStatement);
            endTransaction(dbTransaction);
        }

        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Active Subscriptions are not exist with subscriber ID: " + subscriberIdentity);
        }

        return EMPTY_MAP;
    }

    private LinkedHashMap<String, Subscription> createSubscriptions(String subscriberIdentity, ResultSet resultSet) throws SQLException {
        long currentTime = getCurrentTime();
        LinkedHashMap<String, Subscription> quotaTopUpSubscriptions = null;
        while (resultSet.next()) {
            String quotaTopUpId = resultSet.getString("PACKAGE_ID");
            String productOfferId = resultSet.getString("PRODUCT_OFFER_ID");
            String quotaTopUpSubscriptionId = resultSet.getString("SUBSCRIPTION_ID");
            Timestamp startTime = resultSet.getTimestamp("START_TIME");
            Timestamp endtime = resultSet.getTimestamp("END_TIME");
            int status = resultSet.getInt("STATUS");
            int priority = resultSet.getInt("PRIORITY");
            SubscriptionState state = SubscriptionState.fromValue(status);

            QuotaTopUp subscribedAddon = policyRepository.getQuotaTopUpById(quotaTopUpId);

            if (subscribedAddon == null) {
                if (getLogger().isErrorLogLevel()) {
                    getLogger().error(MODULE, "Skipping subscriptions: " + quotaTopUpSubscriptionId
                            + ", subscriberId: " + subscriberIdentity
                            + ". Reason: Quota TopUp not found for id: " + quotaTopUpId);
                }
                continue;
            }

            if (state == null) {
                getLogger().error(MODULE, "Error while getting active subscriptions:" + quotaTopUpSubscriptionId
                        + ", id:" + quotaTopUpId + ", subscriberId:" + subscriberIdentity
                        + ". Reason: Illegle status determined, state: " + status);
                continue;
            }
            // FIXME need to improve this validation checks --chetan
            if (endtime == null) {
                if (state == SubscriptionState.APPROVAL_PENDING) {
                    if (getLogger().isDebugLogLevel()) {
                        getLogger().debug(MODULE, "Approval pending for subscription:" + quotaTopUpSubscriptionId
                                + ", id:" + quotaTopUpId + ", subscriberId:" + subscriberIdentity);
                    }
                } else if (state == SubscriptionState.REJECTED) {
                    if (getLogger().isDebugLogLevel()) {
                        getLogger().debug(MODULE, "Subscription:" + quotaTopUpSubscriptionId + " rejected for id: "
                                + quotaTopUpId + ", subscriberId: " + subscriberIdentity);
                    }
                } else {
                    if (getLogger().isWarnLogLevel()) {
                        getLogger().warn(MODULE, "Endtime is not provided for subscription:" + quotaTopUpSubscriptionId
                                + ", addon:" + quotaTopUpId + ", subscriberId: " + subscriberIdentity);
                    }

                }
                continue;
            }

            if (endtime.getTime() <= currentTime) {
                if (getLogger().isInfoLogLevel()) {
                    getLogger().info(MODULE, "Skipping subscription:" + quotaTopUpSubscriptionId
                            + ", id:" + quotaTopUpId + " for subscriberId:" + subscriberIdentity
                            + ". Reason:  subscription is expired on Endtime:" + endtime + " ");
                }
                continue;
            }

            if (state == SubscriptionState.UNSUBSCRIBED) {
                if (getLogger().isLogLevel(LogLevel.INFO))
                    getLogger().info(MODULE, "Skipping subscription:" + quotaTopUpSubscriptionId
                            + ", id:" + quotaTopUpId + " for subscriberId:" + subscriberIdentity
                            + ". Reason: Subscription is Unsubscribed");
                continue;
            }

            String param1 = resultSet.getString("PARAM1");
            String param2 = resultSet.getString("PARAM2");

            if (quotaTopUpSubscriptions == null) {
                quotaTopUpSubscriptions = new LinkedHashMap<String, Subscription>();
            }

            quotaTopUpSubscriptions.put(quotaTopUpSubscriptionId, new Subscription(quotaTopUpSubscriptionId, subscriberIdentity, subscribedAddon.getId(), productOfferId, startTime, endtime, state, priority, SubscriptionType.TOP_UP, param1, param2));
        }
        return quotaTopUpSubscriptions;
    }

    public LinkedHashMap<String, Subscription> getQuotaTopUpSubscriptions(
            String subscriberIdentity,
            TransactionFactory transactionFactory) throws OperationFailedException {

        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Fetching active subscriptions for subscriber ID: " + subscriberIdentity);
        }

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        if (transactionFactory.isAlive() == false) {
            throw new OperationFailedException("Unable to fetch active subscription for subscriber ID: " + subscriberIdentity
                    + " Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        DBTransaction dbTransaction = transactionFactory.createReadOnlyTransaction();
        if (dbTransaction == null) {
            throw new OperationFailedException("Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        try {
            dbTransaction.begin();
            preparedStatement = dbTransaction.prepareStatement(getQueryForQuotaTopUp());
            preparedStatement.setString(1, subscriberIdentity);
            preparedStatement.setQueryTimeout(CommonConstants.QUERY_TIMEOUT_DEFAULT);

            Stopwatch stopwatch = new Stopwatch();
            stopwatch.start();
            resultSet = preparedStatement.executeQuery();
            stopwatch.stop();
            long queryExecutionTime = stopwatch.elapsedTime(TimeUnit.MILLISECONDS);

            if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

                alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
                        "DB Query execution time is high while fetching active subscription. "
                                + "Last Query execution time: " + queryExecutionTime + " ms");

                if (getLogger().isLogLevel(LogLevel.WARN)) {
                    getLogger().warn(MODULE, "DB Query execution time is high while fetching active subscription. "
                            + "Last Query execution time: " + queryExecutionTime + " ms");
                }
            }

            LinkedHashMap<String, Subscription> quotaTopUpSubscriptions = createSubscriptions(subscriberIdentity, resultSet);

            resetTotalQueryTimeoutCount();

            if (Maps.isNullOrEmpty(quotaTopUpSubscriptions) == false) {
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, quotaTopUpSubscriptions.size() + " TopUp subscription found for subscriber ID: " + subscriberIdentity);
                }
                return quotaTopUpSubscriptions;
            } else {
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "No TopUp subscription found for subscriber ID: " + subscriberIdentity);
                }
            }
        } catch (SQLException e) {
            handlequotaTopUpException(e, subscriberIdentity, dbTransaction);
        } catch (TransactionException e) {
            handleAddonTransactionException(e, subscriberIdentity, dbTransaction);
        } catch (Exception e) {
            throw new OperationFailedException("Error while fetching quota topUp subscription for subscriber: " + subscriberIdentity
                    + ". Reason. " + e.getMessage(), ResultCode.INTERNAL_ERROR, e);
        } finally {
            DBUtility.closeQuietly(resultSet);
            DBUtility.closeQuietly(preparedStatement);
            endTransaction(dbTransaction);
        }

        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Active Subscriptions are not exist with subscriber ID: " + subscriberIdentity);
        }

        return EMPTY_MAP;
    }

    private void handlequotaTopUpException(SQLException e, String subscriberIdentity, DBTransaction dbTransaction)
            throws OperationFailedException {

        ResultCode resultCode = ResultCode.INTERNAL_ERROR;

        if (isQueryTimeoutException(e)) {
            if (isTotalQueryTimeOutCountExceed()) {
                if (getLogger().isWarnLogLevel()) {
                    getLogger()
                            .warn(MODULE, "total number of query timeouts exceeded then configured max number of query timeouts, so System marked as DEAD");
                }
                dbTransaction.markDead();
            }
            alertListener.generateSystemAlert("", Alerts.QUERY_TIME_OUT, MODULE,
                    "DB Query Timeout while fetching Quota TopUp subscription from table name: "
                            + TABLE_TBLT_SUBSCRIPTION, 0, "fetching data from table name: " + TABLE_TBLT_SUBSCRIPTION);

            resultCode = ResultCode.SERVICE_UNAVAILABLE;
        } else if (dbTransaction.isDBDownSQLException(e)) {
            dbTransaction.markDead();
            if (getLogger().isWarnLogLevel()) {
                getLogger().warn(MODULE, "Database with data source name: " + dbTransaction.getDataSourceName()
                        + " is Down, System marked as dead. Reason: " + e.getMessage());
            }
            resultCode = ResultCode.SERVICE_UNAVAILABLE;
        }

        throw new OperationFailedException(toStringSQLException("fetching Quota TopUp subscription", subscriberIdentity, e), resultCode, e);
    }

    private void endTransaction(@Nullable DBTransaction transaction) {
        try {
            if (transaction != null) {
                transaction.end();
            }
        } catch (TransactionException e) {
            getLogger().trace(MODULE, e);
        }
    }

    protected long getCurrentTime() {
        return System.currentTimeMillis();
    }

    private static String getQueryForQuotaTopUp() {
        return "SELECT PACKAGE_ID, PRODUCT_OFFER_ID, SUBSCRIPTION_ID, START_TIME, END_TIME, PRIORITY, STATUS, PARAM1, PARAM2" +
                " FROM " + TABLE_TBLT_SUBSCRIPTION +
                " WHERE SUBSCRIBER_ID = ?";
    }

    private void handleAddonTransactionException(TransactionException e, String subscriberIdentity, DBTransaction transaction)
            throws OperationFailedException {

        ResultCode resultCode = ResultCode.INTERNAL_ERROR;
        if (isConnectionNotFoundException(e)) {

            alertListener.generateSystemAlert("", Alerts.DATABASE_CONNECTION_NOT_AVAILABLE, MODULE,
                    "Unable to get Add On subscription from " + transaction.getDataSourceName() +
                            " database. Reason: Connection not available");
            resultCode = ResultCode.SERVICE_UNAVAILABLE;
        }

        throw new OperationFailedException("Error while fetching AddOn subscription for subscriber: " + subscriberIdentity + ". Reason. " + e.getMessage(), resultCode, e);
    }

    private void resetTotalQueryTimeoutCount() {
        totalQueryTimeoutCount.set(0);
    }


    public Subscription subscribeQuotaTopUpByName(SPRInfo sprInfo, String parentId, String quotaTopUpName, Integer subscriptionStatusValue, Long startTime,
                                                  Long endTime, Integer priority ,double subscriptionPrice,String monetaryBalanceId,String param1, String param2, TransactionFactory transactionFactory) throws OperationFailedException {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Subscribing quota topUp(" + quotaTopUpName + ") for subscriber ID: " + sprInfo.getSubscriberIdentity());
        }

        QuotaTopUp quotaTopUp = policyRepository.getActiveQuotaTopUpByName(quotaTopUpName);
        if (quotaTopUp == null) {
            throw new OperationFailedException("Unable to subscribe quota topUp(" + quotaTopUpName + ") for subscriber ID: " + sprInfo.getSubscriberIdentity()
                    + ". Reason: quota topUp not found for Name: " + quotaTopUpName, ResultCode.NOT_FOUND);
        }

        if (quotaTopUp.getStatus() == PolicyStatus.FAILURE) {
            throw new OperationFailedException("Unable to subscribe quota quota topUp(" + quotaTopUpName + ") for subscriber ID: " + sprInfo.getSubscriberIdentity()
                    + ". Reason: quota topUp(" + quotaTopUpName + ") is failed quota topUp", ResultCode.INVALID_INPUT_PARAMETER);
        }

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "quota topUp(" + quotaTopUp.getName() + ") found");
        }

        return subscribeQuotaTopUp(quotaTopUp, sprInfo, parentId, subscriptionStatusValue, startTime, endTime, transactionFactory, priority, subscriptionPrice,monetaryBalanceId,param1, param2, null);

    }

    /***
     *
     *
     * @param subscriptionId
     *            , required
     * @param subscriptionStatusValue
     *            , required
     * @param startTime
     * @param endTime
     * @param priority
     * @param rejectReason
     * @param transactionFactory
     *
     * @return
     * @throws OperationFailedException
     */


    public Subscription updateSubscription(
            String subscriberIdentity,
            String subscriptionId, Integer subscriptionStatusValue,
            Long startTime,
            Long endTime,
            Integer priority,
            String rejectReason,
            TransactionFactory transactionFactory
    ) throws OperationFailedException {

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Updating subscription(" + subscriptionId + ")");
        }

        if (transactionFactory.isAlive() == false) {
            throw new OperationFailedException("Unable to update subscription for subscription ID: " + subscriptionId
                    + ". Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        long currentTimeMS = System.currentTimeMillis();

        if (subscriptionStatusValue == null) {
            throw new OperationFailedException("Unable to update subscription for subscription ID: " + subscriptionId
                    + ". Reason: Subscription status value not found", ResultCode.INVALID_INPUT_PARAMETER);
        }

        SubscriptionState newSubscriptionState = SubscriptionState.fromValue(subscriptionStatusValue.intValue());
        if (newSubscriptionState == null) {
            throw new OperationFailedException("Unable to update subscription for subscription ID: " + subscriptionId
                    + ". Reason: Invalid subscription status value: " + subscriptionStatusValue + " received", ResultCode.INVALID_INPUT_PARAMETER);
        }

        try {
            Subscription subscription = getActiveSubscriptionBySubscriptionId(subscriptionId, currentTimeMS, transactionFactory);

            if (subscription == null) {
                throw new OperationFailedException("Active subscription not found with ID: " + subscriptionId, ResultCode.NOT_FOUND);
            }

            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "subscription(" + subscriptionId + ") updated successfully");
            }

            if (subscription.getSubscriberIdentity().equals(subscriberIdentity) == false) {
                getLogger().error(MODULE, "Skip updating subscription. Reason: SubscriberId(" + subscriberIdentity + ") " +
                        "and susbcriptionId(" + subscription.getId() + ") are not related");
                return null;
            }

            return updateSubscription(subscription, newSubscriptionState, startTime, endTime, priority, rejectReason, currentTimeMS, transactionFactory);

        } catch (OperationFailedException e) {
            throw new OperationFailedException("Unable to update subscription for subscription ID: " + subscriptionId
                    + ". Reason: " + e.getMessage(), e.getErrorCode(), e);
        }
    }

    private Subscription updateSubscription(Subscription subscription, SubscriptionState newSubscriptionState, Long startTime, Long endTime, Integer newPririty,
                                            String rejectReason, long currentTimeMS, TransactionFactory transactionFactory) throws OperationFailedException {

        Subscription quotaTopUpSubscription = null;
        if (subscription.getStatus() == SubscriptionState.STARTED) {
            if (newSubscriptionState == SubscriptionState.STARTED) {

				/*
				 *  These checks are added for scenario when either start time or end time change required, so previous value should be maintained.
				 */
                if (startTime == null) {
                    startTime = subscription.getStartTime().getTime();
                } else if (Numbers.POSITIVE_LONG.apply(startTime) == false) {
                    throw new OperationFailedException("Start time(" + startTime + ") should not be negative", ResultCode.INVALID_INPUT_PARAMETER);
                } else {
                    if(isFutureSubscription(subscription, currentTimeMS) == false && startTime > currentTimeMS) {
                        throw new OperationFailedException("Start time(" + new Timestamp(startTime).toString() + ") is greater or equal to current time for already activated addOn", ResultCode.INVALID_INPUT_PARAMETER);
                    }
                }

                if (endTime == null) {
                    endTime = subscription.getEndTime().getTime();
                } else if (Numbers.POSITIVE_LONG.apply(endTime) == false) {
                    throw new OperationFailedException("End time(" + endTime + ") should not be negative", ResultCode.INVALID_INPUT_PARAMETER);
                }

                validateStartTimeAndEndTime(startTime, endTime, currentTimeMS);
                int priority =  Objects.isNull(newPririty) ? subscription.getPriority() : newPririty;

                if(priority < 1) {
                    throw new OperationFailedException("Invalid priority value:"+ priority +", priority shoudld be greater than 1", ResultCode.INVALID_INPUT_PARAMETER);
                }


                if (changeSubscriptionDetails(subscription.getId(), startTime, endTime, priority, transactionFactory) == 0) {
                    if (getLogger().isWarnLogLevel()) {
                        getLogger().warn(MODULE, "No rows updated for susbcription. Please provide valid susbcriptionId(" + subscription.getId() + ")");
                    }
                    return null;


                }



                quotaTopUpSubscription = new Subscription(subscription.getId(), subscription.getSubscriberIdentity(), subscription.getPackageId(), subscription.getProductOfferId()
                        , new Timestamp(startTime), new Timestamp(endTime), newSubscriptionState, priority, SubscriptionType.TOP_UP, subscription.getParameter1(), subscription.getParameter2());
            }else if (newSubscriptionState == SubscriptionState.UNSUBSCRIBED) {
                if (changeStatus(subscription.getId(), newSubscriptionState.state, transactionFactory, rejectReason) == 0) {
                    if (getLogger().isWarnLogLevel()) {
                        getLogger().warn(MODULE, "No rows updated for susbcription. Please provide valid susbcriptionId(" + subscription.getId() + ")");
                    }
                    return null;
                }
                Timestamp startTime2 = startTime == null ? null :  new Timestamp(startTime);
                Timestamp endTime2 = endTime == null ? null : new Timestamp(endTime);
                quotaTopUpSubscription = new Subscription(subscription.getId(), subscription.getSubscriberIdentity(), subscription.getPackageId(), subscription.getProductOfferId()
                        , startTime2, endTime2, newSubscriptionState, subscription.getPriority(), SubscriptionType.TOP_UP, subscription.getParameter1(), subscription.getParameter2());


            } else {
                throw new OperationFailedException("Invalid subscription status(" + newSubscriptionState + ") received. Old Status: "
                        + subscription.getStatus(), ResultCode.INVALID_INPUT_PARAMETER);
            }
        } else {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Invalid subscription status(" + newSubscriptionState + ") received. Old Status: "
                        + subscription.getStatus());
            }

            throw new OperationFailedException("Invalid subscription status(" + newSubscriptionState + ") received. Old Status: " + subscription.getStatus(), ResultCode.INVALID_INPUT_PARAMETER);
        }

        return quotaTopUpSubscription;
    }

    private boolean isFutureSubscription(Subscription subscription, long currentTimeMS) {
        return subscription.getStartTime().getTime() > currentTimeMS;
    }

    private int changeSubscriptionDetails(String subscriptionId, Long startTime, Long endTime, int priority, TransactionFactory transactionFactory) throws OperationFailedException {

        Transaction transaction = transactionFactory.createTransaction();

        if (transaction == null) {
            throw new OperationFailedException("Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Update subscription detail query: " + queryForUpdateSubscriptionDetailBySubscriptionId);
        }

        PreparedStatement psForUpdateStatus = null;
        try {
            transaction.begin();
            psForUpdateStatus = transaction.prepareStatement(queryForUpdateSubscriptionDetailBySubscriptionId);
            psForUpdateStatus.setQueryTimeout(CommonConstants.QUERY_TIMEOUT_DEFAULT);
            psForUpdateStatus.setTimestamp(1, new Timestamp(startTime));
            psForUpdateStatus.setTimestamp(2, new Timestamp(endTime));
            psForUpdateStatus.setInt(3, priority);
            psForUpdateStatus.setString(4, subscriptionId);

            Stopwatch watch = new Stopwatch();
            watch.start();
            int rowsUpdated = psForUpdateStatus.executeUpdate();
            watch.stop();

            long queryExecutionTime = watch.elapsedTime(TimeUnit.MILLISECONDS);
            if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

                alertListener.generateSystemAlert(SEVERITY, Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
                        "DB Query execution time is high while updating subscription start or endtime. "
                                + "Last Query execution time: " + queryExecutionTime + " ms");

                if (getLogger().isWarnLogLevel()) {
                    getLogger().warn(MODULE, "DB Query execution time is high while updating subscription start or endtime. "
                            + "Last Query execution time: " + queryExecutionTime + " ms");
                }
            }
            return rowsUpdated;
        } catch (SQLException e) {

            ResultCode resultCode = ResultCode.INTERNAL_ERROR;

            if (isQueryTimeoutException(e)) {
                if (isTotalQueryTimeOutCountExceed()) {
                    if (getLogger().isWarnLogLevel()) {
                        getLogger().warn(MODULE, "Total number of query timeouts(" + totalQueryTimeoutCount.intValue()
                                + ") exceeded then configured max number "
                                + "of query timeouts(" + CommonConstants.MAX_QUERY_TIMEOUT_COUNT_DEFAULT + "), so System marked as DEAD");
                    }
                    transaction.markDead();
                }
                alertListener.generateSystemAlert(SEVERITY, Alerts.QUERY_TIME_OUT, MODULE,
                        "DB query timeout while updating subscription from DB with data source name: "
                                + transaction.getDataSourceName());
                resultCode = ResultCode.SERVICE_UNAVAILABLE;
            } else if (transaction.isDBDownSQLException(e)) {
                if (getLogger().isWarnLogLevel()) {
                    getLogger().warn(MODULE, "Database with data source name: " + transaction.getDataSourceName()
                            + " is Down, System marked as dead. Reason: " + e.getMessage());
                }
                transaction.markDead();
                resultCode = ResultCode.SERVICE_UNAVAILABLE;
            }

            transaction.rollback();

            throw new OperationFailedException(toStringSQLException(e), resultCode, e);
        } catch (TransactionException e) {

            ResultCode sprErrorCode = ResultCode.INTERNAL_ERROR;

            if (isConnectionNotFoundException(e)) {

                alertListener.generateSystemAlert(SEVERITY, Alerts.DATABASE_CONNECTION_NOT_AVAILABLE, MODULE,
                        "Unable to update subscription for subscription ID: " + subscriptionId + " from DB with data source name: "
                                + transaction.getDataSourceName() + ". Reason: Connection not available");
                sprErrorCode = ResultCode.SERVICE_UNAVAILABLE;
            }

            transaction.rollback();

            throw new OperationFailedException(e.getMessage(), sprErrorCode, e);

        } finally {
            closeQuietly(psForUpdateStatus);
            endTransaction(transaction);
        }
    }

    private int changeStatus(String subscriptionId, int newState, TransactionFactory transactionFactory, String rejectReason) throws OperationFailedException {

        Transaction transaction = transactionFactory.createTransaction();

        if (transaction == null) {
            throw new OperationFailedException("Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }


        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Update subscription status query: " + queryForUpdateSubscriptionStatusBySubscriptionId);
        }

        PreparedStatement psForUpdateStatus = null;
        try {
            transaction.begin();
            psForUpdateStatus = transaction.prepareStatement(queryForUpdateSubscriptionStatusBySubscriptionId);
            psForUpdateStatus.setQueryTimeout(CommonConstants.QUERY_TIMEOUT_DEFAULT);
            psForUpdateStatus.setInt(1, newState);
            psForUpdateStatus.setString(2, rejectReason);
            psForUpdateStatus.setString(3, subscriptionId);

            Stopwatch watch = new Stopwatch();
            watch.start();
            int rowsUpdated = psForUpdateStatus.executeUpdate();
            watch.stop();

            long queryExecutionTime = watch.elapsedTime(TimeUnit.MILLISECONDS);
            if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

                alertListener.generateSystemAlert(SEVERITY, Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
                        "DB Query execution time is high while updating subscription. "
                                + "Last Query execution time: " + queryExecutionTime + " ms");

                if (getLogger().isWarnLogLevel()) {
                    getLogger().warn(MODULE, "DB Query execution time is high while updating subscription. "
                            + "Last Query execution time: " + queryExecutionTime + " ms");
                }
            }
            return rowsUpdated;
        } catch (SQLException e) {

            ResultCode resultCode = ResultCode.INTERNAL_ERROR;

            if (isQueryTimeoutException(e)) {
                if (isTotalQueryTimeOutCountExceed()) {
                    if (getLogger().isWarnLogLevel()) {
                        getLogger().warn(MODULE, "Total number of query timeouts(" + totalQueryTimeoutCount.intValue()
                                + ") exceeded then configured max number "
                                + "of query timeouts(" + CommonConstants.MAX_QUERY_TIMEOUT_COUNT_DEFAULT + "), so System marked as DEAD");
                    }
                    transaction.markDead();
                }
                alertListener.generateSystemAlert(SEVERITY, Alerts.QUERY_TIME_OUT, MODULE,
                        "DB query timeout while updating subscription from DB with data source name: "
                                + transaction.getDataSourceName());

                resultCode = ResultCode.SERVICE_UNAVAILABLE;

            } else if (transaction.isDBDownSQLException(e)) {
                if (getLogger().isWarnLogLevel()) {
                    getLogger().warn(MODULE, "Database with data source name: " + transaction.getDataSourceName()
                            + " is Down, System marked as dead. Reason: " + e.getMessage());
                }
                transaction.markDead();

                resultCode = ResultCode.SERVICE_UNAVAILABLE;
            }
            transaction.rollback();
            throw new OperationFailedException(toStringSQLException(e), resultCode, e);
        } catch (TransactionException e) {

            ResultCode sprErrorCode = ResultCode.INTERNAL_ERROR;

            if (isConnectionNotFoundException(e)) {

                alertListener.generateSystemAlert(SEVERITY, Alerts.DATABASE_CONNECTION_NOT_AVAILABLE, MODULE,
                        "Unable to update subscription for subscription ID: " + subscriptionId + " from DB with data source name: "
                                + transaction.getDataSourceName() + ". Reason: Connection not available");
                sprErrorCode = ResultCode.SERVICE_UNAVAILABLE;
            }

            transaction.rollback();

            throw new OperationFailedException(e.getMessage(), sprErrorCode, e);

        } finally {
            closeQuietly(psForUpdateStatus);
            endTransaction(transaction);
        }
    }

    /*
     * fetch subscription by subscription id.
     *
     * Skips subscription for below scenario: > addOn not exist in repository >
     * expired subscription > subscription in UNSUBSCRIBED state
     */
    @VisibleForTesting
    Subscription getActiveSubscriptionBySubscriptionId(String subscriptionId, long currentTimeMS, TransactionFactory transactionFactory)
            throws OperationFailedException {

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "fetching subscription for subscription ID: " + subscriptionId);
        }

        DBTransaction dbTransaction = transactionFactory.createReadOnlyTransaction();
        if (dbTransaction == null) {
            throw new OperationFailedException("Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        PreparedStatement psForFetchSubscription = null;
        ResultSet rsForFetchSubscription = null;
        try {
            dbTransaction.begin();
            psForFetchSubscription = dbTransaction.prepareStatement(queryForSelectSubscriptionBySubscriptionId);
            psForFetchSubscription.setQueryTimeout(CommonConstants.QUERY_TIMEOUT_DEFAULT);
            psForFetchSubscription.setString(1, subscriptionId);
            Stopwatch watch = new Stopwatch();
            watch.start();
            rsForFetchSubscription = psForFetchSubscription.executeQuery();
            watch.stop();

            long queryExecutionTime = watch.elapsedTime(TimeUnit.MILLISECONDS);
            if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {
                alertListener.generateSystemAlert(SEVERITY, Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
                        "DB Query execution time is high while updating subscription. "
                                + "Last Query execution time: " + queryExecutionTime + " ms");

                if (getLogger().isWarnLogLevel()) {
                    getLogger().warn(MODULE, "DB Query execution time is high while updating subscription. "
                            + "Last Query execution time: " + queryExecutionTime + " ms");
                }
            }

            resetTotalQueryTimeoutCount();

            if (rsForFetchSubscription.next()) {
                String subscriberID = rsForFetchSubscription.getString("SUBSCRIBER_ID");
                String quotaTopUpID = rsForFetchSubscription.getString("PACKAGE_ID");
                String productOfferId = rsForFetchSubscription.getString("PRODUCT_OFFER_ID");
                Timestamp startTime = rsForFetchSubscription.getTimestamp("START_TIME");
                Timestamp endTime = rsForFetchSubscription.getTimestamp("END_TIME");
                int statusValue = rsForFetchSubscription.getInt("STATUS");

                SubscriptionState subscriptionState = SubscriptionState.fromValue(statusValue);
                int priority  = rsForFetchSubscription.getInt("PRIORITY");
                String param1 = rsForFetchSubscription.getString("PARAM1");
                String param2 = rsForFetchSubscription.getString("PARAM2");

                QuotaTopUp subscriptionPackage = policyRepository.getActiveQuotaTopUpById(quotaTopUpID);
                if (subscriptionPackage == null) {
                    if (getLogger().isDebugLogLevel()) {
                        getLogger().debug(MODULE, "Skip quotaTopUpSubscription: " + subscriberID + " for subscriber: " + subscriberID
                                + ". Reason: Subscription expired");
                    }
                    return null;
                }

                if (endTime != null && endTime.getTime() <= currentTimeMS) {
                    if (getLogger().isDebugLogLevel()) {
                        getLogger().debug(MODULE, "Skip quotaTopUpSubscription: " + subscriberID + " for subscriber: " + subscriberID
                                + ". Reason: Subscription expired");
                    }
                    return null;
                }

                if (subscriptionState == SubscriptionState.UNSUBSCRIBED) {
                    if (getLogger().isDebugLogLevel()) {
                        getLogger().debug(MODULE, "Skip quotaTopUpSubscription: " + subscriberID + " for subscriber: " + subscriberID
                                + ". Reason: quotaTopUpSubscription is unsubscribed");
                    }
                    return null;
                }

                Subscription quotaTopUpSubscription = new Subscription(subscriptionId, subscriberID, subscriptionPackage.getId(), productOfferId, startTime, endTime, subscriptionState, priority, SubscriptionType.TOP_UP, param1, param2);

                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "Subscription found: " + quotaTopUpSubscription.getId());
                }

                return quotaTopUpSubscription;
            }

        } catch (SQLException e) {

            ResultCode resultCode = ResultCode.INTERNAL_ERROR;
            if (isQueryTimeoutException(e)) {
                if (isTotalQueryTimeOutCountExceed()) {
                    if (getLogger().isWarnLogLevel()) {
                        getLogger().warn(MODULE, "total number of query timeouts(" + totalQueryTimeoutCount.intValue()
                                + ") exceeded then configured max number "
                                + "of query timeouts(" + CommonConstants.MAX_QUERY_TIMEOUT_COUNT_DEFAULT + "), so System marked as DEAD");
                    }
                    dbTransaction.markDead();
                }
                alertListener.generateSystemAlert(SEVERITY, Alerts.QUERY_TIME_OUT, MODULE,
                        "DB query timeout while updating subscription from DB with data source name: "
                                + dbTransaction.getDataSourceName());

                resultCode = ResultCode.SERVICE_UNAVAILABLE;
            } else if (dbTransaction.isDBDownSQLException(e)) {
                if (getLogger().isWarnLogLevel()) {
                    getLogger().warn(MODULE, "Database with data source name: " + dbTransaction.getDataSourceName()
                            + " is Down, System marked as dead. Reason: " + e.getMessage());
                }
                dbTransaction.markDead();

                resultCode = ResultCode.SERVICE_UNAVAILABLE;
            }

            throw new OperationFailedException(toStringSQLException(e), resultCode, e);
        } catch (TransactionException e) {

            ResultCode sprErrorCode = ResultCode.INTERNAL_ERROR;

            if (isConnectionNotFoundException(e)) {

                alertListener.generateSystemAlert(SEVERITY, Alerts.DATABASE_CONNECTION_NOT_AVAILABLE, MODULE,
                        "Unable to update subscription for subscription ID: " + subscriptionId + " from DB with data source name: "
                                + dbTransaction.getDataSourceName() + ". Reason: Connection not available");
                sprErrorCode = ResultCode.SERVICE_UNAVAILABLE;
            }

            throw new OperationFailedException(e.getMessage(), sprErrorCode, e);

        } finally {
            closeQuietly(rsForFetchSubscription);
            closeQuietly(psForFetchSubscription);
            endTransaction(dbTransaction);
        }

        return null;
    }

    public int unsubscribeBySubscriberId(String subscriberIdentity, Transaction transaction) throws OperationFailedException, TransactionException {

        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Unsubscribing all subscription for subscriber(" + subscriberIdentity + ")");
        }


        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Unsubscribe by subscriber identity query: " + queryForUnsubscribeBySubscriberId);
        }

        PreparedStatement psForUnsubscribeBySubscriberId = null;
        try {

            psForUnsubscribeBySubscriberId = transaction.prepareStatement(queryForUnsubscribeBySubscriberId);
            psForUnsubscribeBySubscriberId.setQueryTimeout(CommonConstants.QUERY_TIMEOUT_DEFAULT);
            psForUnsubscribeBySubscriberId.setString(1, subscriberIdentity);

            Stopwatch watch = new Stopwatch();
            watch.start();
            int updateCount = psForUnsubscribeBySubscriberId.executeUpdate();
            watch.stop();

            long queryExecutionTime = watch.elapsedTime(TimeUnit.MILLISECONDS);
            if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {
                alertListener.generateSystemAlert(SEVERITY, Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
                        "DB Query execution time is high while unsubscribing subscription. "
                                + "Last Query execution time: " + queryExecutionTime + " ms");

                if (getLogger().isWarnLogLevel()) {
                    getLogger().warn(MODULE, "DB Query execution time is high while unsubscribing subscription. "
                            + "Last Query execution time: " + queryExecutionTime + " ms");
                }
            }

            resetTotalQueryTimeoutCount();

            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Total(" + updateCount + ") subscriptions are unsubscribed for subscriber(" + subscriberIdentity + ") successfully");
            }

            return updateCount;

        } catch (SQLException e) {

            ResultCode resultCode = ResultCode.INTERNAL_ERROR;
            if (isQueryTimeoutException(e)) {
                if (isTotalQueryTimeOutCountExceed()) {
                    if (getLogger().isWarnLogLevel()) {
                        getLogger().warn(MODULE, "total number of query timeouts(" + totalQueryTimeoutCount.intValue()
                                + ") exceeded then configured max number "
                                + "of query timeouts(" + CommonConstants.MAX_QUERY_TIMEOUT_COUNT_DEFAULT + "), so System marked as DEAD");
                    }
                    transaction.markDead();
                }
                alertListener.generateSystemAlert(SEVERITY, Alerts.QUERY_TIME_OUT, MODULE,
                        "DB query timeout while unsubscribing subscription from DB with data source name: "
                                + transaction.getDataSourceName());

                resultCode = ResultCode.SERVICE_UNAVAILABLE;
            } else if (transaction.isDBDownSQLException(e)) {
                if (getLogger().isWarnLogLevel()) {
                    getLogger().warn(MODULE, "Database with data source name: " + transaction.getDataSourceName()
                            + " is Down, System marked as dead. Reason: " + e.getMessage());
                }
                transaction.markDead();
                resultCode = ResultCode.SERVICE_UNAVAILABLE;
            }

            throw new OperationFailedException(toStringSQLException(e), resultCode, e);

        } finally {
            closeQuietly(psForUnsubscribeBySubscriberId);
        }
    }

    private Subscription addSubscriptionDetails(QuotaTopUp subscriptionPackage,
                                                String subscriberIdentity,
                                                String parentId,
                                                Integer subscriptionStatusValue,
                                                long startTime,
                                                long endTime,
                                                Integer priority,
                                                Transaction transaction,
                                                String param1,
                                                String param2,
                                                String subscriptionId)
            throws OperationFailedException, TransactionException, SQLException {


        SubscriptionState subscriptionState = getSubscriptionStateFromValue(subscriberIdentity, subscriptionStatusValue);

        Timestamp endTimeMSTS = new Timestamp(endTime);
        Timestamp startTimeMSTS = new Timestamp(startTime);

        if(Objects.isNull(priority)) {
            priority = 100;
        }

        if (getLogger().isLogLevel(LogLevel.DEBUG)) {
            getLogger().debug(MODULE, "Subscribe package query: " + queryForSubscribeQuotaTopUp);
        }

        PreparedStatement psForSubscriberQuotatopUp = null;

        try {
            psForSubscriberQuotatopUp = transaction.prepareStatement(queryForSubscribeQuotaTopUp);
            psForSubscriberQuotatopUp.setQueryTimeout(CommonConstants.QUERY_TIMEOUT_DEFAULT);


            String quotaTopUpSubscriptionId = Strings.isNullOrBlank(subscriptionId) ? getNextSubscriptionId() : subscriptionId.trim();

            psForSubscriberQuotatopUp.setString(1, quotaTopUpSubscriptionId);
            psForSubscriberQuotatopUp.setString(2, subscriptionPackage.getId());
            psForSubscriberQuotatopUp.setString(3, null);
            psForSubscriberQuotatopUp.setString(4, parentId);
            psForSubscriberQuotatopUp.setString(5, subscriberIdentity);
            psForSubscriberQuotatopUp.setString(6, CommonConstants.DEFAULT_SERVER_INSTANCE_VALUE);
            psForSubscriberQuotatopUp.setTimestamp(7, startTimeMSTS);
            psForSubscriberQuotatopUp.setTimestamp(8, endTimeMSTS);
            psForSubscriberQuotatopUp.setString(9, subscriptionState.getStringVal());
            psForSubscriberQuotatopUp.setInt(10, priority);

            psForSubscriberQuotatopUp.setString(11, param1);
            psForSubscriberQuotatopUp.setString(12, param2);
            psForSubscriberQuotatopUp.setString(13, SubscriptionType.TOP_UP.name());

            Stopwatch watch = new Stopwatch();
            watch.start();
            psForSubscriberQuotatopUp.execute();
            watch.stop();

            long queryExecutionTime = watch.elapsedTime(TimeUnit.MILLISECONDS);
            if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

                alertListener.generateSystemAlert(SEVERITY, Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
                        "DB Query execution time is high while subscribing package. "
                                + "Last Query execution time: " + queryExecutionTime + " ms");

                if (getLogger().isWarnLogLevel()) {
                    getLogger().warn(MODULE, "DB Query execution time is high while subscribing package. "
                            + "Last Query execution time: " + queryExecutionTime + " ms");
                }
            }

            resetTotalQueryTimeoutCount();

            return new Subscription(quotaTopUpSubscriptionId, subscriberIdentity, subscriptionPackage.getId(), null, startTimeMSTS, endTimeMSTS,
                    subscriptionState, priority, SubscriptionType.TOP_UP, param1, param2);

        } finally {
            closeQuietly(psForSubscriberQuotatopUp);
        }
    }
}


