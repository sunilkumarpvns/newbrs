package com.elitecore.aaa.radius.systemx.esix.udp;

import com.elitecore.core.systemx.esix.udp.impl.UDPResponseImpl;
import com.elitecore.coreradius.commons.packet.IRadiusPacket;
import com.elitecore.coreradius.commons.packet.RadiusPacket;

public class RadUDPResponseImpl extends UDPResponseImpl implements
		RadUDPResponse {
	IRadiusPacket radiusPacket;
	public RadUDPResponseImpl(byte[] responseBytes,String esiName) {
		super(responseBytes,esiName);
	}

	@Override
	public int getIdentifier() {
		return getRadiusPacket().getIdentifier();
	}

	@Override
	public IRadiusPacket getRadiusPacket() {
		if(radiusPacket == null){
			radiusPacket = new RadiusPacket();
			radiusPacket.setBytes(getBytes());
		}
		return radiusPacket;
	}
	
	@Override
	public String toString() {
		return radiusPacket.toString();
	}
}
