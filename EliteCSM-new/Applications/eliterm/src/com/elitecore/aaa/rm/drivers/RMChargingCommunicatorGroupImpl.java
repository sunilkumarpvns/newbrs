package com.elitecore.aaa.rm.drivers;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.drivers.DriverProcessFailedException;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.servicex.ServiceResponse;
import com.elitecore.core.systemx.esix.ESCommunicatorGroupImpl;

public class RMChargingCommunicatorGroupImpl extends ESCommunicatorGroupImpl<RMChargingDriver> implements
		RMChargingCommunicatorGroup {
	private static final String MODULE = "RM-CHRGN-COMM-GRP";

	public RMChargingCommunicatorGroupImpl() {
		super();
	}

	@Override
	public void handleRequest(ServiceRequest request, ServiceResponse response) throws DriverProcessFailedException{
		RMChargingDriver communicator = getCommunicator();
		if(communicator == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "No Alive RM Charging Communicator found in group.");			
		}else{
			try {
				communicator.handleRequest(request, response);
				return;
			} catch (DriverProcessFailedException e) {
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
					LogManager.getLogger().error(MODULE, "Primary Driver processing failed. Reason: " + e.getMessage());
			}
		}

		//Handle Data using secondary system
		RMChargingDriver secondaryCommunicator = getSecondaryCommunicator(communicator);
		if(secondaryCommunicator == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "No Secondary Alive RM Charging Communicator found in group.");
			throw new DriverProcessFailedException("No Alive RM Charging Communicator found in the group.");
		}else{
			try{
				secondaryCommunicator.handleRequest(request, response); 
			}catch (DriverProcessFailedException e) {
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
					LogManager.getLogger().error(MODULE, "Secondary Driver processing failed. Reason: " + e.getMessage());
				throw e;
			}
		}
	}
	
}
