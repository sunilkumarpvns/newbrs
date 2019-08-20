package com.elitecore.client.eap.peap;

import java.util.Arrays;

import javax.naming.AuthenticationException;
import javax.net.ssl.SSLException;

import com.elitecore.client.configuration.EapConfiguration;
import com.elitecore.client.eap.EapAuthenticator;
import com.elitecore.client.eap.EapMethodAuthenticator;
import com.elitecore.client.eap.tls.TLSAuthenticator;
import com.elitecore.client.util.constants.CommunicationStates;
import com.elitecore.client.util.constants.OuterMethodEvents;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.coreeap.dictionary.EAPTypeDictionary;
import com.elitecore.coreeap.packet.EAPPacket;
import com.elitecore.coreeap.packet.EAPPacketFactory;
import com.elitecore.coreeap.packet.InvalidEAPPacketException;
import com.elitecore.coreeap.packet.types.EAPType;
import com.elitecore.coreeap.util.constants.EapPacketConstants;
import com.elitecore.coreeap.util.constants.EapTypeConstants;

/**
 *  Handles EAp-PEAP Authentication. Uses EapAuthenticator for EAPType negotiation.
 *  Supports EAP-MD5Challenge, EAP-TLS and EAP-MSCHAPv2 as inner PEAP methods.
 *  
 * @author Kuldeep Panchal
 * @author Malav Desai
 *
 */
public class PEAPAuthenticator implements EapMethodAuthenticator {
	
	private static final String MODULE = "PEAP_AUTHENTICATOR";
	private OuterMethodEvents state;
	private final EapTypeConstants innerType;
	private EapAuthenticator eapAuthenticator;
	private TLSAuthenticator tlsAuthenticator;
	private byte[] outData;

	public PEAPAuthenticator(EapConfiguration eapConfiguration) throws InitializationFailedException {
		tlsAuthenticator = new TLSAuthenticator(eapConfiguration.getTls());
		state = OuterMethodEvents.HANDSHAKE_MESSAGE_RECEIVED;
		//TODO EapTypeConstants.getEapTypeConstants is new method generated so check it before commit
		innerType = EapTypeConstants.getEapTypeConstants(eapConfiguration.getPeap().getInnerMethod());
    	eapAuthenticator = new EapAuthenticator(innerType, eapConfiguration, eapConfiguration.getPeap().getInnerIdentity()); 
	}

	@Override
	public CommunicationStates process(byte[] inTlsData) throws AuthenticationException {
		LogManager.getLogger().info(MODULE, "Current State: " + state);
		switch (state) {
		case HANDSHAKE_MESSAGE_RECEIVED:
			setOutData(tlsAuthenticator.actionOnTlsHsRunning(inTlsData));
			if(tlsAuthenticator.isFinished()){
				state = OuterMethodEvents.APPLICATION_DATA_RECEIVED;
				LogManager.getLogger().info(MODULE, "State changes to: " + state);
			}
			return CommunicationStates.CONTINUE;
		
		case APPLICATION_DATA_RECEIVED:
			setOutData(actionOnApplicationData(inTlsData));
			if(eapAuthenticator.isFinished()){
				LogManager.getLogger().info(MODULE, "State changes to: " + state);
				state = OuterMethodEvents.PROCESSING_COMPLETED;
				setOutData(new byte[0]);
			}
			return CommunicationStates.CONTINUE;
			
		case PROCESSING_COMPLETED:
			return CommunicationStates.COMPLETED_PROCESSING;
			
		default:
			LogManager.getLogger().error(MODULE, "Not in proper state. Something in fishy");
			throw new AuthenticationException(MODULE + " not in proper state");
		}
	}
	
	/**
	 * Chooses process on the bases of Inner-method
	 * @param inTlsData
	 * @return byte array
	 * @throws AuthenticationException 
	 */
	private byte[] actionOnApplicationData(byte[] inTlsData) throws AuthenticationException {
		try {
			EAPPacket receivedEapPacket = generateEapPacketFromReceivedData(tlsAuthenticator.decrypt(inTlsData));
			LogManager.getLogger().debug(MODULE, "Inner Eap-Packet :" + receivedEapPacket);
			
			EAPPacket sendEapPacket = eapAuthenticator.process(receivedEapPacket);
			LogManager.getLogger().info(MODULE, "Sending inner Eap Packet: " + sendEapPacket);
			
			byte[] bytesBeforeEncryption = sendEapPacket.getEAPType().toBytes();
			return tlsAuthenticator.encrypt(bytesBeforeEncryption);

		} catch (SSLException e) {
			LogManager.getLogger().error(MODULE, innerType.name + " data not able to process. Reason:" + e.getMessage());
		} catch (InvalidEAPPacketException e) {
			LogManager.getLogger().error(MODULE, innerType.name + " data not able to process. Reason:" + e.getMessage());
		} catch (AuthenticationException e) {
			LogManager.getLogger().error(MODULE, innerType.name + " data not able to process. Reason:" + e.getMessage());
		}
		throw new AuthenticationException("PEAP-" + innerType.name + " Authentication fail");
	}
	
	private EAPPacket generateEapPacketFromReceivedData(byte[] eapData) throws InvalidEAPPacketException{
		EAPPacket receivedEapPacket = EAPPacketFactory.getInstance().createEAPPacket(EapPacketConstants.REQUEST.packetId);
		
		EAPType eapType = EAPTypeDictionary.getInstance().createEAPType(eapData[0]);
		eapType.setData(Arrays.copyOfRange(eapData, 1, eapData.length));
		
		receivedEapPacket.setEAPType(eapType);
		receivedEapPacket.setIdentifier(0);
		receivedEapPacket.resetLength();
		return receivedEapPacket;
	}

	@Override
	public void reset() throws InitializationFailedException {
		state = OuterMethodEvents.HANDSHAKE_MESSAGE_RECEIVED;
		tlsAuthenticator.reset();
		eapAuthenticator.innerReset();
	}

	@Override
	public byte[] getOutData() {
		return outData;
	}

	private void setOutData(byte[] outData) {
		this.outData = outData;
	}

}