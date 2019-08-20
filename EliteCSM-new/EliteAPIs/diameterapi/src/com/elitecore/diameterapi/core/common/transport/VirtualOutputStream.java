package com.elitecore.diameterapi.core.common.transport;

import com.elitecore.diameterapi.core.common.packet.Packet;

public interface VirtualOutputStream {
	void send(Packet packet);
}