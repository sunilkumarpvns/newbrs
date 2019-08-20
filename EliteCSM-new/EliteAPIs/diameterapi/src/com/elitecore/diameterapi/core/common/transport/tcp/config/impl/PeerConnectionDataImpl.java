package com.elitecore.diameterapi.core.common.transport.tcp.config.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;

import com.elitecore.core.commons.tls.EliteSSLParameter;
import com.elitecore.diameterapi.core.common.transport.constant.SecurityStandard;
import com.elitecore.diameterapi.core.common.transport.tcp.config.PeerConnectionData;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterConstants;
import com.elitecore.diameterapi.mibs.constants.TransportProtocols;

public class PeerConnectionDataImpl implements PeerConnectionData{

	private int remotePort = DiameterConstants.DIAMETER_SERVICE_PORT;
	private int localPort = 0;
	private int timeout = 3000;
	private TransportProtocols trasportProtocol = TransportProtocols.TCP;
	private String localIP = null;

	private String rempteIpAddress = "";
	private InetAddress remoteInetAddress;
	private InetAddress localInetAddress;

	private boolean isTcpNagleAlgoEnabled;
	private int socketSendBufferSize = -1;
	private int socketReceiveBufferSize = -1;
	
	private EliteSSLParameter eliteSSLParameter;
	
	private SecurityStandard securityStandard = SecurityStandard.NONE;
	
	/**
	 * @param port the port to set
	 */
	public void setRemotePort(int remotePort) {
		this.remotePort = remotePort;
	}

	/**
	 * @return the port
	 */
	public int getRemotePort() {
		return remotePort;
	}

	public void setPeerTimeout(int timeout) {
		this.timeout = timeout;
	}

	public int getPeerTimeout() {
		return timeout;
	}


	public void setTransportProtocol(TransportProtocols trasportProtocol ) {
			this.trasportProtocol = trasportProtocol;
	}
	public void setLocalIPAddress(String localIP ) {
		if(localIP != null && localIP.trim().length() > 0 )
			this.localIP = localIP; 
	}

	public TransportProtocols getTransportProtocol() {
		return trasportProtocol;
	}
	public String getLocalIPAddress() {
		return localIP;
	}
	
	public String getRemoteIPAddress() {
		return rempteIpAddress;
	}

	public InetAddress getLocalInetAddress() {
		return localInetAddress;
	}
	public InetAddress getRemoteInetAddress() {
		return remoteInetAddress;
	}
	
	@Override
	public void setLocalInetAddress(InetAddress localInetAddress){
		if(localInetAddress != null && localInetAddress.toString().trim().length() > 0)
			this.localInetAddress = localInetAddress;
	}
	
	
	public void setRemoteInetAddress(InetAddress hostInetAddress){
		if(hostInetAddress != null && hostInetAddress.toString().trim().length() > 0)
			this.remoteInetAddress = hostInetAddress;
	}

	public int getLocalPort() {
		return localPort;
	}
	
	@Override
	public void setLocalPort(int localPort){
		this.localPort = localPort;
	}

	public void setRemoteIPAddress(String remoteIpAddress) {
		this.rempteIpAddress =remoteIpAddress;

	}
	
	@Override
	public void setSocketSendBufferSize(int size){
		this.socketSendBufferSize = size;
	}
	
	@Override
	public void setSocketReceiveBufferSize(int size){
		this.socketReceiveBufferSize = size;
	}
	
	@Override
	public void setTCPNagleAlgo(boolean value){
		this.isTcpNagleAlgoEnabled = value; 
	}
	
	public int getSocketReceiveBufferSize(){
		return socketReceiveBufferSize;
	}
	
	public int getSocketSendBufferSize(){
		return socketSendBufferSize;
	}
	public boolean isNagleAlgoEnabled(){
		return isTcpNagleAlgoEnabled;
	}
	
	@Override
	public void setSSLParameter(EliteSSLParameter eliteSSLParameter) {
		this.eliteSSLParameter = eliteSSLParameter;
	}
	
	@Override
	public SecurityStandard getSecurityStandard() {
		return securityStandard;
	}

	@Override
	public void setSecurityStandard(SecurityStandard securityStandard) {
		this.securityStandard = securityStandard;
	}

	@Override
	public String toString(){
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter( stringBuffer);
		out.println("\tLocal Address: " + (this.localInetAddress !=null?this.localInetAddress:"")+ ":" + this.localPort);
		out.println("\tRemote Address: " + (this.remoteInetAddress!=null?this.remoteInetAddress:"")+ ":" + this.remotePort);
		out.println("\tTimeout: " + this.timeout);
		out.println("\tTransport Protocol: " + this.trasportProtocol);
		out.println("\tSocket Send buffer size: " + this.socketSendBufferSize);
		out.println("\tSocket Receive buffer size: " + this.socketReceiveBufferSize);
		out.println("\tTCP Nagle Algorithm: " + this.isTcpNagleAlgoEnabled);
		out.close();
		return stringBuffer.toString();
	}

	@Override
	public EliteSSLParameter getSSLParameter() {
		return eliteSSLParameter;
	}
}
