package com.elitecore.aaa.rm.drivers;

import com.elitecore.core.commons.drivers.DriverProcessFailedException;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.servicex.ServiceResponse;
import com.elitecore.core.systemx.esix.ESCommunicatorGroup;

public interface RMChargingCommunicatorGroup extends ESCommunicatorGroup<RMChargingDriver> {
	
	public void handleRequest(ServiceRequest request, ServiceResponse response) throws DriverProcessFailedException;

}
