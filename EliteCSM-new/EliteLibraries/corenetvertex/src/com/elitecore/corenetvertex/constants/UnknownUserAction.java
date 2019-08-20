package com.elitecore.corenetvertex.constants;

import java.util.ArrayList;
import java.util.List;

public enum UnknownUserAction {

	ALLOW_UNKNOWN_USER(1, "Allow Unknown User"),
	REJECT_UNKNOWN_USER(2, "Reject Unknown User"),
	DROP_REQUEST(3, "Drop Request"),		
	;

	private int val;
	private String name;

	UnknownUserAction(int val, String name) {
		this.val = val;
		this.name = name;
	}

	private static List<UnknownUserAction> objectList;
	static {
		objectList = new ArrayList<UnknownUserAction>();
		for(UnknownUserAction constant: values()){
			objectList.add(constant);
		}
	}
	
	public int getVal() {
		return val;
	}
	public String getName() {
		return name;
	}
	public static List<UnknownUserAction> getObjectList() {
		return objectList;
	}		
	
	public static UnknownUserAction fromValue(int val) {
		if(ALLOW_UNKNOWN_USER.val == val) {
			return ALLOW_UNKNOWN_USER;
		} else if (REJECT_UNKNOWN_USER.val == val) {
			return REJECT_UNKNOWN_USER;
		} else if (DROP_REQUEST.val == val) {
			return DROP_REQUEST;
		}
		return null;
	}

	public static String getDisplayLabelFromInstanceName(String instanceName){
		if(ALLOW_UNKNOWN_USER.name().equals(instanceName)) {
			return ALLOW_UNKNOWN_USER.name;
		} else if (REJECT_UNKNOWN_USER.name().equals(instanceName)) {
			return REJECT_UNKNOWN_USER.name;
		} else if (DROP_REQUEST.name().equals(instanceName)) {
			return DROP_REQUEST.name;
		}
		return null;
	}
}
