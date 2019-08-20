package com.elitecore.coreeap.util.constants.tls;

import java.util.HashMap;
import java.util.Map;


public enum TLSRecordConstants {
	ChangeCipherSpec(20,"Change Cipher Spec"),
	Alert(21,"Alert"),
	Handshake(22,"Handshake"),
	ApplicationData(23,"Application");
	public final int value;
	public final String name;
	private static final Map<Integer,TLSRecordConstants> map;
	public static final TLSRecordConstants[] VALUES = values();
	static {
		map = new HashMap<Integer,TLSRecordConstants>(6);
		for (TLSRecordConstants type : VALUES) {
			map.put(type.value, type);
		}
	}	
	
	TLSRecordConstants(int value,String name){
		this.value = value;
		this.name = name;
	}	
	public int getValue(){
		return this.value;
	}
	public static boolean isValid(int value){
		return map.containsKey(value);	
	}
	public static String getName(int value){
		return map.get(value).name;
	}
}
