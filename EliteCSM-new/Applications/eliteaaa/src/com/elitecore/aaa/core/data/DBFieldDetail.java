package com.elitecore.aaa.core.data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder={})
public class DBFieldDetail {
	
	public static final String DB_FIELD_DETAIL = "db-field-detail";
	public static final String DB_FIELD= "db-field";
	public static final String ATTRIBUTE_ID = "attribute-id";
	public static final String DEFAULT_VALUE = "default-value";
	public static final String MANDATORY = "mandatory";
	
	
	private String dbField;
	private String attributeId;
	private String defaultValue;
	private boolean isMandatory;
	
	public DBFieldDetail() {
		
	}
	
	public  DBFieldDetail(String dbField,String attributeId,String defaultValue,boolean isMandatory) {
		this.attributeId = attributeId;
		this.dbField = dbField;
		this.defaultValue = defaultValue;
		this.isMandatory = isMandatory;
	}

	@XmlElement(name="dbfield",type=String.class)
	public String getDbField() {
		return dbField;
	}

	@XmlElement(name="attribute-id",type=String.class)
	public String getAttributeId() {
		return attributeId;
	}

	@XmlElement(name="default-value",type=String.class)
	public String getDefaultValue() {
		return defaultValue;
	}
	@XmlElement(name="mandatory",type=boolean.class)
	public boolean getIsMandatory() {
		return isMandatory;
	}

	public void setDbField(String dbField) {
		this.dbField = dbField;
	}

	public void setAttributeId(String attributeId) {
		this.attributeId = attributeId;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public void setIsMandatory(boolean isMandatory) {
		this.isMandatory = isMandatory;
	}

	
}
