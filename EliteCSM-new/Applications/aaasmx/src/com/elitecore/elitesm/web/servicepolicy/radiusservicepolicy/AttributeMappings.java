package com.elitecore.elitesm.web.servicepolicy.radiusservicepolicy;

public class AttributeMappings {
	private Long attributeMapId;
	private String attributeId;
	private String dbFields;
	private String defaultValue;
	private String mandatory;
	private Long orderNumber;
	
	public Long getAttributeMapId() {
		return attributeMapId;
	}
	public void setAttributeMapId(Long attributeMapId) {
		this.attributeMapId = attributeMapId;
	}
	public String getAttributeId() {
		return attributeId;
	}
	public void setAttributeId(String attributeId) {
		this.attributeId = attributeId;
	}
	public String getDbFields() {
		return dbFields;
	}
	public void setDbFields(String dbFields) {
		this.dbFields = dbFields;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	public String getMandatory() {
		return mandatory;
	}
	public void setMandatory(String mandatory) {
		this.mandatory = mandatory;
	}
	public Long getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(Long orderNumber) {
		this.orderNumber = orderNumber;
	}
}
