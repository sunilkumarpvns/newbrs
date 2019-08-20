package com.elitecore.corenetvertex.constants;

import java.util.HashMap;
import java.util.Map;

public enum MeteringType {
	
	VOLUME_TOTAL("Volume(Total)"),
	VOLUME_DOWNLOAD("Volume(Download)"),
	VOLUME_UPLOAD("Volume(Upload)"),
	TIME("Time"),
	;
	
	public final String val;
	
	private static final Map<String,MeteringType> objectMap;
	private static final MeteringType[] types = values();
	private MeteringType(String val) {
		this.val = val;
	}

	public String getVal() {
		return val;
	}
	
	static {
		objectMap = new HashMap<String, MeteringType>();
		for ( MeteringType type : types){
			objectMap.put(type.val, type);
		}
	}

	public static MeteringType fromString(String meteringType){
		if(meteringType == null){
			return null;
		}
		return objectMap.get(meteringType.toUpperCase());
	}

}
