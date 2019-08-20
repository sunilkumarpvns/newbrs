package com.elitecore.core.systemx.esix.udp;

public interface UDPResponseListener {
	void responseReceived(UDPRequest udpRequest, UDPResponse udpResponse);
	void requestTimeout(UDPRequest udpRequest, UDPCommunicator udpCommunicator);
	void requestDropped(UDPRequest udpRequest, UDPCommunicator udpCommunicator);
}
