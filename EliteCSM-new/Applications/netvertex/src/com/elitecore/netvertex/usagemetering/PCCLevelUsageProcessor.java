package com.elitecore.netvertex.usagemetering;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Maps;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.data.SessionUsage;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.pkg.datapackage.BasePackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.SubscriptionPackage;
import com.elitecore.corenetvertex.spr.ServiceUsage;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.elitecore.commons.base.Collectionz.isNullOrEmpty;
import static com.elitecore.commons.logging.LogManager.getLogger;

/*
 * Usage processor process the PCC level reported usage and calculate the usage accordingly.
 */
class PCCLevelUsageProcessor {

	private static final String MODULE = "PCC-LVL-USAGE-PRCSR";
	private NetVertexServerContext netVertexServerContext;

	public PCCLevelUsageProcessor(NetVertexServerContext netVertexServerContext) {
		this.netVertexServerContext = netVertexServerContext;
	}

	public void process(PCRFResponse response,
			ExecutionContext executionContext,
			@Nullable Map<String, Subscription> subscriptions,
			@Nullable ServiceUsage currentServiceUsage,
			@Nullable SessionUsage currentSessionUsage,
			List<UsageMonitoringInfo> reportedUsageList) {

		String subscriberIdentity = response.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.getVal());
		
		Map<String, String> monitoringKeyToPackageNameOrSubscriptionId = response.getUsageReservations();

		Map<String, PackageUMHandler> subscriptionIdToPackageUMHandler = new HashMap<String, PackageUMHandler>();

		for (int index = 0; index < reportedUsageList.size(); index++) {

			UsageMonitoringInfo reportedUsage = reportedUsageList.get(index);

			if (reportedUsage.getUsageMonitoringLevel() != UMLevel.PCC_RULE_LEVEL) {
				continue;
			}

			/*
			 * Here we need to remove the key from usageReservations because
			 * UsageReservation map contains keys whose usage is still not
			 * reported. Once it gets reported key should be removed from
			 * UsageReservation map
			 */
			String packageIdOrSubscriptionId = monitoringKeyToPackageNameOrSubscriptionId.remove(reportedUsage.getMonitoringKey());
			if (packageIdOrSubscriptionId == null) {
				getLogger().warn(MODULE,
						"No subscriber package or subscription found for monitoring key: " + reportedUsage.getMonitoringKey());
				continue;
			}

			if (isSubscription(packageIdOrSubscriptionId)) {

				if (Maps.isNullOrEmpty(subscriptions)) {
					getLogger().warn(MODULE, "Unable to meter package usage for subscriber ID: " + subscriberIdentity 
							+ ". Reason: Subscriptions not received");
					continue;
				}

				Subscription subscription = subscriptions.get(packageIdOrSubscriptionId);
				if (subscription == null) {
					getLogger().warn(MODULE, "Unable to meter package usage for subscription:"
							+ packageIdOrSubscriptionId + " for subscriber ID: " + subscriberIdentity + ". Reason: Active subscription not found from DB");
					continue;
				}
				
				SubscriptionPackage subscriptionPackage = netVertexServerContext.getPolicyRepository().getAddOnById(subscription.getPackageId());
				
				if (subscriptionPackage == null) {
					getLogger().warn(MODULE, "Unable to meter usage for key(" + reportedUsage.getMonitoringKey()
							+ ") for subscription(" + subscription.getId() + ") for subscriber ID: "
							+ subscriberIdentity + ". Reason: Package not found for id: "
							+ subscription.getPackageId());
					continue;
				}
				
				if (PolicyStatus.FAILURE == subscriptionPackage.getStatus()) {
					getLogger().warn(MODULE, "Unable to meter usage for key(" + reportedUsage.getMonitoringKey()
							+ ") for subscription(" + subscription.getId() + ") for subscriber ID: "
							+ subscriberIdentity + ". Reason: Subscription package(name:" 
							+ subscriptionPackage.getName() + ") has status FAILURE. Reason: "
									+ subscriptionPackage.getFailReason());
					continue;
				}


				
				if (PkgType.ADDON == subscriptionPackage.getPackageType()) {
					processReportedUsageForAddOn(subscriptionIdToPackageUMHandler, subscription, currentServiceUsage, currentSessionUsage, 
							reportedUsage, executionContext, subscriptionPackage);
				}

			} else {				
				processReportedUsageForBasePackage(subscriptionIdToPackageUMHandler,
						packageIdOrSubscriptionId, 
						currentServiceUsage,
						currentSessionUsage,
						reportedUsage,
						response,
						executionContext);
			}

		}
		
		try {
			submitToRepository(executionContext,
					subscriptionIdToPackageUMHandler);
		} catch (OperationFailedException e) {
			getLogger().error(MODULE,
					"Error while submitting insert or update usage list for subscriber ID: " + subscriberIdentity + ". Reason:" + e.getMessage());
			if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
				getLogger().trace(MODULE, e);
			}
		}

		response.setUsageReservations(monitoringKeyToPackageNameOrSubscriptionId);
		if (currentServiceUsage != null) {
			response.setServiceUsage(currentServiceUsage);
		}
		if (currentSessionUsage != null) {
			response.setSessionUsage(currentSessionUsage);
		}

	}

	/*
	 * check by fetching base package. If base package is exist in policy
	 * manager then it is not addOn subscription. This solution works because we
	 * use UUID for packageId as well as addOnSubscription. So I will never
	 * happen that packagID and subscriptionID are same
	 */
	private boolean isSubscription(String packageOrAddOnSubscription) {
		BasePackage basePackage = netVertexServerContext.getPolicyRepository().getBasePackageDataById(packageOrAddOnSubscription);
		if (basePackage != null) {
			return false;
		}
		
		com.elitecore.corenetvertex.pm.pkg.datapackage.PromotionalPackage promotionalPackage = netVertexServerContext.getPolicyRepository().getPromotionalPackageById(packageOrAddOnSubscription);
		return promotionalPackage == null;
	}

	@Nullable
	BasePackageUMHandler createBasePackageUsage(String packageId,
			PCRFResponse response,
			ExecutionContext executionContext,
			UsageMonitoringInfo usageMonitoringInfo,
			ServiceUsage currentServiceUsage,
			@Nullable SessionUsage currentSessionUsage) {

		String subscriberIdentity = response.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val);

		com.elitecore.corenetvertex.pm.pkg.datapackage.Package pakage = netVertexServerContext.getPolicyRepository().getBasePackageDataById(packageId);
		String productOfferId = null;
		if (pakage == null) {
			pakage = netVertexServerContext.getPolicyRepository().getPromotionalPackageById(packageId);
		} else {
			productOfferId = getProductOfferId(response);
		}

		if (pakage == null) {
			if (getLogger().isInfoLogLevel()) {
				getLogger().info(MODULE, "Unable to perform UM for monitoring key:" + usageMonitoringInfo.getMonitoringKey()
						+ ". Reason: subscriber package not found for id:" + packageId);
			}
			return null;
		}
		
		if(PolicyStatus.FAILURE == pakage.getStatus()) {
			if (getLogger().isWarnLogLevel()) {
				getLogger().info(MODULE, "Unable to perform UM for monitoring key: " + usageMonitoringInfo.getMonitoringKey()
						+ ". Reason: Base package("+pakage.getName()+") has status FAILURE. Reason: " + pakage.getFailReason());
			}
			return null;
		}


		if (pakage.getQuotaProfileType() != QuotaProfileType.USAGE_METERING_BASED) {
			if(getLogger().isDebugLogLevel())
				LogManager.getLogger().debug(MODULE, "Unable to perform UM for monitoring key:" + usageMonitoringInfo.getMonitoringKey() +
						" Reason:  Base package("+ pakage.getName() + ") Quota profile Type is not UM base");
			return null;
		}

		if (Collectionz.isNullOrEmpty(pakage.getQuotaProfiles())) {
			if(getLogger().isDebugLogLevel())
				LogManager.getLogger().debug(MODULE, "Unable to perform UM for monitoring key:" + usageMonitoringInfo.getMonitoringKey() +
						" Reason:  Base package("+ pakage.getName() + ") has no Quota profile configured");
			return null;
		}

		BasePackageUMHandler basePackageUMHandler = new BasePackageUMHandler(subscriberIdentity, pakage, productOfferId, executionContext, currentServiceUsage, currentSessionUsage);
		basePackageUMHandler.init();
		
		return basePackageUMHandler;

	}

	private String getProductOfferId(PCRFResponse response) {
		String productOfferName = response.getAttribute(PCRFKeyConstants.SUB_PRODUCT_OFFER.val);
		ProductOffer productOffer = netVertexServerContext.getPolicyRepository().getProductOffer().byName(productOfferName);
		return productOffer==null?null:productOffer.getId();
	}

	private void submitToRepository(ExecutionContext executionContext,
									Map<String, PackageUMHandler> packageOrSubscriptionIdToPackageUMHandler) throws OperationFailedException {
		
		if (packageOrSubscriptionIdToPackageUMHandler != null) {
			for (PackageUMHandler packageUMHandler : packageOrSubscriptionIdToPackageUMHandler.values()) {
				
				if (isNullOrEmpty(packageUMHandler.getUpdateList()) == false) {
					if (packageUMHandler.isPreviousDayUsage() || packageUMHandler.isPreviousWeekUsage()
							|| packageUMHandler.isPreviousBillingCycleUsage()) {
						
						executionContext.replaceUsage(packageUMHandler.getUpdateList());
					} else {
						executionContext.addToExistingUsage(packageUMHandler.getUpdateList());
					}
				}
				
				if (isNullOrEmpty(packageUMHandler.getInsertList()) == false) {
					executionContext.insertNewUsage(packageUMHandler.getInsertList());
				}
				
			}
		}
	}
	private void processReportedUsageForAddOn(
			Map<String, PackageUMHandler> subscriptionIdToSubscriptionHandler,
			Subscription subscription,
			ServiceUsage currentServiceUsage,
			SessionUsage currentSessionUsage,
			UsageMonitoringInfo reportedUsage,
			ExecutionContext executionContext, SubscriptionPackage addOn){

		if (addOn.getQuotaProfileType() != QuotaProfileType.USAGE_METERING_BASED) {
			if(getLogger().isDebugLogLevel())
				getLogger().debug(MODULE, "Unable to meter package usage for subscription: "
						+ subscription.getId() + " package:"+ addOn.getName() + ". Reason: Quota profile is not UM base");
			return;
		}

		if (Collectionz.isNullOrEmpty(addOn.getQuotaProfiles())) {
			if(getLogger().isDebugLogLevel())
				LogManager.getLogger().debug(MODULE, "Unable to meter package usage for subscription: "
						+ subscription.getId() + " package: "+ addOn.getName() + ". Reason: Quota profile not configured");
			return;
		}
	
		PackageUMHandler subscriptionUMHandler = subscriptionIdToSubscriptionHandler.get(subscription.getId());
		if (subscriptionUMHandler == null) {
			subscriptionUMHandler = new SubscriptionUMHandler(
					subscription, 
					addOn, 
					executionContext,
					currentServiceUsage,
					currentSessionUsage);

			subscriptionUMHandler.init();
			
			subscriptionIdToSubscriptionHandler.put(subscription.getId(), subscriptionUMHandler);
		}

		subscriptionUMHandler.addPCCLevelReportedUsage(reportedUsage);
	}
	
	private void processReportedUsageForBasePackage(
			Map<String, PackageUMHandler> packageIdToPackageUMHandler,
			String packageId,
			ServiceUsage currentServiceUsage,
			SessionUsage currentSessionUsage,
			UsageMonitoringInfo reportedUsage,
			PCRFResponse response,
			ExecutionContext executionContext) {

		PackageUMHandler packageUMHandler = packageIdToPackageUMHandler.get(packageId);
		
		if (packageUMHandler == null) {
			packageUMHandler = createBasePackageUsage(packageId,
					response,
					executionContext,
					reportedUsage,
					currentServiceUsage,
					currentSessionUsage);
			if (packageUMHandler == null) {
				return;
			}
			packageIdToPackageUMHandler.put(packageId, packageUMHandler);
		}
		
		packageUMHandler.addPCCLevelReportedUsage(reportedUsage);
	}

}