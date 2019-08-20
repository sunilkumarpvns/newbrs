package com.elitecore.diameterapi.diameter.translator;

import java.util.Map;

import com.elitecore.diameterapi.core.common.InitializationFailedException;
import com.elitecore.diameterapi.core.common.TranslationFailedException;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;

public interface CopyPacketTranslator {

	public String getName();
	
	public void init() throws InitializationFailedException;
	
	public void postTranslateRequest(TranslatorParams params);
	public void postTranslateResponse(TranslatorParams params);	
	
	public void translateRequest(TranslatorParams params) throws TranslationFailedException;
	public void translateResponse(TranslatorParams params)throws TranslationFailedException;

	public Map<String, String> getDummyMappings();
	
	
}
