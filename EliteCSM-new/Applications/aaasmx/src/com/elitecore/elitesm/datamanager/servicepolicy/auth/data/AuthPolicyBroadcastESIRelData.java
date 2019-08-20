package com.elitecore.elitesm.datamanager.servicepolicy.auth.data;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

import net.sf.json.JSONObject;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.datamanager.externalsystem.data.ExternalSystemInterfaceInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData;

public class AuthPolicyBroadcastESIRelData extends BaseData implements  Serializable,Differentiable{

	private static final long serialVersionUID = 1L;
	private String  authPolicyId; 
	private String esiInstanceId;
	private String ruleSet;
	private String trueOnAttributeNotFound;
	private String isResponseMandatory;
	private ExternalSystemInterfaceInstanceData externalSystemData;
	private String translationMapConfigId;
	private String script;
	private TranslationMappingConfData translationMappingConfData;

	public String getIsResponseMandatory() {
		return isResponseMandatory;
	}
	public void setIsResponseMandatory(String isResponseMandatory) {
		this.isResponseMandatory = isResponseMandatory;
	}
	public String getTrueOnAttributeNotFound() {
		return trueOnAttributeNotFound;
	}
	public void setTrueOnAttributeNotFound(String trueOnAttributeNotFound) {
		this.trueOnAttributeNotFound = trueOnAttributeNotFound;
	}
	public String getRuleSet() {
		return ruleSet;
	}
	public void setRuleSet(String ruleSet) {
		this.ruleSet = ruleSet;
	}

	public String getAuthPolicyId() {
		return authPolicyId;
	}
	public void setAuthPolicyId(String authPolicyId) {
		this.authPolicyId = authPolicyId;
	}
	public String getEsiInstanceId() {
		return esiInstanceId;
	}
	public void setEsiInstanceId(String esiInstanceId) {
		this.esiInstanceId = esiInstanceId;
	}

	public ExternalSystemInterfaceInstanceData getExternalSystemData() {
		return externalSystemData;
	}
	public void setExternalSystemData(
			ExternalSystemInterfaceInstanceData externalSystemData) {
		this.externalSystemData = externalSystemData;
	}
	public String getTranslationMapConfigId() {
		return translationMapConfigId;
	}
	public void setTranslationMapConfigId(String translationMapConfigId) {
		this.translationMapConfigId = translationMapConfigId;
	}
	public String getScript() {
		return script;
	}
	public void setScript(String script) {
		this.script = script;
	}
	public TranslationMappingConfData getTranslationMappingConfData() {
		return translationMappingConfData;
	}
	public void setTranslationMappingConfData(
			TranslationMappingConfData translationMappingConfData) {
		this.translationMappingConfData = translationMappingConfData;
	}
	public String toString(){
		StringWriter out = new StringWriter();
		PrintWriter writer = new PrintWriter(out);
		writer.println();
		writer.println("------------ AuthPolicyBroadcastESIRelData ------------");
		writer.println("authPolicyId 	 :"+authPolicyId);
		writer.println("esiInstanceId    :"+esiInstanceId);
		writer.println("ruleSet   :"+ruleSet);
		writer.println("translationMapConfigId   :"+translationMapConfigId);
		writer.println("script   :"+script);
		writer.println("-----------------------------------------------------");
		writer.println();
		writer.close();
		return out.toString() ;
	}
	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		
		JSONObject innerObject = new JSONObject();
		innerObject.put("Ruleset", ruleSet);
		innerObject.put("True On Attribute Not Found", trueOnAttributeNotFound);
		innerObject.put("Is Response Mandatory", isResponseMandatory);
		if(translationMappingConfData!=null){
			innerObject.put("Translation Mapping", translationMappingConfData.getName());
		}
		object.put("Script", script);

		if(externalSystemData!=null){
			object.put(externalSystemData.getName(), innerObject);
		}
		return object;
	}
}
