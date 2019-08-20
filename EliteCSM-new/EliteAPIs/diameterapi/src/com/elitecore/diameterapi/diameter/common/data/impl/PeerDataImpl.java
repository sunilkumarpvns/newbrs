package com.elitecore.diameterapi.diameter.common.data.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.data.ValueProvider;
import com.elitecore.diameterapi.core.common.peer.group.DiameterPeerGroupParameter;
import com.elitecore.diameterapi.core.common.transport.tcp.config.impl.PeerConnectionDataImpl;
import com.elitecore.diameterapi.diameter.common.data.PeerData;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
import com.elitecore.diameterapi.diameter.common.util.constant.RedirectHostAVPFormat;

public class PeerDataImpl extends PeerConnectionDataImpl implements PeerData,Cloneable{

	private static final String MODULE = "PEER-DATA";

	
	
	private String realmName = "";
	private int watchdogIntervalMs = 30000; 
	private List<IDiameterAVP> additionalCERAVPs;
	private List<IDiameterAVP> additionalDPRAVPs;
	private List<IDiameterAVP> additionalDWRAVPs;
	private int initiateConnectionDuration = 0;

	private boolean isSessionCleanUpOnCER = true;
	private boolean isSessionCleanUpOnDPR = true;
	
	private boolean isSendDPRonCloseEvent = false;
	
	//redirectionHostAVPFormat is a format that Peer accepts in Redirect-Host AVP
	private RedirectHostAVPFormat redirectHostAVPFormat = RedirectHostAVPFormat.DIAMETERURI;
	private String URI = "";
	//followRedirection to indicate whether we have to Accept Redirect Indication from Peer or not
	private boolean followRedirection = false;
	private int retransmissionCount = 0;
	private long requestTimeout = 3000; //in MS
	
	private String hotlinePolicy = null;

	/**
	 * cloneable variable
	 */
	private String peerName;
	private String strHostIdentity = "";



	private String exclusiveAuthAppIDs;
	private String exclusiveAcctAppIDs;
	private long peerIndex = -1;

	//TODO need to introduce this field in UI and Read Configuration form DB
	private boolean reTransmissionComplient = true;
	private String dhcpIpAddress;
	private String haIpAddress;
	private String secondaryPeerName;
	
	private DiameterPeerGroupParameter peerGroupParameter;
	private DuplicateConnectionPolicyType duplicateConnectionPolicyType = DuplicateConnectionPolicyType.DEFAULT;

	public PeerDataImpl() {
		this.additionalCERAVPs = new ArrayList<IDiameterAVP>();
		this.additionalDPRAVPs = new ArrayList<IDiameterAVP>();
		this.additionalDWRAVPs = new ArrayList<IDiameterAVP>();
	}

	@Override
	public void setHostIdentity(String strHostIdentity) {
		if(strHostIdentity != null && strHostIdentity.trim().length() > 0)
			this.strHostIdentity = strHostIdentity;
		
	}
	
	@Override
	public boolean isFollowRedirection() {
		return followRedirection ;
	}
	
	@Override
	public void setFollowRedirection(boolean followRedirection) {
		this.followRedirection = followRedirection;
	}
	@Override
	public RedirectHostAVPFormat getRedirectHostAVPFormat(){
		return redirectHostAVPFormat;
		
	}
	
	@Override
	public void setRedirectHostAVPFormat(RedirectHostAVPFormat redirectHostAVPFormat){
		this.redirectHostAVPFormat = redirectHostAVPFormat;
		
	}
	
	@Override
	public void setURI(String URI) {
		this.URI = URI;
	}
	
	public String getURI() {
		return URI;
	}
	public String getPeerName() {
		return peerName;
	}

	public void setPeerName(String peerName) {
		this.peerName = peerName;
	}

	/**
	 * @param realmName the realmName to set
	 */
	public void setRealmName(String realmName) {
		this.realmName = realmName;
	}

	/**
	 * @return the realmName
	 */
	public String getRealmName() {
		return realmName;
	}

	@Override
	public String getHostIdentity() {
		return this.strHostIdentity;
	}	

	/**
	 * @return the initConnection
	 */
	public boolean isInitConnection() {
		if(initiateConnectionDuration > 0)
			return true;
		else 
			return false;
	}

	@Override
	public String toString(){
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter( stringBuffer);
		out.println("\tPeer Index: " + this.peerIndex);
		out.println("\tPeer Name: " + this.peerName);
		out.println("\tHost Identity: " + this.strHostIdentity);
		out.println("\tRealm: " + this.realmName);
		out.println("\tURI: " + this.URI);
		out.println("\tWatch Dog Interval: " + this.watchdogIntervalMs + " (ms)");
		out.println("\tRetransmission Count: " + this.retransmissionCount);
		out.println("\tRequest Timeout: " + this.requestTimeout + " (ms)");
		out.println("\tSession CleanUp on CER is enabled: " + this.isSessionCleanUpOnCER);
		out.println("\tSession CleanUp on DPR is enabled: " + this.isSessionCleanUpOnDPR);
		out.println("\tSend DPR on Close Event is enabled: " + this.isSendDPRonCloseEvent);
		out.println("\tFollow Redirection: " + this.followRedirection);
		out.println("\tRedirect-Host AVP format: " + this.redirectHostAVPFormat);
		out.println("\tInitiate Connection Duration: " + this.initiateConnectionDuration + " (ms)");
		out.println("\tDuplicate Connection Policy Type: " + this.duplicateConnectionPolicyType);
		out.println("\t-----Connection Parameter------");
		out.println(super.toString());
		out.close();
		return stringBuffer.toString();
	}

	@Override
	public boolean equals(Object obj){
		if(obj == this)
			return true;
		try {
			PeerDataImpl peer = (PeerDataImpl)obj;
			return (peer.peerName.equals(this.peerName));
		} catch (ClassCastException e) {
		}
		return false;
	}
	public void setWatchdogInterval(int watchdogIntervalMs){
		this.watchdogIntervalMs =  watchdogIntervalMs;
	}

	public void setCERAVPString(String cerAVPString) {
		if(cerAVPString == null || cerAVPString.trim().length() == 0){
			this.additionalCERAVPs = new ArrayList<IDiameterAVP>(0);
			return;
		}
		
		try {
			List<IDiameterAVP> cerAVPs = DiameterUtility.getDiameterAttributes(cerAVPString, new StaticValueProvider());
			if(cerAVPs!=null && cerAVPs.size()>0)
				this.additionalCERAVPs = cerAVPs;
		} catch (Exception e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "Failed to parse CER Avps for Peer: "+strHostIdentity+" , Reason :"+e.getMessage());
		}
	}

	public void setDPRAVPString(String dprAVPString) {
		if(dprAVPString == null || dprAVPString.trim().length() == 0){
			this.additionalDPRAVPs = new ArrayList<IDiameterAVP>(0);
			return;
		}
		
		try {
			List<IDiameterAVP> dprAVPs = DiameterUtility.getDiameterAttributes(dprAVPString, new StaticValueProvider());
			if(dprAVPs!=null && dprAVPs.size()>0)
				this.additionalDPRAVPs = dprAVPs;
		} catch (Exception e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "Failed to parse DPR Avps for Peer: "+strHostIdentity+" , Reason :"+e.getMessage());
		}
	}
	
	public void setDWRAVPString(String dwrAVPString) {
		if(dwrAVPString == null || dwrAVPString.trim().length() == 0){
			this.additionalDWRAVPs = new ArrayList<IDiameterAVP>(0);
			return;
		}
		
		try {
			List<IDiameterAVP> dwrAVPs = DiameterUtility.getDiameterAttributes(dwrAVPString, new StaticValueProvider());
			if(dwrAVPs!=null && dwrAVPs.size()>0)
				this.additionalDWRAVPs = dwrAVPs;
		} catch (Exception e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "Failed to parse DWR Avps for Peer: "+strHostIdentity+" , Reason :"+e.getMessage());
		}
	}


	public int getWatchdogInterval() {
		return watchdogIntervalMs;
	}


	@Override
	public List<IDiameterAVP> getAdditionalCERAvps() {
		return getClonedAdditionalAvps("CER", this.additionalCERAVPs);
	}

	@Override
	public List<IDiameterAVP> getAdditionalDPRAvps() {
		return getClonedAdditionalAvps("DPR", this.additionalDPRAVPs);
	}

	@Override
	public void setInitiateConnectionDuration(Integer initiateConnectionDuration) {
		this.initiateConnectionDuration = initiateConnectionDuration;
	}
	
	@Override
	public void setRetransmissionCount(Integer retransmissionCount) {
		if(retransmissionCount < 0){
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Considering "+NO_RETRANSMISSION+" as retransmission count for Peer "+ peerName +".Reason: Invalid Retry Count: " + retransmissionCount + ".");
			this.retransmissionCount = NO_RETRANSMISSION;
		} else if(retransmissionCount > MAX_RETRANSMISSION_COUNT){
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Considering "+MAX_RETRANSMISSION_COUNT+" as retransmission count for Peer "+ peerName +".Reason: Retry count more than "+MAX_RETRANSMISSION_COUNT+" not receommended.");
			this.retransmissionCount = MAX_RETRANSMISSION_COUNT;
		} else {
			this.retransmissionCount = retransmissionCount;
		}
		
		 
	}

	public int getInitiateConnectionDuration() {
		return initiateConnectionDuration;
	}
	public int getRetryCount() {
		return retransmissionCount;
	}

	// For Session CleanUp On Receiving DPR and CER.
	@Override
	public boolean isSessionCleanUpOnCER() {
		return isSessionCleanUpOnCER;
	}
	@Override
	public boolean isSessionCleanUpOnDPR() {
		return isSessionCleanUpOnDPR;
	}
	
	@Override
	public void setSessionCleanUpOnCER(boolean isSessionCleanUpOnCER){
		this.isSessionCleanUpOnCER = isSessionCleanUpOnCER;
	}
	@Override
	public void setSessionCleanUpOnDPR(boolean isSessionCleanUpOnDPR){
		this.isSessionCleanUpOnDPR = isSessionCleanUpOnDPR;
	}
	
	@Override
	public void setSendDPRonCloseEvent(boolean sendDPRonCloseEnabled) {
		this.isSendDPRonCloseEvent = sendDPRonCloseEnabled; 
	}

	@Override
	public boolean isSendDPRonCloseEvent() {
		return isSendDPRonCloseEvent;
	}

	@Override
	public List<IDiameterAVP> getAdditionalDWRAvps() {
		return getClonedAdditionalAvps("DWR", this.additionalDWRAVPs);
	}
	
	private List<IDiameterAVP> getClonedAdditionalAvps(String type,List<IDiameterAVP> additionalAvps) {
		if(additionalAvps.size()>0){
			List<IDiameterAVP> clonedAVPs = new ArrayList<IDiameterAVP>();
			clonedAVPs = new ArrayList<IDiameterAVP>();
			for(IDiameterAVP diameterAVP :additionalAvps){
				try {
					clonedAVPs.add((IDiameterAVP)diameterAVP.clone());
				} catch (CloneNotSupportedException e) {
					LogManager.getLogger().error(MODULE, "Additional AVP : "+diameterAVP.getAVPId()+" will not be add to "+type+", Reason :"+e.getMessage());
				}
			}
			return clonedAVPs;
		}
		return null;
	}

	@Override
	public long getRequestTimeout() {
		return requestTimeout;
	}
	
	
	@Override
	public void setRequestTimeout(long requestTimeoutMS) {
		
		if(requestTimeoutMS < MIN_TIMEOUT_MS) {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
				LogManager.getLogger().warn(MODULE, "Request timeout: " + requestTimeoutMS + "ms. Timeout less than "+MIN_TIMEOUT_MS+"ms is not recommended, Considering "+MIN_TIMEOUT_MS+"ms as timeout");
			}
			this.requestTimeout = MIN_TIMEOUT_MS;
			
		}else if(requestTimeoutMS > MAX_TIMEOUT_MS) {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
				LogManager.getLogger().warn(MODULE, "Request timeout: " + MAX_TIMEOUT_MS + "ms. Timeout greater than "+MAX_TIMEOUT_MS+"ms is not recommended, Considering "+MAX_TIMEOUT_MS+"ms as timeout");
			}
			this.requestTimeout = MAX_TIMEOUT_MS;
			
		} else {
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
				LogManager.getLogger().info(MODULE, "Request timeout: " + requestTimeoutMS + "ms");
			}
			this.requestTimeout = requestTimeoutMS;
		}
		
		
		
	}
	
	@Override
	public @Nullable String getHotlinePolicy() {
		return hotlinePolicy;
	}

	@Override
	public void setHotlinePolicy(String hotlinePolicy) {
		this.hotlinePolicy = hotlinePolicy;
	}
	
	private class StaticValueProvider implements ValueProvider{

	@Override
		public String getStringValue(String identifier) {
			return identifier;
		}
		
	}
	
	@Override
	public int hashCode(){
		return peerName.hashCode();
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	
	public void setExclusiveAuthAppIDs(String strExclusiveAuthAppIds) {
		exclusiveAuthAppIDs = strExclusiveAuthAppIds;
	}

	public void setExclusiveAcctAppIDs(String strExclusiveAcctAppIds) {
		exclusiveAcctAppIDs = strExclusiveAcctAppIds;
	}

	@Override
	public String getExclusiveAuthAppIDs() {
		return exclusiveAuthAppIDs;
	}

	@Override
	public String getExclusiveAcctAppIDs() {
		return exclusiveAcctAppIDs;
	}

	@Override
	public long getPeerIndex() {
		return peerIndex;
	}

	@Override
	public void setPeerIndex(long peerIndex) {
		this.peerIndex = peerIndex;
	}

	@Override
	public boolean isReTransmissionCompliant() {
		return reTransmissionComplient;
	}
	
	@Override
	public void setHaIpAddress(String haIpAddress) {
		this.haIpAddress = haIpAddress;
	}

	@Override
	public void setDhcpIpAddress(String dhcpIpAddress) {
		this.dhcpIpAddress = dhcpIpAddress;
	
	}

	public String getDHCPAddress() {
		return dhcpIpAddress;
	}

	public String getHAAddress() {
		return haIpAddress;
	}
	
	@Override
	public String getSecondaryPeerName() {
		return secondaryPeerName;
	}

	@Override
	public void setSecondaryPeerName(String secondaryPeerName) {
		this.secondaryPeerName = secondaryPeerName;
	}

	@Override
	public void setAdditionalCERAvps(List<IDiameterAVP> additionalCERAVPs) {
		if (additionalCERAVPs != null) {
			this.additionalCERAVPs = additionalCERAVPs;
	    }
	}

	@Override
	public void setAdditionalDWRAvps(List<IDiameterAVP> additionalDWRAVPs) {
		if (additionalDWRAVPs != null) {
			this.additionalDWRAVPs = additionalDWRAVPs;
	    }
	}

	@Override
	public void setAdditionalDPRAvps(List<IDiameterAVP> additionalDPRAVPs) {
		if (additionalDPRAVPs != null) {
			this.additionalDPRAVPs = additionalDPRAVPs;
	    }
	}

	@Override
	public void reload(PeerData updatedPeerData) {
		//profile parameter
		setPeerTimeout(updatedPeerData.getPeerTimeout());
		setRequestTimeout(updatedPeerData.getRequestTimeout());
		setSessionCleanUpOnCER(updatedPeerData.isSessionCleanUpOnCER());
		setSessionCleanUpOnDPR(updatedPeerData.isSessionCleanUpOnDPR());
		setAdditionalCERAvps(updatedPeerData.getAdditionalCERAvps());
    	setAdditionalDPRAvps(updatedPeerData.getAdditionalDPRAvps());
    	setAdditionalDWRAvps(updatedPeerData.getAdditionalDWRAvps());
    	setExclusiveAcctAppIDs(updatedPeerData.getExclusiveAcctAppIDs());
    	setExclusiveAuthAppIDs(updatedPeerData.getExclusiveAuthAppIDs());
    	setSendDPRonCloseEvent(updatedPeerData.isSendDPRonCloseEvent());
    	setSocketReceiveBufferSize(updatedPeerData.getSocketReceiveBufferSize());
    	setSocketSendBufferSize(updatedPeerData.getSocketSendBufferSize());
    	setTCPNagleAlgo(updatedPeerData.isNagleAlgoEnabled());
    	setWatchdogInterval(updatedPeerData.getWatchdogInterval());
    	setInitiateConnectionDuration(updatedPeerData.getInitiateConnectionDuration());
    	setSecurityStandard(updatedPeerData.getSecurityStandard());
    	setSSLParameter(updatedPeerData.getSSLParameter());
    	setTransportProtocol(updatedPeerData.getTransportProtocol());
    	setFollowRedirection(updatedPeerData.isFollowRedirection());
    	setDhcpIpAddress(updatedPeerData.getDHCPAddress());
    	setHaIpAddress(updatedPeerData.getHAAddress());
    	setRedirectHostAVPFormat(updatedPeerData.getRedirectHostAVPFormat());
    	
    	// peer configuration
    	setLocalInetAddress(updatedPeerData.getLocalInetAddress());
    	setLocalIPAddress(updatedPeerData.getLocalIPAddress());
    	setLocalPort(updatedPeerData.getLocalPort());
    	setSecondaryPeerName(updatedPeerData.getSecondaryPeerName());
    	setRetransmissionCount(updatedPeerData.getRetryCount());
    	setDuplicateConnectionPolicyType(updatedPeerData.getDuplicateConnectionPolicyType());
	}

	@Override
	public DuplicateConnectionPolicyType getDuplicateConnectionPolicyType() {
		return duplicateConnectionPolicyType;
	}

	public void setDuplicateConnectionPolicyType(DuplicateConnectionPolicyType duplicateConnectionPolicyType) {
		this.duplicateConnectionPolicyType = duplicateConnectionPolicyType;
	}
	
}
