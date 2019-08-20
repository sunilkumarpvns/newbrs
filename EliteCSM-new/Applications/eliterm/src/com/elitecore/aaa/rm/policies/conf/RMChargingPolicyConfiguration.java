package com.elitecore.aaa.rm.policies.conf;

import java.util.Map;


public interface RMChargingPolicyConfiguration {
	
	public String getRuleSet();
	
	public String getPolicyId();
	
	public String getPolicyName();
	
	public Map<String,Integer> getDriverInstanceIdsMap();
	
	public String getDriverScript();

}
