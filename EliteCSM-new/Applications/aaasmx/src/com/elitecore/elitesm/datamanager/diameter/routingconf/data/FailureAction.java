package com.elitecore.elitesm.datamanager.diameter.routingconf.data;

import java.util.HashMap;
import java.util.Map;

public enum FailureAction {

	DROP((short) 1, "Drop"), 
	FAILOVER((short) 2, "Failover"), 
	REDIRECT((short) 3, "Redirect"), 
	PASSTHROUGH((short) 4, "Passthrough"), 
	TRANSLATE((short) 5, "Translate"), 
	RECORD((short) 6, "Record");

	public final Short failureActionId;
	public final String failureActionName;
	
	private static final Map<Short, String> nameFromIdMap;
	private static final Map<String, Short> idFromNameMap;

	public static final FailureAction[] VALUES = values();

	static {
		
		nameFromIdMap = new HashMap<Short, String>();
		idFromNameMap = new HashMap<String, Short>();
		
		for (FailureAction failureActionValue : VALUES) {
			nameFromIdMap.put(failureActionValue.failureActionId, failureActionValue.failureActionName);
			idFromNameMap.put(failureActionValue.failureActionName, failureActionValue.failureActionId);
		}

	}

	FailureAction(Short failureActionId, String failureActionName) {
		this.failureActionId = failureActionId;
		this.failureActionName = failureActionName;
	}

	public static String nameFromId(Short failureActionId) {
		if (failureActionId != null) {
			if (nameFromIdMap.containsKey(failureActionId)) {
				return nameFromIdMap.get(failureActionId);
			}
		}
		return null;
	}

	public static Short idFromName(String failureActionName) {
		if (failureActionName != null) {
			if (idFromNameMap.containsKey(failureActionName)) {
				return idFromNameMap.get(failureActionName);
			}
		}
		return null;
	}

}
