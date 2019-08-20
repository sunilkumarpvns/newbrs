package com.elitecore.diameterapi.core.translator.policy.data;

import java.util.List;
import java.util.Map;

import com.elitecore.core.commons.config.core.UserDefined;
import com.elitecore.diameterapi.core.translator.policy.data.impl.TranslationDetailImpl;

public interface TranslatorPolicyData extends UserDefined{
	
	public String getTransMapConfId();
	
	public String getName();
	
	public String getFromTranslatorId();
	
	public String getToTranslatorId();
	
	public List<TranslationDetailImpl> getTranslationDetailList();
	
	public boolean getIsDummyResponse();
	
	public Map<String, String> getDummyResponseMap();
	
	public String getBaseTranslationMappingId() ;
	
	public String getScript();
}
