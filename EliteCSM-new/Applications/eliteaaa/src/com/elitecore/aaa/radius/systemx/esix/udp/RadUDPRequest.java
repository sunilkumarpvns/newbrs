package com.elitecore.aaa.radius.systemx.esix.udp;

import com.elitecore.core.systemx.esix.udp.UDPRequest;
import com.elitecore.coreradius.commons.packet.IRadiusPacket;

public interface RadUDPRequest extends UDPRequest {
	IRadiusPacket getRadiusPacket();
	String getSharedSecret();
	void setSharedSecret(String sharedSecret);
}
