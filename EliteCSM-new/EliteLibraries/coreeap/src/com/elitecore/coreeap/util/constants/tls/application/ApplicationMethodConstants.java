package com.elitecore.coreeap.util.constants.tls.application;

import java.util.HashMap;
import java.util.Map;

import com.elitecore.coreeap.util.constants.fsm.IEnum;


public enum ApplicationMethodConstants implements IEnum{
	PAP(1),
	CHAP(2),
	EAP(3),
	MSCHAP(4),
	MSCHAPv2(5);
	public final int value;
	private static final Map<Integer,ApplicationMethodConstants> map;
	public static final ApplicationMethodConstants[] VALUES = values();
	static {
		map = new HashMap<Integer,ApplicationMethodConstants>(8);
		for (ApplicationMethodConstants type : VALUES) {
			map.put(type.value, type);
		}
	}	
	
	ApplicationMethodConstants(int value){
		this.value = value;
	}	
	public int getValue(){
		return this.value;
	}
	public static boolean isValid(int value){
		return map.containsKey(value);	
	}
	
	public static ApplicationMethodConstants get(int key){
		return map.get(key);
	}

}
