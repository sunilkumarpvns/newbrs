package com.elitecore.elitesm.web.driver.subscriberprofile.database.forms;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data.DatabaseSubscriberProfileRecordBean;
import com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data.DatasourceSchemaData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data.IDatabaseSubscriberProfileRecordBean;
import com.elitecore.elitesm.web.radius.base.forms.BaseDictionaryForm;

public class AddDatabaseSubscriberProfileDataForm extends BaseDictionaryForm{

	private static final long serialVersionUID = 1L;
	private String dbAuthId;
	private String driverInstanceId;
	private String action;
	private String strFieldId;
	private String strFieldName;
	private String columnTypeName;
	private List<IDatabaseSubscriberProfileRecordBean> lstDatabaseDatasourceDataSchema = new ArrayList<IDatabaseSubscriberProfileRecordBean>();
	private int nullableValue;
	private String dateFormat;
	
	private List paramPoolValue = new ArrayList();
	
	private List test;
	
	public List getParamPoolValue() {
		return paramPoolValue;
	}
	public void setParamPoolValue(List paramPoolValue) {
		this.paramPoolValue = paramPoolValue;
	}

	public String getAction(){
		return action;
	}
	public void setAction(String action){
		this.action=action;
	}
	public List<IDatabaseSubscriberProfileRecordBean> getLstDatabaseDatasourceDataSchema(){
		return lstDatabaseDatasourceDataSchema;
	}
	public void setLstDatabaseDatasourceDataSchema(List<IDatabaseSubscriberProfileRecordBean> lstDatabaseDatasourceDataSchema){
		this.lstDatabaseDatasourceDataSchema=lstDatabaseDatasourceDataSchema;
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
	public String getFieldValue(int index) {
		return ((DatabaseSubscriberProfileRecordBean)lstDatabaseDatasourceDataSchema.get(index)).getFieldValue();
	}	
	public void setFieldValue(int index, String fieldValue) {
		while(lstDatabaseDatasourceDataSchema.size() - 1 < index)
			lstDatabaseDatasourceDataSchema.add(new DatabaseSubscriberProfileRecordBean());
		((DatabaseSubscriberProfileRecordBean)lstDatabaseDatasourceDataSchema.get(index)).setFieldValue(fieldValue);
	}	
	public String getFieldName(int index) {
		return ((DatabaseSubscriberProfileRecordBean)lstDatabaseDatasourceDataSchema.get(index)).getFieldName();
	}	
	public void setFieldName(int index, String fieldName) {
		while(lstDatabaseDatasourceDataSchema.size() - 1 < index)
			lstDatabaseDatasourceDataSchema.add(new DatabaseSubscriberProfileRecordBean());
		((DatabaseSubscriberProfileRecordBean)lstDatabaseDatasourceDataSchema.get(index)).setFieldName(fieldName);
	}
	public String getFieldId(int index) {
		return ((DatabaseSubscriberProfileRecordBean)lstDatabaseDatasourceDataSchema.get(index)).getFieldId();
	}
	public void setFieldId(int index,String fieldId) {
		while(lstDatabaseDatasourceDataSchema.size() - 1 < index)
			lstDatabaseDatasourceDataSchema.add(new DatabaseSubscriberProfileRecordBean());
		((DatabaseSubscriberProfileRecordBean)lstDatabaseDatasourceDataSchema.get(index)).setFieldId(fieldId);
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
	public int getNullableValue(int index){
		return ((DatabaseSubscriberProfileRecordBean)lstDatabaseDatasourceDataSchema.get(index)).getNullableValue();
	}
	public String getColumnTypeName(int index){
		return ((DatabaseSubscriberProfileRecordBean)lstDatabaseDatasourceDataSchema.get(index)).getColumnTypeName();
	}
	public String getDateFormat(){
		return dateFormat;
	}
	public void setDateFormat(String dateFormat){
		this.dateFormat=dateFormat;
	}
	
	public Set getDbdsParamPoolValueSet(int index) {
		return ((DatasourceSchemaData)test.get(index)).getDbdsParamPoolValueSet();
	}
	public void setDbdsParamPoolValueSet(int index, Set dbdsParamPoolValueSet) {
		while(test.size() - 1 < index)
			test.add(new DatasourceSchemaData());
		((DatasourceSchemaData)test.get(index)).setDbdsParamPoolValueSet(dbdsParamPoolValueSet);
	}
}
