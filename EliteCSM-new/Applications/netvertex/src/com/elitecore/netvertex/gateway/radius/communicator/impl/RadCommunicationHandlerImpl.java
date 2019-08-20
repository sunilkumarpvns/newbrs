package com.elitecore.netvertex.gateway.radius.communicator.impl;

import com.elitecore.core.systemx.esix.udp.UDPCommunicatorContext;
import com.elitecore.core.systemx.esix.udp.UDPRequest;
import com.elitecore.core.systemx.esix.udp.UDPResponse;
import com.elitecore.core.systemx.esix.udp.impl.CommunicationHandlerImpl;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.packet.IRadiusPacket;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.RadiusUtility;
import com.elitecore.coreradius.commons.util.constants.DynAuthErrorCode;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeValuesConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;
import com.elitecore.netvertex.gateway.radius.communicator.RadCommunicationHandler;
import com.elitecore.netvertex.gateway.radius.communicator.RadUDPExternalSystem;
import com.elitecore.netvertex.gateway.radius.communicator.RadUDPRequest;
import com.elitecore.netvertex.gateway.radius.communicator.RadUDPResponse;
import com.elitecore.netvertex.gateway.radius.snmp.dynaauth.DynaAuthClientCounters;

public class RadCommunicationHandlerImpl extends CommunicationHandlerImpl implements RadCommunicationHandler {
	
	private DynaAuthClientCounters dynaAuthClientCounters;
	public RadCommunicationHandlerImpl(
			UDPCommunicatorContext communicatorContext , DynaAuthClientCounters dynaAuthClientCounters) {
		super(communicatorContext);
		this.dynaAuthClientCounters = dynaAuthClientCounters;
	}

	@Override
	protected UDPResponse createUDPResponsePacket(byte[] responseBytes,String name) {
		return new RadUDPResponseImpl(responseBytes,name);
	}

	@Override
	public int getMaxIdentifier() {
		return 255;
	}
	
	@Override
	public Integer getNextIdentifier(UDPRequest request) {
		RadUDPRequest radUDPRequest = (RadUDPRequest)request;
		if(radUDPRequest.getRadiusPacket().getPacketType() == RadiusConstants.STATUS_SERVER_MESSAGE){
			return RadiusConstants.STATUS_SERVER_MESSAGE_RESERVED_ID;
		}
		return super.getNextIdentifier(radUDPRequest);
	}

	@Override
	public void releaseIdentifier(int identifier) {
		if(identifier != RadiusConstants.STATUS_SERVER_MESSAGE_RESERVED_ID){
			super.releaseIdentifier(identifier);
		}
	}

	
	@Override
	protected void preHandleRequest(UDPRequest udpRequest,int identifier){
		super.preHandleRequest(udpRequest, identifier);

		//Update Packet for Destination Server
		RadUDPRequest radUDPRequest = (RadUDPRequest) udpRequest;
		RadiusPacket radiusPacket = (RadiusPacket)radUDPRequest.getRadiusPacket();
		((RadiusPacket)radiusPacket).setIdentifier(identifier);
		String newSharedSecret = ((RadUDPExternalSystem)getCommunicatorContext().getExternalSystem()).getSharedSecret();
		
		//Remove Proxy State Attributes
		IRadiusAttribute proxyStateAttr = radiusPacket.getRadiusAttribute(RadiusAttributeConstants.PROXY_STATE);
		while(proxyStateAttr != null){
			radiusPacket.removeAttribute(proxyStateAttr);
			proxyStateAttr = radiusPacket.getRadiusAttribute(RadiusAttributeConstants.PROXY_STATE);
		}
		//Add Proxy State Attribute
		IRadiusAttribute proxyAttribute = Dictionary.getInstance().getKnownAttribute(RadiusAttributeConstants.PROXY_STATE);
		if(proxyAttribute != null){
			proxyAttribute.setStringValue(String.valueOf(udpRequest.getRequestSentTime()));
			radiusPacket.addAttribute(proxyAttribute);
		}
		
		radiusPacket.refreshPacketHeader();
		
		if(radUDPRequest.getRadiusPacket().getPacketType() == RadiusConstants.ACCESS_REQUEST_MESSAGE){
			byte[] oldAuthenticator = radiusPacket.getAuthenticator();
			String oldSharedSecret = radUDPRequest.getSharedSecret();
			if(radiusPacket.getRadiusAttribute(RadiusAttributeConstants.USER_PASSWORD) != null) {
				byte [] bPassword = radiusPacket.getRadiusAttribute(RadiusAttributeConstants.USER_PASSWORD).getValueBytes();
				/*String strPassword = RadiusUtility.decodepass(oldAuthenticator,bPassword,16,oldSharedSecret);
				((RadiusPacket)radiusPacket).setAuthenticator(RadiusUtility.generateRFC2865RequestAuthenticator());
				byte [] bEncrypatedPassword = RadiusUtility.encryptPasswordRFC2865(strPassword.trim(),radiusPacket.getAuthenticator(),newSharedSecret);*/
				radiusPacket.getRadiusAttribute(RadiusAttributeConstants.USER_PASSWORD).setValueBytes(bPassword);
			}else if(radiusPacket.getRadiusAttribute(RadiusAttributeConstants.CHAP_PASSWORD) != null) {
				if(radiusPacket.getRadiusAttribute(RadiusAttributeConstants.CHAP_CHALLENGE) == null) {
					IRadiusAttribute attr = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CHAP_CHALLENGE);
					attr.setValueBytes(radiusPacket.getAuthenticator());

					radiusPacket.addAttribute(attr);
				}
				((RadiusPacket)radiusPacket).setAuthenticator(RadiusUtility.generateRFC2865RequestAuthenticator());
			}else{
				((RadiusPacket)radiusPacket).setAuthenticator(RadiusUtility.generateRFC2865RequestAuthenticator());
			}
			
			byte[] newAuthenticator = radiusPacket.getAuthenticator();
			
//			re-encrypting value of any encryptable attribute
			((RadiusPacket)radiusPacket).reencryptAttributes(oldAuthenticator, oldSharedSecret, newAuthenticator, newSharedSecret);
			radiusPacket.refreshPacketHeader();
						
			if(radiusPacket.getRadiusAttribute(RadiusAttributeConstants.MESSAGE_AUTHENTICATOR) != null) {
				radiusPacket.getRadiusAttribute(String.valueOf(RadiusAttributeConstants.MESSAGE_AUTHENTICATOR)).setValueBytes(getUpdatedMessageAuthenticator(radiusPacket , newSharedSecret));
			}
		}else if(radiusPacket.getPacketType() == RadiusConstants.ACCOUNTING_REQUEST_MESSAGE || 
				radiusPacket.getPacketType() == RadiusConstants.COA_REQUEST_MESSAGE || 
				radiusPacket.getPacketType() == RadiusConstants.DISCONNECTION_REQUEST_MESSAGE) {
			if(radiusPacket.getRadiusAttribute(RadiusAttributeConstants.MESSAGE_AUTHENTICATOR) != null) {
				radiusPacket.setAuthenticator(new byte[16]); 
				radiusPacket.getRadiusAttribute(String.valueOf(RadiusAttributeConstants.MESSAGE_AUTHENTICATOR)).setValueBytes(getUpdatedMessageAuthenticator(radiusPacket , newSharedSecret));
			}			
			
			((RadiusPacket)radiusPacket).setAuthenticator(RadiusUtility.generateRFC2866RequestAuthenticator(radiusPacket,newSharedSecret));
		}else {
			((RadiusPacket)radiusPacket).setAuthenticator(RadiusUtility.generateRFC2865RequestAuthenticator());
		}
		radUDPRequest.setSharedSecret(newSharedSecret);
		incrementDynaAuthRequestCounters(radUDPRequest);
	}
	
	private byte[] getUpdatedMessageAuthenticator(IRadiusPacket requestPacket ,String sharedSecret){
    	
		byte[] zeroBytes = new byte[16];
		byte[] resultBytes = null;
		
		requestPacket.getRadiusAttribute(String.valueOf(RadiusAttributeConstants.MESSAGE_AUTHENTICATOR)).setValueBytes(zeroBytes);
		requestPacket.refreshPacketHeader();		
		resultBytes = RadiusUtility.HMAC("MD5", requestPacket.getBytes(), sharedSecret);
		
		return resultBytes ;
    	
    }
	
	private void incrementDynaAuthRequestCounters(RadUDPRequest radUDPRequest){
		IRadiusPacket radiusPacket  = radUDPRequest.getRadiusPacket();
		int packetType = radiusPacket.getPacketType();
		String ipAddress = getCommunicatorContext().getIPAddress();
		
		if(packetType == RadiusConstants.COA_REQUEST_MESSAGE) {
			dynaAuthClientCounters.incCoAReqCntr(ipAddress);
			dynaAuthClientCounters.incCoAPenReqCntr(ipAddress);
			if(isAuthOnlyPacket(radiusPacket)){
				dynaAuthClientCounters.incCoAAuthOnlyReqCntr(ipAddress);
			}
		}else if(packetType == RadiusConstants.DISCONNECTION_REQUEST_MESSAGE){
			dynaAuthClientCounters.incDisReqCntr(ipAddress);
			dynaAuthClientCounters.incDisPenReqCntr(ipAddress);
			if(isAuthOnlyPacket(radiusPacket)){
				dynaAuthClientCounters.incDisAuthOnlyReqCntr(ipAddress);
			}	
		}
	}
	
	@Override
	protected void postHandleResponse(UDPResponse udpResponse) {
		
		String ipAddress = getCommunicatorContext().getIPAddress();
		IRadiusPacket radiusPacket = ((RadUDPResponse)udpResponse).getRadiusPacket();
		int packetType = radiusPacket.getPacketType();
		
		dynaAuthClientCounters.setRoundTripTime(ipAddress,getLastRequestResponseTimeDiff());
		if(packetType == RadiusConstants.COA_ACK_MESSAGE){
			dynaAuthClientCounters.incCoAAckCntr(ipAddress);
			dynaAuthClientCounters.decCoAPenReqCntr(ipAddress);
		}else if(packetType == RadiusConstants.COA_NAK_MESSAGE){
			dynaAuthClientCounters.incCoANakCntr(ipAddress);
			dynaAuthClientCounters.decCoAPenReqCntr(ipAddress);
			if(isAuthOnlyPacket(radiusPacket)){
				dynaAuthClientCounters.incCoANakAuthOnlyReqCntr(ipAddress);
			}
			if(isNakWithSessNoContextRes(radiusPacket)){
				dynaAuthClientCounters.incCoANakSessNoCtxCntr(ipAddress);
				
			}
		}else if(packetType == RadiusConstants.DISCONNECTION_ACK_MESSAGE){
			dynaAuthClientCounters.incDisAckCntr(ipAddress);
			dynaAuthClientCounters.decDisPenReqCntr(ipAddress);
		}else if(packetType == RadiusConstants.DISCONNECTION_NAK_MESSAGE){
			dynaAuthClientCounters.incDisNackCntr(ipAddress);
			dynaAuthClientCounters.decDisPenReqCntr(ipAddress);
			if(isAuthOnlyPacket(radiusPacket)){
				dynaAuthClientCounters.incDisNackAuthOnlyReqCntr(ipAddress);
			}
			if(isNakWithSessNoContextRes(radiusPacket)){
				dynaAuthClientCounters.incDisNackSessNoCtxCntr(ipAddress);
			}
		}else{
			dynaAuthClientCounters.incUnknownTypeCntr(ipAddress);
		}
	}
	
	@Override
	protected void actionOnRetransmission(UDPRequest udpRequest) {
		IRadiusPacket radiusPacket  = (IRadiusPacket)((RadUDPRequest)udpRequest).getRadiusPacket();
		int packetType = radiusPacket.getPacketType();
		String ipAddress = getCommunicatorContext().getIPAddress();
		if(packetType==RadiusConstants.DISCONNECTION_REQUEST_MESSAGE){
			dynaAuthClientCounters.incDisRetraCntr(ipAddress);
			dynaAuthClientCounters.decDisPenReqCntr(ipAddress);
		}else if (packetType==RadiusConstants.COA_REQUEST_MESSAGE) {
			dynaAuthClientCounters.incCoARetraCntr(ipAddress);
			dynaAuthClientCounters.decCoAPenReqCntr(ipAddress);
		}	
	}
	
	@Override
	public void actionOnResponseDropped(UDPResponse udpResponse) {
		int packetType = ((RadUDPResponse)udpResponse).getRadiusPacket().getPacketType();
		String ipAddress = getCommunicatorContext().getIPAddress();
		if(packetType == RadiusConstants.COA_ACK_MESSAGE || packetType == RadiusConstants.COA_NAK_MESSAGE){
			dynaAuthClientCounters.incCoAPackDropCntr(ipAddress);
		}else if(packetType == RadiusConstants.DISCONNECTION_ACK_MESSAGE || packetType == RadiusConstants.DISCONNECTION_NAK_MESSAGE){
			dynaAuthClientCounters.incDisPackDropCntr(ipAddress);
		}
	}
	
	@Override
	public void actionOnTimeout(UDPRequest udpRequest) {
		int packetType = ((RadUDPRequest)udpRequest).getRadiusPacket().getPacketType();
		String ipAddress = getCommunicatorContext().getIPAddress();
		if(packetType == RadiusConstants.COA_REQUEST_MESSAGE){
			dynaAuthClientCounters.incCoATimeoutCntr(ipAddress);
			dynaAuthClientCounters.decCoAPenReqCntr(ipAddress);
		}else if(packetType == RadiusConstants.DISCONNECTION_REQUEST_MESSAGE){
			dynaAuthClientCounters.incDisTimeoutCntr(ipAddress);
			dynaAuthClientCounters.decDisPenReqCntr(ipAddress);
		}
	}
	
	private boolean isAuthOnlyPacket(IRadiusPacket radiusPacket) {
		IRadiusAttribute radiusAttribute  = radiusPacket.getRadiusAttribute(RadiusAttributeConstants.SERVICE_TYPE);
		if(radiusAttribute !=null && radiusAttribute.getIntValue() == RadiusAttributeValuesConstants.AUTHORIZE_ONLY)
				return true;
		return false;
	}
	
	private boolean isNakWithSessNoContextRes(IRadiusPacket radiusPacket) {
		IRadiusAttribute radiusAttribute  = radiusPacket.getRadiusAttribute(RadiusAttributeConstants.ERROR_CAUSE);
		if(radiusAttribute != null && (radiusAttribute.getIntValue() == DynAuthErrorCode.SessionContextNotFound.value))
			return true;
		return false;
	}
}
