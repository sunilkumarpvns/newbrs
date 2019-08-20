package com.elitecore.aaa.radius.conf.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {})
public class IpPoolDetails {


	private String scriptName;
	private String translationMappingName;
	private List<IpPoolSystemDetail> ipPoolSystemDetailsList;
	private boolean acceptOnTimeOut;
	private boolean enabled;
	private String ruleset;

	public IpPoolDetails() {
		//required by Jaxb.
		ipPoolSystemDetailsList = new ArrayList<IpPoolSystemDetail>();
	}
	@XmlElement(name = "script",type = String.class)
	public String getScriptName() {
		return scriptName;
	}
	public void setScriptName(String scriptName) {
		this.scriptName = scriptName;
	}
	@XmlElement(name = "translation-mapping-name",type =String.class)
	public String getTranslationMappingName() {
		return translationMappingName;
	}
	public void setTranslationMappingName(String translationMappingName) {
		this.translationMappingName = translationMappingName;
	}

	@XmlElement(name ="accept-on-timeout",type = boolean.class)
	public boolean getIsAcceptOnTimeOut() {
		return acceptOnTimeOut;
	}
	public void setIsAcceptOnTimeOut(boolean acceptOnTimeOut) {
		this.acceptOnTimeOut = acceptOnTimeOut;
	}
	@XmlElement(name ="enabled",type = boolean.class)
	public boolean getIsEnabled() {
		return enabled;
	}
	public void setIsEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	@XmlElement(name ="ruleset",type = String.class)
	public String getRuleset() {
		return ruleset;
	}
	public void setRuleset(String ruleset) {
		this.ruleset = ruleset;
	}

	@XmlElementWrapper(name = "ip-pool-servers")
	@XmlElement(name = "ip-pool-server")
	public List<IpPoolSystemDetail> getIpPoolSystemDetailsList() {
		return ipPoolSystemDetailsList;
	}
	public void setIpPoolSystemDetailsList(List<IpPoolSystemDetail> ipPoolSystemDetailsList) {
		this.ipPoolSystemDetailsList = ipPoolSystemDetailsList;
	}
	
	@Override
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println("	-- IP Pool Communication Detail --  ");
		out.println("	Communication Enabled = " + this.enabled);
		out.println("	Accept On TimeOut     = " + this.acceptOnTimeOut);
		out.println("	RuleSet  			  = " + this.ruleset);
		out.println("	IP Pool Servers");
		if(this.ipPoolSystemDetailsList!=null){
			for(IpPoolSystemDetail system:ipPoolSystemDetailsList){
				out.println(system.toString());
			}
		}
		out.close();
		return stringBuffer.toString();
	}
	

}
