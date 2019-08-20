package com.elitecore.core.event;

import java.util.HashMap;
import java.util.Map;

public class ServiceEvent extends SFEvent {

	private int intServiceEventId;
	private String strMsg;
	private Map<String, Object> params;
	
	
	public ServiceEvent(int intServiceEventId, String strMsg) {
		this.intServiceEventId = intServiceEventId;
		this.strMsg = strMsg;
		params = new HashMap<String, Object>();
	}
	
	public int getServiceEventId() {
		return intServiceEventId;
	}
	
	public String getServiceEventMessage() {
		return strMsg;
	}
	
	public void setParam(String strKey, Object param){
		params.put(strKey, param);
	}
	
	public Object getParam(String strKey){
		return params.get(strKey);
	}
}
