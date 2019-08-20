package com.elitecore.elitesm.util.constants;

import java.util.HashMap;
import java.util.Map;

public enum PluginFlow {
	RAD_AUTH(1,"Rad Auth"),
	RAD_ACCT(2,"Rad Acct"),
	RAD_DYNAAUTH(3,"Rad Dynauth Service"),
	ALL(4,"All");
	
	public final int pluginFlowType;
	public final String pluginFlowStr;
	private static final Map<Integer,PluginFlow> map;
	
	public static final PluginFlow[] VALUES = values();
	
	static {
		map = new HashMap<Integer,PluginFlow>();
		for (PluginFlow type : VALUES) {
			map.put(type.pluginFlowType, type);
		}
		
		
	}
	PluginFlow(int routingAction,String routingActionStr) {
		this.pluginFlowType = routingAction;
		this.pluginFlowStr = routingActionStr;
	}
	
	public static boolean isValid(int value) {
		return map.containsKey(value);
	}
	
	public static String getActionString(int action) {
		PluginFlow routingAction = map.get(action);  
		if(routingAction != null){
			return routingAction.pluginFlowStr;
		}
		
		return "INVALID ACTION";
	}
}
