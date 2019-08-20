package com.elitecore.aaa.radius.conf.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {})
public class ChargingGatewayDetails {

	private String script;
	private String translationMappingName;
	boolean acceptOnTimeOut;
	boolean enabled;
	private String ruleset;
	private List<ChargingGatewayDetail> chargingSystemDetailsList;
	public ChargingGatewayDetails() {
		//required by Jaxb.
		chargingSystemDetailsList = new ArrayList<ChargingGatewayDetail>();
	}

	@XmlElement(name = "script",type = String.class)
	public String getScript() {
		return script;
	}
	public void setScript(String script) {
		this.script = script;
	}
	@XmlElement(name ="translation-mapping-name",type = String.class)
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

	@XmlElementWrapper(name = "charging-servers")
	@XmlElement(name = "charging-server")
	public List<ChargingGatewayDetail> getChargingSystemDetailsList() {
		return chargingSystemDetailsList;
	}
	public void setChargingSystemDetailsList(List<ChargingGatewayDetail> chargingSystemDetailsList) {
		this.chargingSystemDetailsList = chargingSystemDetailsList;
	}
	
	@Override
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println("	-- Charging Communication Detail --  ");
		out.println("	Communication Enabled = " + this.enabled);
		out.println("	Accept On TimeOut     = " + this.acceptOnTimeOut);
		out.println("	RuleSet  			  = " + this.ruleset);
		out.println("	Charging Servers");
		if(this.chargingSystemDetailsList!=null){
			for(ChargingGatewayDetail system:chargingSystemDetailsList){
				out.println(system.toString());
			}
		}
		out.close();
		return stringBuffer.toString();
	}
}
