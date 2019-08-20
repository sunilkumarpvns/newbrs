package com.elitecore.nvsmx.policydesigner.controller.session;

/**
 * This enum is only used for session search criteria attribute
 * @author Dhyani.Raval
 *
 */
public enum SessionSearchAttributes {
	
	SUBSCIBER_IDENTITY("Subscriber Identity"),
	ALTERNATE_IDENTITY("Alternate ID"),
	SESSION_IP("Session IP"),
	CORE_SESSION_ID("Core Session ID");
	
	private String value;
	 
	SessionSearchAttributes(String value){
		this.value = value;
	}

	public String getValue() {
		return value;
	}
	
}
