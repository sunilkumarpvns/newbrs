package com.elitecore.aaa.radius.systemx.esix.udp;

import com.elitecore.core.systemx.esix.udp.UDPResponse;
import com.elitecore.coreradius.commons.packet.IRadiusPacket;

public interface RadUDPResponse extends UDPResponse {
	IRadiusPacket getRadiusPacket();
}
