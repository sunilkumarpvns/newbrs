package com.elitecore.aaa.radius.util.converters;

import java.util.HashMap;
import java.util.Map;

public enum ThreeGPP2PrepaidConstants {

	NONE(0, "null"), 
	QUOTA_IDENTIFIER(1, "QI"), 
	VOLUME_QUOTA(2, "VQ"), 
	VOLUME_QUOTA_OVERFLOW(3, "VQO"), 
	VOLUME_THRESHOLD(4, "VT"), 
	VOLUME_THRESHOLD_OVERFLOW(5,"VTO"), 
	DURATION_QUOTA(6, "DQ"), 
	DURATION_THRESHOLD(7, "DT"), 
	UPDATE_REASON(8, "UR"), 
	PREPAID_SERVER(9, "PS");

	public final int id;
	public final String key;
	private static final Map<Integer, ThreeGPP2PrepaidConstants> map;
	private static final Map<String, ThreeGPP2PrepaidConstants> keyMap;
	public static final ThreeGPP2PrepaidConstants[] VALUES = values();
	static {
		map = new HashMap<Integer, ThreeGPP2PrepaidConstants>(8);
		for (ThreeGPP2PrepaidConstants type : VALUES) {
			map.put(type.id, type);
		}
		keyMap = new HashMap<String, ThreeGPP2PrepaidConstants>(8);
		for (ThreeGPP2PrepaidConstants type : VALUES) {
			keyMap.put(type.key, type);
		}
	}

	ThreeGPP2PrepaidConstants(int id, String key) {
		this.id = id;
		this.key = key;
	}

	public int getTypeId() {
		return this.id;
	}

	public static boolean isValid(int value) {
		return map.containsKey(value);
	}

	public static String getKey(int id) {
		ThreeGPP2PrepaidConstants record = map.get(id);
		if (record != null)
			return record.key;
		return null;
	}

	public static int getId(String key) {
		ThreeGPP2PrepaidConstants record = keyMap.get(key);
		if (record != null)
			return record.id;
		return NONE.id;
	}
}
