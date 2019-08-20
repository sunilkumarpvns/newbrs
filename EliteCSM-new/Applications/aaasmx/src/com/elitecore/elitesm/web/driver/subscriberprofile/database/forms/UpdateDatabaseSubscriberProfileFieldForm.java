package com.elitecore.elitesm.web.driver.subscriberprofile.database.forms;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data.DatasourceSchemaData;
import com.elitecore.elitesm.web.radius.base.forms.BaseDictionaryForm;

public class UpdateDatabaseSubscriberProfileFieldForm extends BaseDictionaryForm{
	
	private String driverInstanceId;
	private String dbAuthId;
	
	private String action;
	private String status;
	private List lstDatasourceSchema = new ArrayList();
	
	private int itemIndex;
	private String toggleAll;
	private String[]select;
	private Set<String> dataTypeSet = new TreeSet<String>();
	


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

	public String getAction() {
		return action;
	}
	
	public void setAction(String action) {
		this.action = action;
	}
	
	public int getItemIndex() {
		return itemIndex;
	}
	
	public void setItemIndex(int itemIndex) {
		this.itemIndex = itemIndex;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}	
	
	public String getDataType(int index) {
		return ((DatasourceSchemaData)lstDatasourceSchema.get(index)).getDataType();
	}
	
	public void setDataType(int index, String dataType) {
		while(lstDatasourceSchema.size() - 1 < index)
			lstDatasourceSchema.add(new DatasourceSchemaData());
		((DatasourceSchemaData)lstDatasourceSchema.get(index)).setDataType(dataType);
	}
	
	public String getFieldId(int index) {
		return ((DatasourceSchemaData)lstDatasourceSchema.get(index)).getFieldId();
	}
	
	public void setFieldId(int index, String fieldId) {
		while(lstDatasourceSchema.size() - 1 < index)
			lstDatasourceSchema.add(new DatasourceSchemaData());
		((DatasourceSchemaData)lstDatasourceSchema.get(index)).setFieldId(fieldId);
	}
	
	public String getFieldName(int index) {
		return ((DatasourceSchemaData)lstDatasourceSchema.get(index)).getFieldName();
	}
	
	public void setFieldName(int index, String fieldName) {
		while(lstDatasourceSchema.size() - 1 < index)
			lstDatasourceSchema.add(new DatasourceSchemaData());
		((DatasourceSchemaData)lstDatasourceSchema.get(index)).setFieldName(fieldName);
	}
	
	public String getDisplayName(int index) {
		return ((DatasourceSchemaData)lstDatasourceSchema.get(index)).getDisplayName();
	}
	
	public void setDisplayName(int index, String displayName) {
		while(lstDatasourceSchema.size() - 1 < index)
			lstDatasourceSchema.add(new DatasourceSchemaData());
		((DatasourceSchemaData)lstDatasourceSchema.get(index)).setDisplayName(displayName);
	}
	
	public long getLength(int index) {
		return ((DatasourceSchemaData)lstDatasourceSchema.get(index)).getLength();
	}
	
	public void setLength(int index, long length) {
		while(lstDatasourceSchema.size() - 1 < index)
			lstDatasourceSchema.add(new DatasourceSchemaData());
		((DatasourceSchemaData)lstDatasourceSchema.get(index)).setLength(length);
	}
	
	public List getLstDatasourceSchema() {
		return lstDatasourceSchema;
	}
	public void setLstDatasourceSchema(List lstDatasourceSchema) {
		this.lstDatasourceSchema = lstDatasourceSchema;
	}
	public String getToggleAll() {
		return toggleAll;
	}
	public void setToggleAll(String toggleAll) {
		this.toggleAll = toggleAll;
	}
	
	public String getSerialNumber(int index) {
		return ((DatasourceSchemaData)lstDatasourceSchema.get(index)).getSerialNumber();
	}
	public void setSerialNumber(int index,String serialNumber) {
		while(lstDatasourceSchema.size() - 1 < index)
			lstDatasourceSchema.add(new DatasourceSchemaData());
		((DatasourceSchemaData)lstDatasourceSchema.get(index)).setSerialNumber(serialNumber);
	}	
	public String[] getSelect() {
		return select;
	}
	public void setSelect( String[] select ) {
		this.select = select;
	}
	public Set<String> getDataTypeSet() {
		return dataTypeSet;
		
	}
	public void setDataTypeSet(Set<String> dataTypeSet) {
		this.dataTypeSet = dataTypeSet;
	}
	
}
