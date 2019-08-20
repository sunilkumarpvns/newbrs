package com.elitecore.core.commons.tls;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.cert.X509Certificate;

public class TrustedCADetail {
	private String certificateFileName;
	private String name;
	
	//Transient properties
	private X509Certificate certificate;
	private byte[] certificateFileBytes;
	private byte[] encodedCertificateBytes;
	
	@XmlTransient
	public byte[] getEncodedCertificateBytes() {
		return encodedCertificateBytes;
	}

	public void setEncodedCertificateBytes(byte[] encodedCertificateBytes) {
		this.encodedCertificateBytes = encodedCertificateBytes;
	}

	@XmlTransient
	public X509Certificate getCertificate() {
		return certificate;
	}
	
	public void setCertificate(X509Certificate certificate) {
		this.certificate = certificate;
	}
	
	public void setCertificateFileName(String certificateFileName) {
		this.certificateFileName = certificateFileName;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setCertificatelFileBytes(byte[] certificateFileBytes) {
		this.certificateFileBytes = certificateFileBytes;
	}
	
	@XmlElement(name="certificate-file-name",type=String.class)
	public String getCertificateFileName() {
		return certificateFileName;
	}
	
	@XmlElement(name="trusted-ca-detail-name",type=String.class)
	public String getName() {
		return name;
	}
	
	@XmlTransient
	public byte[] getCertificateFileBytes() {
		return certificateFileBytes;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((certificate == null) ? 0 : certificate.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		if (obj == null)
			return false;
		
		if (getClass() != obj.getClass())
			return false;
		
		TrustedCADetail other = (TrustedCADetail) obj;
		if (certificate == null) {
			return other.certificate == null;
		} 
		
		return this.certificate.equals(other.certificate);
	}
	
	@Override
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println("Name: " + name);
		out.println("Certificate File Name: " + certificateFileName);
		out.println(certificate);
		out.println();
		out.close();
		return stringBuffer.toString();
	}
}
