package com.elitecore.elitesm.datamanager.servermgr.plugins.universalplugin.data;

import java.util.HashMap;
import java.util.Map;

public enum PluginAction {

	ACCEPT(1,"Accept"), 
	REJECT(2,"Reject"), 
	DROP(3,"Drop"), 
	NONE(4,"none"), 
	STOP(5,"Stop");
	
	public String name;
	public int id;
	
	private static final Map<Integer,PluginAction> map;
	public static final PluginAction[] VALUES = values();
	
	static {
		map = new HashMap<Integer,PluginAction>();
		for (PluginAction type : VALUES) {
			map.put(type.id, type);
		}
	}
	
	private PluginAction(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public static PluginAction fromPluginActionMethods(int id) {
		return map.get(id);
	}
}
