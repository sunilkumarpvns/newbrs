package com.elitecore.corenetvertex.constants;

import java.util.HashMap;
import java.util.Map;

public enum EventType {
	
	QUOTA_THRESHOLD_EVENT(1, "Quota Threshold"),
	BOD_START_EVENT(2, "BOD Start"),
	ACCOUNT_EXPIRY_EVENT(3, "Account Expiry"),
	ADDON_EXPIRY_EVENT(4, "Add-On Expiry"), 
	WIFI_OFFLOAD_EVENT(5,"WiFi OffLoad"),
	BOD_EXPIRY_EVENT(7, "BOD Expiry"),
	CONGESTION_IN_EVENT(8, "CongestionIn"),
	CONGESTION_OUT_EVENT(9, "CongestionOut"),
	USAGE_THRESHOLD_EVENT(10, "UsageThresholdEvent"),
	THRESHOLD_EVENT(11, "ThresholdEvent")
	;
	
	public final int eventId;
	public final String name;
	
	private EventType(int eventId, String name) {
		this.eventId = eventId;
		this.name = name;
	}
	
	private static final Map<Integer,EventType> map;
	
	static {
		map = new HashMap<Integer,EventType>(10,1);
		for (EventType eventType : values()) {
			map.put(eventType.eventId, eventType);
		}
	}
	
	public static EventType fromValue(int eventId) {
		return map.get(eventId);
	}
	
}
