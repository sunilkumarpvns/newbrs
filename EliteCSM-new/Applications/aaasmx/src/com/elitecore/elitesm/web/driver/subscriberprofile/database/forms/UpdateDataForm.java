package com.elitecore.elitesm.web.driver.subscriberprofile.database.forms;
import java.util.List;

import com.elitecore.elitesm.web.radius.base.forms.BaseDictionaryForm;

public class UpdateDataForm extends BaseDictionaryForm{	
	
	private List lstEqualFieldName;
	private List lstFieldData;
	private List lstfieldDataList;
    private String action;	
	private String serialNumber;
	private String datasourceId;
	private String fieldId;
	private String fieldName;
	private String fieldType;
	private long fieldValue;
	
		
	/*private long pageNumber;
	private long totalPages;
	private long totalRecords;	*/
	
	/*public String getDataType(int index) {
		return ((DatasourceSchemaData)lstEqualFieldName.get(index)).getDataType();
	}
	public void setDataType(int index,String dataType) {
		while(lstEqualFieldName.size() - 1 < index)
			lstEqualFieldName.add(new DatasourceSchemaData());
			 ((DatasourceSchemaData)lstEqualFieldName.get(index)).setSerialNumber(dataType);
	}
	
	public String getFieldName(int index) {
		return ((DatasourceSchemaData)lstEqualFieldName.get(index)).getFieldName();
	}
	public void setFieldName(int index, String fieldName) {
		while(lstEqualFieldName.size() - 1 < index)
			lstEqualFieldName.add(new DatasourceSchemaData());
			 ((DatasourceSchemaData)lstEqualFieldName.get(index)).setFieldName(fieldName);
	}
	
	public long getLength(int index) {
		return ((DatasourceSchemaData)lstEqualFieldName.get(index)).getLength();
	}
	public void setLength(int index,long length) {
		while(lstEqualFieldName.size() - 1 <index)
			lstEqualFieldName.add(new DatasourceSchemaData());
		    ((DatasourceSchemaData)lstEqualFieldName.get(index)).setLength(length);
	}
	
	public String getSerialNumber(int index) {
		return ((DatasourceSchemaData)lstEqualFieldName.get(index)).getSerialNumber();
	}
	public void setSerialNumber(int index,String serialNumber) {
		while(lstEqualFieldName.size() - 1 < index)
			lstEqualFieldName.add(new DatasourceSchemaData());
			 ((DatasourceSchemaData)lstEqualFieldName.get(index)).setSerialNumber(serialNumber);
	}		*/
	
		
	public List getLstEqualFieldName() {
		return lstEqualFieldName;
	}
	public void setLstEqualFieldName(List lstEqualFieldName) {
		this.lstEqualFieldName = lstEqualFieldName;
	}		
	public List getLstFieldData() {
		return lstFieldData;
	}
	public void setLstFieldData(List lstFieldData) {
		this.lstFieldData = lstFieldData;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	
	public String getDatasourceId() {
		return datasourceId;
	}
	public void setDatasourceId(String datasourceId) {
		this.datasourceId = datasourceId;
	}
	
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	/*public long getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(long pageNumber) {
		this.pageNumber = pageNumber;
	}
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public long getTotalPages() {
		return totalPages;
	}
	public void setTotalPages(long totalPages) {
		this.totalPages = totalPages;
	}
	public long getTotalRecords() {
		return totalRecords;
	}
	public void setTotalRecords(long totalRecords) {
		this.totalRecords = totalRecords;
	}*/
	public String getFieldId() {
		return fieldId;
	}
	public void setFieldId(String fieldId) {
		this.fieldId = fieldId;
	}
	public String getFieldType() {
		return fieldType;
	}
	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}
	public long getFieldValue() {
		return fieldValue;
	}
	public void setFieldValue(long fieldValue) {
		this.fieldValue = fieldValue;
	}
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public List getLstfieldDataList() {
		return lstfieldDataList;
	}
	public void setLstfieldDataList(List lstfieldDataList) {
		this.lstfieldDataList = lstfieldDataList;
	}
	
	
	
}
