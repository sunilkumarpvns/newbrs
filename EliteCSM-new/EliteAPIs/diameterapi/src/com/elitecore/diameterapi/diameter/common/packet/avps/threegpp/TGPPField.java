package com.elitecore.diameterapi.diameter.common.packet.avps.threegpp;

import java.util.Map;

public interface TGPPField {
	
	public Map<String, Integer> getFieldValueMap(byte []valueBuffer);
	
	public String getName();

}
