package com.elitecore.netvertexsm.datamanager.servermgr.drivers.dbcdrdriver.data;

public class DBCDRFieldMappingData {
	private Long fieldMappingID;
	private String pcrfKey;
	private String dbField;
	private Long dataType;
	private String defaultValue;
	private Long dbCDRDriverID;
	
	public Long getFieldMappingID() {
		return fieldMappingID;
	}
	public void setFieldMappingID(Long fieldMappingID) {
		this.fieldMappingID = fieldMappingID;
	}
	public String getPcrfKey() {
		return pcrfKey;
	}
	public void setPcrfKey(String pcrfKey) {
		this.pcrfKey = pcrfKey;
	}
	public String getDbField() {
		return dbField;
	}
	public void setDbField(String dbField) {
		this.dbField = dbField;
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
	public Long getDbCDRDriverID() {
		return dbCDRDriverID;
	}
	public void setDbCDRDriverID(Long dbCDRDriverID) {
		this.dbCDRDriverID = dbCDRDriverID;
	}
}