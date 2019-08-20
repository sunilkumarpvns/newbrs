package com.elitecore.aaa.core.conf.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.aaa.core.config.Password;
import com.elitecore.core.commons.tls.ServerCertificateProfile;
import com.elitecore.core.commons.tls.constant.PrivateKeyAlgo;

public class ServerCertificateProfileImpl implements ServerCertificateProfile{

	private String uuid;
	private String name;
	private Collection<? extends Certificate > certificates;
	private byte[] certificateFileBytes;
	private byte [] privatekeyBytes;
	private List<byte[]> certificateChainBytes;
	

	private PrivateKey privateKey;
	private String certificateName;
	private String privateKeyName;
	

	//used for jaxb
	private String certificatePath;
	private String privateKeyPath;
	
	
	private Password privateKeyPassword;
	
	private PrivateKeyAlgo privateKeyAlgo;
	
	public ServerCertificateProfileImpl() {
		//required by JAXB
		privateKeyPassword = new Password();
	}
	
	@XmlElement(name="certificate-profile-name",type=String.class)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@XmlTransient
	public PrivateKey getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(PrivateKey privateKey) {
		this.privateKey = privateKey;
	}

	@XmlElement(name="private-key-password",type=String.class)
	public String getPrivateKeyPassword() {
		return privateKeyPassword.getPassword();
	}
	
	@Override
	public String getPlainTextPrivateKeyPassword() {
		return privateKeyPassword.getPlainTextPassword();
	}

	public void setPrivateKeyPassword(String privateKeyPassword) {
		this.privateKeyPassword.setPassword(privateKeyPassword);
	}

	@XmlElement(name="private-key-algo",type=PrivateKeyAlgo.class)
	public PrivateKeyAlgo getPrivateKeyAlgo() {
		return privateKeyAlgo;
	}

	public void setPrivateKeyAlgo(PrivateKeyAlgo privateKeyAlgo) {
		this.privateKeyAlgo = privateKeyAlgo;
	}

	@XmlTransient
	public Collection<? extends Certificate > getCertificates() {
		return certificates;
	}
	
	public void setCertificates(Collection<? extends Certificate > certificates){
		this.certificates = certificates;
	}
	
	@XmlElement(name="certificate-path",type=String.class)
	public String getCertificatePath() {
		return certificatePath;
	}

	public void setCertificatePath(String certificatePath) {
		this.certificatePath = certificatePath;
	}

	@XmlElement(name="private-key-path",type=String.class)
	public String getPrivateKeyPath() {
		return privateKeyPath;
	}

	public void setPrivateKeyPath(String privateKeyPath) {
		this.privateKeyPath = privateKeyPath;
	}
	
	@XmlTransient
	@Deprecated
	public Long getId() {
		return null;
	}

	@Override
	@XmlElement(name="id",type=String.class)
	public String getUUID() {
		return uuid;
	}
	
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	@XmlElement(name="certificate-name",type=String.class)
	public String getCertificateName() {
		return certificateName;
	}

	public void setCertificateName(String certificateName) {
		this.certificateName = certificateName;
	}

	@XmlElement(name="private-key-name",type=String.class)
	public String getPrivateKeyName() {
		return privateKeyName;
	}

	public void setPrivateKeyName(String privateKeyName) {
		this.privateKeyName = privateKeyName;
	}
	
	@XmlTransient
	public byte[] getCertificateFileBytes() {
		return certificateFileBytes;
	}

	public void setCertificateFileBytes(byte[] certificateFileBytes) {
		this.certificateFileBytes = certificateFileBytes;
	}
	
	@XmlTransient
	public byte[] getPrivatekeyFileBytes() {
		return privatekeyBytes;
	}

	public void setPrivatekeyBytes(byte[] privatekeyBytes) {
		this.privatekeyBytes = privatekeyBytes;
	}
	
	public String toString() {

		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println();
		out.println(" --- Server certificate Profile --- ");
		out.println(" Name: " + getName());
		out.println(certificates.size() > 1 ? "(Server Certificate chain)" : "(Server Certificate)");
		for (Certificate cert : certificates) {
			X509Certificate x509Cert = (X509Certificate) cert;
			out.println("Subject DN : " + x509Cert.getSubjectDN());
			out.println("Issuer DN  : " + x509Cert.getIssuerDN());
			out.println("Validity   : " + x509Cert.getNotBefore() + " to " + x509Cert.getNotAfter());
			out.println("--------------------------------------------------------------------------");
		}
		out.println();
		out.close();
		return stringBuffer.toString();
	}
	
	@XmlTransient
	public List<byte[]> getCertificateChainBytes() {
		return certificateChainBytes;
	}

	public void setCertificateChainBytes(List<byte[]> certificateChainBytes) {
		this.certificateChainBytes = certificateChainBytes;
	}
}
