package com.elitecore.diameterapi.diameter.common.util.constant;

import java.util.HashMap;
import java.util.Map;


public enum DiameterFailureConstants {

	DROP(1,"Drop"),
	FAILOVER(2,"Failover"),
	REDIRECT(3,"Redirect"),
	PASSTHROUGH(4,"Passthrough"),
	TRANSLATE(5,"Translate"),
	RECORD(6,"Record");
	
	public final int failureAction;
	public final String failureActionStr;
	private static final Map<Integer,DiameterFailureConstants> map;
	
	public static final DiameterFailureConstants[] VALUES = values();
	
	static {
		map = new HashMap<Integer,DiameterFailureConstants>();
		for (DiameterFailureConstants type : VALUES) {
			map.put(type.failureAction, type);
		}
		
		
	}
	DiameterFailureConstants(int failureAction,String failureActionStr) {
		this.failureAction = failureAction;
		this.failureActionStr = failureActionStr;
	}


	public static DiameterFailureConstants fromDiameterFailureAction(int failureAction) {
		return map.get(failureAction);
	}
	
	public static boolean isValid(int value) {
		return map.containsKey(value);
	}
	
	public static String getFailureString(int action) {
		DiameterFailureConstants failureAction = map.get(action);  
		if(failureAction != null){
			return failureAction.failureActionStr;
		}
		
		return "INVALID ACTION";
	}
		
}