package com.elitecore.corenetvertex.spr.voltdb;

import com.elitecore.commons.base.Maps;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.base.TimeSource;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.commons.threads.EliteThreadFactory;
import com.elitecore.corenetvertex.constants.AggregationKey;
import com.elitecore.corenetvertex.constants.BillingCycleResetStatus;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.core.alerts.AlertConstants;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.alerts.Alerts;
import com.elitecore.corenetvertex.core.db.VoltDBClient;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.pkg.datapackage.PromotionalPackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.SubscriptionPackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.AllowedUsage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.UMBaseQuotaProfileDetail;
import com.elitecore.corenetvertex.spr.CSVFailOverOperation;
import com.elitecore.corenetvertex.spr.RecordProcessor;
import com.elitecore.corenetvertex.spr.SubscriberUsage;
import com.elitecore.corenetvertex.spr.balance.QuotaProfileBalance;
import com.elitecore.corenetvertex.spr.balance.SubscriptionInformation;
import com.elitecore.corenetvertex.spr.balance.Usage;
import com.elitecore.corenetvertex.spr.balance.UsageInfo;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.spr.voltdb.util.VoltDBUtil;
import org.voltdb.VoltTable;
import org.voltdb.client.ClientResponse;
import org.voltdb.client.ProcCallException;
import org.voltdb.client.ProcedureCallback;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.commons.logging.LogManager.ignoreTrace;

public class VoltUMOperation {

    private static final String MODULE = "VOLT-UM-OPR";
    private static final String USAGE_GET_STORED_PROCEDURE = "UsageGetStoredProcedure";
    private static final String USAGE_INSERT_STORED_PROCEDURE = "UsageInsertStoredProcedure";
    private static final String USAGE_ADD_TO_EXISTING_STORED_PROCEDURE = "UsageAddToExistingStoredProcedure";
    private static final String USAGE_REPLACE_STORED_PROCEDURE = "UsageReplaceStoredProcedure";
    private static final String USAGE_RESET_STORED_PROCEDURE = "UsageResetStoredProcedure";
    private static final String USAGE_DELETE_STORED_PROCEDURE = "UsageDeleteStoredProcedure";
    private static final String USAGE_DELETE_BY_PACKAGE_ID_STORED_PROCEDURE = "UsageDeleteByPackageIdStoredProcedure";
    private static final String USAGE_RESET_BILLING_CYCLE_STORED_PROCEDURE = "UsageScheduleResetStoredProcedure";
    private static final long UNLIMITED_USAGE = -1;

    private final AlertListener alertListener;
    private final PolicyRepository policyRepository;
    private AtomicInteger totalQueryTimeoutCount;
    private final Object lockDailyResetTime;
    private final Object lockWeeklyResetTime;
    private volatile long nextWeeklyResetTime;
    private volatile long nextDailyResetTime;
    private TimeSource timeSource;
    private ShutdownHook shutdownHook;
    private RecordProcessor<VoltUMOperationData> recordProcessor;
    private ScheduledExecutorService executor;


    private CSVFailOverOperation<VoltUMOperationData> csvfailOverOperation;

    public VoltUMOperation(AlertListener alertListener, PolicyRepository policyRepository, @Nonnull TimeSource timeSource,
                           RecordProcessor<VoltUMOperationData> recordProcessor,
                           ScheduledThreadPoolExecutor executor) {
        this.alertListener = alertListener;
        this.policyRepository = policyRepository;
        this.totalQueryTimeoutCount = new AtomicInteger(0);
        long currentTime = getCurrentTime();
        this.nextDailyResetTime = calculateDailyResetTime(currentTime);
        this.nextWeeklyResetTime = calculateWeeklyResetTime(currentTime);
        this.lockDailyResetTime = new Object();
        this.lockWeeklyResetTime = new Object();
        this.timeSource = timeSource;
        this.recordProcessor = recordProcessor;
        this.executor = executor;
        this.csvfailOverOperation = new CSVFailOverOperation<>(this.recordProcessor, this.executor);
    }

    public static VoltUMOperation create(AlertListener alertListener, PolicyRepository policyRepository, @Nonnull TimeSource timeSource,
                                         RecordProcessor<VoltUMOperationData> recordProcessor) {

        ScheduledThreadPoolExecutor failOverExecutor = new ScheduledThreadPoolExecutor(1, new EliteThreadFactory("VOLT-UM-FAILOVER-CSV",
                "VOLT-UM-FAILOVER-CSV", Thread.NORM_PRIORITY));

        return new VoltUMOperation(alertListener, policyRepository, timeSource,
                recordProcessor,
                failOverExecutor);
    }

    public void init() {

        try {
            shutdownHook = new ShutdownHook(executor, recordProcessor);
            Runtime.getRuntime().addShutdownHook(shutdownHook);
        } catch (Exception ex) {
            getLogger().error(MODULE, "Error in adding shutdown hook. Reason: " + ex.getMessage());
            getLogger().trace(ex);
        }
    }

    private long calculateDailyResetTime(long currentTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTime);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }


    private long calculateWeeklyResetTime(long currentTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTime);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);

        return calendar.getTimeInMillis();
    }

    public Map<String, Map<String, SubscriberUsage>> getUsage(String subscriberIdentity, Map<String, Subscription> addOnSubscriptions,
                                                              VoltDBClient voltDBClient) throws OperationFailedException {
        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Fetching usage metering information for subscriber ID: " + subscriberIdentity);
        }

        if (voltDBClient.isAlive() == false) {
            throw new OperationFailedException("Unable to fetch usage for subscriber ID: " + subscriberIdentity
                    + " Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        Map<String, Map<String, SubscriberUsage>> subscriptionToSubscriberUsage = new HashMap<>();

        try {
            long currentTime = timeSource.currentTimeInMillis();
            ClientResponse clientResponse = voltDBClient.callProcedure(USAGE_GET_STORED_PROCEDURE, subscriberIdentity);
            long queryExecutionTime = timeSource.currentTimeInMillis() - currentTime;
            if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

                alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
                        "DB Query execution time is high while usage information. " +
                                "Last Query execution time: " + queryExecutionTime + " ms");

                if (getLogger().isWarnLogLevel()) {
                    getLogger().warn(MODULE, "DB select query execution time getting high, Last Query execution time: " + queryExecutionTime +
                            " ms.");
                }
            }

            VoltTable vt = clientResponse.getResults()[0];

            createUsage(subscriberIdentity, addOnSubscriptions, subscriptionToSubscriberUsage, vt);

            if (subscriptionToSubscriberUsage.isEmpty() == false) {
                if (getLogger().isInfoLogLevel()) {
                    getLogger().info(MODULE, "Usage found with subscriber ID: " + subscriberIdentity);
                }
                return subscriptionToSubscriberUsage;
            } else {
                if (getLogger().isInfoLogLevel()) {
                    getLogger().info(MODULE, "Usage not found with subscriber ID: " + subscriberIdentity);
                }
            }

            totalQueryTimeoutCount.set(0);
        } catch (IOException e) {
            throw new OperationFailedException("Error while fetching usage for Subscriber ID: " +
                    subscriberIdentity + ". Reason: " + e.getMessage(), e);
        } catch (ProcCallException e){
            VoltDBUtil.handleProcedureCallException(e, "fetching Profile for subscriber Id: " + subscriberIdentity, MODULE, alertListener, totalQueryTimeoutCount, voltDBClient);
        }

        return Maps.newHashMap();
    }

    private void createUsage(String subscriberIdentity, Map<String, Subscription> addOnSubscriptions, Map<String, Map<String, SubscriberUsage>> subscriptionToSubscriberUsage, VoltTable vt) {
        while (vt.advanceRow()) {

            String id = vt.getString("ID");
            String subscriptionId = vt.getString("SUBSCRIPTION_ID");
            String quotaProfileId = vt.getString("QUOTA_PROFILE_ID");
            String serviceId = vt.getString("SERVICE_ID");
            String packageId = vt.getString("PACKAGE_ID");
            String productOfferId = vt.getString("PRODUCT_OFFER_ID");

            boolean isAddOnUsage = isAddOnSubscriptionUsage(subscriptionId);
            if (isAddOnUsage) {
                Subscription addOnSubscription = addOnSubscriptions.get(subscriptionId);
                if (addOnSubscription == null) {
                    if (getLogger().isInfoLogLevel()) {
                        getLogger().info(MODULE, "Skip to fetch usage for subscription id:" + subscriptionId +
                                ". Reason: subscription is not active");
                    }
                    continue;
                }
            }

            Timestamp dailyResetTime = vt.getTimestampAsSqlTimestamp("DAILY_RESET_TIME");
            long dailyTotalUsage = vt.getLong("DAILY_TOTAL");
            long dailyUploadUsage = vt.getLong("DAILY_UPLOAD");
            long dailyDownloadUsage = vt.getLong("DAILY_DOWNLOAD");
            long dailyTime = vt.getLong("DAILY_TIME");

            Timestamp weeklyResetTime = vt.getTimestampAsSqlTimestamp("WEEKLY_RESET_TIME");
            long weeklyTotalUsage = vt.getLong("WEEKLY_TOTAL");
            long weeklyUploadUsage = vt.getLong("WEEKLY_UPLOAD");
            long weeklyDownloadUsage = vt.getLong("WEEKLY_DOWNLOAD");
            long weeklyTime = vt.getLong("WEEKLY_TIME");

            Timestamp billingCycleResetTime = vt.getTimestampAsSqlTimestamp("BILLING_CYCLE_RESET_TIME");
            long billingCycleTotalUsage = vt.getLong("BILLING_CYCLE_TOTAL");
            long billingCycleUploadUsage = vt.getLong("BILLING_CYCLE_UPLOAD");
            long billingCycleDownloadUsage = vt.getLong("BILLING_CYCLE_DOWNLOAD");
            long billingCycleTime = vt.getLong("BILLING_CYCLE_TIME");

            Timestamp customResetTime = vt.getTimestampAsSqlTimestamp("CUSTOM_RESET_TIME");
            long customTotalUsage = vt.getLong("CUSTOM_TOTAL");
            long customUploadUsage = vt.getLong("CUSTOM_UPLOAD");
            long customDownloadUsage = vt.getLong("CUSTOM_DOWNLOAD");
            long customTime = vt.getLong("CUSTOM_TIME");

            SubscriberUsage subscriberUsage = new SubscriberUsage(id,
                    quotaProfileId,
                    subscriberIdentity,
                    serviceId,
                    subscriptionId,
                    packageId, productOfferId, billingCycleTotalUsage,
                    billingCycleDownloadUsage,
                    billingCycleUploadUsage,
                    billingCycleTime,
                    dailyTotalUsage,
                    dailyDownloadUsage,
                    dailyUploadUsage,
                    dailyTime,
                    weeklyTotalUsage,
                    weeklyDownloadUsage,
                    weeklyUploadUsage,
                    weeklyTime,
                    customTotalUsage,
                    customDownloadUsage,
                    customUploadUsage,
                    customTime,
                    customResetTime.getTime(),
                    dailyResetTime.getTime(),
                    weeklyResetTime.getTime(),
                    billingCycleResetTime.getTime());

            Map<String, SubscriberUsage> quotaProfileAndServiceIdToUsage;
            if (isAddOnUsage) {
                quotaProfileAndServiceIdToUsage = subscriptionToSubscriberUsage.get(subscriptionId);
            } else {
                quotaProfileAndServiceIdToUsage = subscriptionToSubscriberUsage.get(packageId);
            }

            if (quotaProfileAndServiceIdToUsage == null) {
                quotaProfileAndServiceIdToUsage = new HashMap<>();
                if (isAddOnUsage) {
                    subscriptionToSubscriberUsage.put(subscriptionId, quotaProfileAndServiceIdToUsage);
                } else {
                    subscriptionToSubscriberUsage.put(packageId, quotaProfileAndServiceIdToUsage);
                }

            }
            quotaProfileAndServiceIdToUsage.put(quotaProfileId + CommonConstants.USAGE_KEY_SEPARATOR + serviceId, subscriberUsage);
        }
    }

    public Map<String, Map<String, SubscriberUsage>> getUsage(SPRInfo sprInfo,
                                                              VoltDBClient voltDBClient) throws OperationFailedException {

        String subscriberIdentity = sprInfo.getSubscriberIdentity();
        Map<String, Subscription> activeSubscriptions = sprInfo.getActiveSubscriptions(System.currentTimeMillis());

        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Fetching usage metering information for subscriber ID: " + subscriberIdentity);
        }

        if (voltDBClient.isAlive() == false) {
            throw new OperationFailedException("Unable to fetch usage for subscriber ID: " + subscriberIdentity
                    + " Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        Map<String, Map<String, SubscriberUsage>> subscriptionToSubscriberUsage = new HashMap<>();

        try {
            long currentTime = timeSource.currentTimeInMillis();
            ClientResponse clientResponse = voltDBClient.callProcedure(USAGE_GET_STORED_PROCEDURE, subscriberIdentity);
            long queryExecutionTime = timeSource.currentTimeInMillis() - currentTime;
            sprInfo.setUsageLoadTime(queryExecutionTime);
            if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

                alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
                        "DB Query execution time is high while usage information. " +
                                "Last Query execution time: " + queryExecutionTime + " ms");

                if (getLogger().isWarnLogLevel()) {
                    getLogger().warn(MODULE, "DB select query execution time getting high, Last Query execution time: " + queryExecutionTime +
                            " ms.");
                }
            }

            VoltTable vt = clientResponse.getResults()[0];
            long queryReadStartTime = getCurrentTime();
            createUsage(subscriberIdentity, activeSubscriptions, subscriptionToSubscriberUsage, vt);
            long queryReadTime = getCurrentTime() - queryReadStartTime;
            sprInfo.setUsageReadTime(queryReadTime);

            if (subscriptionToSubscriberUsage.isEmpty() == false) {
                if (getLogger().isInfoLogLevel()) {
                    getLogger().info(MODULE, "Usage found with subscriber ID: " + subscriberIdentity);
                }
                return subscriptionToSubscriberUsage;
            }

            totalQueryTimeoutCount.set(0);

        } catch (IOException e) {
            throw new OperationFailedException("Error while fetching usage for Subscriber ID: " +
                    subscriberIdentity + ". Reason: " + e.getMessage(), e);
        } catch (ProcCallException e){
            VoltDBUtil.handleProcedureCallException(e, "fetching usage for subscriber Id: " + subscriberIdentity, MODULE, alertListener, totalQueryTimeoutCount, voltDBClient);
        }

        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Usage not exist with subscriber ID: " + subscriberIdentity);
        }

        return Maps.newHashMap();
    }

    private boolean isAddOnSubscriptionUsage(String subscriptionId) {
        return subscriptionId != null;
    }

    public void insert(String subscriberIdentity, Collection<SubscriberUsage> usages, VoltDBClient voltDBClient) throws OperationFailedException {

        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Inserting usage for subscriber ID: " + subscriberIdentity);
        }

        if (voltDBClient.isAlive() == false) {
            throw new OperationFailedException("Unable to perform insert usage operation for subscriber ID: " + subscriberIdentity
                    + ". Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        long currentTime = timeSource.currentTimeInMillis();
        try {
            for (SubscriberUsage usage : usages) {

                long queryExecutionTime = timeSource.currentTimeInMillis();
                String[] usageArray = createUsageArray(usage);

                long billingCycleResetTime = usage.getBillingCycleResetTime();
                if (billingCycleResetTime == 0) {
                    billingCycleResetTime = getFutureDate();
                }

                long customResetTime = usage.getCustomResetTime();
                if (customResetTime == 0) {
                    customResetTime = getFutureDate();
                }

                voltDBClient.callProcedure(new Callback(subscriberIdentity, VoltUMOperationData.INSERT, usages),
                        USAGE_INSERT_STORED_PROCEDURE, subscriberIdentity, new Timestamp(getResetTime(currentTime)),
                        new Timestamp(getWeeklyTime(currentTime)), new Timestamp(billingCycleResetTime),
                        new Timestamp(customResetTime), usageArray);

                queryExecutionTime = timeSource.currentTimeInMillis() - queryExecutionTime;

                if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

                    alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
                            "DB Query execution time is high while replacing usage information. " +
                                    "Last Query execution time: " + queryExecutionTime + " ms");

                    if (getLogger().isWarnLogLevel()) {
                        getLogger().warn(MODULE, "OPEN-DB update query execution time getting high, Last Query execution time = " +
                                queryExecutionTime + " milliseconds.");
                    }
                }
            }


            totalQueryTimeoutCount.set(0);
        } catch (IOException e) {
            throw new OperationFailedException("Error while inserting usage for subscriber ID: " +
                    subscriberIdentity + ". Reason: " + e.getMessage(), e);
        }
    }

    private String[] createUsageArray(SubscriberUsage su) {

        String[] usageArray = new String[23];

        usageArray[0] = UUID.randomUUID().toString();
        usageArray[1] = su.getSubscriberIdentity();
        usageArray[2] = su.getPackageId();
        usageArray[3] = su.getSubscriptionId();
        usageArray[4] = su.getQuotaProfileId();
        usageArray[5] = su.getServiceId();

        usageArray[6] = su.getDailyTotal() + "";
        usageArray[7] = su.getDailyUpload() + "";
        usageArray[8] = su.getDailyDownload() + "";
        usageArray[9] = su.getDailyTime() + "";

        usageArray[10] = su.getWeeklyTotal() + "";
        usageArray[11] = su.getWeeklyUpload() + "";
        usageArray[12] = su.getWeeklyDownload() + "";
        usageArray[13] = su.getWeeklyTime() + "";

        usageArray[14] = su.getBillingCycleTotal() + "";
        usageArray[15] = su.getBillingCycleUpload() + "";
        usageArray[16] = su.getBillingCycleDownload() + "";
        usageArray[17] = su.getBillingCycleTime() + "";

        usageArray[18] = su.getCustomTotal() + "";
        usageArray[19] = su.getCustomUpload() + "";
        usageArray[20] = su.getCustomDownload() + "";
        usageArray[21] = su.getCustomTime() + "";
        usageArray[22] = su.getProductOfferId() + "";

        return usageArray;
    }

    private long getCurrentTime() {
        return System.currentTimeMillis();
    }

    public long getWeeklyTime(long currentTime) {

        if (currentTime > nextWeeklyResetTime) {
            synchronized (lockWeeklyResetTime) {
                nextWeeklyResetTime = calculateWeeklyResetTime(currentTime);
            }
        }

        return nextWeeklyResetTime;
    }

    public long getResetTime(long currentTime) {

        if (currentTime > nextDailyResetTime) {
            synchronized (lockDailyResetTime) {
                nextDailyResetTime = calculateDailyResetTime(currentTime);
            }
        }
        return nextDailyResetTime;
    }

    public long getFutureDate() {
        return CommonConstants.FUTURE_DATE;
    }

    public void addToExisting(String subscriberIdentity, Collection<SubscriberUsage> usages, VoltDBClient voltDBClient) throws OperationFailedException {
        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Add to existing usage operation started for subscriber ID: " + subscriberIdentity);
        }

        if (voltDBClient.isAlive() == false) {
            throw new OperationFailedException("Unable to perform add usage operation for subscriber ID: " + subscriberIdentity
                    + " Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        try {
            Object[] usageArgs = createUsageArrayForAddToExisting(usages);
            long queryExecutionTime = timeSource.currentTimeInMillis();
            voltDBClient.callProcedure(new Callback(subscriberIdentity, VoltUMOperationData.ADD_TO_EXISTING, usages), USAGE_ADD_TO_EXISTING_STORED_PROCEDURE, subscriberIdentity, usageArgs[0],
                    usageArgs[1],
                    usageArgs[2],
                    usageArgs[3],
                    usageArgs[4],
                    usageArgs[5],
                    usageArgs[6],
                    usageArgs[7],
                    usageArgs[8],
                    usageArgs[9],
                    usageArgs[10],
                    usageArgs[11],
                    usageArgs[12],
                    usageArgs[13],
                    usageArgs[14]);
                queryExecutionTime = timeSource.currentTimeInMillis() - queryExecutionTime;

                if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

                    alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
                            "DB Query execution time is high while addToExisting usage information. " +
                                    "Last Query execution time: " + queryExecutionTime + " ms");

                    if (getLogger().isWarnLogLevel()) {
                        getLogger().warn(MODULE, "DB update query execution time getting high, Last Query execution time = " +
                                queryExecutionTime + " milliseconds.");
                    }
                }

            totalQueryTimeoutCount.set(0);

        } catch (IOException e) {
            throw new OperationFailedException("Error while addToExisting usage for subscriber ID: " +
                    subscriberIdentity + ". Reason: " + e.getMessage(), e);
        }
    }

    private Object[] createUsageArrayForAddToExisting(Collection<SubscriberUsage> subscriberUsages) {
        Object[] usageArgs = new Object[15];

        int i = 0;
        for (SubscriberUsage subscriberUsage: subscriberUsages) {
            String[] usageArray = new String[17];


            usageArray[0] = subscriberUsage.getDailyTotal() + "";
            usageArray[1] = subscriberUsage.getDailyUpload() + "";
            usageArray[2] = subscriberUsage.getDailyDownload() + "";
            usageArray[3] = subscriberUsage.getDailyTime() + "";

            usageArray[4] = subscriberUsage.getWeeklyTotal() + "";
            usageArray[5] = subscriberUsage.getWeeklyUpload() + "";
            usageArray[6] = subscriberUsage.getWeeklyDownload() + "";
            usageArray[7] = subscriberUsage.getWeeklyTime() + "";

            usageArray[8] = subscriberUsage.getBillingCycleTotal() + "";
            usageArray[9] = subscriberUsage.getBillingCycleUpload() + "";
            usageArray[10] = subscriberUsage.getBillingCycleDownload() + "";
            usageArray[11] = subscriberUsage.getBillingCycleTime() + "";

            usageArray[12] = subscriberUsage.getCustomTotal() + "";
            usageArray[13] = subscriberUsage.getCustomUpload() + "";
            usageArray[14] = subscriberUsage.getCustomDownload() + "";
            usageArray[15] = subscriberUsage.getCustomTime() + "";
            usageArray[16] = subscriberUsage.getId();
            usageArgs[i] = usageArray;
            i++;
        }

        return usageArgs;
    }

    public void replace(String subscriberIdentity, Collection<SubscriberUsage> usages, VoltDBClient voltDBClient) throws OperationFailedException {
        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Replace usage operation started for subscriber ID: "+  subscriberIdentity);
        }

        if (voltDBClient.isAlive() == false) {
            throw new OperationFailedException("Unable to perform replace usage operation for subscriber ID: " + subscriberIdentity
                    + ". Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        try {
            Object[] usageArgs = createUsageArrayForReplaceUsage(usages,getCurrentTime());
            long queryExecutionTime = timeSource.currentTimeInMillis();
            voltDBClient.callProcedure(new Callback(subscriberIdentity, VoltUMOperationData.REPLACE, usages), USAGE_REPLACE_STORED_PROCEDURE, subscriberIdentity, generateId(), usageArgs[0],
                    usageArgs[1],
                    usageArgs[2],
                    usageArgs[3],
                    usageArgs[4],
                    usageArgs[5],
                    usageArgs[6],
                    usageArgs[7],
                    usageArgs[8],
                    usageArgs[9],
                    usageArgs[10],
                    usageArgs[11],
                    usageArgs[12],
                    usageArgs[13],
                    usageArgs[14]);
                queryExecutionTime = timeSource.currentTimeInMillis() - queryExecutionTime;
                if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

                    alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
                            "DB Query execution time is high while replacing usage information. " +
                                    "Last Query execution time: " + queryExecutionTime + " ms");

                    if (getLogger().isWarnLogLevel()) {
                        getLogger().warn(MODULE, "DB update query execution time getting high, Last Query execution time = " +
                                queryExecutionTime + " milliseconds.");
                    }
                }
            totalQueryTimeoutCount.set(0);

        } catch (IOException e) {
            throw new OperationFailedException("Error while replace usage for subscriber ID: " +
                    subscriberIdentity + ". Reason: " + e.getMessage(), e);
        }
    }

    protected String generateId() {
        return UUID.randomUUID().toString();
    }

    private Object[] createUsageArrayForReplaceUsage(Collection<SubscriberUsage> subscriberUsages, long currentTime) {
        Object[] usageArgs = new Object[15];

        int i = 0;
        for (SubscriberUsage subscriberUsage: subscriberUsages) {
            String[] usageArray = new String[20];

            usageArray[0] = subscriberUsage.getDailyTotal() + "";
            usageArray[1] = subscriberUsage.getDailyUpload() + "";
            usageArray[2] = subscriberUsage.getDailyDownload() + "";
            usageArray[3] = subscriberUsage.getDailyTime() + "";

            usageArray[4] = subscriberUsage.getWeeklyTotal() + "";
            usageArray[5] = subscriberUsage.getWeeklyUpload() + "";
            usageArray[6] = subscriberUsage.getWeeklyDownload() + "";
            usageArray[7] = subscriberUsage.getWeeklyTime() + "";

            usageArray[8] = subscriberUsage.getBillingCycleTotal() + "";
            usageArray[9] = subscriberUsage.getBillingCycleUpload() + "";
            usageArray[10] = subscriberUsage.getBillingCycleDownload() + "";
            usageArray[11] = subscriberUsage.getBillingCycleTime() + "";

            usageArray[12] = subscriberUsage.getCustomTotal() + "";
            usageArray[13] = subscriberUsage.getCustomUpload() + "";
            usageArray[14] = subscriberUsage.getCustomDownload() + "";
            usageArray[15] = subscriberUsage.getCustomTime() + "";


        long dailyResetTime;
            if (subscriberUsage.getDailyResetTime() > currentTime) {
                dailyResetTime = subscriberUsage.getDailyResetTime();
        } else {
            dailyResetTime = getResetTime(currentTime);
        }

        long weeklyResetTime;
            if (subscriberUsage.getWeeklyResetTime() > currentTime) {
                weeklyResetTime = subscriberUsage.getWeeklyResetTime();
        } else {
            weeklyResetTime = getWeeklyTime(currentTime);
        }

        usageArray[16] = dailyResetTime + "";
        usageArray[17] = weeklyResetTime + "";
            usageArray[18] = subscriberUsage.getBillingCycleResetTime() + "";
            usageArray[19] = subscriberUsage.getId();
            usageArgs[i] = usageArray;
            i++;
        }

        return usageArgs;
    }

    private class Callback implements ProcedureCallback {
        private String subscriberIdentity;
        private int operation;
        private Collection<SubscriberUsage> usages;

        Callback(String subscriberIdentity, int operation, Collection<SubscriberUsage> usages) {
            this.subscriberIdentity = subscriberIdentity;
            this.operation = operation;
            this.usages = usages;
        }

        @Override
        public void clientCallback(ClientResponse response) {
            if (response.getStatus() == ClientResponse.SUCCESS) {
                return;
            }

            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Received ClientResonse status: " + response.getStatusString() + " for operation: "
                        + operation + " for subscriber id: " + subscriberIdentity);
            }
            doFailover(new VoltUMOperationData(usages, subscriberIdentity, operation));
        }

        private void doFailover(VoltUMOperationData unProcessedRecords) {

            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Processing CSV failover. Reason: UM Operation failed for subscriber Id: " + unProcessedRecords.getSubscriberIdentity() + ". ");
            }

            csvfailOverOperation.doFailover(unProcessedRecords);
        }
    }

    public void resetUsage(String subscriberIdentity, String productOfferId, VoltDBClient voltDBClient) throws OperationFailedException {

        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Performing operation to reset usage for subscriber ID: " + subscriberIdentity);
        }

        if (voltDBClient.isAlive() == false) {
            throw new OperationFailedException("Unable to perform reset usage operation for subscriber ID: " + subscriberIdentity
                    + ". Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        try {

            ProductOffer productOffer = policyRepository.getProductOffer().byId(productOfferId);
            UserPackage userPackage = productOffer.getDataServicePkgData();
            if (userPackage != null) {
                List<QuotaProfile> quotaProfiles = userPackage.getQuotaProfiles();
                Object[] usageResetArgs = createUsageArrayForResetUsage(quotaProfiles);

                long queryExecutionTime = timeSource.currentTimeInMillis();
                voltDBClient.callProcedure(
                        USAGE_RESET_STORED_PROCEDURE,
                        subscriberIdentity,
                        productOfferId,
                        generateId(),
                        usageResetArgs[0],
                        usageResetArgs[1],
                        usageResetArgs[2]);
                queryExecutionTime = timeSource.currentTimeInMillis() - queryExecutionTime;
                if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

                    alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
                            "DB Query execution time is high while replacing usage information. " +
                                    "Last Query execution time: " + queryExecutionTime + " ms");

                    if (getLogger().isWarnLogLevel()) {
                        getLogger().warn(MODULE, "DB update query execution time getting high, Last Query execution time = " +
                                queryExecutionTime + " milliseconds.");
                    }
                }
            }

            totalQueryTimeoutCount.set(0);

        } catch (IOException e) {
            throw new OperationFailedException("Error while resetting usage for subscriber ID: " +
                    subscriberIdentity + ". Reason: " + e.getMessage(), e);
        } catch (ProcCallException e){
            VoltDBUtil.handleProcedureCallException(e, "resetting usage for subscriber Id: " + subscriberIdentity, MODULE, alertListener, totalQueryTimeoutCount, voltDBClient);
        }
    }

    public Object[] createUsageArrayForResetUsage(List<QuotaProfile> quotaProfiles) {
        Object[] usageResetArgs = new Object[3];
        long currentTime = timeSource.currentTimeInMillis();

        long billingCycleResetTime = getFutureDate();

        for (int i = 0; i < quotaProfiles.size(); i++) {
            String[] usageResetArray = new String[20];
            if (quotaProfiles.get(i).getRenewalInterval() > 0 && quotaProfiles.get(i).getRenewalIntervalUnit() != null) {
                billingCycleResetTime = quotaProfiles.get(i).getRenewalIntervalUnit().addTime(System.currentTimeMillis(), quotaProfiles.get(i).getRenewalInterval());
            }

            usageResetArray[0] = Integer.toString(0) + "";
            usageResetArray[1] = Integer.toString(0) + "";
            usageResetArray[2] = Integer.toString(0) + "";
            usageResetArray[3] = Integer.toString(0) + "";

            usageResetArray[4] = Integer.toString(0) + "";
            usageResetArray[5] = Integer.toString(0) + "";
            usageResetArray[6] = Integer.toString(0) + "";
            usageResetArray[7] = Integer.toString(0) + "";

            usageResetArray[8] = Integer.toString(0) + "";
            usageResetArray[9] = Integer.toString(0) + "";
            usageResetArray[10] = Integer.toString(0) + "";
            usageResetArray[11] = Integer.toString(0) + "";

            usageResetArray[12] = Integer.toString(0) + "";
            usageResetArray[13] = Integer.toString(0) + "";
            usageResetArray[14] = Integer.toString(0) + "";
            usageResetArray[15] = Integer.toString(0) + "";

            usageResetArray[16] = Long.toString(getResetTime(currentTime)) + "";
            usageResetArray[17] = Long.toString(getWeeklyTime(currentTime)) + "";
            usageResetArray[18] = Long.toString(billingCycleResetTime) + "";
            usageResetArray[19] = quotaProfiles.get(i).getId();

            usageResetArgs[i] = usageResetArray;
        }

        return usageResetArgs;
    }

    public int deleteBaseAndPromotionalPackageUsage(String subscriberIdentity, VoltDBClient voltDBClient) throws OperationFailedException {

        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Delete usage operation for subscriber(" + subscriberIdentity + ") started");
        }

        if (voltDBClient.isAlive() == false) {
            throw new OperationFailedException("Unable to delete base and promotional package usage operation for subscriber ID: " + subscriberIdentity
                    + ". Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        try {
            long queryExecutionTime = timeSource.currentTimeInMillis();
            ClientResponse clientResponse = voltDBClient.callProcedure(USAGE_DELETE_STORED_PROCEDURE, subscriberIdentity);
            queryExecutionTime = timeSource.currentTimeInMillis() - queryExecutionTime;
            if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

                alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
                        "DB Query execution time is high while replacing usage information. " +
                                "Last Query execution time: " + queryExecutionTime + " ms");

                if (getLogger().isWarnLogLevel()) {
                    getLogger().warn(MODULE, "DB update query execution time getting high, Last Query execution time = " +
                            queryExecutionTime + " milliseconds.");
                }
            }

            VoltTable vt = clientResponse.getResults()[0];
            long deleteCount = vt.asScalarLong();

            if (deleteCount > 0) {
                if (getLogger().isInfoLogLevel()) {
                    getLogger().info(MODULE, "Total(" + deleteCount + ") Base package usage deleted for subscriber(" + subscriberIdentity + ")");
                }
            } else {
                getLogger().info(MODULE, "Base package usage not deleted for subscriber(" + subscriberIdentity
                        + "). Reason. Subscriber or base package usage not found");
            }

            return (int) vt.asScalarLong();
        } catch (IOException e) {
            throw new OperationFailedException("Error while replace usage for subscriber ID: " +
                    subscriberIdentity + ". Reason: " + e.getMessage(), e);
        } catch (ProcCallException e){
            VoltDBUtil.handleProcedureCallException(e, "replace usage for subscriber Id: " + subscriberIdentity, MODULE, alertListener, totalQueryTimeoutCount, voltDBClient);
        }

        return 0;
    }

    public int deleteBasePackageUsage(String subscriberIdentity, String packageId, VoltDBClient voltDBClient) throws OperationFailedException {
        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Delete usage operation for subscriber(" + subscriberIdentity + ") started");
        }

        if (voltDBClient.isAlive() == false) {
            throw new OperationFailedException("Unable to delete base package usage operation for subscriber ID: " + subscriberIdentity
                    + ". Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        try {
            long queryExecutionTime = timeSource.currentTimeInMillis();
            ClientResponse clientResponse = voltDBClient.callProcedure(USAGE_DELETE_BY_PACKAGE_ID_STORED_PROCEDURE, subscriberIdentity, packageId);
            queryExecutionTime = timeSource.currentTimeInMillis() - queryExecutionTime;
            if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

                alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
                        "DB Query execution time is high while replacing usage information. " +
                                "Last Query execution time: " + queryExecutionTime + " ms");

                if (getLogger().isWarnLogLevel()) {
                    getLogger().warn(MODULE, "DB update query execution time getting high, Last Query execution time = " +
                            queryExecutionTime + " milliseconds.");
                }
            }

            VoltTable vt = clientResponse.getResults()[0];
            long deleteCount = vt.asScalarLong();

            if (deleteCount > 0) {
                if (getLogger().isInfoLogLevel()) {
                    getLogger().info(MODULE, "Total(" + deleteCount + ") Base package usage deleted for subscriber(" + subscriberIdentity + ")");
                }
            } else {
                getLogger().info(MODULE, "Base package usage not deleted for subscriber(" + subscriberIdentity
                        + "). Reason. Subscriber or base package usage not found");
            }

            return (int) vt.asScalarLong();
        } catch (IOException e) {
            throw new OperationFailedException("Error while deleting base package for subscriber ID: " +
                    subscriberIdentity + ". Reason: " + e.getMessage(), e);
        } catch (ProcCallException e){
            VoltDBUtil.handleProcedureCallException(e, "deleting base package for subscriber Id: " + subscriberIdentity, MODULE, alertListener, totalQueryTimeoutCount, voltDBClient);
        }

        return 0;
    }

    public void scheduleForUsageDelete(String subscriberID, String alternateID, String packageId, String resetReason, String parameter1,
                                       String parameter2, String parameter3, VoltDBClient voltDBClient)
            throws OperationFailedException {

        insertIntoResetTable(subscriberID, alternateID, packageId, null, resetReason, parameter1, parameter2, parameter3, voltDBClient);
    }

    public void resetBillingCycle(String subscriberID, String alternateID, String productOfferId,
                                  long resetBillingCycleDate, String resetReason, String parameter1,
                                  String parameter2, String parameter3, VoltDBClient voltDBClient)
            throws OperationFailedException {

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Performing insertQuery reset usage operation for subscriber ID: " + subscriberID);
        }

        insertIntoResetTable(subscriberID, alternateID, productOfferId, new Timestamp(resetBillingCycleDate), resetReason, parameter1, parameter2, parameter3, voltDBClient);
    }

    private void insertIntoResetTable(String subscriberID, String alternateID, String productOfferId, @Nullable Timestamp resetBillingCycleDate, String resetReason, String parameter1, String parameter2, String parameter3, VoltDBClient voltDBClient) throws OperationFailedException {

        if (voltDBClient.isAlive() == false) {
            throw new OperationFailedException("Unable to perform reset billing cycle operation for subscriber ID: " + subscriberID
                    + ". Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        try {

            long queryExecutionTime = timeSource.currentTimeInMillis();
            voltDBClient.callProcedure(USAGE_RESET_BILLING_CYCLE_STORED_PROCEDURE, resetBillingCycleDate, createUsageArrayForResetBillingCycle(subscriberID, alternateID, productOfferId, resetReason, parameter1, parameter2, parameter3));
            queryExecutionTime = timeSource.currentTimeInMillis() - queryExecutionTime;
            if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

                alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
                        "DB Query execution time is high while replacing usage information. " +
                                "Last Query execution time: " + queryExecutionTime + " ms");

                if (getLogger().isWarnLogLevel()) {
                    getLogger().warn(MODULE, "OPEN-DB update query execution time getting high, Last Query execution time = " +
                            queryExecutionTime + " milliseconds.");
                }
            }

            totalQueryTimeoutCount.set(0);
        } catch (IOException e) {
            throw new OperationFailedException("Error while resetting billing cycle for subscriber ID: " +
                    subscriberID + ". Reason: " + e.getMessage(), e);
        } catch (ProcCallException e){
            VoltDBUtil.handleProcedureCallException(e, "resetting billing usage for subscriber Id: " + subscriberID, MODULE, alertListener, totalQueryTimeoutCount, voltDBClient);
        }
    }

    private String[] createUsageArrayForResetBillingCycle(String subscriberID, String alternateID, String productOfferId, String resetReason, String parameter1, String parameter2, String parameter3) {
        String[] usageArray = new String[11];

        usageArray[0] = UUID.randomUUID().toString();
        usageArray[1] = subscriberID;
        usageArray[2] = alternateID;
        usageArray[3] = BillingCycleResetStatus.PENDING.getVal();
        usageArray[4] = CommonConstants.DEFAULT_SERVER_INSTANCE_VALUE;
        usageArray[5] = policyRepository.getProductOffer().byId(productOfferId).getDataServicePkgId();
        usageArray[6] = resetReason;
        usageArray[7] = parameter1;
        usageArray[8] = parameter2;
        usageArray[9] = parameter3;
        usageArray[10] = productOfferId;

        return usageArray;
    }

    public List<SubscriptionInformation> getBalance(SPRInfo sprInfo, String pkgId, String subscriptionId,
                                                    Map<String, Subscription> addOnSubscriptions, VoltDBClient voltDBClient) throws OperationFailedException {

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Fetching balance information for subscriber ID: " + sprInfo.getSubscriberIdentity());
        }

        ProductOffer productOffer = policyRepository.getProductOffer().byName(sprInfo.getProductOffer());

        if (Objects.isNull(productOffer)) {
            throw new OperationFailedException("No product offer found for subscriber ID: " + sprInfo.getSubscriberIdentity(),ResultCode.NOT_FOUND);
        }

        UserPackage pkg = null;
        if (Strings.isNullOrBlank(pkgId) == false) {
            pkg = policyRepository.getPkgDataById(pkgId);

            if (pkg == null) {
                throw new OperationFailedException("No package found with pkgId: " + pkgId + " for subscriber ID: " + sprInfo.getSubscriberIdentity(), ResultCode.NOT_FOUND);
            }
        }

        Subscription addOnSubscription = null;
        if (Strings.isNullOrBlank(subscriptionId) == false) {
            addOnSubscription = addOnSubscriptions.get(subscriptionId);
            if (addOnSubscription == null) {
                throw new OperationFailedException("No subscription found for subscription ID: " + subscriptionId, ResultCode.NOT_FOUND);
            }
        }

        Map<String, Map<String, SubscriberUsage>> usage = getUsage(sprInfo.getSubscriberIdentity(), addOnSubscriptions, voltDBClient);

        if (Maps.isNullOrEmpty(usage)) {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "No usage found for subscriber ID: " + sprInfo.getSubscriberIdentity());
            }
        } else {
            if (getLogger().isDebugLogLevel()) {
                StringBuilder builder = new StringBuilder();
                builder.append("Usage for subscriber ID: " + sprInfo.getSubscriberIdentity() + CommonConstants.LINE_SEPARATOR);
                for (Map.Entry<String, Map<String, SubscriberUsage>> usageEntry : usage.entrySet()) {
                    builder.append("Package or SubscriptionId: " + usageEntry.getKey() + CommonConstants.LINE_SEPARATOR);
                    for (Map.Entry<String, SubscriberUsage> subscriberUsage : usageEntry.getValue().entrySet()) {
                        builder.append("Key: " + subscriberUsage.getKey() + CommonConstants.LINE_SEPARATOR);
                        builder.append("SubscriberUsage: " + subscriberUsage.getValue() + CommonConstants.LINE_SEPARATOR);
                    }
                }
                getLogger().debug(MODULE, builder.toString());
            }
        }


        List<SubscriptionInformation> subscriptionInformations = new ArrayList<>();
        long currentTimeInMillis = System.currentTimeMillis();
        if (pkg != null && addOnSubscription != null) {
            if (PkgType.BASE == pkg.getPackageType() || PkgType.PROMOTIONAL == pkg.getPackageType()) {
                throw new OperationFailedException("Package : " + pkg.getName() + " is of Base/Promotional type for subscriber Id: " + sprInfo.getSubscriberIdentity() + ". It cannot contain subscription Id", ResultCode.INVALID_INPUT_PARAMETER);
            } else {

                SubscriptionPackage subscriptionPackage = policyRepository.getAddOnById(addOnSubscription.getPackageId());

                if (subscriptionPackage == null) {
                    throw new OperationFailedException("Subscription package not found for package id: " + addOnSubscription.getPackageId()
                            + ", subscription id: " + addOnSubscription.getId() + ", subscriber ID: " + sprInfo.getSubscriberIdentity(), ResultCode.NOT_FOUND);
                }

                if (addOnSubscription.getPackageId().equals(pkgId) == false) {
                    throw new OperationFailedException("SubscriptionId: " + subscriptionId + " and addOnId: " + pkgId + " are not related for subscriber ID: " + sprInfo.getSubscriberIdentity(), ResultCode.NOT_FOUND);
                }
                subscriptionInformations.add(createAddOnSubscriptionInformation(addOnSubscription, usage, currentTimeInMillis, subscriptionPackage, productOffer.getId()));
            }

        } else if (pkg != null && addOnSubscription == null) {

            if (PkgType.BASE == pkg.getPackageType()) {
                if(Objects.isNull(productOffer.getDataServicePkgData()) || (Objects.nonNull(productOffer.getDataServicePkgData()) && productOffer.getDataServicePkgData().getId().equals(pkg.getId()) == false)){
                    throw new OperationFailedException("Base Package :" + pkg.getName() + " is not associated with subscriber ID: " + sprInfo.getSubscriberIdentity(), ResultCode.NOT_FOUND);
                }
                subscriptionInformations.add(createPkgSubscriptionInformation(usage, pkg, currentTimeInMillis,productOffer.getId()));
            } else if (PkgType.PROMOTIONAL == pkg.getPackageType()) {
                subscriptionInformations.add(createPkgSubscriptionInformation(usage, pkg, currentTimeInMillis,productOffer.getId()));
            } else {
                if (Maps.isNullOrEmpty(addOnSubscriptions)) {
                    throw new OperationFailedException("No subscription found for subscriber identity: " + sprInfo.getSubscriberIdentity(), ResultCode.NOT_FOUND);
                }

                boolean isFound = false;
                for (Subscription subscription : addOnSubscriptions.values()) {

                    if (subscription.getPackageId().equals(pkg.getId())) {

                        SubscriptionPackage subscriptionPackage = policyRepository.getAddOnById(subscription.getPackageId());

                        if (subscriptionPackage == null) {
                            getLogger().warn(MODULE, "Skipping subscription(id:" + subscription.getId() + ") for subscriber ID: " + subscription.getSubscriberIdentity()
                                    + ". Reason: Subscription package not found for package id: " + subscription.getPackageId());
                            continue;
                        }

                        subscriptionInformations.add(createAddOnSubscriptionInformation(subscription,
                                usage, currentTimeInMillis, subscriptionPackage,productOffer.getId()));
                        isFound = true;
                    }
                }
                if (isFound == false) {
                    throw new OperationFailedException("No subscription found for package: " + pkg.getName() + "(" + pkgId + ") for subscriber ID: " + sprInfo.getSubscriberIdentity(), ResultCode.NOT_FOUND);
                }
            }

        } else if (pkg == null && addOnSubscription != null) {

            SubscriptionPackage subscriptionPackage = policyRepository.getAddOnById(addOnSubscription.getPackageId());

            if (subscriptionPackage == null) {
                throw new OperationFailedException("Subscription package not found for package id: " + addOnSubscription.getPackageId()
                        + ", subscription id: " + addOnSubscription.getId() + ", subscriber ID: " + sprInfo.getSubscriberIdentity(), ResultCode.NOT_FOUND);
            }

            subscriptionInformations.add(createAddOnSubscriptionInformation(addOnSubscription, usage, currentTimeInMillis, subscriptionPackage,productOffer.getId()));
        } else {
            throw new OperationFailedException("No criteria provided for fetching balance", ResultCode.INVALID_INPUT_PARAMETER);
        }

        return subscriptionInformations;
    }

    private SubscriptionInformation createPkgSubscriptionInformation(@Nullable Map<String, Map<String, SubscriberUsage>> usage,
                                                                     UserPackage pkg, long currentTimeInMillis,String productOfferId) {

        SubscriptionInformation subscriptionInformation = new SubscriptionInformation();
        subscriptionInformation.setPackageId(pkg.getId());
        subscriptionInformation.setPackageName(pkg.getName());
        subscriptionInformation.setPackageType(pkg.getPackageType().name());
        subscriptionInformation.setQuotaProfileType(pkg.getQuotaProfileType());

        if (pkg.getQuotaProfileType() == QuotaProfileType.USAGE_METERING_BASED) {

            List<QuotaProfileBalance> profileBalance;
            if (usage == null) {
                profileBalance = createQuotaBalance(pkg, null, currentTimeInMillis,productOfferId);
            } else {
                profileBalance = createQuotaBalance(pkg, usage.get(pkg.getId()), currentTimeInMillis,productOfferId);
            }
            subscriptionInformation.setQuotaProfileBalances(profileBalance);
        }

        return subscriptionInformation;
    }

    private List<QuotaProfileBalance> createQuotaBalance(UserPackage pkg, @Nullable Map<String, SubscriberUsage> usage,
                                                         long currentTimeInMillis, String productOfferId) {

        List<QuotaProfileBalance> quotaProfileBalanceList = new ArrayList<>();
        List<QuotaProfile> quotaProfileDataList = pkg.getQuotaProfiles();
        for (QuotaProfile quotaProfile : quotaProfileDataList) {
            QuotaProfileBalance quotaProfileBalance = new QuotaProfileBalance();
            quotaProfileBalance.setQuotaProfileId(quotaProfile.getId());
            quotaProfileBalance.setQuotaProfileName(quotaProfile.getName());

            Map<String, QuotaProfileDetail> fupLevelServiceWiseQuotaProfileDetails = quotaProfile.getBalanceLevel().getBalanceLevelQuotaProfileDetail(quotaProfile);
            if (Maps.isNullOrEmpty(fupLevelServiceWiseQuotaProfileDetails)) {
                getLogger().warn(MODULE, "Skipping creating balance for quota profile: " + quotaProfile.getName() + ". Reason: FUP quota detail not configured for mentio");
                continue;
            }

            for (QuotaProfileDetail quotaProfileDetail : fupLevelServiceWiseQuotaProfileDetails.values()) {

                SubscriberUsage subscriberUsage = null;
                if (usage != null) {
                    subscriberUsage = usage.get(((UMBaseQuotaProfileDetail) quotaProfileDetail).getUsageKey());
                }

                if (subscriberUsage == null) {
                    SubscriberUsage.SubscriberUsageBuilder usageBuilder = new SubscriberUsage.SubscriberUsageBuilder("-1", quotaProfile.getId(), "", quotaProfileDetail
                            .getServiceId(), pkg.getId(),productOfferId);
                    subscriberUsage = usageBuilder.withAllTypeUsage(0, 0, 0, 0).build();
                } else {
                    validateAndSetSubscriberUsage(subscriberUsage, currentTimeInMillis);
                }

                createBalance(quotaProfileBalance, quotaProfileDetail, subscriberUsage);
            }

            quotaProfileBalanceList.add(quotaProfileBalance);
        }

        return quotaProfileBalanceList;
    }

    private void createBalance(QuotaProfileBalance quotaProfileBalance, QuotaProfileDetail quotaProfileDetail, SubscriberUsage subscriberUsage) {

        if (((UMBaseQuotaProfileDetail) quotaProfileDetail).getDailyAllowedUsage() != null) {

            quotaProfileBalance.addBalanceInformation(createUsageInformationDetails(subscriberUsage, quotaProfileDetail, ((UMBaseQuotaProfileDetail) quotaProfileDetail).getDailyAllowedUsage()
                    , AggregationKey.DAILY));
        }

        if (((UMBaseQuotaProfileDetail) quotaProfileDetail).getWeeklyAllowedUsage() != null) {

            quotaProfileBalance.addBalanceInformation(createUsageInformationDetails(subscriberUsage, quotaProfileDetail, ((UMBaseQuotaProfileDetail) quotaProfileDetail).getWeeklyAllowedUsage()
                    , AggregationKey.WEEKLY));
        }

        if (((UMBaseQuotaProfileDetail) quotaProfileDetail).getBillingCycleAllowedUsage() != null) {

            quotaProfileBalance.addBalanceInformation(createUsageInformationDetails(subscriberUsage, quotaProfileDetail, ((UMBaseQuotaProfileDetail) quotaProfileDetail).getBillingCycleAllowedUsage()
                    , AggregationKey.BILLING_CYCLE));
        }

        if (((UMBaseQuotaProfileDetail) quotaProfileDetail).getCustomAllowedUsage() != null) {

            quotaProfileBalance.addBalanceInformation(createUsageInformationDetails(subscriberUsage, quotaProfileDetail, ((UMBaseQuotaProfileDetail) quotaProfileDetail).getCustomAllowedUsage()
                    , AggregationKey.CUSTOM));
        }

    }

    private UsageInfo createUsageInformationDetails(SubscriberUsage subscriberUsage, QuotaProfileDetail quotaProfileDetail, AllowedUsage allowedUsage, AggregationKey aggregationKey) {

        UsageInfo usageInformation = new UsageInfo();
        usageInformation.setServiceId(quotaProfileDetail.getServiceId());
        usageInformation.setServiceName(quotaProfileDetail.getServiceName());

        Usage allowedUsageWS = getAllowedUsage(allowedUsage);
        usageInformation.setAllowedUsage(allowedUsageWS);
        usageInformation.setBalance(getBalance(allowedUsageWS, subscriberUsage, aggregationKey));
        usageInformation.setCurretUsage(getCurrentUsage(subscriberUsage, aggregationKey));
        usageInformation.setAggregationKey(aggregationKey);

        return usageInformation;
    }

    /**
     * This method is used when balance from all subscribed package is needed.
     */

    public List<SubscriptionInformation> getBalance(SPRInfo sprInfo, Map<String, Subscription> addOnSubscriptions,
                                                    VoltDBClient voltDBClient) throws OperationFailedException {

        String subscriberIdentity = sprInfo.getSubscriberIdentity();
        List<SubscriptionInformation> subscriptionInformations = new ArrayList<>();
        Map<String, Map<String, SubscriberUsage>> usage = getUsage(subscriberIdentity, addOnSubscriptions, voltDBClient);

        if (Maps.isNullOrEmpty(usage)) {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "No usage found for subscriber ID: " + subscriberIdentity);
            }
        } else {
            if (getLogger().isDebugLogLevel()) {
                StringBuilder builder = new StringBuilder();
                builder.append("Usage for subscriber ID: " + subscriberIdentity + CommonConstants.LINE_SEPARATOR);
                for (Map.Entry<String, Map<String, SubscriberUsage>> usageEntry : usage.entrySet()) {
                    builder.append("Package or SubscriptionId: " + usageEntry.getKey() + CommonConstants.LINE_SEPARATOR);
                    for (Map.Entry<String, SubscriberUsage> subscriberUsage : usageEntry.getValue().entrySet()) {
                        builder.append("Key: " + subscriberUsage.getKey() + CommonConstants.LINE_SEPARATOR);
                        builder.append("SubscriberUsage: " + subscriberUsage.getValue() + CommonConstants.LINE_SEPARATOR);
                    }
                }
                getLogger().debug(MODULE, builder.toString());
            }
        }

        long currentTimeInMillis = System.currentTimeMillis();
        String productOfferName = sprInfo.getProductOffer();
        ProductOffer productOffer = policyRepository.getProductOffer().byName(productOfferName);
        if(Objects.isNull(productOffer)){
            throw new OperationFailedException(
                    "No product offer found with product offer: " + productOfferName + " for subscriber ID: " + sprInfo.getSubscriberIdentity(), ResultCode.NOT_FOUND);
        }

        if(Objects.nonNull(productOffer.getDataServicePkgData())) {
            UserPackage pkg = productOffer.getDataServicePkgData();
            subscriptionInformations.add(createPkgSubscriptionInformation(usage, pkg, currentTimeInMillis,productOffer.getId()));
        }

        for (Subscription addOnSubscription : addOnSubscriptions.values()) {
            SubscriptionPackage subscriptionPackage = policyRepository.getAddOnById(addOnSubscription.getPackageId());

            if (subscriptionPackage == null) {
                getLogger().warn(MODULE, "Skipping subscription(id:" + addOnSubscription.getId() +
                        ") for subscriber ID: " + subscriberIdentity + ". Reason: Subscription package not found for id: "
                        + addOnSubscription.getPackageId());
                continue;
            }

            subscriptionInformations.add(createAddOnSubscriptionInformation(addOnSubscription, usage, currentTimeInMillis, subscriptionPackage,productOffer.getId()));
        }

        //Adding promotional package balance information
        for (PromotionalPackage promotionalPackage : policyRepository.getPromotionalPackages()) {
            subscriptionInformations.add(createPkgSubscriptionInformation(usage, promotionalPackage, currentTimeInMillis,productOffer.getId()));
        }

        return subscriptionInformations;
    }

    private Usage getBalance(Usage allowedUsage,
                             SubscriberUsage subscriberUsage,
                             AggregationKey aggregationKey) {

        long usedTotal = 0;
        long usedUpload = 0;
        long usedDownload = 0;
        long usedTime = 0;

        switch (aggregationKey) {
            case DAILY:

                usedTotal = subscriberUsage.getDailyTotal();
                usedUpload = subscriberUsage.getDailyUpload();
                usedDownload = subscriberUsage.getDailyDownload();
                usedTime = subscriberUsage.getDailyTime();
                break;
            case WEEKLY:

                usedTotal = subscriberUsage.getWeeklyTotal();
                usedUpload = subscriberUsage.getWeeklyUpload();
                usedDownload = subscriberUsage.getWeeklyDownload();
                usedTime = subscriberUsage.getWeeklyTime();
                break;
            case BILLING_CYCLE:

                usedTotal = subscriberUsage.getBillingCycleTotal();
                usedUpload = subscriberUsage.getBillingCycleUpload();
                usedDownload = subscriberUsage.getBillingCycleDownload();
                usedTime = subscriberUsage.getBillingCycleTime();
                break;
            case CUSTOM:

                usedTotal = subscriberUsage.getCustomTotal();
                usedUpload = subscriberUsage.getCustomUpload();
                usedDownload = subscriberUsage.getCustomDownload();
                usedTime = subscriberUsage.getCustomTime();
                break;
        }

        long totalOctets = allowedUsage.getTotalOctets() == UNLIMITED_USAGE ? UNLIMITED_USAGE
                : allowedUsage.getTotalOctets() - usedTotal;
        long uploadOctets = allowedUsage.getUploadOctets() == UNLIMITED_USAGE ? UNLIMITED_USAGE
                : allowedUsage.getUploadOctets() - usedUpload;
        long downloadOctets = allowedUsage.getDownloadOctets() == UNLIMITED_USAGE ? UNLIMITED_USAGE
                : allowedUsage.getDownloadOctets() - usedDownload;
        long time = allowedUsage.getTime() == UNLIMITED_USAGE ? UNLIMITED_USAGE
                : allowedUsage.getTime() - usedTime;

        return new Usage(uploadOctets, downloadOctets, totalOctets, time);
    }

    private Usage getCurrentUsage(SubscriberUsage subscriberUsage, AggregationKey aggregationKey) {

        switch (aggregationKey) {
            case DAILY:

                return new Usage(subscriberUsage.getDailyUpload(), subscriberUsage.getDailyDownload(), subscriberUsage.getDailyTotal()
                        , subscriberUsage.getDailyTime());
            case WEEKLY:

                return new Usage(subscriberUsage.getWeeklyUpload(), subscriberUsage.getWeeklyDownload(), subscriberUsage.getWeeklyTotal()
                        , subscriberUsage.getWeeklyTime());
            case CUSTOM:

                return new Usage(subscriberUsage.getCustomUpload(), subscriberUsage.getCustomDownload(), subscriberUsage.getCustomTotal()
                        , subscriberUsage.getCustomTime());
            case BILLING_CYCLE:

                return new Usage(subscriberUsage.getBillingCycleUpload(), subscriberUsage.getBillingCycleDownload(), subscriberUsage.getBillingCycleTotal()
                        , subscriberUsage.getBillingCycleTime());

        }
        return null;
    }


    private Usage getAllowedUsage(AllowedUsage allowedUsage) {

        long uploadInBytes = allowedUsage.getUploadInBytes() == 0 ? UNLIMITED_USAGE : allowedUsage.getUploadInBytes();
        long downloadInBytes = allowedUsage.getDownloadInBytes() == 0 ? UNLIMITED_USAGE : allowedUsage.getDownloadInBytes();
        long totalInBytes = allowedUsage.getTotalInBytes() == 0 ? UNLIMITED_USAGE : allowedUsage.getTotalInBytes();
        long timeInSeconds = allowedUsage.getTimeInSeconds() == 0 ? UNLIMITED_USAGE : allowedUsage.getTimeInSeconds();

        return new Usage(uploadInBytes, downloadInBytes, totalInBytes
                , timeInSeconds);
    }

    private void validateAndSetSubscriberUsage(SubscriberUsage subscriberUsage, long currentTimeInMillis) {
        if (subscriberUsage.getDailyResetTime() < currentTimeInMillis) {
            subscriberUsage.setDailyTotal(0);
            subscriberUsage.setDailyDownload(0);
            subscriberUsage.setDailyUpload(0);
            subscriberUsage.setDailyTime(0);
        }

        if (subscriberUsage.getWeeklyResetTime() < currentTimeInMillis) {
            subscriberUsage.setWeeklyTotal(0);
            subscriberUsage.setWeeklyDownload(0);
            subscriberUsage.setWeeklyUpload(0);
            subscriberUsage.setWeeklyTime(0);
        }
        if (subscriberUsage.getBillingCycleResetTime() < currentTimeInMillis) {
            subscriberUsage.setBillingCycleTotal(0);
            subscriberUsage.setBillingCycleDownload(0);
            subscriberUsage.setBillingCycleUpload(0);
            subscriberUsage.setBillingCycleTime(0);
        }
        if (subscriberUsage.getCustomResetTime() < currentTimeInMillis) {
            subscriberUsage.setCustomTotal(0);
            subscriberUsage.setCustomDownload(0);
            subscriberUsage.setCustomUpload(0);
            subscriberUsage.setCustomTime(0);
        }
    }


    private SubscriptionInformation createAddOnSubscriptionInformation(Subscription addOnSubscription,
                                                                       @Nullable Map<String, Map<String, SubscriberUsage>> usage, long currentTimeInMillis, SubscriptionPackage subscriptionPackage, String productOfferId) {

        SubscriptionInformation subscriptionInformation = new SubscriptionInformation();
        subscriptionInformation.setPackageName(subscriptionPackage.getName());
        subscriptionInformation.setPackageDescription(subscriptionPackage.getDescription());
        subscriptionInformation.setPackageId(subscriptionPackage.getId());
        subscriptionInformation.setPackageType(subscriptionPackage.getPackageType().name());
        subscriptionInformation.setAddonSubscriptionId(addOnSubscription.getId());
        subscriptionInformation.setStartTime(addOnSubscription.getStartTime());
        subscriptionInformation.setEndTime(addOnSubscription.getEndTime());
        subscriptionInformation.setAddOnStatus(addOnSubscription.getStatus());
        subscriptionInformation.setQuotaProfileType(subscriptionPackage.getQuotaProfileType());

        if (subscriptionPackage.getQuotaProfileType() == QuotaProfileType.USAGE_METERING_BASED) {
            if (usage == null) {
                subscriptionInformation
                        .setQuotaProfileBalances(createQuotaBalance(subscriptionPackage, null, currentTimeInMillis,productOfferId));
            } else {
                subscriptionInformation.setQuotaProfileBalances(createQuotaBalance(subscriptionPackage, usage
                        .get(addOnSubscription.getId()), currentTimeInMillis,productOfferId));
            }
        }
        return subscriptionInformation;
    }

    public class VoltUMOperationData {
        public static final int INSERT = 0;
        public static final int ADD_TO_EXISTING = 1;
        public static final int REPLACE = 2;
        private Collection<SubscriberUsage> subscriberUsages;
        private int operation;
        private String subscriberIdentiry;


        public VoltUMOperationData(Collection<SubscriberUsage> subscriberUsages, String subscriberIdentity, int operation) {
            this.subscriberUsages = subscriberUsages;
            this.subscriberIdentiry = subscriberIdentity;
            this.operation = operation;
        }

        public Collection<SubscriberUsage> getUsages() {
            return subscriberUsages;
        }

        public String getSubscriberIdentity() {
            return subscriberIdentiry;
        }

        public int getOperation() {
            return operation;
        }
    }



    private class ShutdownHook extends Thread {
        private ScheduledExecutorService executor;
        private RecordProcessor<VoltUMOperationData> recordProcessor;

        public ShutdownHook(ScheduledExecutorService executor, RecordProcessor<VoltUMOperationData> recordProcessor) {
            this.executor = executor;
            this.recordProcessor = recordProcessor;
        }

        @Override
        public void run() {
            shutdown();
        }

        private void shutdown() {
            shutDownExecutor(executor);
            recordProcessor.stop();
        }

        private void shutDownExecutor(ScheduledExecutorService executor) {

            try {
                executor.shutdown();
                if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
                    LogManager.getLogger().info(MODULE, "Waiting for VoltUMOperation level scheduled async task executor to complete execution");
                if (executor.awaitTermination(5, TimeUnit.SECONDS) == false) {
                    if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
                        LogManager.getLogger().warn(MODULE, "Shutting down VoltUMOperation level scheduled async task executor forcefully. " +
                                "Reason: Async task taking more than 5 second to complete");
                    executor.shutdownNow();
                }
            } catch (Exception ex) {
                try {
                    executor.shutdownNow();
                } catch (Exception e) {
                    getLogger().trace(MODULE, e);
                }
                ignoreTrace(ex);
            }
        }
    }

    public void stop() {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Stopping Volt UM scheduler");
        }
        try {
            shutdownHook.shutdown();
        } catch (Exception e) {
            getLogger().error(MODULE, "Error while stopping Volt UM scheduler");
            getLogger().trace(MODULE, e);
        }
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Volt UM scheduler stopped successfully");
        }
    }

}