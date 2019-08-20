package com.elitecore.elitesm.web.diameter.diameterconcurrency;

public class ConcurrencyFieldMappingData {
	private String field;
	private String dbFieldName;
	private String referringAttribute;
	private Long dataType;
	private String defaultValue;
	private String includeInASR;

	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public String getDbFieldName() {
		return dbFieldName;
	}
	public void setDbFieldName(String dbFieldName) {
		this.dbFieldName = dbFieldName;
	}
	public String getReferringAttribute() {
		return referringAttribute;
	}
	public void setReferringAttribute(String referringAttribute) {
		this.referringAttribute = referringAttribute;
	}
	public Long getDataType() {
		return dataType;
	}
	public void setDataType(Long dataType) {
		this.dataType = dataType;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	public String getIncludeInASR() {
		return includeInASR;
	}
	public void setIncludeInASR(String includeInASR) {
		this.includeInASR = includeInASR;
	}
}
