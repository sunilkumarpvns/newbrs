package com.elitecore.diameterapi.core.common.transport;

import com.elitecore.diameterapi.core.common.packet.Packet;

public interface VirtualInputStream {
	void received(Packet packet);
}
