package com.elitecore.diameterapi.diameter.common.data;

import java.util.List;

import com.elitecore.diameterapi.core.common.transport.tcp.config.PeerConnectionData;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.constant.RedirectHostAVPFormat;

public interface PeerData extends PeerConnectionData{
	
	int MAX_RETRANSMISSION_COUNT = 3;
	int NO_RETRANSMISSION = 0; 
	int MAX_TIMEOUT_MS = 10000;
	int DEFAULT_TIMEOUT_MS = 3000;
	int MIN_TIMEOUT_MS = 1000;
	
	public enum DuplicateConnectionPolicyType {
		DEFAULT,
		DISCARD_OLD,
	}

	public static final String DEFAULT_URI_FORMAT = "${aaa}${FQDN}${port}${tansport-protocol}${aaa-protocol}";
	
	public String getPeerName();
	public String getHostIdentity();
	public String getRealmName();
	public boolean isInitConnection();
	public int getWatchdogInterval();
	public List<IDiameterAVP> getAdditionalCERAvps();
	public List<IDiameterAVP> getAdditionalDWRAvps();
	public List<IDiameterAVP> getAdditionalDPRAvps();
	public int getInitiateConnectionDuration();
	public int getRetryCount();
	public long getRequestTimeout();
	public void setPeerName(String peerName);
	public void setHostIdentity(String strHostIdentity);
	public boolean isSessionCleanUpOnCER();
	public boolean isSessionCleanUpOnDPR();
	public boolean isSendDPRonCloseEvent();
	public boolean isFollowRedirection();
	public RedirectHostAVPFormat getRedirectHostAVPFormat();
	public String getURI();
	
	
	public Object clone() throws CloneNotSupportedException;
	public String getExclusiveAuthAppIDs();
	public String getExclusiveAcctAppIDs();
	public long getPeerIndex();
	public void setPeerIndex(long peerIndex);
	
	public String getHAAddress();
	public String getDHCPAddress();
	public String getSecondaryPeerName();
	
	public String getHotlinePolicy();
	public void setHotlinePolicy(String hotLinePolicy);
	
	public boolean isReTransmissionCompliant();
	
	public void setWatchdogInterval(int interval);
	public void setAdditionalCERAvps(List<IDiameterAVP> additionalCERAvps);
	public void setAdditionalDWRAvps(List<IDiameterAVP> additionalDWRAvps);
	public void setAdditionalDPRAvps(List<IDiameterAVP> additionalDPRAvps);
	public void setRequestTimeout(long requestTimeout);
	public void setSessionCleanUpOnCER(boolean sessionCleanUpOnCER);
	public void setSessionCleanUpOnDPR(boolean sessionCleanUpOnDPR);
	public void setSendDPRonCloseEvent(boolean sessionCleanUpOnDPR);
	public void setFollowRedirection(boolean followRedirection);
	public void setRedirectHostAVPFormat(RedirectHostAVPFormat redirectHostAVPFormat);
	public void setExclusiveAuthAppIDs(String exclusiveAuthAppIDs);
	public void setExclusiveAcctAppIDs(String exclusiveAcctAppId);
	
	void setRetransmissionCount(Integer retransmissionCount);
	void setInitiateConnectionDuration(Integer initiateConnectionDuration);
	void setURI(String URI);
	void setSecondaryPeerName(String secondaryPeerName);
	void setHaIpAddress(String haIpAddress);
	void setDhcpIpAddress(String dhcpIpAddress);
	
	void reload(PeerData peerData);
	
	DuplicateConnectionPolicyType getDuplicateConnectionPolicyType();
}
