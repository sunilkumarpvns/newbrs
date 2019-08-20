package com.elitecore.aaa.rm.conf;

import java.util.Map;

import com.elitecore.aaa.core.conf.DriverConfigurationProvider;
import com.elitecore.aaa.radius.conf.BaseRadiusServiceConfiguration;
import com.elitecore.aaa.rm.policies.conf.RMChargingPolicyConfiguration;
import com.elitecore.aaa.rm.policies.conf.impl.RMChargingPolicyConfigurationImpl;


public interface RMChargingServiceConfiguration extends BaseRadiusServiceConfiguration, DriverConfigurationProvider {

	public String getServiceIdentifier();

	public RMChargingPolicyConfiguration getPolicyConfigration(String policyId);

	public Map<String, RMChargingPolicyConfigurationImpl> getPolicyConfigrationMap();

}
