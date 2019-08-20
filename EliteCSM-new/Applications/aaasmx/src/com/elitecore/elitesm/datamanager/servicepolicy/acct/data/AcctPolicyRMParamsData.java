package com.elitecore.elitesm.datamanager.servicepolicy.acct.data;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData;

public class AcctPolicyRMParamsData extends BaseData implements  Serializable{

	private static final long serialVersionUID = 1L;
	private Long acctPolicyId;
	private Long esiTypeId;
	private String acceptOnTimeout;
	private String communicationEnabled;
	private String ruleSet;
	private String translationMapConfigId;
	private TranslationMappingConfData translationMappingConfData;
	private String script;
	 
	
	public Long getAcctPolicyId() {
		return acctPolicyId;
	}
	public void setAcctPolicyId(Long acctPolicyId) {
		this.acctPolicyId = acctPolicyId;
	}
	public Long getEsiTypeId() {
		return esiTypeId;
	}
	public void setEsiTypeId(Long esiTypeId) {
		this.esiTypeId = esiTypeId;
	}
	public String getAcceptOnTimeout() {
		return acceptOnTimeout;
	}
	public String getRuleSet() {
		return ruleSet;
	}
	public void setRuleSet(String ruleSet) {
		this.ruleSet = ruleSet;
	}
	public void setAcceptOnTimeout(String acceptOnTimeout) {
		this.acceptOnTimeout = acceptOnTimeout;
	}
	public String getCommunicationEnabled() {
		return communicationEnabled;
	}
	public void setCommunicationEnabled(String communicationEnabled) {
		this.communicationEnabled = communicationEnabled;
	}
	public String getTranslationMapConfigId() {
		return translationMapConfigId;
	}
	public void setTranslationMapConfigId(String translationMapConfigId) {
		this.translationMapConfigId = translationMapConfigId;
	}
	public TranslationMappingConfData getTranslationMappingConfData() {
		return translationMappingConfData;
	}
	public void setTranslationMappingConfData(
			TranslationMappingConfData translationMappingConfData) {
		this.translationMappingConfData = translationMappingConfData;
	}
	public String getScript() {
		return script;
	}
	public void setScript(String script) {
		this.script = script;
	}
	public String toString(){
		StringWriter out = new StringWriter();
		PrintWriter writer = new PrintWriter(out);
		writer.println("--------------------- AcctPolicyRMParamsData ---------------------------");
		writer.println( "acctPolicyId              : "+acctPolicyId);
		writer.println( "esiTypeId                 : "+esiTypeId);
		writer.println( "acceptOnTimeout           : "+acceptOnTimeout);
		writer.println( "communicationEnabled      : "+communicationEnabled);
		writer.println("-----------------------------------------------------------------------------");
		writer.close();
		return out.toString();
	}
	

}
