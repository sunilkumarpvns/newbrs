package com.elitecore.netvertexsm.util.driver;

import com.elitecore.core.commons.configuration.LoadConfigurationException;

public interface DriverConfiguration {

	/**
	 * This method return the driverInstanceId
	 * @return driverInstanceId
	 */
	public int getDriverInstanceId();
	
	/**
	 * This method return the driverTypeId
	 * @return driverTypeId
	 */
	public int getDriverTypeId();
	
	/**
	 * This method returns driver name
	 * @return<code>String</code>
	 */
	public String getDriverName();

	public void readConfiguration() throws LoadConfigurationException;
	
}
