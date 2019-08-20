package com.elitecore.coreeap.util.constants.tls.alert;

import java.util.HashMap;
import java.util.Map;

import com.elitecore.coreeap.util.constants.fsm.IEnum;

public enum TLSAlertLevelConstants implements IEnum {
	WARNING(1,"warning"),
	FATAL(2,"fatal"), 
	RESERVED(255,"reserved");
	public final int typeId;
	public final String name;
	private static final Map<Integer,TLSAlertLevelConstants> map;
	private static final Map<String,TLSAlertLevelConstants> nameMap;
	public static final TLSAlertLevelConstants[] VALUES = values();
	static {
		map = new HashMap<Integer,TLSAlertLevelConstants>(4);
		for (TLSAlertLevelConstants type : VALUES) {
			map.put(type.typeId, type);
		}
		nameMap = new HashMap<String,TLSAlertLevelConstants>(4);
		for (TLSAlertLevelConstants type : VALUES) {
			nameMap.put(type.name, type);
		}
	}	

	TLSAlertLevelConstants(int id,String name){
		this.typeId= id;
		this.name = name;
	}
	public int getTypeId(){
		return this.typeId;
	}
	public static boolean isValid(int value){
		return map.containsKey(value);	
	}
	public static String getName(int value){
		return map.get(value).name;
	}
	public static int getTypeId(String name){
		return nameMap.get(name).typeId;
	}

}
