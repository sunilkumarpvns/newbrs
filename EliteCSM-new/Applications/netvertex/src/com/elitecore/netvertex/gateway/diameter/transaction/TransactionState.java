package com.elitecore.netvertex.gateway.diameter.transaction;

public enum TransactionState {
	
	// General Transaction States
	IDLE("IDLE"),
	COMPLETE("COMPLETE"),
	
	// New-Service Transaction States	
	WAIT_FOR_AUTH_RES("WAIT_FOR_AUTH_RES"),	
	WAIT_RULE_INSTALL_ACK("WAIT_RULE_INSTALL_ACK"),
	
	//Stop-Service  Transaction States 
	WAIT_STOP_SERVICE_ACK("WAIT_STOP_SERVICE_ACK"),
	WAIT_RULE_REMOVE_ACK("WAIT_RULE_REMOVE_ACK"),
	
	//Stop-Session  Transaction States 
	WAIT_FOR_ASA("WAIT_FOR_ASA"),
	
	//Session-Stop
	WAIT_FOR_RAA("WAIT_FOR_RAA");
	
	private final String state;
	
	private TransactionState(String val) {
		state = val;
	}
	
	public String getState(){
		return state;
	}

}
