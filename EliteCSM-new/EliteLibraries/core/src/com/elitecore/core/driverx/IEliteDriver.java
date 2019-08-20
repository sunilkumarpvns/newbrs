package com.elitecore.core.driverx;

import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.systemx.esix.ESCommunicator;

public interface IEliteDriver extends ESCommunicator {
	
	/**
	 * This method is called once the driver instance has been created by the system.
	 * 
	 * <p>The implementation can perform following type of operations such as: initializing the
	 * database, validating the configuration, creating or reserving some resources such
	 * as threads.
	 * 
	 * <p>After calling this method the driver may or may not be alive, callers should call
	 * {@link #isAlive()} to check whether the driver is ready to handle any request
	 * 
	 * @throws DriverInitializationFailedException if there is any error in initializing
	 * the driver
	 */
	public void init() throws DriverInitializationFailedException;
	
	public String getName();
	
	public int getType();
	
	public String getTypeName();
	
}
