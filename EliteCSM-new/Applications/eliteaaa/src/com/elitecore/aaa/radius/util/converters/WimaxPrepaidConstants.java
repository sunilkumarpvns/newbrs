package com.elitecore.aaa.radius.util.converters;

import java.util.HashMap;
import java.util.Map;

public enum WimaxPrepaidConstants {
	NONE(0,"null"),
	QUOTA_IDENTIFIER(1,"QI"),
	VOLUME_QUOTA(2,"VQ"),
	VOLUME_THRESHOLD(3,"VT"),
	DURATION_QUOTA(4,"DQ"),
	DURATION_THRESHOLD(5,"DT"),
	UPDATE_REASON(8,"UR"),
	PREPAID_SERVER(9,"PS"),
	TERMINATION_ACTION(12,"TA");
	public final int id;
	public final String key;
	private static final Map<Integer,WimaxPrepaidConstants> map;
	private static final Map<String,WimaxPrepaidConstants> keyMap;
	public static final WimaxPrepaidConstants[] VALUES = values();
	static {
		map = new HashMap<Integer,WimaxPrepaidConstants>(8);
		for (WimaxPrepaidConstants type : VALUES) {
			map.put(type.id, type);
		}
		keyMap = new HashMap<String,WimaxPrepaidConstants>(8);
		for (WimaxPrepaidConstants type : VALUES) {
			keyMap.put(type.key, type);
		}
	}	

	WimaxPrepaidConstants(int id,String key){
		this.id= id;
		this.key = key;
	}
	public int getTypeId(){
		return this.id;
	}
	public static boolean isValid(int value){
		return map.containsKey(value);	
	}
	public static String getKey(int id){
		WimaxPrepaidConstants record = map.get(id);
		if(record != null)
			return record.key;
		return null;
	}
	public static int getId(String key){
		WimaxPrepaidConstants record = keyMap.get(key);
		if(record != null)
			return record.id;
		return NONE.id;
	}
}