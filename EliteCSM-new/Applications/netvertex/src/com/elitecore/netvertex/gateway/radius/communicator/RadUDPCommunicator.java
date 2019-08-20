package com.elitecore.netvertex.gateway.radius.communicator;

import com.elitecore.core.systemx.esix.udp.UDPCommunicator;

public interface RadUDPCommunicator extends UDPCommunicator {
	void handleRadiusRequest(RadUDPRequest radUDPRequest);
}
