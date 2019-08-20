/**
 * 
 */
package com.elitecore.diameterapi.core.common.transport;

import java.io.IOException;
import java.net.InetAddress;

import com.elitecore.core.commons.tls.EliteSSLContextExt;
import com.elitecore.diameterapi.core.common.packet.Packet;
import com.elitecore.diameterapi.core.common.transport.tcp.ConnectionEvents;
import com.elitecore.diameterapi.core.common.transport.tcp.config.PeerConnectionData;
import com.elitecore.diameterapi.core.common.transport.tcp.exception.HandShakeFailException;
import com.elitecore.diameterapi.mibs.constants.SecurityProtocol;

/**
 * @author pulin
 *
 */
public interface NetworkConnectionHandler {
	
	public void send(Packet Packet) throws IOException;
	
	public boolean isConnected();
	
	public void addNetworkConnectionEventListener(NetworkConnectionEventListener networkConnectionEventListener);
	
	public void closeConnection(ConnectionEvents event);
	
	public boolean isResponder();
	
	public String getSourceIpAddress();
	public int getSourcePort();
	
	public String getHostName();

	public void setHostName(String hostName);
	
	public String getLocalAddress();
	
	
	public void secureConnection(PeerConnectionData peerData, EliteSSLContextExt eliteSSLContext) throws HandShakeFailException;

	public int getLocalPort();

	public SecurityProtocol getSecurityProtocol();

	void terminateConnection();

	InetAddress getSourceInetAddress();
}
