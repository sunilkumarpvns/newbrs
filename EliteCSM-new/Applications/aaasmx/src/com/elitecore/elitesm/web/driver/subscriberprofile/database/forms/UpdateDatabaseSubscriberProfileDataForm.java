package com.elitecore.elitesm.web.driver.subscriberprofile.database.forms;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data.DatabaseSubscriberProfileRecordBean;
import com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data.IDatabaseSubscriberProfileRecordBean;
import com.elitecore.elitesm.web.radius.base.forms.BaseDictionaryForm;

public class UpdateDatabaseSubscriberProfileDataForm extends BaseDictionaryForm{
	
	private String strFieldId;
	private String strFieldName;
	private String driverInstanceId;
	private String dbAuthId;
	
	private List<IDatabaseSubscriberProfileRecordBean> lstDataRecordField = new ArrayList<IDatabaseSubscriberProfileRecordBean>();
	private String action;
	private String lstDataRecord; 
	private List paramPoolValue = new ArrayList();
	private String dateFormat;
	private List<String> uniqueKeyList;
	private String strType;
	
	public String getStrType() {
		return strType;
	}

	public void setStrType(String strType) {
		this.strType = strType;
	}

	public List<String> getUniqueKeyList() {
		return uniqueKeyList;
	}
	
	public void setUniqueKeyList(List<String> uniqueKeyList) {
		this.uniqueKeyList = uniqueKeyList;
	}
	public List getParamPoolValue() {
		return paramPoolValue;
	}
	public void setParamPoolValue(List paramPoolValue) {
		this.paramPoolValue = paramPoolValue;
	}
	public List<IDatabaseSubscriberProfileRecordBean> getLstDataRecordField() {
		return lstDataRecordField;
	}
	public void setLstDataRecordField(List<IDatabaseSubscriberProfileRecordBean> lstDataRecordField) {
		this.lstDataRecordField = lstDataRecordField;
	}	
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}

	public String getDriverInstanceId() {
		return driverInstanceId;
	}
	public void setDriverInstanceId(String driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
	}
	public String getDbAuthId() {
		return dbAuthId;
	}
	public void setDbAuthId(String dbAuthId) {
		this.dbAuthId = dbAuthId;
	}
	public String getLstDataRecord() {
		return lstDataRecord;
	}
	public void setLstDataRecord(String lstDataRecord) {
		this.lstDataRecord = lstDataRecord;
	}		
	public String getFieldValue(int index) {
		return ((DatabaseSubscriberProfileRecordBean)lstDataRecordField.get(index)).getFieldValue();
	}	
	public void setFieldValue(int index, String fieldValue) {
		while(lstDataRecordField.size() - 1 < index)
			lstDataRecordField.add(new DatabaseSubscriberProfileRecordBean());
		((DatabaseSubscriberProfileRecordBean)lstDataRecordField.get(index)).setFieldValue(fieldValue);
	}	
	public String getFieldName(int index) {
		return ((DatabaseSubscriberProfileRecordBean)lstDataRecordField.get(index)).getFieldName();
	}	
	public void setFieldName(int index, String fieldName) {
		while(lstDataRecordField.size() - 1 < index)
			lstDataRecordField.add(new DatabaseSubscriberProfileRecordBean());
		((DatabaseSubscriberProfileRecordBean)lstDataRecordField.get(index)).setFieldName(fieldName);
	}
	public String getFieldId(int index) {
		return ((DatabaseSubscriberProfileRecordBean)lstDataRecordField.get(index)).getFieldId();
	}
	public void setFieldId(int index,String fieldId) {
		while(lstDataRecordField.size() - 1 < index)
			lstDataRecordField.add(new DatabaseSubscriberProfileRecordBean());
		((DatabaseSubscriberProfileRecordBean)lstDataRecordField.get(index)).setFieldId(fieldId);
	}
	public int getNullableValue(int index){
		return ((DatabaseSubscriberProfileRecordBean)lstDataRecordField.get(index)).getNullableValue();
	}
	public String getColumnTypeName(int index){
		return ((DatabaseSubscriberProfileRecordBean)lstDataRecordField.get(index)).getColumnTypeName();
	}
	public String getStrFieldId() {
		return strFieldId;
	}
	public void setStrFieldId(String strFieldId) {
		this.strFieldId = strFieldId;
	}
	public String getStrFieldName() {
		return strFieldName;
	}
	public void setStrFieldName(String strFieldName) {
		this.strFieldName = strFieldName;
	}
	public String getDateFormat(){
		return dateFormat;
	}
	public void setDateFormat(String dateFormat){
		this.dateFormat=dateFormat;
	}
}
