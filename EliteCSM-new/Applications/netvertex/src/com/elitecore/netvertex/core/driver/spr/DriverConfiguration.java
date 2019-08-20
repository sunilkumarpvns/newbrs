package com.elitecore.netvertex.core.driver.spr;

import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.corenetvertex.util.ToStringable;

/**
 * This interface provide basic configuration information for subscriber profile
 * @author Harsh Patel
 *
 */
public interface DriverConfiguration extends ToStringable {
	
	/**
	 * This method return the driverInstanceId
	 * @return driverInstanceId
	 */
	public String getDriverInstanceId();
	
	/**
	 * This method return the driverType
	 * @return driverTypeId
	 */
	public String getDriverType();
	
	/**
	 * This method returns driver name
	 * @return<code>String</code>
	 */
	public String getDriverName();
	
	public void reloadDriverConfiguration() throws LoadConfigurationException;
}
