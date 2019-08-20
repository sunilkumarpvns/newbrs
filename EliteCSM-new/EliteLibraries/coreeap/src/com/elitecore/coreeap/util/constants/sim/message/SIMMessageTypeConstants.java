package com.elitecore.coreeap.util.constants.sim.message;

import java.util.HashMap;
import java.util.Map;

import com.elitecore.coreeap.util.constants.fsm.IEnum;

public enum SIMMessageTypeConstants implements IEnum {
	AKA_CHALLENGE(1,"AKA-Challenge"),
	AKA_AUTHENTICATION_REJECT(2,"AKA-Authentication-Reject"),
	AKA_SYNCHRONIZATION_FAILURE(4,"AKA-Synchronization-Failure"),
	AKA_IDENTITY(5,"AKA-Identity"),
	SIM_START(10,"SIM-Start"),
	SIM_CHALLENGE(11,"SIM-Challenge"),
	NOTIFICATION(12,"Notification"),
	RE_AUTHENTICATION(13,"Re-Authentication"),
	CLIENT_ERROR(14,"Client error");
	public final int Id;
	public final String name;

	private static final Map<Integer,SIMMessageTypeConstants> map;
	private static final Map<String,SIMMessageTypeConstants> nameMap;
	public static final SIMMessageTypeConstants[] VALUES = values();
	static {
		map = new HashMap<Integer,SIMMessageTypeConstants>();
		for (SIMMessageTypeConstants type : VALUES) {
			map.put(type.Id, type);
		}
		nameMap = new HashMap<String,SIMMessageTypeConstants>();
		for (SIMMessageTypeConstants type : VALUES) {
			nameMap.put(type.name, type);
		}
	}
	SIMMessageTypeConstants(int id,String name){
		this.Id= id;
		this.name = name;
	}
	public static SIMMessageTypeConstants get(int key){
		return map.get(key);
	}
	public int getId(){
		return this.Id;
	}
	public static boolean isValid(int value){
		return map.containsKey(value);	
	}
	public static String getName(int value){
		return map.get(value).name;
	}
	public static int getId(String name){
		return nameMap.get(name).Id;
	}
}
