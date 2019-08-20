package com.elitecore.diameterapi.diameter.common.routerx.agent.data;

import java.net.InetAddress;

import com.elitecore.core.commons.tls.EliteSSLParameter;
import com.elitecore.diameterapi.core.common.transport.constant.SecurityStandard;
import com.elitecore.diameterapi.diameter.common.data.impl.PeerDataImpl;
import com.elitecore.diameterapi.diameter.common.util.constant.RedirectHostAVPFormat;
import com.elitecore.diameterapi.mibs.constants.TransportProtocols;

public class PeerDataProvider {

	private PeerDataImpl peerData;
	public PeerDataProvider() {
		peerData = new PeerDataImpl();
	}
	
	public PeerDataImpl build(){
		return peerData;
	}
	
	public PeerDataProvider withRemotePort(int remotePort) {
		peerData.setRemotePort(remotePort);
		return this;
	}
	
	public PeerDataProvider withRemoteIPAddress(String remoteIpAddress) {
		peerData.setRemoteIPAddress(remoteIpAddress);
		return this;
	}
	
	public PeerDataProvider withNagleAlgo(boolean isNagleAlgoEnabled) {
		peerData.setTCPNagleAlgo(isNagleAlgoEnabled);
		return this;
	}
	
	public PeerDataProvider withTransportProtocol(TransportProtocols trasportProtocol) {
		peerData.setTransportProtocol(trasportProtocol);
		return this;
	}
	
	public PeerDataProvider withSocketSendBufferSize(int size) {
		peerData.setSocketSendBufferSize(size);
		return this;
	}
	
	public PeerDataProvider withSocketReceiveBufferSize(int size) {
		peerData.setSocketReceiveBufferSize(size);
		return this;
	}
	
	public PeerDataProvider withSecurityStandard(SecurityStandard securityStandard) {
		peerData.setSecurityStandard(securityStandard);
		return this;
	}
	
	public PeerDataProvider withSSLParameter(EliteSSLParameter eliteSSLParameter) {
		peerData.setSSLParameter(eliteSSLParameter);
		return this;
	}
	
	public PeerDataProvider withRemoteInetAddress(InetAddress hostInetAddress) {
		peerData.setRemoteInetAddress(hostInetAddress);
		return this;
	}
	
	public PeerDataProvider withPeerTimeout(int timeout) {
		peerData.setPeerTimeout(timeout);
		return this;
	}
	
	public PeerDataProvider withLocalPort(int localPort) {
		peerData.setLocalPort(localPort);
		return this;
	}
	
	public PeerDataProvider withLocalInetAddress(InetAddress localInetAddress) {
		peerData.setLocalInetAddress(localInetAddress);
		return this;
	}
	
	public PeerDataProvider withLocalIPAddress(String localIP) {
		peerData.setLocalIPAddress(localIP);
		return this;
	}
	
	public PeerDataProvider withSessionCleanUpOnDPR(boolean isSessionCleanUpOnDPR) {
		peerData.setSessionCleanUpOnDPR(isSessionCleanUpOnDPR);
		return this;
	}
	
	public PeerDataProvider withSessionCleanUpOnCER(boolean isSessionCleanUpOnCER) {
		peerData.setSessionCleanUpOnCER(isSessionCleanUpOnCER);
		return this;
	}
	
	public PeerDataProvider withSendDPRonCloseEvent(boolean sendDPRonCloseEnabled) {
		peerData.setSendDPRonCloseEvent(sendDPRonCloseEnabled);
		return this;
	}
	
	public PeerDataProvider withInitConnection(int initiateConnectionDuration) {
		peerData.setInitiateConnectionDuration(initiateConnectionDuration);
		return this;
	}
	
	public PeerDataProvider withFollowRedirection(boolean followRedirection) {
		peerData.setFollowRedirection(followRedirection);
		return this;
	}
	
	public PeerDataProvider withWatchdogInterval(int watchdogIntervalMs) {
		peerData.setWatchdogInterval(watchdogIntervalMs);
		return this;
	}
	
	public PeerDataProvider withURI(String URI) {
		peerData.setURI(URI);
		return this;
	}
	
	public PeerDataProvider withRetryCount(int retransmissionCount) {
		peerData.setRetransmissionCount(retransmissionCount);
		return this;
	}
	
	public PeerDataProvider withRequestTimeout(long requestTimeoutMS) {
		peerData.setRequestTimeout(requestTimeoutMS);
		return this;
	}
	
	public PeerDataProvider withRedirectHostAVPFormat(RedirectHostAVPFormat redirectHostAVPFormat) {
		peerData.setRedirectHostAVPFormat(redirectHostAVPFormat);
		return this;
	}
	
	public PeerDataProvider withRealmName(String realmName) {
		peerData.setRealmName(realmName);
		return this;
	}
	
	public PeerDataProvider withPeerName(String peerName) {
		peerData.setPeerName(peerName);
		return this;
	}
	
	public PeerDataProvider withInitiateConnectionDuration(int initiateConnectionDuration) {
		peerData.setInitiateConnectionDuration(initiateConnectionDuration);
		return this;
	}
	
	public PeerDataProvider withHostIdentity(String strHostIdentity) {
		peerData.setHostIdentity(strHostIdentity);
		return this;
	}
	
	public PeerDataProvider withAdditionalDWRAvps(String dwrAVPString) {
		peerData.setDWRAVPString(dwrAVPString);
		return this;
	}
	
	public PeerDataProvider withAdditionalDPRAvps(String dprAVPString) {
		peerData.setDPRAVPString(dprAVPString);
		return this;
	}
	
	public PeerDataProvider withAdditionalCERAvps(String cerAVPString) {
		peerData.setCERAVPString(cerAVPString);
		return this;
	}
	
	public PeerDataProvider withExclusiveAuthAppIDs(String strExclusiveAuthAppIds) {
		peerData.setExclusiveAuthAppIDs(strExclusiveAuthAppIds);
		return this;
	}
	
	public PeerDataProvider withExclusiveAcctAppIDs(String strExclusiveAcctAppIds) {
		peerData.setExclusiveAcctAppIDs(strExclusiveAcctAppIds);
		return this;
	}
	
	public PeerDataProvider withSecondaryPeer(String secondaryPeerName) {
		peerData.setSecondaryPeerName(secondaryPeerName);
		return this;
	}
}
