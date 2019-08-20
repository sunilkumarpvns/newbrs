package com.elitecore.test.dependecy.diameter;

import com.elitecore.test.dependecy.diameter.packet.Packet;

public interface VirtualInputStream {
	void received(Packet packet);
}
