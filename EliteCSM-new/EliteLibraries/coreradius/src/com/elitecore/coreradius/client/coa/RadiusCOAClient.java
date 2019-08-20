package com.elitecore.coreradius.client.coa;

import java.net.InetAddress;

import com.elitecore.coreradius.client.base.BaseRadiusClient;
import com.elitecore.coreradius.commons.RadiusGeneralException;
import com.elitecore.coreradius.commons.packet.IRadiusPacket;
import com.elitecore.coreradius.commons.util.RadiusUtility;

/**
 * Radius COA Client can be used to Send Radius Request of COA type to Radius Server. 
 * 
 *
 */

public class RadiusCOAClient extends BaseRadiusClient {

	public RadiusCOAClient(){
	}
	
	public RadiusCOAClient(InetAddress serverAddress, int serverPort, int requestType, String sharedSecret) {
		super(serverAddress, serverPort, requestType, sharedSecret);
	}
	
	public RadiusCOAClient(InetAddress serverAddress, int serverPort, int requestType, String sharedSecret, int timeout) {
		super(serverAddress, serverPort, requestType, sharedSecret, timeout);
	}
	
	@Override
	protected byte[] generateRequestAuthenticator(IRadiusPacket requestPacket, String sharedSecret) {
		return RadiusUtility.generateRFC2866RequestAuthenticator(requestPacket,sharedSecret);
	}

	@Override
	protected byte[] generateResponseAuthenticator(IRadiusPacket responsePacket, byte[] requestAuthenticator, String sharedSecret) throws RadiusGeneralException {
		return RadiusUtility.generateRFC2865ResponseAuthenticator(responsePacket, requestAuthenticator, sharedSecret);
	}

}
