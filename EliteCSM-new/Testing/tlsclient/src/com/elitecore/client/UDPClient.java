package com.elitecore.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import javax.naming.AuthenticationException;

import com.elitecore.client.configuration.EapConfiguration;
import com.elitecore.client.radius.RadiusAuthenticator;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.config.core.ConfigurationContext;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
/**
 * UDPClient is used to handle sending and receiving UDP Packets to and from the AAA server.
 * Radius Packet are transmitted between them
 * 
 * @author Kuldeep Panchal
 * @author Malav Desai
 *
 */
public class UDPClient {
	private static final String MODULE = "UDP-CLIENT";
	private DatagramSocket socket;
	private DatagramPacket responsePacket;
	private DatagramPacket requestPacket;
	private RadiusAuthenticator radiusAuthenticator;
	private String serverIP;
	private int serverPort;
	private EapConfiguration configuration;
	private ConfigurationContext configurationContext;
	private long timestamp = 0;

	public UDPClient() throws LoadConfigurationException, InitializationFailedException {
		String clientHome = System.getenv().get("UDP_CLIENT_HOME");
		if(clientHome == null){
			LogManager.getLogger().error(MODULE, "Cannot start client, UDP_CLIENT_HOME is not set.");
			System.exit(2);
		}
		configurationContext = new ConfigurationContext(clientHome);
		configuration = (EapConfiguration) configurationContext.read(EapConfiguration.class);
		LogManager.getLogger().info(MODULE, "EAP Configuration: " + configuration);
		radiusAuthenticator = new RadiusAuthenticator(getConfiguration());
		this.serverIP = getConfiguration().getServerIp();
		this.serverPort = getConfiguration().getPort();
	}
	
	public UDPClient(String sourceIP, String destIP,int destPort) {
		this.serverIP = destIP;
		this.serverPort = destPort;
	}

	public static void main(String args[]) throws LoadConfigurationException, InitializationFailedException {
		UDPClient udpClient = new UDPClient();
		try {
			SocketAddress socketAddress = new InetSocketAddress(udpClient.getConfiguration().getClietnIp(), 0);
			udpClient.socket = new DatagramSocket(socketAddress);
			udpClient.socket.setSoTimeout(udpClient.configuration.getRequestTimeoutInSecs() * 1000);
			try {
				RadiusPacket identityRadiusPacket = udpClient.getRequestIdentity();
				LogManager.getLogger().info(udpClient.MODULE, "Request Identity Received: " + identityRadiusPacket.toString());
				udpClient.handleAuthenticationRequest(identityRadiusPacket);
				udpClient.sendResponse();
				udpClient.startListening();
			} catch (Exception e) {
				LogManager.getLogger().error(udpClient.MODULE, "Error Handling Request, Reason: " + e.getMessage());
				return;
			}
		} catch (Exception ex) {
			LogManager.getLogger().error(udpClient.MODULE, "Datagram Socket cannot be created, Reason: " + ex.getMessage());
			return;
		}
	}

	private RadiusPacket getRequestIdentity() {
		return radiusAuthenticator.getRequestIdentity();
	}
	/**
	 * Getting ready to start listening and waiting for the request
	 * @throws IOException
	 */
	public void startListening() {
		requestPacket = new DatagramPacket(new byte[2048], 2048);
		while(true) {
			LogManager.getLogger().info(MODULE, "Waiting for the request..");
			try {
				socket.receive(requestPacket);
				timestamp = System.currentTimeMillis();
				try {
					RadiusPacket requestRadiusPacket = radiusAuthenticator.generateRadiusPacketFromDatagram(requestPacket);
					LogManager.getLogger().info(MODULE, "Response Received from server: " + requestRadiusPacket.toString());
					if(radiusAuthenticator.isProcessingRequired(requestRadiusPacket)){
						handleAuthenticationRequest(requestRadiusPacket);
						sendResponse();
					}else {
						if(radiusAuthenticator.isAccessAccept()) {
							LogManager.getLogger().info(MODULE, "Received Accept so skipping radius request processing and resetting the handler for session resumption");
							radiusAuthenticator.reset();
							Thread.sleep(getConfiguration().getTls().getSessionResumptionDuration());
							handleAuthenticationRequest(getRequestIdentity());
							sendResponse();
						} else {
							radiusAuthenticator.reset();
							LogManager.getLogger().info(MODULE, "Access Reject received from server");
							throw new AuthenticationException("Authentication Failed, Reason: Access Reject");
						}
					}
					LogManager.getLogger().info(MODULE, "Time taken to respond : " + (System.currentTimeMillis() - timestamp) + " MS");
				} catch (Exception e) {
					LogManager.getLogger().trace(e);
					LogManager.getLogger().error(MODULE, "Error Handling Request, Reason: " + e.getMessage());
					return;
				}
			} catch (IOException e1) {
				LogManager.getLogger().trace(e1);
				LogManager.getLogger().info(MODULE, "Datagram socket cannot receieve packet, Reason: " + e1.getMessage());
				return;
			}
		}
	}
	/**
	 * It handles received request
	 * @param responseReceived
	 * @return
	 * @throws UnknownHostException 
	 * @throws Exception
	 */
	public RadiusPacket handleAuthenticationRequest(RadiusPacket responseReceived) throws AuthenticationException, UnknownHostException{
		RadiusPacket responseRadiusPacket = radiusAuthenticator.processRequest(responseReceived);
		LogManager.getLogger().info(MODULE, "Request generated: " + responseRadiusPacket);
		responsePacket = new DatagramPacket(responseRadiusPacket.getBytes(), responseRadiusPacket.getBytes().length,InetAddress.getByName(serverIP),serverPort);
		return responseRadiusPacket;
	}

	public void sendResponse() throws IOException {
		socket.send(responsePacket);
	}
	public EapConfiguration getConfiguration() {
		return configuration;
	}
	public void setConfiguration(EapConfiguration configuration) {
		this.configuration = configuration;
	}
}
