package com.elitecore.aaa.core.conf.impl;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder={})
public class DbFiledMapping {


	private String attributeId;
	private String dbField;
	private String dataType;
	private String defaultValue;
	private boolean useDictionaryValue;
	private List<String> attributeIDList;

	@XmlElement(name="attribute-id" ,type=String.class)
	public String getAttributeId() {
		return attributeId;
	}
	@XmlElement(name="dbfield" ,type=String.class)
	public String getDbField() {
		return dbField;
	}
	@XmlElement(name="datatype" ,type=String.class)
	public String getDataType() {
		return dataType;
	}
	@XmlElement(name="default-value" ,type=String.class)
	public String getDefaultValue() {
		return defaultValue;
	}
	@XmlElement(name="use-dictionary-value" ,type=boolean.class)
	public boolean getUseDictionaryValue() {
		return useDictionaryValue;
	}
	
	public void setAttributeId(String attributeId) {
		this.attributeId = attributeId;;
	}
	public void setDbField(String dbField) {
		this.dbField =dbField;
	}
	public void setDataType(String dataType) {
		this.dataType =dataType;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue  =defaultValue;
	}
	public void setUseDictionaryValue(boolean useDictionaryValue) {
		this.useDictionaryValue = useDictionaryValue;
	}
	
	@XmlTransient
	public List<String> getAttributeIDList() {
		return attributeIDList;
	}
	
	public void setAttributeIDList(List<String> attributeIDList) {
		this.attributeIDList = attributeIDList;
	}
}