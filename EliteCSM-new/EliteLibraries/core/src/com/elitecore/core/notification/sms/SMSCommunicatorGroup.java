package com.elitecore.core.notification.sms;

import com.elitecore.core.systemx.esix.ESCommunicatorGroup;

/**
 * @author Manjil Purohit
 *
 */
public interface SMSCommunicatorGroup extends ESCommunicatorGroup<SMSNotifier> {

	public boolean send(String message, String number);
	
}
