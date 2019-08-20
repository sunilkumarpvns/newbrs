package com.elitecore.diameterapi.core.drivers;

import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.commons.drivers.DriverNotFoundException;
import com.elitecore.core.commons.drivers.TypeNotSupportedException;
import com.elitecore.core.driverx.cdr.CDRDriver;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;

public interface DiameterCDRDriverFactory {
	
	public CDRDriver<DiameterPacket> getDriverById(String driverId) throws DriverInitializationFailedException, DriverNotFoundException, TypeNotSupportedException;
	public CDRDriver<DiameterPacket> getDriver(String driverName) throws DriverInitializationFailedException, DriverNotFoundException, TypeNotSupportedException;

}
