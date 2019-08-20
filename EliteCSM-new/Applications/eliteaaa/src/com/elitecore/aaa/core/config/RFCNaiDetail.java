package com.elitecore.aaa.core.config;

import static com.elitecore.commons.base.Strings.repeat;
import static java.lang.String.format;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
@XmlType(propOrder = {})
public class RFCNaiDetail {

	private boolean enabled = false;
	private String realmName;
	public RFCNaiDetail(){
		//required by Jaxb.
	}
	
	@XmlElement(name = "enabled",type = boolean.class,defaultValue = "false")
	public boolean getEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@XmlElement(name = "realm-names",type = String.class)
	public String getRealmName() {
		return realmName;
	}
	public void setRealmName(String realmName) {
		this.realmName = realmName;
	}	
	
	@Override
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println(repeat("-", 70));
		out.println(format("%-30s: %s", "NAI RFC 5729 Enabled", 
				getEnabled()));
		out.println(format("%-30s: %s", "Supported Realms", 
				getRealmName()));
		out.close();
		return stringBuffer.toString();
	}
}
