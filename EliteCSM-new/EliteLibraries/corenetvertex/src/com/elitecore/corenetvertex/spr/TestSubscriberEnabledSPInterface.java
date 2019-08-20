package com.elitecore.corenetvertex.spr;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.core.db.exception.DBDownException;
import com.elitecore.corenetvertex.core.transaction.Transaction;
import com.elitecore.corenetvertex.core.transaction.TransactionException;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.offer.ProductOfferServicePkgRel;
import com.elitecore.corenetvertex.pm.pkg.datapackage.SubscriptionPackage;
import com.elitecore.corenetvertex.pm.pkg.imspackage.IMSPackage;
import com.elitecore.corenetvertex.spr.data.SPRFields;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SPRInfo.SubscriberMode;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;


import static com.elitecore.commons.logging.LogManager.getLogger;

public class TestSubscriberEnabledSPInterface implements TestSubscriberAwareSPInterface {

	private static final String MODULE = "SP-TESTSUBSCRIBER";

	private SPInterface spInterface;
	private PolicyRepository policyRepository;
	private TestSubscriberCache testSubscriberCache;

	public TestSubscriberEnabledSPInterface(SPInterface spInterface,
                                            PolicyRepository policyRepository,
                                            TestSubscriberCache testSubscriberCache) {

		this.spInterface = spInterface;
		this.policyRepository = policyRepository;
		this.testSubscriberCache = testSubscriberCache;
	}

	@Override
	public SPRInfo getProfile(String subscriberIdentity) throws OperationFailedException, DBDownException {
		SPRInfo sprInfo = spInterface.getProfile(subscriberIdentity);

		if (sprInfo == null) {
			return null;
		}

		if (testSubscriberCache.exists(subscriberIdentity)) {
			if (getLogger().isDebugLogLevel())
				getLogger().debug(MODULE, "Considering subscriber: " + sprInfo.getSubscriberIdentity() + " as Test Subscriber");

			sprInfo.setSubscriberMode(SubscriberMode.TEST);
		}
		
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Subscriber profile found: " + sprInfo);
		}
		
		return sprInfo;
	}

	@Override
	public void addProfile(SPRInfo sprInfo) throws OperationFailedException {

		validate(sprInfo);
		spInterface.addProfile(sprInfo);

		if (sprInfo.getSubscriberMode() == SubscriberMode.TEST) {
			testSubscriberCache.add(sprInfo.getSubscriberIdentity());
		}
	}

	@Override
	public void validate(SPRInfo sprInfo) throws OperationFailedException {
		String productOfferName = sprInfo.getProductOffer();
		ProductOffer productOffer = policyRepository.getProductOffer().byName(productOfferName);
		
		String imsPackageName = sprInfo.getImsPackage();
		if (Strings.isNullOrBlank(imsPackageName) == false) {
			
			IMSPackage imsPkg = policyRepository.getIMSPkgByName(imsPackageName);
			validatePackage(imsPackageName, sprInfo.getSubscriberMode(), imsPkg.getMode());
		}

		validatePackage(productOfferName, sprInfo.getSubscriberMode(), productOffer.getPackageMode());
	}

	private void validatePackage(String packageName, SubscriberMode subscriberMode, PkgMode pkgMode) throws OperationFailedException {
		
		if (subscriberMode == SubscriberMode.LIVE) {

			if (PkgMode.TEST == pkgMode) {

				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "Subscriber add operation failed."
							+ " Reason: Live subscriber must not have Test package("
							+ packageName + ")");
				}

				throw new OperationFailedException("Subscriber add operation failed."
						+ " Reason: Live subscriber must not have Test package("
						+ packageName + ")", ResultCode.OPERATION_NOT_SUPPORTED);
			}
		}
		
	}

	private boolean haveTestSubscription(String subscriberIdentity, SubscriptionProvider subscriptionProvider) throws OperationFailedException {
		LinkedHashMap<String, Subscription> addonSubscriptions = subscriptionProvider.getSubscriptions(subscriberIdentity);

		for (Subscription subscription : addonSubscriptions.values()) {
			
			SubscriptionPackage subscriptionPackage = policyRepository.getAddOnById(subscription.getPackageId());
			
			if (subscriptionPackage != null) {
				if (PkgMode.TEST == subscriptionPackage.getMode()) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public int purgeProfile(String subscriberIdentity) throws OperationFailedException {
		int purgeCount = spInterface.purgeProfile(subscriberIdentity);
		try {
			testSubscriberCache.remove(subscriberIdentity);
		} catch (Exception e) {
			// consumed exception here to maintain atomicity of purge subscribers
			getLogger().error(MODULE, "Error while removing test subscriber. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
		}
		return purgeCount;
	}

	@Override
	public int purgeProfile(String subscriberIdentity, Transaction transaction) throws OperationFailedException, TransactionException {
		int purgeCount = spInterface.purgeProfile(subscriberIdentity, transaction);
		try {
			testSubscriberCache.remove(subscriberIdentity);
		} catch (Exception e) {
			// consumed exception here to maintain atomicity of purge subscribers
			getLogger().error(MODULE, "Error while removing test subscriber. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
		}
		return purgeCount;
	}

	private void preCheckForTestSubscriberDelete(String subscriberIdentity, SubscriptionProvider subscriptionProvider) throws OperationFailedException {

		SPRInfo profile;
		try {
			profile = spInterface.getProfile(subscriberIdentity);
		} catch (DBDownException e) {
		
			throw new OperationFailedException(e, ResultCode.SERVICE_UNAVAILABLE);
		}
		
		if (profile == null) {
			throw new OperationFailedException("Test subscriber delete operation failed. Reason: subscriber(" + subscriberIdentity + " ) not found", ResultCode.NOT_FOUND);
		}

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Subscriber profile found: " + profile);
		}
		// verify basePackage with TEST mode
		String baseProductOfferName = profile.getProductOffer();
		if (baseProductOfferName != null) {

			ProductOffer productOffer = policyRepository.getProductOffer().byName(baseProductOfferName);

			if (Objects.nonNull(productOffer) && PkgMode.TEST == productOffer.getPackageMode()) {
					throw new OperationFailedException("Test subscriber(" + subscriberIdentity + ") delete operation failed."
							+ " Reason: Test subscriber(" + subscriberIdentity + ") must not have Test Product Offer("
							+ baseProductOfferName + ").", ResultCode.OPERATION_NOT_SUPPORTED);
			}

			if(Objects.nonNull(productOffer) && Collectionz.isNullOrEmpty(productOffer.getProductOfferServicePkgRelDataList()) == false){
				for(ProductOfferServicePkgRel productOfferServicePkgRel : productOffer.getProductOfferServicePkgRelDataList()){
					if (productOfferServicePkgRel.getRncPackageData() != null && PkgMode.TEST == productOfferServicePkgRel.getRncPackageData().getPackageMode()) {
						throw new OperationFailedException("Test subscriber(" + subscriberIdentity + ") delete operation failed."
								+ " Reason: Test subscriber(" + subscriberIdentity + ") must not have Test package("
								+ baseProductOfferName + ")", ResultCode.OPERATION_NOT_SUPPORTED);
					}
				}
			}
		}
		
		String imsPackageName = profile.getImsPackage();
		if (imsPackageName != null) {
			IMSPackage imsPackage = policyRepository.getIMSPkgByName(imsPackageName);

			if (imsPackage != null && PkgMode.TEST == imsPackage.getMode()) {
				throw new OperationFailedException("Test subscriber(" + subscriberIdentity + ") delete operation failed."
						+ " Reason: Test subscriber(" + subscriberIdentity + ") must not have Test package("
						+ imsPackageName + ")", ResultCode.OPERATION_NOT_SUPPORTED);
			}
		}

		if (haveTestSubscription(subscriberIdentity, subscriptionProvider)) {
			throw new OperationFailedException("Test subscriber(" + subscriberIdentity + ") delete operation failed."
					+ " Reason: Test subscriber(" + subscriberIdentity + ") must not have subscription of test addOns", ResultCode.OPERATION_NOT_SUPPORTED);
		}

	}

	@Override
	public boolean isTestSubscriber(String subscriberIdentity) throws OperationFailedException {
		return testSubscriberCache.refreshAndExist(subscriberIdentity);
	}

	@Override
	public void addTestSubscriber(String subscriberIdentity) throws OperationFailedException {

		SPRInfo profile;
		try {
			profile = spInterface.getProfile(subscriberIdentity);
		} catch (DBDownException e) {
			throw new OperationFailedException(e, ResultCode.SERVICE_UNAVAILABLE);
		}
		if (profile == null) {
			throw new OperationFailedException("Test subscriber add operation not failed. Reason: subscriber(" + subscriberIdentity + " ) not found", ResultCode.NOT_FOUND);
		}

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Subscriber profile found: " + profile);
		}
		
		if (testSubscriberCache.exists(subscriberIdentity) == false) {
			testSubscriberCache.add(subscriberIdentity);
		} else {
			if (getLogger().isInfoLogLevel()) {
				getLogger().info(MODULE, "Skipping add test subscriber(" + subscriberIdentity
						+ "). Reason: test subscriber already exist");
			}
		}
		
	}

	@Override
	public int removeTestSubscriber(String subscriberIdentity, SubscriptionProvider subscriptionProvider) throws OperationFailedException {
		try {
			preCheckForTestSubscriberDelete(subscriberIdentity, subscriptionProvider);
		} catch (OperationFailedException e) {
			// This check is added to resolve JIRA NETVERTEX-1878
			if (ResultCode.NOT_FOUND == e.getErrorCode()) {
				if (getLogger().isInfoLogLevel()) {
					getLogger().info(MODULE, "Removing test subscriber entry for subscriber ID: " + subscriberIdentity + ". Reason: Subscriber not found in repository" );
				}
				return testSubscriberCache.remove(subscriberIdentity);
			}
			
			throw e;
		}
		
		return testSubscriberCache.remove(subscriberIdentity);
	}

	@Override
	public Iterator<String> getTestSubscriberIterator() throws OperationFailedException {
		testSubscriberCache.refresh();
		return testSubscriberCache.getTestSubscriberIterator();
	}

	@Override
	public int markForDeleteProfile(String subscriberIdentity) throws OperationFailedException {
		return spInterface.markForDeleteProfile(subscriberIdentity);
	}

	@Override
	public int removeTestSubscriber(List<String> subscriberIdentities, SubscriptionProvider subscriptionProvider) throws OperationFailedException {

		for (String subscriberIdentity : subscriberIdentities) {
			preCheckForTestSubscriberDelete(subscriberIdentity, subscriptionProvider);
		}

		return testSubscriberCache.remove(subscriberIdentities);
	}

	@Override
	public List<SPRInfo> getDeleteMarkedProfiles() throws OperationFailedException {
		return spInterface.getDeleteMarkedProfiles();
	}

	@Override
	public void refreshTestSubscriberCache() throws OperationFailedException {
		testSubscriberCache.refresh();
	}

	@Override
	public int restoreProfile(String subscriberIdentity) throws OperationFailedException {
		return spInterface.restoreProfile(subscriberIdentity);
	}

	@Override
	public Map<String, Integer> restoreProfile(List<String> subscriberIdentities) throws OperationFailedException {
		return spInterface.restoreProfile(subscriberIdentities);
	}

	@Override
	public int updateProfile(String subscriberIdentity,
			EnumMap<SPRFields, String> updatedProfile)
			throws OperationFailedException {

		validateDataAndIMSPackages(subscriberIdentity, updatedProfile);

		return spInterface.updateProfile(subscriberIdentity, updatedProfile);
	}

	@Override
	public int changeIMSpackage(String subscriberIdentity, String newIMSPackageName) throws OperationFailedException {
		validateIMSPackages(subscriberIdentity, newIMSPackageName);
		return spInterface.changeIMSpackage(subscriberIdentity, newIMSPackageName);
	}

	@Override
	public Transaction createTransaction() throws OperationFailedException {
		return spInterface.createTransaction();
	}

	@Override
	public void addProfile(SPRInfo sprInfo, Transaction transaction) throws OperationFailedException, TransactionException {
		validate(sprInfo);
		spInterface.addProfile(sprInfo, transaction);

		if (sprInfo.getSubscriberMode() == SubscriberMode.TEST) {
			testSubscriberCache.add(sprInfo.getSubscriberIdentity());
		}
 	}

	@Override
	public int updateProfile(String subscriberIdentity, EnumMap<SPRFields, String> updatedProfile, Transaction transaction) throws OperationFailedException, TransactionException {

		validateDataAndIMSPackages(subscriberIdentity, updatedProfile);
		return spInterface.updateProfile(subscriberIdentity, updatedProfile, transaction);
	}

	private void validateDataAndIMSPackages(String subscriberIdentity,
			EnumMap<SPRFields, String> updatedProfile)
			throws OperationFailedException {
	
		/*
		 * if subscriber not exist in test subscriber cache means subscriber is
		 * LIVE
		 */
		if (testSubscriberCache.exists(subscriberIdentity) == false) {

			///FIXME NEED TO HANDLE DURING UPDATE PRODUCT OFFER USER STORY
			// / DATA_PACKAGE changed
			if (updatedProfile.containsKey(SPRFields.PRODUCT_OFFER)) {
				String productOfferName = updatedProfile.get(SPRFields.PRODUCT_OFFER);

				ProductOffer productOffer = policyRepository.getProductOffer().base().byName(productOfferName);
				/*
				 * NULL/STATUS check is already done in
				 * SubscriberRepositoryImpl.updateProfile. 
				 * THIS CODE SHOULD BE
				 * VERIFY IF ANY UPDATION DONE IN
				 * SubscriberRepositoryImpl.updateProfile
				 */
				if (PkgMode.TEST == productOffer.getPackageMode()) {
					throw new OperationFailedException(
							"Subscriber update operation failed. Reason: Live subscriber must not have test data package("
									+ productOfferName + ")", ResultCode.OPERATION_NOT_SUPPORTED);
				}
			
			}

			if (updatedProfile.containsKey(SPRFields.IMS_PACKAGE)) {
				String imsPackageName = updatedProfile.get(SPRFields.IMS_PACKAGE);

				if (Strings.isNullOrBlank(imsPackageName) == false) {
					IMSPackage imsPkg = policyRepository.getIMSPkgByName(imsPackageName);
					/*
					 * NULL/STATUS check is already done in
					 * SubscriberRepositoryImpl.updateProfile.
					 * THIS CODE SHOULD BE
					 * VERIFY IF ANY UPDATION DONE IN
					 * SubscriberRepositoryImpl.updateProfile
					 */
					if (PkgMode.TEST == imsPkg.getMode()) {
						throw new OperationFailedException("Subscriber update operation failed."
										+ " Reason: Live subscriber must not have test ims package("
										+ imsPackageName + ")", ResultCode.OPERATION_NOT_SUPPORTED);
					}
				}
			}
		}
	}

	private void validateIMSPackages(String subscriberIdentity, String imsPackageName)
			throws OperationFailedException {

		/*
		 * if subscriber not exist in test subscriber cache means subscriber is
		 * LIVE
		 */
		if (testSubscriberCache.exists(subscriberIdentity) == false) {


			if (Strings.isNullOrBlank(imsPackageName) == false) {
				IMSPackage imsPkg = policyRepository.getIMSPkgByName(imsPackageName);
					/*
					 * NULL/STATUS check is already done in
					 * SubscriberRepositoryImpl.updateProfile.
					 * THIS CODE SHOULD BE
					 * VERIFY IF ANY UPDATION DONE IN
					 * SubscriberRepositoryImpl.updateProfile
					 */
				if (PkgMode.TEST == imsPkg.getMode()) {
					throw new OperationFailedException("Subscriber update operation failed."
							+ " Reason: Live subscriber must not have test ims package("
							+ imsPackageName + ")", ResultCode.OPERATION_NOT_SUPPORTED);
				}
			}
		}
	}
}
	
