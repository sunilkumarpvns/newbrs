package com.elitecore.netvertex.pm;

import static com.elitecore.commons.logging.LogManager.getLogger;

import java.util.Collections;
import java.util.Map;

import javax.annotation.Nullable;

import com.elitecore.corenetvertex.pm.constants.SelectionResult;
import com.elitecore.corenetvertex.spr.ServiceUsage;
import com.elitecore.corenetvertex.spr.SubscriberUsage;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;

public interface QoSProfileDetail extends com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfileDetail{

	public String FUP = "FUP";
	public String HSQ = "HSQ";

	public static interface UsageProvider extends com.elitecore.corenetvertex.pm.pkg.datapackage.UsageProvider{

		public static final UsageProvider CURRENT_EXECUTING_PACKAGE_USAGE_PROVIDER = new CurrentExecutingSubscriptionUsageProvider();
		public static final UsageProvider BASE_PACKAGE_USAGE_PROVIDER = new BasePackageUsageProvider();

		@Nullable Map<String, SubscriberUsage> getCurrentUsage(PolicyContext policyContext, QoSInformation qosInformation) throws OperationFailedException;
	}

	public static class CurrentExecutingSubscriptionUsageProvider implements UsageProvider {
		
		private static final String MODULE = "SUBSCRIPTION-USAGE-PROVIDER";
		@Override
		@Nullable public Map<String, SubscriberUsage> getCurrentUsage(PolicyContext policyContext, QoSInformation qosInformation)
				throws OperationFailedException {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Fetching current usage of current executing subscriptoin");
			}
			ServiceUsage currentServiceUsage = policyContext.getCurrentUsage();

			if (currentServiceUsage != null) {
				return currentServiceUsage.getPackageUsage(qosInformation.getCurrentSubscriptionOrPackageId());
			}
			return Collections.emptyMap();
		}

	}

	public static class BasePackageUsageProvider implements UsageProvider {
		
		private static final String MODULE = "BASE-PACKAGE-USAGE-PROVIDER";
		
		@Override
		@Nullable public Map<String, SubscriberUsage> getCurrentUsage(PolicyContext policyContext, QoSInformation qosInformation)
				throws OperationFailedException {
			
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Fetching current usage of base package");
			}
			ServiceUsage currentServiceUsage = policyContext.getCurrentUsage();

			if (currentServiceUsage != null) {
				return currentServiceUsage.getPackageUsage(policyContext.getBasePackage().getId());
			}
			return Collections.emptyMap();
		}

	}
	
	SelectionResult apply(PolicyContext policyContext, QoSInformation qosInformation, SelectionResult previousQoSResult);

}
