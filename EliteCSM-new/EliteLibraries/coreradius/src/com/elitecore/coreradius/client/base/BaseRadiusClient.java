package com.elitecore.coreradius.client.base;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.List;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreradius.client.CommunicationException;
import com.elitecore.coreradius.client.RequestTimeoutException;
import com.elitecore.coreradius.commons.RadiusGeneralException;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.packet.IRadiusPacket;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.RadiusUtility;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

public abstract class BaseRadiusClient implements IRadiusClient{
	
	private static final String MODULE = "RADIUS CLIENT";
	protected static final int DEFAULT_TIMEOUT = 10000;
	private static final int MAX_RADIUS_PACKET_LENGTH = 4096;
	
	private InetAddress serverAddress;
	private int serverPort;
	private int requestType;
	private String sharedSecret;
	private int timeout;
	private DatagramSocket datagramSocket;
	
	public BaseRadiusClient(){
		
	}
	
	public BaseRadiusClient(InetAddress serverAddress, int serverPort, int requestType, String sharedSecret) {
		this(serverAddress, serverPort, requestType, sharedSecret, DEFAULT_TIMEOUT);
	}

	public BaseRadiusClient(InetAddress serverAddress, int serverPort, int requestType, String sharedSecret, int timeout) {
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
		this.requestType = requestType;
		this.sharedSecret = sharedSecret;
		this.timeout = timeout;
	}
	
	public final synchronized void openSocket() throws CommunicationException {
		if (datagramSocket == null || datagramSocket.isClosed()) {
			datagramSocket = null;
			try {
				datagramSocket = new DatagramSocket();
				datagramSocket.setSoTimeout(timeout);
			} catch (SocketException e) {
				throw new CommunicationException("Problem opening socket : " + e.getMessage(),e);
			}
		}
	}
	
	public final synchronized void closeSocket() throws CommunicationException {
		if (datagramSocket != null && !datagramSocket.isClosed()) {
			datagramSocket.close();
			datagramSocket = null;
		}
	} 
	
	/**
	 * Creates a Radius Request packet with provided attributes.
	 * 
	 */
	public IRadiusPacket createRadiusRequestPacket(List<IRadiusAttribute> requestAttributes){
		RadiusPacket radiusRequestPacket = new RadiusPacket();
	
		radiusRequestPacket.setPacketType(getRequestType());
		radiusRequestPacket.setIdentifier(RadiusUtility.generateIdentifier());
		if(requestAttributes != null && !requestAttributes.isEmpty()){
			final int size = requestAttributes.size();
			for(int i=0; i<size; i++){
				radiusRequestPacket.addAttribute(requestAttributes.get(i));
			}
		}
		radiusRequestPacket.refreshPacketHeader();
		radiusRequestPacket.setAuthenticator(generateRequestAuthenticator(radiusRequestPacket, getSharedSecret()));
		radiusRequestPacket.setClientIP(getServerAddress().getHostAddress());
		radiusRequestPacket.setClientPort(getServerPort());
		
		if (radiusRequestPacket.getPacketType() == RadiusConstants.ACCESS_REQUEST_MESSAGE){
			IRadiusAttribute password = radiusRequestPacket.getRadiusAttribute(RadiusAttributeConstants.USER_PASSWORD);
			if (password != null){
				byte [] bEncrypatedPassword = RadiusUtility.encryptPasswordRFC2865(new String(password.getValueBytes()),radiusRequestPacket.getAuthenticator(),getSharedSecret());
				radiusRequestPacket.getRadiusAttribute(RadiusAttributeConstants.USER_PASSWORD).setValueBytes(bEncrypatedPassword);
				radiusRequestPacket.refreshPacketHeader();
			}
		}
		return radiusRequestPacket;
	}
	
	/**
	 * Sends the request Packet to Radius and receives Response back. 
	 * @throws RadiusGeneralException 
	 * 
	 */
	public IRadiusPacket sendReceiveRadiusPacket(IRadiusPacket radiusRequestPacket) throws RequestTimeoutException, CommunicationException, IOException, RadiusGeneralException {
		RadiusPacket radiusResponsePacket = null;
		if (radiusRequestPacket != null){
	
			DatagramPacket packetOut = new DatagramPacket(radiusRequestPacket.getBytes(), radiusRequestPacket.getBytes().length, getServerAddress(), getServerPort());
			DatagramPacket packetIn = new DatagramPacket(new byte[MAX_RADIUS_PACKET_LENGTH],MAX_RADIUS_PACKET_LENGTH);
			
			getDatagramSocket().send(packetOut);
			getDatagramSocket().receive(packetIn);
			
			// Validating Response Packet
			String ipAddress = packetIn.getAddress().getHostAddress();
			
			if( radiusRequestPacket.getClientIP().equalsIgnoreCase(ipAddress)){
				radiusResponsePacket = new RadiusPacket();
				radiusResponsePacket.readFrom(new ByteArrayInputStream(packetIn.getData()));
				radiusResponsePacket.setClientIP(packetIn.getAddress().getHostAddress());
				radiusResponsePacket.setClientPort(packetIn.getPort());
				
				if (!validateResponsePacket(radiusResponsePacket, radiusRequestPacket)){
					return null;
				}
			}else{
				new IOException("Received packet from unknown server.");
			}
		}else{
			new IOException("Problem getting response from Server.");
		}
		return radiusResponsePacket;
	}
	
	protected abstract byte[] generateRequestAuthenticator(IRadiusPacket requestPacket, String sharedSecret);
	
	protected abstract byte[] generateResponseAuthenticator(IRadiusPacket responsePacket, byte[] requestAuthenticator, String sharedSecret) throws RadiusGeneralException;
	
	public int getRequestType() {
		return requestType;
	}
	public void setRequestType(int requestType) {
		this.requestType = requestType;
	}
	public InetAddress getServerAddress() {
		return serverAddress;
	}
	public void setServerAddress(InetAddress serverAddress) {
		this.serverAddress = serverAddress;
	}
	public int getServerPort() {
		return serverPort;
	}
	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}
	public String getSharedSecret() {
		return sharedSecret;
	}
	public void setSharedSecret(String sharedSecret) {
		this.sharedSecret = sharedSecret;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public DatagramSocket getDatagramSocket() {
		return datagramSocket;
	}

	public void setDatagramSocket(DatagramSocket datagramSocket) {
		this.datagramSocket = datagramSocket;
	}
	
	/**
	 * Validates the Response Packet received from Server.
	 * @param responsePacket
	 * @param requestPacket
	 * @return
	 * @throws RadiusGeneralException
	 */
	private boolean validateResponsePacket(IRadiusPacket responsePacket, IRadiusPacket requestPacket) throws RadiusGeneralException{
		RadiusPacket tempPacket = new RadiusPacket();
		
		tempPacket.setBytes(responsePacket.getBytes());
		tempPacket.setAuthenticator(new byte[16]);
		tempPacket.refreshPacketHeader();
		
		byte [] authenticator = generateResponseAuthenticator(tempPacket, requestPacket.getAuthenticator(), getSharedSecret());

		if (RadiusUtility.isByteArraySame(authenticator, responsePacket.getAuthenticator())){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Response Authenticator validated.");
			if (responsePacket.getIdentifier() == requestPacket.getIdentifier()){
				return true;
			}else{
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Identifier not matched for Response.");
			}	
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Response Authenticator validation failed.");
		}
		return false;
	}
}
