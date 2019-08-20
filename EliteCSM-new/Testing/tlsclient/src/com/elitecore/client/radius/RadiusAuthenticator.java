package com.elitecore.client.radius;

import java.io.File;
import java.net.DatagramPacket;

import javax.naming.AuthenticationException;

import com.elitecore.client.configuration.EapConfiguration;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

/**
 * RadiusPacketHandler processes the received Radius Packet and also generate RadiusPacket
 * 
 * @author Kuldeep Panchal
 * @author Malav Desai
 *
 */
public class RadiusAuthenticator {
	private static final String MODULE = "RADIUS_AUTHENTICATOR";
	private RadEapAuthenticator radEapAuthenticator;
	/**
	 * accessAccept will be true when Server send Authentication Success and used for SessionResumption to allow or not in UDPClient
	 */
	private boolean accessAccept;
	
	public RadiusAuthenticator(EapConfiguration configuration) throws InitializationFailedException {
		try {
			Dictionary.getInstance().loadDictionary(new File(System.getenv().get("UDP_CLIENT_HOME") + File.separator + "dictionary" + File.separator + "radius"));
		} catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Not able to load dictionary for radius");
		}
		radEapAuthenticator = new RadEapAuthenticator(configuration);
		setAccessAccept(false);
	}
	
	public void reset() throws InitializationFailedException{
		radEapAuthenticator.reset();
		setAccessAccept(false);
	}
	/**
	 * It process the received RadiusPacket and send it to the eligible authentication handler for further processing
	 * @param receivedRadiusPacket
	 * @return responseRadiusPacket
	 * @throws AuthenticationException
	 */
	public RadiusPacket processRequest(RadiusPacket receivedRadiusPacket) throws AuthenticationException {
		if(radEapAuthenticator.isEligible(receivedRadiusPacket)) {
			LogManager.getLogger().info(MODULE, "Request eligible for EAP authentication");
			return radEapAuthenticator.process(receivedRadiusPacket);
		}else{
			LogManager.getLogger().error(MODULE, "Request doesn't contain required for EAP handling so, request is not eligible for EAP Authentication.");
			throw new AuthenticationException("Request doesn't contain required for EAP handling so, request is not eligible for EAP Authentication.");
		}
	}
	
	public RadiusPacket generateRadiusPacketFromDatagram(DatagramPacket inPacket) {
		RadiusPacket inRadiusPacket = new RadiusPacket();
		inRadiusPacket.setBytes(inPacket.getData());
		return inRadiusPacket;
	}
	
	public boolean isProcessingRequired(RadiusPacket inRadiusPacket) {
		if(RadiusConstants.ACCESS_ACCEPT_MESSAGE == inRadiusPacket.getPacketType()) {
			setAccessAccept(true);
			return false;
		} else if(RadiusConstants.ACCESS_REJECT_MESSAGE == inRadiusPacket.getPacketType()) {
			return false;
		} else if(RadiusConstants.ACCESS_CHALLENGE_MESSAGE == inRadiusPacket.getPacketType() ){
			radEapAuthenticator.setStateAvp(inRadiusPacket.getRadiusAttribute(RadiusAttributeConstants.STATE));
		}
		return true;
	}
	/**
	 * Request identity is generated for bootstrapping TLSClient 
	 * @return identityRadiusPacket
	 */
	public RadiusPacket getRequestIdentity() {
		RadiusPacket identityRadiusPacket = radEapAuthenticator.getIdentityRadiusPacket();
		return identityRadiusPacket;
	}

	public boolean isAccessAccept() {
		return accessAccept;
	}

	public void setAccessAccept(boolean accessAccept) {
		this.accessAccept = accessAccept;
	}
}
