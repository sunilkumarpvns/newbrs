package com.elitecore.corenetvertex.constants;

import java.util.HashMap;
import java.util.Map;

public enum MonetaryBalanceType {

	DEFAULT("Default"),
	PROMOTIONAL("Promotional");

	private String val;
	private static final Map<String,MonetaryBalanceType> objectMap;

	MonetaryBalanceType(String val){
		this.val = val;
	}
	static {
		objectMap = new HashMap<>();
		for ( MonetaryBalanceType type : values()){
			objectMap.put(type.name(), type);
		}
	}
	public static MonetaryBalanceType fromName(String name){
		return objectMap.get(name);
	}
	
	public String getVal() {
		return val;
	}
}
