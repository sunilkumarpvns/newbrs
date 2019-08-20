package com.elitecore.test.dependecy.diameter.packet.avps.threegpp;

import java.util.Map;

public interface TGPPField {
	
	public Map<String, Integer> getFieldValueMap(byte[] valueBuffer);
	
	public String getName();

}
