package com.elitecore.aaa.radius.systemx.esix.udp.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPRequest;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPResponse;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.systemx.esix.udp.UDPResponse;
import com.elitecore.core.systemx.esix.udp.UDPResponseListener;
import com.elitecore.core.systemx.esix.udp.impl.UDPRequestImpl;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.packet.IRadiusPacket;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;

public class RadUDPRequestImpl extends UDPRequestImpl implements RadUDPRequest {
	
	private static final String MODULE = "RAD-UDP-REQUEST";
	IRadiusPacket radiusPacket;
	String sharedSecret;
	
	public RadUDPRequestImpl(byte[] requestBytes, String sharedSecret,
			UDPResponseListener responseListener) {
		super(requestBytes, responseListener);
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
	public int getIdentifier() {
		return radiusPacket.getIdentifier();
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
	public boolean validate(UDPResponse response) {
		RadUDPResponse radResponse = (RadUDPResponse)response;
		boolean isProxyStateValid = validateOurProxyState(radResponse);
		if (isProxyStateValid) {
			removeReceivedProxyStatesFrom(radResponse);
		}
		return isProxyStateValid;
	}

	/**
	 * Remove all the proxy-state attributes from response, 
	 * as we add all the income proxy-state attribute
	 * from request to response from post processing 
	 * of radius request.
	 */
	private void removeReceivedProxyStatesFrom(RadUDPResponse response) {
		Collection<IRadiusAttribute> receivedProxyStates = response.getRadiusPacket().getRadiusAttributes(RadiusAttributeConstants.PROXY_STATE);
		if (Collectionz.isNullOrEmpty(receivedProxyStates) == false) {
			for (IRadiusAttribute proxyState : receivedProxyStates) {
				response.getRadiusPacket().removeAttribute(proxyState);
			}
		}
	}

	private boolean validateOurProxyState(RadUDPResponse radResponse) {
		Collection<IRadiusAttribute> receivedProxyStates = radResponse.getRadiusPacket().getRadiusAttributes(RadiusAttributeConstants.PROXY_STATE);
		if (Collectionz.isNullOrEmpty(receivedProxyStates)) {
			return true;
		}
				
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Removing proxy state attributes from response for ID: " + radResponse.getIdentifier());
		}

		IRadiusAttribute tmpProxyState = Dictionary.getInstance().getKnownAttribute(RadiusAttributeConstants.PROXY_STATE);
		if (tmpProxyState == null) {
			return true;
		}
		
		tmpProxyState.setStringValue(String.valueOf(getRequestSentTime()));
		
		List<IRadiusAttribute> proxyStates = (ArrayList<IRadiusAttribute>)receivedProxyStates;
		IRadiusAttribute ownProxyState = proxyStates.get(receivedProxyStates.size() - 1);
		
		if (ownProxyState.equals(tmpProxyState)) {
			return true;
		} else {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Value(s) of proxy state attribute: " + receivedProxyStates +
						" doesn't match with the received proxy state: " + tmpProxyState.getStringValue());
			}
			return false;
		}
	}
	
	@Override
	public String toString() {
		 return radiusPacket.toString();
	}
}
