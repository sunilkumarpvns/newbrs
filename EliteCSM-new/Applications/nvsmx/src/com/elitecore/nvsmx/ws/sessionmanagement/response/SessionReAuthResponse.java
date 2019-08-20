package com.elitecore.nvsmx.ws.sessionmanagement.response;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.nvsmx.ws.interceptor.WebServiceResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;


@XmlRootElement(name = "sessionReAuthByResponse")
public class SessionReAuthResponse implements WebServiceResponse {
	
	private Integer responseCode;
	private String responseMessage;
	private String parameter1;
	private String parameter2;
	private String webServiceName;
	private String webServiceMethodName;
	
	public SessionReAuthResponse(Integer responseCode, String responseMessage,
			String parameter1,
			String parameter2,String webServiceName, String webServiceMethodName) {
		super();
		this.responseCode = responseCode;
		this.responseMessage = responseMessage;
		this.setParameter1(parameter1);
		this.setParameter2(parameter2);
		this.webServiceName = webServiceName;
		this.webServiceMethodName = webServiceMethodName;
	}

	//Used by JAXB
	public SessionReAuthResponse() {
		
	}
	
	@XmlElement(name="responseCode")
	public Integer getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(Integer responseCode) {
		this.responseCode = responseCode;
	}

	@XmlElement(name="responseMessage")
	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	@XmlTransient
	@Override
	@JsonIgnore
	public String getWebServiceMethodName() {
		return webServiceMethodName;
	}

	@XmlTransient
	@Override
	@JsonIgnore
	public String getWebServiceName() {
		return webServiceName;
	}

	@XmlElement(name="parameter1")
	@Override
	public String getParameter1() {
		return parameter1;
	}

	@XmlElement(name="parameter2")
	@Override
	public String getParameter2() {
		return parameter2;
	}

	public void setParameter1(String parameter1) {
		this.parameter1 = parameter1;
	}

	public void setParameter2(String parameter2) {
		this.parameter2 = parameter2;
	}
	
}
