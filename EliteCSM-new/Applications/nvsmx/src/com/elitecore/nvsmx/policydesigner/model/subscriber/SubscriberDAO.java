package com.elitecore.nvsmx.policydesigner.model.subscriber;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.utilx.db.TransactionFactoryGroupImpl;
import com.elitecore.core.systemx.esix.LoadBalancerType;
import com.elitecore.corenetvertex.constants.SubscriptionState;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.conf.FailReason;
import com.elitecore.corenetvertex.core.db.DBDataSource;
import com.elitecore.corenetvertex.core.ldap.LDAPDataSource;
import com.elitecore.corenetvertex.database.DatabaseData;
import com.elitecore.corenetvertex.ldap.LdapData;
import com.elitecore.corenetvertex.pkg.constants.ACLAction;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.pm.rnc.pkg.RnCPackage;
import com.elitecore.corenetvertex.sm.acl.StaffData;
import com.elitecore.corenetvertex.sm.ddf.DdfData;
import com.elitecore.corenetvertex.spr.AlternateIdentityMapper;
import com.elitecore.corenetvertex.spr.MonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberAlternateIds;
import com.elitecore.corenetvertex.spr.SubscriberMonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberMonetaryBalanceWrapper;
import com.elitecore.corenetvertex.spr.SubscriberNonMonitoryBalance;
import com.elitecore.corenetvertex.spr.SubscriberRepository;
import com.elitecore.corenetvertex.spr.SubscriberRnCNonMonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberUsage;
import com.elitecore.corenetvertex.spr.TestSubscriberCache;
import com.elitecore.corenetvertex.spr.balance.SubscriptionInformation;
import com.elitecore.corenetvertex.spr.data.DDFConfiguration;
import com.elitecore.corenetvertex.spr.data.SPRFields;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.data.SubscriberDetails;
import com.elitecore.corenetvertex.spr.data.SubscriberInfo;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.data.SubscriptionDetail;
import com.elitecore.corenetvertex.spr.data.SubscriptionMetadata;
import com.elitecore.corenetvertex.spr.ddf.DDFTableImpl;
import com.elitecore.corenetvertex.spr.ddf.RepositorySelector;
import com.elitecore.corenetvertex.spr.ddf.SubscriptionParameter;
import com.elitecore.corenetvertex.spr.ddf.SubscriptionResult;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.spr.exceptions.UnauthorizedActionException;
import com.elitecore.corenetvertex.spr.params.ChangeBaseProductOfferParams;
import com.elitecore.corenetvertex.spr.params.MonetaryRechargeData;
import com.elitecore.corenetvertex.util.DataSourceProvider;
import com.elitecore.nvsmx.system.alert.AlertListenerImpl;
import com.elitecore.nvsmx.system.db.DBDatasourceImpl;
import com.elitecore.nvsmx.system.db.NVSMXDBConnectionManager;
import com.elitecore.nvsmx.system.driver.cdr.ExternalAlternateIdEDRListenerImpl;
import com.elitecore.nvsmx.system.ldap.LDAPDataSourceImpl;
import com.elitecore.nvsmx.system.listeners.DefaultNVSMXContext;
import com.elitecore.nvsmx.system.spr.SPRProviderImpl;
import com.elitecore.nvsmx.system.spr.TransactionFactoryAdapter;
import org.apache.commons.collections.MapUtils;

import javax.annotation.Nullable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static com.elitecore.commons.logging.LogManager.getLogger;

/**
 * @author aneri.chavda
 */
public class SubscriberDAO {

	private static final String MODULE = "SUBS-DAO";
	private static SubscriberDAO instance = new SubscriberDAO();
	private SPRProviderImpl sprProvider;

	private AlertListener dummyAlertListener = new AlertListenerImpl();
	private DDFTableImpl ddfTable;
	private boolean isInitialized = false;

	private SubscriberDAO() {}
	
	public static SubscriberDAO getInstance() {
		return instance;
	}
	
	public void init() {
		if(isInitialized) {
			return;
		}
		
		getLogger().debug(MODULE, "SubscriberDAO initialization started");
	
		TransactionFactoryGroupImpl transactionFactoryGroup = new TransactionFactoryGroupImpl(LoadBalancerType.SWITCH_OVER);
		transactionFactoryGroup.addCommunicator(NVSMXDBConnectionManager.getInstance().getTransactionFactory());

		TestSubscriberCache testSubscriberCache = new TestSubscriberCache(new TransactionFactoryAdapter(transactionFactoryGroup)
				, dummyAlertListener);
		testSubscriberCache.init();
		this.sprProvider = new SPRProviderImpl(testSubscriberCache);

		DdfData ddfTableData = DDFTableDAO.getDDFTableData();

		DDFConfiguration ddfConfiguration = DDFConfiguration.create(ddfTableData, new DataSourceProvider() {
			@Override
			public DBDataSource getDBDataSource(DatabaseData databaseData, FailReason failReason) {
				return DBDatasourceImpl.create(databaseData, failReason);
			}

			@Override
			public LDAPDataSource getLDAPDataSource(LdapData ldapData, FailReason failReason) {
				return LDAPDataSourceImpl.create(ldapData, failReason);
			}
		});

		TransactionFactoryAdapter adapter = new TransactionFactoryAdapter(transactionFactoryGroup);
		AlternateIdentityMapper mapper = null;
		if (adapter != null) {
			 mapper = new AlternateIdentityMapper(adapter, new ExternalAlternateIdEDRListenerImpl());
		}
		RepositorySelector repositorySelector = RepositorySelector.create(ddfConfiguration, sprProvider);
		DDFTableImpl ddfTable = new com.elitecore.corenetvertex.spr.ddf.DDFTableImpl(ddfConfiguration,
				DefaultNVSMXContext.getContext().getPolicyRepository(),
				sprProvider, mapper, repositorySelector);
		this.ddfTable = ddfTable;
		isInitialized = true;
		
		getLogger().debug(MODULE, "SubscriberDAO initialization successfully completed");
	}

	/**
	 * @author Prakashkumar Pala
	 * @since 23-Oct-2018
	 * for NETVERTEX-3068 - START
	 * Added IP Address for EDR
	 * */
	public void addSubscriber(SubscriberDetails subscriberDetails, StaffData staffData, String requestIpAddress)
			throws OperationFailedException, UnauthorizedActionException {
		((SPRInfoImpl)subscriberDetails.getSprInfo()).setCreatedDate(new Timestamp(System.currentTimeMillis()));
		ddfTable.addProfile(subscriberDetails, staffData, requestIpAddress);
	}

	public int updateSubscriber(SPRInfo sprInfo, StaffData staffData, String requestIpAddress)
			throws OperationFailedException, UnauthorizedActionException {
		return ddfTable.updateProfile(sprInfo, staffData, requestIpAddress);
	}

	public int updateSubscriberByAlternateId(String alternateId, EnumMap<SPRFields, String> updatedProfile, StaffData adminStaff, String requestIpAddress)
			throws OperationFailedException, UnauthorizedActionException {
		return ddfTable.updateProfileByAlternateId(alternateId, updatedProfile, adminStaff, requestIpAddress);
	}

	public int updateSubscriber(String subscriberID, EnumMap<SPRFields, String> updatedProfile, StaffData adminStaff, String requestIpAddress)
			throws OperationFailedException, UnauthorizedActionException {
		return ddfTable.updateProfile(subscriberID, updatedProfile, adminStaff, requestIpAddress);
	}

	public int markedForDeletion(String subscriberIdentity, StaffData staffData, String requestIpAddress)
			throws OperationFailedException, UnauthorizedActionException {
		return ddfTable.deleteProfile(subscriberIdentity, staffData, requestIpAddress);
	}

	public int markedForDeletionByAlternateId(String alternateId, StaffData staffData, String requestIpAddress)
			throws OperationFailedException, UnauthorizedActionException {
		return ddfTable.deleteProfileByAlternateId(alternateId, staffData, requestIpAddress);
	}

	public Map<String,Integer> purgeSubscriber(List<String> subscriberIdentities, StaffData staffData, String requestIpAddress) throws OperationFailedException {
		return ddfTable.purgeSubscriber(subscriberIdentities, staffData, requestIpAddress);
	}
	/**
	 * for NETVERTEX-3068 - END
	 */

	public SPRInfo getSubscriberById(String subscriberIdentity, StaffData staffData) throws OperationFailedException, UnauthorizedActionException {
		return ddfTable.searchSubscriber(subscriberIdentity, staffData);
	}
	
	public SPRInfo getSubscriberByAlternateId(String alternateId, StaffData staffData) throws OperationFailedException, UnauthorizedActionException {
		return ddfTable.searchSubscriberByAlternateId(alternateId, staffData);
	}

	public String getSubscriberIdByAlternateId(String alternateId, StaffData staffData) throws OperationFailedException {
		return ddfTable.searchSubscriberIdByAlternateId(alternateId, staffData);
	}
	
	public List<SPRInfo> searchSubscriber(String subscriberIdentity, StaffData staffData) throws OperationFailedException, UnauthorizedActionException {
		SPRInfo sprInfo = ddfTable.searchSubscriber(subscriberIdentity, staffData);
		List<SPRInfo> subscribers = new ArrayList<SPRInfo>(1);
		if (sprInfo != null) {
			subscribers.add(sprInfo);
		}
		return subscribers;
	}

	public void checkForUserAuthorizationBySubscriberIdentity(String subscriberIdentity, StaffData staffData, ACLAction aclAction) throws OperationFailedException, UnauthorizedActionException {
		ddfTable.checkForUserAuthorizationBySubscriberIdentity(subscriberIdentity, staffData, aclAction);
	}

	public void checkForUserAuthorizationByAlternateIdentity(String alternateIdentity, StaffData staffData, ACLAction aclAction) throws OperationFailedException, UnauthorizedActionException {
		ddfTable.checkForUserAuthorizationByAlternateIdentity(alternateIdentity, staffData, aclAction);
	}

	public List<SPRInfo> searchSubscriberByAlternateIdField(String alternateIdField, StaffData staffData) throws OperationFailedException, UnauthorizedActionException {
		SPRInfo sprInfo = ddfTable.searchSubscriberByAlternateId(alternateIdField, staffData);
		List<SPRInfo> subscribers = new ArrayList<SPRInfo>(1);
		if (sprInfo != null) {
			subscribers.add(sprInfo);
		}
		return subscribers;
	}

	public List<Subscription> getSubscribedAddOns(SPRInfo subscriber, StaffData staffData) throws UnauthorizedActionException {
	
		Map<String, Subscription> addOnSubscriptionMap;
		List<Subscription> addOnsusbcriptions = new ArrayList<Subscription>();
		try {
			addOnSubscriptionMap = ddfTable.getSubscriptions(subscriber.getSubscriberIdentity(), staffData);
			for (Subscription addOnSubscription : addOnSubscriptionMap.values()) {
				addOnsusbcriptions.add(addOnSubscription);
			}
		} catch (OperationFailedException e) {
			LogManager.getLogger().error(MODULE, "Error in reading addOn subscriptions. Reason: " + e.getMessage());
			if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
				getLogger().trace(MODULE, e);
		}
		}
		return addOnsusbcriptions;
	}
	
	public List<Subscription> getSubscriptions(String subscriberIdentity, StaffData staffData) throws UnauthorizedActionException, OperationFailedException {

		List<Subscription> addOnsusbcriptions = new ArrayList<Subscription>();
		Map<String, Subscription> addOnSubscriptionMap = ddfTable.getSubscriptions(subscriberIdentity, staffData);
		for (Subscription addOnSubscription : addOnSubscriptionMap.values()) {
			addOnsusbcriptions.add(addOnSubscription);
		}
		return addOnsusbcriptions;
	}
	
	public List<Subscription> subscribeAddOnProductOfferById(SPRInfo sprInfo, String parentId, String addonProductOfferId, Long startTime, Integer priority, StaffData staffData, Integer billDay, String requestIpAddress,SubscriptionMetadata metadata) throws OperationFailedException, UnauthorizedActionException {
		return ddfTable.subscribeAddOnProductOfferById(sprInfo, parentId, addonProductOfferId, SubscriptionState.STARTED.getVal(), startTime, null, priority,null, null, staffData, billDay, requestIpAddress, metadata);
	}

	public List<Subscription> subscribeAddOnProductOfferById(SPRInfo sprInfo, String parentId,
													   String addonId, SubscriptionState subscriptionState,
													   Long startTime, Long endTime, Integer priority, StaffData staffData,
													   String parameter1, String parameter2, Integer billDay, String requestIpAddress,SubscriptionMetadata metadata) throws OperationFailedException, UnauthorizedActionException {

		return ddfTable.subscribeAddOnProductOfferById(sprInfo, parentId, addonId, subscriptionState.state, startTime, endTime, priority, parameter1, parameter2, staffData, billDay, requestIpAddress,metadata);
	}

	public List<Subscription> subscribeAddOnProductOfferById(
			SubscriptionParameter subscriptionParameter, StaffData staffData, String requestIpAddress)
			throws OperationFailedException, UnauthorizedActionException {
		try {
			SubscriptionResult subscriptionResult = ddfTable.subscribeAddOnProductOfferById(
					subscriptionParameter, staffData, requestIpAddress);
			return subscriptionResult.getSubscriptions();
		} catch (UnauthorizedActionException e) {
			if (getLogger().isWarnLogLevel()) {
				getLogger().warn(MODULE, e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}
		return null;
	}
	
	public List<Subscription> subscribeAddOnByName(SPRInfo sprInfo, String parentId,
			String addonName, SubscriptionState subscriptionState, 
			Long startTime, Long endTime, Integer priority, StaffData staffData,
			String parameter1, String parameter2, Integer billDay, SubscriptionMetadata metadata) throws OperationFailedException, UnauthorizedActionException {
		
		return ddfTable.subscribeProductOfferAddOnByName(sprInfo, parentId, addonName, subscriptionState.state, startTime, endTime, priority, parameter1, parameter2, staffData, billDay, metadata);
	}

	public Subscription unsubscribeAddOn(SPRInfo sprInfo, String subscriptionId, String rejectReason, StaffData staffData, String requestIpAddress) throws OperationFailedException, UnauthorizedActionException {
		return ddfTable.updateSubscription(sprInfo, subscriptionId, SubscriptionState.UNSUBSCRIBED.getVal(),  null, null, null, rejectReason, null, null, staffData, requestIpAddress);
	}

	public boolean isTestSubscriber(String subscriberIdentity) {
		try {
			return ddfTable.isTestSubscriber(subscriberIdentity);
		} catch (OperationFailedException e) {
			LogManager.ignoreTrace(e);
		}
		return false;
	}

	public Iterator<String> getTestSubscriberIterator() throws OperationFailedException {
		return ddfTable.getTestSubscriberIterator();
	}

	public void addTestSubscriber(String subscriberIdentity, StaffData staffData) throws OperationFailedException, UnauthorizedActionException {
		ddfTable.addTestSubscriber(subscriberIdentity,staffData);
	}

	public void removeTestSubscriber(String subscriberIdentity,StaffData staffData) throws OperationFailedException, UnauthorizedActionException {
		ddfTable.removeTestSubscriber(subscriberIdentity,staffData);
	}

	public void removeTestSubscriber(List<String> subscriberIdentities ,StaffData staffData) throws OperationFailedException, UnauthorizedActionException {
		ddfTable.removeTestSubscriber(subscriberIdentities, staffData);
	}

	public List<SPRInfo> getDeleteMarkedProfiles(StaffData staffData) throws OperationFailedException {
		
		List<SPRInfo> deleteMarkedProfiles = new ArrayList<SPRInfo>();
		
		for (SubscriberRepository repository : sprProvider.getAllSubscriberRepository()) {
			
			try {
				if (staffData != null) {
					if (staffData.isAccessibleAction(repository.getGroupIds(), ACLModules.SUBSCRIBER, ACLAction.VIEW_DELETED_SUBSCRIBER) == false) {
						throw new com.elitecore.corenetvertex.spr.exceptions.UnauthorizedActionException(ACLModules.SUBSCRIBER, ACLAction.VIEW_DELETED_SUBSCRIBER);
					}
				}
				deleteMarkedProfiles.addAll(repository.getDeleteMarkedProfiles());
			} catch (Exception e) {
				getLogger().error(MODULE, "Error while fetching deleted subscribers in Subscriber Repository("+ repository.getName()
						+"). Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		return deleteMarkedProfiles;
	}

	public SPRInfo getDeleteMarkedProfile(String subscriberIdentity, StaffData staffData) throws OperationFailedException, UnauthorizedActionException {
		return ddfTable.getDeleteMarkedProfile(subscriberIdentity, staffData);
	}

	public int purgeSubscriber(String subscriberIdentity, StaffData staffData, String requestIpAddress) throws OperationFailedException, UnauthorizedActionException {
		return ddfTable.purgeSubscriber(subscriberIdentity, staffData, requestIpAddress);
	}

	public Map<String, Integer> purgeAllSubscribers(StaffData staffData, String requestIpAddress) throws OperationFailedException {
		return ddfTable.purgeAllSubscribers(staffData, requestIpAddress);
	}

	public int restoreSubscriber(String subscriberIdentity, StaffData staffData, String requestIpAddress) throws OperationFailedException, UnauthorizedActionException {
		return ddfTable.restoreSubscriber(subscriberIdentity, staffData, requestIpAddress);
	}
	
	public Map<String, Integer> restoreAllSubscribers(StaffData staffData, String requestIpAddress) throws OperationFailedException, UnauthorizedActionException {
		return ddfTable.restoreAllSubscribers(staffData, requestIpAddress);
	}

	public Map<String, Integer> restoreSubscriber(List<String> subscriberIdentities, StaffData staffData) throws OperationFailedException {
		return ddfTable.restoreSubscriber(subscriberIdentities, staffData);
	}

	public Map<String, Integer> restoreSubscriberByAlternateId(List<String> alternateIds, StaffData staffData) throws OperationFailedException {
		return ddfTable.restoreSubscribersByAlternateId(alternateIds, staffData);
	}

	public int restoreSubscriberByAlternateId(String alternateId, StaffData staffData, String requestIpAddress) throws OperationFailedException, UnauthorizedActionException {
		return ddfTable.restoreSubscriberByAlternateId(alternateId, staffData, requestIpAddress);
	}
	
	public @Nullable Subscription getSubscribedAddOnBySubscriberId(String subscriberIdentity, String addOnSubscriptionId, StaffData staffData) throws OperationFailedException, UnauthorizedActionException {
		return ddfTable.getSubscriptionBySubscriberId(subscriberIdentity, addOnSubscriptionId, staffData);
	}
	
	public @Nullable Subscription getSubscribedAddOnByAlternateId(String alternateIdentity, String addOnSubscriptionId, StaffData staffData) throws OperationFailedException, UnauthorizedActionException {
		return ddfTable.getSubscriptionByAlternateId(alternateIdentity, addOnSubscriptionId, staffData);
	}
	
	public Subscription changeAddOnSubscription(SPRInfo sprInfo, String addOnSubscriptionId, SubscriptionState subscriptionStatus, Long startTime,
												Long endTime, Integer priority, String rejectReason, StaffData staffData, String parameter1, String parameter2, String requestIpAddress) throws OperationFailedException, UnauthorizedActionException {

		return ddfTable.updateSubscription(sprInfo, addOnSubscriptionId, subscriptionStatus.state, startTime, endTime, priority, rejectReason, parameter1, parameter2, staffData, requestIpAddress);
	}

	public Map<String, Map<String, SubscriberUsage>> getCurrentUsage(String subscriberIdentity) throws OperationFailedException {
		return ddfTable.getCurrentUsage(subscriberIdentity);
	}

	public List<SubscriptionInformation> getBalanceBySubscriberId(SPRInfo sprInfo, String pkgId, String subscriptionId) throws OperationFailedException {
		return ddfTable.getBalanceBySubscriberId(sprInfo, pkgId, subscriptionId);
	}

	public SubscriberNonMonitoryBalance getRGNonMonitoryBalanceWithResetExpiredBalance(SPRInfo sprInfo, String pkgId, String subscriptionId) throws OperationFailedException, UnauthorizedActionException {
		return ddfTable.getRGNonMonitoryBalanceWithResetExpiredBalance(sprInfo, pkgId, subscriptionId);
	}

	public SubscriberRnCNonMonetaryBalance getRncNonMonetaryBalance(SPRInfo sprInfo, String pkgId, String subscriptionId) throws OperationFailedException {
		return ddfTable.getNonMonetaryBalanceForRnCPackage(sprInfo, pkgId, subscriptionId);
	}

	/**
	 * Method not used.
	 * */
	public int updateSubscriberByAlternateId(SPRInfo sprInfo, String alternateId, StaffData staffData) throws OperationFailedException, UnauthorizedActionException {
		return ddfTable.updateProfileByAlternateId(sprInfo, alternateId, staffData);
	}
	
	public Map<String, Integer> markedForDeletionByAlternateId(List<String> alternateIds, StaffData staffData, String requestIpAddress) throws OperationFailedException {
		return ddfTable.deleteProfileByAlternateId(alternateIds, staffData, requestIpAddress);
	}
	
	public Map<String, Integer> markedForDeletion(List<String> subscriberIdentities, StaffData staffData, String requestIpAddress) throws OperationFailedException {
		return ddfTable.deleteProfile(subscriberIdentities, staffData, requestIpAddress);
	}

	public Map<String, Integer> purgeSubscriberByAlternateId(List<String> alternateIds, StaffData staffData, String requestIpAddress) throws OperationFailedException {
		return ddfTable.purgeSubscriberByAlternateId(alternateIds, staffData, requestIpAddress);
	}

	public int purgeSubscriberAlternateId(String alternateId, StaffData staffData, String requestIpAddress) throws OperationFailedException, UnauthorizedActionException {
		return ddfTable.purgeSubscriberByAlternateId(alternateId, staffData, requestIpAddress);
	}

	public List<Subscription> getSubscriptionsByAlternateId(String alternateId, StaffData staffData) throws OperationFailedException, UnauthorizedActionException {
		List<Subscription> addOnsusbcriptions = new ArrayList<Subscription>();
		Map<String, Subscription> addOnSubscriptionMap = ddfTable.getSubscriptionsbyAlternateId(alternateId, staffData);
		for (Subscription addOnSubscription : addOnSubscriptionMap.values()) {
			addOnsusbcriptions.add(addOnSubscription);
		}
		return addOnsusbcriptions;
	}

	public Map<String, Map<String, SubscriberUsage>> getCurrentUsageByAlternateId(String alternateId) throws OperationFailedException {
		return ddfTable.getCurrentUsageByAlternateId(alternateId);
	}

	public List<SubscriptionInformation> getAllBalance(SPRInfo sprInfo) throws OperationFailedException {
		return ddfTable.getAllBalance(sprInfo);
	}

	public SubscriberNonMonitoryBalance getRGNonMonitoryBalance(SPRInfo sprInfo) throws OperationFailedException {
		return ddfTable.getRGNonMonitoryBalance(sprInfo);
	}
	
	public void resetBillingCycleBySubscriberID(String subscriberID,
	String alternateID, String productOfferId,
	Long resetBillingCycleDate, String resetReason, String parameter1,
	String parameter2, String parameter3) throws OperationFailedException{
		ddfTable.resetBillingCycleBySubscriberIdentity(subscriberID,alternateID,productOfferId,resetBillingCycleDate,resetReason,parameter1,parameter2,parameter3);
	}
	
	public void resetBillingCycleByAlternateID(String alternateID, String productOfferId,
			Long resetBillingCycleDate, String resetReason, String parameter1,
			String parameter2, String parameter3) throws OperationFailedException{
		ddfTable.resetBillingCycleByAlternateID(alternateID,productOfferId,resetBillingCycleDate,resetReason,parameter1,parameter2,parameter3);
	}

	public void migrateSubscriber(String currentSubscriberIdentity, String newSubscriberIdentity, StaffData staffData, String requestIpAddress)
			throws OperationFailedException, UnauthorizedActionException{
		ddfTable.migrateSubscriber(currentSubscriberIdentity, newSubscriberIdentity, staffData, requestIpAddress);
	}

	public String getStrippedSubscriberIdentity(String subscriberID) {
		return ddfTable.getStrippedSubscriberIdentity(subscriberID);
	}
	
	public void importSubscriber(SubscriberInfo subscriberInfo) throws OperationFailedException {
		ddfTable.importSubscriber(subscriberInfo);
	}

	public void importSubscriptions(String subscriberId, List<SubscriptionDetail> subscriptionDetails) throws OperationFailedException {
		ddfTable.importSubscriptions(subscriberId, subscriptionDetails);
	}

	public void resetAllUsageByAlternateId(String alternateId, String packageId) throws OperationFailedException {
		ddfTable.resetUsageByAlternateId(alternateId, packageId);
	}

	public void resetAllUsageBySubscriberId(String subscriberId, String productOfferId) throws OperationFailedException {
		ddfTable.resetUsageBySubscriberId(subscriberId, productOfferId);
	}

    public void resetAllQuotaBySubscriberId(String subscriberID, String productOfferId) throws OperationFailedException {
        ddfTable.resetQuotaBySubscriberId(subscriberID, productOfferId);
    }

	public void resetRnCNonMonetaryBalance(SPRInfo sprInfo, String productOfferId, RnCPackage rnCPackage) throws OperationFailedException {
		ddfTable.resetBalanceBySubscriberId(sprInfo, productOfferId, rnCPackage);
	}
	
	public int changeDataPackageBySubscriberId(ChangeBaseProductOfferParams params, StaffData staff, String requestIpAddress) throws OperationFailedException, UnauthorizedActionException {
		return ddfTable.changeDataPackageBySubscriberId(params, staff, requestIpAddress);
	}

	public int changeIMSPackageBySubscriberId(String subscriberId, String newPackageName, String parameter1, String parameter2, String parameter3, StaffData staff) throws UnauthorizedActionException, OperationFailedException {
		return ddfTable.changeIMSPackageBySubscriberId(subscriberId, newPackageName, parameter1, parameter2, parameter3, staff);
	}

	public SubscriberMonetaryBalance getMonetaryBalance(String subscriberId, Predicate<MonetaryBalance> predicate) throws OperationFailedException {
		return ddfTable.getMonetaryBalance(subscriberId, predicate);
	}

	public void addMonetaryBalance(String subscriberId, SubscriberMonetaryBalanceWrapper subscriberMonetaryBalanceWrapper,
								   String remark, String requestIPAddress) throws OperationFailedException {
		ddfTable.addMonetaryBalance(subscriberId, subscriberMonetaryBalanceWrapper, remark, requestIPAddress);
	}

	public void addMonetaryBalance(String subscriberId, SubscriberMonetaryBalanceWrapper subscriberMonetaryBalanceWrapper,
								   String remark, String requestIPAddress,EnumMap<SPRFields, String> updatedProfile,
								   String monetaryRechargePlanName) throws OperationFailedException {
		ddfTable.addMonetaryBalance(subscriberId, subscriberMonetaryBalanceWrapper, remark, requestIPAddress, updatedProfile, monetaryRechargePlanName);
	}

	public void updateMonetaryBalance(String subscriberId, SubscriberMonetaryBalanceWrapper subscriberMonetaryBalanceWrapper, String remark, String requestIPAddress) throws OperationFailedException {
		ddfTable.updateMonetaryBalance(subscriberId,subscriberMonetaryBalanceWrapper,remark,requestIPAddress);
	}

	public void rechargeMonetaryBalance(MonetaryRechargeData monetaryRechargeData,EnumMap<SPRFields, String> updatedProfile,String subscriberId, String requestIpAddress) throws OperationFailedException {
		ddfTable.rechargeMonetaryBalance(monetaryRechargeData,updatedProfile,subscriberId, requestIpAddress);
	}

	public void updateCreditLimit(String subscriberId, SubscriberMonetaryBalanceWrapper subscriberMonetaryBalanceWrapper,
								  String remark, String requestIPAddress) throws OperationFailedException {
		ddfTable.updateCreditLimit(subscriberId, subscriberMonetaryBalanceWrapper, remark, requestIPAddress);
	}

	public Subscription subscribeTopUpByTopUpIdByAlternateId(String alternateId, String parentId, String addOnId, SubscriptionState subscriptionState,
															 Long startTime, Long endTime, Integer priority, double subscriptionPrice,String monetaryBalanceId,StaffData staffData, String parameter1, String parameter2) throws OperationFailedException, UnauthorizedActionException {

		return ddfTable.subscribeTopUpByTopUpIdByAlternateId(alternateId, parentId, addOnId, subscriptionState.state, startTime, endTime, priority,subscriptionPrice,monetaryBalanceId, parameter1, parameter2, staffData);
	}

	public Subscription subscribeTopUpByName(String subscriberIdentity, String parentId,
											 String topUpName, SubscriptionState subscriptionState,
											 Long startTime, Long endTime, Integer priority,double subscriptionPrice ,String monetaryBalanceId , StaffData staffData,
											 String parameter1, String parameter2) throws OperationFailedException, UnauthorizedActionException {

		return ddfTable.subscribeQuotaTopUpByName(subscriberIdentity, parentId, topUpName, subscriptionState.state, startTime, endTime, priority,subscriptionPrice ,monetaryBalanceId, parameter1, parameter2, staffData);
	}

	public @Nullable Subscription getTopUpSubscriptionByAlternateId(String alternateIdentity, String topUpSubscriptionId, StaffData staffData) throws OperationFailedException, UnauthorizedActionException {
		return ddfTable.getQuotaTopUpSubscriptionByAlternateId(alternateIdentity, topUpSubscriptionId, staffData);
	}

	public @Nullable Subscription getTopUpSubscriptionBySubscriberId(String subscriberIdentity, String topUpSubscriptionId, StaffData staffData) throws OperationFailedException, UnauthorizedActionException {
		return ddfTable.getQuotaTopUpSubscriptionBySubscriberId(subscriberIdentity, topUpSubscriptionId, staffData);
	}

	public Subscription changeTopUpSubscriptionByAlternateId(String alternateId, String topUpSubscriptionId, SubscriptionState newSubscriptionState,
                                                             Long startTime, Long endTime, Integer priority, String rejectReason, StaffData staffData, String parameter1, String parameter2) throws UnauthorizedActionException, OperationFailedException {
		return ddfTable.updateQuotaTopUpSubscriptionByAlternateId(alternateId, topUpSubscriptionId, newSubscriptionState.state, startTime, endTime, priority, rejectReason, parameter1, parameter2, staffData);
	}

	public Subscription changeTopUpSubscription(String subscriberId, String topUpSubscriptionId, SubscriptionState subscriptionStatus, Long startTime,
                                                Long endTime, Integer priority, String rejectReason, StaffData staffData, String parameter1, String parameter2) throws OperationFailedException, UnauthorizedActionException {

		return ddfTable.updateQuotaTopUpSubscription(subscriberId, topUpSubscriptionId, subscriptionStatus.state, startTime, endTime, priority, rejectReason, parameter1, parameter2, staffData);
	}

	public List<Subscription> getTopUpSubscriptionsByAlternateId(String alternateId, StaffData staffData) throws OperationFailedException, UnauthorizedActionException {
		List<Subscription> topUpsusbcriptions = Collectionz.newArrayList();
		Map<String, Subscription> idToTopUpSubscription = ddfTable.getQuotaTopUpSubscriptionsbyAlternateId(alternateId, staffData);
		if(MapUtils.isNotEmpty(idToTopUpSubscription)) {
			for (Subscription topUpSubscription : idToTopUpSubscription.values()) {
				topUpsusbcriptions.add(topUpSubscription);
			}
		}
		return topUpsusbcriptions;
	}

	public List<Subscription> getTopUpSubscriptions(String subscriberIdentity, StaffData staffData) throws UnauthorizedActionException, OperationFailedException {

		List<Subscription> topUpsusbcriptions = Collectionz.newArrayList();
		Map<String, Subscription> idToTopUpSubscription = ddfTable.getTopUpSubscriptions(subscriberIdentity, staffData);
		if(MapUtils.isNotEmpty(idToTopUpSubscription)) {
			for (Subscription topUpSubscription : idToTopUpSubscription.values()) {
				topUpsusbcriptions.add(topUpSubscription);
			}
		}
		return topUpsusbcriptions;
	}

	public List<Subscription> getBodSubscriptions(String subscriberIdentity, StaffData staffData) throws UnauthorizedActionException, OperationFailedException {

		List<Subscription> bodSubscriptions = Collectionz.newArrayList();
		Map<String, Subscription> idToBodSubscription = ddfTable.getBodSubscriptions(subscriberIdentity, staffData);
		if(MapUtils.isNotEmpty(idToBodSubscription)) {
			for (Subscription bodSubscription : idToBodSubscription.values()) {
				bodSubscriptions.add(bodSubscription);
			}
		}
		return bodSubscriptions;
	}

	public void addExternalAlternateIdentity(String subscriberIdentity, String alternateIdentity,StaffData staffData) throws OperationFailedException,UnauthorizedActionException{
	    ddfTable.addExternalAlternateId(subscriberIdentity,alternateIdentity,staffData);
    }


	public SubscriberAlternateIds getExternalAlternateIds(String subscriberIdentity,StaffData staffData) throws OperationFailedException,UnauthorizedActionException{
		return ddfTable.getExternalAlternateIds(subscriberIdentity,staffData);
	}


	public int changingExternalAlternateIdentityStatus(String subscriberIdentity, String alternateId, String status,StaffData staffData) throws OperationFailedException,UnauthorizedActionException{
		return ddfTable.changeAlternateIdentityStatus(subscriberIdentity,alternateId,status,staffData);
	}

	public int removeExternalAlternateIdentity(String subscriberIdentity, String alternateId,StaffData staffData) throws OperationFailedException,UnauthorizedActionException{
		return ddfTable.removeExternalAlternateId(subscriberIdentity,alternateId,staffData);
	}


	public int updateExternalAlternateIdentity(String subscriberIdentity, String oldAlternateId, String updateAlternateId,StaffData staffData) throws OperationFailedException,UnauthorizedActionException{
		return ddfTable.updateExternalAlternateId(subscriberIdentity,oldAlternateId,updateAlternateId,staffData);
	}

	public void changeBillDay(String subscriberId, Timestamp nextBillDate, Timestamp billChangeDate, String requestIpAddress) throws OperationFailedException {
		ddfTable.changeBillDay(subscriberId, nextBillDate, billChangeDate, requestIpAddress);
	}


	public Subscription subscribeBodPackage(SubscriptionParameter subscriptionParameter, StaffData staffData, String requestIpAddress)
			throws OperationFailedException, UnauthorizedActionException {

		return ddfTable.subscribeBodPackage(subscriptionParameter, staffData, requestIpAddress);
	}

	public Subscription updateFnFGroup(SPRInfo sprInfo, Subscription subscription, StaffData staffData,String requestIpAddress) throws OperationFailedException,
			UnauthorizedActionException{
		return ddfTable.updateFnFGroup(sprInfo, subscription, staffData, requestIpAddress);
	}

	public void processReset(SPRInfo sprInfo, String requestIpAddress) throws OperationFailedException{
		ddfTable.processReset(sprInfo, requestIpAddress);
	}
}
