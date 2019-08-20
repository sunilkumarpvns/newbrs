package com.elitecore.elitesm.datamanager.servermgr.copypacket.data;

import java.util.HashMap;
import java.util.Map;



public enum OperationType {
	ADD("ADD"),
	REMOVE("REMOVE"),
	UPDATE("UPDATE"),
	MOVE("MOVE"),
	UNKNOWN("UNKNOWN");

	public  final String operationName;
	private static final Map<String,OperationType> operationMap; 
	private OperationType(String value){
		this.operationName = value;
	}
	
	static{
		operationMap = new HashMap<String,OperationType>(4,1);
		for(OperationType operation : values()){
			operationMap.put(operation.operationName, operation);
		}
		
	}
	public static OperationType fromValue(String operationName){
		OperationType operation= operationMap.get(operationName);
		return operation != null ? operation : UNKNOWN;
	}
}
