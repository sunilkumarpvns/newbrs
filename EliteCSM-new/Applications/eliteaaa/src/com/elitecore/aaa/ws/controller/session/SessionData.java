package com.elitecore.aaa.ws.controller.session;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="session-data")
public class SessionData {
	
	private String key;
	private String value;
	private String protocol;
	
	public SessionData() {
	}
	
	public SessionData(String fieldName, String fieldValue, String sessionFor) {
		this.key=fieldName;
		this.value=fieldValue;
		this.protocol = sessionFor;
	}
	
	@XmlElement(name = "session-key")
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@XmlElement(name = "value")
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	
	@XmlElement(name="session-for")
	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	
}
