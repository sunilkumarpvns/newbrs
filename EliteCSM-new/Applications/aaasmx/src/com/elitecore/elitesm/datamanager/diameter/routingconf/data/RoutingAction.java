package com.elitecore.elitesm.datamanager.diameter.routingconf.data;

public enum RoutingAction {

	LOCAL(1L,"Local"),
	RELAY(2L,"Relay"),
	PROXY(3L,"Proxy"),
	REDIRECT(4L,"Redirect"),
	VIRTUAL(5L,"Virtual");
	
	public final Long routingActionId;
	public final String routingActionName;
	
	public static final RoutingAction[] VALUES = values();
	
	RoutingAction(Long routingActionId, String routingActionName) {
		this.routingActionId = routingActionId;
		this.routingActionName = routingActionName;
	}
	
	public static String nameFromId(Long id) {
		if (id != null) {
			if (LOCAL.routingActionId == id) {
				return LOCAL.routingActionName;
			} else if (RELAY.routingActionId == id) {
				return RELAY.routingActionName;
			} else if (PROXY.routingActionId == id) {
				return PROXY.routingActionName;
			} else if (REDIRECT.routingActionId == id) {
				return REDIRECT.routingActionName;
			} else if (VIRTUAL.routingActionId == id) {
				return VIRTUAL.routingActionName;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
	
	public static Long idFromName(String name) {
		if (name != null) {
			if (LOCAL.routingActionName.equalsIgnoreCase(name)) {
				return LOCAL.routingActionId;
			} else if (RELAY.routingActionName.equalsIgnoreCase(name)) {
				return RELAY.routingActionId;
			} else if (PROXY.routingActionName.equalsIgnoreCase(name)) {
				return PROXY.routingActionId;
			} else if (REDIRECT.routingActionName.equalsIgnoreCase(name)) {
				return REDIRECT.routingActionId;
			} else if (VIRTUAL.routingActionName.equalsIgnoreCase(name)) {
				return VIRTUAL.routingActionId;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
	
}
