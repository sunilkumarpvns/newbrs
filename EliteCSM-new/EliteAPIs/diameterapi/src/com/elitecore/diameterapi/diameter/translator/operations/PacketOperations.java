package com.elitecore.diameterapi.diameter.translator.operations;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

@XmlEnum(value = String.class)
public enum PacketOperations {

	@XmlEnumValue(value = "ADD")
	ADD("ADD"),
	@XmlEnumValue(value = "UPDATE")
	UPDATE("UPDATE"),
	@XmlEnumValue(value = "UPGRADE")
	UPGRADE("UPGRADE"),
	@XmlEnumValue(value = "DEL")
	DELETE("DEL"),
	@XmlEnumValue(value = "MOVE")
	MOVE("MOVE"),
	;

	private static final Map<String, PacketOperations> map;
	
	static {
		map = new HashMap<String, PacketOperations>();
		for (PacketOperations type : values()) {
			map.put(type.operation, type);
		}
		
		
	}
	
	public final String operation;
	
	PacketOperations(String operation){
		this.operation = operation;
	}

	public static PacketOperations fromOperations(String operation) {
		operation = operation.toUpperCase();
		return map.get(operation);
	}
}
