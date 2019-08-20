package com.elitecore.elitesm.datamanager.servermgr.plugins.universalplugin.data;

import java.util.HashMap;
import java.util.Map;

public enum PacketType {

	ACCOUNTING_RESPONSE(4,"Accounting Response"), 
	ACCOUNTING_REQUEST(5,"Accounting Request"), 
	ACCESS_REQUEST(1,"Access Request"), 
	ACCESS_RESPONSE(2,"Access Response"), 
	DEFAULT(0,"Default");
	
	public String name;
	public int id;
	
	private static final Map<Integer,PacketType> map;
	public static final PacketType[] VALUES = values();
	
	static {
		map = new HashMap<Integer,PacketType>();
		for (PacketType type : VALUES) {
			map.put(type.id, type);
		}
	}
	
	private PacketType(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public static PacketType fromPacketTypeMethods(int id) {
		return map.get(id);
	}
}
