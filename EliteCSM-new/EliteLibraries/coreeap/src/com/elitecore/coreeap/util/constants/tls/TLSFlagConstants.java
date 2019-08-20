package com.elitecore.coreeap.util.constants.tls;

import java.util.HashMap;
import java.util.Map;

public enum TLSFlagConstants {
	S_FLAG(32,"Start"),
	LM_FLAG(192,"Length and More Fragment"),	
	L_FLAG (128,"Length"),
	M_FLAG(64,"More Fragment"),
	NULL_FLAG(0,"Zero");
	public final int value;
	public final String name;
	private static final Map<Integer,TLSFlagConstants> map;
	public static final TLSFlagConstants[] VALUES = values();
	
	static {
	map = new HashMap<Integer,TLSFlagConstants>(8);
		for (TLSFlagConstants type : VALUES) {
			map.put(type.value, type);
		}
	}	

	TLSFlagConstants(int value,String name){
		this.value = value;
		this.name = name;
	}
	public static boolean isFragmented(int value){
		return(value == LM_FLAG.value || value == M_FLAG.value);
	}
	public static String getName(int value){
		return map.get(value).name;
	}
}
