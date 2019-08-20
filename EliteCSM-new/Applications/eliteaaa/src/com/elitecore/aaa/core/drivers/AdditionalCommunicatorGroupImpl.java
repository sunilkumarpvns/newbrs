package com.elitecore.aaa.core.drivers;

import com.elitecore.core.commons.drivers.DriverProcessFailedException;
import com.elitecore.core.servicex.ServiceContext;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.servicex.ServiceResponse;
import com.elitecore.core.systemx.esix.ESCommunicatorGroupImpl;

public class AdditionalCommunicatorGroupImpl extends ESCommunicatorGroupImpl implements AdditionalCommunicatorGroup{

	public AdditionalCommunicatorGroupImpl(ServiceContext serviceContext) {
		super();
	}

	@Override
	public void handleAccountingRequest(ServiceRequest serviceRequest,
			ServiceResponse serviceResponse)
			throws DriverProcessFailedException {
		// TODO Auto-generated method stub
		
	}

}
