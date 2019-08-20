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

@XmlType(propOrder ={"eapTtlsCertificateRequest","ttlsNegotiationMethod"})
public class EAPTTLSConfiguration {
	
	@NotNull(message="TTLS Negotiation Method must be specified")
	private Integer ttlsNegotiationMethod;
	
	@NotEmpty(message="TTLS Certificate Request must be specified")
	private String eapTtlsCertificateRequest;
	
	@XmlElement(name="ttls-negotiation-method")
	@XmlJavaTypeAdapter(value=EAPTTLSNegotiationMethod.class)
	@Min(value=0,message="Invalid TTLS Negotiation Method Name")
	public Integer getTtlsNegotiationMethod() {
		return ttlsNegotiationMethod;
	}
	public void setTtlsNegotiationMethod(Integer ttlsNegotiationMethod) {
		this.ttlsNegotiationMethod = ttlsNegotiationMethod;
	}
	
	@XmlElement(name="ttls-certificate-request")
	@Pattern(regexp=RestValidationMessages.TRUE_FALSE_WITH_EMPTY,message="Invalid TTLS Certificate Request Value (true/false)")
	public String getEapTtlsCertificateRequest() {
		return eapTtlsCertificateRequest;
	}
	public void setEapTtlsCertificateRequest(String eapTtlsCertificateRequest) {
		this.eapTtlsCertificateRequest = eapTtlsCertificateRequest.toLowerCase();
	}
}
