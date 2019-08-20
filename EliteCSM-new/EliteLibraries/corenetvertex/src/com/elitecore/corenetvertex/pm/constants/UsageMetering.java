package com.elitecore.corenetvertex.pm.constants;

import java.util.HashMap;
import java.util.Map;

public enum UsageMetering {

	TIME_QUOTA("UMT01","TIME_QUOTA"),
	VOLUME_QUOTA("UMT02","VOLUME_QUOTA"),
	TIME_VOLUME_QUOTA("UMT03","TIME_VOLUME_QUOTA"),
	DISABLE_QUOTA("UMT04","DISABLE_QUOTA");
	
	private final String id;
	
	private final String val;
	
	private static final Map<String,UsageMetering> map;
	
	static {
		map = new HashMap<String,UsageMetering>();
		for (UsageMetering usageMetering : values()) {
			map.put(usageMetering.id, usageMetering);
		}
	}
	
	private UsageMetering(String id, String val) {
		this.id = id;
		this.val = val;
	}
	
	public String getVal() {
		return this.val;
	}
	
	public static UsageMetering fromValue(String id) {
	    return map.get(id);
	}

	public String getId() {
		return id;
	}
	
}
