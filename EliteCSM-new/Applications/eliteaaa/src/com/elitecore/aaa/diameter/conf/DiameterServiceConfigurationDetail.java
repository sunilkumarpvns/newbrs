package com.elitecore.aaa.diameter.conf;

import java.util.Map;

import com.elitecore.aaa.diameter.policies.applicationpolicy.conf.DiameterServicePolicyConfiguration;

public interface DiameterServiceConfigurationDetail {
	
	public DiameterServicePolicyConfiguration getDiameterServicePolicyConfiguration(String policyId);
	
	public Map<String,DiameterServicePolicyConfiguration> getDiameterServicePolicyConfiguration();
	
	public DiameterServiceConfigurable getDiameterServiceConfiguration();
	
	public String getKey();
}
