package com.elitecore.elitesm.ws.rest.serverconfig.server.configurations.elitecsmserver.diameterstack.data;

import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.validator.constraints.NotEmpty;

@XmlType(propOrder={"validateCertificateExpiry","validateCertificateCA","validateCertificateRevocation"})
public class CertificationValidationData {
	
	@NotEmpty(message="Expiry Date value must be specified")
	private String validateCertificateExpiry;
	
	@NotEmpty(message="Revoked Certificate value must be specified")
	private String validateCertificateRevocation;
	
	@NotEmpty(message="Unknown CA value must be specified")
	private String validateCertificateCA;
	
	public CertificationValidationData() {}
	
	@XmlElement(name="validate-certificate-expiry")
	@Pattern(regexp = "true|false",message = "Invalid value of Expiry date (true/false)")
	public String getValidateCertificateExpiry() {
		return validateCertificateExpiry;
	}
	
	@XmlElement(name="validate-certificate-revocation")
	@Pattern(regexp = "true|false",message = "Invalid value of Revoked Certification (true/false)")
	public String getValidateCertificateRevocation() {
		return validateCertificateRevocation;
	}
	
	@XmlElement(name="validate-certificate-ca")
	@Pattern(regexp = "true|false",message = "Invalid value of Unknown CA (true/false)")
	public String getValidateCertificateCA() {
		return validateCertificateCA;
	}
	
	public void setValidateCertificateExpiry(String validateCertificateExpiry) {
		this.validateCertificateExpiry = validateCertificateExpiry.toLowerCase();
	}
	
	public void setValidateCertificateRevocation(
			String validateCertificateRevocation) {
		this.validateCertificateRevocation = validateCertificateRevocation.toLowerCase();
	}
	
	public void setValidateCertificateCA(String validateCertificateCA) {
		this.validateCertificateCA = validateCertificateCA.toLowerCase();
	}
}
