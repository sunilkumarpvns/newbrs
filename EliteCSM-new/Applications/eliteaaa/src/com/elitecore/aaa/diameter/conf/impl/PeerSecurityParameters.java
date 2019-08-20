package com.elitecore.aaa.diameter.conf.impl;

import javax.xml.bind.annotation.XmlElement;

import com.elitecore.core.commons.tls.TLSVersion;

public class PeerSecurityParameters {

	private String serverCertificateId;
	private TLSVersion maxTlsVersion;
	private TLSVersion minTlsVersion;
	private String enabledCiphersuites;
	private boolean clientCertificateRequest;
	private long handShakeTimeout;
	
	
	private boolean validateHost;
	private boolean validateCertificateExpiry;
	private boolean validateCertificateRevocation;
	private boolean validateCertificateCA;
	
	
	public PeerSecurityParameters(){
		handShakeTimeout = 3000;
		minTlsVersion = TLSVersion.TLS1_0;
		maxTlsVersion = TLSVersion.TLS1_0;
		clientCertificateRequest = true;
		validateHost = true;
		validateCertificateExpiry = true;
		validateCertificateRevocation = true;
		validateCertificateCA = true;
	}
	
	@XmlElement(name="handshake-timeout",type=Long.class,defaultValue="3000")
	public Long getHandShakeTimeout() {
		return handShakeTimeout;
	}	

	@XmlElement(name="server-certificate-id",type=String.class)
	public String getServerCertificateId() {
		return serverCertificateId;
	}
	
	@XmlElement(name="max-tls-version",type=TLSVersion.class, defaultValue="TLSv1")
	public TLSVersion getMaxTlsVersion() {
		return maxTlsVersion;
	}
	
	@XmlElement(name="min-tls-version",type=TLSVersion.class, defaultValue="TLSv1")
	public TLSVersion getMinTlsVersion() {
		return minTlsVersion;
	}
	
	@XmlElement(name="enabled-cipher-suites", type=String.class)
	public String getEnabledCiphersuites() {
		return enabledCiphersuites;
	}
	
	@XmlElement(name="client-certificate-request",type=boolean.class, defaultValue="true")
	public boolean getClientCertificateRequest() {
		return clientCertificateRequest;
	}
	
	@XmlElement(name="validate-certificate-expiry",type=boolean.class, defaultValue="true")
	public boolean getValidateCertificateExpiry() {
		return validateCertificateExpiry;
	}
	
	@XmlElement(name="validate-certificate-revocation",type=Boolean.class, defaultValue="true")
	public boolean getValidateCertificateRevocation() {
		return validateCertificateRevocation;
	}
	
	@XmlElement(name="validate-certificate-ca",type=Boolean.class, defaultValue="true")
	public boolean getValidateCertificateCA() {
		return validateCertificateCA;
	}
	
	@XmlElement(name="validate-host",type=Boolean.class, defaultValue="true")
	public boolean getValidateHost() {
		return validateHost;
	}

	public void setValidateHost(boolean validateHost) {
		this.validateHost = validateHost;
	}
	
	public void setValidateCertificateCA(boolean validateCertificateCA) {
		this.validateCertificateCA = validateCertificateCA;
	}
	
	public void setValidateCertificateRevocation(boolean validateCertificateRevocation) {
		this.validateCertificateRevocation = validateCertificateRevocation;
	}
	
	public void setValidateCertificateExpiry(boolean validateCertificateExpiry) {
		this.validateCertificateExpiry = validateCertificateExpiry;
	}
	
	public void setHandShakeTimeout(long handShakeTimeout) {
		this.handShakeTimeout = handShakeTimeout;
	}
	
	public void setServerCertificateId(String serverCertificateId) {
		this.serverCertificateId = serverCertificateId;
	}
	
	public void setMaxTlsVersion(TLSVersion maxTlsVersion) {
		this.maxTlsVersion = maxTlsVersion;
	}
	
	public void setMinTlsVersion(TLSVersion minTlsVersion) {
		this.minTlsVersion = minTlsVersion;
	}
	
	public void setEnabledCiphersuites(String enabledCiphersuites) {
		this.enabledCiphersuites = enabledCiphersuites;
	}
	
	public void setClientCertificateRequest(boolean peerCertificateRequest) {
		this.clientCertificateRequest = peerCertificateRequest;
	}

}
