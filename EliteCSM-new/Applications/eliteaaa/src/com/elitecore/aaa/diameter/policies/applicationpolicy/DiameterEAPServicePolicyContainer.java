package com.elitecore.aaa.diameter.policies.applicationpolicy;

import java.util.Map;

import javax.annotation.Nullable;

import com.elitecore.aaa.diameter.policies.applicationpolicy.conf.DiameterServicePolicyConfiguration;
import com.elitecore.aaa.diameter.service.DiameterServiceContext;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.sessionmanager.DiameterSessionManager;
import com.elitecore.core.serverx.servicepolicy.ServicePolicy;
import com.elitecore.core.servicex.ServiceContext;

public class DiameterEAPServicePolicyContainer extends DiameterServicePolicyContainer {

	public DiameterEAPServicePolicyContainer(ServiceContext serviceContext,Map<String,DiameterServicePolicyConfiguration> policyConfMap,
			@Nullable DiameterSessionManager diameterSessionManager){
		super(serviceContext, policyConfMap, diameterSessionManager);
	}
	
	@Override
	protected ServicePolicy<ApplicationRequest> getPolicyObject(ServiceContext serviceContext,String policyId, 
			@Nullable DiameterSessionManager diameterSessionManager) {
		return new EapAppPolicy((DiameterServiceContext)serviceContext, policyId, diameterSessionManager);
	}

}
