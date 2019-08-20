package com.elitecore.diameterapi.core.common.transport;

import java.io.IOException;
import java.net.InetAddress;

import com.elitecore.core.commons.tls.EliteSSLContextExt;
import com.elitecore.diameterapi.core.common.packet.Packet;
import com.elitecore.diameterapi.core.common.transport.tcp.DiameterInputStream;
import com.elitecore.diameterapi.mibs.constants.SecurityProtocol;

public interface Connection {
	
	public static final int DEFAULT_TIMEOUT_IN_MS = 5000;
	String getSourceIpAddress();

	int getSourcePort();
	
	String getLocalAddress();
	
	boolean isConnected();
	
	void write(Packet packet) throws IOException;
	
	void read(byte [] data, int off ,int len);

	String getClientAddress();

	int getClientPort();
	
	DiameterInputStream getInputStream();

	void closeConnection();
		
	boolean isClosed();
	boolean isInputShutdown();
	boolean isOutputShutdown();
	
	EliteSSLContextExt getEliteSSLContext();

	public int getLocalPort();

	SecurityProtocol getSecurityProtocol();

	InetAddress getSourceInetAddress();
	
}
