package com.elitecore.aaa.core.drivers;

import com.elitecore.core.commons.drivers.DriverProcessFailedException;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.servicex.ServiceResponse;
import com.elitecore.core.systemx.esix.ESCommunicatorGroup;

public interface AcctCommunicatorGroup extends ESCommunicatorGroup<IEliteAcctDriver> {
	void handleAccountingRequest(ServiceRequest serviceRequest,ServiceResponse serviceResponse) throws DriverProcessFailedException;
}
