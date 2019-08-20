package com.elitecore.corenetvertex.pkg.rnc;

import java.util.HashMap;
import java.util.Map;

public enum QuotaUsageType {

	VOLUME("Volume"),
	TIME("Time"),
	HYBRID("Hybrid");
	

	private final String value;
	
	private static final Map<String,QuotaUsageType> map;
	
	static {
		map = new HashMap();
		for (QuotaUsageType quotaUsageType : values()) {
			map.put(quotaUsageType.name(), quotaUsageType);
		}
	}
	
	QuotaUsageType(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return this.value;
	}
	
	public static QuotaUsageType fromQuotaUsageType(String id) {
	    return map.get(id);
	}
}

