package com.elitecore.core.event;

public class ServerEvent extends SFEvent {

	private int intServerEventId;
	private String strMsg;
	
	public ServerEvent(int intServerEventId,String strMsg) {
		this.intServerEventId = intServerEventId;
		this.strMsg = strMsg;
	}
	
	public int getServerEventId() {
		return intServerEventId;
	}
	
	public String getServerEventMessage() {
		return strMsg;
	}
}
