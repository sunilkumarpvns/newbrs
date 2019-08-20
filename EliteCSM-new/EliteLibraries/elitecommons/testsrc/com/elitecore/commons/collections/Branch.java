package com.elitecore.commons.collections;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "branch")
public class Branch {
	
	private String value;
	private String key;
	
	@XmlAttribute(name = "key", required = true)
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
	@XmlAttribute(name = "value", required = true)
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

}
