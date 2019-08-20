package com.elitecore.diameterapi.core.translator;

import com.elitecore.diameterapi.core.common.InitializationFailedException;
import com.elitecore.diameterapi.core.common.TranslationFailedException;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorPolicyData;


/**
 * The implementor class should be able to Translate the
 * Request/Response by two ways.
 * 
 * @author jatin
 *
 */

public interface Translator {
	
	public String getFromId();
	
	public String getToId();
	
	public void init(TranslatorPolicyData policyData) throws InitializationFailedException;
	
	public void postTranslateRequest(String policyId, TranslatorParams params);
	
	public void translateRequest(String policyDataId, TranslatorParams params) throws TranslationFailedException;
	
	public void translateResponse(String policyDataId, TranslatorParams params)throws TranslationFailedException;
	
	public void postTranslateResponse(String policyId, TranslatorParams params);
	
}
