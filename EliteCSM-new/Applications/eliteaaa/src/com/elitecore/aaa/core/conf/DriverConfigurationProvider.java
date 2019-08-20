package com.elitecore.aaa.core.conf;

import java.util.Collection;

import com.elitecore.aaa.core.drivers.DriverConfiguration;

public interface DriverConfigurationProvider {
	public DriverConfiguration getDriverConfiguration(String driverInstanceId);
	public Collection<DriverConfiguration> getDriverConfigurations();
}
