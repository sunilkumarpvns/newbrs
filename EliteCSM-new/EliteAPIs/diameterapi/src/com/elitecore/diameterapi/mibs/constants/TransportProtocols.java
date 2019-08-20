package com.elitecore.diameterapi.mibs.constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public enum TransportProtocols {
	
	TCP(1,"TCP"),
	SCTP(2,"SCTP"),;
	
	public final int code;
	public final String protocolTypeStr;
	private static final Map<Integer,TransportProtocols> map;
	
	public static final TransportProtocols[] VALUES = values();
	
	static {
		map = new HashMap<Integer,TransportProtocols>();
		for (TransportProtocols type : VALUES) {
			map.put(type.code, type);
		}
		
		
	}
	TransportProtocols(int code,String protocolTypeStr) {
		this.code = code;
		this.protocolTypeStr = protocolTypeStr;
	}

	public static TransportProtocols fromProtocolTypeCode(int protocolTypeCode) {
		return map.get(protocolTypeCode);
	}
	
	public static TransportProtocols fromProtocolTypeString(String protocolTypeStr) {
		if(protocolTypeStr != null){
			for(TransportProtocols protocol : VALUES){
				if(protocol.protocolTypeStr.equalsIgnoreCase(protocolTypeStr))
					return protocol;
			}
		}
		return null;
	}
	
	public static boolean isValid(int value) {
		return map.containsKey(value);
	}
	
	public static String getProtocolTypeString(int protocolTypeCode) {
		TransportProtocols protocolType = map.get(protocolTypeCode);  
		if(protocolType != null){
			return protocolType.protocolTypeStr;
		}
		
		return "INVALID PROTOCOL TYPE ";
	}

	public static ArrayList<String> getprotocolList(){
		
		ArrayList<String> list = new ArrayList<String>();
		for(TransportProtocols protocol : VALUES){
			list.add(protocol.protocolTypeStr);
		}
		return list;
	}
}
