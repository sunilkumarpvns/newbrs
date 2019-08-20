package com.elitecore.core.commons.tls;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elitecore.core.commons.tls.cipher.CipherSuites;

public class EliteSSLParameter {
	
	private ServerCertificateProfile serverCertificateProfile;
	private TLSVersion maxTlsVersion;
	private TLSVersion minTlsVersion;
	
	private Map<TLSVersion ,List<String>> enabledCiphersuites;
	
	private boolean clientCertificateRequest;
	
	private boolean validateCertificateExpiry;
	private boolean validateCertificateRevocation;
	private boolean validateCertificateCA;
	private boolean validateSubjectCN;

	private long handshakeTimeout;


	public EliteSSLParameter(ServerCertificateProfile serverCertificateProfile, TLSVersion minTlsVersion, TLSVersion maxTlsVersion) {
		this.serverCertificateProfile = serverCertificateProfile;
		this.maxTlsVersion = maxTlsVersion;
		this.minTlsVersion = minTlsVersion;
		this.enabledCiphersuites = new HashMap<TLSVersion, List<String>>();
		
		this.validateCertificateExpiry = true;
		this.validateCertificateRevocation = true;
		this.validateCertificateCA = true;
		this.validateSubjectCN = true;
	}
	
	public long getHandshakeTimeout() { return handshakeTimeout; }


	public void setHandshakeTimeout(long handshakeTimeout) { this.handshakeTimeout = handshakeTimeout; }
	
	
	public ServerCertificateProfile getServerCertificateProfile() { return serverCertificateProfile; }

	public boolean isClientCertificateRequestRequired() { return clientCertificateRequest; }

	public void setClientCertificateRequestRequired(boolean clientCertificateRequest) { this.clientCertificateRequest = clientCertificateRequest; }

	public boolean isValidateCertificateExpiry() { return validateCertificateExpiry; }

	public void setValidateCertificateExpiry(boolean validateCertificateExpiry) {
		this.validateCertificateExpiry = validateCertificateExpiry;
	}

	public boolean isValidateCertificateRevocation() {
		return validateCertificateRevocation;
	}

	public void setValidateCertificateRevocation(boolean validateCertificateRevocation) {
		this.validateCertificateRevocation = validateCertificateRevocation;
	}

	public boolean isValidateCertificateCA() {
		return validateCertificateCA;
	}

	public void setValidateCertificateCA(boolean validateCertificateCA) {
		this.validateCertificateCA = validateCertificateCA;
	}


	public TLSVersion getMaxTlsVersion() {
		return maxTlsVersion;
	}
	
	public TLSVersion getMinTlsVersion() {
		return minTlsVersion;
	}
	
	public List<String> getEnabledCiphersuites(List<TLSVersion> tlsVersions) {
		ArrayList<String> cipherSuites = new ArrayList<String>();
		
		for(TLSVersion tlsVersion : tlsVersions){
			List<String> vesionViseCipherSuites = enabledCiphersuites.get(tlsVersion);
			if(vesionViseCipherSuites != null && !vesionViseCipherSuites.isEmpty()){
				cipherSuites.addAll(vesionViseCipherSuites);
			}
		}
		return cipherSuites;
	}

	public void addEnabledCiphersuites(List<CipherSuites> enabledCiphersuites) {
		if(enabledCiphersuites != null){
			for(CipherSuites cipherSuite :  enabledCiphersuites){
				if(cipherSuite.isSupportTLS1_0()){
					List<String> cipherSuites = this.enabledCiphersuites.get(TLSVersion.TLS1_0);
					if(cipherSuites == null){
						cipherSuites = new ArrayList<String>();
						this.enabledCiphersuites.put(TLSVersion.TLS1_0, cipherSuites);
					}
					cipherSuites.add(cipherSuite.name);
				}
				
				
				if(cipherSuite.isSupportTLS1_1()){
					List<String> cipherSuites = this.enabledCiphersuites.get(TLSVersion.TLS1_1);
					if(cipherSuites == null){
						cipherSuites = new ArrayList<String>();
						this.enabledCiphersuites.put(TLSVersion.TLS1_1, cipherSuites);
					}
					cipherSuites.add(cipherSuite.name);
				}
				
				if(cipherSuite.isSupportTLS1_2()){
					List<String> cipherSuites = this.enabledCiphersuites.get(TLSVersion.TLS1_2);
					if(cipherSuites == null){
						cipherSuites = new ArrayList<String>();
						this.enabledCiphersuites.put(TLSVersion.TLS1_2, cipherSuites);
					}
					cipherSuites.add(cipherSuite.name);
				}
			}
		}
	}
	
	public boolean isValidateSubjectCN() {
		return validateSubjectCN;
	}

	public void setValidateSubjectCN(boolean validateSubjectCN) {
		this.validateSubjectCN = validateSubjectCN;
	}


}
