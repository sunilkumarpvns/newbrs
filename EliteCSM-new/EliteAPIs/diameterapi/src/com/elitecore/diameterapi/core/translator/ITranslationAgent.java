package com.elitecore.diameterapi.core.translator;

import java.util.Map;

import com.elitecore.diameterapi.core.common.TranslationFailedException;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;

//FIXME
public interface ITranslationAgent {

	public void translate(String policyId, TranslatorParams params, boolean isRequest)
			throws TranslationFailedException;
	
	public boolean isExists(String transMappName);

	public Map<String, String> getDummyResponseMap(String translationMapping);

}
