package com.elitecore.aaa.core.data;

public class AAADBSMAttributeConfData {
	
	private String attributeId;
	private String columnName;
	private String defaultValue;
	private boolean updateValueRequired;
	private String objectType;
	private boolean useInWhereClause;
	
	
	public String getAttributeId() {
		return attributeId;
	}
	public void setAttributeId(String attributeId) {
		this.attributeId = attributeId;
	}
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	public boolean isUpdateValueRequired() {
		return updateValueRequired;
	}
	public void setUpdateValueRequired(boolean updateValueRequired) {
		this.updateValueRequired = updateValueRequired;
	}
	public String getObjectType() {
		return objectType;
	}
	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}
	public boolean useInWhereClause() {
		return useInWhereClause;
	}
	public void setUseInWhereClause(boolean useInWhereClause) {
		this.useInWhereClause = useInWhereClause;
	}
	
}
