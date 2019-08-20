package com.elitecore.corenetvertex.constants;

import java.util.HashMap;
import java.util.Map;

public enum NotificationType {

	SYSTEM("system"),
	CUSTOM("custom");
	
	public final String type;
	
	private NotificationType(String val) {
		this.type = val;
	}
	
	private static final Map<String,NotificationType> map;
	
	public static final NotificationType[] VALUES = values();
	
	static {
		map = new HashMap<String,NotificationType>(2,1);
		for (NotificationType notificationType : VALUES) {
			map.put(notificationType.type, notificationType);
		}
	}
	
	public static NotificationType fromValue(String notificationType) {
	    return map.get(notificationType);
	}
	
}
