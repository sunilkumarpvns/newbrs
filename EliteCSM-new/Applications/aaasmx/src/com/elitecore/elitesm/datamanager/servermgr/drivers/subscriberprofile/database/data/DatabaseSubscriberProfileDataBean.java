package com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;


public class DatabaseSubscriberProfileDataBean extends BaseData implements IDatabaseSubscriberProfileDataBean{
	
	private String fieldId;
	private String fieldName;
	private String fieldType;
	private Object fieldValue;
	
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
	public String getFieldType() {
		return fieldType;
	}
	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}
	public Object getFieldValue() {
		return fieldValue;
	}
	public void setFieldValue(Object fieldValue) {
		this.fieldValue = fieldValue;
	}
	public String toString()
	{
		String str = "--------- DataSource Data --------\n"+
					 " Field Id    = "+fieldId +"\n" +
					 " Field Name  = "+fieldName +"\n" +
					 " Field Type  = "+fieldType +"\n" +
					 " Field Value = "+fieldValue ;
		return str;
		
	}
}
