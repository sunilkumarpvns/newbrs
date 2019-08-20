package com.elitecore.netvertex.core.tls;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Collection;
import java.util.List;

import com.elitecore.core.commons.tls.ServerCertificateProfile;
import com.elitecore.core.commons.tls.constant.PrivateKeyAlgo;

public class ServerCertificateProfileImpl implements ServerCertificateProfile {
	private Long id;
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
	
	
	private String privateKeyPassword;
	
	private PrivateKeyAlgo privateKeyAlgo;
	
	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public PrivateKey getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(PrivateKey privateKey) {
		this.privateKey = privateKey;
	}

	@Override
	public String getPrivateKeyPassword() {
		return privateKeyPassword;
	}
	
	@Override
	public String getPlainTextPrivateKeyPassword() {
		return privateKeyPassword;
	}

	public void setPrivateKeyPassword(String privateKeyPassword) {
		this.privateKeyPassword = privateKeyPassword;
	}

	@Override
	public PrivateKeyAlgo getPrivateKeyAlgo() {
		return privateKeyAlgo;
	}

	public void setPrivateKeyAlgo(PrivateKeyAlgo privateKeyAlgo) {
		this.privateKeyAlgo = privateKeyAlgo;
	}

	@Override
	public Collection<? extends Certificate > getCertificates() {
		return certificates;
	}
	
	public void setCertificates(Collection<? extends Certificate > certificates){
		this.certificates = certificates;
	}
	
	@Override
	public String getCertificatePath() {
		return certificatePath;
	}

	public void setCertificatePath(String certificatePath) {
		this.certificatePath = certificatePath;
	}

	@Override
	public String getPrivateKeyPath() {
		return privateKeyPath;
	}

	public void setPrivateKeyPath(String privateKeyPath) {
		this.privateKeyPath = privateKeyPath;
	}
	
	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String getUUID() {
		return null;
	}
	
	@Override
	public String getCertificateName() {
		return certificateName;
	}

	public void setCertificateName(String certificateName) {
		this.certificateName = certificateName;
	}

	@Override
	public String getPrivateKeyName() {
		return privateKeyName;
	}

	public void setPrivateKeyName(String privateKeyName) {
		this.privateKeyName = privateKeyName;
	}
	
	@Override
	public byte[] getCertificateFileBytes() {
		return certificateFileBytes;
	}

	public void setCertificateFileBytes(byte[] certificateFileBytes) {
		this.certificateFileBytes = certificateFileBytes;
	}
	
	@Override
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
		out.println();
		out.println(certificates.size() > 1 ? "    Server Certificate chain " : "     Server Certificate ");
		out.println(certificates);
		out.println();
		out.close();
		return stringBuffer.toString();
	}
	
	@Override
	public List<byte[]> getCertificateChainBytes() {
		return certificateChainBytes;
	}

	public void setCertificateChainBytes(List<byte[]> certificateChainBytes) {
		this.certificateChainBytes = certificateChainBytes;
	}
	
}
