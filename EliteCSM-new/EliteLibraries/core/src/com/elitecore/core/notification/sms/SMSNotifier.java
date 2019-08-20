package com.elitecore.core.notification.sms;

import java.util.Map;

import com.elitecore.core.commons.util.constants.SMSNotifierKeyReplacer;
import com.elitecore.core.systemx.esix.ESCommunicator;


/**
 * This Interface Provides method for send sms Notification 
 *
 */
public interface SMSNotifier extends ESCommunicator {

	/**
	 * send message to given number
	 * 
	 * @param message a message content for send
	 * @param number a number on which message send 
	 * 
	 * @return <code>true</code> if the message send successfully
	 * <code>false</code> if the message not send to number
	 */
	public boolean send(String message, String number);
	
	
	/**
	 * replace parameter Of key Replacer Map in message body then send message to given number
	 * 
	 * @param message a message content for send
	 * @param number a number on which message send 
	 * @param keyReplacerMap a Map of Replace Parameters(SMSNotifierKeyReplacer/value)
	 * 
	 * @return <code>true</code> if the message send successfully
	 * <code>false</code> if the message not send to number       
	 */
	public boolean send(String message, String number, Map<SMSNotifierKeyReplacer, String> keyReplacerMap);
	
}
