package com.elitecore.aaa.radius.conf;

import java.util.Map;

import com.elitecore.aaa.radius.conf.impl.DynAuthExternalSystemDetail;
import com.elitecore.aaa.radius.policies.servicepolicy.conf.DynAuthServicePolicyConfiguration;

public interface RadDynAuthConfiguration  extends BaseRadiusServiceConfiguration{
	
	public DynAuthServicePolicyConfiguration getDynAuthServicePolicyConfiguraion(String policyId);
	public Map<String, DynAuthServicePolicyConfiguration> getDynAuthServicePolicyConfiguraionMap();
	
	public DynAuthExternalSystemDetail getExternalDataBaseDetail() ;
		
}
