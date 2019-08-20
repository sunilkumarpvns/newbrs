package com.elitecore.aaa.core.drivers;

import javax.annotation.Nullable;

import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.core.driverx.IEliteDriver;

/**
 * Creates the instance of the driver based on it's type
 *
 */
public interface DriverFactory {
	/**
	 * Factory method to create instance of driver based on type.
	 * 
	 * @param driverType type of driver.
	 * @param driverInstanceId instance id of driver.
	 * @return newly created driver or null if type is unknown
	 */
	@Nullable IEliteDriver createDriver(DriverTypes driverType, String driverInstanceId);
}
