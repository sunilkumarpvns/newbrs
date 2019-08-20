package com.elitecore.netvertex.service.pcrf.servicepolicy.handler;

import com.elitecore.netvertex.pm.BasePackage;
import com.elitecore.netvertex.pm.PolicyContext;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class BasePolicyHandler {

	private static final String MODULE = "BASE-POLICY-HANDLER";

	public void applyPackage(PolicyContext policyContext) {
	
		BasePackage basePackage = policyContext.getBasePackage();

		if (getLogger().isInfoLogLevel())
			getLogger().info(MODULE, "Applying base package: " + basePackage.getName());

		com.elitecore.netvertex.pm.PackageProcessor.apply(policyContext, basePackage, null);

		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "Applying package: " + basePackage.getName() + " completed");
		}

	}
	
	
}

