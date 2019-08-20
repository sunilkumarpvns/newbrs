package com.elitecore.coreradius.client.radius;

import java.net.InetAddress;

import com.elitecore.coreradius.client.base.BaseRadiusClient;
import com.elitecore.coreradius.commons.RadiusGeneralException;
import com.elitecore.coreradius.commons.packet.IRadiusPacket;
import com.elitecore.coreradius.commons.util.RadiusUtility;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

public class RadiusAcctClient extends BaseRadiusClient {

	public RadiusAcctClient(){
		
	}
	
	public RadiusAcctClient(InetAddress serverAddress, int serverPort, int requestType, String sharedSecret) {
		this(serverAddress, serverPort, RadiusConstants.ACCOUNTING_REQUEST_MESSAGE, sharedSecret, DEFAULT_TIMEOUT);
	}

	public RadiusAcctClient(InetAddress serverAddress, int serverPort, int requestType, String sharedSecret, int timeout) {
		super(serverAddress, serverPort, requestType, sharedSecret, timeout);
	}

	@Override
	protected byte[] generateRequestAuthenticator(IRadiusPacket acctRequestPacket, String sharedSecret) {
		return RadiusUtility.generateRFC2866RequestAuthenticator(acctRequestPacket,sharedSecret);
	}

	@Override
	protected byte[] generateResponseAuthenticator(IRadiusPacket responsePacket, byte[] requestAuthenticator, String sharedSecret) throws RadiusGeneralException {
		return RadiusUtility.generateRFC2865ResponseAuthenticator(responsePacket, requestAuthenticator, sharedSecret);
	}

}
