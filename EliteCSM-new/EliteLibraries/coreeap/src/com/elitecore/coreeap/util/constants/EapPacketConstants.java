package com.elitecore.coreeap.util.constants;

import java.util.HashMap;
import java.util.Map;

import com.elitecore.coreeap.util.constants.fsm.IEnum;

public enum EapPacketConstants implements IEnum{
	REQUEST(1,"Request"),
	RESPONSE(2,"Response"),
	SUCCESS(3,"Success"),
	FAILURE(4,"Failure");
	
	public final int packetId;
	public final String name;
	private static final Map<Integer,EapPacketConstants> map;
	public static final EapPacketConstants[] VALUES = values();
	
	static {
		map = new HashMap<Integer,EapPacketConstants>(6);
		for (EapPacketConstants type : VALUES) {
			map.put(type.packetId, type);
		}
	}	
	
	EapPacketConstants(int id,String name) {
		this.packetId = id;
		this.name = name;
	}
	public int getPacketId(){
		return this.packetId;
	}
	public static boolean isValid(int value){
		return map.containsKey(value);	
	}
	public static String getName(int value){
		return map.get(value).name;
	}
}
