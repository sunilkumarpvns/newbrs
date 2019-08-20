package com.elitecore.aaa.diameter.conf.impl;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.core.commons.util.constants.CommonConstants;
import com.elitecore.diameterapi.diameter.common.data.PeerData.DuplicateConnectionPolicyType;


@XmlType(propOrder={})
public class PeerProfileDetail {
	private String id;
	private String name;
	private boolean isSessionCleanupOnCER;
	private boolean isSessionCleanupOnDPR;
	private String strCERAvps="";
	private String strDPRAvps="";
	private String strDWRAvps="";
	private String redirectHostAVPFormat = "DIAMETERURI";
	//followRedirection to indicate whether we have to Accept Redirect Indication from Peer or not
	private boolean followRedirection = false;
	
	private PeerConnectionParameters connectionParameters;
	private PeerSecurityParameters securityParameters;
	private String strExclusiveAcctAppIds;
	private String strExclusiveAuthAppIds;
	private String haIpAddress = CommonConstants.RESERVED_IPV_4_ADDRESS;
	private String dhcpIpAddress = CommonConstants.RESERVED_IPV_4_ADDRESS;
	private String hotlinePolicy;
	
	/*
	 * Transient properties
	 */
	private DuplicateConnectionPolicyType duplicateConnectionPolicyType = DuplicateConnectionPolicyType.DEFAULT;
	
	public PeerProfileDetail() {
		this.connectionParameters = new PeerConnectionParameters();
	}
	
	@XmlElement(name="id",type=String.class)
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@XmlElement(name="name",type=String.class)
	public String getName() {
		return name;
	}

	@XmlElement(name="session-cleanup-on-cer",type=boolean.class)
	public boolean getIsSessionCleanupOnCER() {
		return isSessionCleanupOnCER;
	}

	@XmlElement(name="session-cleanup-on-dpr",type=boolean.class)
	public boolean getIsSessionCleanupOnDPR() {
		return isSessionCleanupOnDPR;
	}

	@XmlElement(name="cer-avps",type=String.class,defaultValue="")
	public String getStrCERAvps() {
		return strCERAvps;
	}

	@XmlElement(name="dpr-avps",type=String.class,defaultValue="")
	public String getStrDPRAvps() {
		return strDPRAvps;
	}
	
	@XmlElement(name="dwr-avps",type=String.class,defaultValue="")
	public String getStrDWRAvps() {
		return strDWRAvps;
	}
	
	@XmlElement(name="redirect-host-avp-format",type=String.class)
	public String getRedirectHostAVPFormat() {
		return redirectHostAVPFormat;
	}

	public void setRedirectHostAVPFormat(String redirectHostAVPFormat) {
		this.redirectHostAVPFormat = redirectHostAVPFormat;
	}
	
	@XmlElement(name="follow-redirection",type=boolean.class)
	public boolean getFollowRedirection() {
		return followRedirection;
	}
	
	@XmlElement(name="exclusive-acct-application-ids",type=String.class,defaultValue="")
	public String getStrExclusiveAcctAppIds() {
		return strExclusiveAcctAppIds;
	}

	@XmlElement(name="exclusive-auth-application-ids",type=String.class,defaultValue="")
	public String getStrExclusiveAuthAppIds() {
		return strExclusiveAuthAppIds;
	}
	
	public void setFollowRedirection(boolean value) {
		this.followRedirection = value;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void setIsSessionCleanupOnCER(boolean isSessionCleanupOnCER) {
		this.isSessionCleanupOnCER = isSessionCleanupOnCER;
	}

	public void setIsSessionCleanupOnDPR(boolean isSessionCleanupOnDPR) {
		this.isSessionCleanupOnDPR = isSessionCleanupOnDPR;
	}

	public void setStrExclusiveAuthAppIds(String authAppIds) {
		this.strExclusiveAuthAppIds = authAppIds;
	}

	public void setStrExclusiveAcctAppIds(String acctAppIds) {
		this.strExclusiveAcctAppIds = acctAppIds;
	}
	
	public void setStrCERAvps(String strCERAvps) {
		this.strCERAvps = strCERAvps;
	}

	public void setStrDPRAvps(String strDPRAvps) {
		this.strDPRAvps = strDPRAvps;
	}

	public void setStrDWRAvps(String strDWRAvps) {
		this.strDWRAvps = strDWRAvps;
	}

	public void setConnectionParameters(PeerConnectionParameters connectionParameters) {
		this.connectionParameters = connectionParameters;
	}

	@XmlElement(name="connection-parameters")
	public PeerConnectionParameters getConnectionParameters() {
		return connectionParameters;
	}
	
	public void setSecurityParameters(PeerSecurityParameters securityParameters) {
		this.securityParameters = securityParameters;
	}

	@XmlElement(name="security-parameters")
	public PeerSecurityParameters getSecurityParameters() {
		return securityParameters;
	}
	
	@XmlElement(name = "ha-address", type = String.class, defaultValue = CommonConstants.RESERVED_IPV_4_ADDRESS)
	public String getHaIpAddress() {
		return haIpAddress;
	}

	public void setHaIpAddress(String haIpAddress) {
		this.haIpAddress = haIpAddress;
	}

	@XmlElement(name = "dhcp-address", type = String.class, defaultValue = CommonConstants.RESERVED_IPV_4_ADDRESS)
	public String getDhcpIpAddress() {
		return dhcpIpAddress;
	}

	public void setDhcpIpAddress(String dhcpIpAddress) {
		this.dhcpIpAddress = dhcpIpAddress;
	}
	
	@XmlElement(name = "hotline-policy", type = String.class)
	public @Nullable String getHotlinePolicy() {
		return hotlinePolicy;
	}

	public void setHotlinePolicy(String hotlinePolicy) {
		this.hotlinePolicy = hotlinePolicy;
	}
	
	@XmlTransient
	public DuplicateConnectionPolicyType getDuplicateConnectionPolicyType() {
		return duplicateConnectionPolicyType;
}

	public void setDuplicateConnectionPolicyType(DuplicateConnectionPolicyType duplicateConnectionPolicyType) {
		this.duplicateConnectionPolicyType = duplicateConnectionPolicyType;
	}
	
}
