package com.elitecore.elitesm.datamanager.servermgr.plugins.universalplugin.data;

import java.util.HashMap;
import java.util.Map;

public enum DiameterUniversalPluginAction {

	ACCEPT(1,"none"), 
	REJECT(2,"Stop");
	
	public String name;
	public int id;
	
	private static final Map<Integer,DiameterUniversalPluginAction> map;
	public static final DiameterUniversalPluginAction[] VALUES = values();
	
	static {
		map = new HashMap<Integer,DiameterUniversalPluginAction>();
		for (DiameterUniversalPluginAction type : VALUES) {
			map.put(type.id, type);
		}
	}
	
	private DiameterUniversalPluginAction(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public static DiameterUniversalPluginAction fromPluginActionMethods(int id) {
		return map.get(id);
	}
}
