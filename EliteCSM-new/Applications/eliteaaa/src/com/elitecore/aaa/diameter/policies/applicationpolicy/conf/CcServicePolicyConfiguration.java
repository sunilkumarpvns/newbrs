package com.elitecore.aaa.diameter.policies.applicationpolicy.conf;

import java.util.Map;


public interface CcServicePolicyConfiguration extends DiameterServicePolicyConfiguration{
	
	public Map<String,Integer> getDriverInstanceIdMap();
	
	public String getDriverScript();

}
