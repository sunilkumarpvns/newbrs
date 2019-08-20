package com.elitecore.aaa.ws.provider;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="session-detail")
public class SessionDetails {
	
	private long createdTime;
	private String sessionId;
	private long lastAccessedTime;
	private String frammedIpAddress;
	
	@XmlElement(name="frammedIP-Address")
	public String getFrammedIpAddress() {
		return frammedIpAddress;
	}
	public void setFrammedIpAddress(String frammedIpAddress) {
		this.frammedIpAddress = frammedIpAddress;
	}
	
	@XmlElement(name="created-time")
	public long getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}
	
	@XmlElement(name="session-id")
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	
	@XmlElement(name="last-accessed-time")
	public long getLastAccessedTime() {
		return lastAccessedTime;
	}
	public void setLastAccessedTime(long lastAccessedTime) {
		this.lastAccessedTime = lastAccessedTime;
	}
	
}
