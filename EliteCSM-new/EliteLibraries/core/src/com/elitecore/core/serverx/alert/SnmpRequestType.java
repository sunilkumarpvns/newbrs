package com.elitecore.core.serverx.alert;

import java.util.HashMap;
import java.util.Map;

public enum SnmpRequestType {
	
	TRAP("Trap" , 1),
	INFORM("Inform" , 2),
	;

	private String type;
	private int id;

	private SnmpRequestType(String name , int id){
		this.type = name;
		this.id = id;
	}

	private static final Map<Integer , SnmpRequestType> objectMap;

	static {
		objectMap = new HashMap<Integer,SnmpRequestType>();
		for (SnmpRequestType type : values()){
			objectMap.put(type.id, type);
		}
	}

	public static SnmpRequestType fromId(int id){
		return objectMap.get(id);
	}

	public String getType(){
		return type;
	}

	public int getId(){
		return id;
	}
}
