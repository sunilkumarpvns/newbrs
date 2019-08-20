package com.elitecore.corenetvertex.constants;

import java.util.HashMap;
import java.util.Map;


public enum ConversionType {

	GATEWAY_TO_PCC("GATEWAY TO PCC"),
	PCC_TO_GATEWAY("PCC TO GATEWAY");
	
	
	private final String conversionType;
	
	private ConversionType(String conversionType){
		this.conversionType=conversionType;
	}
	
	private static final Map<String, ConversionType> objectMap;
	
	static {
		objectMap = new HashMap<String,ConversionType>(2,1);
		for (ConversionType type : values()){
			objectMap.put(type.conversionType, type);
		}
	}

	public static ConversionType fromValue(String conversionType){
		return objectMap.get(conversionType);
	}

	public String getConversionType(){
		return conversionType;
	}
	
}
