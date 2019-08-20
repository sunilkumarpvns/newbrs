package com.elitecore.core.systemx.esix.udp;

public interface UDPResponse {
	int getIdentifier();
	byte[] getBytes();
	String getCommunicatorName();
}
