package com.elitecore.aaa.diameter.policies.applicationpolicy;

import java.util.Map;

import com.elitecore.aaa.diameter.policies.applicationpolicy.conf.DiameterServicePolicyConfiguration;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.sessionmanager.DiameterSessionManager;
import com.elitecore.core.serverx.servicepolicy.ServicePolicy;
import com.elitecore.core.servicex.ServiceContext;

public class DiameterNoneServicePolicyContainer extends DiameterServicePolicyContainer {

	public DiameterNoneServicePolicyContainer(ServiceContext serviceContext,
			Map<String, DiameterServicePolicyConfiguration> policyConfMap, 
			DiameterSessionManager diameterSessionManager) {
		super(serviceContext, policyConfMap, diameterSessionManager);
	}

	@Override
	protected ServicePolicy<ApplicationRequest> getPolicyObject(ServiceContext serviceContext,
			String policyId, DiameterSessionManager diameterSessionManager) {
		throw new IllegalArgumentException("Invalid diameter Service-Policy: " + policyId);
	}

}
