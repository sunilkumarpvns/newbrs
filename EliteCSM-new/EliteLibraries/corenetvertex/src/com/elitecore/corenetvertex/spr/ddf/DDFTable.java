
package com.elitecore.corenetvertex.spr.ddf;

import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.rnc.pkg.RnCPackage;
import com.elitecore.corenetvertex.sm.acl.StaffData;
import com.elitecore.corenetvertex.spr.MonetaryBalance;
import com.elitecore.corenetvertex.spr.NonMonetoryBalance;
import com.elitecore.corenetvertex.spr.RnCNonMonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberAlternateIds;
import com.elitecore.corenetvertex.spr.SubscriberMonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberMonetaryBalanceWrapper;
import com.elitecore.corenetvertex.spr.SubscriberNonMonitoryBalance;
import com.elitecore.corenetvertex.spr.SubscriberRnCNonMonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberUsage;
import com.elitecore.corenetvertex.spr.SubscriptionNonMonitoryBalance;
import com.elitecore.corenetvertex.spr.balance.SubscriptionInformation;
import com.elitecore.corenetvertex.spr.data.SPRFields;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SubscriberDetails;
import com.elitecore.corenetvertex.spr.data.SubscriberInfo;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.data.SubscriptionMetadata;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.UnauthorizedActionException;
import com.elitecore.corenetvertex.spr.params.ChangeBaseProductOfferParams;
import com.elitecore.corenetvertex.spr.params.MonetaryRechargeData;

import java.sql.Timestamp;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public interface DDFTable {

	SPRInfo getProfile(String subscriberIdentity) throws OperationFailedException;
	void addProfile(SubscriberDetails subscriberDetails, StaffData staffData, String requestIpAddress)
			throws OperationFailedException, UnauthorizedActionException;
	int updateProfile(String subscriberID, EnumMap<SPRFields, String> updatedProfile, StaffData adminStaff, String requestIpAddress)
			throws OperationFailedException, UnauthorizedActionException;
	int updateProfileByAlternateId(String alternateId, EnumMap<SPRFields, String> updatedProfile, StaffData adminStaff, String requestIpAddress)
			throws OperationFailedException, UnauthorizedActionException;
	int updateProfile(SPRInfo sprInfo, StaffData staffData, String requestIpAddress)
			throws OperationFailedException, UnauthorizedActionException;
	int updateProfile(SPRInfo sprInfo, String requestIpAddress)
			throws OperationFailedException;
	int deleteProfile(String subscriberIdentity, StaffData staffData, String requestIpAddress)
			throws OperationFailedException, UnauthorizedActionException;
	int deleteProfileByAlternateId(String alternateId, StaffData staffData, String requestIpAddress)
			throws OperationFailedException, UnauthorizedActionException;
	Map<String, Integer> deleteProfileByAlternateId(List<String> alternateIds, StaffData staffData, String requestIpAddress)
			throws OperationFailedException;
	Map<String, Integer> deleteProfile(List<String> subscriberIdentities, StaffData staffData, String requestIpAddress)
			throws OperationFailedException;
	Map<String, Integer> purgeSubscriber(List<String> subscriberIdentities, StaffData staffData, String requestIpAddress)
			throws OperationFailedException;
	Map<String, Integer> purgeSubscriberByAlternateId(List<String> alternateIds, StaffData staffData, String requestIpAddress)
			throws OperationFailedException;
	void migrateSubscriber(String currentSubscriberIdentity, String newSubscriberIdentity, StaffData staffData, String requestIpAddress)
			throws UnauthorizedActionException, OperationFailedException;
	int deleteProfile(String subscriberIdentity, String requestIpAddress)
			throws OperationFailedException;
	int purgeSubscriber(String subscriberIdentity, String requestIpAddress)
			throws OperationFailedException;
	int purgeSubscriber(String subscriberIdentity, StaffData staffData, String requestIpAddress)
			throws OperationFailedException, UnauthorizedActionException;
	int purgeSubscriberByAlternateId(String alternateId, StaffData staffData, String requestIpAddress)
			throws UnauthorizedActionException, OperationFailedException;
	Map<String, Integer> purgeAllSubscribers(StaffData staffData, String requestIpAddress)
			throws OperationFailedException;
	int restoreSubscriber(String subscriberIdentity, StaffData staffData, String requestIpAddress)
			throws OperationFailedException, UnauthorizedActionException;
	int restoreSubscriberByAlternateId(String alternateId, StaffData staffData, String requestIpAddress)
			throws UnauthorizedActionException, OperationFailedException;
	int restoreSubscriber(String subscriberIdentity,String requestIpAddress)
			throws OperationFailedException;
	Map<String, Integer> restoreAllSubscribers(StaffData staffData, String requestIpAddress)
			throws OperationFailedException, UnauthorizedActionException;

	void addProfile(SubscriberDetails subscriberDetails) throws OperationFailedException;

	SPRInfo searchSubscriber(String subscriberIdentity, StaffData staffData) throws OperationFailedException, UnauthorizedActionException;
	SPRInfo searchSubscriber(String subscriberIdentity) throws OperationFailedException;
	LinkedHashMap<String, Subscription> getSubscriptions(String subscriberIdentity, StaffData staffData) throws OperationFailedException, UnauthorizedActionException;
	LinkedHashMap<String, Subscription> getSubscriptions(String subscriberIdentity) throws OperationFailedException;
	
	List<Subscription> subscribeAddOnProductOfferById(SPRInfo sprInfo, String parentId, String addonId,
													  Integer subscriptionStatusValue,
													  Long startTime, Long endTime, Integer priority,
													  String param1, String param2,
													  StaffData staffData, Integer billDay, String requestIpAddress, SubscriptionMetadata subscriptionMetadata) throws OperationFailedException, UnauthorizedActionException;

	SubscriptionResult subscribeAddOnProductOfferById(SubscriptionParameter subscriptionParameter, StaffData staffData, String requestIpAddress) throws OperationFailedException, UnauthorizedActionException;
	SubscriptionResult autoSubscribeAddOnProductOfferById(SubscriptionParameter subscriptionParameter) throws OperationFailedException, UnauthorizedActionException;

	List<Subscription> subscribeAddOnProductOfferById(SPRInfo sprInfo, String parentId, String addonId,
                                                Integer subscriptionStatusValue,
                                                Long startTime, Long endTime, Integer priority,
                                                String param1, String param2, Integer billDay, String requestIpAddress,
													  SubscriptionMetadata subscriptionMetadata) throws OperationFailedException;


	List<Subscription> subscribeProductOfferAddOnByName(SPRInfo sprInfo, String parentId, String addonName,
														Integer subscriptionStatusValue, Long startTime, Long endTime,
														Integer priority, String param1, String param2,
														StaffData staffData, Integer billDay,
														SubscriptionMetadata metadata)
			throws OperationFailedException, UnauthorizedActionException;

	
	Subscription updateSubscription(
			SPRInfo sprInfo,
			String subscriptionId,
			Integer subscriptionStatusValue,
			Long startTime,
			Long endTime,
			Integer priority,
			String rejectReason,
			String param1,
			String param2,
			StaffData staffData,
			String requestIpAddress
			) throws OperationFailedException, UnauthorizedActionException;
	
	Subscription updateSubscription(
			SPRInfo sprInfo,
			String subscriptionId,
			Integer subscriptionStatusValue,
			Long startTime,
			Long endTime,
			Integer priority,
			String rejectReason,
			String param1,
			String param2,
			String requestIpAddress) throws OperationFailedException;
	
	boolean isTestSubscriber(String subscriberIdentity) throws OperationFailedException;
	Iterator<String> getTestSubscriberIterator() throws OperationFailedException;
	
	void addTestSubscriber(String subscriberIdentity, StaffData staffData) throws OperationFailedException, UnauthorizedActionException;
	void removeTestSubscriber(String subscriberIdentity, StaffData staffData) throws OperationFailedException, UnauthorizedActionException;
	void removeTestSubscriber(List<String> subscriberIdentities, StaffData staffData) throws OperationFailedException, UnauthorizedActionException;
	
	List<SPRInfo> getDeleteMarkedProfiles(StaffData staffData) throws OperationFailedException, UnauthorizedActionException;
	List<SPRInfo> getDeleteMarkedProfiles() throws OperationFailedException, UnauthorizedActionException;
	SPRInfo getDeleteMarkedProfile(String subscriberIdentity, StaffData staffData) throws OperationFailedException, UnauthorizedActionException;

	Map<String, Integer> purgeSubscriber(List<String> subscriberIdentities) throws OperationFailedException;


	Map<String, Integer> restoreSubscriber(List<String> subscriberIdentities, StaffData staffData) throws OperationFailedException;
	Map<String, Integer> restoreSubscriber(List<String> subscriberIdentities) throws OperationFailedException;
	Map<String, Map<String, SubscriberUsage>> getCurrentUsage(String subscriberIdentity) throws OperationFailedException;
	Subscription getSubscriptionBySubscriberId(String subscriberIdentity, String addOnSubscriptionId, StaffData staffData) throws OperationFailedException, UnauthorizedActionException;
	SPRInfo searchSubscriberByAlternateId(String alternateId, StaffData staffData) throws OperationFailedException, UnauthorizedActionException;
	int updateProfileByAlternateId(SPRInfo sprInfo, String alternateId, StaffData staffData) throws OperationFailedException, UnauthorizedActionException;
	Map<String, Integer> restoreSubscribersByAlternateId(List<String> alternateIds, StaffData staffData) throws OperationFailedException;
	LinkedHashMap<String, Subscription> getSubscriptionsbyAlternateId(String alternateId, StaffData staffData) throws OperationFailedException, UnauthorizedActionException;
	Map<String, Map<String, SubscriberUsage>> getCurrentUsageByAlternateId(String alternateId) throws OperationFailedException;
	Subscription getSubscriptionByAlternateId(String alternateIdentity, String addOnSubscriptionId, StaffData staffData) throws OperationFailedException, UnauthorizedActionException;
	List<SubscriptionInformation> getBalanceBySubscriberId(SPRInfo sprInfo, String pkgId, String subscriptionId) throws OperationFailedException;
	List<SubscriptionInformation> getAllBalance(SPRInfo sprInfo) throws OperationFailedException;
	SubscriberNonMonitoryBalance getRGNonMonitoryBalanceWithResetExpiredBalance(SPRInfo sprInfo, String pkgId, String subscriptionId) throws OperationFailedException, UnauthorizedActionException;
	SubscriberNonMonitoryBalance getRGNonMonitoryBalance(SPRInfo sprInfo) throws OperationFailedException;
	SubscriberRnCNonMonetaryBalance getRnCNonMonitoryBalance(SPRInfo sprInfo) throws OperationFailedException;

	void resetBillingCycleBySubscriberIdentity(String subscriberID,String alternateID,String productOfferId,long resetBillingCycleDate,String resetReason,String parameter1,String parameter2,String parameter3) throws OperationFailedException;
	void resetBillingCycleByAlternateID(String alternateID,String productOfferId,long resetBillingCycleDate,String resetReason,String parameter1,String parameter2,String parameter3) throws OperationFailedException;
	String searchSubscriberIdByAlternateId(String alternateId, StaffData staffData) throws OperationFailedException;

	void importSubscriber(SubscriberInfo subscriberInfo) throws OperationFailedException;
	void resetUsageByAlternateId(String alternateId,String packageId) throws OperationFailedException;
	void resetUsageBySubscriberId(String subscriberId, String packageId) throws OperationFailedException;
	int changeIMSPackageBySubscriberId(String subscriberIdentity, String newPackageName, String parameter1, String parameter2, String parameter3, StaffData staffData) throws UnauthorizedActionException, OperationFailedException;
	int changeDataPackageBySubscriberId(ChangeBaseProductOfferParams params, StaffData staffData, String requestIpAddress) throws UnauthorizedActionException, OperationFailedException;
	
	void reserveBalance(String subscriberIdentity, List<NonMonetoryBalance> serviceWiseNonMonetaryBalances) throws OperationFailedException;
	void reserveRnCBalance(String subscriberIdentity, List<RnCNonMonetaryBalance> serviceWiseNonMonetaryBalances) throws OperationFailedException;
	void reportBalance(String subscriberIdentity, List<NonMonetoryBalance> serviceWiseNonMonetaryBalances) throws OperationFailedException;
	void reportRnCBalance(String subscriberIdentity, List<RnCNonMonetaryBalance> serviceWiseNonMonetaryBalances) throws OperationFailedException;
	void resetBalance(String subscriberIdentity, List<NonMonetoryBalance> serviceWiseNonMonetaryBalances) throws OperationFailedException;
	void directDebitBalance(String subscriberIdentity, List<NonMonetoryBalance> serviceWiseNonMonetaryBalances) throws OperationFailedException;
	void reportAndReserveBalance(String subscriberIdentity, List<NonMonetoryBalance> serviceWiseNonMonetaryBalances) throws OperationFailedException;
	void reportAndReserveRnCBalance(String subscriberIdentity, List<RnCNonMonetaryBalance> serviceWiseNonMonetaryBalances) throws OperationFailedException;
	void updateNextBillingCycleBalance(Set<Map.Entry<String, NonMonetoryBalance>> nonMonetoryBalances, Integer newBillDay) throws OperationFailedException;

	SubscriberMonetaryBalance getMonetaryBalance(String subscriberIdentity, Predicate<MonetaryBalance> predicate)
			throws OperationFailedException;
	void addMonetaryBalance(String subscriberIdentity, SubscriberMonetaryBalanceWrapper subscriberMonetaryBalanceWrapper,
							String remark, String requestIPAddress) throws OperationFailedException;

	void addMonetaryBalance(String subscriberIdentity, SubscriberMonetaryBalanceWrapper subscriberMonetaryBalanceWrapper,
							String remark, String requestIPAddress,EnumMap<SPRFields, String> updatedProfile,
							String monetaryRechargePlanName) throws OperationFailedException;

	void updateMonetaryBalance(String subscriberIdentity, SubscriberMonetaryBalanceWrapper subscriberMonetaryBalanceWrapper,
							   String remark, String requestIPAddress) throws OperationFailedException;

	void rechargeMonetaryBalance(MonetaryRechargeData monetaryRechargeData, EnumMap<SPRFields, String> updatedProfile,String subscriberId, String requestIpAddress) throws OperationFailedException;

    void updateCreditLimit(String subscriberIdentity, SubscriberMonetaryBalanceWrapper subscriberMonetaryBalanceWrapper,
						   String remark, String requestIPAddress) throws OperationFailedException;

    MonetaryBalanceOperation getMonetaryBalanceOp();

	Subscription subscribeQuotaTopUpByName(String subscriberIdentity, String parentId, String topUpName,
                                           Integer subscriptionStatusValue,
                                           Long startTime, Long endTime,
                                           Integer priority,double subscriptionPrice ,String monetaryBalanceId,String param1, String param2,
                                           StaffData staffData) throws OperationFailedException, UnauthorizedActionException;

	Subscription subscribeTopUpByTopUpIdByAlternateId(String alternateId, String parentId, String topUpId, int subscriptionStatusValue, Long startTime, Long endTime,
													  Integer priority,double subscriptionPrice,String monetaryBalanceId, String parameter1, String parameter2, StaffData staffData) throws OperationFailedException, UnauthorizedActionException;

	Subscription getQuotaTopUpSubscriptionByAlternateId(String alternateIdentity, String addOnSubscriptionId, StaffData staffData) throws OperationFailedException, UnauthorizedActionException;

	Subscription getQuotaTopUpSubscriptionBySubscriberId(String subscriberIdentity, String addOnSubscriptionId, StaffData staffData) throws OperationFailedException, UnauthorizedActionException;

	Subscription updateQuotaTopUpSubscriptionByAlternateId(String alternateId, String topUpSubscriptionId, int subscriptionStatusValue, Long startTime, Long endTime,
                                                           Integer priority, String rejectReason, String parameter1, String parameter2, StaffData staffData) throws UnauthorizedActionException, OperationFailedException;

	Subscription updateQuotaTopUpSubscription(String subscriberIdentity, String subscriptionId, Integer subscriptionStatusValue, Long startTime, Long endTime, Integer priority, String rejectReason,
                                              String param1, String param2, StaffData staffData) throws OperationFailedException, UnauthorizedActionException;

	LinkedHashMap<String, Subscription> getQuotaTopUpSubscriptionsbyAlternateId(String alternateId, StaffData staffData) throws OperationFailedException, UnauthorizedActionException;

	LinkedHashMap<String, Subscription> getTopUpSubscriptions(String subscriberIdentity, StaffData staffData) throws OperationFailedException,
			UnauthorizedActionException;

	SubscriberRnCNonMonetaryBalance getNonMonetaryBalanceForRnCPackage(SPRInfo sprInfo, String pkgId, String subscriptionId) throws OperationFailedException;

    void resetQuotaBySubscriberId(String subscriberID, String productOfferId) throws OperationFailedException;

	void resetBalanceBySubscriberId(SPRInfo sprInfo, String productOfferId, RnCPackage rnCPackage) throws OperationFailedException;

	void refundRnCBalance(String subscriberIdentity, List<RnCNonMonetaryBalance> refundNonMonetaryBalances) throws OperationFailedException;

   /*Following methods added for external alternate Identity operation(i.e. provisioned apart from spr operation)
   * Add
   * Update
   * delete
   * Get
   * Change
   * */
	void addExternalAlternateId(String subscriberIdentity, String alternateIdentity,StaffData staffData) throws OperationFailedException,UnauthorizedActionException;
	int removeExternalAlternateId(String subscriberIdentity, String alternateIdentity,StaffData staffData) throws OperationFailedException,UnauthorizedActionException;
    int updateExternalAlternateId(String subscriberIdentity, String oldAlternateIdentity, String newAlternateIdentity,StaffData staffData) throws OperationFailedException, UnauthorizedActionException;
    SubscriberAlternateIds getExternalAlternateIds(String subscriberIdentity,StaffData staffData) throws OperationFailedException, UnauthorizedActionException;
    int changeAlternateIdentityStatus(String subscriberIdentity, String alternateIdentity, String status,StaffData staffData) throws OperationFailedException, UnauthorizedActionException;
	void changeBillDay(String subscriberIdentity, Timestamp nextBillDate,Timestamp billChangeDate, String requestIpAddress) throws OperationFailedException;
	void checkForBillDateChange(SPRInfo sprInfo) throws OperationFailedException, UnauthorizedActionException;
	SubscriptionNonMonitoryBalance addDataRnCBalance(String subscriberIdentity, Subscription subscription, ProductOffer productOffer) throws OperationFailedException;
	Subscription subscribeBodPackage(SubscriptionParameter subscriptionParameter, StaffData staffData, String requestIpAddress) throws OperationFailedException, UnauthorizedActionException;
	LinkedHashMap<String, Subscription> getBodSubscriptions(String subscriberIdentity, StaffData staffData) throws OperationFailedException,
			UnauthorizedActionException;
	Subscription updateFnFGroup(SPRInfo sprInfo, Subscription subscription, StaffData staffData, String requestIpAddress)
			throws OperationFailedException, UnauthorizedActionException;

	void processReset(SPRInfo sprInfo, String requestIpAddress) throws OperationFailedException;
}
