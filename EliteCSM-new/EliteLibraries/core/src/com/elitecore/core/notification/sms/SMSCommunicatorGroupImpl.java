package com.elitecore.core.notification.sms;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.systemx.esix.ESCommunicatorGroupImpl;

/**
 * @author Manjil Purohit
 *
 */
public class SMSCommunicatorGroupImpl extends ESCommunicatorGroupImpl<SMSNotifier> implements SMSCommunicatorGroup {

	private static final String MODULE = "SMS-COMM-GRP";
	
	@Override
	public boolean send(String message, String number) {
		SMSNotifier communicator = getCommunicator();
		if(communicator == null){
			LogManager.getLogger().error(MODULE, "No alive SMS Communicator found");
			return false;
		}
		try {
			return communicator.send(message, number);
		} catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Primary SMS communicator processing failed. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
		
		// using secondary communicator
		 /*
		  * below code is working fine, still commented due to we do not have secondary communicator configuration  
		  */
		/*SMSNotifier secondaryCommunicator = getSecondaryCommunicator(communicator);
		if(secondaryCommunicator == null) {
			getLogger().warn(MODULE, "No secondary SMS Communicator found in group");
			return false;
		}
		
		try{
			return secondaryCommunicator.send(message, number);
		}catch (Exception e) {
			getLogger().error(MODULE, "Secondary SMS communicator processing failed. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
		}*/
		
		return false;
	}
	
}
