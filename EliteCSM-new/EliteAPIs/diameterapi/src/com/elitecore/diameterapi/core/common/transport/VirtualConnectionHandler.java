package com.elitecore.diameterapi.core.common.transport;

import com.elitecore.core.commons.tls.EliteSSLContextExt;
import com.elitecore.core.commons.util.constants.CommonConstants;
import com.elitecore.diameterapi.core.common.packet.Packet;
import com.elitecore.diameterapi.core.common.transport.tcp.ConnectionEvents;
import com.elitecore.diameterapi.core.common.transport.tcp.config.PeerConnectionData;
import com.elitecore.diameterapi.core.common.transport.tcp.exception.HandShakeFailException;
import com.elitecore.diameterapi.diameter.common.data.PeerData;
import com.elitecore.diameterapi.diameter.stack.DiameterStack;
import com.elitecore.diameterapi.mibs.constants.SecurityProtocol;

import java.net.InetAddress;

public class VirtualConnectionHandler implements NetworkConnectionHandler {
	private VirtualOutputStream outputStream;
	private PeerData peerData;
	private VirtualInputStream inputStream;
	
	public VirtualConnectionHandler(final DiameterStack stack, PeerData peerData, VirtualOutputStream outputStream) {
		this.outputStream = outputStream;
		this.peerData = peerData;
		this.inputStream = packet -> stack.handleReceivedMessage(packet, VirtualConnectionHandler.this);
	}
	
	@Override
	public void send(Packet packet) {
		outputStream.send(packet);
	}

	@Override
	public boolean isConnected() {
		return true;
	}

	@Override
	public void addNetworkConnectionEventListener(
			NetworkConnectionEventListener networkConnectionEventListener) {
		//No need to handle
	}

	@Override
	public void closeConnection(ConnectionEvents event) {
		// No need to close connection
	}

	@Override
	public boolean isResponder() {
		return true;
	}

	@Override
	public String getSourceIpAddress() {
		return peerData.getRemoteIPAddress();
	}

	@Override
	public int getSourcePort() {
		return peerData.getRemotePort();
	}

	@Override
	public String getHostName() {
		return peerData.getHostIdentity();
	}

	@Override
	public void setHostName(String hostName) {
		//No need to set hot name
	}

	@Override
	public String getLocalAddress() {
		if (peerData.getLocalIPAddress() == null) {
			return CommonConstants.UNIVERSAL_IP;
		}
		return peerData.getLocalIPAddress();
	}
	
	public VirtualInputStream getInputStream(){
		return inputStream;
	}
	
	@Override
	public void secureConnection(PeerConnectionData peerData, EliteSSLContextExt eliteSSLContext) throws HandShakeFailException {
		throw new HandShakeFailException("Secure connection unsuppored in virtual connection");
		
	}
	@Override
	public int getLocalPort() {
		return peerData.getLocalPort();
	}
	@Override
	public SecurityProtocol getSecurityProtocol() {
		return SecurityProtocol.NONE;
	}
	@Override
	public void terminateConnection() {
		//no need to handler
	}
	@Override
	public InetAddress getSourceInetAddress() {
		return peerData.getRemoteInetAddress();
	}
}
