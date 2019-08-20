package com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data;

public interface IDatabaseSubscriberProfileRecordBean {
	
	public String getFieldValue();
	public void setFieldValue(String fieldValue);
	public String getFieldId();
	public void setFieldId(String fieldId); 
	public String getFieldName(); 
	public void setFieldName(String fieldName);
	public int getNullableValue();
	public void setNullableValue(int nullableValue);
	public String getColumnTypeName();
	public void setColumnTypeName(String columnTypeName);
	public void setUniqueKeyConstraint(boolean isUniqueKey);
	public boolean isUniqueKeyConstraint();
}
