package com.elitecore.nvsmx.ws.subscriberprovisioning.response;

import com.elitecore.nvsmx.ws.interceptor.WebServiceResponse;
import com.elitecore.nvsmx.ws.subscriberprovisioning.data.SubscriberProfile;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.List;

@XmlRootElement
public class SubscriberProvisioningResponse implements WebServiceResponse{

	private Integer responseCode;
	private String responseMessage;
	private String parameter1;
	private String parameter2;
	private List<SubscriberProfile> subscriberProfile;
	private String webServiceName;
	private String webServiceMethodName;

	public SubscriberProvisioningResponse(){

	}
	public SubscriberProvisioningResponse(Integer responseCode, String responseMessage, List<SubscriberProfile> subscriberProfile,
			String parameter1, String parameter2, String webServiceName, String webServiceMethodName) {
		this.responseCode = responseCode;
		this.responseMessage = responseMessage;
		this.subscriberProfile = subscriberProfile;
		this.parameter1 = parameter1;
		this.parameter2 = parameter2;
		this.webServiceName = webServiceName;
		this.webServiceMethodName = webServiceMethodName;
	}

	public Integer getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(Integer responseCode) {
		this.responseCode = responseCode;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	public String getParameter1() {
		return parameter1;
	}

	public void setParameter1(String parameter1) {
		this.parameter1 = parameter1;
	}

	public String getParameter2() {
		return parameter2;
	}

	public void setParameter2(String parameter2) {
		this.parameter2 = parameter2;
	}

	public List<SubscriberProfile> getSubscriberProfile() {
		return subscriberProfile;
	}

	public void setSubscriberProfile(List<SubscriberProfile> subscriberProfile) {
		this.subscriberProfile = subscriberProfile;
	}

	@XmlTransient
	@Override
	@JsonIgnore
	public String getWebServiceName() {
		return webServiceName;
	}

	@XmlTransient
	@Override
	@JsonIgnore
	public String getWebServiceMethodName() {
		return webServiceMethodName;
	}
}