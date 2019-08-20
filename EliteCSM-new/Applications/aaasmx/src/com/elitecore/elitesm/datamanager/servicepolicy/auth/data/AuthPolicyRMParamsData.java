package com.elitecore.elitesm.datamanager.servicepolicy.auth.data;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData;

public class AuthPolicyRMParamsData extends BaseData implements  Serializable{

	private static final long serialVersionUID = 1L;
	private String authPolicyId;
	private Long esiTypeId;
	private String acceptOnTimeout;
	private String communicationEnabled;
	private Long defaultSessionTimeout;
	private String ratingClassAttribute;
	private Long driverInstanceId;
	private DriverInstanceData driverInstanceData;
	private String ruleSet;
	private String translationMapConfigId;
	private TranslationMappingConfData translationMappingConfData;
	private String script;

	
	
//	private String failureCDRFileName;
//	private String failureCDRFileLocation;
	
	 
	public String getAuthPolicyId() {
		return authPolicyId;
	}
	public void setAuthPolicyId(String authPolicyId) {
		this.authPolicyId = authPolicyId;
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
	public void setAcceptOnTimeout(String acceptOnTimeout) {
		this.acceptOnTimeout = acceptOnTimeout;
	}
	public String getCommunicationEnabled() {
		return communicationEnabled;
	}
	public void setCommunicationEnabled(String communicationEnabled) {
		this.communicationEnabled = communicationEnabled;
	}
	public Long getDefaultSessionTimeout() {
		return defaultSessionTimeout;
	}
	public void setDefaultSessionTimeout(Long defaultSessionTimeout) {
		this.defaultSessionTimeout = defaultSessionTimeout;
	}
	public String getRatingClassAttribute() {
		return ratingClassAttribute;
	}
	public void setRatingClassAttribute(String ratingClassAttribute) {
		this.ratingClassAttribute = ratingClassAttribute;
	}
	
//	public String getFailureCDRFileName() {
//		return failureCDRFileName;
//	}
//	public void setFailureCDRFileName(String failureCDRFileName) {
//		this.failureCDRFileName = failureCDRFileName;
//	}
//	public String getFailureCDRFileLocation() {
//		return failureCDRFileLocation;
//	}
//	public void setFailureCDRFileLocation(String failureCDRFileLocation) {
//		this.failureCDRFileLocation = failureCDRFileLocation;
//	}
	
	public String getRuleSet() {
		return ruleSet;
	}
	public void setRuleSet(String ruleSet) {
		this.ruleSet = ruleSet;
	}
	public Long getDriverInstanceId() {
		return driverInstanceId;
	}
	public DriverInstanceData getDriverInstanceData() {
		return driverInstanceData;
	}
	public void setDriverInstanceData(DriverInstanceData driverInstanceData) {
		this.driverInstanceData = driverInstanceData;
	}
	public void setDriverInstanceId(Long driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
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
		writer.println("--------------------- AuthPolicyRMParamsData ---------------------------");
		writer.println( "authPolicyId              : "+authPolicyId);
		writer.println( "esiTypeId                 : "+esiTypeId);
		writer.println( "acceptOnTimeout           : "+acceptOnTimeout);
		writer.println( "communicationEnabled      : "+communicationEnabled);
		writer.println( "defaultSessionTimeout     : "+defaultSessionTimeout);
		writer.println( "ratingClassAttribute      : "+ratingClassAttribute);
		writer.println( "driverInstanceId          : "+driverInstanceId);
		writer.println( "translationId             : "+translationMapConfigId);
		writer.println( "script                    : "+script);
//		writer.println( "failureCDRFileName        : "+failureCDRFileName);
//		writer.println( "failureCDRFileLocation    : "+failureCDRFileLocation);
		writer.println("-----------------------------------------------------------------------------");
		writer.close();
		return out.toString();
	}
	

}
