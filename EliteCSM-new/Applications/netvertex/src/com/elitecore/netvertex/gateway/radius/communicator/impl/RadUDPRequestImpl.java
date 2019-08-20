package com.elitecore.netvertex.gateway.radius.communicator.impl;

import com.elitecore.core.systemx.esix.udp.UDPResponse;
import com.elitecore.core.systemx.esix.udp.UDPResponseListener;
import com.elitecore.core.systemx.esix.udp.impl.UDPRequestImpl;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.packet.IRadiusPacket;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.netvertex.gateway.radius.communicator.RadUDPRequest;
import com.elitecore.netvertex.gateway.radius.communicator.RadUDPResponse;

public class RadUDPRequestImpl extends UDPRequestImpl implements RadUDPRequest {

	IRadiusPacket radiusPacket;
	String sharedSecret;
	public RadUDPRequestImpl(byte[] requestBytes,String sharedSecret,UDPResponseListener udpResponseListener) {
		super(requestBytes, udpResponseListener);
		this.sharedSecret = sharedSecret;
		radiusPacket = new RadiusPacket();
		radiusPacket.setBytes(requestBytes);
	}
	
	public RadUDPRequestImpl(byte[] requestBytes, String sharedSecret) {
		super(requestBytes);
		this.sharedSecret = sharedSecret;
		radiusPacket = new RadiusPacket();
		radiusPacket.setBytes(requestBytes);
	}

	@Override
	public IRadiusPacket getRadiusPacket() {
		return radiusPacket;
	}

	@Override
	public String getSharedSecret() {
		return sharedSecret;
	}

	@Override
	public void setSharedSecret(String sharedSecret) {
		this.sharedSecret = sharedSecret;
	}

	@Override
	public byte[] getBytes() {
		return radiusPacket.getBytes();
	}

	@Override
	public int getIdentifier() {
		return radiusPacket.getIdentifier();
	}
	
	@Override
	public boolean validate(UDPResponse response) {
		RadUDPResponse radResponse = (RadUDPResponse)response;
		IRadiusAttribute proxyState = radResponse.getRadiusPacket().getRadiusAttribute(RadiusAttributeConstants.PROXY_STATE);
		if(proxyState == null)
			return true;
		IRadiusAttribute tmpProxyState = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.PROXY_STATE);
		tmpProxyState.setStringValue(String.valueOf(getRequestSentTime()));
		radResponse.getRadiusPacket().removeAttribute(proxyState);
		return proxyState.equals(tmpProxyState);
	}
	
	@Override
	public String toString() {
		 return radiusPacket.toString();
	}
}
