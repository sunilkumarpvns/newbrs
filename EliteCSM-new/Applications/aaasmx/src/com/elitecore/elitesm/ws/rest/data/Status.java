package com.elitecore.elitesm.ws.rest.data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "response")
@XmlType(propOrder = {"responseCode","message"})
public class Status {
	
	private String responseCode;
	private String message;

	public Status() {
	}
	
	public Status(String reCode,String msg) {
		this.responseCode = reCode;
		this.message = msg;
	}
	
	@XmlElement(name = "response-code")
	public String getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	@XmlElement(name = "response-message")
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
