package com.elitecore.aaa.radius.systemx.esix.udp;

import com.elitecore.core.systemx.esix.udp.CommunicationHandler;
import com.elitecore.core.systemx.esix.udp.SessionLimitReachedException;
import com.elitecore.core.systemx.esix.udp.UDPRequest;

public interface RadCommunicationHandler extends CommunicationHandler {
	@Override
	public void handleRequest(UDPRequest request)
			throws SessionLimitReachedException;
}
