package com.elitecore.diameterapi.core.common.util.constant;

import java.util.HashMap;
import java.util.Map;


public enum TranslatorId {
	
	DIAMETER("DIAMETER"),
	RADIUS("RADIUS"),
	CRESTAL("CRESTAL")
	;
	
	
	public final String interpretorId;
	
	public static final TranslatorId[] VALUES = values();
	
	private static final Map<String, TranslatorId> map;
	static {
		map = new HashMap<String,TranslatorId>();
		for (TranslatorId type : VALUES) {
			map.put(type.interpretorId, type);
		}
	}
	
	TranslatorId(String id){
		interpretorId = id;
	}
	
	public String getId(){
		return this.interpretorId;
	}
	
}


/*

public enum RoutingActions {
	LOCAL(0,"LOCAL"),
	RELAY(1,"RELAY"),
	PROXY(2,"PROXY"),
	REDIRECT(3,"REDIRECT");
	
	public final int routingAction;
	public final String routingActionStr;
	private static final Map<Integer,RoutingActions> map;
	
	public static final RoutingActions[] VALUES = values();
	
	static {
		map = new HashMap<Integer,RoutingActions>();
		for (RoutingActions type : VALUES) {
			map.put(type.routingAction, type);
		}
		
		
	}
	RoutingActions(int routingAction,String routingActionStr) {
		this.routingAction = routingAction;
		this.routingActionStr = routingActionStr;
	}

	public int getRoutingAction() {
		return routingAction;
	}

	public static RoutingActions fromRoutingAction(int routingAction) {
		return map.get(routingAction);
	}
	
	public static boolean isValid(int value) {
		return map.containsKey(value);
	}
	
	public static String getActionString(int action) {
		RoutingActions routingAction = map.get(action);  
		if(routingAction != null){
			return routingAction.routingActionStr;
		}
		
		return "INVALID ACTION";
	}
}

*/