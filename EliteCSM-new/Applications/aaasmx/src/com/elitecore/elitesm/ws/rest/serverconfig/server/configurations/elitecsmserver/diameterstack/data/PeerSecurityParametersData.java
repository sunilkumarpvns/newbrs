package com.elitecore.elitesm.ws.rest.serverconfig.server.configurations.elitecsmserver.diameterstack.data;

import java.util.TreeSet;

import javax.validation.ConstraintValidatorContext;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.servermgr.eap.EAPConfigBLManager;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.util.eapconfig.EAPConfigUtils;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;

@XmlType(propOrder={"minTlsVersion","maxTlsVersion","handShakeTimeout","serverCertificateId","clientCertificateRequest","enabledCiphersuites","certficateValidationData"})
@ValidObject
public class PeerSecurityParametersData implements Validator{

	@NotEmpty(message="Maximum TLS Version must be specified")
	private String maxTlsVersion;
	
	@NotEmpty(message="Minimum TLS Version must be specified")
	private String minTlsVersion;
	
	private String handShakeTimeout;
	
	@NotEmpty(message="Server Certificate Profile must be specified")
	private String serverCertificateId;
	
	@NotEmpty(message="Client Certificate Request must be specified")
	private String clientCertificateRequest;
	
	private String enabledCiphersuites;
	
	private CertificationValidationData certficateValidationData ;
	
	public PeerSecurityParametersData(){
		this.certficateValidationData = new CertificationValidationData();
		this.handShakeTimeout = "3000";
	}
	@XmlElement(name="min-tls-version")
	@Pattern(regexp=RestValidationMessages.REGEX_TLS_VERSION,message="Enter valid Minimum TLS version(TLSv1,TLSv1.1,TLSv1.2)")
	public String getMinTlsVersion() {
		return minTlsVersion;
	}
	@XmlElement(name="max-tls-version")
	@Pattern(regexp=RestValidationMessages.REGEX_TLS_VERSION,message="Enter valid Minimum TLS version(TLSv1,TLSv1.1,TLSv1.2)")
	public String getMaxTlsVersion() {
		return maxTlsVersion;
	}
	
	@XmlElement(name="handshake-timeout")
	@Pattern(regexp=RestValidationMessages.REGEX_NUMERIC_POSITIVE,message="Handshake Timeout must numeric")
	public String getHandShakeTimeout() {
		return handShakeTimeout;
	}	

	@XmlElement(name="server-certificate-id")
	public String getServerCertificateId() {
		return serverCertificateId;
	}
	
	@XmlElement(name="client-certificate-request")
	@Pattern(regexp = RestValidationMessages.REGEX_TRUE_FALSE,message = "Client certificate request value must be true or false")
	public String getClientCertificateRequest() {
		return clientCertificateRequest;
	}
	
	@XmlElement(name="enabled-cipher-suites")
	public String getEnabledCiphersuites() {
		return enabledCiphersuites;
	}
	
	@XmlElement(name="certificate-validation")
	@Valid
	public CertificationValidationData getCertficateValidationData() {
		return certficateValidationData;
	}

	public void setCertficateValidationData(
			CertificationValidationData certficateValidationData) {
		this.certficateValidationData = certficateValidationData;
	}
	public void setHandShakeTimeout(String handShakeTimeout) {
		this.handShakeTimeout = handShakeTimeout;
	}
	
	public void setServerCertificateId(String serverCertificateId) {
			this.serverCertificateId = serverCertificateId;
	}
	
	public void setMaxTlsVersion(String maxTlsVersion) {
		this.maxTlsVersion = maxTlsVersion;
	}
	
	public void setMinTlsVersion(String minTlsVersion) {
		this.minTlsVersion = minTlsVersion;
	}
	
	public void setEnabledCiphersuites(String enabledCiphersuites) {
		this.enabledCiphersuites = enabledCiphersuites;
	}
	
	public void setClientCertificateRequest(String peerCertificateRequest) {
		this.clientCertificateRequest = peerCertificateRequest.toLowerCase();
	}
	@Override
	public boolean validate(ConstraintValidatorContext context) {
		
		boolean isValid = true;
		if(Strings.isNullOrBlank(minTlsVersion) == false && Strings.isNullOrBlank(maxTlsVersion) == false){
			Boolean isValidTlsVersionSelection = EAPConfigUtils.isMaxTLSVersionGreaterThanOrEqualToMinTLSVersion(minTlsVersion, maxTlsVersion);
			if(isValidTlsVersionSelection == false){
				isValid = false;
				RestUtitlity.setValidationMessage(context, "Maximum TLS Version must be greater than or equals to Minimum TLS Version");
			} else{
				String cipherSuiteIds = "";
				//convert name to id
				if(Strings.isNullOrBlank(enabledCiphersuites) == false){
					cipherSuiteIds = EAPConfigUtils.convertCipherSuitesNamesToCipherSuiteCodes(enabledCiphersuites);
					// validate cipher suite name
					TreeSet<String> invalidCipherSuites = EAPConfigUtils.getTLSVersionSpecificUnsupportedCipherSuiteList(minTlsVersion,maxTlsVersion,cipherSuiteIds);
					if(Collectionz.isNullOrEmpty(invalidCipherSuites) == false){
						isValid = false	;
						RestUtitlity.setValidationMessage(context, "Invalid Cipher Suites :"+invalidCipherSuites);
					}
				}
			}
		}
		
		//validate server certificate name
		if(RestValidationMessages.NONE.equalsIgnoreCase(serverCertificateId) == false && Strings.isNullOrBlank(serverCertificateId) == false){
			try{
				EAPConfigBLManager eapBLManager = new EAPConfigBLManager();
				String serverCertifiacateId = eapBLManager.getServerCertificateIdFromName(serverCertificateId);
				
				if(Strings.isNullOrBlank(serverCertifiacateId)){
					isValid = false;
					RestUtitlity.setValidationMessage(context, "Invalid name of Server Certificate Profile");
				}
			}catch(Exception e){
				isValid = false;
				RestUtitlity.setValidationMessage(context, "Invalid name of Server Certificate Profile");
			}
		}
		return isValid;
	}

}
