package com.elitecore.aaa.radius.systemx.esix.udp.scanner;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.systemx.esix.udp.impl.UDPCommunicatorImpl;
import com.elitecore.coreradius.commons.util.RadiusUtility;

public class UDPScanner implements StatusScanner {

	private static final String MODULE = "UDP-SCANNER";
	private DatagramSocket socket;
	private DatagramPacket scanPacket;
	private UDPCommunicatorImpl udpCommunicator;
	private String udpBytesStr;
	private final DatagramPacket response = new DatagramPacket(new byte[4000], 4000);
	private static final int SOCKET_IDLE_TIMEOUT_MS=2000;
	
	public UDPScanner(UDPCommunicatorImpl udpCommunicator, String udpBytesStr) {
		this.udpCommunicator = udpCommunicator;
		this.udpBytesStr = udpBytesStr;
	}
	
	@Override
	public void init() throws InitializationFailedException {
		
		if(udpBytesStr == null || udpBytesStr.trim().length() == 0){
			throw new InitializationFailedException("Bytes not Provided.");
		}
		udpBytesStr = udpBytesStr.trim();
		byte[] udpBytes = null;
		if(udpBytesStr.startsWith("0x") || udpBytesStr.startsWith("0X")) {
			udpBytes = RadiusUtility.getBytesFromHexValue(udpBytesStr);
		}else{
			udpBytes = RadiusUtility.getBytesFromHexValue("0x"+udpBytesStr);
		}
		
		if(udpBytes == null){
			throw new InitializationFailedException("Unable to generate bytes from: " + udpBytesStr);
		}
		try {
			socket = new DatagramSocket(0);
			socket.setSoTimeout(SOCKET_IDLE_TIMEOUT_MS);
			scanPacket = new DatagramPacket(udpBytes, udpBytes.length,
					InetAddress.getByName(udpCommunicator.getCommunicatorContext().getIPAddress()),
					udpCommunicator.getCommunicatorContext().getPort());
		} catch (Exception e) {
			throw new InitializationFailedException(e);
		}
	}

	@Override
	public void scan() {
		try {
			socket.send(scanPacket);
			socket.receive(response);
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Response recieved from System: " + udpCommunicator.getCommunicatorContext().getExternalSystem().getName() + 
						"[" + udpCommunicator.getCommunicatorContext().getIPAddress() + ":" + 
						udpCommunicator.getCommunicatorContext().getExternalSystem().getPort() + 
						"], Marking it Alive");
			udpCommunicator.markAlive();
		} catch (IOException e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Marking System "+ udpCommunicator.getCommunicatorContext().getExternalSystem().getName() + 
						"[" + udpCommunicator.getCommunicatorContext().getIPAddress() + ":" + 
						udpCommunicator.getCommunicatorContext().getExternalSystem().getPort() + 
						"] DEAD, Reason: " + e.getMessage());
			udpCommunicator.markDead();
		}
	}

}
