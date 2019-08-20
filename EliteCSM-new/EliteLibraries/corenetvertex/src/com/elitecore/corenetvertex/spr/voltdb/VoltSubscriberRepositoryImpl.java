package com.elitecore.corenetvertex.spr.voltdb;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Predicate;
import com.elitecore.commons.base.TimeSource;
import com.elitecore.corenetvertex.constants.ActionType;
import com.elitecore.corenetvertex.constants.BillingCycleResetStatus;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.MonetaryBalanceType;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.constants.SubscriberStatus;
import com.elitecore.corenetvertex.constants.SubscriptionState;
import com.elitecore.corenetvertex.core.alerts.AlertConstants;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.alerts.Alerts;
import com.elitecore.corenetvertex.core.db.VoltDBClient;
import com.elitecore.corenetvertex.core.db.exception.DBDownException;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.bod.BoDPackage;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.offer.ProductOfferServicePkgRel;
import com.elitecore.corenetvertex.pm.pkg.datapackage.BasePackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.imspackage.IMSPackage;
import com.elitecore.corenetvertex.pm.rnc.pkg.RnCPackage;
import com.elitecore.corenetvertex.spr.BalanceProvider;
import com.elitecore.corenetvertex.spr.DeleteMarkedSubscriberPredicate;
import com.elitecore.corenetvertex.spr.EDRListener;
import com.elitecore.corenetvertex.spr.MonetaryBalance;
import com.elitecore.corenetvertex.spr.NonMonetoryBalance;
import com.elitecore.corenetvertex.spr.RecordProcessor;
import com.elitecore.corenetvertex.spr.RnCNonMonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberMonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberMonetaryBalanceWrapper;
import com.elitecore.corenetvertex.spr.SubscriberNonMonitoryBalance;
import com.elitecore.corenetvertex.spr.SubscriberRepository;
import com.elitecore.corenetvertex.spr.SubscriberRnCNonMonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberUsage;
import com.elitecore.corenetvertex.spr.SubscriptionNonMonitoryBalance;
import com.elitecore.corenetvertex.spr.SubscriptionProvider;
import com.elitecore.corenetvertex.spr.TestSubscriberAwareSPInterface;
import com.elitecore.corenetvertex.spr.UsageProvider;
import com.elitecore.corenetvertex.spr.balance.SubscriptionInformation;
import com.elitecore.corenetvertex.spr.data.SPRFields;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.data.SubscriberDetails;
import com.elitecore.corenetvertex.spr.data.SubscriberEDRData;
import com.elitecore.corenetvertex.spr.data.SubscriberInfo;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.data.SubscriptionDetail;
import com.elitecore.corenetvertex.spr.data.SubscriptionMetadata;
import com.elitecore.corenetvertex.spr.ddf.SubscriptionParameter;
import com.elitecore.corenetvertex.spr.ddf.SubscriptionResult;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.spr.params.ChangeBaseProductOfferParams;
import com.elitecore.corenetvertex.spr.params.MonetaryRechargeData;
import com.elitecore.corenetvertex.spr.util.SPRInfoUtil;
import com.elitecore.corenetvertex.spr.util.SubscriberPkgValidationUtil;
import com.elitecore.corenetvertex.spr.voltdb.util.VoltDBDateUtil;
import com.elitecore.corenetvertex.spr.voltdb.util.VoltDBUtil;
import com.elitecore.corenetvertex.util.util.Predicates;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.logging.log4j.ThreadContext;
import org.voltdb.VoltTable;
import org.voltdb.client.ClientResponse;
import org.voltdb.client.ProcCallException;

import java.io.IOException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static com.elitecore.commons.base.Strings.isNullOrBlank;
import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.corenetvertex.constants.CommonConstants.ADD_MONETARY_BALANCE;
import static com.elitecore.corenetvertex.constants.CommonConstants.CHANGE_BILL_DAY;
import static com.elitecore.corenetvertex.constants.CommonConstants.CREATE_SUBSCRIBER;
import static com.elitecore.corenetvertex.constants.CommonConstants.DELETE_SUBSCRIBER;
import static com.elitecore.corenetvertex.constants.CommonConstants.IMPORT_SUBSCRIBER;
import static com.elitecore.corenetvertex.constants.CommonConstants.PURGE_SUBSCRIBER;
import static com.elitecore.corenetvertex.constants.CommonConstants.RESTORE_SUBSCRIBER;
import static com.elitecore.corenetvertex.constants.CommonConstants.SUBSCRIBE_BOD;
import static com.elitecore.corenetvertex.constants.CommonConstants.UPDATE_CREDIT_LIMIT;
import static com.elitecore.corenetvertex.constants.CommonConstants.UPDATE_MONETARY_BALANCE;
import static com.elitecore.corenetvertex.constants.CommonConstants.UPDATE_SUBSCRIBER;
import static com.elitecore.corenetvertex.spr.voltdb.VoltSubscriptionOperation.UNSUBSCRIBE_SUBSCRIPTION_BY_SUBSCRIBER_ID;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class VoltSubscriberRepositoryImpl implements SubscriberRepository {

    private static final String MODULE = "VOLT-SUBSCRIBER-REPO";
    private static final int PURGE_INTERNAL_ERROR = -1;
    private final String id;
    private final String name;
    private final TestSubscriberAwareSPInterface spInterface;
    private final PolicyRepository policyRepository;
    private final AlertListener alertListener;
    private final List<String> groupIds;
    private final SPRFields alternateIdField;
    private VoltDBClient voltDBClient;
    private UsageProvider usageProvider;
    private SubscriptionProviderImpl subscriptionProvider;
    private VoltBalanceProviderImpl balanceProvider;
    private VoltUMOperation umOperation;
    private TimeSource timeSource;
    private VoltSubscriptionOperation subscriptionOperation;
    private AtomicInteger totalQueryTimeoutCount;
    private VoltMonetaryABMFOperation monetaryABMFOperation;
    private String systemCurrency;
    private EDRListener subscriptionEDRListener;
    private EDRListener balanceEDRListener;
    private EDRListener subscriberEDRListener;

    public VoltSubscriberRepositoryImpl(TestSubscriberAwareSPInterface spInterface,
                                        String id, String name,
                                        VoltDBClient voltDBClient,
                                        AlertListener alertListener,
                                        PolicyRepository policyRepository,
                                        List<String> groupIds,
                                        SPRFields alternateIdField,
                                        RecordProcessor<VoltMonetaryABMFOperation.MonetaryOperationData> monetaryAbmfRecordProcessor,
                                        VoltUMOperation umOperation,
                                        TimeSource timeSource, String systemCurrency,
                                        EDRListener subscriptionEDRListener,EDRListener balanceEDRListener, EDRListener subscriberEDRListener) {

        this.id = id;
        this.name = name;
        this.voltDBClient = voltDBClient;
        this.policyRepository = policyRepository;
        this.alertListener = alertListener;
        this.groupIds = groupIds;
        this.alternateIdField = alternateIdField;
        this.umOperation = umOperation;
        this.balanceEDRListener = balanceEDRListener;
        this.monetaryABMFOperation = new VoltMonetaryABMFOperation(alertListener, monetaryAbmfRecordProcessor, TimeSource.systemTimeSource(), this.balanceEDRListener);
        this.subscriptionOperation = new VoltSubscriptionOperation(alertListener, policyRepository, timeSource, umOperation, balanceEDRListener);
        this.spInterface = spInterface;
        this.usageProvider = new UsageProviderImpl();
        this.subscriptionProvider = new SubscriptionProviderImpl();
        this.balanceProvider = new VoltBalanceProviderImpl();
        this.timeSource = timeSource;
        this.totalQueryTimeoutCount = new AtomicInteger(0);
        this.systemCurrency = systemCurrency;
        this.subscriptionEDRListener = subscriptionEDRListener;
        this.subscriberEDRListener = subscriberEDRListener;
    }


    @Override
    public void addTestSubscriber(String subscriberIdentity) throws OperationFailedException {
        spInterface.addTestSubscriber(subscriberIdentity);
    }

    @Override
    public int removeTestSubscriber(String subscriberIdentity) throws OperationFailedException {
        return spInterface.removeTestSubscriber(subscriberIdentity, subscriptionProvider);
    }

    @Override
    public int removeTestSubscriber(List<String> subscriberIdentities) throws OperationFailedException {
        return spInterface.removeTestSubscriber(subscriberIdentities, subscriptionProvider);
    }

    @Override
    public boolean isTestSubscriber(String subscriberIdentity) throws OperationFailedException {
        return spInterface.isTestSubscriber(subscriberIdentity);
    }

    @Override
    public Iterator<String> getTestSubscriberIterator() throws OperationFailedException {
        return spInterface.getTestSubscriberIterator();
    }

    @Override
    public void refreshTestSubscriberCache() throws OperationFailedException {
        spInterface.refreshTestSubscriberCache();
    }

    @Override
    public List<SPRInfo> getDeleteMarkedProfiles() throws OperationFailedException {
        List<SPRInfo> deleteMarkedProfiles = spInterface.getDeleteMarkedProfiles();
        if(Collectionz.isNullOrEmpty(deleteMarkedProfiles)){
            return deleteMarkedProfiles;
        }

        for(SPRInfo sprInfo:deleteMarkedProfiles){
            SPRInfoImpl sprInfoImpl = (SPRInfoImpl) sprInfo;
            sprInfoImpl.setUsageProvider(usageProvider);
            sprInfoImpl.setSubscriptionProvider(subscriptionProvider);
            sprInfoImpl.setBalanceProvider(balanceProvider);
        }
        return deleteMarkedProfiles;
    }

    @Override
    public int markForDeleteProfile(String subscriberIdentity, String requestIpAddress) throws OperationFailedException {
        SPRInfo sprInfo = getProfile(subscriberIdentity);
        if(isNull(sprInfo) || SPRInfo.ANONYMOUS.equals(sprInfo.getStatus())){
            throw new OperationFailedException("Subscriber(" + subscriberIdentity + ") not found for delete.", ResultCode.NOT_FOUND);
        }

        int profileDeleted = spInterface.markForDeleteProfile(subscriberIdentity);
        if(profileDeleted > 0){
            subscriberEDRListener.deleteSubscriberEDR(sprInfo, DELETE_SUBSCRIBER, ActionType.UPDATE.name(), requestIpAddress);
        }
        return profileDeleted;
    }

    @Override
    public int restoreProfile(String subscriberIdentity, String requestIpAddress) throws OperationFailedException {
        SPRInfo sprInfo = getProfile(subscriberIdentity, DeleteMarkedSubscriberPredicate.getInstance());
        if(isNull(sprInfo)){
            throw new OperationFailedException("Subscriber(" + subscriberIdentity + ") not found for restore.", ResultCode.NOT_FOUND);
        }
        int profileRestored = spInterface.restoreProfile(subscriberIdentity);
        if(profileRestored > 0){
            subscriberEDRListener.restoreSubscriberEDR(sprInfo, RESTORE_SUBSCRIBER, ActionType.UPDATE.name(), requestIpAddress);
        }
        return profileRestored;
    }

    @Override
    public Map<String, Integer> restoreProfile(List<String> subscriberIdentities) throws OperationFailedException {
        return spInterface.restoreProfile(subscriberIdentities);
    }

    @Override
    public SPRInfo getProfile(String subscriberIdentity) throws OperationFailedException {

        SPRInfoImpl sprInfo;
        try {
            sprInfo = (SPRInfoImpl) spInterface.getProfile(subscriberIdentity);
        } catch (DBDownException e) {
            getLogger().error(MODULE, "Error while fetching profile for subscriber: " + subscriberIdentity + ", Considering as UNAVAILABLE. Reason:" + e.getMessage());
            getLogger().trace(MODULE, e);

            sprInfo =  new SPRInfoImpl();
            sprInfo.setStatus(SPRInfo.UNAVAILABLE);
            sprInfo.setSubscriberIdentity(subscriberIdentity);
            sprInfo.setCui(subscriberIdentity);
        }

        /*
         * sprInfo is null means subscriber not found from database, so creating
         * Unknown profile
         */
        if (sprInfo == null || sprInfo.getStatus().equals(SubscriberStatus.DELETED.name())) {

            if (getLogger().isInfoLogLevel()) {
                if (sprInfo == null) {
                    getLogger().info(MODULE, "Profile not found for Subscriber Identity: " + subscriberIdentity);
                } else {
                    getLogger().info(MODULE, "Profile found with status: " + SubscriberStatus.DELETED.name() + " for Subscriber ID: "
                            + subscriberIdentity);
                }
            }

            sprInfo = new SPRInfoImpl();

            sprInfo.setSubscriberIdentity(subscriberIdentity);
            sprInfo.setStatus(SPRInfo.ANONYMOUS);
            sprInfo.setCui(subscriberIdentity);
            sprInfo.setUnknownUser(true);

            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Considering as unknown user, creating unknown virtual profile for ID: " + subscriberIdentity);
            }
        }

        sprInfo.setSPRGroupIds(groupIds);

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Subscriber profile found: " + sprInfo);
        }

        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Subscriber profile of " + subscriberIdentity + " is fetched successfully");
        }

        sprInfo.setUsageProvider(usageProvider);
        sprInfo.setSubscriptionProvider(subscriptionProvider);
        sprInfo.setBalanceProvider(balanceProvider);
        return sprInfo;

    }

    private class SubscriptionProviderImpl implements SubscriptionProvider {

        @Override
        public LinkedHashMap<String, Subscription> getSubscriptions(SPRInfo sprInfo) throws OperationFailedException {
            return getAddonSubscriptions(sprInfo);
        }

        @Override
        public LinkedHashMap<String, Subscription> getSubscriptions(String subscriberId) throws OperationFailedException {
            return getAddonSubscriptions(subscriberId);
        }
    }

    private class UsageProviderImpl implements UsageProvider {

        @Override
        public Map<String, Map<String, SubscriberUsage>> getCurrentUsage(SPRInfo sprInfo) throws OperationFailedException {
            return VoltSubscriberRepositoryImpl.this.getCurrentUsage(sprInfo);
        }

        @Override
        public void insertNew(String subscriberIdentity, Collection<SubscriberUsage> usages) throws OperationFailedException {
            VoltSubscriberRepositoryImpl.this.insertNew(subscriberIdentity, usages);
        }

        @Override
        public void addToExisting(String subscriberIdentity, Collection<SubscriberUsage> usages) throws OperationFailedException {
            VoltSubscriberRepositoryImpl.this.addToExisting(subscriberIdentity, usages);
        }

        @Override
        public void replace(String subscriberIdentity, Collection<SubscriberUsage> usages) throws OperationFailedException {
            VoltSubscriberRepositoryImpl.this.replace(subscriberIdentity, usages);
        }
    }



    @Override
    public SPRInfo getProfile(String subscriberIdentity, Predicate<SPRInfo> primaryPredicate, Predicate<SPRInfo>... predicates) throws OperationFailedException {
        SPRInfoImpl sprInfo;
        try {
            sprInfo = (SPRInfoImpl) spInterface.getProfile(subscriberIdentity);
        } catch (DBDownException e) {
            throw new OperationFailedException(e, ResultCode.SERVICE_UNAVAILABLE);
        }

        if (sprInfo == null) {

            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "No profile found for Subscriber Identity(" + subscriberIdentity + ")");
            }

            return null;
        }

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Subscriber profile found: " + sprInfo);
        }

        if (primaryPredicate.apply(sprInfo) == false) {
            return null;
        }

        if (predicates != null) {
            for (Predicate<SPRInfo> predicate : predicates) {
                if (predicate.apply(sprInfo) == false) {
                    return null;
                }
            }
        }

        sprInfo.setUsageProvider(usageProvider);
        sprInfo.setSubscriptionProvider(subscriptionProvider);
        sprInfo.setBalanceProvider(balanceProvider);
        return sprInfo;
    }


    @Override
    public void addProfile(SubscriberDetails subscriberDetails,String requestIpAddress) throws OperationFailedException {
        SPRInfo sprInfo = subscriberDetails.getSprInfo();
        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Performing add subscriber operation for subscriber ID: " + sprInfo.getSubscriberIdentity());
        }
        validateDataAndRncAndIMSPkgs(sprInfo);
        validateCreditLimit(subscriberDetails.getCreditLimit());

        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Adding profile with subscriber ID: " + sprInfo.getSubscriberIdentity());
        }

        String productOfferName = sprInfo.getProductOffer();
        ProductOffer productOffer = policyRepository.getProductOffer().byName(productOfferName);

        List<SubscriberUsage> initialUsages = null;
        if(nonNull(productOffer.getDataServicePkgData()) && QuotaProfileType.USAGE_METERING_BASED == productOffer.getDataServicePkgData().getQuotaProfileType()) {
            initialUsages = createUsageEntryForBasePackage(sprInfo.getSubscriberIdentity(), (BasePackage) productOffer.getDataServicePkgData(),productOffer.getId());
        }

        MonetaryBalance monetaryBalance = null;
        if (Objects.nonNull(subscriberDetails.getCreditLimit()) || productOffer.getCreditBalance() != 0.0) {
            monetaryBalance = new MonetaryBalance(UUID.randomUUID().toString()
                    , sprInfo.getSubscriberIdentity(), null
                    //, 0
                    , productOffer.getCreditBalance(), productOffer.getCreditBalance(),
                    0, Objects.nonNull(subscriberDetails.getCreditLimit()) ? subscriberDetails.getCreditLimit() : 0l, 0,System.currentTimeMillis(), CommonConstants.FUTURE_DATE, productOffer.getCurrency()
                    , MonetaryBalanceType.DEFAULT.name(), System.currentTimeMillis(), 0, null, null);

        }
        if (Collectionz.isNullOrEmpty(initialUsages) == false) {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Adding Subscriber profile with initial base package usage for subscriber ID: " + sprInfo.getSubscriberIdentity());
            }
            /**first validate sprInfo related to configured packages
             *    if validate
             *       then perform add profile operations & Add To Test Cache if Subscriber is in Test Mode
             */
            spInterface.validate(sprInfo);
            processSubscriberAddWithBasePackageUsage(sprInfo, initialUsages, monetaryBalance, requestIpAddress);
        } else {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Adding Subscriber profile only, for subscriber ID: " + sprInfo.getSubscriberIdentity());
            }
            if(monetaryBalance==null){
                processSubscriberAdd(sprInfo);
            } else {
                processSubscriberAddWithMonetaryBalance(sprInfo, monetaryBalance);
            }
        }

        if(nonNull(subscriberEDRListener)){
            SubscriberEDRData subscriberEDRData = prepareSubscriberEDRData(sprInfo,CREATE_SUBSCRIBER,ActionType.ADD.name(), requestIpAddress);
            subscriberEDRData.setCurrency(productOffer.getCurrency());
            subscriberEDRListener.subscriberEDR(subscriberEDRData);
        }

        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Add subscriber operation completed for subscriber ID: " + sprInfo.getSubscriberIdentity());
        }
    }

    private SubscriberEDRData prepareSubscriberEDRData(SPRInfo sprInfo, String operation, String action, String requestIpAddress) {
        SubscriberEDRData subscriberEDRData = new SubscriberEDRData();
        // Package currency to be inserted in EDR
        subscriberEDRData.setSprInfo(sprInfo);

        if(isNullOrBlank(operation) && isNullOrBlank(ThreadContext.get("Operation")) == false){
            subscriberEDRData.setOperation(ThreadContext.get("Operation"));
        } else{
            subscriberEDRData.setOperation(operation);
        }

        subscriberEDRData.setAction(action);

        if(isNullOrBlank(requestIpAddress) && isNullOrBlank(ThreadContext.get("IpAddress")) == false){
            subscriberEDRData.setRequestIpAddress(ThreadContext.get("IpAddress"));
        } else{
            subscriberEDRData.setRequestIpAddress(requestIpAddress);
        }

        return subscriberEDRData;
    }

    private boolean validateCreditLimit(Long creditLimit) throws OperationFailedException {
        if(creditLimit == null){
            return true;
        }

        if(creditLimit==Long.MIN_VALUE){
            throw new OperationFailedException("Invalid credit limit value received, value must be non negative integer", ResultCode.INVALID_INPUT_PARAMETER);
        }

        if(creditLimit<0){
            throw new OperationFailedException("Invalid Credit Limit value: "+creditLimit+ ". Value must be positive", ResultCode.INVALID_INPUT_PARAMETER);
        }

        if(creditLimit> CommonConstants.MONETARY_VALUE_LIMIT){
            throw new OperationFailedException("Invalid Credit Limit value: "+creditLimit+ ". Value must not be greater than 999999999", ResultCode.INVALID_INPUT_PARAMETER);
        }
        return false;
    }

    private void addTestSubscriber(SPRInfo sprInfo) throws OperationFailedException {

        if (sprInfo.getSubscriberMode() == SPRInfo.SubscriberMode.TEST) {
            addTestSubscriber(sprInfo.getSubscriberIdentity());
        }
    }

    private void processSubscriberAdd(SPRInfo sprInfo) throws OperationFailedException {

        spInterface.addProfile(sprInfo);
    }

    private void processSubscriberAddWithBasePackageUsage(SPRInfo sprInfo, List<SubscriberUsage> initialUsages, MonetaryBalance monetaryBalance, String requestIpAddress) throws OperationFailedException {
        Object subscriberArgs = VoltDBUtil.createVoltDBArgsForSubscriberProfile(sprInfo);
        Object[] usageArgs = createUMInputArray(initialUsages);
        Object monetaryBalanceArgs = monetaryBalance==null?new Object[0]:VoltDBUtil.createAddMonetaryBalanceArray(monetaryBalance);

        try {

            long queryExecutionTime = timeSource.currentTimeInMillis();

            voltDBClient.callProcedure(VoltDBSPInterface.SUBSCRIBER_ADD_WITH_UM_STORED_PROCEDURE,
                    sprInfo.getSubscriberIdentity(),
                    subscriberArgs,
                    sprInfo.getBirthdate(),
                    sprInfo.getExpiryDate(),
                    monetaryBalanceArgs,
                    monetaryBalance == null ? null : new Timestamp(monetaryBalance.getValidFromDate()),
                    monetaryBalance == null ? null : new Timestamp(monetaryBalance.getValidToDate()),
                    usageArgs[0],
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
                    usageArgs[14],
                    monetaryBalance == null ? null : new Timestamp(monetaryBalance.getCreditLimitUpdateTime()));

            queryExecutionTime = timeSource.currentTimeInMillis() - queryExecutionTime;

            if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

                alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
                        "DB Query execution time is high while adding profile. " +
                                "Last Query execution time: " + queryExecutionTime + " ms");

                if (getLogger().isWarnLogLevel()) {
                    getLogger().warn(MODULE, "DB update query execution time getting high, Last Query execution time was = " +
                            queryExecutionTime + " milliseconds.");
                }
            }

            totalQueryTimeoutCount.set(0);

            addTestSubscriber(sprInfo);

            if(nonNull(balanceEDRListener) && nonNull(monetaryBalance)) {
                balanceEDRListener.addMonetaryEDR(new SubscriberMonetaryBalanceWrapper(monetaryBalance, null, sprInfo, CREATE_SUBSCRIBER, ActionType.ADD.name(),null), requestIpAddress, CREATE_SUBSCRIBER);
            }

            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Profile added successfully with ID: " + sprInfo.getSubscriberIdentity());
            }

        } catch (IOException e) {
            throw new OperationFailedException("Error while adding subscriber profile for ID: " +
                    sprInfo.getSubscriberIdentity() + ". Reason: " + e.getMessage(), e);
        } catch (ProcCallException e){
            VoltDBUtil.handleProcedureCallException(e, "adding subscriber profile for ID: " + sprInfo.getSubscriberIdentity(), MODULE, alertListener, totalQueryTimeoutCount, voltDBClient);
        } catch (OperationFailedException e) {
            throw new OperationFailedException("Error while adding subscriber profile for ID: " +
                    sprInfo.getSubscriberIdentity() + ". Reason: " + e.getMessage(), e.getErrorCode(), e);
        }
    }

    private void processSubscriberAddWithMonetaryBalance(SPRInfo sprInfo, MonetaryBalance monetaryBalance) throws OperationFailedException {
        Object subscriberArgs = VoltDBUtil.createVoltDBArgsForSubscriberProfile(sprInfo);
        Object monearyBalanceArgs = VoltDBUtil.createAddMonetaryBalanceArray(monetaryBalance);

        try {

            long queryExecutionTime = timeSource.currentTimeInMillis();

            voltDBClient.callProcedure(VoltDBSPInterface.SUBSCRIBER_ADD_WITH_MONETARY_BALANCE_STORED_PROCEDURE,
                    sprInfo.getSubscriberIdentity(),
                    subscriberArgs,
                    sprInfo.getBirthdate(),
                    sprInfo.getExpiryDate(),
                    monearyBalanceArgs,
                    new Timestamp(monetaryBalance.getValidFromDate()),
                    new Timestamp(monetaryBalance.getValidToDate()),
                    new Timestamp(monetaryBalance.getCreditLimitUpdateTime()));

            queryExecutionTime = timeSource.currentTimeInMillis() - queryExecutionTime;

            if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

                alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
                        "DB Query execution time is high while adding profile. " +
                                "Last Query execution time: " + queryExecutionTime + " ms");

                if (getLogger().isWarnLogLevel()) {
                    getLogger().warn(MODULE, "DB update query execution time getting high, Last Query execution time was = " +
                            queryExecutionTime + " milliseconds.");
                }
            }

            totalQueryTimeoutCount.set(0);

            addTestSubscriber(sprInfo);

            if(nonNull(balanceEDRListener)){
                balanceEDRListener.addMonetaryEDR(new SubscriberMonetaryBalanceWrapper(monetaryBalance, null, sprInfo, CREATE_SUBSCRIBER,
                        ActionType.ADD.name(),null), null, CREATE_SUBSCRIBER);
            }

            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Profile added successfully with ID: " + sprInfo.getSubscriberIdentity());
            }

        } catch (IOException e) {
            throw new OperationFailedException("Error while adding subscriber profile for ID: " +
                    sprInfo.getSubscriberIdentity() + ". Reason: " + e.getMessage(), e);
        } catch (ProcCallException e){
            VoltDBUtil.handleProcedureCallException(e, "adding subscriber profile for ID: " + sprInfo.getSubscriberIdentity(), MODULE, alertListener, totalQueryTimeoutCount, voltDBClient);
        } catch (OperationFailedException e) {
            throw new OperationFailedException("Error while adding subscriber profile for ID: " +
                    sprInfo.getSubscriberIdentity() + ". Reason: " + e.getMessage(), e.getErrorCode(), e);
        }
    }

    private Object[] createUMInputArray(List<SubscriberUsage> initialUsages) {

        Object[] usageArgs = new Object[15];

        if(isNull(initialUsages)) {
            return usageArgs;
        }

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
            usageArray[26] = initialUsages.get(i).getProductOfferId() + "";
            usageArgs[i] = usageArray;
        }
        return usageArgs;
    }



    private List<SubscriberUsage> createUsageEntryForBasePackage(String subscriberId, BasePackage dataPackage, String productOfferId) {

        if (dataPackage.getQuotaProfileType() != QuotaProfileType.USAGE_METERING_BASED) {
            return Collections.emptyList();
        }

        List<SubscriberUsage> subscriberUsages = new ArrayList<>();
        for (QuotaProfile quotaProfile : dataPackage.getQuotaProfiles()) {
            Set<String> serviceTosubscriberUsage = new HashSet<>(4);
            for (int level = 0; quotaProfile.getServiceWiseQuotaProfileDetails(level) != null; level++) {
                for (QuotaProfileDetail quotaProfileDetail : quotaProfile.getServiceWiseQuotaProfileDetails(level).values()) {
                    addUsage(dataPackage, subscriberId, subscriberUsages, quotaProfile, serviceTosubscriberUsage, quotaProfileDetail.getServiceId(), productOfferId);
                }
            }
        }

        return subscriberUsages;
    }

    private void addUsage(BasePackage basePackage, String subscriberIdentity, List<SubscriberUsage> subscriberUsages, QuotaProfile quotaProfile,
                          Set<String> serviceToSubscriberUsage, String serviceId, String productOfferId) {

        if (serviceToSubscriberUsage.add(serviceId) == true) {
            SubscriberUsage.SubscriberUsageBuilder subscriberUsageBuilder = new SubscriberUsage.SubscriberUsageBuilder(SubscriberUsage.NEW_ID, subscriberIdentity, serviceId, quotaProfile
                    .getId(), basePackage.getId(), productOfferId);

            if(quotaProfile.getRenewalInterval()!=0 && quotaProfile.getRenewalIntervalUnit()!=null){
                subscriberUsageBuilder.withBillingCycleResetTime(quotaProfile.getRenewalIntervalUnit().addTime(System.currentTimeMillis(), quotaProfile.getRenewalInterval()));
                subscriberUsageBuilder.withCustomResetTime(quotaProfile.getRenewalIntervalUnit().addTime(System.currentTimeMillis(), quotaProfile.getRenewalInterval()));
            }

            subscriberUsages.add(subscriberUsageBuilder.build());
        }
    }

    private void validateDataAndRncAndIMSPkgs(SPRInfo sprInfo) throws OperationFailedException {


        String baseProductOfferName = sprInfo.getProductOffer();
        ProductOffer baseProductOffer = policyRepository.getProductOffer().byName(baseProductOfferName);
        SubscriberPkgValidationUtil.validateProductOffer(baseProductOffer, baseProductOfferName, sprInfo.getSubscriberIdentity(), SubscriberPkgValidationUtil.SUBSCRIBER_PROFILE_ADD_OPERATION);

        BasePackage basePkg = (BasePackage) baseProductOffer.getDataServicePkgData();
        if(nonNull(basePkg)) {
            SubscriberPkgValidationUtil.validateBasePackage(basePkg, basePkg.getName(), sprInfo.getSubscriberIdentity(), SubscriberPkgValidationUtil.SUBSCRIBER_PROFILE_ADD_OPERATION);
        }


        String imsPackageName = sprInfo.getImsPackage();
        if (isNullOrBlank(imsPackageName) == false) {
            IMSPackage imsPkg = policyRepository.getIMSPkgByName(imsPackageName);
            SubscriberPkgValidationUtil.validateImsPackage(imsPkg, imsPackageName, sprInfo.getSubscriberIdentity(),SubscriberPkgValidationUtil.SUBSCRIBER_PROFILE_ADD_OPERATION);
        }
    }

    @Override
    public int updateProfile(SPRInfo sprInfo, String requestIpAddress) throws OperationFailedException {
        return updateProfile(sprInfo.getSubscriberIdentity(), createSPRFieldMap(sprInfo), requestIpAddress);
    }

    private EnumMap<SPRFields, String> createSPRFieldMap(SPRInfo sprInfo) {

        EnumMap<SPRFields, String> sprFieldMap = new EnumMap<>(SPRFields.class);

        for (SPRFields sprField : SPRFields.values()) {
            /*
			 * Timestamp value is converted to millisecond
			 */
            if (Types.TIMESTAMP == sprField.type || Types.NUMERIC == sprField.type) {
                sprFieldMap.put(sprField, sprField.getNumericValue(sprInfo) == null ? null : sprField.getNumericValue(sprInfo) + "");
            } else if (Types.VARCHAR == sprField.type) {
                sprFieldMap.put(sprField, sprField.getStringValue(sprInfo));
            }
        }

        return sprFieldMap;
    }

    @Override
    public int updateProfile(String subscriberIdentity, EnumMap<SPRFields, String> updatedProfile, String requestIpAddress) throws OperationFailedException {
        String currentProductOfferName = getProfile(subscriberIdentity).getProductOffer();
        ProductOffer currentProductOffer = policyRepository.getProductOffer().base().byName(currentProductOfferName);
        if (updatedProfile.containsKey(SPRFields.PRODUCT_OFFER)) {
            String baseProductOfferName = updatedProfile.get(SPRFields.PRODUCT_OFFER);
            validateDataPackage(subscriberIdentity, baseProductOfferName, currentProductOffer);
        }

        if (updatedProfile.containsKey(SPRFields.IMS_PACKAGE)) {
            String imsPackageName = updatedProfile.get(SPRFields.IMS_PACKAGE);
            if(isNullOrBlank(imsPackageName) == false){
                SubscriberPkgValidationUtil.validateImsPackage(policyRepository.getIMSPkgByName(imsPackageName),imsPackageName,subscriberIdentity, SubscriberPkgValidationUtil.SUBSCRIBER_PROFILE_UPDATE_OPERATION);
            }

        }

        updatedProfile.put(SPRFields.MODIFIED_DATE,String.valueOf(System.currentTimeMillis()));

        int updateSuccessful = spInterface.updateProfile(subscriberIdentity, updatedProfile);
        if(nonNull(subscriberEDRListener) && updateSuccessful>0){
            SPRInfoImpl sprInfo= SPRInfoUtil.createSPRInfoImpl(updatedProfile,subscriberIdentity);
            SubscriberEDRData subscriberEDRData = prepareSubscriberEDRData(sprInfo,UPDATE_SUBSCRIBER,ActionType.UPDATE.name(), requestIpAddress);
            subscriberEDRData.setCurrency(currentProductOffer.getCurrency());
            subscriberEDRListener.subscriberEDR(subscriberEDRData);
        }

        return updateSuccessful;
    }

    private void validateDataPackage(String subscriberIdentity, String baseProductOfferName, ProductOffer currentProductOffer) throws OperationFailedException {

        if (isNullOrBlank(baseProductOfferName)) {
            throw new OperationFailedException("Subscriber(" + subscriberIdentity + ") update operation failed."
                    + " Reason: subscriber product offer can not set to empty", ResultCode.NOT_FOUND);
        }

        ProductOffer productOffer = policyRepository.getProductOffer().base().byName(baseProductOfferName);
        SubscriberPkgValidationUtil.validateProductOffer(productOffer,baseProductOfferName,subscriberIdentity,SubscriberPkgValidationUtil.SUBSCRIBER_PROFILE_ADD_OPERATION);

        if(nonNull(productOffer.getDataServicePkgData())) {
            BasePackage basePkg = (BasePackage) productOffer.getDataServicePkgData();
            SubscriberPkgValidationUtil.validateBasePackage(basePkg,basePkg.getName(),subscriberIdentity,SubscriberPkgValidationUtil.SUBSCRIBER_PROFILE_ADD_OPERATION);
        }

        if (isTestSubscriber(subscriberIdentity) == false) {
            /*
             * NULL/STATUS check is already done in
             * SubscriberRepositoryImpl.updateProfile.
             * THIS CODE SHOULD BE
             * VERIFY IF ANY UPDATION DONE IN
             * SubscriberRepositoryImpl.updateProfile
             */
            if (PkgMode.TEST == productOffer.getPackageMode()) {
                throw new OperationFailedException("Subscriber update operation failed."
                        + " Reason: Live subscriber must not have test product offer("
                        + baseProductOfferName + ")", ResultCode.OPERATION_NOT_SUPPORTED);
            }
        }


        List<ProductOfferServicePkgRel> productOfferServicePkgRelList = productOffer.getProductOfferServicePkgRelDataList();
        if(Collectionz.isNullOrEmpty(productOfferServicePkgRelList) == false) {
            throw new OperationFailedException("Subscriber(" + subscriberIdentity + ") add operation failed."
                    + " Reason: RnC Package is not supported ", ResultCode.INVALID_INPUT_PARAMETER);
        }

        if(currentProductOffer == null){
            throw new OperationFailedException("Subscriber(" + subscriberIdentity + ") update operation failed."
                    + " Reason: subscribed current base product offer not found", ResultCode.NOT_FOUND);
        }

        if(currentProductOffer.getCurrency().equals(productOffer.getCurrency()) == false){
            throw new OperationFailedException("Subscriber(" + subscriberIdentity + ") update operation failed."
                    + " Reason: New Base product offer(" + baseProductOfferName + ") found with different currency", ResultCode.PRECONDITION_FAILED);
        }

    }

    @Override
    public int purgeProfile(String subscriberIdentity, String requestIpAddress) throws OperationFailedException {
        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Purging subscriber(" + subscriberIdentity + ")");
        }

        int purgeCount;

        purgeCount = purgeSubscriber(subscriberIdentity, requestIpAddress);
        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Subscriber(" + subscriberIdentity + ") purged successfully");
        }

        return purgeCount;
    }

    private int purgeSubscriber(String subscriberIdentity, String requestIpAddress)throws OperationFailedException {

        SPRInfo sprInfo = getProfile(subscriberIdentity, DeleteMarkedSubscriberPredicate.getInstance());
        if(isNull(sprInfo)){
            throw new OperationFailedException("Skipping purge for subscriber(" + subscriberIdentity + "), subscriber not found for purge.", ResultCode.NOT_FOUND);
        }

        Collection<MonetaryBalance> subscriberMonetaryBalances = sprInfo.getMonetaryBalance(Predicates.ALL_MONETARY_BALANCE).getAllBalance();
        int purgeCount = spInterface.purgeProfile(subscriberIdentity);

        if (purgeCount > 0) {
            subscriberEDRListener.deleteSubscriberEDR(sprInfo, PURGE_SUBSCRIBER, ActionType.DELETE.name(), requestIpAddress);
        }

        if(nonNull(balanceEDRListener)) {
            for(MonetaryBalance monetaryBalance : subscriberMonetaryBalances) {
                balanceEDRListener.deleteMonetaryEDR(new SubscriberMonetaryBalanceWrapper(monetaryBalance, null, sprInfo, PURGE_SUBSCRIBER, ActionType.DELETE.name(),null),
                        requestIpAddress, PURGE_SUBSCRIBER);
            }
        }

        return purgeCount;
    }

    @Override
    public Map<String, Integer> purgeProfile(List<String> subscriberIdentities, String requestIpAddress){
        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Subscriber purge multiple operation started");
        }
        Map<String, Integer> subscriberIdToPurgeCountMap = new HashMap<>(subscriberIdentities.size());
        for (String subscriberIdentity : subscriberIdentities) {
            purgeProfileWithBestEffort(subscriberIdToPurgeCountMap, subscriberIdentity, requestIpAddress);
        }
        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Subscriber purge multiple operation completed");
        }
        return subscriberIdToPurgeCountMap;
    }

    private void purgeProfileWithBestEffort(Map<String, Integer> subscriberIdToPurgeCountMap, String subscriberIdentity, String requestIpAddress){
        try {
            subscriberIdToPurgeCountMap.put(subscriberIdentity, purgeSubscriber(subscriberIdentity, requestIpAddress));
            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Subscriber(" + subscriberIdentity + ") purged successfully");
            }
        } catch (OperationFailedException e) {
            printLogSkippingPurgeforSubscriber(subscriberIdentity, e);
            subscriberIdToPurgeCountMap.put(subscriberIdentity, PURGE_INTERNAL_ERROR);
        }
    }

    private void printLogSkippingPurgeforSubscriber(String subscriberIdentity, Exception e) {
        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Purge operation skipped for subscriber(" + subscriberIdentity + "). Reason: " + e.getMessage());
        }
        getLogger().error(MODULE, e.getMessage());
        getLogger().trace(MODULE, e);
    }


    @Override
    public Map<String, Integer> purgeAllProfile(String requestIpAddress) throws OperationFailedException {

        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Subscriber purge all profile operation started");
        }

        Map<String, Integer> subscriberIdToPurgeCountMap = new HashMap<>();

        List<SPRInfo> deleteMarkedProfiles = spInterface.getDeleteMarkedProfiles();

        for (SPRInfo sprInfo : deleteMarkedProfiles) {
            purgeProfileWithBestEffort(subscriberIdToPurgeCountMap, sprInfo.getSubscriberIdentity(), requestIpAddress);
        }

        if(getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Subscriber purge all operation completed");
        }

        return subscriberIdToPurgeCountMap;
    }


    @Override
    public List<Subscription> subscribeAddOnProductOfferById(SPRInfo sprInfo, String parentId,
                                                             String addOnProductOfferId, Integer subscriptionStatusValue,
                                                             Long startTime, Long endTime, Integer priority, String param1,
                                                             String param2, Integer billDay, String requestIpAddress,
                                                             SubscriptionMetadata subscriptionMetadata)
            throws OperationFailedException {
        return subscribeAddOnProductOfferById(sprInfo, parentId, addOnProductOfferId, subscriptionStatusValue,
                startTime, endTime, priority, param1, param2, null, billDay, requestIpAddress, subscriptionMetadata);
    }

    public List<Subscription> subscribeAddOnProductOfferById(SPRInfo sprInfo, String parentId,
                                                             String addOnProductOfferId, Integer subscriptionStatusValue,
                                                             Long startTime, Long endTime, Integer priority, String param1,
                                                             String param2, MonetaryBalance monetaryBalance, Integer billDay,
                                                             String requestIpAddress, SubscriptionMetadata subscriptionMetadata)
            throws OperationFailedException {

        ProductOffer subscriptionPackage = policyRepository.getProductOffer().byId(addOnProductOfferId);

        String subscriberIdentity = sprInfo.getSubscriberIdentity();
        if (subscriptionPackage != null) {
            if (PkgStatus.ACTIVE != subscriptionPackage.getStatus()) {
                throw new OperationFailedException("Unable to subscribe addOn(" + subscriptionPackage.getName() + ") for subscriber ID: " + subscriberIdentity
                        + " Reason: AddOn found with "
                        + subscriptionPackage.getStatus() + " Status", ResultCode.NOT_FOUND);
            }
        }

		/* if LIVE subscriber, not allow to subscriber TEST addon */
        if (spInterface.isTestSubscriber(subscriberIdentity) == false) {
            if (subscriptionPackage != null) {
                if (PkgMode.TEST == subscriptionPackage.getPackageMode()) {
                    throw new OperationFailedException("Unable to subscribe addOn(" + subscriptionPackage.getName() + ") for subscriber ID: " + subscriberIdentity
                            + " Reason: Live subscriber must not have subscription of test addOn", ResultCode.INVALID_INPUT_PARAMETER);
                }
            }
        }

        return subscriptionOperation.subscribeAddOnById(sprInfo, parentId, addOnProductOfferId,subscriptionStatusValue,
                startTime,endTime,priority,param1,param2,monetaryBalance,voltDBClient, requestIpAddress);
    }

    @Override
    public Subscription updateSubscription(SPRInfo sprInfo, String subscriptionId, Integer subscriptionStatusValue, Long startTime, Long endTime, Integer priority, String rejectReason, String param1, String param2, String requestIpAddress) throws OperationFailedException {
        return subscriptionOperation.updateSubscription(sprInfo, subscriptionId, subscriptionStatusValue, startTime, endTime, priority, rejectReason, voltDBClient);
    }

    @Override
    public List<Subscription> subscribeProductOfferAddOnByName(SPRInfo sprInfo, String parentId,
                                                               String addOnProductOfferName,
                                                               Integer subscriptionStatusValue, Long startTime,
                                                               Long endTime, Integer priority, String param1,
                                                               String param2, Integer billDay,
                                                               SubscriptionMetadata subscriptionMetadata)
            throws OperationFailedException {
        ProductOffer addOnProductOffer = policyRepository.getProductOffer().byName(addOnProductOfferName);


        String subscriberIdentity = sprInfo.getSubscriberIdentity();
        if(Objects.isNull(addOnProductOffer)) {
            throw new OperationFailedException("Unable to subscribe product offer(" + addOnProductOfferName + ") for subscriber ID: " + subscriberIdentity
                    + " Reason: Product offer not found", ResultCode.NOT_FOUND);
        }

        if (PkgStatus.ACTIVE != addOnProductOffer.getStatus()) {
            throw new OperationFailedException("Unable to subscribe addOn(" + addOnProductOffer.getName() + ") for subscriber ID: " + subscriberIdentity
                    + " Reason: Product offer with "
                    + addOnProductOffer.getStatus() + " Status", ResultCode.NOT_FOUND);
        }


		/* if LIVE subscriber, not allow to subscriber TEST addon */
        if (spInterface.isTestSubscriber(subscriberIdentity) == false) {
            if (addOnProductOffer != null) {
                if (PkgMode.TEST == addOnProductOffer.getPackageMode()) {
                    throw new OperationFailedException("Unable to subscriber addOn(" + addOnProductOffer.getName() + ")."
                            + " Reason: Live subscriber must not have subscription of test addOn", ResultCode.INVALID_INPUT_PARAMETER);
                }
            }
        }

        return subscriptionOperation
                .subscribeProductOfferAddOnByName(sprInfo, parentId, addOnProductOffer.getId(), addOnProductOfferName, subscriptionStatusValue, startTime, endTime, priority, param1, param2, voltDBClient);
    }

    @Override
    public void replace(String subscriberIdentity, Collection<SubscriberUsage> usages) throws OperationFailedException {
        umOperation.replace(subscriberIdentity, usages, voltDBClient);
    }

    @Override
    public void addToExisting(String subscriberIdentity, Collection<SubscriberUsage> usages) throws OperationFailedException {
        umOperation.addToExisting(subscriberIdentity, usages, voltDBClient);
    }

    @Override
    public void insertNew(String subscriberIdentity, Collection<SubscriberUsage> usages) throws OperationFailedException {
        umOperation.insert(subscriberIdentity, usages, voltDBClient);
    }

    @Override
    public LinkedHashMap<String, Subscription> getAddonSubscriptions(String subscriberIdentity) throws OperationFailedException {
        return subscriptionOperation.getSubscriptions(subscriberIdentity, voltDBClient);
    }


    @Override
    public Subscription getAddonSubscriptionsBySubscriptionId(String subscriberIdentity, String subscriptionId) throws OperationFailedException {
        return subscriptionOperation.getActiveSubscriptionBySubscriptionId(subscriberIdentity,subscriptionId,voltDBClient);
    }

    @Override
    public Map<String, Map<String, SubscriberUsage>> getCurrentUsage(String subscriberIdentity) throws OperationFailedException {
        return umOperation.getUsage(subscriberIdentity,
                getAddonSubscriptions(subscriberIdentity),
                voltDBClient);
    }


    @Override
    public LinkedHashMap<String, Subscription> getAddonSubscriptions(SPRInfo sprInfo) throws OperationFailedException {
        return subscriptionOperation.getSubscriptions(sprInfo, voltDBClient);
    }

    public Map<String, Map<String, SubscriberUsage>> getCurrentUsage(SPRInfo sprInfo) throws OperationFailedException {
        return umOperation.getUsage(sprInfo, voltDBClient);
    }

    public Map<String, Map<String, SubscriberUsage>> getCurrentUsage(String subscriberIdentity,
                                                                     LinkedHashMap<String, Subscription> subscriptions) throws OperationFailedException {
        return umOperation.getUsage(subscriberIdentity, subscriptions, voltDBClient);
    }

    public Map<String, Map<String, SubscriberUsage>> getCurrentUsage(SPRInfo sprInfo,
                                                                     LinkedHashMap<String, Subscription> subscriptions) throws OperationFailedException {
        return umOperation.getUsage(sprInfo.getSubscriberIdentity(), subscriptions, voltDBClient);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<String> getGroupIds() {
        return groupIds;
    }

    @Override
    public SPRFields getAlternateIdField() {
        return alternateIdField;
    }

    @Override
    public void stop() {
        umOperation.stop();
    }

    @Override
    public List<SubscriptionInformation> getBalance(SPRInfo sprInfo, String pkgId, String subscriptionId) throws OperationFailedException {
        return umOperation.getBalance(sprInfo, pkgId, subscriptionId, getAddonSubscriptions(sprInfo.getSubscriberIdentity()), voltDBClient);
    }

    @Override
    public void resetBillingCycle(String subscriberID, String alternateID, String productOfferId, long resetBillingCycleDate, String resetReason, String parameter1, String parameter2, String parameter3) throws OperationFailedException {
        umOperation.resetBillingCycle(subscriberID,alternateID,productOfferId,
                resetBillingCycleDate, resetReason,parameter1,
                parameter2,parameter3, voltDBClient);
    }

    @Override
    public List<SubscriptionInformation> getBalance(SPRInfo sprInfo) throws OperationFailedException {
        return umOperation.getBalance(sprInfo, getAddonSubscriptions(sprInfo.getSubscriberIdentity()), voltDBClient);
    }

    @Override
    public SubscriberNonMonitoryBalance getRGNonMonitoryBalance(SPRInfo sprInfo) throws OperationFailedException {
        return new SubscriberNonMonitoryBalance(null);
    }

    @Override
    public SubscriberRnCNonMonetaryBalance getRnCNonMonetaryBalance(String subscriberIdentity) throws OperationFailedException {
        return new SubscriberRnCNonMonetaryBalance(null);
    }

    @Override
    public SubscriberNonMonitoryBalance getRGNonMonitoryBalanceWithResetExpiredBalance(SPRInfo sprInfo, String pkgId, String subscriptionId) throws OperationFailedException {
        return new SubscriberNonMonitoryBalance(null);

    }

    /**While importing Subscriber following Scenarios will be possible
     * 1. Subscriber
     * 2. Subscriber + Subscription
     * 3. Subscriber + Subscription + Subscription Usage
     * 4. Subscriber + Base Package Usage
     * 5. Subscriber + Base Package Usage + Subscription
     * 6. Subscriber + Base Package Usage + Subscription + Subscription Usage
     *
     * @param subscriberInfo
     * @throws OperationFailedException
     */

    @Override
    public void importSubscriber(SubscriberInfo subscriberInfo) throws OperationFailedException {
        SPRInfo sprInfo = subscriberInfo.getSprInfo();
        String subscriberIdentity = sprInfo.getSubscriberIdentity();
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Importing subscriber with identity: " + subscriberIdentity);
        }

        try {
            validateDataAndRncAndIMSPkgs(sprInfo);

            if (Collectionz.isNullOrEmpty(subscriberInfo.getBasePackageUsages())) {
                //Scenario 1
                if (Collectionz.isNullOrEmpty(subscriberInfo.getAddOnSubscriptionsDetails())) {
                    if (getLogger().isDebugLogLevel()) {
                        getLogger().debug(MODULE, "Adding Subscriber profile only for subscriber ID: " + sprInfo.getSubscriberIdentity());
                    }
                    processSubscriberAdd(sprInfo);
                } else {
                    //Scenario 2 & 3
                    processSubscriberAdd(sprInfo);
                    subscriptionOperation.importSubscriptions(sprInfo.getSubscriberIdentity(), subscriberInfo.getAddOnSubscriptionsDetails(), voltDBClient);
                }
            } else {
                //For Rest of Scenarios from 4,5,6
                spInterface.validate(sprInfo);
                processSubscriberAddWithBasePackageUsage(sprInfo, subscriberInfo.getBasePackageUsages(),null, null);
                subscriptionOperation.importSubscriptions(subscriberIdentity, subscriberInfo.getAddOnSubscriptionsDetails(), voltDBClient);
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "Subscriber imported with identity: " + sprInfo.getSubscriberIdentity());
                }
            }
            List<MonetaryBalance> monetaryBalanceList = subscriberInfo.getMonetaryBalances();
            if (Collectionz.isNullOrEmpty(monetaryBalanceList) == false) {
                for (MonetaryBalance monetaryBalance : monetaryBalanceList) {
                    monetaryBalance.setId(UUID.randomUUID().toString());
                    monetaryBalance.setSubscriberId(subscriberIdentity);
                    getMonetaryABMFOperation().addBalance(subscriberIdentity, monetaryBalance, voltDBClient);
                }
            }
            if (nonNull(subscriberEDRListener)) {
                subscriberEDRListener.addSubscriberEDR(sprInfo, IMPORT_SUBSCRIBER, ActionType.ADD.name(), null);
            }
        } catch(OperationFailedException e){
            /**
             *In case of import operation failed it should flush whole import operation
             * So calling purge subscriber method here
             */
            getLogger().error(MODULE,"Failed to import Subscriber "+subscriberIdentity+ ".Reason: "+e.getMessage());

            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Purging Subscriber: "+subscriberIdentity+ " Reason: All the Subscription not imported successfully ");
            }
            markForDeleteProfile(subscriberIdentity, null);
            purgeProfile(subscriberIdentity, null);
            throw e;
        }
    }

    @Override
    public void importSubscriptions(SPRInfo sprInfo, List<SubscriptionDetail> subscriptionDetails) throws OperationFailedException {

        String subscriberId = sprInfo.getSubscriberIdentity();
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Importing subscriptions of subscriber: " + subscriberId);
        }
        try {
            subscriptionOperation.importSubscriptions(subscriberId, subscriptionDetails,voltDBClient);
        } catch (OperationFailedException e) {
            getLogger().error(MODULE,"Failed to import subscription for subscriber Id: "+subscriberId+" Reason: "+e.getMessage());

            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Unsubscribing all the subscription for subscriber Id: "+subscriberId+" Reason: subscription import operation was failed");
            }
            unsubscribeSubscriptions(subscriberId);
            throw e;
        }
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Subscriptions imported for subscriber: " + subscriberId);
        }

    }

    private void unsubscribeSubscriptions(String subscriberIdentity) throws OperationFailedException{
        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Unsubscribing subscription for subscriber ID: " + subscriberIdentity);
        }

        try {
            long queryExecutionTime = timeSource.currentTimeInMillis();
            ClientResponse clientResponse = voltDBClient.callProcedure(UNSUBSCRIBE_SUBSCRIPTION_BY_SUBSCRIBER_ID, subscriberIdentity,SubscriptionState.UNSUBSCRIBED.state);
            queryExecutionTime = timeSource.currentTimeInMillis() - queryExecutionTime;
            if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {
                alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
                        "DB Query execution time is high while unsubscribing subscription for subscriber . " +
                                "Last Query execution time: " + queryExecutionTime + " ms");
                if (getLogger().isWarnLogLevel()) {
                    getLogger().warn(MODULE, "DB query execution time getting high, Last Query execution time was = " + queryExecutionTime + " milliseconds.");
                }
            }

            totalQueryTimeoutCount.set(0);

            VoltTable vt = clientResponse.getResults()[0];
            long updatedSubscription = vt.asScalarLong();

            if (updatedSubscription > 0) {
                if (getLogger().isInfoLogLevel()) {
                    getLogger().info(MODULE, "Subscription successfully unsubscribed  for subscriber ID: (" + subscriberIdentity + ")");
                }
            } else {
                if (getLogger().isInfoLogLevel()) {
                    getLogger().info(MODULE, "Subscription can't be unsubscribed. Reason: Subscriber(" + subscriberIdentity + ") not found");
                }
            }


        } catch (IOException e) {
            throw new OperationFailedException("Error while unsubscribing subscription subscriber ID: " +subscriberIdentity + ". Reason: " + e.getMessage(), e);
        } catch (ProcCallException e){
            VoltDBUtil.handleProcedureCallException(e, "unsubscribing subscription subscriber ID: " + subscriberIdentity, MODULE, alertListener, totalQueryTimeoutCount, voltDBClient);
        }
    }


    @Override
    public void resetUsage(String subscriberId, String productOfferId) throws OperationFailedException {

        umOperation.resetUsage(subscriberId, productOfferId, voltDBClient);


    }

    @Override
    public int changeBaseProductOffer(ChangeBaseProductOfferParams params, String requestIpAddress) throws OperationFailedException {
        String subscriberIdentity = params.getSubscriberId();
        String newProductOfferName = params.getNewProductOfferName();
        String currentProductOfferId = params.getCurrentProductOfferId();
        String alternateId = params.getAlternateId();
        String param1 = params.getParam1();
        String param2 = params.getParam2();
        String param3 = params.getParam3();

        ProductOffer currentProductOffer = policyRepository.getProductOffer().base().byId(currentProductOfferId);
        validateDataPackage(subscriberIdentity, newProductOfferName, currentProductOffer);
        ProductOffer productOffer = policyRepository.getProductOffer().base().byName(newProductOfferName);
        List<SubscriberUsage> usageTobeProvision = null;
        if(nonNull(productOffer.getDataServicePkgData())) {
            usageTobeProvision = createUsageEntryForBasePackage(subscriberIdentity, (BasePackage) productOffer.getDataServicePkgData(),productOffer.getId());
        }

        try {

            long queryExecutionTime = timeSource.currentTimeInMillis();

            ClientResponse clientResponse;

            Object dataForScheduleDeleteUsage = null;
            Object[] usageArgs = new Object[15];
            Object[] addMonetaryBalance = null;
            Object[] updateMOnetaryBalance = null;

            if (isScheduleUsageDeleteIsRequired(currentProductOfferId) && Collectionz.isNullOrEmpty(usageTobeProvision)) {
                dataForScheduleDeleteUsage = createDataForScheduleDeleteUsage(subscriberIdentity, alternateId, currentProductOfferId,
                        "TO DELETE USAGE", param1, param2, param3);
            }

            if (isScheduleUsageDeleteIsRequired(currentProductOfferId) == false && CollectionUtils.isEmpty(usageTobeProvision) == false) {
                usageArgs = createUMInputArray(usageTobeProvision);
            }

            ProductOffer newProductOffer = policyRepository.getProductOffer().base().byName(newProductOfferName);
            if (Objects.nonNull(newProductOffer.getCreditBalance()) && newProductOffer.getCreditBalance() != 0.0) {
                SubscriberMonetaryBalance existingMonetaryBalance = getMonetaryBalance(subscriberIdentity, Predicates.DEFAULT_MONETARY_BALANCE);

                if(Objects.isNull(existingMonetaryBalance.getMainBalance())) {
                    // add monetary balance
                    addMonetaryBalance = prepareAddMonetaryBalanceParam(subscriberIdentity, newProductOffer);
                } else {
                    // update monetary balance
                    updateMOnetaryBalance = prepareUpdateMonetaryBalanceParam(subscriberIdentity,existingMonetaryBalance, newProductOffer);
                }
            }

            clientResponse = voltDBClient.callProcedure(VoltDBSPInterface.CHANGE_BASE_PRODUCT_OFFER_STORED_PROCEDURE,
                    subscriberIdentity, newProductOfferName, null, dataForScheduleDeleteUsage, null,null,
                    null,null,null, addMonetaryBalance, updateMOnetaryBalance,

                    usageArgs[0],
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
                        "DB Query execution time is high while changin Data Package. " +
                                "Last Query execution time: " + queryExecutionTime + " ms");

                if (getLogger().isWarnLogLevel()) {
                    getLogger().warn(MODULE, "DB update query execution time getting high, Last Query execution time was = " +
                            queryExecutionTime + " milliseconds.");
                }
            }

            totalQueryTimeoutCount.set(0);

            if (clientResponse == null) {
                throw new OperationFailedException("Error while Changing Data Package for Subscriber ID: " +
                        subscriberIdentity + ". Reason: ClientResponse is null");
            }

            if (clientResponse.getResults().length > 0) {
                return (int) clientResponse.getResults()[0].asScalarLong();
            }
        } catch (IOException e) {
            throw new OperationFailedException("Error while Changing Base Package for Subscriber ID: " +
                    subscriberIdentity + ". Reason: " + e.getMessage(), e);
        } catch (ProcCallException e){
            VoltDBUtil.handleProcedureCallException(e, "Changing Base Package for Subscriber ID: " + subscriberIdentity, MODULE, alertListener, totalQueryTimeoutCount, voltDBClient);
        } catch (OperationFailedException e) {
            throw new OperationFailedException("Error while Changing Base Package for Subscriber ID: " +
                    subscriberIdentity + ". Reason: " + e.getMessage(), e.getErrorCode(), e);
        }

        return 0;
    }

    private Object[] prepareAddMonetaryBalanceParam(String subscriberIdentity, ProductOffer newProductOffer) throws OperationFailedException {
        SPRInfo sprInfo = getProfile(subscriberIdentity);
        MonetaryBalance monetaryBalance = new MonetaryBalance(UUID.randomUUID().toString()
                , sprInfo.getSubscriberIdentity(), null
                //, 0
                , newProductOffer.getCreditBalance(), newProductOffer.getCreditBalance(),
                0, 0l, 0, System.currentTimeMillis(), CommonConstants.FUTURE_DATE, systemCurrency
                , MonetaryBalanceType.DEFAULT.name(), System.currentTimeMillis(), 0,null, null);

        Object[] addMonetaryBalanceArray = new Object[16];
        addMonetaryBalanceArray[0] = monetaryBalance.getId();
        addMonetaryBalanceArray[1] = monetaryBalance.getSubscriberId();
        addMonetaryBalanceArray[2] = monetaryBalance.getServiceId();
        addMonetaryBalanceArray[3] = Double.toString(monetaryBalance.getAvailBalance());
        addMonetaryBalanceArray[4] = Double.toString(monetaryBalance.getInitialBalance());
        addMonetaryBalanceArray[5] = Double.toString(monetaryBalance.getTotalReservation());
        addMonetaryBalanceArray[6] = Long.toString(monetaryBalance.getCreditLimit());
        addMonetaryBalanceArray[7] = Long.toString(monetaryBalance.getNextBillingCycleCreditLimit());
        addMonetaryBalanceArray[8] = monetaryBalance.getCurrency();
        addMonetaryBalanceArray[9] = monetaryBalance.getType();
        addMonetaryBalanceArray[10] = monetaryBalance.getParameter1();
        addMonetaryBalanceArray[11] = monetaryBalance.getParameter2();
        addMonetaryBalanceArray[12] = VoltDBDateUtil.convertDate(timeSource.currentTimeInMillis(), VoltDBDateUtil.VOLTDB_DATE_FORMAT);
        addMonetaryBalanceArray[13] = VoltDBDateUtil.convertDate(monetaryBalance.getValidFromDate(), VoltDBDateUtil.VOLTDB_DATE_FORMAT);
        addMonetaryBalanceArray[14] = VoltDBDateUtil.convertDate(monetaryBalance.getValidToDate(), VoltDBDateUtil.VOLTDB_DATE_FORMAT);
        addMonetaryBalanceArray[15] = VoltDBDateUtil.convertDate(monetaryBalance.getCreditLimitUpdateTime(), VoltDBDateUtil.VOLTDB_DATE_FORMAT);
        return addMonetaryBalanceArray;
    }

    private boolean isScheduleUsageDeleteIsRequired(String currentProductOfferId) {
        ProductOffer productOffer = policyRepository.getProductOffer().base().byId(currentProductOfferId);

        if (productOffer.getDataServicePkgData() == null
                || productOffer.getDataServicePkgData().getQuotaProfiles().isEmpty()) {

            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Skipping schedule usage delete for subscriber Id: " + currentProductOfferId + ", product offer: " + productOffer.getName() +
                        ". Reason: Data package quota profile not configured");
            }

            return false;
        }

        return true;
    }

    private Object createDataForScheduleDeleteUsage(String subscriberId, String altenateId, String currentProductOfferId, String resetReason, String parameter1,
                                                    String parameter2, String parameter3) {
        String[] dataForUsageDelete = new String[11];
        dataForUsageDelete[0] = UUID.randomUUID().toString();
        dataForUsageDelete[1] = subscriberId;
        dataForUsageDelete[2] = altenateId;
        dataForUsageDelete[3] = BillingCycleResetStatus.PENDING.getVal();
        dataForUsageDelete[4] = CommonConstants.DEFAULT_SERVER_INSTANCE_VALUE;
        dataForUsageDelete[5] = policyRepository.getProductOffer().base().byId(currentProductOfferId).getDataServicePkgId();
        dataForUsageDelete[6] = resetReason;
        dataForUsageDelete[7] = parameter1;
        dataForUsageDelete[8] = parameter2;
        dataForUsageDelete[9] = parameter3;
        dataForUsageDelete[10] = currentProductOfferId;
        return dataForUsageDelete;
    }

    private Object[] prepareUpdateMonetaryBalanceParam(String subscriberIdentity, SubscriberMonetaryBalance existingMonetaryBalance, ProductOffer newSubscribedProductOffer) {
        MonetaryBalance previousMonetaryBalance = existingMonetaryBalance.getMainBalance();
        MonetaryBalance updatedMonetaryBalance = previousMonetaryBalance.copy();

        if (Objects.nonNull(newSubscribedProductOffer.getCreditBalance())) {
            updatedMonetaryBalance.setAvailBalance(newSubscribedProductOffer.getCreditBalance());
            updatedMonetaryBalance.setInitialBalance(0);
        }

        Object[] updateMonetaryArray = new Object[6];
        updateMonetaryArray[0] = Double.toString(updatedMonetaryBalance.getAvailBalance());
        updateMonetaryArray[1] = Double.toString(updatedMonetaryBalance.getInitialBalance());
        updateMonetaryArray[2] = VoltDBDateUtil.convertDate(updatedMonetaryBalance.getValidToDate(), VoltDBDateUtil.VOLTDB_DATE_FORMAT);
        updateMonetaryArray[3] = VoltDBDateUtil.convertDate(timeSource.currentTimeInMillis(), VoltDBDateUtil.VOLTDB_DATE_FORMAT);
        updateMonetaryArray[4] = subscriberIdentity;
        updateMonetaryArray[5] = updatedMonetaryBalance.getId();

        return updateMonetaryArray;
    }

    @Override
    public int changeIMSPackage(String subscriberIdentity,String newPackageName, String parameter1, String parameter2, String parameter3) throws OperationFailedException {
        validateIMSPackage(subscriberIdentity, newPackageName);
        return spInterface.changeIMSpackage(subscriberIdentity, newPackageName);
    }

    @Override
    public void reserveBalance(String subscriberId, List<NonMonetoryBalance> reservationBalances) throws OperationFailedException {
        throw new UnsupportedOperationException("reserveBalance");
    }

    @Override
    public void reserveRnCBalance(String subscriberId, List<RnCNonMonetaryBalance> reservationBalances) throws OperationFailedException {
        throw new UnsupportedOperationException("reserveRnCBalance");
    }

    @Override
    public void reportBalance(String subscriberIdentity, List<NonMonetoryBalance> serviceWiseNonMonetaryBalances) throws OperationFailedException {
        throw new UnsupportedOperationException("reportBalance");
    }

    @Override
    public void reportRnCBalance(String subscriberIdentity, List<RnCNonMonetaryBalance> serviceWiseNonMonetaryBalances) throws OperationFailedException {
        throw new UnsupportedOperationException("reportRnCBalance");
    }

    @Override
    public void resetBalance(String subscriberIdentity, List<NonMonetoryBalance> serviceWiseNonMonetaryBalances) throws OperationFailedException {
        throw new UnsupportedOperationException("resetBalance");
    }

    @Override
    public void directDebitBalance(String subscriberIdentity, List<NonMonetoryBalance> serviceWiseNonMonetaryBalances) throws OperationFailedException {
        throw new UnsupportedOperationException("directDebitBalance");
    }

    @Override
    public void reportAndReserveBalance(String subscriberIdentity, List<NonMonetoryBalance> serviceWiseNonMonetaryBalances) throws OperationFailedException {
        throw new UnsupportedOperationException("reportAndReserveBalance");
    }

    @Override
    public void reportAndReserveRnCBalance(String subscriberIdentity, List<RnCNonMonetaryBalance> serviceWiseNonMonetaryBalances) throws OperationFailedException {
        throw new UnsupportedOperationException("reportAndReserveRnCBalance");
    }

    @Override
    public void updateNextBillingCycleBalance(Set<Map.Entry<String, NonMonetoryBalance>> nonMonetoryBalances, Integer newBillDay) throws OperationFailedException {
        throw new UnsupportedOperationException("updateNextBillingCycleBalance");
    }


    @Override
    public SubscriberMonetaryBalance getMonetaryBalance(String subscriberId, java.util.function.Predicate<MonetaryBalance> predicate) throws OperationFailedException {
        return monetaryABMFOperation.getMonetaryBalance(subscriberId, predicate, voltDBClient);
    }

    @Override
    public void addMonetaryBalance(String subscriberId, SubscriberMonetaryBalanceWrapper subscriberMonetaryBalanceWrapper
            , String remark, String requestIPAddress) throws OperationFailedException {
        monetaryABMFOperation.addBalance(subscriberId, subscriberMonetaryBalanceWrapper.getCurrentMonetaryBalance(), voltDBClient);
        generateBalanceEDR(subscriberMonetaryBalanceWrapper, subscriberId, null, requestIPAddress, remark);
    }

    @Override
    public void updateMonetaryBalance(String subscriberId, SubscriberMonetaryBalanceWrapper subscriberMonetaryBalanceWrapper, String remark, String requestIPAddress) throws OperationFailedException {
        monetaryABMFOperation.updateBalance(subscriberId, subscriberMonetaryBalanceWrapper.getCurrentMonetaryBalance(), voltDBClient);
        if(nonNull(balanceEDRListener)){
            if(isNull(subscriberMonetaryBalanceWrapper.getSprInfo())){
                subscriberMonetaryBalanceWrapper.setSprInfo(getProfile(subscriberId));
            }
            if(isNullOrBlank(subscriberMonetaryBalanceWrapper.getOperation())){
                subscriberMonetaryBalanceWrapper.setOperation(UPDATE_MONETARY_BALANCE);
            }
            balanceEDRListener.updateMonetaryEDR(subscriberMonetaryBalanceWrapper, requestIPAddress, remark);
        }
    }

    @Override
    public void rechargeMonetaryBalance(MonetaryRechargeData monetaryRechargeData) throws OperationFailedException {
        throw new UnsupportedOperationException("rechargeMonetaryBalance");
    }

    @Override
    public void rechargeMonetaryBalance(MonetaryRechargeData monetaryRechargeData,EnumMap<SPRFields, String> updatedProfile,String subscriberId, String requestIpAddress) throws OperationFailedException {
        monetaryABMFOperation.rechargeMonetaryBalance(monetaryRechargeData,updatedProfile,voltDBClient);

        if (nonNull(balanceEDRListener)) {
            SPRInfoImpl sprInfo = (SPRInfoImpl) getProfile(subscriberId);
            monetaryRechargeData.setSprInfo(sprInfo);
            balanceEDRListener.rechargeMonetaryBalanceEDR(monetaryRechargeData);
        }
        if(nonNull(subscriberEDRListener) && MapUtils.isNotEmpty(updatedProfile)){
            subscriberEDRListener.updateSubscriberEDR(updatedProfile, subscriberId,
                    (isNullOrBlank(monetaryRechargeData.getOperation())==false) ? monetaryRechargeData.getOperation() : UPDATE_SUBSCRIBER,
                    ActionType.UPDATE.name(), requestIpAddress);
        }
    }


    @Override
    public void addMonetaryBalance(String subscriberId, SubscriberMonetaryBalanceWrapper subscriberMonetaryBalanceWrapper
            , String remark, String requestIPAddress,EnumMap<SPRFields, String> updatedProfile) throws OperationFailedException{
        addMonetaryBalance(subscriberId, subscriberMonetaryBalanceWrapper, remark, requestIPAddress, updatedProfile, null);
    }

    @Override
    public void addMonetaryBalance(String subscriberId, SubscriberMonetaryBalanceWrapper subscriberMonetaryBalanceWrapper,
                                   String remark, String requestIPAddress, EnumMap<SPRFields, String> updatedProfile,
                                   String monetaryRechargePlanName) throws OperationFailedException {
        monetaryABMFOperation.addMonetaryBalance(subscriberId, subscriberMonetaryBalanceWrapper.getCurrentMonetaryBalance()
                , remark, requestIPAddress, updatedProfile, voltDBClient);
        generateBalanceEDR(subscriberMonetaryBalanceWrapper, subscriberId, monetaryRechargePlanName, requestIPAddress, remark);
        if (nonNull(subscriberEDRListener) && MapUtils.isNotEmpty(updatedProfile)) {
            subscriberEDRListener.updateSubscriberEDR(updatedProfile, subscriberId,
                    nonNull(subscriberMonetaryBalanceWrapper.getOperation()) ? subscriberMonetaryBalanceWrapper.getOperation() : UPDATE_SUBSCRIBER,
                    ActionType.UPDATE.name(), requestIPAddress);
        }
    }

    private void generateBalanceEDR(SubscriberMonetaryBalanceWrapper subscriberMonetaryBalanceWrapper, String subscriberId, String monetaryRechargePlanName,
                                    String requestIpAddress, String remark) throws OperationFailedException {
        if(nonNull(balanceEDRListener)){
            if(isNull(subscriberMonetaryBalanceWrapper.getSprInfo())){
                subscriberMonetaryBalanceWrapper.setSprInfo(getProfile(subscriberId));
            }
            if(isNullOrBlank(subscriberMonetaryBalanceWrapper.getOperation())){
                subscriberMonetaryBalanceWrapper.setOperation(ADD_MONETARY_BALANCE);
            }
            balanceEDRListener.addMonetaryEDR(subscriberMonetaryBalanceWrapper,monetaryRechargePlanName, requestIpAddress, remark);
        }
    }

    @Override
    public void updateCreditLimit(String subscriberId, SubscriberMonetaryBalanceWrapper subscriberMonetaryBalanceWrapper
            , String remark, String requestIPAddress) throws OperationFailedException {
        monetaryABMFOperation.updateCreditLimit(subscriberId, subscriberMonetaryBalanceWrapper.getCurrentMonetaryBalance(), voltDBClient);
        if(nonNull(balanceEDRListener)){
            if(isNull(subscriberMonetaryBalanceWrapper.getSprInfo())){
                subscriberMonetaryBalanceWrapper.setSprInfo(getProfile(subscriberId));
            }
            if(isNullOrBlank(subscriberMonetaryBalanceWrapper.getOperation())){
                subscriberMonetaryBalanceWrapper.setOperation(UPDATE_CREDIT_LIMIT);
            }
            balanceEDRListener.updateMonetaryEDR(subscriberMonetaryBalanceWrapper, requestIPAddress, remark);
        }
    }

    @Override
    public void reserveMonetaryBalance(String subscriberId, List<MonetaryBalance> lstmonetaryBalance) throws OperationFailedException {
        monetaryABMFOperation.reserveBalance(subscriberId, lstmonetaryBalance, voltDBClient);
    }

    @Override
    public void directDebitMonetaryBalance(String subscriberId, List<MonetaryBalance> lstmonetaryBalance) throws OperationFailedException {
        monetaryABMFOperation.directDebitBalance(subscriberId, lstmonetaryBalance, voltDBClient);
    }

    @Override
    public void reportAndReserveMonetaryBalance(String subscriberId, List<MonetaryBalance> lstmonetaryBalance) throws OperationFailedException {
        monetaryABMFOperation.reportAndReserveBalance(subscriberId, lstmonetaryBalance, voltDBClient);
    }

    @Override
    public Subscription subscribeQuotaTopUpByName(String subscriberIdentity, String parentId, String quotaTopUpName, Integer subscriptionStatusValue, Long startTime, Long endTime, Integer priority,double subscriptionPrice ,String monetaryBalanceId,String param1, String param2) throws OperationFailedException {
        throw new UnsupportedOperationException("subscribeQuotaTopUpByName");
    }

    @Override
    public Subscription subscribeQuotaTopUpById(String subscriberIdentity, String parentId, String topUpId, Integer subscriptionStatusValue, Long startTime, Long endTime, Integer priority,double subscriptionPrice ,String monetaryBalanceId, String param1, String param2) throws OperationFailedException {
        throw new UnsupportedOperationException("subscribeQuotaTopUpById");
    }

    @Override
    public Subscription getTopUpSubscriptionsBySubscriptionId(String subscriptionId) throws OperationFailedException {
        throw new UnsupportedOperationException("getTopUpSubscriptionsBySubscriptionId");
    }

    @Override
    public Subscription updateQuotaTopUpSubscription(String subscriberIdentity, String subscriptionId, Integer subscriptionStatusValue, Long startTime, Long endTime, Integer priority, String rejectReason) throws OperationFailedException {
        throw new UnsupportedOperationException("updateQuotaTopUpSubscription");
    }

    @Override
    public LinkedHashMap<String, Subscription> getTopUpSubscriptions(String subscriberIdentity) throws OperationFailedException {
        return new LinkedHashMap<>();
    }

    @Override
    public LinkedHashMap<String, Subscription> getBodSubscriptions(String subscriberIdentity) throws OperationFailedException {
        return subscriptionOperation.getSubscriptions(subscriberIdentity,voltDBClient);
    }

    @Override
    public SubscriberRnCNonMonetaryBalance getNonMonetaryBalanceForRnCPackage(SPRInfo sprInfo, String pkgId, String subscriptionId) throws OperationFailedException {
        return new SubscriberRnCNonMonetaryBalance(null);
    }

    @Override
    public void resetQuota(String subscriberId, String productOfferId) throws OperationFailedException {
        throw new UnsupportedOperationException("ResetQuota operation is not supported in VoltDB SPR: " + getName());
    }

    @Override
    public SubscriptionResult subscribeAddOnProductOfferById(SubscriptionParameter subscriptionParameter, String requestIpAddress) throws OperationFailedException {
        List<Subscription> subscriptions = subscribeAddOnProductOfferById(subscriptionParameter.getSprInfo(),subscriptionParameter.getParentId(), subscriptionParameter.getProductOfferId(),subscriptionParameter.getSubscriptionStatusValue(),
                subscriptionParameter.getStartTime(),subscriptionParameter.getEndTime(),subscriptionParameter.getPriority(), subscriptionParameter.getParam1(), subscriptionParameter.getParam2(),subscriptionParameter.getMonetaryBalance(),
                subscriptionParameter.getBillDay(),requestIpAddress,subscriptionParameter.getMetadata());

        SubscriptionResult subscriptionResult = new SubscriptionResult();
        subscriptionResult.setSubscriptions(subscriptions);

        return subscriptionResult;
    }

    @Override
    public SubscriptionResult autoSubscribeAddOnProductOfferById(SubscriptionParameter subscriptionParameter) throws OperationFailedException {
        throw new UnsupportedOperationException("Auto Subscription Not supported in VoltDB SPR: " + getName());
    }

    @Override
    public void resetBalance(SPRInfo sprInfo, String productOfferId, RnCPackage rnCPackage) throws OperationFailedException {
        throw new UnsupportedOperationException("ResetBalance operation is not supported in VoltDB SPR: " + getName());
    }

    @Override
    public void refundRnCBalance(String subscriberIdentity, List<RnCNonMonetaryBalance> refundNonMonetaryBalances) throws OperationFailedException {
        throw new UnsupportedOperationException("Refund RnC Balance Not supported in VoltDB SPR: " + getName());
    }

    @Override
    public void changeBillDay(String subscriberId, Timestamp nextBillDate, Timestamp billChangeDate, String requestIpAddress) throws OperationFailedException {
        subscriptionOperation.changeBillDay(subscriberId, nextBillDate, billChangeDate, voltDBClient);
        if (nonNull(subscriberEDRListener)) {
            subscriberEDRListener.changeBillDayEDR(getProfile(subscriberId), nextBillDate, billChangeDate, CHANGE_BILL_DAY, ActionType.UPDATE.name(), requestIpAddress);
        }
    }

    @Override
    public SubscriptionNonMonitoryBalance addDataRnCBalance(String subscriberIdentity, Subscription subscription, ProductOffer productOffer) throws OperationFailedException {
        throw new UnsupportedOperationException("Add Data RnC Balance Not supported in VoltDB SPR: " + getName());
    }

    @Override
    public Subscription subscribeBoDPackage(SubscriptionParameter subscriptionParameter, String requestIpAddress) throws OperationFailedException {
        BoDPackage boDPackage;

        if(nonNull(subscriptionParameter.getBodPackageId())) {
            boDPackage = policyRepository.getBoDPackage().byId(subscriptionParameter.getBodPackageId());
        } else{
            boDPackage = policyRepository.getBoDPackage().byName(subscriptionParameter.getBodPackageName());
        }

        String subscriberIdentity = subscriptionParameter.getSprInfo().getSubscriberIdentity();
        validateBoDPackage(subscriptionParameter, boDPackage, subscriberIdentity);

        Subscription subscription =  subscriptionOperation.subscribeBod(boDPackage,
                subscriptionParameter.getSprInfo(),
                subscriptionParameter.getParentId(),
                subscriptionParameter.getSubscriptionStatusValue(),
                subscriptionParameter.getStartTime(),
                subscriptionParameter.getEndTime(),
                subscriptionParameter.getPriority(),
                subscriptionParameter.getSubscriptionPrice(),
                subscriptionParameter.getMonetaryBalance(),
                subscriptionParameter.getParam1(),
                subscriptionParameter.getParam2(),
                voltDBClient,
                requestIpAddress);

        SubscriptionResult subscriptionResult = new SubscriptionResult();
        subscriptionResult.addSubscription(subscription);

        if(nonNull(subscriptionEDRListener)){
            subscriptionEDRListener.addSubscriptionEDR(subscriptionParameter.getSprInfo(), subscriptionResult, SUBSCRIBE_BOD, requestIpAddress);
        }

        return subscription;
    }

    @Override
    public Subscription updateFnFGroup(SPRInfo sprInfo, Subscription subscription, String requestIpAddress) throws OperationFailedException {
        throw new UnsupportedOperationException("FnF group operations are not supported in VoltDB SPR: " + getName());
    }

    @Override
    public void processReset(SPRInfo sprInfo, String requestIpAddress) throws OperationFailedException {

        ProductOffer productOffer = policyRepository.getProductOffer().byName(sprInfo.getProductOffer());

        // reset rnc balance
        if(Objects.nonNull(productOffer.getProductOfferServicePkgRelDataList()) && productOffer.getProductOfferServicePkgRelDataList().isEmpty() == false) {
            throw new UnsupportedOperationException("Reset RnC Balance operation is not supported in VoltDB SPR: " + getName());
        }

        // reset data balance and Add/Update Monetary Balance
        if(productOffer.getDataServicePkgData() != null) {
            UserPackage userPackage = productOffer.getDataServicePkgData();

            if (userPackage == null) {
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "Skipping Usage/Quota reset. Reason: Data package not configured in product offer: " + productOffer.getName());
                }
            } else {
                if (QuotaProfileType.USAGE_METERING_BASED == userPackage.getQuotaProfileType()) {
                    resetUsageAndAddMonetaryBalance(sprInfo, productOffer, voltDBClient);
                } else if (QuotaProfileType.RnC_BASED == userPackage.getQuotaProfileType()) {
                    throw new UnsupportedOperationException("ResetQuota operation is not supported in VoltDB SPR: " + getName());
                }
            }
        }
    }

    private void resetUsageAndAddMonetaryBalance(SPRInfo sprInfo, ProductOffer productOffer, VoltDBClient voltDBClient) throws OperationFailedException {
        try {
            String subscriberID = sprInfo.getSubscriberIdentity();
            long queryExecutionTime = timeSource.currentTimeInMillis();
            String productOfferID = productOffer.getId();

            ClientResponse clientResponse;

            Object[] addMonetaryBalance = null;
            Object[] updateMOnetaryBalance = null;
            UserPackage userPackage = productOffer.getDataServicePkgData();

            List<QuotaProfile> quotaProfiles = userPackage.getQuotaProfiles();
            Object[] usageResetArray = umOperation.createUsageArrayForResetUsage(quotaProfiles);

            if (Objects.nonNull(productOffer.getCreditBalance()) && productOffer.getCreditBalance() != 0.0) {
                SubscriberMonetaryBalance existingMonetaryBalance = getMonetaryBalance(subscriberID, Predicates.DEFAULT_MONETARY_BALANCE);

                if(Objects.isNull(existingMonetaryBalance.getMainBalance())) {
                    // add monetary balance
                    addMonetaryBalance = prepareAddMonetaryBalanceParam(subscriberID, productOffer);
                } else {
                    // update monetary balance
                    updateMOnetaryBalance = prepareUpdateMonetaryBalanceParam(subscriberID,existingMonetaryBalance, productOffer);
                }
            }

            clientResponse = voltDBClient.callProcedure(VoltDBSPInterface.CHANGE_BASE_PRODUCT_OFFER_STORED_PROCEDURE,
                    subscriberID, null, null, null,
                    usageResetArray[0],usageResetArray[1],usageResetArray[2], productOfferID, UUID.randomUUID().toString(),
                    addMonetaryBalance, updateMOnetaryBalance,
                    null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);

            queryExecutionTime = timeSource.currentTimeInMillis() - queryExecutionTime;

            if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

                alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
                        "DB Query execution time is high while changing Base Package. " +
                                "Last Query execution time: " + queryExecutionTime + " ms");

                if (getLogger().isWarnLogLevel()) {
                    getLogger().warn(MODULE, "DB update query execution time getting high, Last Query execution time was = " +
                            queryExecutionTime + " milliseconds.");
                }
            }

            totalQueryTimeoutCount.set(0);

            if (clientResponse == null) {
                throw new OperationFailedException("Error while Changing Data Package for Subscriber ID: " +
                        sprInfo + ". Reason: ClientResponse is null");
            }

            if (clientResponse.getResults().length > 0) {
                getLogger().info(MODULE, "Subscriber "+sprInfo+" Base Product offer reset successfully");
            }

        } catch (IOException e) {
            throw new OperationFailedException("Error while Changing Base Package for Subscriber ID: " +
                    sprInfo + ". Reason: " + e.getMessage(), e);
        } catch (ProcCallException e){
            VoltDBUtil.handleProcedureCallException(e, "Changing Base Package for Subscriber ID: " + sprInfo, MODULE, alertListener, totalQueryTimeoutCount, voltDBClient);
        } catch (OperationFailedException e) {
            throw new OperationFailedException("Error while Changing Base Package for Subscriber ID: " +
                    sprInfo + ". Reason: " + e.getMessage(), e.getErrorCode(), e);
        }
    }

    private void validateBoDPackage(SubscriptionParameter subscriptionParameter, BoDPackage boDPackage, String subscriberIdentity) throws OperationFailedException {
        if (isNull(boDPackage)) {
            throw new OperationFailedException("Unable to subscribe BoD package(" + subscriptionParameter.getBodPackageId() + ") for subscriber ID: " + subscriberIdentity
                    + ". Reason: BoD package not found for ID: " + subscriptionParameter.getBodPackageId(), ResultCode.NOT_FOUND);
        }

        if (Objects.equals(PolicyStatus.FAILURE, boDPackage.getPolicyStatus())) {
            throw new OperationFailedException("Unable to subscribe BoD package(" + subscriptionParameter.getBodPackageId() + ") for subscriber ID: " + subscriberIdentity
                    + ". Reason: BoD package (" + boDPackage.getName() + ") is failed BoD package", ResultCode.INVALID_INPUT_PARAMETER);
        }

        if (Objects.equals(PkgStatus.ACTIVE, boDPackage.getPkgStatus()) == false) {
            throw new OperationFailedException("Unable to subscribe BoD(" + boDPackage.getName() + ") for subscriber ID: " + subscriberIdentity
                    + " Reason: BoD package found with "
                    + boDPackage.getPkgStatus() + " Status", ResultCode.INVALID_INPUT_PARAMETER);
        }

        if (spInterface.isTestSubscriber(subscriberIdentity) == false && boDPackage != null && PkgMode.TEST == boDPackage.getPackageMode()) {
            throw new OperationFailedException("Unable to subscribe BoD package(" + boDPackage.getName() + ") for subscriber ID: " + subscriberIdentity + " Reason: Live subscriber must not have subscription of test BoD", ResultCode.INVALID_INPUT_PARAMETER);
        }

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "BoD Package(" + boDPackage.getName() + ") found for ID: " + boDPackage.getId());
        }
    }

    private class VoltBalanceProviderImpl implements BalanceProvider {

        @Override
        public SubscriberMonetaryBalance getMonetaryBalance(String subscriberId, java.util.function.Predicate<MonetaryBalance> predicate) throws OperationFailedException {
            return VoltSubscriberRepositoryImpl.this.getMonetaryBalance(subscriberId, predicate);
        }

        @Override
        public SubscriberNonMonitoryBalance getNonMonetaryBalance(SPRInfo sprInfo) throws OperationFailedException {
            return VoltSubscriberRepositoryImpl.this.getRGNonMonitoryBalance(sprInfo);
        }

        @Override
        public SubscriberRnCNonMonetaryBalance getRnCNonMonetaryBalance(String subscriberId) throws OperationFailedException {
            return VoltSubscriberRepositoryImpl.this.getRnCNonMonetaryBalance(subscriberId);
        }
    }

    private void validateIMSPackage(String subscriberIdentity,
                                    String imsPackageName) throws OperationFailedException {
        if (isNullOrBlank(imsPackageName) == false) {
            IMSPackage imsPkg = policyRepository.getIMSPkgByName(imsPackageName);
            if (imsPkg == null) {
                throw new OperationFailedException("Subscriber(" + subscriberIdentity + ") update operation failed."
                        + " Reason: subscribed IMS package(" + imsPackageName + ") not found", ResultCode.NOT_FOUND);
            }

            if (imsPkg.getStatus() == PolicyStatus.FAILURE) {
                throw new OperationFailedException("Subscriber(" + subscriberIdentity + ") update operation failed."
                        + " Reason: IMS Base package(" + imsPackageName + ")  is failed base package", ResultCode.INVALID_INPUT_PARAMETER);
            }

            if (PkgStatus.ACTIVE !=  imsPkg.getAvailabilityStatus()) {
                throw new OperationFailedException("Subscriber(" + subscriberIdentity + ") update operation failed."
                        + " Reason: subscribed IMS package(" + imsPackageName + ")  found with "
                        + imsPkg.getAvailabilityStatus() + " Status", ResultCode.NOT_FOUND);
            }
        }
    }

    @Override
    public SPRInfo createAnonymousProfile(String subscriberIdentity) {
        SPRInfoImpl sprInfo = new SPRInfoImpl();
        sprInfo.setSubscriberIdentity(subscriberIdentity);
        sprInfo.setStatus(SPRInfo.ANONYMOUS);
        sprInfo.setCui(subscriberIdentity);
        sprInfo.setUnknownUser(true);
        sprInfo.setSPRGroupIds(groupIds);
        sprInfo.setUsageProvider(usageProvider);
        sprInfo.setSubscriptionProvider(subscriptionProvider);
        sprInfo.setBalanceProvider(balanceProvider);

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Considering as unknown user, creating unknown virtual profile for ID: " + subscriberIdentity);
        }
        return sprInfo;
    }

    public VoltMonetaryABMFOperation getMonetaryABMFOperation() {
        return monetaryABMFOperation;
    }
}