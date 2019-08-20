package com.elitecore.aaa.ws.config;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="response")
public class EliteResponse {
	private String responseMsg;
	private String responseCode;
	
	public EliteResponse() {
		// need for JAXB
	}
	
	public EliteResponse(String msg, String resultCode) {
		this.responseMsg = msg;
		this.responseCode = resultCode;
	}
	
	@XmlElement(name="message")
	public String getResponseMsg() {
		return responseMsg;
	}
	public void setResponseMsg(String responseMsg) {
		this.responseMsg = responseMsg;
	}
	
	@XmlElement(name="response-code")
	public String getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	
	
}
