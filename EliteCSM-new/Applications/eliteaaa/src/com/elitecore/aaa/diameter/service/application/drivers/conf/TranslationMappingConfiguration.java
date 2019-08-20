package com.elitecore.aaa.diameter.service.application.drivers.conf;

import java.util.Map;

import com.elitecore.diameterapi.core.translator.policy.data.TranslatorPolicyData;

public interface TranslationMappingConfiguration {

	public TranslatorPolicyData getTranslatorPolicyData(String transMappingConfId);
	
	public Map<String,TranslatorPolicyData> getTranslatorPolicyDataMap();
}
