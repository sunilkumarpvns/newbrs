package com.elitecore.aaa.radius.conf.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder={})
public class AcctPrepaidSystemDetails {

	private String script;
	private String translationMappingName;
	private boolean acceptOnTimeOut;
	private boolean enabled;
	private String ruleset;
	private List<PrepaidSystemDetail> prepaidSystemDetailsList;


	public AcctPrepaidSystemDetails() {
		prepaidSystemDetailsList = new ArrayList<PrepaidSystemDetail>();
	}
	@XmlElement(name = "script",type = String.class)
	public String getScript() {
		return script;
	}
	public void setScript(String script) {
		this.script = script;
	}
	@XmlElement(name = "translation-mapping-name",type = String.class)
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

	@XmlElementWrapper(name = "prepaid-servers")
	@XmlElement(name = "prepaid-server")
	public List<PrepaidSystemDetail> getPrepaidSystemDetailsList() {
		return prepaidSystemDetailsList;
	}
	public void setPrepaidSystemDetailsList(List<PrepaidSystemDetail> prepaidSystemDetailsList) {
		this.prepaidSystemDetailsList = prepaidSystemDetailsList;
	}
	@Override
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println("	-- Prepaid Communication Detail --  ");
		out.println("	Communication Enabled = " + this.enabled);
		out.println("	Accept On TimeOut     = " + this.acceptOnTimeOut);
		out.println("	RuleSet  			  = " + this.ruleset);
		out.println("	Prepaid Servers");
		if(this.prepaidSystemDetailsList!=null){
			for(PrepaidSystemDetail system:this.prepaidSystemDetailsList){
				out.println(system.toString());
			}
		}
		out.close();
		return stringBuffer.toString();
	}
}
