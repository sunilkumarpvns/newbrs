package com.elitecore.test.dependecy.diameter;

import com.elitecore.test.dependecy.diameter.packet.Packet;

public interface VirtualOutputStream {
	void send(Packet  packet);
}
