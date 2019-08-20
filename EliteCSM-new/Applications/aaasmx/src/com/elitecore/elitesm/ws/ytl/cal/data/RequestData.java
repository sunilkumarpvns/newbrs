package com.elitecore.elitesm.ws.ytl.cal.data;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "request")
public class RequestData {
	
	private String version;
	private String principal;
	private String credentials;
	private TargetData target;

	@XmlAttribute(name = "version")
	public String getVersion() {
		return version;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
	
	@XmlAttribute(name = "principal")
	public String getPrincipal() {
		return principal;
	}
	
	public void setPrincipal(String principal) {
		this.principal = principal;
	}
	
	@XmlAttribute(name = "credentials")
	public String getCredentials() {
		return credentials;
	}
	
	public void setCredentials(String credentials) {
		this.credentials = credentials;
	}
	
	@XmlElement(name = "target")
	public TargetData getTarget() {
		return target;
	}
	
	public void setTarget(TargetData target) {
		this.target = target;
	}
}