package com.elitecore.corenetvertex.constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public enum SessionTypeConstant {
	
	GX("Gx", GatewayTypeConstant.DIAMETER, true),
	GY("Gy", GatewayTypeConstant.DIAMETER, true),
	RX("Rx", GatewayTypeConstant.DIAMETER, true),
	RO("Ro", GatewayTypeConstant.DIAMETER, true),
	S9("S9", GatewayTypeConstant.DIAMETER, true),
	SY("SY", GatewayTypeConstant.DIAMETER, false),
	CISCO_GX("CISCO_GX", GatewayTypeConstant.DIAMETER, true),
	CISCO_GY("CISCO_GY", GatewayTypeConstant.DIAMETER, false),
	RADIUS("RADIUS", GatewayTypeConstant.RADIUS, true);
	
	public final String val;
	public final GatewayTypeConstant gatewayType;
	public final boolean hasDBSession;
	
	private static HashMap<String, SessionTypeConstant> map;
	private static final List<SessionTypeConstant> dbSessionTypes;
	
	static {
		map = new HashMap<>(6,1);
		dbSessionTypes = new ArrayList<>();
		for(SessionTypeConstant sessionType : values()){
			map.put(sessionType.val, sessionType);
			if(sessionType.hasDBSession) {
				dbSessionTypes.add(sessionType);
			}
		}


	}
	
	private SessionTypeConstant(String val, GatewayTypeConstant type, boolean hasDBSession) {
		this.val = val;
		this.gatewayType = type;
		this.hasDBSession = hasDBSession;
	}

	public static List<SessionTypeConstant> getDbSessionType() {
		return dbSessionTypes;
	}
		
	public String getVal() {
		return val;
	}
	
	public static SessionTypeConstant fromValue(String type) {
		return map.get(type);
	}
}
