package com.elitecore.corenetvertex.constants;


public enum QoSProfileAction {
	
	ACCEPT("Accept",0),
	REJECT("Reject",1);
	
	private final int actionEnumId;
	private final String actionName;
	
	private QoSProfileAction(String actionName,int actionEnumId){
		this.actionName=actionName;
		this.actionEnumId=actionEnumId;
	}
	
	public static QoSProfileAction fromValue(int actionEnumId){
		if (ACCEPT.actionEnumId == actionEnumId) {
			return ACCEPT;
		} else if (REJECT.actionEnumId == actionEnumId) {
			return REJECT;
		}
		return null;
	}
	
	public int getId(){
		return actionEnumId;
	}
	
	public String getName(){
		return actionName;
	}

}
