package com.elitecore.aaa.radius.systemx.esix.udp.impl;

import java.util.Arrays;

import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.mibs.radius.accounting.client.RadiusAcctClientMIB;
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
 * This communicator is used for handling Rad Acct type of external system.
 * It has inner communication handler for maintaining the counters of Radius Acct client MIB.
 * 
 * @author elitecore
 *
 */
public class RadAcctCommunicator extends RadUDPCommunicatorImpl {

	private ESIAttributeHandler esiAttributeHandler;

	public RadAcctCommunicator(final ServerContext serverContext, DefaultExternalSystemData externalSystem) {
		super(serverContext, externalSystem);
	}

	public RadAcctCommunicator(final ServiceContext serviceContext, DefaultExternalSystemData externalSystem) {
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
		return new RadAcctCommHandler(getCommunicatorContext());
	}
	
	public class RadAcctCommHandler extends RadCommunicationHandlerImpl {

		public RadAcctCommHandler(UDPCommunicatorContext communicatorContext) {
			super(communicatorContext);
		}
		
		@Override
		public void handlePreRadiusRequest(UDPRequest radUDPRequest,int identifier){
			
			RadiusAcctClientMIB.incrementRadiusAccClientRequests(getCommunicatorContext().getName());
			RadiusAcctClientMIB.incrementRadiusAcctClientPendingRequests(getCommunicatorContext().getName());
			
			RadiusPacket radiusPacket  = (RadiusPacket)((RadUDPRequest)radUDPRequest).getRadiusPacket();
			radiusPacket.setIdentifier(identifier);
			
			String oldSharedSecret = ((RadUDPRequest)radUDPRequest).getSharedSecret(); 
			
			byte[] oldAuthenticator = radiusPacket.getAuthenticator();
			
			IRadiusAttribute proxyAttribute = Dictionary.getInstance().getKnownAttribute(RadiusAttributeConstants.PROXY_STATE);
			if (proxyAttribute != null) {
				proxyAttribute.setStringValue(String.valueOf(radUDPRequest.getRequestSentTime()));
				radiusPacket.addAttribute(proxyAttribute);
			}
			
			radiusPacket.refreshPacketHeader();
			
			String newSharedSecret = ((RadiusExternalSystemData)getCommunicatorContext().getExternalSystem()).getSharedSecret();
			byte[] newAuthenticator = RadiusUtility.generateRFC2866RequestAuthenticator(radiusPacket,newSharedSecret);
			((RadiusPacket)radiusPacket).setAuthenticator(newAuthenticator);
			
			 // TODO	re-encrypting value of any encryptable attribute
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
		protected void postHandleResponse(UDPResponse udpResponse){
			RadUDPResponse radUDPResponse = (RadUDPResponse)udpResponse;
			
			RadiusAcctClientMIB.setRadiusAcctClientRoundTripTime(getCommunicatorContext().getName(),getLastRequestResponseTimeDiff());
			RadiusAcctClientMIB.incrementRadiusAcctClientResponses(getCommunicatorContext().getName());
			if(radUDPResponse.getRadiusPacket().getPacketType()== RadiusConstants.ACCOUNTING_RESPONSE_MESSAGE){
				RadiusAcctClientMIB.decrementRadiusAcctClientPendingRequests(getCommunicatorContext().getName());
				RadiusAcctClientMIB.setRadiusAcctClientRoundTripTime(getCommunicatorContext().getName(),getLastRequestResponseTimeDiff());
			}else {
				RadiusAcctClientMIB.incrementRadiusAcctClientUnknowTypes(getCommunicatorContext().getName());
			}
		}
		
		@Override
		protected void actionOnRetransmission(UDPRequest udpRequest) {
			RadiusAcctClientMIB.incrementRadiusAccClientRetransmissions(getCommunicatorContext().getName());
			RadiusAcctClientMIB.incrementRadiusAcctClientPendingRequests(getCommunicatorContext().getName());
		}
		
		@Override
		public void actionOnMalformedResponseReceived(UDPResponse udpreResponse) {
			RadiusAcctClientMIB.incrementRadiusAcctClientMalformedAccessResponse(getCommunicatorContext().getName());
		}
		
		@Override
	    public void actionOnTimeout(UDPRequest udpRequest) {
			RadiusAcctClientMIB.incrementRadiusAcctClientTimeouts(getCommunicatorContext().getName());
			RadiusAcctClientMIB.decrementRadiusAcctClientPendingRequests(getCommunicatorContext().getName());
		}
		
		@Override
		public void actionOnResponseDropped(UDPResponse udpResponse) {
			RadiusAcctClientMIB.incrementRadiusAcctClientPacketsDropped(getCommunicatorContext().getName());
		}
		@Override
		public void actionOnBadAuthenticatorResponseReceived(UDPResponse udpreResponse) {
			RadiusAcctClientMIB.incrementRadiusAcctClientBadAuthenticators(getCommunicatorContext().getName());
		}

	}
}
