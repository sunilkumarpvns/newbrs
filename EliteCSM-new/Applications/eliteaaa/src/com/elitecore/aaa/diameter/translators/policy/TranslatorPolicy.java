package com.elitecore.aaa.diameter.translators.policy;

import java.util.Map;

import com.elitecore.aaa.diameter.translators.policy.data.ParsedMappingInfo;
import com.elitecore.diameterapi.core.common.InitializationFailedException;

public interface TranslatorPolicy{
	
	public void init() throws InitializationFailedException;

	public String getPolicyId();
	
	public String getFromInterpreterId();
	
	public String getToInterpreterId();
	
	public boolean isInitialized();
	
	public Map<String,String> getDummyParametersMap();
	
	public String getBaseTranslationMappingId() ;
	
	public String getName();
	
	public String getScript();

	public Map<String,ParsedMappingInfo> getParsedRequestMappingInfoMap();
	
	public Map<String,ParsedMappingInfo> getParsedResponseMappingInfoMap();

}
