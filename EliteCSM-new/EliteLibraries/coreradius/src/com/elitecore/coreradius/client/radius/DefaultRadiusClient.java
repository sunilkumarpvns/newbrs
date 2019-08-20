package com.elitecore.coreradius.client.radius;

import java.net.InetAddress;

import com.elitecore.coreradius.client.base.BaseRadiusClient;
import com.elitecore.coreradius.commons.RadiusGeneralException;
import com.elitecore.coreradius.commons.packet.IRadiusPacket;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

public class DefaultRadiusClient extends BaseRadiusClient{
	
	public DefaultRadiusClient(){
		
	}
	
	public DefaultRadiusClient(InetAddress serverAddress, int serverPort, int requestType, String sharedSecret) {
		this(serverAddress, serverPort, RadiusConstants.ACCOUNTING_REQUEST_MESSAGE, sharedSecret, DEFAULT_TIMEOUT);
	}

	public DefaultRadiusClient(InetAddress serverAddress, int serverPort, int requestType, String sharedSecret, int timeout) {
		super(serverAddress, serverPort, requestType, sharedSecret, timeout);
	}

	@Override
	protected byte[] generateRequestAuthenticator(IRadiusPacket requestPacket,String sharedSecret) {
		return new byte[16];
	}

	@Override
	protected byte[] generateResponseAuthenticator(IRadiusPacket responsePacket, byte[] requestAuthenticator, String sharedSecret) throws RadiusGeneralException {
		return new byte[16];
	}

}
