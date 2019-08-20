package com.elitecore.diameterapi.mibs.constants;


public enum SecurityProtocol {
	NONE(1, "NONE"),
	TLS(2,"TLS"),
	IPSEC(3, "IPSEC");
	
	public static final SecurityProtocol[] VALUES = values();
	public int protocol;
	public String protocolName;
	
	private SecurityProtocol(int protocol, String protocolName) {
		this.protocol = protocol;
		this.protocolName = protocolName;
	}
	
	public static SecurityProtocol fromCode(int protocol) {
		for (int i = 0; i < VALUES.length; i++) {
			if(VALUES[i].protocol == protocol)
				return VALUES[i];
		}
		return null;
	}
}
