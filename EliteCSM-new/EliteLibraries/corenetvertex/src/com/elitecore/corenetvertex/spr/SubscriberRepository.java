package com.elitecore.corenetvertex.spr;

import com.elitecore.commons.base.Predicate;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.rnc.pkg.RnCPackage;
import com.elitecore.corenetvertex.spr.balance.SubscriptionInformation;
import com.elitecore.corenetvertex.spr.data.*;
import com.elitecore.corenetvertex.spr.ddf.SubscriptionParameter;
import com.elitecore.corenetvertex.spr.ddf.SubscriptionResult;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.params.ChangeBaseProductOfferParams;
import com.elitecore.corenetvertex.spr.params.MonetaryRechargeData;

import java.sql.Timestamp;
import java.util.*;

public interface SubscriberRepository {


	/**
	 * Provision subscriber identity as Test Subscriber
	 *
	 * @param subscriberIdentity
	 * @throws OperationFailedException
	 */
	void addTestSubscriber(String subscriberIdentity) throws OperationFailedException;

	int removeTestSubscriber(String subscriberIdentity) throws OperationFailedException;

	int removeTestSubscriber(List<String> subscriberIdentities) throws OperationFailedException;

	boolean isTestSubscriber(String subscriberIdentity) throws OperationFailedException;

	Iterator<String> getTestSubscriberIterator() throws OperationFailedException;

	void refreshTestSubscriberCache() throws OperationFailedException;

	List<SPRInfo> getDeleteMarkedProfiles() throws OperationFailedException;

	Map<String, Integer> restoreProfile(List<String> subscriberIdentities) throws OperationFailedException;

	/**
	 * to be used for SM, unknown user not supported
     *
	 * @return {@link SPRInfo},
	 */
	SPRInfo getProfile(String subscriberIdentity, Predicate<SPRInfo> primaryPredicate, Predicate<SPRInfo>... predicates)
			throws OperationFailedException;

    /**
     * IF subscriber status is DELETED THEN
     * consider it as unknown user
     *
     * @throws OperationFailedException
     */
    SPRInfo getProfile(String subscriberIdentity) throws OperationFailedException;

	void addProfile(SubscriberDetails subscriberDetails, String requestIpAddress)
			throws OperationFailedException;
	int updateProfile(String subscriberIdentity, EnumMap<SPRFields, String> updatedProfile, String requestIpAddress)
			throws OperationFailedException;
	int updateProfile(SPRInfo sprInfo, String requestIpAddress)
			throws OperationFailedException;
	int markForDeleteProfile(String subscriberIdentity, String requestIpAddress)
			throws OperationFailedException;
	Map<String, Integer> purgeProfile(List<String> subscriberIdentities, String requestIpAddress)
			throws OperationFailedException;
	int purgeProfile(String subscriberIdentity, String requestIpAddress)
			throws OperationFailedException;
	Map<String, Integer> purgeAllProfile(String requestIpAddress)
			throws OperationFailedException;
	int restoreProfile(String subscriberIdentity, String requestIpAddress)
			throws OperationFailedException;


	List<Subscription> subscribeAddOnProductOfferById(SPRInfo sprInfo, String parentId,
													  String addOnProductOfferId,
													  Integer subscriptionStatusValue, Long startTime,
													  Long endTime, Integer priority, String param1,
													  String param2, Integer billDay,
													  String requestIpAddress, SubscriptionMetadata metadata)
			throws OperationFailedException;

	Subscription updateSubscription(
			SPRInfo sprInfo,
			String subscriptionId, Integer subscriptionStatusValue,
			Long startTime,
			Long endTime,
			Integer priority, String rejectReason,
			String param1,
			String param2, String requestIpAddress
	) throws OperationFailedException;


	/* (non-Javadoc)
	 * @see com.elitecore.corenetvertex.spr.SubscriberRepositoryI#subscribeProductOfferAddOnByName(
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.Integer, java.lang.Long, java.lang.Long,
	 * java.lang.String, java.lang.String)
	 */
	List<Subscription> subscribeProductOfferAddOnByName(SPRInfo sprInfo, String parentId, String addonName,
														Integer subscriptionStatusValue, Long startTime, Long endTime,
														Integer priority, String param1, String param2, Integer billDay,
														SubscriptionMetadata subscriptionMetadata)
			throws OperationFailedException;

	void replace(String subscriberIdentity, Collection<SubscriberUsage> usages) throws OperationFailedException;

	void addToExisting(String subscriberIdentity, Collection<SubscriberUsage> usages) throws OperationFailedException;

	void insertNew(String subscriberIdentity, Collection<SubscriberUsage> usages) throws OperationFailedException;

	LinkedHashMap<String, Subscription> getAddonSubscriptions(String subscriberIdentity) throws OperationFailedException;

	LinkedHashMap<String, Subscription> getAddonSubscriptions(SPRInfo sprInfo) throws OperationFailedException;

	Subscription getAddonSubscriptionsBySubscriptionId(String subscriberIdentity, String subscriptionId) throws OperationFailedException;
	
	Map<String, Map<String, SubscriberUsage>> getCurrentUsage(String subscriberIdentity) throws OperationFailedException;

	String getId();

	String getName();

	List<String> getGroupIds();

	SPRFields getAlternateIdField();

	void stop();
	
	List<SubscriptionInformation> getBalance(SPRInfo sprInfo, String pkgId, String subscriptionId) throws OperationFailedException;
	
	void resetBillingCycle(String subscriberID, String alternateID, String productOfferId,
									long resetBillingCycleDate, String resetReason, String parameter1,
									String parameter2, String parameter3) throws OperationFailedException;

	List<SubscriptionInformation> getBalance(SPRInfo sprInfo) throws OperationFailedException;
	SubscriberNonMonitoryBalance getRGNonMonitoryBalance(SPRInfo spr) throws OperationFailedException;
	SubscriberRnCNonMonetaryBalance getRnCNonMonetaryBalance(String subscriberIdentity) throws OperationFailedException;
	SubscriberNonMonitoryBalance getRGNonMonitoryBalanceWithResetExpiredBalance(SPRInfo sprInfo, String pkgId, String subscriptionId) throws OperationFailedException;
	//TODO Import subscriber should be validated with the definition of the package, which is getting passed with susbcriberInformation
	void importSubscriber(SubscriberInfo subscriberInfo) throws OperationFailedException;

	void importSubscriptions(SPRInfo sprInfo, List<SubscriptionDetail> subscriptionDetails) throws OperationFailedException;

	void resetUsage(String subscriberId, String productOfferId) throws OperationFailedException;
	int changeBaseProductOffer(ChangeBaseProductOfferParams params, String requestIpAddress) throws OperationFailedException;

	int changeIMSPackage(String subscriberIdentity,String newPackageName, String parameter1, String parameter2, String parameter3) throws OperationFailedException;

	void reserveBalance(String subscriberId, List<NonMonetoryBalance> reservationBalances) throws OperationFailedException;
	void reserveRnCBalance(String subscriberId, List<RnCNonMonetaryBalance> reservationBalances) throws OperationFailedException;
	void reportBalance(String subscriberIdentity, List<NonMonetoryBalance> serviceWiseNonMonetaryBalances) throws OperationFailedException;
	void reportRnCBalance(String subscriberIdentity, List<RnCNonMonetaryBalance> serviceWiseNonMonetaryBalances) throws OperationFailedException;
	void resetBalance(String subscriberIdentity, List<NonMonetoryBalance> serviceWiseNonMonetaryBalances) throws OperationFailedException;
	void directDebitBalance(String subscriberIdentity, List<NonMonetoryBalance> serviceWiseNonMonetaryBalances) throws OperationFailedException;
	void reportAndReserveBalance(String subscriberIdentity, List<NonMonetoryBalance> serviceWiseNonMonetaryBalances) throws OperationFailedException;
	void reportAndReserveRnCBalance(String subscriberIdentity, List<RnCNonMonetaryBalance> serviceWiseNonMonetaryBalances) throws OperationFailedException;
	void updateNextBillingCycleBalance(Set<Map.Entry<String, NonMonetoryBalance>> nonMonetoryBalances, Integer newBillDay) throws OperationFailedException;

	SubscriberMonetaryBalance getMonetaryBalance(String subscriberId, java.util.function.Predicate<MonetaryBalance> predicate) throws OperationFailedException;
	void addMonetaryBalance(String subscriberId, SubscriberMonetaryBalanceWrapper subscriberMonetaryBalanceWrapper,
							String remark, String requestIPAddress) throws OperationFailedException;
	void addMonetaryBalance(String subscriberId, SubscriberMonetaryBalanceWrapper subscriberMonetaryBalanceWrapper,
							String remark, String requestIPAddress,EnumMap<SPRFields, String> updatedProfile) throws OperationFailedException;
	void addMonetaryBalance(String subscriberId, SubscriberMonetaryBalanceWrapper subscriberMonetaryBalanceWrapper,
							String remark, String requestIPAddress,EnumMap<SPRFields, String> updatedProfile,
							String monetaryRechargePlanName) throws OperationFailedException;

	void updateMonetaryBalance(String subscriberId, SubscriberMonetaryBalanceWrapper subscriberMonetaryBalanceWrapper, String remark, String requestIPAddress)
			throws OperationFailedException;

	void rechargeMonetaryBalance(MonetaryRechargeData monetaryRechargeData) throws OperationFailedException;
	void rechargeMonetaryBalance(MonetaryRechargeData monetaryRechargeData,EnumMap<SPRFields, String> updatedProfile,String subscriberId, String requestIpAddress) throws OperationFailedException;
    void updateCreditLimit(String subscriberId, SubscriberMonetaryBalanceWrapper subscriberMonetaryBalanceWrapper, String remark, String requestIPAddress) throws OperationFailedException;

    void reserveMonetaryBalance(String subscriberId, List<MonetaryBalance> lstmonetaryBalance) throws OperationFailedException;
	void directDebitMonetaryBalance(String subscriberId, List<MonetaryBalance> lstmonetaryBalance) throws OperationFailedException;
	void reportAndReserveMonetaryBalance(String subscriberId, List<MonetaryBalance> lstmonetaryBalance) throws OperationFailedException;

	Subscription subscribeQuotaTopUpByName(String subscriberIdentity, String parentId, String quotaTopUpName,
														   Integer subscriptionStatusValue,
														   Long startTime, Long endTime, Integer priority,double subscriptionPrice ,String monetaryBalanceId , String param1, String param2) throws OperationFailedException;

	Subscription subscribeQuotaTopUpById(String subscriberIdentity, String parentId, String topUpId,
                                                         Integer subscriptionStatusValue,
                                                         Long startTime, Long endTime, Integer priority,double subscriptionPrice,String monetaryBalanceId, String param1, String param2) throws OperationFailedException;

	Subscription getTopUpSubscriptionsBySubscriptionId(String subscriptionId) throws OperationFailedException;

	Subscription updateQuotaTopUpSubscription(
			String subscriberIdentity,
			String subscriptionId, Integer subscriptionStatusValue,
			Long startTime,
			Long endTime,
			Integer priority, String rejectReason
	) throws OperationFailedException;

	LinkedHashMap<String, Subscription> getTopUpSubscriptions(String subscriberIdentity) throws OperationFailedException;

	LinkedHashMap<String, Subscription> getBodSubscriptions(String subscriberIdentity) throws OperationFailedException;

	SubscriberRnCNonMonetaryBalance getNonMonetaryBalanceForRnCPackage(SPRInfo sprInfo, String pkgId, String subscriptionId) throws OperationFailedException;

    void resetQuota(String subscriberId, String productOfferId) throws OperationFailedException;

    SubscriptionResult subscribeAddOnProductOfferById(SubscriptionParameter subscriptionParameter, String requestIpAddress) throws OperationFailedException;

    SubscriptionResult autoSubscribeAddOnProductOfferById(SubscriptionParameter subscriptionParameter) throws OperationFailedException;

	void resetBalance(SPRInfo sprInfo, String productOfferId, RnCPackage rnCPackage) throws OperationFailedException;

	void refundRnCBalance(String subscriberIdentity, List<RnCNonMonetaryBalance> refundNonMonetaryBalances) throws OperationFailedException;
	void changeBillDay(String subscriberId, Timestamp nextBillDate, Timestamp billChangeDate, String requestIpAddress) throws OperationFailedException;

	SPRInfo createAnonymousProfile(String subscriberIdentity);
	SubscriptionNonMonitoryBalance addDataRnCBalance(String subscriberIdentity, Subscription subscription, ProductOffer productOffer) throws OperationFailedException;
	Subscription subscribeBoDPackage(SubscriptionParameter subscriptionParameter, String requestIpAddress) throws OperationFailedException;

	Subscription updateFnFGroup(SPRInfo sprInfo, Subscription subscription, String requestIpAddress) throws OperationFailedException;

	void processReset(SPRInfo sprInfo, String requestIpAddress) throws OperationFailedException;
}