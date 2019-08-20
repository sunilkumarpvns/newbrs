package com.elitecore.aaa.core.config;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


@XmlType(propOrder={})
public class JNDIProprtyDetail {
	

	private String property;
	private String value;
	
	public JNDIProprtyDetail() {
		// TODO Auto-generated constructor stub
	}
	
	@XmlElement(name="property",type=String.class)
	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	@XmlElement(name="value",type=String.class)
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
}
