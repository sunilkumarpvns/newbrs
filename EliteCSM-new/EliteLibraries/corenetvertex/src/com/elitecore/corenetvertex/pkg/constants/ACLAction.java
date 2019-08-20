package com.elitecore.corenetvertex.pkg.constants;

import java.util.HashMap;
import java.util.Map;


public enum ACLAction {
	
	CREATE("CREATE"),
	VIEW("VIEW"),
	DELETE("DELETE"),
	UPDATE("UPDATE"),
	UPDATE_BASIC_DETAIL("UPDATE BASIC DETAIL"),
	CREATE_SUBSCRIBER("CREATE SUBSCRIBER"),
	VIEW_SUBSCRIBER("VIEW SUBSCRIBER"),
	DELETE_SUBSCRIBER("DELETE SUBSCRIBER"),
	UPDATE_SUBSCRIBER("UPDATE SUBSCRIBER"),
	PURGE_SUBSCRIBER("PURGE SUBSCRIBER"),
	RESTORE_SUBSCRIBER("RESTORE SUBSCRIBER"),
	VIEW_DELETED_SUBSCRIBER("VIEW DELETED SUBSCRIBER"),
	SUBSCRIBE_ADDON("SUBSCRIB ADDON"),
	UNSUBSCRIBE_ADDON("UNSUBSCRIBE ADDON"),
	SUBSCRIBE_TOPUP("SUBSCRIBE TOPUP"),
	SUBSCRIBE_BOD("SUBSCRIBE BOD"),
	UNSUBSCRIBE_TOPUP("UNSUBSCRIBE TOPUP"),
	VIEW_SUBSCRIPTION("VIEW SUBSCRIPTION"),
	IMPORT("IMPORT"),
	EXPORT("EXPORT"),
	MANAGEORDER("MANAGEORDER"),

	REAUTH("REAUTH"),
	DISCONNECT("DISCONNECT"),
	SEARCH("SEARCH"),
	PUT("UPDATE"),
	POST("CREATE");

	private String val;
	private static Map<String, ACLAction> actionMap;
	
	static{
		actionMap = new HashMap<String, ACLAction>();
		for(ACLAction action:values()){
			actionMap.put(action.name(), action);
		}
	}
	
	ACLAction(String val){
		this.val = val;
	}
	
	public String getVal() {
		return val;
	}
	
	public static ACLAction fromName(String name){
		return actionMap.get(name);
	}

	public static String fromVal(String methodName){
		for (Map.Entry<String, ACLAction> e : actionMap.entrySet()) {
			if (methodName.contains(e.getKey())) {
				return e.getKey();
			}
		}
		return methodName;
	}


}
