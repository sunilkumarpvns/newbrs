package com.elitecore.aaa.radius.systemx.esix.udp.impl;

import java.util.Arrays;

import com.elitecore.aaa.radius.systemx.esix.udp.RadCommunicationHandler;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPRequest;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPResponseImpl;
import com.elitecore.aaa.radius.systemx.esix.udp.RadiusExternalSystemData;
import com.elitecore.core.systemx.esix.udp.SessionLimitReachedException;
import com.elitecore.core.systemx.esix.udp.UDPCommunicatorContext;
import com.elitecore.core.systemx.esix.udp.UDPRequest;
import com.elitecore.core.systemx.esix.udp.UDPResponse;
import com.elitecore.core.systemx.esix.udp.impl.CommunicationHandlerImpl;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.packet.IRadiusPacket;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.RadiusUtility;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

public class RadCommunicationHandlerImpl extends CommunicationHandlerImpl
		implements RadCommunicationHandler {
	
	public RadCommunicationHandlerImpl(UDPCommunicatorContext communicatorContext) {
		super(communicatorContext);
	}
	
	public void handleRequest(RadUDPRequest request)
			throws SessionLimitReachedException {
		super.handleRequest(request);
	}

	@Override
	protected UDPResponse createUDPResponsePacket(byte[] responseBytes,String esiName) {
		return new RadUDPResponseImpl(responseBytes,esiName);
	}
	
	@Override
	public int getMaxIdentifier() {
		return 255;
	}
	
	@Override
	public Integer getNextIdentifier(UDPRequest udpRequest) {
		RadUDPRequest radUDPRequest = (RadUDPRequest)udpRequest;
		if(radUDPRequest.getRadiusPacket().getPacketType() == RadiusConstants.STATUS_SERVER_MESSAGE){
			return RadiusConstants.STATUS_SERVER_MESSAGE_RESERVED_ID;
		}
		return super.getNextIdentifier(udpRequest);
	}
	
	@Override
	public void releaseIdentifier(int identifier) {
		if(identifier != RadiusConstants.STATUS_SERVER_MESSAGE_RESERVED_ID){
			super.releaseIdentifier(identifier);
		}
	}
	
	@Override
	protected void preHandleRequest(UDPRequest udpRequest,int identifier){
		getCommunicatorContext().removeAttributes(udpRequest);
		super.preHandleRequest(udpRequest, identifier);
		handlePreRadiusRequest(udpRequest, identifier);
	}
	public void handlePreRadiusRequest(UDPRequest udpRequest,int identifier){
		
		//Update Packet for Destination Server
		RadUDPRequest radUDPRequest = (RadUDPRequest) udpRequest;
		IRadiusPacket radiusPacket = radUDPRequest.getRadiusPacket();
		((RadiusPacket)radiusPacket).setIdentifier(identifier);
		
		String newSharedSecret = ((RadiusExternalSystemData)getCommunicatorContext().getExternalSystem()).getSharedSecret();
		
		IRadiusAttribute proxyAttribute = Dictionary.getInstance().getKnownAttribute(RadiusAttributeConstants.PROXY_STATE);
		if (proxyAttribute != null) {
			proxyAttribute.setStringValue(String.valueOf(radUDPRequest.getRequestSentTime()));
			radiusPacket.addAttribute(proxyAttribute);
		}
		
		radiusPacket.refreshPacketHeader();
		
//		((RadiusPacket)radiusPacket).setAuthenticator(RadiusUtility.generateRFC2866RequestAuthenticator(radiusPacket,newSharedSecret));
		
		byte[] oldAuthenticator = radiusPacket.getAuthenticator();
		String oldSharedSecret = ((RadUDPRequest)radUDPRequest).getSharedSecret();
		if(radiusPacket.getRadiusAttribute(RadiusAttributeConstants.USER_PASSWORD) != null) {
			byte [] bPassword = radiusPacket.getRadiusAttribute(RadiusAttributeConstants.USER_PASSWORD).getValueBytes();
			String strPassword = RadiusUtility.decodepass(oldAuthenticator,bPassword,16,oldSharedSecret);
			((RadiusPacket)radiusPacket).setAuthenticator(RadiusUtility.generateRFC2865RequestAuthenticator());
			byte [] bEncrypatedPassword = RadiusUtility.encryptPasswordRFC2865(strPassword.trim(),radiusPacket.getAuthenticator(),newSharedSecret);
			radiusPacket.getRadiusAttribute(RadiusAttributeConstants.USER_PASSWORD).setValueBytes(bEncrypatedPassword);
		}else if(radiusPacket.getRadiusAttribute(RadiusAttributeConstants.CHAP_PASSWORD) != null) {
			if(radiusPacket.getRadiusAttribute(RadiusAttributeConstants.CHAP_CHALLENGE) == null) {
				IRadiusAttribute attr = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CHAP_CHALLENGE);
				attr.setValueBytes(radiusPacket.getAuthenticator());

				radiusPacket.addAttribute(attr);
			}
			((RadiusPacket)radiusPacket).setAuthenticator(RadiusUtility.generateRFC2865RequestAuthenticator());
		} else if (radiusPacket.getPacketType() == RadiusConstants.ACCOUNTING_REQUEST_MESSAGE) {
			((RadiusPacket)radiusPacket).setAuthenticator(RadiusUtility.generateRFC2866RequestAuthenticator(radiusPacket,newSharedSecret));
		} else {
			((RadiusPacket)radiusPacket).setAuthenticator(RadiusUtility.generateRFC2865RequestAuthenticator());
		}
		
		byte[] newAuthenticator = radiusPacket.getAuthenticator();
		
         //	re-encrypting value of any encryptable attribute
		((RadiusPacket)radiusPacket).reencryptAttributes(oldAuthenticator, oldSharedSecret, newAuthenticator, newSharedSecret);
		radiusPacket.refreshPacketHeader();
					
		if(radiusPacket.getRadiusAttribute(RadiusAttributeConstants.MESSAGE_AUTHENTICATOR) != null) {
			radiusPacket.getRadiusAttribute(String.valueOf(RadiusAttributeConstants.MESSAGE_AUTHENTICATOR)).setValueBytes(getUpdatedMessageAuthenticator(radiusPacket , newSharedSecret));
		}
		
		radUDPRequest.setSharedSecret(newSharedSecret);
		
	}
	
	private byte[] getUpdatedMessageAuthenticator(IRadiusPacket requestPacket ,String sharedSecret){
    	
		byte[] zeroBytes = new byte[16];
		Arrays.fill(zeroBytes, (byte)0);
		byte[] resultBytes = null;
		
		requestPacket.getRadiusAttribute(String.valueOf(RadiusAttributeConstants.MESSAGE_AUTHENTICATOR)).setValueBytes(zeroBytes);
		requestPacket.refreshPacketHeader();		
		resultBytes = RadiusUtility.HMAC("MD5", requestPacket.getBytes(), sharedSecret);
		
		return resultBytes ;
    	
    }
	
    public void actionOnBadAuthenticatorResponseReceived(UDPResponse udpreResponse) {
		
	}
    
    public void actionOnMalformedResponseReceived(UDPResponse udpreResponse) {
		
	}
}
