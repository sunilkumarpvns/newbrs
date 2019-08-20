package com.elitecore.client.configuration;

import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.core.commons.tls.CRLConfiguration;
import com.elitecore.core.commons.tls.ServerCertificateProfile;
import com.elitecore.core.commons.tls.TLSVersion;
import com.elitecore.core.commons.tls.TrustedCAConfiguration;
import com.elitecore.core.commons.tls.constant.PrivateKeyAlgo;

/**
 * 
 * @author Kuldeep Panchal
 * @author Malav Desai
 *
 */
@XmlRootElement(name = "tls")
@XmlType(propOrder = {})
public class TlsConfiguration implements ServerCertificateProfile, TrustedCAConfiguration ,CRLConfiguration {

	private TLSVersion minTlsVersion = TLSVersion.TLS1_0;
	private TLSVersion maxTlsVersion = TLSVersion.TLS1_0;
	private int sessionResumptionDuration;
	
	private byte[] privateKeyBytes;
	private String privateKeyPassword;
	private String privateKeyFileName;
	private PrivateKeyAlgo privateKeyAlgo;
	private PrivateKey privateKey;
	private Collection<? extends Certificate> certificates;
	private String certificateFileName;
	private byte[] clientCertificateBytes;

	private List<X509Certificate> caCertificateList;
	private List<X509CRL> crlList;
	
	@XmlElement(name = "min-tls-version", defaultValue = "TLSv1")
	public TLSVersion getMinTlsVersion() {
		return minTlsVersion;
	}
	
	public void setMinTlsVersion(TLSVersion minTlsVersion) {
		this.minTlsVersion = minTlsVersion;
	}
	
	@XmlElement(name = "max-tls-version", defaultValue = "TLSv1")
	public TLSVersion getMaxTlsVersion() {
		return maxTlsVersion;
	}
	
	public void setMaxTlsVersion(TLSVersion maxTlsVersion) {
		this.maxTlsVersion = maxTlsVersion;
	}
	
	@XmlElement(name = "session-resumption-duration")
	public int getSessionResumptionDuration() {
		return sessionResumptionDuration;
	}
	public void setSessionResumptionDuration(int sessionResumptionDuration) {
		this.sessionResumptionDuration = sessionResumptionDuration;
	}
	
	@Override
	public String getName() {
		return null;
	}

	@Override
	@XmlTransient
	public PrivateKey getPrivateKey() {
		return privateKey;
	}

	@Override
	@XmlElement(name = "private-key-passwd")
	public String getPrivateKeyPassword() {
		return privateKeyPassword;
	}

	@Override
	public String getPlainTextPrivateKeyPassword() {
		return null;
	}

	@Override
	@XmlTransient
	public PrivateKeyAlgo getPrivateKeyAlgo() {
		return privateKeyAlgo;
	}

	@Override
	@XmlTransient
	public Collection<? extends Certificate> getCertificates() {
		return certificates;
	}

	@Override
	public String getCertificatePath() {
		return null;
	}

	@Override
	public String getPrivateKeyPath() {
		return null;
	}

	@Override
	public Long getId() {
		return null;
	}

	@Override
	@XmlElement(name = "client-cert-file")
	public String getCertificateName() {
		return certificateFileName;
	}
	
	@Override
	@XmlElement(name = "private-key-file")
	public String getPrivateKeyName() {
		return privateKeyFileName;
	}

	@Override
	@XmlTransient
	public byte[] getCertificateFileBytes() {
		return clientCertificateBytes;
	}

	@Override
	@XmlTransient
	public byte[] getPrivatekeyFileBytes() {
		return privateKeyBytes;
	}

	@Override
	@XmlTransient
	public List<byte[]> getCertificateChainBytes() {
		return null;
	}
	
	public void setPrivatekeyFileBytes(byte[] privateKeyBytes) {
		this.privateKeyBytes = privateKeyBytes;
	}
	
	public void setPrivateKey(PrivateKey privateKey) {
		this.privateKey = privateKey;
	}

	public void setPrivateKeyPassword(String privateKeyPassword) {
		this.privateKeyPassword = privateKeyPassword;
	}
	
	public void setPrivateKeyAlgo(PrivateKeyAlgo privateKeyAlgo) {
		this.privateKeyAlgo = privateKeyAlgo;
	}

	public void setCertificates(Collection<? extends Certificate> certificates) {
		this.certificates = certificates;
	}
	
	public void setCertificateName(String certificateFileName) {
		this.certificateFileName = certificateFileName;
	}

	public void setPrivateKeyName(String privateKeyFileName) {
		this.privateKeyFileName = privateKeyFileName;
	}

	public void setCertificateFileBytes(byte[] clientCertificateBytes) {
		this.clientCertificateBytes = clientCertificateBytes;
	}

	@Override
	public boolean getOCSPEnabled() {
		return false;
	}

	@Override
	public String getOCSPURL() {
		return null;
	}

	@Override
	@XmlTransient
	public List<X509CRL> getCRLs() {
		return crlList;
	}
	
	@Override
	@XmlTransient
	public List<X509Certificate> getCACertificates() {
		return caCertificateList;
	}
	
	public void setCRLs(List<X509CRL> crlList) {
		this.crlList = crlList;
	}
	
	public void setCACertificates(List<X509Certificate> caCertificateList) {
		this.caCertificateList = caCertificateList;
	}

	@Override
	public String getUUID() {
		return null;
	}
}