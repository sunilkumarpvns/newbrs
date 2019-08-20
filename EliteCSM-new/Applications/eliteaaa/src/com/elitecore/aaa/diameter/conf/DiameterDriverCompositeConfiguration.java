package com.elitecore.aaa.diameter.conf;

import java.util.Map;

import com.elitecore.aaa.core.drivers.DriverConfiguration;

public interface DiameterDriverCompositeConfiguration {
	public Map<Integer,DriverConfiguration> getDriverConfigurationMap();
	public DriverConfiguration getDriverConfiguration(int driverInstanceId);
}
