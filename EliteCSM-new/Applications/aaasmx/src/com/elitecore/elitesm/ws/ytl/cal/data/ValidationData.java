package com.elitecore.elitesm.ws.ytl.cal.data;

import static com.elitecore.commons.base.Strings.padStart;
import static com.elitecore.commons.base.Strings.repeat;
import static java.lang.String.format;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "validation-data")
public class ValidationData {
	
	private String organizationName;
	private String realmName;
	private List<String> profileSets = new ArrayList<String>();
	
	@XmlElement(name = "organization-name")
	public String getOrganizationName() {
		return organizationName;
	}
	
	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}
	
	@XmlElement(name = "realm-name")
	public String getRealmName() {
		return realmName;
	}
	
	public void setRealmName(String realmName) {
		this.realmName = realmName;
	}
	
	@XmlElementWrapper(name = "profile-set")
	@XmlElement(name = "name")
	public List<String> getProfileSets() {
		return profileSets;
	}
	
	public String toString() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println(repeat("-", 70));
		out.println(padStart("Data", 10, ' '));
		out.println(repeat("-", 70));
		out.println(format("%-30s: %s", "Organization", getOrganizationName()));
		out.println(format("%-30s: %s", "Realm", getRealmName()));
		out.println(format("%-30s: %s", "Profile Set", getProfileSets()));
		out.close();
		return writer.toString();
	}
}
