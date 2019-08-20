package com.elitecore.diameterapi.core.translator.policy.data;

import java.util.List;

import com.elitecore.diameterapi.core.translator.policy.data.impl.MappingDataImpl;

public interface TranslationDetail {

	public String getInRequestType() ;

	public String getOutRequestType() ;

	public List<MappingDataImpl> getRequestMappingDataList() ;

	public List<MappingDataImpl> getResponseMappingDataList() ;

	public boolean getIsDummyResponse();

	// Added For Translation Mapping Name
	public String getMappingName();
}
