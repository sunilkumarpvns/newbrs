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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

import static com.elitecore.commons.base.Collectionz.isNullOrEmpty;
import static com.elitecore.commons.logging.LogManager.getLogger;

/*
 * process the session level reported usage
 */
class SessionLevelUsageProcessor { 
	
	private static final String MODULE = "SESS-LVL-UM-HNDLR";
	
	private static final Pattern pattern = Pattern.compile(",");

	private NetVertexServerContext netVertexServerContext;
	
	public SessionLevelUsageProcessor(NetVertexServerContext netVertexServerContext) {
		this.netVertexServerContext = netVertexServerContext;
	}
	
	public void process(PCRFResponse response,
			ExecutionContext executionContext,
			List<UsageMonitoringInfo> monitoringInfos, 
			Map<String, Subscription> subscriptions,
			ServiceUsage currentServiceUsage,
			@Nullable SessionUsage currentSessionUsage) {
		
		String subscriberIdentity = response.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val);
		
		UsageMonitoringInfo reportedUsage = null;
		for (int index = 0; index < monitoringInfos.size(); index++) {
			UsageMonitoringInfo monitoringInfo = monitoringInfos.get(index);
			if (monitoringInfo.getUsageMonitoringLevel() == UMLevel.SESSION_LEVEL) {
				reportedUsage = monitoringInfo;
				break;
			}
		}
		
		if (reportedUsage == null) {
			getLogger().warn(MODULE, "Unable to meter package usage for subscriber:"
					+ subscriberIdentity + ". Reason: No session level usage reported");
			return;
		}
		
		Map<String, String> packageNameOrSubscriptionIdToQuotaProfileId = response.getUsageReservations();
		
		Set<String> keys = packageNameOrSubscriptionIdToQuotaProfileId.keySet();
		
		String packageIdOrSubscriptionId = null;
		String quotaProfileId = null;
		for(String key : keys) {
			packageIdOrSubscriptionId = key;
			String value = packageNameOrSubscriptionIdToQuotaProfileId.remove(key);
			if(value.contains(",")){
				String[] values = pattern.split(value, 2);
				quotaProfileId = values[0];
				packageNameOrSubscriptionIdToQuotaProfileId.put(key, values[1]);
			} else {
				quotaProfileId = value;
			}
			break;
		}
		
		
		if(isSubscription(packageIdOrSubscriptionId)) {
			
			if (Maps.isNullOrEmpty(subscriptions)) {
				getLogger().warn(UMHandler.MODULE, "Unable to meter package usage for subscriber ID: " + subscriberIdentity 
						+ ". Reason: Subscriptions not received");
				return;
			}
			
			Subscription subscription = subscriptions.get(packageIdOrSubscriptionId);
			if (subscription == null) {
				getLogger().warn(MODULE, "Unable to meter package usage for addOnSubscription:"
						+ packageIdOrSubscriptionId + " for subscriber ID: " + subscriberIdentity + ". Reason: Active subscription not found from DB");
				return;
			}
			

			SubscriptionPackage subscriptionPackage = netVertexServerContext.getPolicyRepository().getAddOnById(subscription.getPackageId());
			
			if (subscriptionPackage == null) {
				getLogger().warn(MODULE, "Unable to meter usage for key(" + reportedUsage.getMonitoringKey()
						+ ") subscription(" + subscription.getId() + ") for subscriber ID: "
						+ subscriberIdentity + ". Reason: Package not found for id: "
						+ subscription.getPackageId());
				return;
			}
			
			if (PolicyStatus.FAILURE == subscriptionPackage.getStatus()) {
				getLogger().warn(MODULE, "Unable to meter usage for key(" + reportedUsage.getMonitoringKey()
						+ ") subscription(" + subscription.getId() + ") for subscriber ID: "
						+ subscriberIdentity + ". Reason: Subscription package(name:" + subscriptionPackage.getName() + ") has status FAILURE. Reason: "
						+ subscriptionPackage.getFailReason());
				return;
			}

			if (subscriptionPackage.getQuotaProfileType() != QuotaProfileType.USAGE_METERING_BASED) {
				LogManager.getLogger().debug(MODULE, "Unable to meter package usage for subscription: "
						+ subscription.getId() + " package:"+ subscriptionPackage.getName() + ". Reason: Quota profile is not UM base");
				return;
			}

			
			
			if (PkgType.ADDON == subscriptionPackage.getPackageType()) {
				if (Collectionz.isNullOrEmpty(subscriptionPackage.getQuotaProfiles())) {
					if(getLogger().isDebugLogLevel())
						LogManager.getLogger().debug(MODULE, "Unable to meter package usage for subscription: "
								+ subscription.getId() + " package: "+ subscriptionPackage.getName() + ". Reason: Quota profile not configured");
					return;
				}
				processReportedUsageForAddOn(subscription, quotaProfileId, 
						currentServiceUsage, currentSessionUsage, reportedUsage,
						executionContext, subscriptionPackage);
			} else {
				getLogger().warn(MODULE, "Unable to meter usage for key(" + reportedUsage.getMonitoringKey()
						+ ") subscription(" + subscription.getId() + ") for subscriber ID: "
						+ subscriberIdentity + ". Reason: Unsupported package type: " + subscriptionPackage.getPackageType());
			}
			
		} else {
			processReportedUsageForBasePackage(packageIdOrSubscriptionId, quotaProfileId, currentServiceUsage, currentSessionUsage,
					reportedUsage, executionContext, response);
		}
		
		response.setUsageReservations(packageNameOrSubscriptionIdToQuotaProfileId);
		if (currentServiceUsage != null) {
			response.setServiceUsage(currentServiceUsage);
		}
		if (currentSessionUsage != null) {
			response.setSessionUsage(currentSessionUsage);
		}
		
	}
	
	private void processReportedUsageForAddOn(Subscription subscription, String quotaProfileId,
											  ServiceUsage currentServiceUsage, SessionUsage currentSessionUsage, UsageMonitoringInfo reportedUsage,
											  ExecutionContext executionContext, SubscriptionPackage addOn) {


		SubscriptionUMHandler subscriptionUMHandler = new SubscriptionUMHandler(
				subscription, 
				addOn,
				executionContext,
				currentServiceUsage,
				currentSessionUsage);
		
		subscriptionUMHandler.init();

		subscriptionUMHandler.addSessionLevelReportedUsage(reportedUsage, quotaProfileId);
		
		try {
			submitToRepository(executionContext, subscriptionUMHandler);
		} catch (OperationFailedException e) {
			getLogger().error(MODULE, "Error while submitting insert or update usage list. Reason:" + e.getMessage());
			if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
				getLogger().trace(MODULE, e);
			}
		}
	}

	private void processReportedUsageForBasePackage(String packageId, String quotaProfileId, ServiceUsage currentServiceUsage,
			SessionUsage currentSessionUsage, UsageMonitoringInfo reportedUsage, ExecutionContext executionContext, PCRFResponse response) {
		

		BasePackageUMHandler basePackageUMHandler = createBasePackageUsage(packageId, 
				response, 
				executionContext, 
				reportedUsage, 
				currentServiceUsage,
				currentSessionUsage);
		
		if(basePackageUMHandler == null) {
			return;
		}
		
		basePackageUMHandler.addSessionLevelReportedUsage(reportedUsage,quotaProfileId);
		try {
			submitToRepository(executionContext,
                    basePackageUMHandler);
		} catch (OperationFailedException e) {
			getLogger().error(MODULE, "Error while submitting insert or update usage list. Reason:" + e.getMessage());
			if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
				getLogger().trace(MODULE, e);
			}
		}
	}
	
	/*
	 * check by fetching base package. If base package is exist in policy manager then it is not addOn subscription.
	 * This solution works because we use UUID for packageId as well as addOnSubscription.
	 * So I will never happen that packagID and subscriptionID are same 
	 */
	private boolean isSubscription(String packageOrsubscriptionId) {
		BasePackage basePackage = netVertexServerContext.getPolicyRepository().getBasePackageDataById(packageOrsubscriptionId);
		if (basePackage != null) {
			return false;
		}
		
		com.elitecore.corenetvertex.pm.pkg.datapackage.PromotionalPackage promotionalPackage = netVertexServerContext.getPolicyRepository().getPromotionalPackageById(packageOrsubscriptionId);
		return promotionalPackage == null;
	}
	
	private void submitToRepository(ExecutionContext executionContext,
                                    BasePackageUMHandler basePackageUMHandler) throws OperationFailedException {


		if (isNullOrEmpty(basePackageUMHandler.getUpdateList()) == false) {
			if(basePackageUMHandler.isPreviousDayUsage() || basePackageUMHandler.isPreviousWeekUsage()
					|| basePackageUMHandler.isPreviousBillingCycleUsage()) {
				executionContext.replaceUsage(basePackageUMHandler.getUpdateList());
			} else {
				executionContext.addToExistingUsage(basePackageUMHandler.getUpdateList());
			}
		}

		if (isNullOrEmpty(basePackageUMHandler.getInsertList()) == false) {
			executionContext.insertNewUsage(basePackageUMHandler.getInsertList());
		}
	}
	
	private void submitToRepository(ExecutionContext executionContext,
                                    SubscriptionUMHandler addOnUMHandler) throws OperationFailedException {

		if(isNullOrEmpty(addOnUMHandler.getUpdateList()) == false) {
			if(addOnUMHandler.isPreviousDayUsage() || addOnUMHandler.isPreviousWeekUsage()
					|| addOnUMHandler.isPreviousBillingCycleUsage()) {

				executionContext.replaceUsage(addOnUMHandler.getUpdateList());
			} else {
				executionContext.addToExistingUsage(addOnUMHandler.getUpdateList());
			}
		}
	}
	
	private @Nullable BasePackageUMHandler createBasePackageUsage(String packageId, 
			PCRFResponse response,
			ExecutionContext executionContext,
			UsageMonitoringInfo usageMonitoringInfo,
			ServiceUsage currentServiceUsage, 
			@Nullable SessionUsage currentSessionUsage) {

		String subscriberIdentity = response.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val);
		String productOfferName = response.getAttribute(PCRFKeyConstants.SUB_PRODUCT_OFFER.val);

		ProductOffer productOffer;
		if(Objects.nonNull(productOfferName)) {
			productOffer = netVertexServerContext.getPolicyRepository().getProductOffer().byName(productOfferName);

			if(Objects.isNull(productOffer)) {
				if (getLogger().isWarnLogLevel()) {
					getLogger().warn(MODULE, "Unable to perform UM for monitoring key: " + usageMonitoringInfo.getMonitoringKey()
							+ ". Reason: product offer not found with name:" + productOfferName);
				}
				return null;
			}
		} else {
			if (getLogger().isWarnLogLevel()) {
				getLogger().warn(MODULE, "Unable to perform UM for monitoring key: " + usageMonitoringInfo.getMonitoringKey()
						+ ". Reason: product offer name not found from response");
			}
			return null;
		}

		BasePackage basePackage = (BasePackage) productOffer.getDataServicePkgData();
		
		if(basePackage == null) {
			if (getLogger().isWarnLogLevel()) {
				getLogger().warn(MODULE, "Unable to perform UM for monitoring key: " + usageMonitoringInfo.getMonitoringKey()
						+ ". Reason: subscriber package not found for ID: " + packageId);
			}
			return null;
		}
		
		if(PolicyStatus.FAILURE == basePackage.getStatus()) {
			if (getLogger().isWarnLogLevel()) {
				getLogger().info(MODULE, "Unable to perform UM for monitoring key: " + usageMonitoringInfo.getMonitoringKey()
						+ ". Reason: Base package(name:"+basePackage.getName()+") has status FAILURE. Reason: " + basePackage.getFailReason());
			}
			return null;
		}


		
		if (basePackage.getId().equalsIgnoreCase(packageId) == false) {
			BasePackage reportedUsageBasePackage = netVertexServerContext.getPolicyRepository().getBasePackageDataById(packageId);

			if (getLogger().isInfoLogLevel()) {
				getLogger().info(MODULE, "Unable to perform UM for monitoring key: " + usageMonitoringInfo.getMonitoringKey()
						+ ". Reason: subscriber package change and usage reported for previous package, current package: "
						+ basePackage.getName()+"(" + basePackage.getId()+ "), reported usage package: " + (Objects.nonNull(reportedUsageBasePackage) ? reportedUsageBasePackage.getName() + "("+ packageId+")" : packageId));
			}

			return null;
		}

		if (basePackage.getQuotaProfileType() != QuotaProfileType.USAGE_METERING_BASED) {
			if(getLogger().isDebugLogLevel())
				LogManager.getLogger().debug(MODULE, "Unable to perform UM for monitoring key:" + usageMonitoringInfo.getMonitoringKey() +
						" Reason:  Base package("+ basePackage.getName() + ") Quota profile Type is not UM base");
			return null;
		}

		if (Collectionz.isNullOrEmpty(basePackage.getQuotaProfiles())) {
			if(getLogger().isDebugLogLevel())
				LogManager.getLogger().debug(MODULE, "Unable to perform UM for monitoring key:" + usageMonitoringInfo.getMonitoringKey() +
						" Reason:  Base package("+ basePackage.getName() + ") has no Quota profile configured");
			return null;
		}

		
		BasePackageUMHandler basePackageUMHandler = new BasePackageUMHandler(subscriberIdentity, basePackage, productOffer.getId(), executionContext, currentServiceUsage, currentSessionUsage);
		basePackageUMHandler.init();

		return basePackageUMHandler;
	
	}
}