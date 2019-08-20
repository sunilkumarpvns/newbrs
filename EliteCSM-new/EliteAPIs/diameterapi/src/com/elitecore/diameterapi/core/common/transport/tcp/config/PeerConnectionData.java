package com.elitecore.diameterapi.core.common.transport.tcp.config;

import java.net.InetAddress;

import com.elitecore.core.commons.tls.EliteSSLParameter;
import com.elitecore.diameterapi.core.common.transport.constant.SecurityStandard;
import com.elitecore.diameterapi.mibs.constants.TransportProtocols;

public interface PeerConnectionData {

	
	public int getRemotePort();
	public int getPeerTimeout();
	public TransportProtocols getTransportProtocol();
	public String getLocalIPAddress();
	public String getRemoteIPAddress();
	public InetAddress getLocalInetAddress();
	public InetAddress getRemoteInetAddress();
	public int getLocalPort();
	public int getSocketReceiveBufferSize();	
	public int getSocketSendBufferSize();
	public boolean isNagleAlgoEnabled();
	public EliteSSLParameter getSSLParameter();
	public SecurityStandard getSecurityStandard();
	
	
	//setter
	public void setRemoteInetAddress(InetAddress hostInetAddress);
	public void setRemoteIPAddress(String remoteIpAddress);
	public void setRemotePort(int remotePort);
	public void setPeerTimeout(int peerTimeout);
	public void setTransportProtocol(TransportProtocols transportProtocols);
	public void setLocalIPAddress(String localIPAddress);
	
	public void setLocalPort(int port);
	public void setSocketReceiveBufferSize(int socketReceiveBuffer);	
	public void setSocketSendBufferSize(int socketSendBufferSize);
	public void setTCPNagleAlgo(boolean enabled);
	
	public void setSSLParameter(EliteSSLParameter eliteSSLParameter);
	public void setSecurityStandard(SecurityStandard securityStandard);
	public void setLocalInetAddress(InetAddress localInetAddress);
}
