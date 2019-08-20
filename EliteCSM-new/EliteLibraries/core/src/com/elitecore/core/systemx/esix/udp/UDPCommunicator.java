package com.elitecore.core.systemx.esix.udp;

import com.elitecore.core.systemx.esix.ESCommunicator;

public interface UDPCommunicator extends ESCommunicator{
	public static final String COMMAND_KEY = "UDP_COMM";
	int getMinLocalPort();
	int getTimeOutRequestCounter();
	void handleUDPRequest(UDPRequest request);
	UDPCommunicatorContext getCommunicatorContext();
	public void shutdown();
}
