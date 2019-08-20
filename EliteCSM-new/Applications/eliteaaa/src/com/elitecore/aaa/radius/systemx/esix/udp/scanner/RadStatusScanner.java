package com.elitecore.aaa.radius.systemx.esix.udp.scanner;

import java.util.ArrayList;
import java.util.Arrays;

import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPRequest;
import com.elitecore.aaa.radius.systemx.esix.udp.RadiusExternalSystemData;
import com.elitecore.aaa.radius.systemx.esix.udp.impl.RadUDPRequestImpl;
import com.elitecore.aaa.radius.systemx.esix.udp.impl.RadUDPCommunicatorImpl;
import com.elitecore.aaa.radius.util.StringAttributeParser;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.policies.ParserException;
import com.elitecore.core.serverx.policies.ParserUtility;
import com.elitecore.core.systemx.esix.udp.UDPCommunicator;
import com.elitecore.core.systemx.esix.udp.UDPRequest;
import com.elitecore.core.systemx.esix.udp.UDPResponse;
import com.elitecore.core.systemx.esix.udp.UDPResponseListener;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.packet.IRadiusPacket;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.RadiusUtility;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

public class RadStatusScanner implements StatusScanner {

	private static final String MODULE = "RAD-STATUS-SCNR";
	private RadUDPCommunicatorImpl udpCommunicator;
	private RadiusPacket scanRequestPacket;
	private static final String REQ_TYPE = "type";
	private String radPacketStr;

	public RadStatusScanner(RadUDPCommunicatorImpl udpCommunicator) {
		this.udpCommunicator = udpCommunicator;
	}

	public RadStatusScanner(RadUDPCommunicatorImpl udpCommunicator, String radPacketStr) {
		this(udpCommunicator);
		this.radPacketStr = radPacketStr;
	}
	
	@Override
	public void init() throws InitializationFailedException{

		if(radPacketStr == null || radPacketStr.trim().length() == 0){
			scanRequestPacket = generateScannerPacket();
		}else{
			radPacketStr = radPacketStr.trim();
			try {
				scanRequestPacket = parseAndBuildPacket();
			} catch (ParserException e) {
				throw new InitializationFailedException(e);
			}
		}
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(MODULE, "Scanner Packet initialized for "  + 
					udpCommunicator.getCommunicatorContext().getExternalSystem().getName() + 
					"[" + udpCommunicator.getCommunicatorContext().getIPAddress() + ":" + 
					udpCommunicator.getCommunicatorContext().getExternalSystem().getPort() + "]");
			LogManager.getLogger().debug(MODULE, scanRequestPacket.toString());
		}
	}
	
	private RadiusPacket parseAndBuildPacket() throws ParserException {
		
		String[] packetParams = ParserUtility.splitString(radPacketStr, ',', ';');
		
		if(packetParams.length < 1){
			throw new ParserException("No Attributes configured for Scanner Packet.");
		}
		String typeString = packetParams[0].substring(0, packetParams[0].indexOf('='));
		if(REQ_TYPE.equalsIgnoreCase(typeString.trim()) == false){
			throw new ParserException("Request Type should be first parameter. Eg: " + REQ_TYPE + "=4");
		}
		String reqType = packetParams[0].substring(packetParams[0].indexOf('=') + 1, packetParams[0].length());
		if(reqType == null || reqType.trim().length() == 0){
			throw new ParserException("Request Type must have a Value");
		}
		
		ArrayList<IRadiusAttribute> requestAttributes = new ArrayList<IRadiusAttribute>();

		for(int i=1; i<packetParams.length; i++){
			IRadiusAttribute attribute = StringAttributeParser.parseString(packetParams[i].trim());
			if(attribute == null){
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "Unable to add Attribute: " + packetParams[i]);
				continue;
			}
			requestAttributes.add(attribute);
		}
		
		
		RadiusPacket scanRequestPacket = new RadiusPacket();
		
		scanRequestPacket.setPacketType(Integer.parseInt(reqType.trim()));
		scanRequestPacket.setIdentifier(RadiusUtility.generateIdentifier());
		if(requestAttributes != null && !requestAttributes.isEmpty()){
			final int size = requestAttributes.size();
			for(int i=0; i<size; i++){
				scanRequestPacket.addAttribute(requestAttributes.get(i));
			}
		}
		scanRequestPacket.setAuthenticator(generateRequestAuthenticator(Integer.parseInt(reqType.trim()), scanRequestPacket));
		scanRequestPacket.setClientPort(udpCommunicator.getCommunicatorContext().getPort());
		scanRequestPacket.setClientIP(udpCommunicator.getCommunicatorContext().getIPAddress());

		if (scanRequestPacket.getPacketType() != RadiusConstants.ACCESS_REQUEST_MESSAGE){
			return scanRequestPacket;
		}
		IRadiusAttribute password = scanRequestPacket.getRadiusAttribute(RadiusAttributeConstants.USER_PASSWORD);
		if (password == null){
			return scanRequestPacket;
		} 
		byte [] bEncrypatedPassword = RadiusUtility.encryptPasswordRFC2865(password.getStringValue(),scanRequestPacket.getAuthenticator(), 
				((RadiusExternalSystemData)udpCommunicator.getCommunicatorContext().getExternalSystem()).getSharedSecret());
		password.setValueBytes(bEncrypatedPassword);
		return scanRequestPacket;
	}

	private byte[] generateRequestAuthenticator(int requestType, IRadiusPacket packet) {
		
		switch(requestType){

		case RadiusConstants.ACCOUNTING_REQUEST_MESSAGE:
		case RadiusConstants.COA_REQUEST_MESSAGE:
			return RadiusUtility.generateRFC2866RequestAuthenticator(packet, ((RadiusExternalSystemData)udpCommunicator.getCommunicatorContext().getExternalSystem()).getSharedSecret());
		default:
			return RadiusUtility.generateRFC2865RequestAuthenticator();
		}
	}
	
	private RadiusPacket generateScannerPacket(){
		RadiusPacket scanRequestPacket = new RadiusPacket();
		scanRequestPacket.setPacketType(RadiusConstants.STATUS_SERVER_MESSAGE);
		scanRequestPacket.setIdentifier(RadiusConstants.STATUS_SERVER_MESSAGE_RESERVED_ID);
		scanRequestPacket.setAuthenticator(RadiusUtility.generateRFC2865RequestAuthenticator());
		IRadiusAttribute messageAuthenticatorAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.MESSAGE_AUTHENTICATOR);
		byte[] zeroBytes = new byte[16];
		Arrays.fill(zeroBytes, (byte)0);
		messageAuthenticatorAttribute.setValueBytes(zeroBytes);
		scanRequestPacket.addAttribute(messageAuthenticatorAttribute);
		scanRequestPacket.refreshPacketHeader();		

		byte[] resultBytes = RadiusUtility.HMAC("MD5", scanRequestPacket.getBytes(), ((RadiusExternalSystemData)udpCommunicator.getCommunicatorContext().getExternalSystem()).getSharedSecret());
		scanRequestPacket.getRadiusAttribute(String.valueOf(RadiusAttributeConstants.MESSAGE_AUTHENTICATOR)).setValueBytes(resultBytes);
		return scanRequestPacket;
	}

	@Override
	public void scan() {
		scanRequestPacket.refreshPacketHeader();
		RadUDPRequest udpRequest = new RadUDPRequestImpl(scanRequestPacket.getBytes(), 
				((RadiusExternalSystemData)udpCommunicator.getCommunicatorContext().getExternalSystem()).getSharedSecret(), 
				new UDPResponseListener() {

			@Override
			public void responseReceived(UDPRequest udpRequest,UDPResponse udpResponse) {
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
					LogManager.getLogger().info(MODULE, "Response received from " + 
							udpCommunicator.getCommunicatorContext().getExternalSystem().getName() + 
							"[" + udpCommunicator.getCommunicatorContext().getIPAddress() + ":" + 
							udpCommunicator.getCommunicatorContext().getExternalSystem().getPort() + 
							"], marking System as alive");
				udpCommunicator.markAlive();
			}

			@Override
			public void requestTimeout(UDPRequest udpRequest, UDPCommunicator udpCommunicator) {
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
					LogManager.getLogger().info(MODULE, "System: "+ 
							udpCommunicator.getCommunicatorContext().getExternalSystem().getName() + 
							"[" + udpCommunicator.getCommunicatorContext().getIPAddress() + ":" + 
							udpCommunicator.getCommunicatorContext().getExternalSystem().getPort() + 
							"] is dead");
				RadStatusScanner.this.udpCommunicator.markDead();
			}

			@Override
			public void requestDropped(UDPRequest udpRequest,
					UDPCommunicator udpCommunicator) {
			}
		});
		udpCommunicator.handleUDPRequest(udpRequest);
	}


}
