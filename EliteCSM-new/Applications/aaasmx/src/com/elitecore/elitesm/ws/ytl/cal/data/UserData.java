package com.elitecore.elitesm.ws.ytl.cal.data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "user")
public class UserData {
	
	private String name;
	private String loginName;
	private Password password;
	
	private Organization organization;
	private ProfileSet profileSet;
	private Domain domain;
	private Status status;
	
	private String newLoginName;
	
	@XmlElement(name = "name")
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@XmlElement(name = "login-name",type = String.class)
	public String getLoginName() {
		return loginName;
	}
	
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	
	@XmlElement(name = "password")
	public Password getPassword() {
		return password;
	}
	
	public void setPassword(Password password) {
		this.password = password;
	}
	
	@XmlElement(name = "organization")
	public Organization getOrganization() {
		return organization;
	}
	
	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	@XmlElement(name = "domain")
	public Domain getDomain() {
		return domain;
	}
	
	public void setDomain(Domain domain) {
		this.domain = domain;
	}
	
	@XmlElement(name = "profile-set")
	public ProfileSet getProfileSet() {
		return profileSet;
	}
	
	public void setProfileSet(ProfileSet profileSet) {
		this.profileSet = profileSet;
	}

	@XmlElement(name = "status")
	public Status getStatus() {
		return status;
	}
	
	public void setStatus(Status status) {
		this.status = status;
	}

	@XmlElement(name = "new-login-name")
	public String getNewLoginName() {
		return newLoginName;
	}
	
	public void setNewLoginName(String newLoginName) {
		this.newLoginName = newLoginName;
	}
}