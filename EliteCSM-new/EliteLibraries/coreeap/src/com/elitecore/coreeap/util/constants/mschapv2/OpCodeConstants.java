package com.elitecore.coreeap.util.constants.mschapv2;

import java.util.HashMap;
import java.util.Map;

import com.elitecore.coreeap.util.constants.fsm.IEnum;

public enum OpCodeConstants implements IEnum{
	CHALLENGE(1, "Challenge"),
	RESPONSE(2, "Response"),
	SUCCESS(3, "Success"),
	FAILURE(4, "Failure"),
	CHANGE_PASSWORD(7, "Change-Password");
	
	public final int opCode;
	public final String name;
	private static final Map<Integer,OpCodeConstants> map;
	private static final Map<String,OpCodeConstants> nameMap;
	public static final OpCodeConstants[] VALUES = values();
	static {
		map = new HashMap<Integer,OpCodeConstants>();
		for (OpCodeConstants type : VALUES) {
			map.put(type.opCode, type);
		}
		nameMap = new HashMap<String,OpCodeConstants>();
		for (OpCodeConstants type : VALUES) {
			nameMap.put(type.name, type);
		}
	}	

	OpCodeConstants(int id,String name){
		this.opCode= id;
		this.name = name;
	}
	public int getOpCode(){
		return this.opCode;
	}
	public static boolean isValid(int value){
		return map.containsKey(value);	
	}
	public static String getName(int value){
		return map.get(value).name;
	}
	public static int getOpCode(String name){
		return nameMap.get(name).opCode;
	}
}
