package com.elitecore.netvertexsm.datamanager.core.util;

public class MessageData {
	
	public static final String WARN="WARN";
	public static final String INFO="INFO";
	public static final String ERROR="ERROR";
	
	private String messageType;
	private String message;

	public MessageData(String messageType,String message){
		this.messageType = messageType;
		this.message = message;
	}
	
	public String getMessageType() {
		return messageType;
	}
	public String getMessage() {
		return message;
	}
	
}
