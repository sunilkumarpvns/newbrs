package com.elitecore.diameterapi.diameter.translator.data;

import java.util.List;

import com.elitecore.diameterapi.diameter.translator.data.impl.CopyPacketMappingDataImpl;

public interface CopyPacketTranslationConfigurationData {

	public String getMappingName();

	public String getInExpression() ;

	public List<CopyPacketMappingDataImpl> getRequestMappingDataList() ;

	public List<CopyPacketMappingDataImpl> getResponseMappingDataList() ;

	boolean isDummyMappingEnabled();

}
