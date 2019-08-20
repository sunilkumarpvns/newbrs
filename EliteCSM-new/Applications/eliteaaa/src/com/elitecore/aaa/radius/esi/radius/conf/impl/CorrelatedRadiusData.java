package com.elitecore.aaa.radius.esi.radius.conf.impl;

import javax.xml.bind.annotation.XmlElement;

public class CorrelatedRadiusData {
	
	private String id;
	private String name;
	private String description;
	private String authEsiId;
	private String acctEsiId;
	private String authEsiName;
	private String acctEsiName;
	
	@XmlElement(name = "id")
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	@XmlElement(name = "name")
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	
	@XmlElement(name = "description")
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	
	@XmlElement(name = "auth-esi-id")
	public String getAuthEsiId() {
		return authEsiId;
	}
	
	public void setAuthEsiId(String authEsiId) {
		this.authEsiId = authEsiId;
	}
	
	@XmlElement(name = "acct-esi-id")
	public String getAcctEsiId() {
		return acctEsiId;
	}
	
	public void setAcctEsiId(String acctEsiId) {
		this.acctEsiId = acctEsiId;
	}

	@XmlElement(name = "auth-esi-name")
	public String getAuthEsiName() {
		return authEsiName;
	}

	public void setAuthEsiName(String authEsiName) {
		this.authEsiName = authEsiName;
	}

	@XmlElement(name = "acct-esi-name")
	public String getAcctEsiName() {
		return acctEsiName;
	}

	public void setAcctEsiName(String acctEsiName) {
		this.acctEsiName = acctEsiName;
	}
	
	
	
}
