package com.elitecore.elitesm.datamanager.diameter.diameterpeerprofile.data;

import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.elitecore.elitesm.ws.rest.adapter.LowerCaseConvertAdapter;

@XmlRootElement(name="certificate-validation")
public class CertificateValidationCheckBox {
	
	public CertificateValidationCheckBox() {
		setExpiryDate("true");
		setUnknownCA("true");
		setRevokedCertificate("true");
		setUnknownHost("true");
	}
	
	@Pattern(regexp = "true|false", message = "Invalid value of Expiry Date. Value could be 'true' or 'false'.")
	private String expiryDate; 
	
	@Pattern(regexp = "true|false", message = "Invalid value of Unknown CA. Value could be 'true' or 'false'.")
	private String unknownCA;
	
	@Pattern(regexp = "true|false", message = "Invalid value of Revoked Certificate. Value could be 'true' or 'false'.")
	private String revokedCertificate;
	
	@Pattern(regexp = "true|false", message = "Invalid value of Unknown Host. Value could be 'true' or 'false'.")
	private String unknownHost;
	
	@XmlElement(name = "expiry-date")
	@XmlJavaTypeAdapter(LowerCaseConvertAdapter.class)
	public String getExpiryDate() {
		return expiryDate;
	}
	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}
	
	@XmlElement(name = "unknown-ca")
	@XmlJavaTypeAdapter(LowerCaseConvertAdapter.class)
	public String getUnknownCA() {
		return unknownCA;
	}
	public void setUnknownCA(String unknownCA) {
		this.unknownCA = unknownCA;
	}
	
	@XmlElement(name = "revoked-certificate")
	@XmlJavaTypeAdapter(LowerCaseConvertAdapter.class)
	public String getRevokedCertificate() {
		return revokedCertificate;
	}
	public void setRevokedCertificate(String revokedCertificate) {
		this.revokedCertificate = revokedCertificate;
	}
	
	@XmlElement(name = "unknown-host")
	@XmlJavaTypeAdapter(LowerCaseConvertAdapter.class)
	public String getUnknownHost() {
		return unknownHost;
	}
	public void setUnknownHost(String unknownHost) {
		this.unknownHost = unknownHost;
	}
	
}
