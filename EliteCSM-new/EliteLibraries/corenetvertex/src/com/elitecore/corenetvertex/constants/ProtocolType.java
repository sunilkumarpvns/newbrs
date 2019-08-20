package com.elitecore.corenetvertex.constants;

import java.util.HashMap;
import java.util.Map;

public enum ProtocolType {
	
	DIAMETER("DIAMETER"),
	RADIUS("RADIUS");
	
	
	private final String protocolType;
	
	private ProtocolType(String protocolType){
		this.protocolType=protocolType;
	}
	
	private static final Map<String, ProtocolType> objectMap;
	
	static {
		objectMap = new HashMap<String,ProtocolType>(2,1);
		for (ProtocolType type : values()){
			objectMap.put(type.protocolType, type);
		}
	}

	public static ProtocolType fromValue(String protocolType){
		return objectMap.get(protocolType);
	}
	

	public String getProtocolType(){
		return protocolType;
	}
}
