package com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

public class DatabaseSubscriberProfileRecordBean extends BaseData implements IDatabaseSubscriberProfileRecordBean{
	
	private String fieldValue;
	private String fieldId;
	private String fieldName;
	private String columnTypeName;
	private int nullableValue;
	private boolean uniqueKeyConstraint = false;
	
	public String getFieldValue() {
		return fieldValue;
	}
	
	public void setFieldValue(String fieldValue) {
		this.fieldValue = fieldValue;
	}
	
	public String getFieldId() {
		return fieldId;
	}
	
	public void setFieldId(String fieldId) {
		this.fieldId = fieldId;
	}
	
	public String getFieldName() {
		return fieldName;
	}
	
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public int getNullableValue() {
		return nullableValue;
	}
	
	public void setNullableValue(int nullableValue) {
		this.nullableValue=nullableValue;
	}
	public String getColumnTypeName(){
		return columnTypeName;
	}
	public void setColumnTypeName(String columnTypeName){
		this.columnTypeName=columnTypeName;
	}

	public void setUniqueKeyConstraint(boolean isUniqueKey) {
		this.uniqueKeyConstraint = isUniqueKey;
	}

	public boolean isUniqueKeyConstraint() {
		return this.uniqueKeyConstraint;
	}
}
