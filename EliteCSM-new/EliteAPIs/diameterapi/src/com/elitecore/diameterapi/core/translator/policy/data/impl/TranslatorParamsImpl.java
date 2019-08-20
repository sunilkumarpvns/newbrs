package com.elitecore.diameterapi.core.translator.policy.data.impl;

import java.util.HashMap;
import java.util.Map;

import com.elitecore.diameterapi.core.translator.TranslatorConstants;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;

public class TranslatorParamsImpl implements TranslatorParams {
	private Map<String, Object> paramsMap = new HashMap<String, Object>();

	public TranslatorParamsImpl(Object fromPacketType,Object toPacketType){
		
		paramsMap.put(TranslatorConstants.FROM_PACKET,fromPacketType);
		paramsMap.put(TranslatorConstants.TO_PACKET, toPacketType);
		
	}
	public TranslatorParamsImpl(Object fromPacketType,Object toPacketType,Object sourceRequest,Object destinationRequest){
	
		paramsMap.put(TranslatorConstants.FROM_PACKET,fromPacketType);
		paramsMap.put(TranslatorConstants.TO_PACKET, toPacketType);
		paramsMap.put(TranslatorConstants.SOURCE_REQUEST,sourceRequest);
		paramsMap.put(TranslatorConstants.DESTINATION_REQUEST, destinationRequest);
	}
	
	@Override
	public Object getParam(String key) {		
		return paramsMap.get(key);
	}

	@Override
	public void setParam(String key, Object value) {
		paramsMap.put(key, value);
	}

}
