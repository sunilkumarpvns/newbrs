package com.elitecore.elitesm.ws.ytl.cal.data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "profile-set")
public class ProfileSet {
	
	private String qualifiedName;
	private Profile profile;
	
	@XmlElement(name = "qualified-name")
	public String getQualifiedName() {
		return qualifiedName;
	}

	public void setQualifiedName(String qualifiedName) {
		this.qualifiedName = qualifiedName;
	}
	
	@XmlElement(name = "profile")
	public Profile getProfile() {
		return profile;
	}
	
	public void setProfile(Profile profile) {
		this.profile = profile;
	}
}