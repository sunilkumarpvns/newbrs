package com.elitecore.coreradius.client.radius;

import java.net.InetAddress;

import com.elitecore.coreradius.client.base.BaseRadiusClient;
import com.elitecore.coreradius.commons.RadiusGeneralException;
import com.elitecore.coreradius.commons.packet.IRadiusPacket;
import com.elitecore.coreradius.commons.util.RadiusUtility;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

public class RadiusAuthClient extends BaseRadiusClient {
	
	public RadiusAuthClient(){
		
	}
	public RadiusAuthClient(InetAddress serverAddress, int serverPort, String sharedSecret) {
		this(serverAddress, serverPort, RadiusConstants.ACCESS_REQUEST_MESSAGE, sharedSecret, DEFAULT_TIMEOUT);
	}

	public RadiusAuthClient(InetAddress serverAddress, int serverPort, int requestType, String sharedSecret, int timeout) {
		super(serverAddress, serverPort, requestType, sharedSecret, timeout);
	}

	@Override
	protected byte[] generateRequestAuthenticator(IRadiusPacket requestPacket, String sharedSecret) {
		return RadiusUtility.generateRFC2865RequestAuthenticator();
	}
	@Override
	protected byte[] generateResponseAuthenticator(IRadiusPacket responsePacket, byte[] requestAuthenticator, String sharedSecret) throws RadiusGeneralException {
		return RadiusUtility.generateRFC2865ResponseAuthenticator(responsePacket, requestAuthenticator, sharedSecret);
	}


}
