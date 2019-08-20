package com.elitecore.nvsmx.system.util.migrate;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Maps;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.constants.SubscriptionState;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.pkg.datapackage.BasePackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.spr.SubscriberUsage;
import com.elitecore.corenetvertex.spr.data.SPRFields;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.data.SubscriberInfo;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.data.SubscriptionDetail;
import com.elitecore.corenetvertex.spr.data.SubscriptionType;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.nvsmx.system.listeners.DefaultNVSMXContext;

import javax.annotation.Nullable;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.UUID;

import static com.elitecore.commons.logging.LogManager.getLogger;

//TODO suggest name
/**
 * This class does following task:'
 *  - take JSON format and converts to NV68SubscriberInfo
 *  - take NV648SubscriberInfo to SubscriberInfo(that is 680 Subscriber) 
 * 
 */
public class SubscriberTransformer {

	private static final String MODULE = "NV648-SUBSCRIBER-READER";
	
	private PackageNameMapping packageNameMapping;
	private SPRColumnConfigurationProvider columnConfigurationProvider;

	public SubscriberTransformer(PackageNameMapping packageNameMapping, SPRColumnConfigurationProvider columnConfigurationProvider) {
		this.columnConfigurationProvider = columnConfigurationProvider;
		this.packageNameMapping = packageNameMapping;
	}

	public SubscriberInfo transformTo680Subscriber(NV648SubscriberInfo nv648SubscriberInfo) throws OperationFailedException {
		
		AccountDataInfo accountDataInfo = nv648SubscriberInfo.getAccountDataInfo();
		
		SPRInfo sprInfo;

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "SPRInfo creation started");
		}
		
		sprInfo = createSPRInfo(accountDataInfo);

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "SPRInfo creation completed");
		}

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Subscription creation started");
		}
		List<SubscriptionDetail> addOnSubscriptionDetails;
		List<SubscriberUsage> basePackageUsages;
		addOnSubscriptionDetails = createSubscriptionsDetails(nv648SubscriberInfo.getSubscriptionInfos());
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Subscription creation completed");
		}
		
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Base package usage creation started");
		}
		basePackageUsages = new ArrayList<SubscriberUsage>(0);
		SubscriberUsage basePackageUsage = createBasePackageUsage(nv648SubscriberInfo.getBaseUsageInfos(), sprInfo);
		if (basePackageUsage != null) {
			basePackageUsages.add(basePackageUsage);
		}
		
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Base package usage creation started");
		}
		
		return new SubscriberInfo(sprInfo, addOnSubscriptionDetails, basePackageUsages);
	}

	private @Nullable SubscriberUsage createBasePackageUsage(List<NV648BaseUsageInfo> nv648BaseUsageInfos, SPRInfo sprInfo) throws OperationFailedException {
		
		String productOfferName = packageNameMapping.getNewPackageName(sprInfo.getProductOffer());
		
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Base package after fetching from package name mapping is " + productOfferName);
		}

		ProductOffer productOffer = getPolicyRepository().getProductOffer().byName(productOfferName);
		if(Objects.isNull(productOffer)){
			throw new OperationFailedException("Error while creating base package usage for subscriber ID: " + sprInfo.getSubscriberIdentity()
					+ ". Reason: Base product offer not found with name: " + productOfferName, ResultCode.NOT_FOUND);
		}

		BasePackage basePackage = (BasePackage) productOffer.getDataServicePkgData();
		if (basePackage == null) {
			throw new OperationFailedException("Error while creating base package usage for subscriber ID: " + sprInfo.getSubscriberIdentity()
					+ ". Reason: Base package not found with name: " + productOfferName, ResultCode.NOT_FOUND);
		}
		
		if (PolicyStatus.FAILURE == basePackage.getStatus()) {
			throw new OperationFailedException("Error while creation of base package usage for subscriber ID: " + sprInfo.getSubscriberIdentity()
					+ ". Reason: Base package(name:" + basePackage.getName()
					+ ") has status FAILURE. Reason: " + basePackage.getFailReason(), ResultCode.INVALID_INPUT_PARAMETER);
		}


		NV648BaseUsageInfo nv648BaseUsageInfo = fetchBasePackageUsage(nv648BaseUsageInfos, sprInfo, basePackage);

		if (nv648BaseUsageInfo == null) {
			return null;
		}

		nv648BaseUsageInfo.setBasePackageName(productOfferName);

		QuotaProfile quotaProfile = basePackage.getQuotaProfiles().get(0);

		SubscriberUsage baseUsage = new SubscriberUsage(SubscriberUsage.NEW_ID, quotaProfile.getId(),
				sprInfo.getSubscriberIdentity(), CommonConstants.ALL_SERVICE_ID, null, basePackage.getId(),productOffer.getId());
		baseUsage.setBillingCycleUpload(nv648BaseUsageInfo.getUploadOctets());
		baseUsage.setBillingCycleDownload(nv648BaseUsageInfo.getDownloadOctets());
		baseUsage.setBillingCycleTotal(nv648BaseUsageInfo.getTotalOctets());
		baseUsage.setBillingCycleTime(nv648BaseUsageInfo.getUsageTime());
		baseUsage.setBillingCycleResetTime(0);

		
		return baseUsage;
	}

	private @Nullable NV648BaseUsageInfo fetchBasePackageUsage(List<NV648BaseUsageInfo> nv648BaseUsageInfos, SPRInfo sprInfo, BasePackage basePackage) throws OperationFailedException {

		if (QuotaProfileType.USAGE_METERING_BASED != basePackage.getQuotaProfileType() || Collectionz.isNullOrEmpty(basePackage.getQuotaProfiles())) {
			return null;
		}

		if (Collectionz.isNullOrEmpty(nv648BaseUsageInfos) == false) {
			for (NV648BaseUsageInfo nv648BaseUsageInfo : nv648BaseUsageInfos) {
				if (sprInfo.getProductOffer().equalsIgnoreCase(nv648BaseUsageInfo.getBasePackageName())) {
					return nv648BaseUsageInfo;
				}
			}
		}

		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "Creating zero base package usage for subscriber ID: " + sprInfo.getSubscriberIdentity()
					+ ". Reason: Usage not found for base package: " + sprInfo.getProductOffer());
		}

		return new NV648BaseUsageInfo();
	}

	public List<SubscriptionDetail> createSubscriptionsDetails(List<NV648SubscriptionInfo> subscriptionInfos) throws OperationFailedException {
		
		List<SubscriptionDetail> addOnSubscriptionDetails = new ArrayList<SubscriptionDetail>(0);

		if (Collectionz.isNullOrEmpty(subscriptionInfos)) {
			return addOnSubscriptionDetails;
		}
		
		for (NV648SubscriptionInfo subscriptionInfo : subscriptionInfos) {
			
			String subscriberId = subscriptionInfo.getSubscriberid();
			
			String oldPackageName = subscriptionInfo.getPackageName();
			
			String newPackageName = packageNameMapping.getNewPackageName(oldPackageName);

			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Subscription package after fetching from package name mapping is " + newPackageName);
			}
													
			ProductOffer productOffer = getPolicyRepository().getProductOffer().byName(newPackageName);
			
			if (productOffer == null) {
				throw new OperationFailedException("Error while creation of subscriptions for subscriber ID: " + subscriberId
						+ ". Reason: Package not found with name: " + newPackageName, ResultCode.NOT_FOUND);
			}
			
			if (PolicyStatus.FAILURE == productOffer.getPolicyStatus()) {
				throw new OperationFailedException("Error while creation of subscriptions for subscriber ID: " + subscriberId
						+ ". Reason: Package (name:" + productOffer.getName()
						+ ") has status FAILURE. Reason: " + productOffer.getFailReason(), ResultCode.INVALID_INPUT_PARAMETER);
			}
			
			String subscriptionId = getNewSubscriptionid(subscriptionInfo);
			UserPackage subscriptionPackage = productOffer.getDataServicePkgData();
			Subscription addOnSubscription = new Subscription(subscriptionId,
					subscriberId,
					productOffer.getId(),subscriptionPackage.getId(),
					subscriptionInfo.getStartTime(),
					subscriptionInfo.getEndTime(), 
					SubscriptionState.STARTED,
					CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY,
					SubscriptionType.ADDON,
					subscriptionInfo.getParam1(),
					subscriptionInfo.getParam2());

			if(subscriptionPackage != null) {

				if (subscriptionInfo.isUsageExist() == false) {

					if (QuotaProfileType.USAGE_METERING_BASED == subscriptionPackage.getQuotaProfileType()) {
						if (Collectionz.isNullOrEmpty(subscriptionPackage.getQuotaProfiles()) == false) {
							throw new OperationFailedException("Skipping creation of subscriptions for subscriber ID: " + subscriberId
									+ ". Reason: Subscription package(name:" + newPackageName + ") has quota profile but subscription found without usage", ResultCode.INVALID_INPUT_PARAMETER);
						}
					}

					SubscriptionDetail addOnSubscriptionDetail = new SubscriptionDetail(addOnSubscription, new ArrayList<SubscriberUsage>());
					addOnSubscriptionDetails.add(addOnSubscriptionDetail);
				} else {
					List<SubscriberUsage> subscriptionUsages = new ArrayList<SubscriberUsage>(0);

					if (QuotaProfileType.USAGE_METERING_BASED == subscriptionPackage.getQuotaProfileType()
							&& Collectionz.isNullOrEmpty(subscriptionPackage.getQuotaProfiles()) == false) {

						QuotaProfile quotaProfile = subscriptionPackage.getQuotaProfiles().get(0);

						SubscriberUsage subscriberUsage = new SubscriberUsage(SubscriberUsage.NEW_ID,
								quotaProfile.getId(),
								subscriberId,
								CommonConstants.ALL_SERVICE_ID,
								subscriptionId,
								subscriptionPackage.getId(),productOffer.getId());

						subscriberUsage.setBillingCycleUpload(subscriptionInfo.getUploadOctets());
						subscriberUsage.setBillingCycleDownload(subscriptionInfo.getDownloadOctets());
						subscriberUsage.setBillingCycleTotal(subscriptionInfo.getTotalOctets());
						subscriberUsage.setBillingCycleTime(subscriptionInfo.getUsageTime());
						subscriberUsage.setBillingCycleResetTime(0);
						subscriptionUsages.add(subscriberUsage);
					}

					SubscriptionDetail addOnSubscriptionDetail = new SubscriptionDetail(addOnSubscription, subscriptionUsages);
					addOnSubscriptionDetails.add(addOnSubscriptionDetail);
				}
			}
		}
		
		return addOnSubscriptionDetails;
	}

	private String getNewSubscriptionid(NV648SubscriptionInfo subscriptionInfo) {
		if(Strings.isNullOrEmpty(subscriptionInfo.getSubscription())) {
			return UUID.randomUUID().toString();
		} else {
			return subscriptionInfo.getSubscription();
		}
	}

	private SPRInfo createSPRInfo(AccountDataInfo accountDataInfo) throws OperationFailedException {
		
		if (Maps.isNullOrEmpty(accountDataInfo.getValueByColumnName())) {
			throw new OperationFailedException("SPR fields not found", ResultCode.NOT_FOUND);
		}
		
		Map<String, String> valueByColumnNameMap = accountDataInfo.getValueByColumnName();
		
		SPRInfoImpl sprInfo = new SPRInfoImpl();
		boolean validate = true;
		for (Entry<String, String> valueByColumnNameEntry : valueByColumnNameMap.entrySet()) {
			String oldColumnName = valueByColumnNameEntry.getKey();
			
			if (columnConfigurationProvider.isIgnoreColumn(oldColumnName)) {
				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "Skipping column: " + oldColumnName + ". Reason: Column is listed in ignore column list");
				}
				continue;
			}
			
			String newColumnName = columnConfigurationProvider.getNewColumnName(oldColumnName);
			
			SPRFields sprField = SPRFields.fromColumnName(newColumnName.toUpperCase());
			
			if (sprField == null) {
				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "Skipping value for column: " + oldColumnName + ". Reason: spr field not found");
				}
				continue;
			}
			
			String value = valueByColumnNameEntry.getValue();
			
			if (Strings.isNullOrBlank(value)) {
				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "Skipping value for key: " + sprField.name() + ". Reason: Value not found");
				}
				continue;
			}

			if (sprField.type == Types.TIMESTAMP) {
				if (Strings.isNullOrEmpty(value) == false) {
					long time;
					try {
						time = new SimpleDateFormat(columnConfigurationProvider.getDateFormat()).parse(value).getTime();
					} catch (ParseException e) {
						throw new OperationFailedException("Error while parsing spr field: " + sprField.name() + ". Reason: " + e.getMessage(), e);
					}
					sprField.setTimestampValue(sprInfo, new Timestamp(time), validate);
				}				
			} else {
				
				sprField.setStringValue(sprInfo, value, validate);
			}
			
		}
		
		
		if (Strings.isNullOrBlank(sprInfo.getSubscriberIdentity())) {
			String value = accountDataInfo.getValueByColumnName().get(SPRFields.SUBSCRIBER_IDENTITY.columnName);
			if(Strings.isNullOrBlank(value)) {
				throw new OperationFailedException("Subscriber identity not found from profile", ResultCode.NOT_FOUND);
			} else {
				sprInfo.setSubscriberIdentity(value);
			}
		}
		
		if (Strings.isNullOrBlank(sprInfo.getProductOffer())) {
			throw new OperationFailedException("Data package not found from profile for subscriber id: " + sprInfo.getSubscriberIdentity(), ResultCode.NOT_FOUND);
		}
		
		String newPackageName = packageNameMapping.getNewPackageName(sprInfo.getProductOffer());
		ProductOffer productOffer = getPolicyRepository().getProductOffer().byName(newPackageName);

		if (Objects.isNull(productOffer)) {
			throw new OperationFailedException("Base product offer not found with name: " + sprInfo.getProductOffer() + " in policy repository for subscriber id: " + sprInfo.getSubscriberIdentity(), ResultCode.NOT_FOUND);
		}

		BasePackage basePackage = (BasePackage) productOffer.getDataServicePkgData();
		
		if (basePackage == null) {
			throw new OperationFailedException("Base package not found with name: " + sprInfo.getProductOffer() + " in policy repository for subscriber id: " + sprInfo.getSubscriberIdentity(), ResultCode.NOT_FOUND);
		}
		
		if (PolicyStatus.FAILURE == basePackage.getStatus()) {
			throw new OperationFailedException("Subscriber profile creation failed. Reason: Data package(name:" + basePackage.getName()+ ") has status FAILURE. Reason: " + basePackage.getFailReason(), ResultCode.INVALID_INPUT_PARAMETER);
		}
		
		sprInfo.setProductOffer(newPackageName);
		
		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, sprInfo.toString());
		}
		
		return sprInfo;
	}
	
	private PolicyRepository getPolicyRepository() {
		return DefaultNVSMXContext.getContext().getPolicyRepository();
	}
}
