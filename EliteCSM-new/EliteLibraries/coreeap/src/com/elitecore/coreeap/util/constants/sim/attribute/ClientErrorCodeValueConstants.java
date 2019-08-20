package com.elitecore.coreeap.util.constants.sim.attribute;

import java.util.HashMap;
import java.util.Map;

import com.elitecore.coreeap.util.constants.fsm.IEnum;

public enum ClientErrorCodeValueConstants implements IEnum {
	UnableToProcess(0,"Unable to process packet."),
	UnsupportedVersion(1,"Unsupported version"),
	InsufficientChallenges(2,"Insufficient number of challenges."),
	OldRand(3,"RANDs are not fresh");
	
	public final int code;
	public final String message;

	private static final Map<Integer,ClientErrorCodeValueConstants> map;	
	public static final ClientErrorCodeValueConstants[] VALUES = values();
	static {
		map = new HashMap<Integer,ClientErrorCodeValueConstants>();
		for (ClientErrorCodeValueConstants type : VALUES) {
			map.put(type.code, type);
		}
	}
	ClientErrorCodeValueConstants(int code,String message){
		this.code= code;
		this.message = message;
	}
	public int getcode(){
		return this.code;
	}
	public static boolean isValcode(int value){
		return map.containsKey(value);	
	}
	public static String getmessage(int value){
		return map.get(value).message;
	}
}
