package com.elitecore.corenetvertex.constants;

import java.util.HashMap;

public enum AddOnPackageType {

	QUOTA_CONTROL("QuotaControl"),
	ACCESS_CONTROL("AccessControl"),
	ACCESS_AND_QUOTACONTROL("AccessAndQuotaControl"),
	;
	
	private String val;
	private static final HashMap<String,AddOnPackageType> map;
	static {
		map = new HashMap<String,AddOnPackageType>();
		for (AddOnPackageType addOnPackageType : values()) {
			map.put(addOnPackageType.val, addOnPackageType);
		}
	}
	
	private AddOnPackageType(String val) {
		this.val = val;
	}
	public String getVal() {
		return this.val;
	}
	
	public static AddOnPackageType fromValue(String val) {
	    return map.get(val);
	}
}