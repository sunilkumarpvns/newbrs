package com.elitecore.elitesm.datamanager.servermgr.eap.data;

import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.elitesm.util.constants.RestValidationMessages;

@XmlType(propOrder ={"expiryDate","revokedCertificate","missingClientCertificate","macValidation"})
public class TLSCertificateValidation {
	private String expiryDate; 
	private String macValidation;
	private String revokedCertificate;
	private String missingClientCertificate;
	
	@XmlElement(name="expiry-date")
	@Pattern(regexp="|"+RestValidationMessages.REGEX_TRUE_FALSE,message="Invalid Expiry Date Value(true|false)")
	public String getExpiryDate() {
		return expiryDate;
	}
	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}
	
	@XmlElement(name="mac-validation")
	@Pattern(regexp="|"+RestValidationMessages.REGEX_TRUE_FALSE,message="Invalid Mac Validation Value(true|false)")
	public String getMacValidation() {
		return macValidation;
	}
	public void setMacValidation(String macValidation) {
		this.macValidation = macValidation;
	}
	
	@XmlElement(name="revoked-certificate")
	@Pattern(regexp="|"+RestValidationMessages.REGEX_TRUE_FALSE,message="Invalid Revoked Certificate Value(true|false)")
	public String getRevokedCertificate() {
		return revokedCertificate;
	}
	public void setRevokedCertificate(String revokedCertificate) {
		this.revokedCertificate = revokedCertificate;
	}
	
	@XmlElement(name="missing-client-certificate")
	@Pattern(regexp="|"+RestValidationMessages.REGEX_TRUE_FALSE,message="Invalid Missing Client Certificate Value(true|false)")
	public String getMissingClientCertificate() {
		return missingClientCertificate;
	}
	public void setMissingClientCertificate(String missingClientCertificate) {
		this.missingClientCertificate = missingClientCertificate;
	}
}
