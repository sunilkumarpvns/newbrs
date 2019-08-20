package com.elitecore.aaa.core.conf.impl;

import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {})
public class KeyValuePair {
	private String key;
	private String attributeid;
	private String defaultValue;

	public KeyValuePair(){
		
	}
	
	public KeyValuePair(String key,String attributeId,String defaultValue) {
		this.key = key;
		this.attributeid = attributeId;
		this.defaultValue = defaultValue;
	}
	
	public void setKey(String key){
		this.key = key;
	}		
	public String getKey(){
		return key;
	}
	
	public void setAttributeId(String attributeId){
		this.attributeid = attributeId;
		
	}
	public String getAttributeId(){
		return attributeid;
	}
	
	public void setDefaultValue(String defaultValue){
		this.defaultValue = defaultValue;
	}
	public String getDefaultValue(){
		return defaultValue;
	}
	@Override
	public int hashCode() {
		return 1;
	}
	@Override
	public boolean equals(Object obj) {
		KeyValuePair keyValuePair = (KeyValuePair)obj;
		if(this.getKey() != null && !this.getKey().equalsIgnoreCase(keyValuePair.getKey()))
			return false;
		if(this.getAttributeId() != null && !this.getAttributeId().equalsIgnoreCase(keyValuePair.getAttributeId()))
			return false;			
		if(this.getDefaultValue() != null && !this.getDefaultValue().equalsIgnoreCase(keyValuePair.getDefaultValue()))
			return false;
		return true;
	}
}
