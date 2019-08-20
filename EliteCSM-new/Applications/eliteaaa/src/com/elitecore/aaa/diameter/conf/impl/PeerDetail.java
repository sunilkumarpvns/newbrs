package com.elitecore.aaa.diameter.conf.impl;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder={})
public class PeerDetail {
	private String peerUUID = "-1";
	private long peerId = -1;
	private String peerName = "";
	private String hostIdentity="";
	private String realm=""; 
	private String remoteAddress="";
	private String localAddress="";
	private String profileName="";
	private String URI="";
	private long requestTimeout;
	private int retransmissionCount;
	private String secondaryPeerName;

	public PeerDetail() {
		
	}

	@XmlElement(name="id",type=String.class)
	public String getPeerUUID() {
		return peerUUID;
	}

	public void setPeerUUID(String peerUUID) {
		this.peerUUID = peerUUID;
	}


	@XmlTransient
	public long getPeerId() {
		return peerId;
	}

	public void setPeerId(long peerId) {
		this.peerId = peerId;
	}
	
	@XmlElement(name="name",type=String.class,defaultValue="")
	public String getPeerName() {
		return peerName;
	}

	@XmlElement(name="host-identity",type=String.class,defaultValue="")
	public String getHostIdentity() {
		return hostIdentity;
	}

	@XmlElement(name="realm",type=String.class,defaultValue="")
	public String getRealm() {
		return realm;
	}

	@XmlElement(name="remote-address",type=String.class,defaultValue="")
	public String getRemoteAddress() {
		return remoteAddress;
	}

	@XmlElement(name="local-address",type=String.class,defaultValue="")
	public String getLocalAddress() {
		return localAddress;
	}

	@XmlElement(name="profile-name",type=String.class,defaultValue="")
	public String getProfileName() {
		return profileName;
	}

	@XmlElement(name="uri",type=String.class,defaultValue="")
	public String getURI() {
		return this.URI;
	}
	
	@XmlElement(name="request-timeout",type=Long.class,defaultValue="3000")
	public long getRequestTimeout() {
		return requestTimeout;
	}

	@XmlElement(name="retransmission-count",type=Integer.class,defaultValue="0")
	public int getRetransmissionCount() {
		return retransmissionCount;
	}
	
	public void setPeerName(String peerName) {
		this.peerName = peerName;
	}

	public void setHostIdentity(String hostIdentity) {
		this.hostIdentity = hostIdentity;
	}

	public void setRealm(String realm) {
		this.realm = realm;
	}

	public void setRemoteAddress(String remoteAddress) {
		this.remoteAddress = remoteAddress;
	}

	public void setLocalAddress(String localAddress) {
		this.localAddress = localAddress;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}
	
	public void setURI(String uri){
		this.URI = uri;
	}
	
	public void setRequestTimeout(long requestTimeout) {
		this.requestTimeout = requestTimeout;
	}
	
	public void setRetransmissionCount(int retransmissioncount) {
		this.retransmissionCount = retransmissioncount;
	}

	@XmlElement(name="secondary-peer-name", type=String.class)
	public String getSecondaryPeerName() {
		return secondaryPeerName;
	}

	public void setSecondaryPeerName(String secondaryPeerName) {
		this.secondaryPeerName = secondaryPeerName;
	}

	@Override
	public String toString() {
		return "PeerDetail [peerUUID=" + peerUUID + ", peerId=" + peerId + ", peerName=" + peerName + ", hostIdentity="
				+ hostIdentity + ", realm=" + realm + ", remoteAddress=" + remoteAddress + ", localAddress="
				+ localAddress + ", profileName=" + profileName + ", URI=" + URI + ", requestTimeout=" + requestTimeout
				+ ", retransmissionCount=" + retransmissionCount + ", secondaryPeerName=" + secondaryPeerName + "]";
	}
}
