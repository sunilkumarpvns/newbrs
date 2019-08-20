package com.elitecore.client.eap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.naming.AuthenticationException;

import com.elitecore.client.configuration.EapConfiguration;
import com.elitecore.client.eap.methods.GTCAuthenticator;
import com.elitecore.client.eap.methods.MD5Authenticator;
import com.elitecore.client.eap.methods.MSCHAPv2Authenticator;
import com.elitecore.client.eap.peap.PEAPAuthenticator;
import com.elitecore.client.eap.tls.TLSAuthenticator;
import com.elitecore.client.eap.ttls.TTLSAuthenticator;
import com.elitecore.client.util.constants.CommunicationStates;
import com.elitecore.client.util.constants.EapEvents;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.coreeap.dictionary.EAPTypeDictionary;
import com.elitecore.coreeap.packet.EAPPacket;
import com.elitecore.coreeap.packet.EAPPacketFactory;
import com.elitecore.coreeap.packet.InvalidEAPPacketException;
import com.elitecore.coreeap.packet.InvalidEAPTypeException;
import com.elitecore.coreeap.packet.types.EAPType;
import com.elitecore.coreeap.packet.types.NAKEAPType;
import com.elitecore.coreeap.packet.types.tls.TLSEAPType;
import com.elitecore.coreeap.util.constants.EapPacketConstants;
import com.elitecore.coreeap.util.constants.EapTypeConstants;
import com.elitecore.coreeap.util.constants.tls.TLSFlagConstants;

/**
 * EapHandler handles Request Identity from server and also does EAP - Method Negotiation
 * 
 * @author Kuldeep Panchal
 * @author Malav Desai
 *
 */
public class EapAuthenticator {
	private static final String MODULE = "EAP_AUTHENTICATOR";
	private EapConfiguration configuration;
	private EapMethodAuthenticator methodAuthenticator;
	private int identifier;
	private EapTypeConstants eapMethod;
	private EapEvents event;
	private final String identity;

	/*
	 * sTOcBuffer is used to buffer the fragments received from the server
	 */
	private ByteArrayOutputStream sTOcBuffer;

	private boolean finished = false;

	public EapAuthenticator(EapTypeConstants type,EapConfiguration configuration, String identity) throws InitializationFailedException {
		sTOcBuffer = new ByteArrayOutputStream();
		this.configuration = configuration;
		eapMethod = type;
		this.identity = identity;
		switch (type) {
		case TLS:
			methodAuthenticator = new TLSAuthenticator(getConfiguration().getTls());
			break;

		case TTLS:
			methodAuthenticator = new TTLSAuthenticator(getConfiguration().getTtls(), getConfiguration().getTls());
			break;

		case PEAP:
			methodAuthenticator = new PEAPAuthenticator(getConfiguration());
			break;

		case MD5_CHALLENGE:
			methodAuthenticator = new MD5Authenticator(getConfiguration());
			break;

		case MSCHAPv2:
			methodAuthenticator = new MSCHAPv2Authenticator(getConfiguration().getPeap());
			break;

		case GTC:
			methodAuthenticator = new GTCAuthenticator(getConfiguration().getPeap());
			break;

		default:
			LogManager.getLogger().debug(MODULE, "Requested Method not supported.");
			break;
		}
	}

	public void reset() throws InitializationFailedException{
		event = null;
		finished = false;
		methodAuthenticator.reset();
	}
	
	public void innerReset() throws InitializationFailedException{
		event = null;
		finished = false;
		if(methodAuthenticator.getClass().equals(TLSAuthenticator.class)){
			methodAuthenticator = new TLSAuthenticator(getConfiguration().getTls());
			return;
		}
		methodAuthenticator.reset();
	}

	/**
	 * Here it is decided that for what purpose the request has come
	 * @param receivedEapPacket
	 * @return 
	 * @throws AuthenticationException
	 */
	public EAPPacket process(EAPPacket receivedEapPacket) throws AuthenticationException {
		LogManager.getLogger().info(MODULE, "Processing in EapHandler");
		LogManager.getLogger().info(MODULE, "EAPPacket Received: \n" + receivedEapPacket.toString());
		EAPPacket responseEapPacket = null;
		if(receivedEapPacket.getIdentifier() > 0){
			getConfiguration().setIdentifier(receivedEapPacket.getIdentifier());
		}
		event = getEvent(receivedEapPacket);
		try{
			switch(event){
			case RequestIdentityReceived:
				LogManager.getLogger().info(MODULE, "Event: Request Identity Received");
				responseEapPacket = actionOnRequestIdentityReceived();
				break;

			case OtherMethodResponse:
				LogManager.getLogger().info(MODULE, "Event: Received Proposed Method");
				responseEapPacket = actionOnOtherMethodResponseReceived(receivedEapPacket);
				break;

			case MethodNotSupported:
				LogManager.getLogger().info(MODULE, "Event: Method Not Supported");
				responseEapPacket = actionOnGenerateNAKResponse(receivedEapPacket);
				break;

			case TLSMethodResponse:
				LogManager.getLogger().info(MODULE, "Event: Method Response Received");
				responseEapPacket = actionOnTLSMethodResponseReceived(receivedEapPacket);
				break;

			case FragmentReceived:
				LogManager.getLogger().info(MODULE, "Event: Fragment Received");
				responseEapPacket = actionOnFragmentReceived(receivedEapPacket);
				break;

			case IgnoreReceivedPacket:
				LogManager.getLogger().info(MODULE, "Event: Ignore Received Packet");
				actionOnIgnoreReceivedPacket();
				break;

			default:
				LogManager.getLogger().error(MODULE, "Event: Not proper Response");
				break;
			}
		}catch(Exception e){
			LogManager.getLogger().trace(e);
			throw new AuthenticationException("Authentication Failed, Reason: " + e.getMessage());			
		}
		LogManager.getLogger().info(MODULE, "EAPPacket generated: ");
		LogManager.getLogger().info(MODULE, responseEapPacket.toString());
		return responseEapPacket;
	}
	private void actionOnIgnoreReceivedPacket() throws AuthenticationException {
		LogManager.getLogger().debug(MODULE, "Packet is not an expected EAP packet, so ignored");
		throw new AuthenticationException("Invalid Packet is received, so ignored");
	}

	/**
	 * 	Generates the respected Event after parsing the received packet.
	 *  
	 * @param eapPacket
	 * @return EAP events after parsing the packet
	 */
	private EapEvents getEvent(EAPPacket eapPacket){

		if(eapPacket.getCode() != EapPacketConstants.REQUEST.packetId){
			return EapEvents.IgnoreReceivedPacket;
		}
		
		EAPType eapType = eapPacket.getEAPType(); 
		if(eapType.getType() == EapTypeConstants.IDENTITY.typeId){
			return EapEvents.RequestIdentityReceived;
		} 

		if(!(eapType.getType() == eapMethod.typeId)){ 
			return EapEvents.MethodNotSupported;
		}

		if(eapType.getType() == EapTypeConstants.TLS.typeId ||
				eapType.getType() == EapTypeConstants.TTLS.typeId ||
				eapType.getType() == EapTypeConstants.PEAP.typeId){

			if(TLSFlagConstants.isFragmented(((TLSEAPType)eapType).getFlagValue())){
				return EapEvents.FragmentReceived;
			}
			return EapEvents.TLSMethodResponse;
		}
		return EapEvents.OtherMethodResponse;
	}

	private EAPPacket actionOnRequestIdentityReceived() {
		EAPType eapType = EAPTypeDictionary.getInstance().createEAPType(EapTypeConstants.IDENTITY.typeId);
		byte[] identity = getConfiguration().getIdentity().getBytes();
		eapType.setData(identity);
		return genrateEapPacket(eapType);
	}

	/**
	 * Generates the NAK Packet for Negative Acknowledgment
	 * @param receivedEapPacket
	 * @return
	 */
	private EAPPacket actionOnGenerateNAKResponse(EAPPacket receivedEapPacket) {
		NAKEAPType nakEAPType = (NAKEAPType)EAPTypeDictionary.getInstance().createEAPType(EapTypeConstants.NAK.typeId);
		byte[] alternateMethod = {(byte)eapMethod.getTypeId()};
		nakEAPType.setAlternateMethods(alternateMethod);
		return genrateEapPacket(nakEAPType);
	}

	private EAPPacket actionOnOtherMethodResponseReceived(EAPPacket receivedEapPacket) throws AuthenticationException {

		CommunicationStates state = methodAuthenticator.process(receivedEapPacket.getEAPType().toBytes());
		if(state == CommunicationStates.COMPLETED_PROCESSING){
			finished = true;
		}
		EAPType outEapType;
		try{
			outEapType = new EAPType(methodAuthenticator.getOutData());
		} catch (InvalidEAPTypeException e) {
			LogManager.getLogger().error(MODULE, "EAPPacket not generated. Reason : " + e.getMessage());
			throw new AuthenticationException("Improper packet generated");
		}
		return genrateEapPacket(outEapType);

	}

	/**
	 * It handles the Access Challenges from the server. It buffers the response till SSLEnginge finishes its wrapping.
	 * @param receivedEapPacket
	 * @return EAPPacket
	 * @throws AuthenticationException 
	 */
	private EAPPacket actionOnTLSMethodResponseReceived(EAPPacket receivedEapPacket) throws AuthenticationException {
		/*
		 * here buffering is done to handle the last fragment received from the server
		 */
		try {
			sTOcBuffer.write(((TLSEAPType)receivedEapPacket.getEAPType()).getTLSData());
		} catch (IOException e) {
			LogManager.getLogger().error(MODULE, "Unable to write data in Buffer");
			LogManager.getLogger().trace(e);
			throw new AuthenticationException("Unable to write data in Buffer");
		}

		CommunicationStates state = methodAuthenticator.process(sTOcBuffer.toByteArray());
		sTOcBuffer.reset();
		if(state == CommunicationStates.COMPLETED_PROCESSING){
			finished = true;
		}
		
		TLSEAPType outTlsEapType = (TLSEAPType)EAPTypeDictionary.getInstance().createEAPType(eapMethod.getTypeId());
		byte[] outTlsData = methodAuthenticator.getOutData();
		/**
		 *  in case of ACK message, data part of the TLSEAPType message is nothing.
		 *  Our TLSEAPType is not able parse data with less than 3 bytes.
		 */
		if(outTlsData.length != 0){
			//TODO ask narendra about it that lenght flag is to set or not?????
			outTlsEapType.setFlagValue(TLSFlagConstants.L_FLAG.value);
			outTlsEapType.setTLSMessageLength(outTlsData.length);
			outTlsEapType.setTLSData(outTlsData);
		}
		return genrateEapPacket(outTlsEapType);
	}
	
	/**
	 * Handles the received fragment by buffering it and send the Acknowledgment
	 * @param receivedEapPacket
	 * @return
	 * @throws AuthenticationException 
	 */
	private EAPPacket actionOnFragmentReceived(EAPPacket receivedEapPacket) throws AuthenticationException{
		/*
		 * Here buffering is done for the fragments received from the server, it will not include the last fragment
		 * as it will be handled in method response
		 */
		try {
			sTOcBuffer.write(((TLSEAPType)receivedEapPacket.getEAPType()).getTLSData());
		} catch (IOException e) {
			LogManager.getLogger().error(MODULE, "Unable to write data in Buffer");
			LogManager.getLogger().trace(e);
			throw new AuthenticationException("Unable to write data in Buffer");
		}
		return genrateEapPacket(EAPTypeDictionary.getInstance().createEAPType(eapMethod.getTypeId()));
	}
	
	private EAPPacket genrateEapPacket(EAPType eapType){
		EAPPacket sendEapPacket = EAPPacketFactory.getInstance().createEAPPacket(EapPacketConstants.RESPONSE.packetId);
		try {
			sendEapPacket.setIdentifier(nextId());
			sendEapPacket.setEAPType(eapType);
			sendEapPacket.resetLength();
		} catch (InvalidEAPPacketException e) {
			LogManager.getLogger().error(MODULE, "EapPacket not generated, Reason: " + e.getMessage());
			LogManager.getLogger().trace(e);
		}
		return sendEapPacket;
	}
	
	/**
	 * Provides next identifier for the next EAPPacket
	 * @param identifier
	 * @return next identifier
	 */
	private int nextId(){
		this.identifier %= 255;
		return ++this.identifier;
	}

	public EAPPacket getIdentityEapPacket() {
		EAPType eapType = EAPTypeDictionary.getInstance().createEAPType(EapTypeConstants.IDENTITY.typeId);
		eapType.setData(identity.getBytes());
		EAPPacket identityPacket = EAPPacketFactory.getInstance().createEAPPacket(EapPacketConstants.REQUEST.packetId);
		try {
			identityPacket.setIdentifier(0);
			identityPacket.setEAPType(eapType);
			identityPacket.resetLength();
		} catch (InvalidEAPPacketException e) {
			LogManager.getLogger().error(MODULE, "EapPacket not generated, Reason: " + e.getMessage());
			LogManager.getLogger().trace(e);
		}
		return identityPacket;
	}
	
	public EapConfiguration getConfiguration() {
		return configuration;
	}

	/**
	 *  Used by Outer EAP Method state machine to know wheather the tls Handshake for 
	 *  inner tls method is finished or not.
	 * 
	 * @return bollean value about processing is finished or not
	 */
	public boolean isFinished() {
		return finished;
	}
}