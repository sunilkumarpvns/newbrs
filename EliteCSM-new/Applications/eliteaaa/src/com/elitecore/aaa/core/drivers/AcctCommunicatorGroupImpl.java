package com.elitecore.aaa.core.drivers;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.drivers.DriverProcessFailedException;
import com.elitecore.core.servicex.ServiceContext;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.servicex.ServiceResponse;
import com.elitecore.core.systemx.esix.ESCommunicatorGroupImpl;

public class AcctCommunicatorGroupImpl extends ESCommunicatorGroupImpl<IEliteAcctDriver>
		implements AcctCommunicatorGroup {
	private static final String MODULE = "ACCT-COMM-GRP";
	public AcctCommunicatorGroupImpl(ServiceContext serviceContext) {
		super();
	}

	@Override
	public void handleAccountingRequest(ServiceRequest serviceRequest, ServiceResponse serviceResponse) throws DriverProcessFailedException{
		IEliteAcctDriver communicator = getCommunicator();
		if(communicator == null){
			throw new DriverProcessFailedException("No Alive Acct Communicator found in the group");
		}
		try {
			communicator.handleAccountingRequest(serviceRequest);
		} catch (DriverProcessFailedException e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "Primary Driver: " + communicator.getName() + " processing failed. Reason: " + e.getMessage());
			
			//Get Account Data using secondary system			
			IEliteAcctDriver secondaryCommunicator = getSecondaryCommunicator(communicator);
			if(secondaryCommunicator == null){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "No secondary Acct communicator found in group");
				throw e;
			}
			try{
				secondaryCommunicator.handleAccountingRequest(serviceRequest); 
			}catch (DriverProcessFailedException e1) {
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
					LogManager.getLogger().error(MODULE, "Secondary Acct Driver: " + secondaryCommunicator.getName() + " processing failed. Reason: " + e1.getMessage());
				throw e1;
			}
		}
	}
}
