package com.elitecore.aaa.radius.drivers;

import java.util.List;

import com.elitecore.aaa.core.drivers.DriverConfiguration;

/**
 * Interface for all driver configurations.
 * 
 * @author narendra.pathai
 */
public interface DriverConfigurable {
	/**
	 * Returns the list of driver configurations read.
	 */
	public List<? extends DriverConfiguration> getDriverConfigurationList();
}
