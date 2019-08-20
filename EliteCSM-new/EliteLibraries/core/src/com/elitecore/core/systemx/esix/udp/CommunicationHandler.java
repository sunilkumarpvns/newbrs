package com.elitecore.core.systemx.esix.udp;

import java.nio.channels.DatagramChannel;
import java.util.List;

import com.elitecore.core.commons.InitializationFailedException;

public interface CommunicationHandler {
	void init(DatagramChannel dataGramChannel) throws InitializationFailedException;
	int getPortNumber();
	void handleRequest(UDPRequest request) throws SessionLimitReachedException;
	List<UDPRequest> getTimeoutRequests();
	public void shutdown();
}
