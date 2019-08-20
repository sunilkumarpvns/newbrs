package com.elitecore.core.serverx.snmp;

public enum MIBStates {

	OTHER(1),       
	RESET(2),       
	INITIALIZING(3),
	RUNNING(4);
	
	private int stateValue;
	
	MIBStates(int value){
		stateValue = value;
	}
	
	public int getStateValue() {
		return stateValue;
	}
}
