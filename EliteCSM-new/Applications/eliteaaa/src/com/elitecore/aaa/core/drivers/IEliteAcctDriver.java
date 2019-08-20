package com.elitecore.aaa.core.drivers;

import com.elitecore.core.commons.drivers.DriverProcessFailedException;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.systemx.esix.ESCommunicator;

public interface IEliteAcctDriver extends ESCommunicator{
	
	public void handleAccountingRequest(ServiceRequest serviceRequest) throws DriverProcessFailedException;
	public String cdrflush();
	
}
