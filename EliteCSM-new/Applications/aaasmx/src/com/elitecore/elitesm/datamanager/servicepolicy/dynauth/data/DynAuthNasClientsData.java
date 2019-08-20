package com.elitecore.elitesm.datamanager.servicepolicy.dynauth.data;

import java.io.Serializable;
import java.util.Set;

import javax.validation.ConstraintValidatorContext;
import javax.validation.Valid;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.servermgr.copypacket.CopyPacketTransMapConfBLManager;
import com.elitecore.elitesm.blmanager.servermgr.transmapconf.TranslationMappingConfBLManager;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationConfData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.adapter.TranslationMappingNameToIDAdapter;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;

@XmlRootElement(name ="nas-client")
@XmlType(propOrder = {"ruleset", "dynaAuthNasClientDetailsData", "translationMapConfigId", "script"})
@ValidObject
public class DynAuthNasClientsData extends BaseData implements Serializable, Differentiable, Validator {

	private static final long serialVersionUID = 1L;
	private String dynaAuthNasId;
	private String ruleset;
	
	private String translationMapConfigId;
	private String copyPacketMapConfigId;
	private String dynAuthPolicyId;
	private String script;
	private Long orderNumber;
	
	@Valid
	private Set<DynAuthNasClientDetailData> dynaAuthNasClientDetailsData;
	
	
	private TranslationMappingConfData translationMappingConfData;
	private CopyPacketTranslationConfData copyPacketTranslationConfData;
	
	@XmlTransient
	public String getDynaAuthNasId() {
		return dynaAuthNasId;
	}

	public void setDynaAuthNasId(String dynaAuthNasId) {
		this.dynaAuthNasId = dynaAuthNasId;
	}

	@XmlElement(name = "ruleset")
	public String getRuleset() {
		return ruleset;
	}
	
	public void setRuleset(String ruleset) {
		this.ruleset = ruleset;
	}

	@XmlElement(name = "translation-mapping")
	@XmlJavaTypeAdapter(TranslationMappingNameToIDAdapter.class)
	public String getTranslationMapConfigId() {
		return translationMapConfigId;
	}

	public void setTranslationMapConfigId(String translationMapConfigId) {
		this.translationMapConfigId = translationMapConfigId;
	}

	@XmlTransient
	public String getDynAuthPolicyId() {
		return dynAuthPolicyId;
	}

	public void setDynAuthPolicyId(String dynAuthPolicyId) {
		this.dynAuthPolicyId = dynAuthPolicyId;
	}

	@XmlElement(name = "script")
	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	@XmlElementWrapper(name = "nas-client-details")
	@XmlElement(name = "nas-client", type = DynAuthNasClientDetailData.class)
	public Set<DynAuthNasClientDetailData> getDynaAuthNasClientDetailsData() {
		return dynaAuthNasClientDetailsData;
	}

	public void setDynaAuthNasClientDetailsData(
			Set<DynAuthNasClientDetailData> dynaAuthNasClientDetailsData) {
		this.dynaAuthNasClientDetailsData = dynaAuthNasClientDetailsData;
	}

	@XmlTransient
	public String getCopyPacketMapConfigId() {
		return copyPacketMapConfigId;
	}

	public void setCopyPacketMapConfigId(String copyPacketMapConfigId) {
		this.copyPacketMapConfigId = copyPacketMapConfigId;
	}

	@XmlTransient
	public Long getOrderNumber() {
		return orderNumber;
	}

	@Override
	public void setOrderNumber(Long orderNumber) {
		this.orderNumber = orderNumber;
	}

	@XmlTransient
	public TranslationMappingConfData getTranslationMappingConfData() {
		return translationMappingConfData;
	}

	public void setTranslationMappingConfData(TranslationMappingConfData translationMappingConfData) {
		this.translationMappingConfData = translationMappingConfData;
	}

	@XmlTransient
	public CopyPacketTranslationConfData getCopyPacketTranslationConfData() {
		return copyPacketTranslationConfData;
	}

	public void setCopyPacketTranslationConfData(
			CopyPacketTranslationConfData copyPacketTranslationConfData) {
		this.copyPacketTranslationConfData = copyPacketTranslationConfData;
	}
	
	@Override
	public String toString() {
		return "DynAuthNasClientsData [dynaAuthNasId=" + dynaAuthNasId
				+ ", ruleset=" + ruleset + ", translationMapConfigId="
				+ translationMapConfigId + ", copyPacketMapConfigId="
				+ copyPacketMapConfigId + ", dynAuthPolicyId="
				+ dynAuthPolicyId + ", script=" + script
				+ ", dynaAuthNasClientDetailsData="
				+ dynaAuthNasClientDetailsData + "]";
	}

	@Override
	public JSONObject toJson() {
		JSONObject innerObject = new JSONObject();
		JSONObject object = new JSONObject();
		if(dynaAuthNasClientDetailsData != null){
			JSONArray array = new JSONArray();
			for(DynAuthNasClientDetailData elClientsData :dynaAuthNasClientDetailsData){
				array.add(elClientsData.toJson());
			}
			object.put("NAS Clients Details ", array);
		}
		if(Strings.isNullOrBlank(translationMapConfigId) == false){
			object.put("Translation Mapping", getTranslationMappingConfData().getName());
		}
		if(Strings.isNullOrBlank(copyPacketMapConfigId) == false){
			object.put("Copy packet Mapping", getCopyPacketTranslationConfData().getName());
		}
		if(script != null && script.length() > 0)
			object.put("Script", script);
		
		if(ruleset != null && ruleset.length() > 0)
			object.put("Ruleset", ruleset);
		
		if( object != null ){
			innerObject.put("Nas Client Entries", object);
		}
		
		return innerObject;
	}

	@Override
	public boolean validate(ConstraintValidatorContext context) {
		
		boolean isValid = true;
		
		if(RestValidationMessages.INVALID.equals(this.translationMapConfigId)){
			isValid = false;
			RestUtitlity.setValidationMessage(context, "Translation Mapping does not exist.");
		}
		
		if (Strings.isNullOrBlank(translationMapConfigId) == false) {
			try {
				TranslationMappingConfBLManager translationMappingBLManager = new TranslationMappingConfBLManager();
				TranslationMappingConfData data = translationMappingBLManager.getTranslationMappingConfDataById(translationMapConfigId);
				setTranslationMappingConfData(data);
				copyPacketMapConfigId = null;
			} catch (Exception e) {
				try {
					CopyPacketTransMapConfBLManager copyTransMapConfBLManager = new CopyPacketTransMapConfBLManager();
					CopyPacketTranslationConfData copyPacketData = copyTransMapConfBLManager.getCopyPacketTransMapConfigDetailDataById(translationMapConfigId.toString());
					setCopyPacketTranslationConfData(copyPacketData);
					copyPacketMapConfigId = translationMapConfigId;
					translationMapConfigId = null;
				} catch (Exception ex){
					ex.printStackTrace();
				}
			}
		} 
		return isValid;
	}
}
