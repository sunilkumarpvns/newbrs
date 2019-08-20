package com.elitecore.aaa.radius.util.converters;

import java.util.HashMap;
import java.util.Map;

public enum ZTEPrepaidConstants {
	
	NONE(0,"null"),
	RATING_GROUP(1,"RG"),
	SI(2,"SI"),
	DATA_VOLUME_UPLINK(3,"DVU"),
	DATA_VOLUME_DOWNLINK(4,"DVD"),
	RESULT_CODE(5,"RC"),
	LOCAL_SEQUENCE_NUMBER(8,"LSN"),
	TIME_OF_FIRST_USAGE(9,"TOFU"),
	TIME_OF_LAST_USAGE(12,"TOLU"),
	REPORT_REASON(12,"RR"),
	QOS_INFORMATION(12,"QOSI"),
	REPORT_TIME(12,"RT");
	
	public final int id;
	public final String key;
	private static final Map<Integer,ZTEPrepaidConstants> map;
	private static final Map<String,ZTEPrepaidConstants> keyMap;
	public static final ZTEPrepaidConstants[] VALUES = values();
	static {
		map = new HashMap<Integer,ZTEPrepaidConstants>(8);
		for (ZTEPrepaidConstants type : VALUES) {
			map.put(type.id, type);
		}
		keyMap = new HashMap<String,ZTEPrepaidConstants>(8);
		for (ZTEPrepaidConstants type : VALUES) {
			keyMap.put(type.key, type);
		}
	}	

	ZTEPrepaidConstants(int id,String key){
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
		ZTEPrepaidConstants record = map.get(id);
		if(record != null)
			return record.key;
		return null;
	}
	public static int getId(String key){
		ZTEPrepaidConstants record = keyMap.get(key);
		if(record != null)
			return record.id;
		return NONE.id;
	}

}