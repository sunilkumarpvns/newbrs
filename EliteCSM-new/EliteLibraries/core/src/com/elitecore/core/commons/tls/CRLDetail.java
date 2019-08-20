package com.elitecore.core.commons.tls;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.cert.X509CRL;
import java.util.List;

public class CRLDetail {
	private String crlFileName;
	private String name;
	
	//Transient properties
	private List<X509CRL> crls;
	private byte[] crlFileBytes;
	
	@XmlTransient
	public List<X509CRL> getCRLs() {
		return crls;
	}
	
	public void setCrl(List<X509CRL> crl) {
		this.crls = crl;
	}
	
	public void setCrlFileName(String crlFileName) {
		this.crlFileName = crlFileName;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	
	@XmlElement(name="crl-file-name",type=String.class)
	public String getCrlFileName() {
		return crlFileName;
	}
	
	@XmlElement(name="crl-detail-name",type=String.class)
	public String getName() {
		return name;
	}
	
	@XmlTransient
	public byte[] getCrlFileBytes() {
		return crlFileBytes;
	}
	
	public void setCrlFileBytes(byte[] crlFileBytes) {
		this.crlFileBytes = crlFileBytes;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((crls == null) ? 0 : crls.hashCode());
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
		
		CRLDetail other = (CRLDetail) obj;
		if (crls == null) {
			return other.crls == null;
		} 
		
		for(X509CRL crl : other.crls ){
			if(!crls.contains(crl)){
				return false;
			}
		}
			
		return true;
	}
	
	@Override
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println();
		out.println(crls);
		out.close();
		return stringBuffer.toString();
	}
	
}
