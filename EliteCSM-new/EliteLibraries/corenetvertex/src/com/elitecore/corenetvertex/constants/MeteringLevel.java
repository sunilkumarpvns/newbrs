package com.elitecore.corenetvertex.constants;

import java.util.HashMap;
import java.util.Map;

public enum MeteringLevel {
	
	SERVICE_LEVEL("SERVICE"),      
	PCCRULE_LEVEL("PCCRULE"),	     
	PACKAGE_LEVEL("PACKAGE"),	     
	SUBSCRIBER_LEVEL("SUBSCRIBER"),
			
	;

	public final String level;
	
	private static final Map<String,MeteringLevel> objectMap;
	private static final MeteringLevel[] types = values();
	private MeteringLevel(String val) {
		this.level = val;
	}

	public String getVal() {
		return level;
	}

	static {
		objectMap = new HashMap<String, MeteringLevel>();
		for (MeteringLevel type : types) {
			objectMap.put(type.level, type);
		}
	}

	public static MeteringLevel fromString(String meteringLevel){
		if(meteringLevel == null){
			return null;
		}
		return objectMap.get(meteringLevel.toUpperCase());
	}
}
