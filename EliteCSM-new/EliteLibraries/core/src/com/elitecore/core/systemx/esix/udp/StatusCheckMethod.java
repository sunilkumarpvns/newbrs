package com.elitecore.core.systemx.esix.udp;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

@XmlEnum(value = String.class)
public enum StatusCheckMethod {

	@XmlEnumValue(value = "ICMP-Ping")
	ICMP_REQUEST(1, "ICMP-Ping"),
	
	@XmlEnumValue(value = "Radius-Message")
	RADIUS_PACKET(2, "Radius-Message"),
	
	@XmlEnumValue(value = "Packet-Bytes")
	PACKET_BYTES(3, "Packet-Bytes");
	
	public String name;
	public int id;
	private static final Map<Integer,StatusCheckMethod> map;
	
	public static final StatusCheckMethod[] VALUES = values();
	
	static {
		map = new HashMap<Integer,StatusCheckMethod>();
		for (StatusCheckMethod type : VALUES) {
			map.put(type.id, type);
		}
	}
	
	private StatusCheckMethod(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public static StatusCheckMethod fromStatusCheckMethods(int id) {
		return map.get(id);
	}
	
}
