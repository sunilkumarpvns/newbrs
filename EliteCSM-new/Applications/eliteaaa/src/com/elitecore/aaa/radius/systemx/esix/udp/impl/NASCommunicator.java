package com.elitecore.aaa.radius.systemx.esix.udp.impl;

import java.util.Arrays;

import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.mibs.radius.dynauth.client.RadiusDynAuthClientMIB;
import com.elitecore.aaa.radius.systemx.esix.udp.DefaultExternalSystemData;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPRequest;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPResponse;
import com.elitecore.aaa.radius.systemx.esix.udp.RadiusExternalSystemData;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.servicex.ServiceContext;
import com.elitecore.core.systemx.esix.udp.CommunicationHandler;
import com.elitecore.core.systemx.esix.udp.UDPCommunicatorContext;
import com.elitecore.core.systemx.esix.udp.UDPRequest;
import com.elitecore.core.systemx.esix.udp.UDPResponse;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.packet.IRadiusPacket;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.RadiusUtility;
import com.elitecore.coreradius.commons.util.constants.DynAuthErrorCode;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

/**
 * 
 * This communicator is used for handling NAS type of external system.
 * It has inner communication handler for maintaining the counters of Dynauth client MIB.
 * 
 * @author elitecore
 *
 */
public class NASCommunicator extends RadUDPCommunicatorImpl {

	private ESIAttributeHandler esiAttributeHandler;

	public NASCommunicator(final ServerContext serverContext, DefaultExternalSystemData externalSystem) {
		super(serverContext, externalSystem);
	}

	public NASCommunicator(final ServiceContext serviceContext, DefaultExternalSystemData externalSystem) {
		super(serviceContext, externalSystem);
	}
	
	@Override
	public void init() throws InitializationFailedException {
		super.init();
		initializeAttributeRemover();
	}
	
	private void initializeAttributeRemover(){
		DefaultExternalSystemData radUDPExternalSystem = (DefaultExternalSystemData)externalSystemData;
		this.esiAttributeHandler = ESIAttributeHandler.create(radUDPExternalSystem.getSupportedAttributesStr(),
				radUDPExternalSystem.getUnsupportedAttributesStr());
	}
	
	@Override
	public void reInit() throws InitializationFailedException {
		super.reInit();
		this.externalSystemData = ((AAAServerContext)getServerContext()).getServerConfiguration()
		.getRadESConfiguration().getESData(externalSystemData.getUUID()).get();
		initializeAttributeRemover();
	}

	@Override
	public void removeAttributes(UDPRequest udpRequest) {
		esiAttributeHandler.handleRequest((RadiusPacket)((RadUDPRequest)udpRequest).getRadiusPacket());
		((RadUDPRequest)udpRequest).getRadiusPacket().refreshPacketHeader();
	}
	
	@Override
	protected CommunicationHandler createCommunicationHandler() {
		return new StaticNasCommHandler(getCommunicatorContext());
	}
	
	public class StaticNasCommHandler extends RadCommunicationHandlerImpl {

		public StaticNasCommHandler(UDPCommunicatorContext communicatorContext) {
			super(communicatorContext);
		}
		
		@Override
		public void handlePreRadiusRequest(UDPRequest radUDPRequest,int identifier){
			
			RadiusPacket radiusPacket  = (RadiusPacket)((RadUDPRequest)radUDPRequest).getRadiusPacket();
			String oldSharedSecret = ((RadUDPRequest)radUDPRequest).getSharedSecret();
			incrementInitialRequestCounter(radiusPacket);
			
			radiusPacket.setIdentifier(identifier);

			
			//Remove Proxy State Attributes
			IRadiusAttribute proxyStateAttr = radiusPacket.getRadiusAttribute(RadiusAttributeConstants.PROXY_STATE);
			while(proxyStateAttr != null){
				radiusPacket.removeAttribute(proxyStateAttr);
				proxyStateAttr = radiusPacket.getRadiusAttribute(RadiusAttributeConstants.PROXY_STATE);
			}
			
			String newSharedSecret = ((RadiusExternalSystemData)getCommunicatorContext().getExternalSystem()).getSharedSecret();
			
            //	re-encrypting value of any encryptable attribute
			((RadiusPacket)radiusPacket).reencryptAttributes(radiusPacket.getAuthenticator(), oldSharedSecret, radiusPacket.getAuthenticator(), newSharedSecret);
			radiusPacket.refreshPacketHeader();

			((RadiusPacket)radiusPacket).setAuthenticator(RadiusUtility.generateRFC2866RequestAuthenticator(radiusPacket,newSharedSecret));

			if(radiusPacket.getRadiusAttribute(RadiusAttributeConstants.MESSAGE_AUTHENTICATOR) != null) {
				radiusPacket.getRadiusAttribute(String.valueOf(RadiusAttributeConstants.MESSAGE_AUTHENTICATOR)).setValueBytes(getUpdatedMessageAuthenticator(radiusPacket , newSharedSecret));
			}
			
			((RadUDPRequest)radUDPRequest).setSharedSecret(newSharedSecret);
			
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
		
		private void incrementInitialRequestCounter(RadiusPacket radiusPacket) {
			int packetType = radiusPacket.getPacketType();
			
			if(packetType==RadiusConstants.DISCONNECTION_REQUEST_MESSAGE){
				RadiusDynAuthClientMIB.incrementRadiusDynAuthClientDisconPendingRequests(getCommunicatorContext().getName());
				RadiusDynAuthClientMIB.incrementRadiusDynAuthClientDisconRequests(getCommunicatorContext().getName());
				if(isAuthOnlyRequests(radiusPacket))
					RadiusDynAuthClientMIB.incrementRadiusDynAuthClientDisconAuthOnlyRequests(getCommunicatorContext().getName());
			}else if (packetType==RadiusConstants.COA_REQUEST_MESSAGE) {
				RadiusDynAuthClientMIB.incrementRadiusDynAuthClientCoARequests(getCommunicatorContext().getName());
				RadiusDynAuthClientMIB.incrementRadiusDynAuthClientCoAPendingRequests(getCommunicatorContext().getName());
				if(isAuthOnlyRequests(radiusPacket)){
					RadiusDynAuthClientMIB.incrementRadiusDynAuthClientCoAAuthOnlyRequest(getCommunicatorContext().getName());
				}
			}
				
		}

		private boolean isAuthOnlyRequests(RadiusPacket radiusPacket) {
			IRadiusAttribute radiusAttribute  = radiusPacket.getRadiusAttribute(RadiusAttributeConstants.SERVICE_TYPE);
				if(radiusAttribute!=null && radiusAttribute.getIntValue()==8)
					return true;
			return false;
		}
		
		@Override
		protected void actionOnRetransmission(UDPRequest udpRequest) {
			int packetType = ((RadUDPRequest)udpRequest).getRadiusPacket().getPacketType();
			if(packetType==RadiusConstants.DISCONNECTION_REQUEST_MESSAGE){
				RadiusDynAuthClientMIB.incrementRadiusDynAuthClientDisconRetransmissions(getCommunicatorContext().getName());
				RadiusDynAuthClientMIB.incrementRadiusDynAuthClientDisconPendingRequests(getCommunicatorContext().getName());
			}else if (packetType==RadiusConstants.COA_REQUEST_MESSAGE) {
				RadiusDynAuthClientMIB.incrementRadiusDynAuthClientCoARetransmissions(getCommunicatorContext().getName());
				RadiusDynAuthClientMIB.incrementRadiusDynAuthClientCoAPendingRequests(getCommunicatorContext().getName());
			}	
		}

		@Override
		protected void postHandleResponse(UDPResponse udpResponse){
			RadUDPResponse radUDPResponse = (RadUDPResponse)udpResponse;
			RadiusPacket responsePacket = (RadiusPacket)radUDPResponse.getRadiusPacket();
			
			int packetType = responsePacket.getPacketType();
			
			if(packetType==RadiusConstants.DISCONNECTION_ACK_MESSAGE){
				RadiusDynAuthClientMIB.setRadiusDynAuthClientRoundTripTime(getCommunicatorContext().getName(),getLastRequestResponseTimeDiff());
				RadiusDynAuthClientMIB.incrementRadiusDynAuthClientDisconAcks(getCommunicatorContext().getName());
				RadiusDynAuthClientMIB.decrementRadiusDynAuthClientDisconPendingRequests(getCommunicatorContext().getName());
			}else if (packetType == RadiusConstants.DISCONNECTION_NAK_MESSAGE) {
				RadiusDynAuthClientMIB.setRadiusDynAuthClientRoundTripTime(getCommunicatorContext().getName(),getLastRequestResponseTimeDiff());
				RadiusDynAuthClientMIB.incrementRadiusDynAuthClientDisconNaks(getCommunicatorContext().getName());
				RadiusDynAuthClientMIB.decrementRadiusDynAuthClientDisconPendingRequests(getCommunicatorContext().getName());
				if(isAuthOnlyRequests(responsePacket))
					RadiusDynAuthClientMIB.incrementRadiusDynAuthClientDisconNakAuthOnlyRequest(getCommunicatorContext().getName());
				if(isNakWithSessNoContextRes(responsePacket))
					RadiusDynAuthClientMIB.incrementRadiusDynAuthClientDisconNakSessNoContext(getCommunicatorContext().getName());
			}else if (packetType==RadiusConstants.COA_ACK_MESSAGE) {
				RadiusDynAuthClientMIB.setRadiusDynAuthClientRoundTripTime(getCommunicatorContext().getName(),getLastRequestResponseTimeDiff());
				RadiusDynAuthClientMIB.incrementRadiusDynAuthClientCoAAcks(getCommunicatorContext().getName());
				RadiusDynAuthClientMIB.decrementRadiusDynAuthClientCoAPendingRequests(getCommunicatorContext().getName());
			}else if (packetType==RadiusConstants.COA_NAK_MESSAGE) {
				RadiusDynAuthClientMIB.setRadiusDynAuthClientRoundTripTime(getCommunicatorContext().getName(),getLastRequestResponseTimeDiff());
				RadiusDynAuthClientMIB.incrementRadiusDynAuthClientCoANaks(getCommunicatorContext().getName());
				RadiusDynAuthClientMIB.decrementRadiusDynAuthClientCoAPendingRequests(getCommunicatorContext().getName());
				
				
				IRadiusAttribute serviceTypeAttr = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.SERVICE_TYPE);
				serviceTypeAttr.setStringValue(String.valueOf(8));
				responsePacket.addAttribute(serviceTypeAttr);
				responsePacket.refreshPacketHeader();
				
				IRadiusAttribute errorcause = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.ERROR_CAUSE);
				errorcause.setStringValue(String.valueOf(503));
				responsePacket.addAttribute(errorcause);
				responsePacket.refreshPacketHeader();
				
				if(isAuthOnlyRequests(responsePacket))
					RadiusDynAuthClientMIB.incrementRadiusDynAuthClientCoANakAuthOnlyRequest(getCommunicatorContext().getName());
				if(isNakWithSessNoContextRes(responsePacket))
					RadiusDynAuthClientMIB.incrementRadiusDynAuthClientCoANakSessNoContext(getCommunicatorContext().getName());
			}else {
				RadiusDynAuthClientMIB.incrementRadiusDynAuthClientUnknownTypes(getCommunicatorContext().getName());
			}	
		}

		private boolean isNakWithSessNoContextRes(RadiusPacket responsePacket) {
			IRadiusAttribute radiusAttribute  = responsePacket.getRadiusAttribute(RadiusAttributeConstants.ERROR_CAUSE);
			if(radiusAttribute!=null && (radiusAttribute.getIntValue() == DynAuthErrorCode.SessionContextNotFound.value))
				return true;
		return false;
		}
		
		@Override
		public void actionOnMalformedResponseReceived(UDPResponse udpreResponse) {
			int packetType = ((RadUDPResponse)udpreResponse).getRadiusPacket().getPacketType();
			if(packetType==RadiusConstants.DISCONNECTION_ACK_MESSAGE || packetType==RadiusConstants.DISCONNECTION_NAK_MESSAGE){
				RadiusDynAuthClientMIB.incrementRadiusDynAuthClientMalformedDisconResponses(getCommunicatorContext().getName());
			}else if (packetType==RadiusConstants.COA_ACK_MESSAGE || packetType==RadiusConstants.COA_NAK_MESSAGE) {
				RadiusDynAuthClientMIB.incrementRadiusDynAuthClientMalformedCoAResponses(getCommunicatorContext().getName());
			}
		}
		
		@Override
		public void actionOnBadAuthenticatorResponseReceived(UDPResponse udpResponse) {
			int packetType = ((RadUDPResponse)udpResponse).getRadiusPacket().getPacketType();
			if(packetType==RadiusConstants.DISCONNECTION_ACK_MESSAGE || packetType==RadiusConstants.DISCONNECTION_NAK_MESSAGE){
				RadiusDynAuthClientMIB.incrementRadiusDynAuthClientDisconBadAuthenticators(getCommunicatorContext().getName());
			}else if (packetType==RadiusConstants.COA_ACK_MESSAGE || packetType==RadiusConstants.COA_NAK_MESSAGE) {
				RadiusDynAuthClientMIB.incrementRadiusDynAuthClientCoABadAuthenticators(getCommunicatorContext().getName());
			}	
		}
		
		@Override
	    public void actionOnTimeout(UDPRequest udpRequest) {
			int packetType = ((RadUDPRequest)udpRequest).getRadiusPacket().getPacketType();
			if(packetType==RadiusConstants.DISCONNECTION_REQUEST_MESSAGE){
				RadiusDynAuthClientMIB.incrementRadiusDynAuthClientDisconTimeouts(getCommunicatorContext().getName());
				RadiusDynAuthClientMIB.decrementRadiusDynAuthClientDisconPendingRequests(getCommunicatorContext().getName());
			}else if (packetType==RadiusConstants.COA_REQUEST_MESSAGE) {
				RadiusDynAuthClientMIB.incrementRadiusDynAuthClientCoATimeouts(getCommunicatorContext().getName());
				RadiusDynAuthClientMIB.decrementRadiusDynAuthClientCoAPendingRequests(getCommunicatorContext().getName());
			}
				
		}
		@Override
		public void actionOnResponseDropped(UDPResponse udpResponse) {
			int packetType = ((RadUDPResponse)udpResponse).getRadiusPacket().getPacketType();
			if(packetType==RadiusConstants.DISCONNECTION_ACK_MESSAGE || packetType==RadiusConstants.DISCONNECTION_NAK_MESSAGE){
				RadiusDynAuthClientMIB.incrementRadiusDynAuthClientDisconPacketsDropped(getCommunicatorContext().getName());
			}else if (packetType==RadiusConstants.COA_ACK_MESSAGE || packetType==RadiusConstants.COA_NAK_MESSAGE) {
				RadiusDynAuthClientMIB.incrementRadiusDynAuthClientCoAPacketsDropped(getCommunicatorContext().getName());
			}
			
		}

	}
}
