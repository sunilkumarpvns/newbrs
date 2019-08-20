package com.elitecore.aaa.rm.conf;

import com.elitecore.aaa.core.conf.DriverConfigurationProvider;
import com.elitecore.aaa.radius.conf.BaseRadiusServiceConfiguration;

public interface RMConcurrentLoginServiceConfiguration extends BaseRadiusServiceConfiguration, DriverConfigurationProvider {
	
	public String getSessionManagerName();
	public String getSessionManagerInstanceId();
	
}
