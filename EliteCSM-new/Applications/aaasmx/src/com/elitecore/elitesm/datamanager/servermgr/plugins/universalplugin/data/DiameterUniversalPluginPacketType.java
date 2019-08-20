package com.elitecore.elitesm.datamanager.servermgr.plugins.universalplugin.data;

import java.util.HashMap;
import java.util.Map;

public enum DiameterUniversalPluginPacketType {

	RESPONSE(1,"Request"), 
	REQUEST(2,"Answer");	
	
	public String name;
	public int id;
	
	private static final Map<Integer,DiameterUniversalPluginPacketType> map;
	public static final DiameterUniversalPluginPacketType[] VALUES = values();
	
	static {
		map = new HashMap<Integer,DiameterUniversalPluginPacketType>();
		for (DiameterUniversalPluginPacketType type : VALUES) {
			map.put(type.id, type);
		}
	}
	
	private DiameterUniversalPluginPacketType(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public static DiameterUniversalPluginPacketType fromPacketTypeMethods(int id) {
		return map.get(id);
	}
}
