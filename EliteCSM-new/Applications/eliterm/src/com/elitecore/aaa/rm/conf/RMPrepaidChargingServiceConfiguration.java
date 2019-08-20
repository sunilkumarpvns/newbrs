package com.elitecore.aaa.rm.conf;

import java.util.Map;

import com.elitecore.aaa.core.conf.DriverConfigurationProvider;
import com.elitecore.aaa.radius.conf.BaseRadiusServiceConfiguration;

public interface RMPrepaidChargingServiceConfiguration extends BaseRadiusServiceConfiguration, DriverConfigurationProvider {
	
	public Map getDriverConfig() ;
	public String getDriverInstanceId();
}
