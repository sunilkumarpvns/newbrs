package com.elitecore.netvertex.gateway.radius.communicator.impl;

import com.elitecore.core.systemx.esix.udp.impl.UDPResponseImpl;
import com.elitecore.coreradius.commons.packet.IRadiusPacket;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.netvertex.gateway.radius.communicator.RadUDPResponse;

public class RadUDPResponseImpl extends UDPResponseImpl implements RadUDPResponse {

	IRadiusPacket radiusPacket;
	public RadUDPResponseImpl(byte[] responseBytes,String esiName) {
		super(responseBytes,esiName);
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
	public byte[] getBytes() {
		return super.getBytes();
	}

	@Override
	public int getIdentifier() {
		return getRadiusPacket().getIdentifier();
	}
	
	@Override
	public String toString() {
		return radiusPacket.toString();
	}
}
