package com.elitecore.aaa.core.conf;

import java.util.List;

import com.elitecore.aaa.core.conf.impl.KeyValuePair;


public interface VSAInClassConfiguration {
	
	public boolean getIsEnabled(); 
	public String[] getClassAttributeId(); 
	public char getSeparator() ;
	public List<KeyValuePair> getAttrbiutesFromReqeustPacket();
	public List<KeyValuePair> getAttributesFromResponsePacket(); 
	public String getStrPrefix(); 

}
