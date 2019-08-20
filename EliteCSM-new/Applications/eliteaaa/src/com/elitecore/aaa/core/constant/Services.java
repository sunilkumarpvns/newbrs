package com.elitecore.aaa.core.constant;

import java.util.HashMap;
import java.util.Map;
public enum Services {
	
	RADIUSAUTH(101),
	RADIUSACCT(102),
	
	BASE(0),
	NASREQ(1),
	MobileIPV4(2),
	BASEACCOUNTING(3),
	Cc(4),
	EAP(5),
	SIP(6);
	
	public final int serviceId;
	private static final Map<Integer,Services> map;
	
	public static final Services[] VALUES = values();
	
	static {
		map = new HashMap<Integer,Services>();
		for (Services type : VALUES) {
			map.put(type.serviceId, type);
		}
	}
	Services(int serviceId) {
		this.serviceId = serviceId;
	}

	public int getserviceId() {
		return serviceId;
	}

	public static Services fromServiceId(int serviceId) {
		return map.get(serviceId);
	}
	
	public static boolean isValid(int value) {
		return map.containsKey(value);
	}

}
