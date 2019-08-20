package com.elitecore.aaa.diameter.policies.applicationpolicy;

import java.util.Map;

import com.elitecore.aaa.diameter.policies.applicationpolicy.conf.DiameterServicePolicyConfiguration;
import com.elitecore.aaa.diameter.sessionmanager.DiameterSessionManager;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.core.servicex.ServiceContext;

public class DiameterServicePolicyContainerFactory {
	
	public static DiameterServicePolicyContainer getServicePolicyContainer(String serviceIdentifier, ServiceContext serviceContext,Map<String,DiameterServicePolicyConfiguration> policyConfMap, 
			DiameterSessionManager diameterSessionManager){
		if(AAAServerConstants.DIA_NAS_SERVICE_ID.equals(serviceIdentifier)){
			return new DiameterNASServicePolicyContainer(serviceContext, policyConfMap, diameterSessionManager);
		}else if (AAAServerConstants.DIA_EAP_SERVICE_ID.equals(serviceIdentifier)) {
			return new DiameterEAPServicePolicyContainer(serviceContext, policyConfMap, diameterSessionManager);
		}else if (AAAServerConstants.DIA_CC_SERVICE_ID.equals(serviceIdentifier)) {
			return new DiameterCCServicePolicyContainer(serviceContext, policyConfMap, diameterSessionManager);
		} else if (AAAServerConstants.DIA_TGPP_SERVER_SERVICE_ID.equals(serviceIdentifier)) {
			return new DiameterTGPPServerPolicyContainer(serviceContext, policyConfMap, diameterSessionManager);
		}
		return new DiameterNoneServicePolicyContainer(serviceContext,policyConfMap, diameterSessionManager);
	}

}
