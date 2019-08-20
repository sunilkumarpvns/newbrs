package com.elitecore.coreeap.util.constants.sim.attribute;

import java.util.HashMap;
import java.util.Map;

import com.elitecore.coreeap.util.constants.fsm.IEnum;

public enum NotificationValueConstants implements IEnum {
	FailureAfterAuthentication(0,"General failure after authentication."),
	Failure(16384,"General failure"),
	Success(32768,"Success. User has been successfully authenticated."),
	Denied(1026,"User has been temporarily denied access to the requested service."),
	NotScribed(1031,"User has not subscribed to the requested service");
	
	public final int code;
	public final String message;

	private static final Map<Integer,NotificationValueConstants> map;	
	public static final NotificationValueConstants[] VALUES = values();
	static {
		map = new HashMap<Integer,NotificationValueConstants>();
		for (NotificationValueConstants type : VALUES) {
			map.put(type.code, type);
		}
	}
	NotificationValueConstants(int code,String message){
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
