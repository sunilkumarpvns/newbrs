package com.elitecore.test;

import com.elitecore.test.dependecy.diameter.VirtualGateway;
import com.elitecore.test.diameter.factory.PacketFactory;

public interface ScenarioContext {
	
	PacketFactory getPacketFactory();

	VirtualGateway getVirtualGateway();

}
