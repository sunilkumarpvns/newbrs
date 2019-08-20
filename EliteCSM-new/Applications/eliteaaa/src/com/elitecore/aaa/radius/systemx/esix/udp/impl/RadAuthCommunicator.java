package com.elitecore.aaa.radius.systemx.esix.udp.impl;

import java.util.Arrays;

import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.mibs.radius.authentication.client.RadiusAuthClientMIB;
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
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

/**
 * This communicator is used for handling Rad Auth type of external system.
 * It has inner communication handler for maintaining the counters of Radius Auth client MIB.
 * 
 * @author elitecore
 *
 */
public class RadAuthCommunicator extends RadUDPCommunicatorImpl {

	private ESIAttributeHandler esiAttributeHandler;

	public RadAuthCommunicator(final ServerContext serverContext, DefaultExternalSystemData externalSystem) {
		super(serverContext, externalSystem);
	}

	public RadAuthCommunicator(final ServiceContext serviceContext, DefaultExternalSystemData externalSystem) {
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
		return new RadAuthCommHandler(getCommunicatorContext());
	}
	
	public class RadAuthCommHandler extends RadCommunicationHandlerImpl {

		public RadAuthCommHandler(UDPCommunicatorContext communicatorContext) {
			super(communicatorContext);
		}
		
		@Override
		public void handlePreRadiusRequest(UDPRequest radUDPRequest,int identifier){
			
			RadiusAuthClientMIB.incrementRadiusClientAuthAccessRequests(getCommunicatorContext().getName());
			RadiusAuthClientMIB.incrementRadiusAuthClientPandingRequests(getCommunicatorContext().getName());
			
			
			String newSharedSecret = ((RadiusExternalSystemData)getCommunicatorContext().getExternalSystem()).getSharedSecret();
			RadiusPacket radiusPacket  = (RadiusPacket)((RadUDPRequest)radUDPRequest).getRadiusPacket();
			
			radiusPacket.setIdentifier(identifier);
			
			IRadiusAttribute proxyAttribute = Dictionary.getInstance().getKnownAttribute(RadiusAttributeConstants.PROXY_STATE);
			if (proxyAttribute != null) {
				proxyAttribute.setStringValue(String.valueOf(radUDPRequest.getRequestSentTime()));
				radiusPacket.addAttribute(proxyAttribute);
			}
			
			radiusPacket.refreshPacketHeader();

			byte[] oldAuthenticator = radiusPacket.getAuthenticator();
			String oldSharedSecret = ((RadUDPRequest)radUDPRequest).getSharedSecret();
			if(radiusPacket.getRadiusAttribute(RadiusAttributeConstants.USER_PASSWORD) != null) {
				byte [] bPassword = radiusPacket.getRadiusAttribute(RadiusAttributeConstants.USER_PASSWORD).getValueBytes();
				String strPassword = RadiusUtility.decryptPasswordRFC2865(bPassword, oldAuthenticator, oldSharedSecret);
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
			}else{
				((RadiusPacket)radiusPacket).setAuthenticator(RadiusUtility.generateRFC2865RequestAuthenticator());
			}
			
			byte[] newAuthenticator = radiusPacket.getAuthenticator();
			
             //	re-encrypting value of any encryptable attribute
			((RadiusPacket)radiusPacket).reencryptAttributes(oldAuthenticator, oldSharedSecret, newAuthenticator, newSharedSecret);
			radiusPacket.refreshPacketHeader();
						
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
		
		@Override
		public void actionOnBadAuthenticatorResponseReceived(UDPResponse udpreResponse) {
			RadiusAuthClientMIB.incrementRadiusAuthClientBadAuthenticators(getCommunicatorContext().getName());
		}
		
		@Override
		public void actionOnMalformedResponseReceived(UDPResponse udpreResponse) {
			RadiusAuthClientMIB.incrementRadiusAuthClientMalformedAccessResponse(getCommunicatorContext().getName());
		}
		
		@Override
	    public void actionOnTimeout(UDPRequest udpRequest) {
			RadiusAuthClientMIB.incrementRadiusAuthClientTimeouts(getCommunicatorContext().getName());
			RadiusAuthClientMIB.decrementRadiusAuthClientPandingRequests(getCommunicatorContext().getName());
		}
		
		@Override
		protected void actionOnRetransmission(UDPRequest udpRequest) {
			RadiusAuthClientMIB.incrementRadiusClientAuthAccessRetransmissions(getCommunicatorContext().getName());
			RadiusAuthClientMIB.incrementRadiusAuthClientPandingRequests(getCommunicatorContext().getName());
		}
		
		@Override
		public void actionOnResponseDropped(UDPResponse udpResponse) {
			RadiusAuthClientMIB.incrementRadiusAuthClientPacketsDropped(getCommunicatorContext().getName());
		}
		
		@Override
		protected void postHandleResponse(UDPResponse udpResponse){
			RadUDPResponse radUDPResponse = (RadUDPResponse)udpResponse;
			int responsePacketType = radUDPResponse.getRadiusPacket().getPacketType();
			if(responsePacketType == RadiusConstants.ACCESS_ACCEPT_MESSAGE){
				RadiusAuthClientMIB.setRadiusAuthClientRoundTripTime(getCommunicatorContext().getName(),getLastRequestResponseTimeDiff());
				RadiusAuthClientMIB.incrementRadiusAuthClientAccessAccept(getCommunicatorContext().getName());
				RadiusAuthClientMIB.decrementRadiusAuthClientPandingRequests(getCommunicatorContext().getName());
			}else if (responsePacketType == RadiusConstants.ACCESS_REJECT_MESSAGE) {
				RadiusAuthClientMIB.setRadiusAuthClientRoundTripTime(getCommunicatorContext().getName(),getLastRequestResponseTimeDiff());
				RadiusAuthClientMIB.incrementRadiusAuthClientAccessReject(getCommunicatorContext().getName());
				RadiusAuthClientMIB.decrementRadiusAuthClientPandingRequests(getCommunicatorContext().getName());
			}else if (responsePacketType == RadiusConstants.ACCESS_CHALLENGE_MESSAGE) {
				RadiusAuthClientMIB.setRadiusAuthClientRoundTripTime(getCommunicatorContext().getName(),getLastRequestResponseTimeDiff());
				RadiusAuthClientMIB.incrementRadiusAuthClientAccessChallenges(getCommunicatorContext().getName());
				RadiusAuthClientMIB.decrementRadiusAuthClientPandingRequests(getCommunicatorContext().getName());
			}else {
				RadiusAuthClientMIB.incrementRadiusAuthClientUnknowTypes(getCommunicatorContext().getName());
			}
		}
	}
}
