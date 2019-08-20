package com.elitecore.corenetvertex.spr.voltdb;

import com.elitecore.commons.annotations.VisibleForTesting;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Maps;
import com.elitecore.commons.base.Numbers;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.base.TimeSource;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.corenetvertex.constants.ActionType;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.constants.SubscriptionState;
import com.elitecore.corenetvertex.constants.ValidityPeriodUnit;
import com.elitecore.corenetvertex.core.alerts.AlertConstants;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.alerts.Alerts;
import com.elitecore.corenetvertex.core.db.VoltDBClient;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.bod.BoDPackage;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.pkg.datapackage.QuotaTopUp;
import com.elitecore.corenetvertex.pm.pkg.datapackage.SubscriptionPackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;
import com.elitecore.corenetvertex.spr.EDRListener;
import com.elitecore.corenetvertex.spr.MonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberMonetaryBalanceWrapper;
import com.elitecore.corenetvertex.spr.SubscriberUsage;
import com.elitecore.corenetvertex.spr.SubscriptionOperation;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.data.SubscriptionDetail;
import com.elitecore.corenetvertex.spr.data.SubscriptionType;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.spr.util.SubscriptionPackagePredicates;
import com.elitecore.corenetvertex.spr.util.SubscriptionPackagePredicates.SubscriptionEndTimePredicate;
import com.elitecore.corenetvertex.spr.util.SubscriptionPackagePredicates.SubscriptionPackageIdPredicate;
import com.elitecore.corenetvertex.spr.util.SubscriptionUtil;
import com.elitecore.corenetvertex.spr.voltdb.util.VoltDBUtil;
import org.voltdb.VoltTable;
import org.voltdb.client.ClientResponse;
import org.voltdb.client.ProcCallException;

import javax.annotation.Nullable;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.corenetvertex.constants.CommonConstants.SUBSCRIBE_ADDON_PRODUCT_OFFER;
import static com.elitecore.corenetvertex.constants.CommonConstants.SUBSCRIBE_BOD;
import static java.util.Objects.nonNull;

public class VoltSubscriptionOperation implements SubscriptionOperation {

    private static final String MODULE = "VOLT-SUBSCRIPTION-OPR";
    private static final String SUBSCRIPTION_SELECT_STORED_PROCEDURE = "SubscriptionSelectStoredProcedure";
    private static final String SUBSCRIPTION_ADD_STORED_PROCEDURE = "SubscriptionAddStoredProcedure";
    private static final String SUBSCRIPTION_ADD_WITH_UM_STORED_PROCEDURE = "SubscriptionAddWithUmStoredProcedure";
    public static final String FETCHING_ACTIVE_SUBSCRIPTION = "fetching active subscription";
    public static final String ADD_SUBSCRIPTION_DETAILS = "Add subscription details";
    public static final String UNSUBSCRIBE_SUBSCRIPTION_BY_SUBSCRIBER_ID = "SubscriptionUnsubscribeStoredProcedure";
    private static final String FETCH_SUBSCRIPTION_BY_SUBSCRIPTION_ID = "SubscriptionSelectBySubscriptionIdStoredProcedure";
    private static final String UPDATE_SUBSCRIPTION_DETAIL_BY_SUBSCRIPTION_ID = "SubscriptionUpdateDetailStoredProcedure";
    public static final String CHANGE_BILL_DAY_DETAILS = "Change Bill Day";
    private static final String CHANGE_BILL_DAY_BY_SUBSCRIBER_ID = "SubscriberChangeBillDayStoredProcedure";

    private AtomicInteger totalQueryTimeoutCount;
    private AlertListener alertListener;
    private PolicyRepository policyRepository;
    private TimeSource timeSource;
    private VoltUMOperation umOperation;
    private EDRListener balanceEDRListener;

    public VoltSubscriptionOperation(AlertListener alertListener, PolicyRepository policyRepository, TimeSource timeSource, VoltUMOperation umOperation, EDRListener balanceEDRListener) {
        this.alertListener = alertListener;
        this.policyRepository = policyRepository;
        this.timeSource = timeSource;
        this.umOperation = umOperation;
        this.totalQueryTimeoutCount = new AtomicInteger(0);
        this.balanceEDRListener = balanceEDRListener;
    }

    /**
     * Subscribes Addon by Addon Id
     *
     *
     * @param sprInfo
     * @param parentId
     * @param addOnProductOfferId
     * @param subscriptionStatusValue
     * @param startTime
     * @param endTime
     * @param voltDBClient
     * @return
     * @throws OperationFailedException
     */


    public List<Subscription> subscribeAddOnById(SPRInfo sprInfo, String parentId, String addOnProductOfferId,
                                           Integer subscriptionStatusValue,
                                           Long startTime, Long endTime, Integer priority, String param1, String param2,
                                           MonetaryBalance monetaryBalance,
                                           VoltDBClient voltDBClient,
                                           String requestIpAddress) throws OperationFailedException {

        String subscriberIdentity = sprInfo.getSubscriberIdentity();

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Subscribing package(" + addOnProductOfferId + ") for subscriber ID: " + subscriberIdentity);
        }

        ProductOffer addOn = policyRepository.getProductOffer().byId(addOnProductOfferId);
        if (addOn == null) {
            throw new OperationFailedException("Unable to subscribe package(" + addOnProductOfferId + ") for subscriber ID: " + subscriberIdentity
                    + ". Reason: Package not found for ID: " + addOnProductOfferId, ResultCode.NOT_FOUND);
        }

        if (addOn.getPolicyStatus() == PolicyStatus.FAILURE) {
            throw new OperationFailedException("Unable to subscribe addOn(" + addOnProductOfferId + ") for subscriber ID: " + subscriberIdentity
                    + ". Reason: AddOn(" + addOn.getName() + ") is failed addOn", ResultCode.INVALID_INPUT_PARAMETER);
        }

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Package(" + addOn.getName() + ") found for ID: " + addOn.getId());
        }

        return subscribeAddOn(addOn, sprInfo, parentId, addOnProductOfferId,  subscriptionStatusValue, startTime, endTime, priority, voltDBClient, param1, param2, null,monetaryBalance, requestIpAddress);
    }

    private List<Subscription> subscribeAddOn(ProductOffer addOnProductOffer, SPRInfo sprInfo, String parentId,String productOfferId, Integer subscriptionStatusValue, Long startTime,
                                        Long endTime, Integer priority, VoltDBClient voltDBClient, String param1, String param2, @Nullable String subscriptionId,MonetaryBalance monetaryBalance, String requestIpAddress) throws OperationFailedException {

        String subscriberIdentity = sprInfo.getSubscriberIdentity();
        List<Subscription> subscriptions = Collectionz.newArrayList();
        String monetaryBalanceId = null;
        long currentTimeMS = System.currentTimeMillis();

        startTime = Numbers.POSITIVE_LONG.apply(startTime) ? startTime.longValue() : currentTimeMS;

        endTime = Numbers.POSITIVE_LONG.apply(endTime) ? endTime.longValue()
                : addTime(startTime, addOnProductOffer.getValidityPeriod(), addOnProductOffer.getValidityPeriodUnit());

        validateStartTimeAndEndTime(startTime, endTime, currentTimeMS);

        if(Objects.isNull(priority)) {
            priority = CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY;
        }

        try {

            if(addOnProductOffer.getDataServicePkgData() != null) {
                SubscriptionState subscriptionState = getSubscriptionStateFromValue(subscriberIdentity, subscriptionStatusValue);

                Timestamp endTimeMSTS = new Timestamp(endTime);
                Timestamp startTimeMSTS = new Timestamp(startTime);

                String addOnSubscriptionId = Strings.isNullOrBlank(subscriptionId) ? getNextSubscriptionId() : subscriptionId.trim();
                Subscription addOnSubscription = new Subscription(addOnSubscriptionId, subscriberIdentity, addOnProductOffer.getDataServicePkgData().getId(), productOfferId, startTimeMSTS, endTimeMSTS,
                        subscriptionState, priority, SubscriptionType.ADDON, param1, param2);

                if (addOnProductOffer.getDataServicePkgData().getQuotaProfileType() == QuotaProfileType.RnC_BASED) {
                    throw new OperationFailedException("Unable to subscribe package(" + addOnProductOffer.getName() + ") for subscriber ID: " + subscriberIdentity
                            + ". Reason: RNC Based Quota profile type not supported.");
                }

                if (addOnProductOffer.getDataServicePkgData().getQuotaProfileType() == QuotaProfileType.USAGE_METERING_BASED) {
                    List<SubscriberUsage> subscriberUsages = new ArrayList<>();
                    for (QuotaProfile quotaProfile : addOnProductOffer.getDataServicePkgData().getQuotaProfiles()) {
                        Set<String> serviceTosubscriberUsage = new HashSet<String>(4);

                        addUsageBasedOnLevels(addOnProductOffer, subscriberIdentity, productOfferId, addOnSubscription, subscriberUsages, quotaProfile, serviceTosubscriberUsage);
                    }

                    MonetaryBalance previousMonetaryBalance = null;
                    if(addOnProductOffer.getSubscriptionPrice() > 0){
                        if(Objects.nonNull(monetaryBalance)) {
                            monetaryBalanceId = monetaryBalance.getId();
                            previousMonetaryBalance = monetaryBalance.copy();
                            monetaryBalance.setAvailBalance(addOnProductOffer.getSubscriptionPrice()*-1);
                        }
                    }

                    if (Collectionz.isNullOrEmpty(subscriberUsages) == false) {
                        addSubscriptionDetailsWithUsage(subscriberIdentity, startTimeMSTS, endTimeMSTS, parentId, addOnSubscription, voltDBClient, subscriberUsages, subscriptionState,addOnProductOffer.getSubscriptionPrice(),monetaryBalanceId);
                    } else {
                        if (getLogger().isDebugLogLevel()) {
                            getLogger().debug(MODULE, "Adding Subscription details only for subscriber ID: " + subscriberIdentity);
                        }
                        addSubscriptionDetails(subscriberIdentity, startTimeMSTS, endTimeMSTS, parentId, addOnSubscription, subscriptionState.getStringVal(),SubscriptionType.ADDON.name(),addOnProductOffer.getSubscriptionPrice(),monetaryBalanceId, voltDBClient);
                    }

                    if(nonNull(balanceEDRListener) && nonNull(monetaryBalance) && nonNull(previousMonetaryBalance)) {
                        balanceEDRListener.updateMonetaryEDR(new SubscriberMonetaryBalanceWrapper(monetaryBalance, previousMonetaryBalance, sprInfo, SUBSCRIBE_ADDON_PRODUCT_OFFER, ActionType.UPDATE.name(),null),
                                requestIpAddress, SUBSCRIBE_ADDON_PRODUCT_OFFER);
                    }

                }

                if (getLogger().isInfoLogLevel()) {
                    getLogger().info(MODULE, "Package(" + addOnProductOffer.getName() + ") subscription successful for subscriber ID: "
                            + subscriberIdentity + " with subscription ID(" + addOnSubscription.getId() + ")");
                }

                subscriptions.add(addOnSubscription);
            }
            return subscriptions;
        } catch (OperationFailedException e) {

            throw new OperationFailedException("Unable to subscribe package(" + addOnProductOffer.getName() + ") for subscriber ID: " + subscriberIdentity
                    + ". Reason: " + e.getMessage(), e.getErrorCode(), e);
        }

    }

    private void addUsageBasedOnLevels(ProductOffer addOnProductOffer, String subscriberIdentity, String productOfferId, Subscription addOnSubscription, List<SubscriberUsage> subscriberUsages, QuotaProfile quotaProfile, Set<String> serviceTosubscriberUsage) {
        for (QuotaProfileDetail quotaProfileDetail : quotaProfile.getHsqLevelServiceWiseQuotaProfileDetails().values()) {
            addUsage(addOnProductOffer, subscriberIdentity, subscriberUsages, quotaProfile, serviceTosubscriberUsage, quotaProfileDetail.getServiceId(), addOnSubscription, productOfferId);
        }

        for (int level = 1; quotaProfile.getServiceWiseQuotaProfileDetails(level) != null; level++) {
            Map<String, QuotaProfileDetail> quotaProfileDetails = quotaProfile.getServiceWiseQuotaProfileDetails(level);

            if (Maps.isNullOrEmpty(quotaProfileDetails)) {
                continue;
            }

            for (QuotaProfileDetail quotaProfileDetail : quotaProfileDetails.values()) {
                addUsage(addOnProductOffer, subscriberIdentity, subscriberUsages, quotaProfile, serviceTosubscriberUsage, quotaProfileDetail.getServiceId(), addOnSubscription, productOfferId);
            }
        }
    }

    private void addUsage(ProductOffer addOnProductOffer, String subscriberIdentity, List<SubscriberUsage> subscriberUsages, QuotaProfile quotaProfile,
                          Set<String> serviceTosubscriberUsage, String serviceId, Subscription addOnSubscription, String productOfferId) {

        if (serviceTosubscriberUsage.add(serviceId) == true) {
            SubscriberUsage.SubscriberUsageBuilder subscriberUsageBuilder = new SubscriberUsage.SubscriberUsageBuilder(SubscriberUsage.NEW_ID, subscriberIdentity, serviceId, quotaProfile
                    .getId(), addOnProductOffer.getDataServicePkgData().getId(), productOfferId)
                    .withSubscriptionId(addOnSubscription.getId());

            if(quotaProfile.getRenewalInterval()!=0 && quotaProfile.getRenewalIntervalUnit()!=null){
                long resetTime = quotaProfile.getRenewalIntervalUnit().addTime(addOnSubscription.getStartTime().getTime(), quotaProfile.getRenewalInterval());

                if(resetTime>addOnSubscription.getEndTime().getTime()){
                    resetTime = addOnSubscription.getEndTime().getTime();
                }
                subscriberUsageBuilder.withBillingCycleResetTime(resetTime);
                subscriberUsageBuilder.withCustomResetTime(resetTime);
            } else {
                subscriberUsageBuilder.withBillingCycleResetTime(addOnSubscription.getEndTime().getTime());
                subscriberUsageBuilder.withCustomResetTime(addOnSubscription.getEndTime().getTime());
            }

            subscriberUsages.add(subscriberUsageBuilder.build());
        }
    }

    private void addSubscriptionDetailsWithUsage(String subscriberIdentity, Timestamp startTimeMSTS, Timestamp endTimeMSTS, String parentId, Subscription addOnSubscription, VoltDBClient voltDBClient, List<SubscriberUsage> subscriberUsages, SubscriptionState subscriptionState,double subscriptionPrice,String monetaryBalanceId)
            throws OperationFailedException {

        try {

            Object addSubscriptionArray = createAddSubscriptionArray(addOnSubscription, parentId, subscriptionState.getStringVal(),SubscriptionType.ADDON.name());
            Object[] usageArgs = createUMInputArray(subscriberUsages);

            long queryExecutionTime = timeSource.currentTimeInMillis();
            voltDBClient.callProcedure(SUBSCRIPTION_ADD_WITH_UM_STORED_PROCEDURE, subscriberIdentity, addSubscriptionArray, startTimeMSTS, endTimeMSTS,subscriptionPrice,monetaryBalanceId, usageArgs[0],
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

            checkForHighResponseTime(queryExecutionTime, ADD_SUBSCRIPTION_DETAILS);

            resetTotalQueryTimeoutCount();
        } catch (IOException e) {
            throw new OperationFailedException("Error while adding subscription details for subscriber: " + subscriberIdentity + ". Reason: " + e.getMessage(), e);
        }catch (ProcCallException e){
            VoltDBUtil.handleProcedureCallException(e, "adding subscription details for subscriber ID: " + subscriberIdentity, MODULE, alertListener, totalQueryTimeoutCount, voltDBClient);
        }
    }

    private Object[] createUMInputArray(List<SubscriberUsage> initialUsages) {

        Object[] usageArgs = new Object[15];

        for (int i = 0; i < initialUsages.size(); i++) {
            String[] usageArray = new String[27];
            usageArray[0] = UUID.randomUUID().toString();
            usageArray[1] = initialUsages.get(i).getSubscriberIdentity();
            usageArray[2] = initialUsages.get(i).getPackageId();
            usageArray[3] = initialUsages.get(i).getSubscriptionId();
            usageArray[4] = initialUsages.get(i).getQuotaProfileId();
            usageArray[5] = initialUsages.get(i).getServiceId();

            usageArray[6] = Long.toString(initialUsages.get(i).getDailyTotal());
            usageArray[7] = Long.toString(initialUsages.get(i).getDailyUpload());
            usageArray[8] = Long.toString(initialUsages.get(i).getDailyDownload());
            usageArray[9] = Long.toString(initialUsages.get(i).getDailyTime());

            usageArray[10] = Long.toString(initialUsages.get(i).getWeeklyTotal());
            usageArray[11] = Long.toString(initialUsages.get(i).getWeeklyUpload());
            usageArray[12] = Long.toString(initialUsages.get(i).getWeeklyDownload());
            usageArray[13] = Long.toString(initialUsages.get(i).getWeeklyTime());

            usageArray[14] = Long.toString(initialUsages.get(i).getBillingCycleTotal());
            usageArray[15] = Long.toString(initialUsages.get(i).getBillingCycleUpload());
            usageArray[16] = Long.toString(initialUsages.get(i).getBillingCycleDownload());
            usageArray[17] = Long.toString(initialUsages.get(i).getBillingCycleTime());

            usageArray[18] = Long.toString(initialUsages.get(i).getCustomTotal());
            usageArray[19] = Long.toString(initialUsages.get(i).getCustomUpload());
            usageArray[20] = Long.toString(initialUsages.get(i).getCustomDownload());
            usageArray[21] = Long.toString(initialUsages.get(i).getCustomTime());

            usageArray[22] = Long.toString(umOperation.getResetTime(timeSource.currentTimeInMillis()));
            usageArray[23] = Long.toString(umOperation.getWeeklyTime(timeSource.currentTimeInMillis()));

            long billingCycleResetTime = initialUsages.get(i).getBillingCycleResetTime();
            if (billingCycleResetTime == 0) {
                billingCycleResetTime = umOperation.getFutureDate();
            }

            long customResetTime = initialUsages.get(i).getCustomResetTime();
            if (customResetTime == 0) {
                customResetTime = umOperation.getFutureDate();
            }
            usageArray[24] = Long.toString(billingCycleResetTime);
            usageArray[25] = Long.toString(customResetTime);
            usageArray[26] = initialUsages.get(i).getProductOfferId();
            usageArgs[i] = usageArray;
        }
        return usageArgs;
    }

    private void addSubscriptionDetails(String subscriberIdentity, Timestamp startTimeMSTS, Timestamp endTimeMSTS, String parentId, Subscription addOnSubscription, String status,String subscriptionType,double subscriptionPrice,String monetaryBalanceId, VoltDBClient voltDBClient)
            throws OperationFailedException {

        try {

            long queryExecutionTime = timeSource.currentTimeInMillis();
            voltDBClient.callProcedure(SUBSCRIPTION_ADD_STORED_PROCEDURE, subscriberIdentity, createAddSubscriptionArray(addOnSubscription, parentId,status,subscriptionType), startTimeMSTS, endTimeMSTS ,subscriptionPrice,monetaryBalanceId);
            queryExecutionTime = timeSource.currentTimeInMillis() - queryExecutionTime;

            checkForHighResponseTime(queryExecutionTime, ADD_SUBSCRIPTION_DETAILS);

            resetTotalQueryTimeoutCount();

        } catch (IOException e) {
            throw new OperationFailedException("Error while adding subscription details for subscriber: " + subscriberIdentity + ". Reason: " + e.getMessage(), e);
        }catch (ProcCallException e){
            VoltDBUtil.handleProcedureCallException(e, "adding subscription details for subscriber ID: " + subscriberIdentity, MODULE, alertListener, totalQueryTimeoutCount, voltDBClient);
        }
    }

    @VisibleForTesting
    String getNextSubscriptionId() {
        return UUID.randomUUID().toString();
    }

    private void resetTotalQueryTimeoutCount() {
        totalQueryTimeoutCount.set(0);
    }

    private Object createAddSubscriptionArray(Subscription addOnSubscription, String parentId, String status,String subscriptionType) {
        String[] addSubscription = new String[11];

        addSubscription[0] = addOnSubscription.getId();
        addSubscription[1] = addOnSubscription.getPackageId();
        addSubscription[2] = addOnSubscription.getProductOfferId();
        addSubscription[3] = parentId;
        addSubscription[4] = addOnSubscription.getSubscriberIdentity();
        addSubscription[5] = CommonConstants.DEFAULT_SERVER_INSTANCE_VALUE;
        addSubscription[6] = status;
        addSubscription[7] = String.valueOf(addOnSubscription.getPriority());
        addSubscription[8] = addOnSubscription.getParameter1();
        addSubscription[9] = addOnSubscription.getParameter2();
        addSubscription[10] = subscriptionType;

        return addSubscription;
     }

    /**
     * IF subscription state is not received THEN take SubscriptionState.STARTED
     *
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

    /*
	 * gives future time (time + validity period according to
	 * validityPeriodUnit)
	 */
    private long addTime(long startTime, int validityPeriod, ValidityPeriodUnit validityPeriodUnit) {
        return validityPeriodUnit.addTime(startTime, validityPeriod);
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
     *   @param sprInfo
     *            , subscriber for whom want to subscribe addon
     * @param parentId
     *            ,
     * @param addonProductOfferName
*            , AddOn package name to be subscribed
     * @param subscriptionStatusValue
*            ,
     * @param startTime
*            , subscription start time
     * @param endTime
     * @param priority
     */
    public List<Subscription> subscribeProductOfferAddOnByName(SPRInfo sprInfo, String parentId, String productOfferId, String addonProductOfferName, Integer subscriptionStatusValue, Long startTime,
                                                         Long endTime, Integer priority, String param1, String param2, VoltDBClient voltDBClient) throws OperationFailedException {

        String subscriberIdentity = sprInfo.getSubscriberIdentity();
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Subscribing addOn(" + addonProductOfferName + ") for subscriber ID: " + subscriberIdentity);
        }

        ProductOffer addOn = policyRepository.getProductOffer().byName(addonProductOfferName);
        if (addOn == null) {
            throw new OperationFailedException("Unable to subscribe addOn(" + addonProductOfferName + ") for subscriber ID: " + subscriberIdentity
                    + ". Reason: AddOn not found for Name: " + addonProductOfferName, ResultCode.NOT_FOUND);
        }

        if (addOn.getPolicyStatus() == PolicyStatus.FAILURE) {
            throw new OperationFailedException("Unable to subscribe addOn(" + addonProductOfferName + ") for subscriber ID: " + subscriberIdentity
                    + ". Reason: AddOn(" + addonProductOfferName + ") is failed addOn", ResultCode.INVALID_INPUT_PARAMETER);
        }

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "AddOn(" + addOn.getName() + ") found");
        }

        return subscribeAddOn(addOn, sprInfo, parentId, productOfferId, subscriptionStatusValue, startTime, endTime, priority, voltDBClient, param1, param2, null,null, null);

    }

    public LinkedHashMap<String, Subscription> getSubscriptions(SPRInfo sprInfo, VoltDBClient voltDBClient) throws OperationFailedException {
        String subscriberIdentity = sprInfo.getSubscriberIdentity();

        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Fetching active subscriptions for subscriber ID: " + subscriberIdentity);
        }

        try {
            long queryExecutionTime = timeSource.currentTimeInMillis();
            ClientResponse clientResponse = voltDBClient.callProcedure(SUBSCRIPTION_SELECT_STORED_PROCEDURE, subscriberIdentity);
            queryExecutionTime = timeSource.currentTimeInMillis() - queryExecutionTime;
            sprInfo.setSubscriptionsLoadTime(queryExecutionTime);
            checkForHighResponseTime(queryExecutionTime, FETCHING_ACTIVE_SUBSCRIPTION);

            long queryReadStartTime = timeSource.currentTimeInMillis();
            LinkedHashMap<String, Subscription> subscriptions = createSubscriptions(subscriberIdentity, clientResponse);
            long queryReadTime = timeSource.currentTimeInMillis() - queryReadStartTime;
            sprInfo.setSubscriptionsReadTime(queryReadTime);

            if ((subscriptions == null || subscriptions.isEmpty()) == false) {
                return subscriptions;
            }
        } catch (IOException e) {
            throw new OperationFailedException("Error while fetching subscriber profile for ID: " + subscriberIdentity + ". Reason: " + e.getMessage(), e);
        } catch (ProcCallException e){
            VoltDBUtil.handleProcedureCallException(e, "fetching subscriber profile for ID: " + subscriberIdentity, MODULE, alertListener, totalQueryTimeoutCount, voltDBClient);
        }
        return new LinkedHashMap<>();
    }

    public LinkedHashMap<String, Subscription> getSubscriptions(String subscriberIdentity, VoltDBClient voltDBClient) throws OperationFailedException {

        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Fetching active subscriptions for subscriber ID: " + subscriberIdentity);
        }
        try {
            long queryExecutionTime = timeSource.currentTimeInMillis();
            ClientResponse clientResponse = voltDBClient.callProcedure(SUBSCRIPTION_SELECT_STORED_PROCEDURE, subscriberIdentity);
            queryExecutionTime = timeSource.currentTimeInMillis() - queryExecutionTime;
            checkForHighResponseTime(queryExecutionTime, FETCHING_ACTIVE_SUBSCRIPTION);
            LinkedHashMap<String, Subscription> subscriptions = createSubscriptions(subscriberIdentity, clientResponse);
            if ((subscriptions == null || subscriptions.isEmpty()) == false) {
                return subscriptions;
            }
        } catch (IOException e) {
            throw new OperationFailedException("Error while fetching subscriber profile for ID: " + subscriberIdentity + ". Reason: " + e.getMessage(), e);
        } catch (ProcCallException e){
            VoltDBUtil.handleProcedureCallException(e, "fetching subscriber profile for ID: " + subscriberIdentity, MODULE, alertListener, totalQueryTimeoutCount, voltDBClient);
        }

        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Active Subscriptions are not exist with subscriber ID: " + subscriberIdentity);
        }

        return new LinkedHashMap<>();
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

    private LinkedHashMap<String, Subscription> createSubscriptions(String subscriberIdentity, ClientResponse clientResponse) {

        VoltTable vt = clientResponse.getResults()[0];

        if (vt.getRowCount() == 0) {
            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Subscriptions does not exist with subscriber ID: " + subscriberIdentity);
            }
            return null;
        }

        LinkedHashMap<String, Subscription> subscriptions = null;

        while (vt.advanceRow()) {
            String packageId = vt.getString("PACKAGE_ID");
            String productOfferId = vt.getString("PRODUCT_OFFER_ID");
            String subscriptionId = vt.getString("SUBSCRIPTION_ID");
            Timestamp startTime = vt.getTimestampAsSqlTimestamp("START_TIME");
            Timestamp endTime = vt.getTimestampAsSqlTimestamp("END_TIME");
            String param1 = vt.getString("PARAM1");
            String param2 = vt.getString("PARAM2");
            String type = vt.getString("TYPE");
            int status = (int) vt.getLong("STATUS");
            int priority = (int) vt.getLong("PRIORITY");
            SubscriptionState state = SubscriptionState.fromValue(status);
            SubscriptionPackage subscribedPackage = policyRepository.getAddOnById(packageId);

            if (subscribedPackage == null) {
                QuotaTopUp quotaTopUp = policyRepository.getQuotaTopUpById(packageId);

                if (quotaTopUp == null) {
                    BoDPackage boDPackage = policyRepository.getBoDPackage().byId(packageId);

                    if (boDPackage == null) {
                        if (getLogger().isErrorLogLevel()) {
                            getLogger().error(MODULE, "Skipping subscriptions: " + subscriptionId
                                    + ", subscriberId: " + subscriberIdentity
                                    + ". Reason: Addon or Quota TopUp or BoD not found for id " + packageId);
                        }
                        continue;
                    }
                }
            }

            if (state == null) {
                getLogger().error(MODULE, "Error while getting active subscriptions:" + subscriptionId
                        + ", id:" + packageId + ", subscriberId:" + subscriberIdentity
                        + ". Reason: Illegal status determined, state: " + status);
                continue;
            }
            if (endTime == null) {
                if (state == SubscriptionState.APPROVAL_PENDING) {
                    if (getLogger().isDebugLogLevel()) {
                        getLogger().debug(MODULE, "Approval pending for subscription:" + subscriptionId
                                + ", id:" + packageId + ", subscriberId:" + subscriberIdentity);
                    }
                } else if (state == SubscriptionState.REJECTED) {
                    if (getLogger().isDebugLogLevel()) {
                        getLogger().debug(MODULE, "Subscription:" + subscriptionId + " rejected for id: "
                                + packageId + ", subscriberId: " + subscriberIdentity);
                    }
                } else {
                    if (getLogger().isWarnLogLevel()) {
                        getLogger().warn(MODULE, "Endtime is not provided for subscription:" + subscriptionId
                                + ", addon:" + packageId + ", subscriberId: " + subscriberIdentity);
                    }

                }
                continue;
            }

            if (endTime.getTime() <= timeSource.currentTimeInMillis()) {
                if (getLogger().isInfoLogLevel()) {
                    getLogger().info(MODULE, "Skipping subscription:" + subscriptionId
                            + ", id:" + packageId + " for subscriberId:" + subscriberIdentity
                            + ". Reason:  subscription is expired on Endtime:" + endTime + " ");
                }
                continue;
            }

            if (state == SubscriptionState.UNSUBSCRIBED) {
                if (getLogger().isLogLevel(LogLevel.INFO))
                    getLogger().info(MODULE, "Skipping subscription:" + subscriptionId
                            + ", id:" + packageId + " for subscriberId:" + subscriberIdentity
                            + ". Reason: Subscription is Unsubscribed");
                continue;
            }

            if(Objects.isNull(type)) {
                if (getLogger().isLogLevel(LogLevel.INFO))
                    getLogger().info(MODULE, "Skipping subscription:" + subscriptionId
                            + ", id:" + productOfferId + " for subscriberId:" + subscriberIdentity
                            + ". Reason: Subscription Type not found from subscription");
                continue;
            }

            if(Objects.isNull(SubscriptionType.fromVal(type))) {
                if (getLogger().isLogLevel(LogLevel.INFO))
                    getLogger().info(MODULE, "Skipping subscription:" + subscriptionId
                            + ", id:" + productOfferId + " for subscriberId:" + subscriberIdentity
                            + ". Reason: Unknown Subscription Type:" + type);

                continue;
            }

            if (subscriptions == null) {
                subscriptions = new LinkedHashMap<>();
            }
            subscriptions.put(subscriptionId, new Subscription(subscriptionId, subscriberIdentity, packageId, productOfferId, startTime, endTime, state, priority, SubscriptionType.fromVal(type), param1, param2));
        }

        return subscriptions;
    }


    Subscription getActiveSubscriptionBySubscriptionId(String subscriberIdentity, String subscriptionId, VoltDBClient voltDBClient) throws OperationFailedException {

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "fetching subscription for subscription ID: " + subscriptionId);
        }
        try {

            long queryExecutionTime = timeSource.currentTimeInMillis();
            ClientResponse clientResponse = voltDBClient.callProcedure(FETCH_SUBSCRIPTION_BY_SUBSCRIPTION_ID, subscriberIdentity, subscriptionId);
            queryExecutionTime = timeSource.currentTimeInMillis() - queryExecutionTime;

            checkForHighResponseTime(queryExecutionTime, " fetching subscription ");

            VoltTable subscriptionResults = clientResponse.getResults()[0];

            if (subscriptionResults.advanceRow()) {
                String subscriberID = subscriptionResults.getString("SUBSCRIBER_ID");
                String packageId = subscriptionResults.getString("PACKAGE_ID");
                String productOfferId = subscriptionResults.getString("PRODUCT_OFFER_ID");
                Timestamp startTime = subscriptionResults.getTimestampAsSqlTimestamp("START_TIME");
                Timestamp endTime = subscriptionResults.getTimestampAsSqlTimestamp("END_TIME");
                int statusValue = (int) subscriptionResults.getLong("STATUS");
                int priority = (int) subscriptionResults.getLong("PRIORITY");
                SubscriptionState subscriptionState = SubscriptionState.fromValue(statusValue);
                String param1 = subscriptionResults.getString("PARAM1");
                String param2 = subscriptionResults.getString("PARAM2");

                Subscription subscription = new Subscription(subscriptionId, subscriberID, packageId, productOfferId, startTime, endTime, subscriptionState, priority, SubscriptionType.ADDON, param1, param2);
                Predicate<Subscription> subscriptionPredicate = createSubscriptionPredicate();

                if (subscriptionPredicate.test(subscription)) {
                    return subscription;
                }
                return null;
            }
        } catch (IOException e) {
            throw new OperationFailedException("Error while fetching subscription for subscriptionID :" + subscriptionId + ". Reason: " + e.getMessage(), e);
        } catch (ProcCallException e){
            VoltDBUtil.handleProcedureCallException(e, "fetching subscription for subscription ID: " + subscriptionId, MODULE, alertListener, totalQueryTimeoutCount, voltDBClient);
        }
        return null;
    }

    private Predicate<Subscription> createSubscriptionPredicate() {
        return new SubscriptionPackageIdPredicate(policyRepository).and(new SubscriptionEndTimePredicate(timeSource)).and(SubscriptionPackagePredicates.STATE_PREDICATE);
    }

    public Subscription updateSubscription(SPRInfo sprInfo, String subscriptionId, Integer subscriptionStatusValue, Long startTime, Long endTime, Integer priority, String rejectReason, VoltDBClient voltDBClient) throws OperationFailedException {

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Updating subscription(" + subscriptionId + ")");
        }

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
            long currentTimeInMillis = timeSource.currentTimeInMillis();
            Subscription subscription = getActiveSubscriptionBySubscriptionId(sprInfo.getSubscriberIdentity(), subscriptionId, voltDBClient);

            if (subscription == null) {
                throw new OperationFailedException("Active subscription not found with ID: " + subscriptionId, ResultCode.NOT_FOUND);
            }

            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "subscription(" + subscriptionId + ") updated successfully");
            }

            if (subscription.getSubscriberIdentity().equals(sprInfo.getSubscriberIdentity()) == false) {
                getLogger().error(MODULE, "Skip updating subscription. Reason: SubscriberId(" + sprInfo.getSubscriberIdentity() + ") " +
                        "and subscriptionId(" + subscription.getId() + ") are not related");
                return null;
            }

            return updateSubscription(sprInfo.getSubscriberIdentity(), subscription, newSubscriptionState, startTime, endTime, priority,rejectReason, currentTimeInMillis,voltDBClient);

        } catch (OperationFailedException e) {
            throw new OperationFailedException("Unable to update subscription for subscription ID: " + subscriptionId
                    + ". Reason: " + e.getMessage(), e.getErrorCode(), e);
        }


    }

    private Subscription updateSubscription(String subscriberId, Subscription subscription, SubscriptionState newSubscriptionState, Long startTime, Long endTime, Integer newPriority, String rejectReason, long currentTimeMS, VoltDBClient voltDBClient) throws OperationFailedException {

        if (SubscriptionState.STARTED != subscription.getStatus()) {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Invalid old subscription status(" + subscription.getStatus() + ")");
            }

            throw new OperationFailedException("Invalid old subscription status(" + subscription.getStatus() + ")", ResultCode.INVALID_INPUT_PARAMETER);
        }
        if (newSubscriptionState == SubscriptionState.STARTED) {
            /*
             *  These checks are added for scenario when either start time or end time change required, so previous value should be maintained.
             */
            if (startTime == null) {
                startTime = subscription.getStartTime().getTime();
            } else if (Numbers.POSITIVE_LONG.apply(startTime) == false) {
                throw new OperationFailedException("Start time(" + startTime + ") should not be negative", ResultCode.INVALID_INPUT_PARAMETER);
            } else {
                if (subscription.isFutureSubscription(currentTimeMS) == false && startTime > currentTimeMS) {
                    throw new OperationFailedException("Start time(" + new Timestamp(startTime).toString() + ") is greater or equal to current time for already activated addOn", ResultCode.INVALID_INPUT_PARAMETER);
                }
            }

            if (endTime == null) {
                endTime = subscription.getEndTime().getTime();
            } else if (Numbers.POSITIVE_LONG.apply(endTime) == false) {
                throw new OperationFailedException("End time(" + endTime + ") should not be negative", ResultCode.INVALID_INPUT_PARAMETER);
            }


            SubscriptionUtil.validateStartTimeAndEndTime(startTime, endTime, currentTimeMS);
            int priority = Objects.nonNull(newPriority) ? newPriority : subscription.getPriority();

            if(priority < 1) {
                throw new OperationFailedException("Invalid priority value:"+ priority +", priority shoudld be greater than 1", ResultCode.INVALID_INPUT_PARAMETER);
            }

            if (changeSubscriptionDetails(subscriberId, subscription.getId(), new Timestamp(startTime), new Timestamp(endTime), newSubscriptionState.state, priority, rejectReason, voltDBClient) == 0) {
                if (getLogger().isWarnLogLevel()) {
                    getLogger().warn(MODULE, "No rows updated for subscription. Please provide valid subscriptionId(" + subscription.getId() + ")");
                }
                return null;
            }



            return new Subscription(subscription.getId(), subscription.getSubscriberIdentity(), subscription.getPackageId() , null
                    , new Timestamp(startTime), new Timestamp(endTime), newSubscriptionState, priority, SubscriptionType.ADDON, subscription.getParameter1(), subscription.getParameter2());
        } else if (newSubscriptionState == SubscriptionState.UNSUBSCRIBED) {
            if (changeSubscriptionDetails(subscriberId, subscription.getId(), subscription.getStartTime(), subscription.getEndTime(), newSubscriptionState.state, subscription.getPriority(), rejectReason, voltDBClient) == 0) {
                if (getLogger().isWarnLogLevel()) {
                    getLogger().warn(MODULE, "No rows updated for subscription. Please provide valid subscriptionId(" + subscription.getId() + ")");
                }
                return null;
            }
            Timestamp startTime2 = startTime == null ? null : new Timestamp(startTime);
            Timestamp endTime2 = endTime == null ? null : new Timestamp(endTime);
            return new Subscription(subscription.getId(), subscription.getSubscriberIdentity(), subscription.getPackageId(),subscription.getProductOfferId()
                    , startTime2, endTime2, newSubscriptionState, subscription.getPriority(), SubscriptionType.ADDON, subscription.getParameter1(), subscription.getParameter2());
        } else {
            throw new OperationFailedException("Invalid subscription status(" + newSubscriptionState + ") received. Old Status: "
                    + subscription.getStatus(), ResultCode.INVALID_INPUT_PARAMETER);
        }

    }


    private int changeSubscriptionDetails(String subscriberId, String subscriptionId, Timestamp startTime, Timestamp endTime, int subscriptionState, int priority, String rejectReason, VoltDBClient voltDBClient) throws OperationFailedException {

        try {
            long queryExecutionTime = timeSource.currentTimeInMillis();
            ClientResponse clientResponse = voltDBClient.callProcedure(UPDATE_SUBSCRIPTION_DETAIL_BY_SUBSCRIPTION_ID, subscriberId, subscriptionId, startTime, endTime,  priority, subscriptionState,rejectReason);
            queryExecutionTime = timeSource.currentTimeInMillis() - queryExecutionTime;

            checkForHighResponseTime(queryExecutionTime, "updating subscription ");

            VoltTable vt = clientResponse.getResults()[0];
            return (int) vt.asScalarLong();


        } catch (IOException e) {
            throw new OperationFailedException("Error while updating subscription for subscriptionID :" + subscriptionId + ". Reason: " + e.getMessage(), e);
        } catch (ProcCallException e){
            VoltDBUtil.handleProcedureCallException(e, "updating subscription for subscription ID: " + subscriptionId, MODULE, alertListener, totalQueryTimeoutCount, voltDBClient);
        }
        return 0;
    }


    public void importSubscriptionsAndUsages(String subscriberIdentity, List<SubscriptionDetail> addOnSubscriptionDetails,
                                             VoltDBClient voltDBClient) throws OperationFailedException {

        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Importing usages and subscriptions for subscriber: " + subscriberIdentity);
        }

        importSubscriptions(subscriberIdentity, addOnSubscriptionDetails, voltDBClient);

        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Usages and subscriptions imported for subscriber: " + subscriberIdentity);
        }
    }


    public void importSubscriptions(String subscriberIdentity, List<SubscriptionDetail> addOnSubscriptionDetails, VoltDBClient voltDBClient) throws OperationFailedException {

        for (SubscriptionDetail addOnSubscriptionDetail : addOnSubscriptionDetails) {

            Subscription existingSubscription = addOnSubscriptionDetail.getAddOnSubscription();

            if (existingSubscription.getSubscriberIdentity().equals(subscriberIdentity) == false) {
                throw new OperationFailedException("Subscriber identity(" + subscriberIdentity + ") provided and subscriber identity("
                        + existingSubscription.getSubscriberIdentity() + ")from subscription does not match", ResultCode.INVALID_INPUT_PARAMETER);
            }

            int validity;
            ValidityPeriodUnit validityPeriodUnit;
            // DO NOT USE getActive method for policy because RETIRED package should considered
            SubscriptionPackage subscriptionPackage = policyRepository.getAddOnById(existingSubscription.getPackageId());

            if (subscriptionPackage == null) {
                QuotaTopUp quotaTopUp = policyRepository.getQuotaTopUpById(existingSubscription.getPackageId());
                if (quotaTopUp == null) {
                    getLogger().warn(MODULE, "Skipping subscription(id:" + existingSubscription.getId() + ") for subscriber id: " + subscriberIdentity
                            + ". Reason: Subscription package not found for id: " + existingSubscription.getId());
                    continue;
                }
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "Skipping subscription to package(" + quotaTopUp.getName() + ") for subscriber ID: " + subscriberIdentity +
                            ". Reason: Quota Top Up not supported.");
                }
                continue;
            }
            if (QuotaProfileType.RnC_BASED == subscriptionPackage.getQuotaProfileType()) {
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "Skipping subscription to  package(" + subscriptionPackage.getName() + ") for subscriber ID: " + subscriberIdentity +
                            ". Reason: Quota profile type " + subscriptionPackage.getQuotaProfileType() + " not supported.");
                }
                continue;
            }
            validity = subscriptionPackage.getValidity();
            validityPeriodUnit = subscriptionPackage.getValidityPeriodUnit();


            // INTENTIONALY not checked fail status

            long currentTimeMS = timeSource.currentTimeInMillis();
            long startTime = existingSubscription.getStartTime() == null ? null : existingSubscription.getStartTime().getTime();
            long endTime = existingSubscription.getEndTime() == null ? null : existingSubscription.getEndTime().getTime();


            startTime = Numbers.POSITIVE_LONG.apply(startTime) ? startTime : currentTimeMS;

            endTime = Numbers.POSITIVE_LONG.apply(endTime) ? endTime
                    : addTime(startTime, validity, validityPeriodUnit);

            String addOnSubscriptionId = Strings.isNullOrBlank(existingSubscription.getId()) ? getNextSubscriptionId() : existingSubscription.getId().trim();
            Subscription newSubscription = new Subscription(addOnSubscriptionId, existingSubscription.getSubscriberIdentity(), subscriptionPackage.getId(),null, new Timestamp(startTime), new Timestamp(endTime),
                    existingSubscription.getStatus(), existingSubscription.getPriority(), SubscriptionType.ADDON, existingSubscription.getParameter1(), existingSubscription.getParameter2());

            List<SubscriberUsage> usages = addOnSubscriptionDetail.getUsages();
            String monetaryBalanceId=null;
            double subscriptionPrice =0;
            if (Collectionz.isNullOrEmpty(usages) == false) {
                for (SubscriberUsage usage : usages) {

                    usage.setSubscriptionId(addOnSubscriptionId);

                    QuotaProfile quotaProfile = subscriptionPackage.getQuotaProfile(usage.getQuotaProfileId());

                    if (quotaProfile != null
                            && quotaProfile.getRenewalInterval() > 0
                            && quotaProfile.getRenewalIntervalUnit() != null) {
                        long resetTime = quotaProfile.getRenewalIntervalUnit().addTime(usage.getBillingCycleTime(), quotaProfile.getRenewalInterval());
                        if (resetTime > endTime) {
                            resetTime = endTime;
                        }
                        usage.setBillingCycleResetTime(resetTime);
                    } else {
                        usage.setBillingCycleResetTime(endTime);
                    }

                }
                addSubscriptionDetailsWithUsage(subscriberIdentity,newSubscription.getStartTime(),newSubscription.getEndTime(),null,newSubscription,voltDBClient,usages,newSubscription.getStatus(),subscriptionPrice,monetaryBalanceId);
            } else {
                addSubscriptionDetails(subscriberIdentity, newSubscription.getStartTime(), newSubscription.getEndTime(), null, newSubscription, newSubscription.getStatus().getStringVal(),SubscriptionType.ADDON.name(),subscriptionPrice,monetaryBalanceId, voltDBClient);
            }
        }
    }

    public void changeBillDay(String subscriberId, Timestamp nextBillDate, Timestamp billChangeDate, VoltDBClient voltDBClient) throws OperationFailedException {
        try {
            long queryExecutionTime = timeSource.currentTimeInMillis();
            voltDBClient.callProcedure(CHANGE_BILL_DAY_BY_SUBSCRIBER_ID, subscriberId, nextBillDate, billChangeDate);
            queryExecutionTime = timeSource.currentTimeInMillis() - queryExecutionTime;

            checkForHighResponseTime(queryExecutionTime, CHANGE_BILL_DAY_DETAILS);

            resetTotalQueryTimeoutCount();


        } catch (IOException e) {
            throw new OperationFailedException("Error while changing bill day for subscriber ID :" + subscriberId + ". Reason: " + e.getMessage(), e);
        } catch (ProcCallException e) {
            VoltDBUtil.handleProcedureCallException(e, "changing bill day for subscriber ID : " + subscriberId, MODULE, alertListener, totalQueryTimeoutCount, voltDBClient);
        }
    }

    public Subscription subscribeBod(BoDPackage subscriptionPackage,
                                     SPRInfo sprInfo,
                                     String parentId,
                                     Integer subscriptionStatusValue,
                                     Long startTime,
                                     Long endTime,
                                     Integer priority,
                                     double subscriptionPrice,
                                     MonetaryBalance monetaryBalance,
                                     String param1,
                                     String param2,
                                     VoltDBClient voltDBClient,
                                     String requestIpAddress)
            throws OperationFailedException {

        String subscriberIdentity = sprInfo.getSubscriberIdentity();
        long currentTimeMS = System.currentTimeMillis();
        if(Objects.nonNull(priority) && priority < 1) {
            throw new OperationFailedException("Unable to subscribe BOD(ID: " + subscriptionPackage.getId() + ", Name: " + subscriptionPackage.getName()
                    + ") for subscriber ID: " + subscriberIdentity
                    + " Reason: Invalid priority:" + priority);
        }

        if(Objects.isNull(priority)) {
            priority = 100;
        }


        startTime = Numbers.POSITIVE_LONG.apply(startTime) ? startTime.longValue() : currentTimeMS;

        endTime = Numbers.POSITIVE_LONG.apply(endTime) ? endTime.longValue() : addTime(startTime, subscriptionPackage.getValidityPeriod(), subscriptionPackage.getValidityPeriodUnit());

        validateStartTimeAndEndTime(startTime, endTime, currentTimeMS);

        try {
            Timestamp endTimeMSTS = new Timestamp(endTime);
            Timestamp startTimeMSTS = new Timestamp(startTime);
            SubscriptionState subscriptionState = getSubscriptionStateFromValue(subscriberIdentity, subscriptionStatusValue);
            String bodSubscriptionId = getNextSubscriptionId() ;
            Subscription bodSubscription = new Subscription(bodSubscriptionId, subscriberIdentity, subscriptionPackage.getId(), null, startTimeMSTS, endTimeMSTS,
                    subscriptionState, priority, SubscriptionType.BOD, param1, param2);

            addSubscriptionDetails(subscriberIdentity, startTimeMSTS, endTimeMSTS, parentId, bodSubscription, subscriptionState.getStringVal(),
                    SubscriptionType.BOD.name(), subscriptionPrice, nonNull(monetaryBalance) ? monetaryBalance.getId() : null, voltDBClient);

            if(subscriptionPrice > 0 && nonNull(monetaryBalance) &&  nonNull(balanceEDRListener)) {
                MonetaryBalance previousMonetaryBalance = monetaryBalance.copy();
                monetaryBalance.setAvailBalance(subscriptionPrice*-1);
                balanceEDRListener.updateMonetaryEDR(new SubscriberMonetaryBalanceWrapper(monetaryBalance, previousMonetaryBalance, sprInfo, SUBSCRIBE_BOD, ActionType.UPDATE.name(),null),
                        requestIpAddress, SUBSCRIBE_BOD);
            }

            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Package(" + subscriptionPackage.getName() + ") subscription successful for subscriber ID: "
                        + subscriberIdentity + " with subscription ID(" + bodSubscription.getId() + ")");
            }

            return bodSubscription;

        } catch (OperationFailedException e) {
            throw new OperationFailedException("Unable to subscribe package(" + subscriptionPackage.getName() + ") for subscriber ID: " + subscriberIdentity
                    + ". Reason: " + e.getMessage(), e.getErrorCode(), e);
        }
    }

}