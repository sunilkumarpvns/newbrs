package com.elitecore.elitesm.datamanager.servermgr.eap.data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.adapter.eapconfig.EAPTTLSNegotiationMethod;

@XmlType(propOrder ={"eapPeapCertificateRequest","peapVersion","peapNegotiationMethod"})
public class EAPPeapConfiguration {
	
	@NotEmpty(message="Peap Version must be specified")
	private String peapVersion;
	
	@NotEmpty(message="Peap Certificate Request must be specified")
	private String eapPeapCertificateRequest;
	
	@NotNull(message="Peap Negotiation Method must be specified")
	private Integer peapNegotiationMethod;
	
	@XmlElement(name="peap-version")
	@Pattern(regexp="0|1",message="Invalid Peap Version Value(0 | 1)")
	public String getPeapVersion() {
		return peapVersion;
	}
	public void setPeapVersion(String peapVersion) {
		this.peapVersion = peapVersion;
	}
	
	@XmlElement(name="peap-certificate-request")
	@Pattern(regexp=RestValidationMessages.BOOLEAN_REGEX,message="Invalid Peap Certificate Request Value (true/false)")
	public String getEapPeapCertificateRequest() {
		return eapPeapCertificateRequest;
	}
	public void setEapPeapCertificateRequest(String eapPeapCertificateRequest) {
		this.eapPeapCertificateRequest = eapPeapCertificateRequest.toLowerCase();
	}
	
	@XmlElement(name="peap-negotiation-method")
	@XmlJavaTypeAdapter(value=EAPTTLSNegotiationMethod.class)
	@Min(value=0,message="Invalid peap negotiation method name")
	public Integer getPeapNegotiationMethod() {
		return peapNegotiationMethod;
	}
	public void setPeapNegotiationMethod(Integer peapNegotiationMethod) {
		this.peapNegotiationMethod = peapNegotiationMethod;
	}
}
