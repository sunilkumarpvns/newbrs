package com.elitecore.aaa.rm.translator.data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.diameterapi.core.translator.policy.data.impl.TranslatorPolicyDataImpl;

@XmlType(propOrder = {})
public class TranslationMappingConfigurationData {

	private String transMappingConfId;
	private TranslatorPolicyDataImpl translationPolicyDataObj;
	
	public TranslationMappingConfigurationData() {
		// Required by JAXB
		translationPolicyDataObj = new TranslatorPolicyDataImpl();
	}
	
	@XmlElement(name = "translation-id",type = String.class)
	public String getTransMappingConfId() {
		return transMappingConfId;
	}

	public void setTransMappingConfId(String transMappingConfId) {
		this.transMappingConfId = transMappingConfId;
	}

	public void setTranslationPolicyDataObj(TranslatorPolicyDataImpl translationPolicyDataObj){
		this.translationPolicyDataObj = translationPolicyDataObj;
	}
	@XmlElement(name ="translation-mapping-policy")
	public TranslatorPolicyDataImpl getTranslationPolicyDataObj() {
		return translationPolicyDataObj;
	}
}