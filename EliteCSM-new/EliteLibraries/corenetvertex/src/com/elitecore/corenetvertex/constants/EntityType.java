package com.elitecore.corenetvertex.constants;

import com.elitecore.commons.base.Strings;

import java.util.HashMap;
import java.util.Map;

public enum EntityType {

	OFFER("Product Offer"),
	RNC("RnC Package"),
	DATA("Data Package"),
	IMS("IMS Package"),
	EMERGENCY("Emergency Package"),
	PROMOTIONAL("Promotional Package"),
	TOPUP("Data Top-Up"),
	MONETARYRECHARGEPLAN("Monetary Recharge Plan"),
	BOD("BoD Package");

	private String value;
	private static Map<String,EntityType> entityTypeMap;

	static {
		entityTypeMap = new HashMap<>();
		for (EntityType entityType : values()) {
			entityTypeMap.put(entityType.name(),entityType);

		}
	}

	EntityType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static EntityType fromVal(String value){
		return entityTypeMap.get(value);
	}

	public static EntityType fromName(String name){
		if(Strings.isNullOrBlank(name) == true ){
			return null;
		}
		return entityTypeMap.get(name);
	}
}
	
