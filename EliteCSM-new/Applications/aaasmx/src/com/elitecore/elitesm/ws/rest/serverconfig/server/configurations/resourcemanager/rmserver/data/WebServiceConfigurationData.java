package com.elitecore.elitesm.ws.rest.serverconfig.server.configurations.resourcemanager.rmserver.data;

import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.elitesm.util.constants.RestValidationMessages;

@XmlType(propOrder = {"webServiceEnabled","serviceAddress","maxSession","threadPoolSize"})
public class WebServiceConfigurationData{
	
	@NotEmpty(message="Web Service Enabled value must be specified")
	private String webServiceEnabled;
	
	private String serviceAddress;
	
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
	@Pattern(regexp=RestValidationMessages.REGEX_TRUE_FALSE,message="Invalid value of Web Service Enable (it could be true or false)")
	public String getWebServiceEnabled() {
		return this.webServiceEnabled;
	}
	public void setWebServiceEnabled(String webServiceEnabled){
		this.webServiceEnabled = webServiceEnabled.toLowerCase();
	}

}