package com.elitecore.elitesm.datamanager.servermgr.plugins.universalplugin.data;

import java.util.HashMap;
import java.util.Map;

public enum ParameterUsage {

	CHECK_ITEM("C","Check Item"), 
	DYNAMICAL_ASSIGN_ITEM("A","Dynamical Assign Item"), 
	FILTER_ITEM("F","Filter Item"), 
	REJECT_ITEM("J","Reject Item"), 
	REPLY_ITEM("R","Reply Item"),
	UPDATE_ITEM("U","Update Item"),
	VALUE_REPLACE_ITEM("V","Value Replace Item");
	
	public String name;
	public final String parameterUsage;

	
	private static final Map<String,ParameterUsage> map;
	public static final ParameterUsage[] VALUES = values();
	
	static {
		map = new HashMap<String,ParameterUsage>();
		for (ParameterUsage type : VALUES) {
			map.put(type.name, type);
		}
	}
	
	private ParameterUsage(String name, String parametrUsageName) {
		this.name = name;
		this.parameterUsage =parametrUsageName;
	}
	
	public static ParameterUsage fromPacketTypeMethods(String name) {
		return map.get(name);
	}
}
