package com.elitecore.corenetvertex.spr;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Predicate;
import com.elitecore.corenetvertex.constants.ActionType;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.MonetaryBalanceType;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.constants.RateCardType;
import com.elitecore.corenetvertex.constants.RenewalIntervalUnit;
import com.elitecore.corenetvertex.constants.SubscriberStatus;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.alerts.Alerts;
import com.elitecore.corenetvertex.core.db.exception.DBDownException;
import com.elitecore.corenetvertex.core.transaction.Transaction;
import com.elitecore.corenetvertex.core.transaction.TransactionException;
import com.elitecore.corenetvertex.core.transaction.TransactionFactory;
import com.elitecore.corenetvertex.core.transaction.exception.TransactionErrorCode;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.bod.BoDPackage;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.offer.ProductOfferServicePkgRel;
import com.elitecore.corenetvertex.pm.pkg.datapackage.BasePackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.QuotaTopUp;
import com.elitecore.corenetvertex.pm.pkg.datapackage.SubscriptionPackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.imspackage.IMSPackage;
import com.elitecore.corenetvertex.pm.rnc.pkg.RnCPackage;
import com.elitecore.corenetvertex.pm.rnc.ratecard.NonMonetaryRateCard;
import com.elitecore.corenetvertex.pm.rnc.ratecard.RateCard;
import com.elitecore.corenetvertex.pm.rnc.rcgroup.RateCardGroup;
import com.elitecore.corenetvertex.spr.balance.SubscriptionInformation;
import com.elitecore.corenetvertex.spr.data.SPRFields;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.data.SubscriberDetails;
import com.elitecore.corenetvertex.spr.data.SubscriberInfo;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.data.SubscriptionDetail;
import com.elitecore.corenetvertex.spr.data.SubscriptionMetadata;
import com.elitecore.corenetvertex.spr.data.SubscriberEDRData;
import com.elitecore.corenetvertex.spr.data.SubscriptionEDRData;
import com.elitecore.corenetvertex.spr.ddf.SubscriptionParameter;
import com.elitecore.corenetvertex.spr.ddf.SubscriptionResult;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.spr.params.ChangeBaseProductOfferParams;
import com.elitecore.corenetvertex.spr.params.MonetaryRechargeData;
import com.elitecore.corenetvertex.spr.util.SPRInfoUtil;
import com.elitecore.corenetvertex.spr.util.SubscriberPkgValidationUtil;
import com.elitecore.corenetvertex.spr.util.SubscriptionEDRUtil;
import com.elitecore.corenetvertex.util.util.Predicates;
import org.apache.logging.log4j.ThreadContext;

import javax.annotation.Nullable;
import java.sql.SQLException;
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

import static com.elitecore.commons.base.Strings.isNullOrBlank;
import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.corenetvertex.constants.CommonConstants.ADD_MONETARY_BALANCE;
import static com.elitecore.corenetvertex.constants.CommonConstants.CHANGE_BILL_DAY;
import static com.elitecore.corenetvertex.constants.CommonConstants.CREATE_SUBSCRIBER;
import static com.elitecore.corenetvertex.constants.CommonConstants.DELETE_SUBSCRIBER;
import static com.elitecore.corenetvertex.constants.CommonConstants.IMPORT_SUBSCRIBER;
import static com.elitecore.corenetvertex.constants.CommonConstants.PURGE_SUBSCRIBER;
import static com.elitecore.corenetvertex.constants.CommonConstants.RESTORE_SUBSCRIBER;
import static com.elitecore.corenetvertex.constants.CommonConstants.SUBSCRIBE_ADDON_PRODUCT_OFFER;
import static com.elitecore.corenetvertex.constants.CommonConstants.SUBSCRIBE_BOD;
import static com.elitecore.corenetvertex.constants.CommonConstants.UPDATE_ADDON_PRODUCT_OFFER;
import static com.elitecore.corenetvertex.constants.CommonConstants.UPDATE_CREDIT_LIMIT;
import static com.elitecore.corenetvertex.constants.CommonConstants.UPDATE_FNF_GROUP;
import static com.elitecore.corenetvertex.constants.CommonConstants.UPDATE_MONETARY_BALANCE;
import static com.elitecore.corenetvertex.constants.CommonConstants.UPDATE_SUBSCRIBER;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class SubscriberRepositoryImpl implements SubscriberRepository {

	private static final String MODULE = "SP-REPO";
	private static final int PURGE_INTERNAL_ERROR = -1;
	private TransactionFactory transactionFactory;
	private UMOperation umOperation;	
	private ABMFOperation abmfOperation;
	private RnCABMFOperation rnCABMFOperation;
	private MonetaryABMFOperationImpl monetaryABMFOperationImpl;
	private SubscriptionOperationImpl subscriptionOperation;
	private TestSubscriberAwareSPInterface spInterface;
	private PolicyRepository policyRepository;
	private AlertListener alertListener;
	private String id;
	private String name;
	private List<String> groupIds;
	private SPRFields alternateIdField;
	private static final String SEVERITY = "";
	private EDRListener balanceEDRListener;
	private EDRListener subscriberEDRListener;
	private EDRListener subscriptionEDRListener;
	private String systemCurrency;
	private UsageProviderImpl usageProvider;
	private SubscriptionProviderImpl subscriptionProvider;
	private BalanceProviderImpl balanceProvider;

	public SubscriberRepositoryImpl(String id,
                                    String name,
                                    TransactionFactory transactionFactory,
                                    AlertListener alertListener,
                                    SPInterface spInterface,
                                    PolicyRepository policyRepository,
                                    UMOperation umOperation,
                                    ABMFconfiguration abmFconfiguration,
                                    TestSubscriberCache testSubscriberCache,
                                    List<String> groupIds,
                                    SPRFields alternateIdField,
                                    RecordProcessor<ABMFBatchOperation.BatchOperationData> abmfRecordProcessor,
                                    RecordProcessor<MonetaryABMFOperationImpl.MonetaryOperationData> monetaryAbmfRecordProcessor,
									RecordProcessor<RnCABMFBatchOperation.BatchOperationData> rnCAbmfRecordProcessor,
									EDRListener balanceEDRListener,
									EDRListener subscriberEDRListener,
									EDRListener subscriptionEDRListener,
									String systemCurrency) {

		this.id = id;
		this.name = name;
		this.transactionFactory = transactionFactory;
		this.policyRepository = policyRepository;
		this.alertListener = alertListener;
		this.groupIds = groupIds;
		this.alternateIdField = alternateIdField;
		this.umOperation = umOperation;
		this.balanceEDRListener = balanceEDRListener;
		this.subscriberEDRListener = subscriberEDRListener;
		this.subscriptionEDRListener = subscriptionEDRListener;
		this.systemCurrency = systemCurrency;

		if (abmFconfiguration.getBatchSize() > 1) {
			this.abmfOperation = new ABMFBatchOperation(transactionFactory, alertListener, policyRepository, abmfRecordProcessor, abmFconfiguration.getBatchSize(), abmFconfiguration.getBatchQueryTimeout(), abmFconfiguration.getQueryTimeout());
			this.rnCABMFOperation = new RnCABMFBatchOperation(transactionFactory, alertListener, policyRepository, rnCAbmfRecordProcessor, abmFconfiguration.getBatchSize(), abmFconfiguration.getBatchQueryTimeout(), abmFconfiguration.getQueryTimeout());
		} else {
			this.abmfOperation = new ABMFOperation(alertListener, policyRepository, abmFconfiguration.getQueryTimeout());
			this.rnCABMFOperation = new RnCABMFOperation(alertListener, policyRepository, abmFconfiguration.getQueryTimeout());
		}
		this.abmfOperation.init();
		this.rnCABMFOperation.init();

		this.monetaryABMFOperationImpl = new MonetaryABMFOperationImpl(alertListener, 1, monetaryAbmfRecordProcessor, this.balanceEDRListener);

		subscriptionOperation = new SubscriptionOperationImpl(alertListener, policyRepository, this.umOperation, this.abmfOperation, this.rnCABMFOperation, monetaryABMFOperationImpl, this.balanceEDRListener);

		if (testSubscriberCache != null) {
			this.spInterface = new TestSubscriberEnabledSPInterface(spInterface, policyRepository, testSubscriberCache);
		} else {
			this.spInterface = new TestSubscriberDisabledSPInterface(spInterface);
		}
		this.usageProvider = new UsageProviderImpl();
		this.subscriptionProvider = new SubscriptionProviderImpl();
		this.balanceProvider = new BalanceProviderImpl();

	}

	public SubscriberRepositoryImpl(String id,
									String name,
									TransactionFactory transactionFactory,
									AlertListener alertListener,
									PolicyRepository policyRepository,
									UMOperation umOperation,
									ABMFconfiguration abmFconfiguration,
									TestSubscriberCache testSubscriberCache,
									List<String> groupIds, SPRFields alternateIdField,
									RecordProcessor<ABMFBatchOperation.BatchOperationData> abmfRecordProcessor,
									RecordProcessor<MonetaryABMFOperationImpl.MonetaryOperationData> monetaryAbmfRecordProcessor,
									RecordProcessor<RnCABMFBatchOperation.BatchOperationData> rnCAbmfRecordProcessor,
									EDRListener balanceEDRListener,
									EDRListener subscriberEDRListener,
									EDRListener subscriptionEDRListener, String systemCurrency) {
		this(id, name, transactionFactory, alertListener,
				new NVDBSPInterface(alertListener, transactionFactory),
				policyRepository, umOperation, abmFconfiguration, testSubscriberCache, groupIds, alternateIdField,
				abmfRecordProcessor, monetaryAbmfRecordProcessor, rnCAbmfRecordProcessor, balanceEDRListener, subscriberEDRListener, subscriptionEDRListener, systemCurrency);
	}

	SubscriberRepositoryImpl(String id,
									String name,
									TransactionFactory transactionFactory,
									AlertListener alertListener,
									PolicyRepository policyRepository,
	     							UMOperation umOperation,
									ABMFconfiguration abmFconfiguration,
									List<String> groupIds,
									SPRFields alternateIdField,
									RecordProcessor<ABMFBatchOperation.BatchOperationData> abmfRecordProcessor,
									RecordProcessor<RnCABMFBatchOperation.BatchOperationData> rnCAbmfRecordProcessor,
									EDRListener balanceEDRListener,
									EDRListener subscriberEDRListener,
									EDRListener subscriptionEDRListener,
		     						String systemCurrency,TestSubscriberAwareSPInterface testSubscriberAwareSPInterface,MonetaryABMFOperationImpl monetaryABMFOperationImpl) {

		this.id = id;
		this.name = name;
		this.transactionFactory = transactionFactory;
		this.policyRepository = policyRepository;
		this.alertListener = alertListener;
		this.groupIds = groupIds;
		this.alternateIdField = alternateIdField;
		this.umOperation = umOperation;
		this.balanceEDRListener = balanceEDRListener;
		this.subscriberEDRListener = subscriberEDRListener;
		this.subscriptionEDRListener = subscriptionEDRListener;
		this.systemCurrency = systemCurrency;

		if (abmFconfiguration.getBatchSize() > 1) {
			this.abmfOperation = new ABMFBatchOperation(transactionFactory, alertListener, policyRepository, abmfRecordProcessor, abmFconfiguration.getBatchSize(), abmFconfiguration.getBatchQueryTimeout(), abmFconfiguration.getQueryTimeout());
			this.rnCABMFOperation = new RnCABMFBatchOperation(transactionFactory, alertListener, policyRepository, rnCAbmfRecordProcessor, abmFconfiguration.getBatchSize(), abmFconfiguration.getBatchQueryTimeout(), abmFconfiguration.getQueryTimeout());
		} else {
			this.abmfOperation = new ABMFOperation(alertListener, policyRepository, abmFconfiguration.getQueryTimeout());
			this.rnCABMFOperation = new RnCABMFOperation(alertListener, policyRepository, abmFconfiguration.getQueryTimeout());
		}
		this.abmfOperation.init();
		this.rnCABMFOperation.init();
		subscriptionOperation = new SubscriptionOperationImpl(alertListener, policyRepository, this.umOperation, this.abmfOperation, this.rnCABMFOperation, monetaryABMFOperationImpl, this.balanceEDRListener);
		this.spInterface = testSubscriberAwareSPInterface;
		this.usageProvider = new UsageProviderImpl();
		this.subscriptionProvider = new SubscriptionProviderImpl();
		this.balanceProvider = new BalanceProviderImpl();
		this.monetaryABMFOperationImpl=  monetaryABMFOperationImpl;
	}


		@Override
	public String getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.corenetvertex.spr.SubscriberRepositoryI#addTestSubscriber(java.lang.String)
	 */
	@Override
	public void addTestSubscriber(String subscriberIdentity) throws OperationFailedException {
		spInterface.addTestSubscriber(subscriberIdentity);
	}

	/* (non-Javadoc)
	 * @see com.elitecore.corenetvertex.spr.SubscriberRepositoryI#removeTestSubscriber(java.lang.String)
	 */
	@Override
	public int removeTestSubscriber(String subscriberIdentity) throws OperationFailedException {
		return spInterface.removeTestSubscriber(subscriberIdentity, subscriptionProvider);
	}

	/* (non-Javadoc)
	 * @see com.elitecore.corenetvertex.spr.SubscriberRepositoryI#removeTestSubscriber(java.util.List)
	 */
	@Override
	public int removeTestSubscriber(List<String> subscriberIdentities) throws OperationFailedException {
		return spInterface.removeTestSubscriber(subscriberIdentities, subscriptionProvider);
	}

	/* (non-Javadoc)
	 * @see com.elitecore.corenetvertex.spr.SubscriberRepositoryI#isTestSubscriber(java.lang.String)
	 */
	@Override
	public boolean isTestSubscriber(String subscriberIdentity) throws OperationFailedException {
		return spInterface.isTestSubscriber(subscriberIdentity);
	}

	/* (non-Javadoc)
	 * @see com.elitecore.corenetvertex.spr.SubscriberRepositoryI#getTestSubscriberIterator()
	 */
	@Override
	public Iterator<String> getTestSubscriberIterator() throws OperationFailedException {
		return spInterface.getTestSubscriberIterator();
	}

	/* (non-Javadoc)
	 * @see com.elitecore.corenetvertex.spr.SubscriberRepositoryI#refreshTestSubscriberCache()
	 */
	@Override
	public void refreshTestSubscriberCache() throws OperationFailedException {
		spInterface.refreshTestSubscriberCache();
	}

	/* (non-Javadoc)
	 * @see com.elitecore.corenetvertex.spr.SubscriberRepositoryI#getDeleteMarkedProfiles()
	 */
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
		}
		return deleteMarkedProfiles;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.corenetvertex.spr.SubscriberRepositoryI#markForDeleteProfile(java.lang.String)
	 */
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

	/* (non-Javadoc)
	 * @see com.elitecore.corenetvertex.spr.SubscriberRepositoryI#restoreProfile(java.lang.String)
	 */
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

	/* (non-Javadoc)
	 * @see com.elitecore.corenetvertex.spr.SubscriberRepositoryI#restoreProfile(java.util.List)
	 */
	@Override
	public Map<String, Integer> restoreProfile(List<String> subscriberIdentities) throws OperationFailedException {
		return spInterface.restoreProfile(subscriberIdentities);
	}

	/**
	 * IF subscriber status is DELETED THEN
	 * 	consider it as unknown user
	 * 
	 * @throws OperationFailedException
	 */
	@Override
	public SPRInfo getProfile(String subscriberIdentity) throws OperationFailedException {

		
		SPRInfoImpl sprInfo = null;
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

	/* (non-Javadoc)
	 * @see com.elitecore.corenetvertex.spr.SubscriberRepositoryI#getProfile(java.lang.String, com.elitecore.commons.base.Predicate, com.elitecore.commons.base.Predicate)
	 */
	@Override
	public @Nullable SPRInfo getProfile(String subscriberIdentity, Predicate<SPRInfo> primaryPredicate, Predicate<SPRInfo>... predicates) throws OperationFailedException {

		SPRInfoImpl sprInfo;
		try {
			sprInfo = (SPRInfoImpl) spInterface.getProfile(subscriberIdentity);
		} catch (DBDownException e) {
			throw new OperationFailedException(e, ResultCode.SERVICE_UNAVAILABLE);
		}
		
		if (sprInfo == null) {
			
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "No profile found for Subscriber Identity("+subscriberIdentity+")");
			}
			
			return null;
		}
		
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Subscriber profile found: " + sprInfo);
		}
		
		if(primaryPredicate.apply(sprInfo) == false) {
			return null;
		}

		if(predicates != null) {
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

	/* (non-Javadoc)
	 * @see com.elitecore.corenetvertex.spr.SubscriberRepositoryI#addProfile(com.elitecore.corenetvertex.spr.data.SPRInfo)
	 */
	@Override
	public void addProfile(SubscriberDetails subscriberDetails, String requestIpAddress) throws OperationFailedException {

		SPRInfo sprInfo = subscriberDetails.getSprInfo();
		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "Performing add subscriber operation for subscriber ID: " + sprInfo.getSubscriberIdentity());
		}

		validateDataAndRncAndIMSPkgs(sprInfo);
		validateCreditLimit(subscriberDetails.getCreditLimit());

		if (transactionFactory.isAlive() == false) {
			throw new OperationFailedException("Unable to add Subscriber for ID: " + sprInfo.getSubscriberIdentity()
					+ " . Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}
		
		Transaction transaction = transactionFactory.createTransaction();
		if (transaction == null) {
			throw new OperationFailedException("Unable to add Subscriber for ID: " + sprInfo.getSubscriberIdentity()
					+ " . Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}

		try {
			transaction.begin();
			spInterface.addProfile(sprInfo, transaction);

			String productOfferName = sprInfo.getProductOffer();
			ProductOffer productOffer = policyRepository.getProductOffer().byName(productOfferName);


			if(Objects.nonNull(productOffer.getDataServicePkgData())) {
				abmfOperation.addBalance(sprInfo.getSubscriberIdentity(), null, productOffer, transaction, sprInfo.getBillingDate());
				List<SubscriberUsage> usages = createUsageEntryForBasePackage(sprInfo.getSubscriberIdentity(), (BasePackage) productOffer.getDataServicePkgData(), productOffer.getId());
				if (Collectionz.isNullOrEmpty(usages) == false) {
					umOperation.insert(sprInfo.getSubscriberIdentity(), usages, transaction);
				}
			}


			List<ProductOfferServicePkgRel> productOfferServicePkgRelList = productOffer.getProductOfferServicePkgRelDataList();
			if(Collectionz.isNullOrEmpty(productOfferServicePkgRelList) == false) {
				for(ProductOfferServicePkgRel productOfferServicePkgRel : productOfferServicePkgRelList){
					rnCABMFOperation.addBalance(sprInfo.getSubscriberIdentity(), null, productOfferServicePkgRel.getRncPackageData(), transaction, productOffer.getId(),sprInfo.getBillingDate());
				}
			}

			if (Objects.nonNull(subscriberDetails.getCreditLimit()) || productOffer.getCreditBalance() != 0.0) {
				MonetaryBalance monetaryBalance = new MonetaryBalance(UUID.randomUUID().toString()
						, sprInfo.getSubscriberIdentity(), null
						//, 0
						, productOffer.getCreditBalance(), productOffer.getCreditBalance(),
						0, Objects.nonNull(subscriberDetails.getCreditLimit()) ? subscriberDetails.getCreditLimit() : 0l, 0, System.currentTimeMillis(), CommonConstants.FUTURE_DATE, productOffer.getCurrency()
						, MonetaryBalanceType.DEFAULT.name(), System.currentTimeMillis(), 0,null, null);

				addMonetaryBalance(new SubscriberMonetaryBalanceWrapper(monetaryBalance, null,
								sprInfo, CREATE_SUBSCRIBER, ActionType.ADD.name(),null)
						, "Initial credit limit " + subscriberDetails.getCreditLimit(), transaction);
			}

			if(nonNull(subscriberEDRListener)){
				SubscriberEDRData subscriberEDRData = prepareSubscriberEDRData(sprInfo,CREATE_SUBSCRIBER,ActionType.ADD.name(), requestIpAddress);
				subscriberEDRData.setCurrency(productOffer.getCurrency());
				subscriberEDRListener.subscriberEDR(subscriberEDRData);
			}

		} catch (TransactionException e) {

			ResultCode sprErrorCode = ResultCode.INTERNAL_ERROR;
			if (e.getErrorCode() == TransactionErrorCode.CONNECTION_NOT_FOUND) {

				alertListener.generateSystemAlert(SEVERITY, Alerts.DATABASE_CONNECTION_NOT_AVAILABLE, MODULE,
						"Unable to add subscriber with subscriber ID: " + sprInfo.getSubscriberIdentity() + " from DB with data source name: "
								+ transaction.getDataSourceName() + ". Reason: Connection not available");
				sprErrorCode = ResultCode.SERVICE_UNAVAILABLE;
			}
			transaction.rollback();
			throw new OperationFailedException("Unable to add subscriber with subscriber Id: " + sprInfo.getSubscriberIdentity()
					+ ". Reason: " + e.getMessage(), sprErrorCode, e);
		} catch (OperationFailedException e) {
			transaction.rollback();
			throw e;
		} catch (SQLException e) {
			transaction.rollback();
			throw new OperationFailedException("Unable to add subscriber with subscriber Id: " + sprInfo.getSubscriberIdentity()
					+ ". Reason: " + e.getMessage(), e);
		} finally {
			endTransaction(transaction);
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

	private List<SubscriberUsage> createUsageEntryForBasePackage(String subscriberId, BasePackage dataPackage, String productOfferId) {

		if (dataPackage.getQuotaProfileType() != QuotaProfileType.USAGE_METERING_BASED) {
			return Collections.emptyList();
		}

		List<SubscriberUsage> subscriberUsages = new ArrayList<SubscriberUsage>();
		for (QuotaProfile quotaProfile : dataPackage.getQuotaProfiles()) {
			Set<String> serviceTosubscriberUsage = new HashSet<String>(4);
			for (int level = 0; quotaProfile.getServiceWiseQuotaProfileDetails(level) != null; level++) {
				for (QuotaProfileDetail quotaProfileDetail : quotaProfile.getServiceWiseQuotaProfileDetails(level).values()) {
					addUsage(dataPackage, subscriberId, subscriberUsages, quotaProfile, serviceTosubscriberUsage, quotaProfileDetail.getServiceId(),productOfferId);
				}
			}
		}

		return subscriberUsages;
	}

	private void addUsage(BasePackage basePackage, String subscriberIdentity, List<SubscriberUsage> subscriberUsages, QuotaProfile quotaProfile,
						  Set<String> serviceToSubscriberUsage, String serviceId, String productOfferId) {

		if (serviceToSubscriberUsage.add(serviceId) == true) {
			SubscriberUsage.SubscriberUsageBuilder subscriberUsageBuilder = new SubscriberUsage.SubscriberUsageBuilder(SubscriberUsage.NEW_ID, subscriberIdentity, serviceId, quotaProfile
					.getId(), basePackage.getId(),productOfferId);

			if(quotaProfile.getRenewalInterval()!=0 && quotaProfile.getRenewalIntervalUnit()!=null){
				subscriberUsageBuilder.withBillingCycleResetTime(quotaProfile.getRenewalIntervalUnit().addTime(System.currentTimeMillis(), quotaProfile.getRenewalInterval()));
				subscriberUsageBuilder.withCustomResetTime(quotaProfile.getRenewalIntervalUnit().addTime(System.currentTimeMillis(), quotaProfile.getRenewalInterval()));
			}

			subscriberUsages.add(subscriberUsageBuilder.build());
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
				sprFieldMap.put(sprField, sprField.getNumericValue(sprInfo) == null ? null : sprField.getNumericValue(sprInfo)+"");
			} else if (Types.VARCHAR == sprField.type) {
				sprFieldMap.put(sprField, sprField.getStringValue(sprInfo));
			}
		}
		
		return sprFieldMap;
	}

	@Override
	public int 	updateProfile(String subscriberIdentity, EnumMap<SPRFields, String> updatedProfile, String requestIpAddress) throws OperationFailedException {
		int updateSuccessful;
		String productOfferName = null;
		String currentProductOfferName = getProfile(subscriberIdentity).getProductOffer();
		ProductOffer currentProductOffer = policyRepository.getProductOffer().base().byName(currentProductOfferName);

		if (updatedProfile.containsKey(SPRFields.PRODUCT_OFFER)) {
			productOfferName = updatedProfile.get(SPRFields.PRODUCT_OFFER);
			validateProductOffer(subscriberIdentity, productOfferName, currentProductOffer);
		}

		if (updatedProfile.containsKey(SPRFields.BILLING_DATE)) {
			Integer billingDate = getProfile(subscriberIdentity).getBillingDate();

			if(Objects.nonNull(productOfferName)) {
				validateTillBillDate(billingDate, subscriberIdentity, policyRepository.getProductOffer().base().byName(productOfferName));
			}
		}
		
		if (updatedProfile.containsKey(SPRFields.IMS_PACKAGE)) {
			String imsPackageName = updatedProfile.get(SPRFields.IMS_PACKAGE);
			validateIMSPackage(subscriberIdentity, imsPackageName);
		}
		updatedProfile.put(SPRFields.MODIFIED_DATE,String.valueOf(System.currentTimeMillis()));
		updateSuccessful = spInterface.updateProfile(subscriberIdentity, updatedProfile);

		if(isNullOrBlank(productOfferName) == false && Objects.equals(productOfferName, currentProductOfferName) == false) {
			ChangeBaseProductOfferParams params = new ChangeBaseProductOfferParams.Builder()
					.withSubscriberId(subscriberIdentity)
					.withCurrentProductOfferId(currentProductOffer.getId())
					.withNewProductOfferName(productOfferName)
					.withParam1(updatedProfile.get(SPRFields.PARAM1))
					.withParam2(updatedProfile.get(SPRFields.PARAM2))
					.withParam3(updatedProfile.get(SPRFields.PARAM3))
					.build();

			changeBaseProductOffer(params, requestIpAddress);
		}

		if(nonNull(subscriberEDRListener) && updateSuccessful>0){
			SPRInfoImpl sprInfo= SPRInfoUtil.createSPRInfoImpl(updatedProfile,subscriberIdentity);
			SubscriberEDRData subscriberEDRData = prepareSubscriberEDRData(sprInfo,UPDATE_SUBSCRIBER,ActionType.UPDATE.name(), requestIpAddress);
			subscriberEDRData.setCurrency(currentProductOffer.getCurrency());
			subscriberEDRListener.subscriberEDR(subscriberEDRData);
		}
		return updateSuccessful;
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

    private void validateProductOffer(String subscriberIdentity, String baseProductOffer, ProductOffer currentProductOffer) throws OperationFailedException {

        if (isNullOrBlank(baseProductOffer)) {
            throw new OperationFailedException("Subscriber(" + subscriberIdentity + ") update operation failed."
                    + " Reason: subscriber base product offer can not set to empty", ResultCode.NOT_FOUND);
        }

        ProductOffer productOffer = policyRepository.getProductOffer().base().byName(baseProductOffer);

        if (productOffer == null) {
            throw new OperationFailedException("Subscriber(" + subscriberIdentity + ") update operation failed."
                    + " Reason: subscribed base product offer(" + baseProductOffer + ") not found", ResultCode.NOT_FOUND);
        }

        if (productOffer.getPolicyStatus() == PolicyStatus.FAILURE) {
            throw new OperationFailedException("Subscriber(" + subscriberIdentity + ") update operation failed."
                    + " Reason: Base product offer(" + baseProductOffer + ")  is with policy status: " + productOffer.getPolicyStatus(), ResultCode.INVALID_INPUT_PARAMETER);
        }

        if (PkgStatus.ACTIVE != productOffer.getStatus()) {
            throw new OperationFailedException("Subscriber(" + subscriberIdentity + ") update operation failed."
                    + " Reason: Base product offer(" + baseProductOffer + ") found with "
                    + productOffer.getStatus() + " Status", ResultCode.INVALID_INPUT_PARAMETER);
        }

		if(currentProductOffer == null){
			throw new OperationFailedException("Subscriber(" + subscriberIdentity + ") update operation failed."
					+ " Reason: subscribed current base product offer not found", ResultCode.NOT_FOUND);
		}

		if(currentProductOffer.getCurrency().equals(productOffer.getCurrency()) == false){
			throw new OperationFailedException("Subscriber(" + subscriberIdentity + ") update operation failed."
					+ " Reason: New Base product offer(" + baseProductOffer + ") found with different currency", ResultCode.PRECONDITION_FAILED);
		}
	}
		
	@Override
	public int changeIMSPackage(String subscriberIdentity, String newPackageName, String parameter1, String parameter2, String parameter3) throws OperationFailedException {
		Transaction transaction = null;
		int updatedRows; 
		
		if (transactionFactory.isAlive() == false) {
			throw new OperationFailedException("Unable to change package for subscriber ID: " + subscriberIdentity
					+ ". Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
				}
				
		transaction = transactionFactory.createTransaction();
		if (transaction == null) {
			throw new OperationFailedException("Unable to change package subscriber ID: " +subscriberIdentity
					+ ". Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
				}
				
		try {
			
			transaction.begin();
			validateIMSPackage(subscriberIdentity, newPackageName);
			EnumMap<SPRFields, String> updateMap = new EnumMap<SPRFields, String>(SPRFields.class);
			updateMap.put(SPRFields.IMS_PACKAGE, newPackageName);
			updatedRows = spInterface.updateProfile(subscriberIdentity, updateMap, transaction);
		} catch (OperationFailedException e) {
			
			if (transaction != null) {
				transaction.rollback();
				} 
			
			throw e;
		} catch (TransactionException e) {
			ResultCode sprErrorCode = ResultCode.INTERNAL_ERROR;

			if (e.getErrorCode() == TransactionErrorCode.CONNECTION_NOT_FOUND) {

				alertListener.generateSystemAlert(SEVERITY, Alerts.DATABASE_CONNECTION_NOT_AVAILABLE, MODULE,
						"Unable to change IMS package for subscriber ID: " + subscriberIdentity + " to DB with Data Source name: "
								+ transaction.getDataSourceName() + ". Reason: Connection not available");

				sprErrorCode = ResultCode.SERVICE_UNAVAILABLE;
			}
			
			transaction.rollback();

			throw new OperationFailedException("Error while changing IMS package for subscriber ID: " + 
					subscriberIdentity + " from DB with datasource name: " + transaction.getDataSourceName() + ". Reason: " + e.getMessage(), sprErrorCode, e);
		} finally {
			if (transaction != null) {
				try {
					transaction.end();
				} catch (TransactionException e) { 
					getLogger().trace(MODULE, e);

		}
	}
		}

		return updatedRows;
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

        if (transactionFactory.isAlive() == false) {
            throw new OperationFailedException("Unable to change package for subscriber ID: " + subscriberIdentity
					+ " . Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        Transaction transaction = transactionFactory.createTransaction();
        if (transaction == null) {
            throw new OperationFailedException("Unable to change package for subscriber ID: " + subscriberIdentity
					+ " . Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        int updatedRows;
        try {
			
			transaction.begin();
			ProductOffer currentProductOffer = policyRepository.getProductOffer().base().byId(currentProductOfferId);
			validateProductOffer(subscriberIdentity, newProductOfferName, currentProductOffer);
			validateTillBillDate(getProfile(subscriberIdentity).getBillingDate(), subscriberIdentity, policyRepository.getProductOffer().base().byName(newProductOfferName));
			EnumMap<SPRFields, String> updateMap = new EnumMap<SPRFields, String>(SPRFields.class);
			updateMap.put(SPRFields.PRODUCT_OFFER, newProductOfferName);
			updatedRows = spInterface.updateProfile(subscriberIdentity, updateMap, transaction);
			if (updatedRows > 0) {
                handleCurrentUsageOrQuota(subscriberIdentity, currentProductOfferId, alternateId, param1, param2, param3, transaction);
                handleUsageOrQuota(subscriberIdentity, newProductOfferName, transaction);

                // add update monetary balance
				ProductOffer newProductOffer = policyRepository.getProductOffer().base().byName(newProductOfferName);
				addUpdateMonetaryBalance(getProfile(subscriberIdentity),newProductOffer,requestIpAddress, transaction);
			}
		} catch (OperationFailedException e) {
			
			if (transaction != null) {
				transaction.rollback();
			}
			
			throw e;
		} catch (TransactionException e) {
			ResultCode sprErrorCode = ResultCode.INTERNAL_ERROR;

			if (e.getErrorCode() == TransactionErrorCode.CONNECTION_NOT_FOUND) {

				alertListener.generateSystemAlert(SEVERITY, Alerts.DATABASE_CONNECTION_NOT_AVAILABLE, MODULE,
						"Unable to change data package for subscriber ID: " + subscriberIdentity + " to DB with Data Source name: "
								+ transaction.getDataSourceName() + ". Reason: Connection not available");

				sprErrorCode = ResultCode.SERVICE_UNAVAILABLE;
			}
			
			transaction.rollback();

			throw new OperationFailedException("Error while changing data package for subscriber ID: " + 
					subscriberIdentity + " from DB with datasource name: " + transaction.getDataSourceName() + ". Reason: " + e.getMessage(), sprErrorCode, e);
		} catch (SQLException e) {
			transaction.rollback();
			throw new OperationFailedException("Error while changing data package for subscriber ID: " +
					subscriberIdentity + " from DB with datasource name: " + transaction.getDataSourceName() + ". Reason: " + e.getMessage(), e);
		} finally {
			if (transaction != null) {
				try {
					transaction.end();
				} catch (TransactionException e) { 
					getLogger().trace(MODULE, e);
				}
			}
		}
		
		return updatedRows;
	}

    private void handleUsageOrQuota(String subscriberIdentity, String newProductOfferName, Transaction transaction) throws TransactionException, OperationFailedException {
        ProductOffer newProductOffer = policyRepository.getProductOffer().base().byName(newProductOfferName);

        if (newProductOffer.getDataServicePkgData() == null && newProductOffer.getProductOfferServicePkgRelDataList().isEmpty() == true) {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Skipping usage/quota provision of base product offer: " + newProductOfferName
                        + " for subscriber: " + subscriberIdentity + ". Reason: Data or RnC package not configured");
            }
            return;
        }

		if(newProductOffer.getDataServicePkgData() != null) {
			if (newProductOffer.getDataServicePkgData().getQuotaProfiles().isEmpty()) {
				if (getLogger().isInfoLogLevel()) {
					getLogger().info(MODULE, "Skipping usage/quota provision of base product offer: " + newProductOfferName
							+ " for subscriber: " + subscriberIdentity + ". Reason: Data package does not have quota profile configured");
				}
				return;
			}

			QuotaProfileType quotaProfileType = newProductOffer.getDataServicePkgData().getQuotaProfileType();

			if (QuotaProfileType.USAGE_METERING_BASED == quotaProfileType) {
				List<SubscriberUsage> usages = createUsageEntryForBasePackage(subscriberIdentity, (BasePackage)newProductOffer.getDataServicePkgData(), newProductOffer.getId());

				if (Collectionz.isNullOrEmpty(usages) == false) {
					umOperation.insert(subscriberIdentity, usages, transaction);
				}
			} else if (QuotaProfileType.RnC_BASED == quotaProfileType) {
				abmfOperation.addBalance(subscriberIdentity, null, newProductOffer, transaction, getProfile(subscriberIdentity).getBillingDate());
			}
		}

		if(newProductOffer.getProductOfferServicePkgRelDataList() != null) {
			for (ProductOfferServicePkgRel productOfferServicePkgRel : newProductOffer.getProductOfferServicePkgRelDataList()) {
				rnCABMFOperation.addBalance(subscriberIdentity, null, productOfferServicePkgRel.getRncPackageData(), transaction, newProductOffer.getId(), getProfile(subscriberIdentity).getBillingDate());
			}
		}

    }

	private void handleCurrentUsageOrQuota(String subscriberIdentity, String currentProductOfferId, String alternateId, String param1,
                                           String param2, String param3, Transaction transaction) throws OperationFailedException, TransactionException {
        if (currentProductOfferId == null) {
            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Skipping delete base product offer usage/quota for subscriber: " + subscriberIdentity
                        + ". Reason: Current product offer id not found");
            }
            return;
        }

        ProductOffer currentProductOffer = policyRepository.getProductOffer().base().byId(currentProductOfferId);

        if (currentProductOffer == null) {
            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Skipping delete base product offer usage/quota for subscriber: " + subscriberIdentity
                        + ". Reason: Current product offer not found with Id: "  + currentProductOfferId);
            }
            return;
        }

        if (currentProductOffer.getDataServicePkgData() == null && currentProductOffer.getProductOfferServicePkgRelDataList().isEmpty() == true) {
            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Skipping delete base product offer usage/quota for subscriber: " + subscriberIdentity
                        + ". Reason: Current product offer: " + currentProductOffer.getName() + " does not have data or rnc package attached");
            }
            return;
        }

        if(currentProductOffer.getDataServicePkgData() != null) {
			if (currentProductOffer.getDataServicePkgData().getQuotaProfiles().isEmpty()) {
				if (getLogger().isInfoLogLevel()) {
					getLogger().info(MODULE, "Skipping delete base product offer usage/quota for subscriber: " + subscriberIdentity
							+ ". Reason: Data package does not have quota profile configured");
				}
				return;
			}

			QuotaProfileType quotaProfileType = currentProductOffer.getDataServicePkgData().getQuotaProfileType();
			if (QuotaProfileType.USAGE_METERING_BASED == quotaProfileType) {
				umOperation.scheduleForUsageDelete(subscriberIdentity, alternateId, currentProductOfferId, "TO DELETE USAGE", param1,
						param2, param3, transaction);
			} else if (QuotaProfileType.RnC_BASED == quotaProfileType) {
				abmfOperation.expireQuota(subscriberIdentity, currentProductOffer.getId(), transaction);
			} else {
				if (getLogger().isInfoLogLevel()) {
					getLogger().info(MODULE, "Skipping delete base product offer usage/quota for subscriber: " + subscriberIdentity
							+ ". Reason: Current product offer: " + currentProductOffer.getName() + " is of Quota Profile Type: " + quotaProfileType);
				}
			}
		}

		if(currentProductOffer.getProductOfferServicePkgRelDataList() != null && currentProductOffer.getProductOfferServicePkgRelDataList().isEmpty() == false){
			rnCABMFOperation.expireNonMonetaryBalance(subscriberIdentity, currentProductOfferId, transactionFactory);
		}
    }

	/* (non-Javadoc)
     * @see com.elitecore.corenetvertex.spr.SubscriberRepositoryI#purgeProfile(java.lang.String)
     */
	@Override
	public int purgeProfile(String subscriberIdentity, String requestIpAddress) throws OperationFailedException {

		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "Purging subscriber(" + subscriberIdentity + ")");
		}

		if (transactionFactory.isAlive() == false) {
			throw new OperationFailedException("Unable to purge profile for subscriber(" + subscriberIdentity + ")"
					+ " Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}

		Transaction transaction = transactionFactory.createTransaction();

		if (transaction == null) {
			throw new OperationFailedException("Unable to purge profile for subscriber(" + subscriberIdentity + ")"
					+ " Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}

		int purgeCount;
		try {
			transaction.begin();

			purgeCount = purgeSubscriber(subscriberIdentity, transaction, requestIpAddress);
			if (getLogger().isInfoLogLevel()) {
				getLogger().info(MODULE, "Subscriber(" + subscriberIdentity + ") purged successfully");
			}
		} catch (TransactionException e) {
			ResultCode sprErrorCode = ResultCode.INTERNAL_ERROR;

			if (e.getErrorCode() == TransactionErrorCode.CONNECTION_NOT_FOUND) {

				alertListener.generateSystemAlert("", Alerts.DATABASE_CONNECTION_NOT_AVAILABLE, MODULE,
						"Unable to purge profile for subscriber(" + subscriberIdentity + ") to DB with Data Source name: "
								+ transaction.getDataSourceName() + ". Reason: Connection not available");

				sprErrorCode = ResultCode.SERVICE_UNAVAILABLE;
			}

			transaction.rollback();
			throw new OperationFailedException("Unable to purge profile for subscriber(" + subscriberIdentity + "). Reason: " + e.getMessage(), sprErrorCode, e);
		} catch (OperationFailedException e) {
			transaction.rollback();
			throw new OperationFailedException("Unable to purge profile for subscriber(" + subscriberIdentity + "). Reason: " + e.getMessage(), e.getErrorCode(), e);
		} finally {
			endTransaction(transaction);
		}

		return purgeCount;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.corenetvertex.spr.SubscriberRepositoryI#purgeProfile(java.util.List)
	 */
	@Override
	public Map<String, Integer> purgeProfile(List<String> subscriberIdentities, String requestIpAddress) throws OperationFailedException {

		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "Subscriber purge multiple operation started");
		}

		if (transactionFactory.isAlive() == false) {
			throw new OperationFailedException("Unable to purge subscribers."
					+ " Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}

		Map<String, Integer> subscriberIdToPurgeCountMap = new HashMap<String, Integer>(subscriberIdentities.size());

		try {
			for (String subscriberIdentity : subscriberIdentities) {
				
				purgeProfileWithBestEffort(subscriberIdToPurgeCountMap, subscriberIdentity, requestIpAddress);
			}

		} catch (OperationFailedException e) {
			getLogger().error(MODULE, "Error while purge subscribers. Reason: " + e.getMessage());
			if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
			getLogger().trace(MODULE, e);
			}
			return subscriberIdToPurgeCountMap;
		}
		
		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "Subscriber purge multiple operation completed");
		}

		return subscriberIdToPurgeCountMap;
	}

	private int purgeSubscriber(String subscriberIdentity, Transaction transaction, String requestIpAddress)
			throws OperationFailedException, TransactionException {

        SPRInfo sprInfo = getProfile(subscriberIdentity, DeleteMarkedSubscriberPredicate.getInstance());
		if(isNull(sprInfo)){
			throw new OperationFailedException("Skipping purge for subscriber(" + subscriberIdentity + "), subscriber not found for purge.", ResultCode.NOT_FOUND);
		}

		umOperation.deleteBaseAndPromotionalPackageUsage(subscriberIdentity, transaction);
		int deleteCount = getMonetaryABMFOperation().deleteMonetaryBalance(subscriberIdentity, transaction);
		abmfOperation.deleteDataBalance(subscriberIdentity, transaction);
		rnCABMFOperation.deleteRnCBalance(subscriberIdentity, transaction);
		subscriptionOperation.unsubscribeBySubscriberId(subscriberIdentity, transaction);

		int purgeCount = spInterface.purgeProfile(subscriberIdentity, transaction);

		if (purgeCount > 0) {
			subscriberEDRListener.deleteSubscriberEDR(sprInfo, PURGE_SUBSCRIBER, ActionType.DELETE.name(), requestIpAddress);
		}

		if(deleteCount > 0 && nonNull(balanceEDRListener)) {
			for(MonetaryBalance monetaryBalance : sprInfo.getMonetaryBalance(Predicates.ALL_MONETARY_BALANCE).getAllBalance()) {
				balanceEDRListener.deleteMonetaryEDR(new SubscriberMonetaryBalanceWrapper(monetaryBalance, null, sprInfo, PURGE_SUBSCRIBER, ActionType.DELETE.name(),null),
						requestIpAddress, PURGE_SUBSCRIBER);
			}
		}

		return purgeCount;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.corenetvertex.spr.SubscriberRepositoryI#purgeAllProfile()
	 */
	@Override
	public Map<String, Integer> purgeAllProfile(String requestIpAddress) throws OperationFailedException {

		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "Subscriber purge all profile operation started");
		}

		if (transactionFactory.isAlive() == false) {
			throw new OperationFailedException("Unable to purge all subscribers."
					+ " Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}

		Map<String, Integer> subscriberIdToPurgeCountMap = new HashMap<String, Integer>();

		List<SPRInfo> deleteMarkedProfiles = spInterface.getDeleteMarkedProfiles();
		
		try {
			for (SPRInfo sprInfo : deleteMarkedProfiles) {
				purgeProfileWithBestEffort(subscriberIdToPurgeCountMap, sprInfo.getSubscriberIdentity(), requestIpAddress);
			}
		} catch (OperationFailedException e) {
			getLogger().error(MODULE, "Error while purge subscribers. Reason: " + e.getMessage());
			if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
			getLogger().trace(MODULE, e);
			}
			return subscriberIdToPurgeCountMap;
		}
		
		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "Subscriber purge all operation completed");
		}

		return subscriberIdToPurgeCountMap;
	}

	
	/*
	 * In below scenario Operation will be failed,
	 * 
	 *  DB down exceptions
	 */
	private void purgeProfileWithBestEffort(Map<String, Integer> subscriberIdToPurgeCountMap, String subscriberIdentity, String requestIpAddress)
			throws OperationFailedException {

		Transaction transaction = transactionFactory.createTransaction();
		
		if (transaction == null) {
			throw new OperationFailedException("Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}
		
		try {
			transaction.begin();
			subscriberIdToPurgeCountMap.put(subscriberIdentity, purgeSubscriber(subscriberIdentity, transaction, requestIpAddress));
			if (getLogger().isInfoLogLevel()) {
				getLogger().info(MODULE, "Subscriber(" + subscriberIdentity + ") purged successfully");
			}
		} catch (TransactionException e) {
			transaction.rollback();

			if (e.getErrorCode() == TransactionErrorCode.CONNECTION_NOT_FOUND) {

				alertListener.generateSystemAlert("", Alerts.DATABASE_CONNECTION_NOT_AVAILABLE, MODULE,
						"Unable to purge profile for subscribers to DB with Data Source name: "
								+ transaction.getDataSourceName() + ". Reason: Connection not available");
			} 

			printLogSkippingPurgeforSubscriber(subscriberIdentity, e);
			subscriberIdToPurgeCountMap.put(subscriberIdentity, PURGE_INTERNAL_ERROR);
			
		} catch (OperationFailedException e) {
			transaction.rollback();

			printLogSkippingPurgeforSubscriber(subscriberIdentity, e);
			subscriberIdToPurgeCountMap.put(subscriberIdentity, PURGE_INTERNAL_ERROR);

		} finally {
			endTransaction(transaction);
		}
	}

	private void printLogSkippingPurgeforSubscriber(String subscriberIdentity, Exception e) {
		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "Purge operation skipped for subscriber(" + subscriberIdentity + "). Reason: " + e.getMessage());
		}

		getLogger().error(MODULE, e.getMessage());
		getLogger().trace(MODULE, e);
	}

	/* (non-Javadoc)
	 * @see com.elitecore.corenetvertex.spr.SubscriberRepositoryI#subscribeAddOnProductOfferById(java.lang.String, java.lang.String, java.lang.String, java.lang.Integer, java.lang.Long, java.lang.Long, java.lang.String, java.lang.String)
	 */
	@Override
	public List<Subscription> subscribeAddOnProductOfferById(SPRInfo sprInfo, String parentId,
															 String addOnProductOfferId,
															 Integer subscriptionStatusValue, Long startTime,
															 Long endTime, Integer priority, String param1,
															 String param2, Integer billDay, String requestIpAddress,
															 SubscriptionMetadata subscriptionMetadata)
			throws OperationFailedException {

		SubscriptionResult subscriptionResult = subscribeAddOnProductOfferById(new SubscriptionParameter(sprInfo, parentId, addOnProductOfferId, subscriptionStatusValue, null, null, null, startTime, endTime, priority, param1, param2, billDay, subscriptionMetadata), requestIpAddress);

		return subscriptionResult.getSubscriptions();
	}
	

	/* (non-Javadoc)
	 * @see com.elitecore.corenetvertex.spr.SubscriberRepositoryI#updateSubscription(java.lang.String, java.lang.Integer, java.lang.Long, java.lang.Long, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public Subscription updateSubscription(
			SPRInfo sprInfo,
			String subscriptionId,
			Integer subscriptionStatusValue,
			Long startTime,
			Long endTime,
			Integer priority, String rejectReason,
			String param1,
			String param2, String requestIpAddress
	) throws OperationFailedException {
		Subscription updatedSubscription = subscriptionOperation
				.updateSubscription(sprInfo.getSubscriberIdentity(), subscriptionId, subscriptionStatusValue, startTime, endTime, priority, rejectReason,transactionFactory);
		if(nonNull(subscriptionEDRListener)) {
			ProductOffer productOffer = policyRepository.getProductOffer().byName(sprInfo.getProductOffer());
			if (productOffer != null){
				SubscriptionEDRData subscriptionEDRData = SubscriptionEDRUtil.prepareUpdateAddonSubscriptionEDRData(sprInfo, updatedSubscription, startTime, endTime, rejectReason, UPDATE_ADDON_PRODUCT_OFFER, requestIpAddress);
			subscriptionEDRData.setCurrency(productOffer.getCurrency());

			subscriptionEDRListener.subscriptionEDR(subscriptionEDRData);
		}
		}

		return updatedSubscription;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.corenetvertex.spr.SubscriberRepositoryI#subscribeProductOfferAddOnByName(java.lang.String, java.lang.String, java.lang.String, java.lang.Integer, java.lang.Long, java.lang.Long, java.lang.String, java.lang.String)
	 */
	@Override
	public List<Subscription> subscribeProductOfferAddOnByName(SPRInfo sprInfo, String parentId, String addonName,
															   Integer subscriptionStatusValue, Long startTime, Long endTime,
															   Integer priority, String param1, String param2,
															   Integer billDay, SubscriptionMetadata subscriptionMetadata)
			throws OperationFailedException {

		SubscriptionPackage subscrptionPackage = policyRepository.getActiveAddOnByName(addonName);

		String subscriberIdentity = sprInfo.getSubscriberIdentity();
		if (subscrptionPackage != null && PkgStatus.ACTIVE != subscrptionPackage.getAvailabilityStatus()) {
			throw new OperationFailedException("Unable to subscribe addOn(" + subscrptionPackage.getName() + ") for subscriber ID: " + subscriberIdentity
					+ " Reason: AddOn found with "
					+ subscrptionPackage.getAvailabilityStatus() + " Status", ResultCode.NOT_FOUND);
		}

		/* if LIVE subscriber, not allow to subscriber TEST addon */
		if (spInterface.isTestSubscriber(subscriberIdentity) == false && subscrptionPackage != null && PkgMode.TEST == subscrptionPackage.getMode()) {
			throw new OperationFailedException("Unable to subscriber addOn(" + subscrptionPackage.getName() + ")."
					+ " Reason: Live subscriber must not have subscription of test addOn", ResultCode.INVALID_INPUT_PARAMETER);
		}

		SubscriptionResult subscriptionResult = subscriptionOperation
				.subscribeProductOfferByName(new SubscriptionParameter(sprInfo, parentId, null,
						subscriptionStatusValue, addonName, null, null, startTime, endTime, priority, param1, param2,
						billDay,subscriptionMetadata), transactionFactory);

		return subscriptionResult.getSubscriptions();
	}

	/* (non-Javadoc)
	 * @see com.elitecore.corenetvertex.spr.SubscriberRepositoryI#replace(java.lang.String, java.util.Collection)
	 */
	@Override
	public void replace(String subscriberIdentity, Collection<SubscriberUsage> usages) throws OperationFailedException {
		umOperation.replace(subscriberIdentity, usages, transactionFactory);
	}

	/* (non-Javadoc)
	 * @see com.elitecore.corenetvertex.spr.SubscriberRepositoryI#addToExisting(java.lang.String, java.util.Collection)
	 */
	@Override
	public void addToExisting(String subscriberIdentity, Collection<SubscriberUsage> usages) throws OperationFailedException {
		umOperation.addToExisting(subscriberIdentity, usages, transactionFactory);
	}

	/* (non-Javadoc)
	 * @see com.elitecore.corenetvertex.spr.SubscriberRepositoryI#insertNew(java.lang.String, java.util.Collection)
	 */
	@Override
	public void insertNew(String subscriberIdentity, Collection<SubscriberUsage> usages) throws OperationFailedException {
		umOperation.insert(subscriberIdentity, usages, transactionFactory);
	}

	/* (non-Javadoc)
	 * @see com.elitecore.corenetvertex.spr.SubscriberRepositoryI#getSubscriptions(java.lang.String)
	 */
	@Override
	public LinkedHashMap<String, Subscription> getAddonSubscriptions(String subscriberIdentity) throws OperationFailedException {
		return subscriptionOperation.getSubscriptions(subscriberIdentity, transactionFactory);
	}

	@Override
	public LinkedHashMap<String, Subscription> getAddonSubscriptions(SPRInfo sprInfo) throws OperationFailedException {
		return subscriptionOperation.getSubscriptions(sprInfo, transactionFactory);
	}

	@Override
	public Subscription getAddonSubscriptionsBySubscriptionId(
			String subscriberIdentity, String subscriptionId) throws OperationFailedException {
		return subscriptionOperation.getActiveSubscriptionBySubscriptionId(subscriptionId, System.currentTimeMillis(), transactionFactory);
	}

	/**
	 * Gives current usage(of all subscribed addOns and packages) based on subscriber identity provided
	 * 
	 * Current-usage map structure
	 * Map <Subscription Id/ package Id,
	 * 
	 * 		Map <"QuotaPrifile Id + SEPARATOR + Service Id", 
	 * 					
	 * 				Subscriber Usage>>
	 */
	@Override
	public Map<String, Map<String, SubscriberUsage>> getCurrentUsage(String subscriberIdentity) throws OperationFailedException {
		return umOperation.getUsage(subscriberIdentity,
				getAddonSubscriptions(subscriberIdentity),
				transactionFactory);
	}
	
	@Override
	public List<SubscriptionInformation> getBalance(SPRInfo sprInfo, String pkgId, String subscriptionId) throws OperationFailedException {
		return umOperation.getBalance(sprInfo, pkgId, subscriptionId, getAddonSubscriptions(sprInfo.getSubscriberIdentity()), transactionFactory);
	}
	
	@Override
	public List<SubscriptionInformation> getBalance(SPRInfo sprInfo) throws OperationFailedException {
		return umOperation.getBalance(sprInfo, getAddonSubscriptions(sprInfo.getSubscriberIdentity()), transactionFactory);
	}

	@Override
	public SubscriberNonMonitoryBalance getRGNonMonitoryBalanceWithResetExpiredBalance(SPRInfo sprInfo, String pkgId, String subscriptionId) throws OperationFailedException {
		return abmfOperation.getNonMonitoryBalanceWithResetExpiredBalance(sprInfo, pkgId, subscriptionId, getAddonSubscriptions(sprInfo.getSubscriberIdentity()), transactionFactory);
	}

	@Override
	public SubscriberRnCNonMonetaryBalance getNonMonetaryBalanceForRnCPackage(SPRInfo sprInfo, String pkgId, String subscriptionId) throws OperationFailedException {
		return rnCABMFOperation.getNonMonetaryBalance(sprInfo.getSubscriberIdentity(), pkgId, subscriptionId, transactionFactory);
	}

	@Override
	public SubscriberNonMonitoryBalance getRGNonMonitoryBalance(SPRInfo spr) throws OperationFailedException {
		return abmfOperation.getNonMonitoryBalance(spr, transactionFactory);
	}

	@Override
	public SubscriberRnCNonMonetaryBalance getRnCNonMonetaryBalance(String subscriberIdentity) throws OperationFailedException {
		return rnCABMFOperation.getNonMonetaryBalance(subscriberIdentity, null, null, transactionFactory);
	}

	/**
	 * 
	 * 
	 * Gives current Usage of provided subscription and all base packages of subscriber id provided
	 * @param subscriberIdentity
	 * @param subscriptions
	 * @return
	 * * Current-usage map structure
	 * Map <Subscription Id/ package Id,
	 * 
	 * 		Map <"QuotaPrifile Id + SEPARATOR + Service Id", 
	 * 					
	 * 				Subscriber Usage>>
	 * @throws OperationFailedException 
	 */
	public Map<String, Map<String, SubscriberUsage>> getCurrentUsage(String subscriberIdentity, 
			LinkedHashMap<String, Subscription> subscriptions) throws OperationFailedException {
		return umOperation.getUsage(subscriberIdentity, subscriptions, transactionFactory);
	}
	
	public Map<String, Map<String, SubscriberUsage>> getCurrentUsage(SPRInfo sprInfo) throws OperationFailedException{
		return umOperation.getUsage(sprInfo, transactionFactory);
	}
	
	/**
	 * reads profile from source(LDAP or DB)
	 * 
	 * IF profile not found or status is DELETED THEN
	 * 	creates unknown user profile
	 */
	
	public List<String> getGroupIds() {
		return groupIds;
	}
	
	@Override
	public SPRFields getAlternateIdField() {
		return alternateIdField;
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
			return SubscriberRepositoryImpl.this.getCurrentUsage(sprInfo);
		}

		@Override
		public void insertNew(String subscriberIdentity, Collection<SubscriberUsage> usages) throws OperationFailedException {
			SubscriberRepositoryImpl.this.insertNew(subscriberIdentity, usages);
		}

		@Override
		public void addToExisting(String subscriberIdentity, Collection<SubscriberUsage> usages) throws OperationFailedException {
			SubscriberRepositoryImpl.this.addToExisting(subscriberIdentity, usages);
		}

		@Override
		public void replace(String subscriberIdentity, Collection<SubscriberUsage> usages) throws OperationFailedException {
			SubscriberRepositoryImpl.this.replace(subscriberIdentity, usages);
		}
	}

	private class BalanceProviderImpl implements BalanceProvider {

		@Override
		public SubscriberMonetaryBalance getMonetaryBalance(String subscriberId, java.util.function.Predicate<MonetaryBalance> predicate) throws OperationFailedException {
			return SubscriberRepositoryImpl.this.getMonetaryBalance(subscriberId, predicate);
		}

		@Override
		public SubscriberNonMonitoryBalance getNonMonetaryBalance(SPRInfo sprInfo) throws OperationFailedException {
			return SubscriberRepositoryImpl.this.getRGNonMonitoryBalance(sprInfo);
		}

		@Override
		public SubscriberRnCNonMonetaryBalance getRnCNonMonetaryBalance(String subscriberId) throws OperationFailedException {
			return SubscriberRepositoryImpl.this.getRnCNonMonetaryBalance(subscriberId);
		}
	}

	@Override
	public void stop() {
		umOperation.stop();
		abmfOperation.stop();
		rnCABMFOperation.stop();
	}

	@Override
	public void resetBillingCycle(String subscriberID, String alternateID, String packageId,
			long resetBillingCycleDate, String resetReason, String parameter1,
			String parameter2, String parameter3) throws OperationFailedException {
		umOperation.resetBillingCycle(subscriberID,alternateID,packageId,
			resetBillingCycleDate, resetReason,parameter1,
			parameter2,parameter3, transactionFactory);
		
	}
	
	@Override
	public void importSubscriber(SubscriberInfo subscriberInfo) throws OperationFailedException{
		
		SPRInfo sprInfo = subscriberInfo.getSprInfo();
		String subscriberIdentity = sprInfo.getSubscriberIdentity();
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Importing subscriber with identity: " + subscriberIdentity);
		}

		//Transaction from the sp interface will never be null
		Transaction transaction = spInterface.createTransaction();
		try {
			transaction.begin();
			validatePackageAvailability(sprInfo);
			spInterface.addProfile(sprInfo, transaction);
			
			subscriptionOperation.importSubscriptionsAndUsages(sprInfo, subscriberInfo, transaction);

			if(nonNull(subscriberEDRListener)){
				subscriberEDRListener.addSubscriberEDR(sprInfo, IMPORT_SUBSCRIBER, ActionType.ADD.name(), null);
			}

		} catch (OperationFailedException e) {
			
			if (transaction != null) {
				transaction.rollback();
			}
			
			throw e;
		} catch (TransactionException e) {
			ResultCode sprErrorCode = ResultCode.INTERNAL_ERROR;

			if (e.getErrorCode() == TransactionErrorCode.CONNECTION_NOT_FOUND) {

				
				alertListener.generateSystemAlert(SEVERITY, Alerts.DATABASE_CONNECTION_NOT_AVAILABLE,MODULE,
						"Unable to import subscriber with subscriber ID: " + subscriberIdentity + " from DB with data source name: "
								+ transaction.getDataSourceName() + ". Reason: Connection not available");
				sprErrorCode = ResultCode.SERVICE_UNAVAILABLE;
			}
			
			transaction.rollback();
			
			throw new OperationFailedException("Unable to import subscriber with subscriber Id: " + subscriberIdentity
					+ ". Reason: " + e.getMessage(), sprErrorCode, e);
		} finally {
			endTransaction(transaction);
		}
		
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Subscriber imported with identity: " + sprInfo.getSubscriberIdentity());
		}
		
	}

	private void validatePackageAvailability(SPRInfo sprInfo) throws OperationFailedException {
		String baseProductOfferName = sprInfo.getProductOffer();

		ProductOffer productOffer = policyRepository.getProductOffer().byName(baseProductOfferName);

		if (Objects.isNull(productOffer)) {
			throw new OperationFailedException("Subscriber(" + sprInfo.getSubscriberIdentity() + ") add operation failed."
					+ " Reason: subscribed product offer(" + baseProductOfferName + ")  not found", ResultCode.NOT_FOUND);
		}

		BasePackage basePkg = (BasePackage) productOffer.getDataServicePkgData();

		if (basePkg == null) {
			throw new OperationFailedException("Subscriber(" + sprInfo.getSubscriberIdentity() + ") add operation failed."
					+ " Reason: Data package not associated with product offer (" + baseProductOfferName + ")  not found", ResultCode.NOT_FOUND);
		}

		String imsPackageName = sprInfo.getImsPackage();

		if (isNullOrBlank(imsPackageName) == false) {

			IMSPackage imsPkg = policyRepository.getIMSPkgByName(imsPackageName);
			if (imsPkg == null) {
				throw new OperationFailedException("Subscriber(" + sprInfo.getSubscriberIdentity() + ") add operation failed."
						+ " Reason: subscribed IMS package(" + imsPackageName + ")  not found", ResultCode.NOT_FOUND);
			}
		}
	}

	private void endTransaction(@Nullable Transaction transaction) {
		try {
			if (transaction != null) {
				transaction.end();
			}
		} catch (TransactionException e) {
			getLogger().trace(MODULE, e);
		}
	}
	
	private void validateDataAndRncAndIMSPkgs(SPRInfo sprInfo) throws OperationFailedException {

		String productOfferName = sprInfo.getProductOffer();
		ProductOffer productOffer = policyRepository.getProductOffer().byName(productOfferName);

		if (productOffer == null) {
			throw new OperationFailedException("Subscriber(" + sprInfo.getSubscriberIdentity() + ") add operation failed."
					+ " Reason: subscribed base product offer(" + productOfferName + ")  not found", ResultCode.NOT_FOUND);
		}

		if (PolicyStatus.FAILURE == productOffer.getPolicyStatus()) {
			throw new OperationFailedException("Subscriber(" + sprInfo.getSubscriberIdentity() + ") add operation failed."
					+ " Reason: Base product offer(" + productOfferName + ")  is failed base product offer", ResultCode.INVALID_INPUT_PARAMETER);
		}

		if (PkgStatus.ACTIVE != productOffer.getStatus()) {
			throw new OperationFailedException("Subscriber(" + sprInfo.getSubscriberIdentity() + ") add operation failed."
					+ " Reason: subscribed product offer(" + productOfferName + ") found with "
					+ productOffer.getStatus() + " Status", ResultCode.NOT_FOUND);
		}

		if(Objects.nonNull(productOffer.getDataServicePkgData())){
			BasePackage basePkg = (BasePackage) productOffer.getDataServicePkgData();
			SubscriberPkgValidationUtil.validateBasePackage(basePkg,basePkg.getName(),sprInfo.getSubscriberIdentity(),SubscriberPkgValidationUtil.SUBSCRIBER_PROFILE_ADD_OPERATION);
		}

		validateTillBillDate(sprInfo.getBillingDate(), sprInfo.getSubscriberIdentity(), productOffer);

		String imsPackageName = sprInfo.getImsPackage();
		if (isNullOrBlank(imsPackageName) == false) {
			
			IMSPackage imsPkg = policyRepository.getIMSPkgByName(imsPackageName);
			if (imsPkg == null) {
				throw new OperationFailedException("Subscriber(" + sprInfo.getSubscriberIdentity() + ") add operation failed."
						+ " Reason: subscribed IMS package(" + imsPackageName + ")  not found", ResultCode.NOT_FOUND);
			}
			
			if (imsPkg.getStatus() == PolicyStatus.FAILURE) {
				throw new OperationFailedException("Subscriber(" + sprInfo.getSubscriberIdentity() + ") add operation failed."
						+ " Reason: IMS Base package(" + imsPackageName + ")  is failed base package", ResultCode.INVALID_INPUT_PARAMETER);
			}
			
			if (PkgStatus.ACTIVE != imsPkg.getAvailabilityStatus()) {
				throw new OperationFailedException("Subscriber(" + sprInfo.getSubscriberIdentity() + ") add operation failed."
						+ " Reason: subscribed IMS package(" + imsPackageName + ")  found with " 
						+ imsPkg.getAvailabilityStatus() + " Status", ResultCode.NOT_FOUND);
			}
		}

		List<ProductOfferServicePkgRel> productOfferServicePkgRelList = productOffer.getProductOfferServicePkgRelDataList();
		if(Collectionz.isNullOrEmpty(productOfferServicePkgRelList) == false) {
			for (ProductOfferServicePkgRel productOfferServicePkgRel : productOfferServicePkgRelList) {

				RnCPackage rnCPackage = productOfferServicePkgRel.getRncPackageData();

				if (rnCPackage != null) {

					if (rnCPackage.getPolicyStatus() == PolicyStatus.FAILURE) {
						throw new OperationFailedException("Subscriber(" + sprInfo.getSubscriberIdentity() + ") add operation failed."
								+ " Reason: rnc package(" + rnCPackage.getName() + ")  is failed base package", ResultCode.INVALID_INPUT_PARAMETER);
					}

					if (PkgStatus.ACTIVE != rnCPackage.getPkgStatus()) {
						throw new OperationFailedException("Subscriber(" + sprInfo.getSubscriberIdentity() + ") add operation failed."
								+ " Reason: subscribed rnc package(" + rnCPackage.getName() + ") found with "
								+ rnCPackage.getPkgStatus() + " Status", ResultCode.NOT_FOUND);
					}
				}
			}
		}
	}

	private void validateTillBillDate(Integer billDay, String subscriberIdentity, ProductOffer productOffer) throws OperationFailedException {
		validateTillBillDateForDataPackage(billDay, subscriberIdentity, productOffer);
		validateTillBillDateForRnCPackage(billDay, subscriberIdentity, productOffer);
	}

	private void validateTillBillDateForRnCPackage(Integer billDay, String subscriberIdentity, ProductOffer productOffer) throws OperationFailedException {
		if (Objects.nonNull(productOffer.getProductOfferServicePkgRelDataList()) && Objects.isNull(billDay)) {
			for(ProductOfferServicePkgRel productOfferServicePkgRel : productOffer.getProductOfferServicePkgRelDataList()){
				validateRnCPackageWithTillBillDate(subscriberIdentity, productOfferServicePkgRel);

			}
		}
	}

	private void validateRnCPackageWithTillBillDate(String subscriberIdentity, ProductOfferServicePkgRel productOfferServicePkgRel) throws OperationFailedException {
		RnCPackage rncPackage = productOfferServicePkgRel.getRncPackageData();
		if(Collectionz.isNullOrEmpty(rncPackage.getRateCardGroups()) == false){
            for(RateCardGroup rateCardGroup : rncPackage.getRateCardGroups()){
                validatePeakRateCard(subscriberIdentity, rateCardGroup.getPeakRateCard());
                validateOffPeakRateCard(subscriberIdentity, rateCardGroup.getOffPeakRateCard());

            }
        }
	}

	private void validateOffPeakRateCard(String subscriberIdentity, RateCard offPeakRateCard) throws OperationFailedException {
		if(Objects.nonNull(offPeakRateCard) && Objects.equals(RateCardType.NON_MONETARY,offPeakRateCard.getType())){
            NonMonetaryRateCard nonMonetaryRateCard = (NonMonetaryRateCard) offPeakRateCard;
            if (Objects.equals(RenewalIntervalUnit.TILL_BILL_DATE, nonMonetaryRateCard.getRenewalIntervalUnit())) {
                throw new OperationFailedException("Unable to add/Update Subscriber for ID: " + subscriberIdentity
                        + " . Reason: Billing day is mandatory for TILL-BILL DATE non-monetary rate card", ResultCode.NOT_FOUND);
            }
        }
	}

	private void validatePeakRateCard(String subscriberIdentity, RateCard peakRateCard) throws OperationFailedException {
		if(Objects.nonNull(peakRateCard) && Objects.equals(RateCardType.NON_MONETARY,peakRateCard.getType())){
            NonMonetaryRateCard nonMonetaryRateCard = (NonMonetaryRateCard) peakRateCard;
            if (Objects.equals(RenewalIntervalUnit.TILL_BILL_DATE, nonMonetaryRateCard.getRenewalIntervalUnit())) {
                throw new OperationFailedException("Unable to add/Update Subscriber for ID: " + subscriberIdentity
                        + " . Reason: Billing day is mandatory for TILL-BILL DATE non-monetary rate card", ResultCode.NOT_FOUND);
            }
        }
	}

	private void validateTillBillDateForDataPackage(Integer billDay, String subscriberIdentity, ProductOffer productOffer) throws OperationFailedException {
		if (Objects.nonNull(productOffer.getDataServicePkgData()) && Objects.isNull(billDay)) {
			List<QuotaProfile> quotaProfiles = productOffer.getDataServicePkgData().getQuotaProfiles();
			for (QuotaProfile quotaProfile : quotaProfiles) {
				if (Objects.equals(RenewalIntervalUnit.TILL_BILL_DATE, quotaProfile.getRenewalIntervalUnit())) {
					throw new OperationFailedException("Unable to add/Update Subscriber for ID: " + subscriberIdentity
							+ " . Reason: Billing day is mandatory for TILL-BILL DATE quota profile", ResultCode.NOT_FOUND);
				}
			}
		}
	}

	@Override
	public void importSubscriptions(SPRInfo sprInfo, List<SubscriptionDetail> subscriptionDetails) throws OperationFailedException {
		
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Importing subscriptions of subscriber: " + sprInfo.getSubscriberIdentity());
		}
		
		Transaction transaction = spInterface.createTransaction();
		try {
			transaction.begin();
			
			/* 
			 * importSubscription method available but not used due to overhead of SQLException
			*/

			SubscriberInfo subscriberInfo = new SubscriberInfo(null, subscriptionDetails, Collections.<SubscriberUsage>emptyList());

			subscriptionOperation.importSubscriptionsAndUsages(sprInfo, subscriberInfo , transaction);

		} catch (OperationFailedException e) {
			
			transaction.rollback();

			throw e;
		} catch (TransactionException e) {
			ResultCode sprErrorCode = ResultCode.INTERNAL_ERROR;

			if (e.getErrorCode() == TransactionErrorCode.CONNECTION_NOT_FOUND) {
				
				alertListener.generateSystemAlert(SEVERITY, Alerts.DATABASE_CONNECTION_NOT_AVAILABLE, MODULE,
						"Unable to import subscriptions with subscriber ID: " + sprInfo.getSubscriberIdentity() + " from DB with data source name: "
								+ transaction.getDataSourceName() + ". Reason: Connection not available");
				sprErrorCode = ResultCode.SERVICE_UNAVAILABLE;
			}

			transaction.rollback();
			
			throw new OperationFailedException("Unable to import subscriptions with subscriber Id: " + sprInfo.getSubscriberIdentity()
					+ ". Reason: " + e.getMessage(), sprErrorCode, e);
		} finally {
			endTransaction(transaction);
		}
		
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Subscriptions imported for subscriber: " + sprInfo.getSubscriberIdentity());
		}
		
	}

	@Override
	public void resetUsage(String subscriberId, String productOfferId) throws OperationFailedException {
		umOperation.resetUsage(subscriberId, productOfferId, transactionFactory);
	}

    @Override
    public void resetQuota(String subscriberId, String productOfferId) throws OperationFailedException {
        abmfOperation.resetQuota(subscriberId, productOfferId, getProfile(subscriberId).getBillingDate(), transactionFactory);
    }

    @Override
	public void resetBalance(SPRInfo sprInfo, String productOfferId, RnCPackage rnCPackage) throws OperationFailedException {
		rnCABMFOperation.resetNonMonetaryBalance(sprInfo, productOfferId, rnCPackage, transactionFactory);
	}

	@Override
	public void refundRnCBalance(String subscriberIdentity, List<RnCNonMonetaryBalance> refundNonMonetaryBalances) throws OperationFailedException {
		rnCABMFOperation.refundBalance(subscriberIdentity, refundNonMonetaryBalances, transactionFactory);
	}

	@Override
	public SubscriptionResult subscribeAddOnProductOfferById(SubscriptionParameter subscriptionParameter, String requestIpAddress) throws OperationFailedException {
		ProductOffer subscriptionPackage = policyRepository.getProductOffer().byId(subscriptionParameter.getProductOfferId());
		String subscriberIdentity = subscriptionParameter.getSprInfo().getSubscriberIdentity();
		if (subscriptionPackage != null && PkgStatus.ACTIVE != subscriptionPackage.getStatus()) {
				throw new OperationFailedException("Unable to subscribe addOn(" + subscriptionPackage.getName() + ") for subscriber ID: " + subscriberIdentity
						+ " Reason: AddOn found with "
						+ subscriptionPackage.getStatus() + " Status", ResultCode.NOT_FOUND);
		}

		/* if LIVE subscriber, don't allow subscription of TEST addon */
		if (spInterface.isTestSubscriber(subscriberIdentity) == false && subscriptionPackage != null && PkgMode.TEST == subscriptionPackage.getPackageMode()) {
			throw new OperationFailedException("Unable to subscribe addOn(" + subscriptionPackage.getName() + ") for subscriber ID: " + subscriberIdentity
					+ " Reason: Live subscriber must not have subscription of test addOn", ResultCode.INVALID_INPUT_PARAMETER);
		}

		SubscriptionResult subscriptionResult =  subscriptionOperation.subscribeAddOnProductOfferById(subscriptionParameter, transactionFactory, requestIpAddress);

		if(nonNull(subscriptionEDRListener)){
			List<SubscriptionEDRData> listSubscriptionEDRData = SubscriptionEDRUtil.prepareAddonSubscriptionEDRData(subscriptionParameter.getSprInfo(),subscriptionResult,SUBSCRIBE_ADDON_PRODUCT_OFFER, requestIpAddress,subscriptionPackage.getName());
			for(SubscriptionEDRData subscriptionEDRData : listSubscriptionEDRData) {
				subscriptionEDRData.setCurrency(subscriptionPackage.getCurrency());

				subscriptionEDRListener.subscriptionEDR(subscriptionEDRData);
			}
		}

		return subscriptionResult;
	}

	@Override
	public SubscriptionResult autoSubscribeAddOnProductOfferById(SubscriptionParameter subscriptionParameter) throws OperationFailedException {
		ProductOffer subscriptionPackage = policyRepository.getProductOffer().byId(subscriptionParameter.getProductOfferId());

		if (subscriptionPackage != null && PkgStatus.ACTIVE != subscriptionPackage.getStatus()) {
			throw new OperationFailedException("Unable to subscribe addOn(" + subscriptionPackage.getName() + ") for subscriber ID: " + subscriptionParameter.getSprInfo().getSubscriberIdentity()
					+ " Reason: AddOn found with "
					+ subscriptionPackage.getStatus() + " Status", ResultCode.NOT_FOUND);
		}

		return subscriptionOperation.subscribeAddOnProductOfferById(subscriptionParameter, transactionFactory, null);
	}

	@Override
	public void reserveBalance(String subscriberId, List<NonMonetoryBalance> reservationBalances) throws OperationFailedException {
		abmfOperation.reserveBalance(subscriberId, reservationBalances, transactionFactory);
	}

	@Override
	public void reserveRnCBalance(String subscriberId, List<RnCNonMonetaryBalance> reservationBalances) throws OperationFailedException {
		rnCABMFOperation.reserveBalance(subscriberId, reservationBalances, transactionFactory);
	}

	@Override
	public void reportBalance(String subscriberIdentity, List<NonMonetoryBalance> reportBalances) throws OperationFailedException {
		abmfOperation.reportBalance(subscriberIdentity, reportBalances, transactionFactory);
	}

	@Override
	public void reportRnCBalance(String subscriberIdentity, List<RnCNonMonetaryBalance> reportBalances) throws OperationFailedException {
		rnCABMFOperation.reportBalance(subscriberIdentity, reportBalances, transactionFactory);
	}


	/*
	    THIS IS USED ONLY FOR RESETTING DAILY AND WEEKLY QUOTA
	 */
	@Override
	public void resetBalance(String subscriberId, List<NonMonetoryBalance> resetBalances) throws OperationFailedException {
		abmfOperation.resetBalance(subscriberId, resetBalances, transactionFactory);
	}

	@Override
	public void directDebitBalance(String subscriberId, List<NonMonetoryBalance> directDebitBalances) throws OperationFailedException {
		abmfOperation.directDebitBalance(subscriberId, directDebitBalances, transactionFactory);
	}

	@Override
	public void reportAndReserveBalance(String subscriberId, List<NonMonetoryBalance> reportAndReserveBalances) throws OperationFailedException {
		abmfOperation.reportAndReserveBalance(subscriberId, reportAndReserveBalances, transactionFactory);
	}

	@Override
	public void reportAndReserveRnCBalance(String subscriberId, List<RnCNonMonetaryBalance> reportAndReserveBalances) throws OperationFailedException {
		rnCABMFOperation.reportAndReserveBalance(subscriberId, reportAndReserveBalances, transactionFactory);
	}

	@Override
	public void updateNextBillingCycleBalance(Set<Map.Entry<String, NonMonetoryBalance>> nonMonetoryBalances, Integer newBillDay) throws OperationFailedException {
		abmfOperation.updateNextBillingCycleBalance(nonMonetoryBalances, newBillDay, transactionFactory);
	}

	@Override
	public SubscriberMonetaryBalance getMonetaryBalance(String subscriberId, java.util.function.Predicate<MonetaryBalance> predicate) throws OperationFailedException {
		return getMonetaryABMFOperation().getMonetaryBalance(subscriberId, predicate, transactionFactory);
	}

	@Override
	public void addMonetaryBalance(String subscriberId, SubscriberMonetaryBalanceWrapper subscriberMonetaryBalanceWrapper, String remark, String requestIPAddress) throws OperationFailedException {
		getMonetaryABMFOperation().addBalance(subscriberId, subscriberMonetaryBalanceWrapper.getCurrentMonetaryBalance(), transactionFactory);
		if(nonNull(balanceEDRListener)){
			if(isNull(subscriberMonetaryBalanceWrapper.getSprInfo())){
				subscriberMonetaryBalanceWrapper.setSprInfo(getProfile(subscriberId));
			}
			if(isNullOrBlank(subscriberMonetaryBalanceWrapper.getOperation())){
				subscriberMonetaryBalanceWrapper.setOperation(ADD_MONETARY_BALANCE);
			}
			balanceEDRListener.addMonetaryEDR(subscriberMonetaryBalanceWrapper, requestIPAddress, remark);
		}
	}

	@Override
	public void addMonetaryBalance(String subscriberId, SubscriberMonetaryBalanceWrapper subscriberMonetaryBalanceWrapper,
								   String remark, String requestIPAddress,EnumMap<SPRFields, String> updatedProfile) throws OperationFailedException {
		addMonetaryBalance(subscriberId, subscriberMonetaryBalanceWrapper, remark, requestIPAddress, updatedProfile, null);
	}

	@Override
	public void addMonetaryBalance(String subscriberId, SubscriberMonetaryBalanceWrapper subscriberMonetaryBalanceWrapper,
								   String remark, String requestIPAddress, EnumMap<SPRFields, String> updatedProfile,
								   String monetaryRechargePlanName) throws OperationFailedException {

		Transaction transaction = transactionFactory.createTransaction();
		int updateSuccessful;
		if (isNull(transaction)) {
			throw new OperationFailedException("Unable to recharge Monetary Balance" + " . Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}

		try {
			transaction.begin();
			if(nonNull(subscriberMonetaryBalanceWrapper.getCurrentMonetaryBalance())){
				getMonetaryABMFOperation().addMonetaryBalance(subscriberMonetaryBalanceWrapper.getCurrentMonetaryBalance(), transaction);
				if(nonNull(balanceEDRListener)){
					if(isNull(subscriberMonetaryBalanceWrapper.getSprInfo())){
						subscriberMonetaryBalanceWrapper.setSprInfo(getProfile(subscriberId));
					}
					if(isNullOrBlank(subscriberMonetaryBalanceWrapper.getOperation())){
						subscriberMonetaryBalanceWrapper.setOperation(ADD_MONETARY_BALANCE);
					}
					balanceEDRListener.addMonetaryEDR(subscriberMonetaryBalanceWrapper, requestIPAddress, remark);
				}
			}
			if(nonNull(updatedProfile)) {
				updatedProfile.put(SPRFields.MODIFIED_DATE, String.valueOf(System.currentTimeMillis()));
				updateSuccessful = spInterface.updateProfile(subscriberId, updatedProfile, transaction);
				if (nonNull(subscriberEDRListener) && updateSuccessful > 0) {
					subscriberEDRListener.updateSubscriberEDR(updatedProfile, subscriberId,
							nonNull(subscriberMonetaryBalanceWrapper.getOperation()) ? subscriberMonetaryBalanceWrapper.getOperation() : UPDATE_SUBSCRIBER,
							ActionType.UPDATE.name(), requestIPAddress);
				}
			}
		} catch (TransactionException e) {

			ResultCode sprErrorCode = ResultCode.INTERNAL_ERROR;
			if (e.getErrorCode() == TransactionErrorCode.CONNECTION_NOT_FOUND) {

				alertListener.generateSystemAlert(SEVERITY, Alerts.DATABASE_CONNECTION_NOT_AVAILABLE, MODULE,
						"Unable to add Monetary Balance from DB with data source name:"
								+ transaction.getDataSourceName() + ". Reason: Connection not available");
				sprErrorCode = ResultCode.SERVICE_UNAVAILABLE;
			}
			transaction.rollback();
			throw new OperationFailedException("Unable to add Monetary Balance" + "Reason: " + e.getMessage(), sprErrorCode, e);
		} catch (OperationFailedException e) {
			transaction.rollback();
			throw e;
		}  finally {
			endTransaction(transaction);
		}
	}

	private void addMonetaryBalance(SubscriberMonetaryBalanceWrapper subscriberMonetaryBalanceWrapper, String remark, Transaction transaction) throws OperationFailedException, SQLException, TransactionException {
		getMonetaryABMFOperation().addBalance(subscriberMonetaryBalanceWrapper.getCurrentMonetaryBalance(), transaction);
		if(nonNull(balanceEDRListener)){
			if(isNull(subscriberMonetaryBalanceWrapper.getSprInfo())){
				subscriberMonetaryBalanceWrapper.setSprInfo(getProfile(subscriberMonetaryBalanceWrapper.getCurrentMonetaryBalance().getSubscriberId()));
			}
			if(isNullOrBlank(subscriberMonetaryBalanceWrapper.getOperation())){
				subscriberMonetaryBalanceWrapper.setOperation(ADD_MONETARY_BALANCE);
			}
			balanceEDRListener.addMonetaryEDR(subscriberMonetaryBalanceWrapper, null, remark);
		}
	}

	@Override
	public void updateMonetaryBalance(String subscriberId, SubscriberMonetaryBalanceWrapper subscriberMonetaryBalanceWrapper, String remark, String requestIPAddress) throws OperationFailedException {
		getMonetaryABMFOperation().updateBalance(subscriberId, subscriberMonetaryBalanceWrapper.getCurrentMonetaryBalance(), transactionFactory);
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
		getMonetaryABMFOperation().rechargeMonetaryBalance(monetaryRechargeData,transactionFactory);
		if (nonNull(balanceEDRListener)) {
			if(isNull(monetaryRechargeData.getSprInfo())){
				monetaryRechargeData.setSprInfo(getProfile(monetaryRechargeData.getSubscriberId()));
			}
			balanceEDRListener.rechargeMonetaryBalanceEDR(monetaryRechargeData);
		}
	}

	@Override
	public void rechargeMonetaryBalance(MonetaryRechargeData monetaryRechargeData,EnumMap<SPRFields, String> updatedProfile,String subscriberId, String requestIpAddress) throws OperationFailedException {
		Transaction transaction = transactionFactory.createTransaction();
		int updateSuccessful;
		if (isNull(transaction)) {
			throw new OperationFailedException("Unable to recharge Monetary Balance" + " . Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}
		try {
			transaction.begin();

			if(nonNull(updatedProfile)){
			updatedProfile.put(SPRFields.MODIFIED_DATE,String.valueOf(System.currentTimeMillis()));
			updateSuccessful =spInterface.updateProfile(subscriberId,updatedProfile,transaction);
				if(nonNull(subscriberEDRListener) && updateSuccessful>0){
					subscriberEDRListener.updateSubscriberEDR(updatedProfile, subscriberId,
							(nonNull(monetaryRechargeData) && isNullOrBlank(monetaryRechargeData.getOperation())==false)
									? monetaryRechargeData.getOperation() : UPDATE_SUBSCRIBER,
							ActionType.UPDATE.name(), requestIpAddress);
				}
			}
			if(nonNull(monetaryRechargeData)){
				getMonetaryABMFOperation().rechargeMonetaryBalance(monetaryRechargeData,transaction);
				if (nonNull(balanceEDRListener)) {
					if(isNull(monetaryRechargeData.getSprInfo())){
						monetaryRechargeData.setSprInfo(getProfile(monetaryRechargeData.getSubscriberId()));
					}
					balanceEDRListener.rechargeMonetaryBalanceEDR(monetaryRechargeData);
				}
			}

		} catch (TransactionException e) {
			ResultCode sprErrorCode = ResultCode.INTERNAL_ERROR;
			if (e.getErrorCode() == TransactionErrorCode.CONNECTION_NOT_FOUND) {

				alertListener.generateSystemAlert(SEVERITY, Alerts.DATABASE_CONNECTION_NOT_AVAILABLE, MODULE,
						"Unable to recharge Monetary Balance from DB with data source name:"
								+ transaction.getDataSourceName() + ". Reason: Connection not available");
				sprErrorCode = ResultCode.SERVICE_UNAVAILABLE;
			}
			transaction.rollback();
			throw new OperationFailedException("Unable to recharge Monetary Balance" + "Reason: " + e.getMessage(), sprErrorCode, e);
		} catch (OperationFailedException e) {
			transaction.rollback();
			throw e;
		}  finally {
			endTransaction(transaction);
		}
	}

	@Override
	public void  updateCreditLimit(String subscriberId, SubscriberMonetaryBalanceWrapper subscriberMonetaryBalanceWrapper, String remark, String requestIPAddress) throws OperationFailedException {
		getMonetaryABMFOperation().updateCreditLimit(subscriberId, subscriberMonetaryBalanceWrapper.getCurrentMonetaryBalance(), transactionFactory);
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
		getMonetaryABMFOperation().reserveBalance(subscriberId, lstmonetaryBalance, transactionFactory);
	}

	@Override
	public void directDebitMonetaryBalance(String subscriberId, List<MonetaryBalance> lstmonetaryBalance) throws OperationFailedException {
		getMonetaryABMFOperation().directDebitBalance(subscriberId, lstmonetaryBalance, transactionFactory);
	}

	@Override
	public void reportAndReserveMonetaryBalance(String subscriberId, List<MonetaryBalance> lstmonetaryBalance) throws OperationFailedException {
		getMonetaryABMFOperation().reportAndReserveBalance(subscriberId, lstmonetaryBalance, transactionFactory);
	}

	@Override
	public Subscription subscribeQuotaTopUpByName(String subscriberIdentity, String parentId, String quotaTopUpName,
												  Integer subscriptionStatusValue,
												  Long startTime,
												  Long endTime,
												  Integer priority,
												  double subscriptionPrice ,
												  String monetaryBalanceId,
												  String param1,
												  String param2)
			throws OperationFailedException {

		QuotaTopUp quotaTopUp = policyRepository.getActiveQuotaTopUpByName(quotaTopUpName);

		if (quotaTopUp != null && PkgStatus.ACTIVE != quotaTopUp.getAvailabilityStatus()) {
			throw new OperationFailedException("Unable to subscribe Quota TopUp(" + quotaTopUp.getName() + ") for subscriber ID: " + subscriberIdentity
					+ " Reason: Quota TopUp found with "
					+ quotaTopUp.getAvailabilityStatus() + " Status", ResultCode.NOT_FOUND);
		}

		/* if LIVE subscriber, not allow to subscriber TEST addon */
		if (spInterface.isTestSubscriber(subscriberIdentity) == false && quotaTopUp != null && PkgMode.TEST == quotaTopUp.getMode()) {
			throw new OperationFailedException("Unable to subscriber Quota TopUp(" + quotaTopUp.getName() + ")."
					+ " Reason: Live subscriber must not have subscription of test Quota TopUp", ResultCode.INVALID_INPUT_PARAMETER);
		}

		return subscriptionOperation
				.subscribeQuotaTopUpByName(getProfile(subscriberIdentity), parentId, quotaTopUpName, subscriptionStatusValue, startTime, endTime, priority,subscriptionPrice,monetaryBalanceId, param1, param2, transactionFactory);

	}

	@Override
	public Subscription subscribeQuotaTopUpById(String subscriberIdentity, String parentId, String topUpId, Integer subscriptionStatusValue, Long startTime, Long endTime, Integer priority,double subscriptionPrice,String monetaryBalanceId, String param1, String param2) throws OperationFailedException {
		QuotaTopUp subscriptionPackage = policyRepository.getQuotaTopUpById(topUpId);

		if (subscriptionPackage == null) {
			throw new OperationFailedException("Unable to subscribe topUp for subscriber ID: " + subscriberIdentity
					+ " Reason: TopUp not found", ResultCode.NOT_FOUND);
		}

		if (PkgStatus.ACTIVE != subscriptionPackage.getAvailabilityStatus()) {
			throw new OperationFailedException("Unable to subscribe topUp(" + subscriptionPackage.getName() + ") for subscriber ID: " + subscriberIdentity
					+ " Reason: TopUp found with "
					+ subscriptionPackage.getAvailabilityStatus() + " Status", ResultCode.NOT_FOUND);
		}

		/* if LIVE subscriber, not allow to subscriber TEST topup */
		if (spInterface.isTestSubscriber(subscriberIdentity) == false && PkgMode.TEST == subscriptionPackage.getMode()) {
			throw new OperationFailedException("Unable to subscribe topUp(" + subscriptionPackage.getName() + ") for subscriber ID: " + subscriberIdentity
					+ " Reason: Live subscriber must not have subscription of test topUp", ResultCode.INVALID_INPUT_PARAMETER);
		}

		return subscriptionOperation.subscribeQuotaTopUpById(getProfile(subscriberIdentity), parentId, topUpId, subscriptionStatusValue,
				startTime, endTime, priority,subscriptionPrice, monetaryBalanceId,param1, param2, transactionFactory);

	}

	@Override
	public Subscription getTopUpSubscriptionsBySubscriptionId(
			String subscriptionId) throws OperationFailedException {
		return subscriptionOperation.getActiveQuotaTopUpSubscriptionBySubscriptionId(subscriptionId, System.currentTimeMillis(), transactionFactory);
	}

	/* (non-Javadoc)
	 * @see com.elitecore.corenetvertex.spr.SubscriberRepositoryI#updateSubscription(java.lang.String, java.lang.Integer, java.lang.Long, java.lang.Long, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public Subscription updateQuotaTopUpSubscription(
			String subscriberIdentity,
			String subscriptionId,
			Integer subscriptionStatusValue,
			Long startTime,
			Long endTime,
			Integer priority, String rejectReason
	) throws OperationFailedException {
		return  subscriptionOperation.updateQuotaTopUpSubscription(subscriberIdentity, subscriptionId, subscriptionStatusValue, startTime, endTime, priority, rejectReason, transactionFactory);
	}

	@Override
	public LinkedHashMap<String, Subscription> getTopUpSubscriptions(String subscriberIdentity) throws OperationFailedException {
		return subscriptionOperation.getQuotaTopUpSubscriptions(subscriberIdentity, transactionFactory);
	}

	@Override
	public LinkedHashMap<String, Subscription> getBodSubscriptions(String subscriberIdentity) throws OperationFailedException {
		return subscriptionOperation.getBodSubscriptions(subscriberIdentity, transactionFactory);
	}

	@Override
	public void changeBillDay(String subscriberId, Timestamp nextBillDate, Timestamp billChangeDate, String requestIpAddress) throws OperationFailedException {
		subscriptionOperation.changeBillDay(subscriberId, nextBillDate, billChangeDate, transactionFactory);
		if (nonNull(subscriberEDRListener)) {
			subscriberEDRListener.changeBillDayEDR(getProfile(subscriberId), nextBillDate, billChangeDate, CHANGE_BILL_DAY, ActionType.UPDATE.name(), requestIpAddress);
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

	@Override
	public SubscriptionNonMonitoryBalance addDataRnCBalance(String subscriberIdentity, Subscription subscription, ProductOffer productOffer) throws OperationFailedException {
		if (transactionFactory.isAlive() == false) {
			throw new OperationFailedException("Unable to add balance for Subscriber ID: " + subscriberIdentity
					+ " . Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}

		Transaction transaction = transactionFactory.createTransaction();
		if (transaction == null) {
			throw new OperationFailedException("Unable to add balance for Subscriber ID: " + subscriberIdentity
					+ " . Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}

		try {
			transaction.begin();
			return abmfOperation.addBalance(subscriberIdentity, subscription, productOffer, transaction, null);
		}catch (OperationFailedException e) {

			transaction.rollback();

			throw e;
		} catch (TransactionException e) {
			ResultCode sprErrorCode = ResultCode.INTERNAL_ERROR;

			if (e.getErrorCode() == TransactionErrorCode.CONNECTION_NOT_FOUND) {

				alertListener.generateSystemAlert(SEVERITY, Alerts.DATABASE_CONNECTION_NOT_AVAILABLE, MODULE,
						"Unable to add balance for subscriber ID: " + subscriberIdentity + " from DB with data source name: "
								+ transaction.getDataSourceName() + ". Reason: Connection not available");
				sprErrorCode = ResultCode.SERVICE_UNAVAILABLE;
			}

			transaction.rollback();

			throw new OperationFailedException("Unable to add balance for subscriber Id: " + subscriberIdentity
					+ ". Reason: " + e.getMessage(), sprErrorCode, e);
		} finally {
			endTransaction(transaction);
		}
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

		Subscription subscription =  subscriptionOperation.subscribeBod(boDPackage,
				subscriptionParameter.getSprInfo(),
				subscriptionParameter.getParentId(),
				subscriptionParameter.getSubscriptionStatusValue(),
				subscriptionParameter.getStartTime(),
				subscriptionParameter.getEndTime(),
				transactionFactory,
				subscriptionParameter.getPriority(),
				subscriptionParameter.getParam1(),
				subscriptionParameter.getParam2(),
				null,
				subscriptionParameter.getSubscriptionPrice(),
				subscriptionParameter.getMonetaryBalance(),
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
		subscriptionOperation.updateFnFGroup(sprInfo,subscription,transactionFactory);
		SubscriptionResult subscriptionResult = new SubscriptionResult();
		subscriptionResult.addSubscription(subscription);

		if(nonNull(subscriptionEDRListener)){
			subscriptionEDRListener.addSubscriptionEDR(sprInfo, subscriptionResult, UPDATE_FNF_GROUP, requestIpAddress);
		}

		return  subscription;
	}

	@Override
	public void processReset(SPRInfo sprInfo, String requestIpAddress) throws OperationFailedException {
		String subscriberIdentity = sprInfo.getSubscriberIdentity();

		if (transactionFactory.isAlive() == false) {
			throw new OperationFailedException("Unable to process reset balance for Subscriber ID: " + subscriberIdentity
					+ " . Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}

		Transaction transaction = transactionFactory.createTransaction();
		if (transaction == null) {
			throw new OperationFailedException("Unable to process reset balance for Subscriber ID: " + subscriberIdentity
					+ " . Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}

		try {

			transaction.begin();

			// reset data balance
			ProductOffer productOffer = policyRepository.getProductOffer().byName(sprInfo.getProductOffer());
			if(productOffer.getDataServicePkgData() != null) {
				processDataPackageReset(sprInfo.getSubscriberIdentity(), productOffer, transaction);
			}

			// reset rnc balance
			for(ProductOfferServicePkgRel productOfferServicePkgRel: productOffer.getProductOfferServicePkgRelDataList()) {
				getRnCAbmfOperation().resetNonMonetaryBalance(sprInfo, productOffer.getId(), productOfferServicePkgRel.getRncPackageData(), transaction);
			}

			// add/update monetary balance
			addUpdateMonetaryBalance(sprInfo, productOffer,requestIpAddress, transaction);

		} catch (OperationFailedException e) {
			transaction.rollback();

			throw e;
		} catch (TransactionException e) {
			ResultCode sprErrorCode = ResultCode.INTERNAL_ERROR;

			if (e.getErrorCode() == TransactionErrorCode.CONNECTION_NOT_FOUND) {

				alertListener.generateSystemAlert(SEVERITY, Alerts.DATABASE_CONNECTION_NOT_AVAILABLE, MODULE,
						"Unable to process reset balance for subscriber ID: " + subscriberIdentity + " from DB with data source name: "
								+ transaction.getDataSourceName() + ". Reason: Connection not available");
				sprErrorCode = ResultCode.SERVICE_UNAVAILABLE;
			}

			transaction.rollback();

			throw new OperationFailedException("Unable to process reset balance for subscriber Id: " + subscriberIdentity
					+ ". Reason: " + e.getMessage(), sprErrorCode, e);
		} catch (SQLException e) {
			transaction.rollback();
			throw new OperationFailedException("Unable to process reset balance for subscriber Id: " + subscriberIdentity
					+ ". Reason: " + e.getMessage(), e);
		} finally {
			endTransaction(transaction);
		}
	}

	private void processDataPackageReset(String subscriberIdentity, ProductOffer currentProductOffer, Transaction transaction) throws OperationFailedException, TransactionException, SQLException {
		UserPackage userPackage = currentProductOffer.getDataServicePkgData();

		if (userPackage == null) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Skipping Usage/Quota reset. Reason: Data package not configured in product offer: " + currentProductOffer.getName());
			}
			return;
		}

		if (QuotaProfileType.USAGE_METERING_BASED == userPackage.getQuotaProfileType()) {
			umOperation.resetUsage(subscriberIdentity, currentProductOffer.getId(), transaction);
		} else if (QuotaProfileType.RnC_BASED == userPackage.getQuotaProfileType()) {
			getAbmfOperation().resetQuota(subscriberIdentity, currentProductOffer.getId(), getProfile(subscriberIdentity).getBillingDate(), transaction);
		}
	}

	private void addUpdateMonetaryBalance(SPRInfo sprInfo, ProductOffer newSubscribedProductOffer, String requestIpAddress, Transaction transaction) throws OperationFailedException, SQLException, TransactionException {

		String subscriberId = sprInfo.getSubscriberIdentity();

		if (Objects.nonNull(newSubscribedProductOffer.getCreditBalance()) && newSubscribedProductOffer.getCreditBalance() != 0.0) {

			// get Monetary balance
			SubscriberMonetaryBalance existingMonetaryBalance = getMonetaryBalance(subscriberId, Predicates.DEFAULT_MONETARY_BALANCE);
			if (Objects.isNull(existingMonetaryBalance.getMainBalance())) {

				// add Monetary balance
				MonetaryBalance monetaryBalance = new MonetaryBalance(UUID.randomUUID().toString()
						, sprInfo.getSubscriberIdentity(), null
						//, 0
						, newSubscribedProductOffer.getCreditBalance(), newSubscribedProductOffer.getCreditBalance(),
						0, 0l, 0, System.currentTimeMillis(), CommonConstants.FUTURE_DATE, systemCurrency
						, MonetaryBalanceType.DEFAULT.name(), System.currentTimeMillis(), 0,null, null);

				addMonetaryBalance(new SubscriberMonetaryBalanceWrapper(monetaryBalance, null,
								sprInfo, CommonConstants.ADD_MONETARY_BALANCE, ActionType.ADD.name(),null)
						, "Add Monetary Balance", transaction);
			} else {

				// update Monetary balance
				MonetaryBalance previousMonetaryBalance = existingMonetaryBalance.getMainBalance();
				MonetaryBalance updatedMonetaryBalance = previousMonetaryBalance.copy();

				if (Objects.nonNull(newSubscribedProductOffer.getCreditBalance())) {
					updatedMonetaryBalance.setAvailBalance(newSubscribedProductOffer.getCreditBalance());
					updatedMonetaryBalance.setInitialBalance(0);
				}

				SubscriberMonetaryBalanceWrapper subscriberMonetaryBalanceWrapper =
						new SubscriberMonetaryBalanceWrapper(updatedMonetaryBalance, previousMonetaryBalance, sprInfo,CommonConstants.UPDATE_MONETARY_BALANCE, ActionType.UPDATE.getDisplayName(),null);

				updateMonetaryBalance(subscriberId, subscriberMonetaryBalanceWrapper, "", requestIpAddress, transaction);
			}
		}

	}

	private void updateMonetaryBalance(String subscriberId, SubscriberMonetaryBalanceWrapper subscriberMonetaryBalanceWrapper, String remark, String requestIpAddress, Transaction transaction) throws OperationFailedException, SQLException, TransactionException {
		getMonetaryABMFOperation().updateBalance( subscriberMonetaryBalanceWrapper.getCurrentMonetaryBalance(), transaction);
		if(nonNull(balanceEDRListener)){
			if(isNull(subscriberMonetaryBalanceWrapper.getSprInfo())){
				subscriberMonetaryBalanceWrapper.setSprInfo(getProfile(subscriberId));
			}
			if(isNullOrBlank(subscriberMonetaryBalanceWrapper.getOperation())){
				subscriberMonetaryBalanceWrapper.setOperation(UPDATE_MONETARY_BALANCE);
			}
			balanceEDRListener.updateMonetaryEDR(subscriberMonetaryBalanceWrapper, requestIpAddress, remark);
		}
	}

	public ABMFOperation getAbmfOperation() {
		return this.abmfOperation;
	}
	public RnCABMFOperation getRnCAbmfOperation() {
		return this.rnCABMFOperation;
	}
	public MonetaryABMFOperationImpl getMonetaryABMFOperation() {
		return this.monetaryABMFOperationImpl;
	}
}