package com.elitecore.elitesm.ws.rest.serverconfig.server.configurations.elitecsmserver.eliteaaaserver.data;

import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.servermgr.eap.EAPConfigBLManager;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;

@XmlType(propOrder = {"webServiceEnabled","serviceAddress","httpsServiceAddress","serverCertificateProfileId","maxSession","threadPoolSize"})
@ValidObject
public class WebServiceConfigurationData implements Validator{
	
	@NotEmpty(message="Web Service Enabled value must be specified")
	private String webServiceEnabled;
	
	private String serviceAddress;
	private String httpsServiceAddress;
	
	@NotEmpty(message="Server Certificate Profile name must be specified")
	private String serverCertificateProfileId;
	
	private String maxSession;
	private String threadPoolSize;

	public  WebServiceConfigurationData() {
		serviceAddress ="0.0.0.0:8080";
		maxSession="1";
		threadPoolSize="10";
	}
	

	@XmlElement(name = "service-address")
	public String getServiceAddress() {
		return serviceAddress;
	}
	public void setServiceAddress(String serviceAddress) {
		this.serviceAddress = serviceAddress;
	}
	
	@XmlElement(name = "https-service-address")
	public String getHttpsServiceAddress() {
		return httpsServiceAddress;
	}
	public void setHttpsServiceAddress(String httpServiceAddress) {
		this.httpsServiceAddress = httpServiceAddress;
	}
	
	@XmlElement(name = "max-session")
	@Pattern(regexp=RestValidationMessages.REGEX_NUMERIC_POSITIVE,message="Max Session must be numeric")
	public String getMaxSession() {
		return maxSession;
	}
	public void setMaxSession(String maxSession){
		this.maxSession = maxSession;
	}

	@XmlElement(name = "thread-pool-size")
	@Pattern(regexp=RestValidationMessages.REGEX_NUMERIC_POSITIVE,message="Thread Pool Size must be numeric")
	public String getThreadPoolSize() {
		return threadPoolSize;
	}
	public void setThreadPoolSize(String threadPoolSize){
		this.threadPoolSize = threadPoolSize;
	}
	
	@XmlElement(name = "web-service-enabled")
	@Pattern(regexp=RestValidationMessages.REGEX_TRUE_FALSE,message="Invalid value of Web Service Enable (true/false)")
	public String getWebServiceEnabled() {
		return this.webServiceEnabled;
	}
	public void setWebServiceEnabled(String webServiceEnabled){
		this.webServiceEnabled = webServiceEnabled.toLowerCase();
	}
	
	@XmlElement(name = "server-certificate-id")
	public String getServerCertificateProfileId() {
		return serverCertificateProfileId;
	}
	
	public void setServerCertificateProfileId(String serverCertificateProfileId) {
		this.serverCertificateProfileId = serverCertificateProfileId;
	}


	@Override
	public boolean validate(ConstraintValidatorContext context) {
		boolean isValid = true;
		if(RestValidationMessages.NONE_WITH_HYPHEN.equalsIgnoreCase(serverCertificateProfileId) == false && Strings.isNullOrBlank(serverCertificateProfileId) == false){
			try{
				EAPConfigBLManager eapBLManager = new EAPConfigBLManager();
				String serverCertifiacateId = eapBLManager.getServerCertificateIdFromName(serverCertificateProfileId);
				
			}catch(Exception e){
				isValid = false;
				RestUtitlity.setValidationMessage(context, "Configured "+serverCertificateProfileId+" Server Certificate Profile does not exists");
			}
		}
		return isValid;
	}
}