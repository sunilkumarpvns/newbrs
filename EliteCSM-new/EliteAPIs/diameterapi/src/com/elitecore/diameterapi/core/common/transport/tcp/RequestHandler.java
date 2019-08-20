package com.elitecore.diameterapi.core.common.transport.tcp;

import com.elitecore.diameterapi.core.common.packet.Packet;
import com.elitecore.diameterapi.core.common.transport.NetworkConnectionHandler;

public interface RequestHandler {
	
	public void handleReceivedMessage(Packet packet, NetworkConnectionHandler connectionListener);

}
