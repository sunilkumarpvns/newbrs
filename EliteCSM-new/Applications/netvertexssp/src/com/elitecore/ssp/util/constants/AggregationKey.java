package com.elitecore.ssp.util.constants;

import java.util.HashMap;
import java.util.Map;

public enum AggregationKey {
	
	SESSION_USAGE("SESSION_USAGE", 1),
	DAILY("DAILY", 2),
	WEEKLY("WEEKLY", 3),
	MONTHLY("MONTHLY", 4),
	BILLING_CYCLE("BILLING_CYCLE", 5),
	;
	
	public final String val;
	private int rank;
	
	private static final Map<String,AggregationKey> objectMap;
	private static final AggregationKey[] types = values();
	private AggregationKey(String val, int rank) {
		this.val = val;
		this.rank = rank;
	}

	public String getVal() {
		return val;
	}
	
	public int getRank() {
		return rank;
	}

	static {
		objectMap = new HashMap<String, AggregationKey>();
		for ( AggregationKey type : types){
			objectMap.put(type.val, type);
		}
	}

	public static AggregationKey fromString(String aggregationKey){
		if(aggregationKey == null){
			return null;
		}
		return objectMap.get(aggregationKey.toUpperCase());
	}

}
