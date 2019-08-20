package com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.web.driver.subscriberprofile.database.forms.SQLPoolValueData;

public class DatasourceSchemaData extends BaseData implements Serializable,IDatasourceSchemaData{
	
	private String fieldId;
	private String fieldName;
	private String dataType;
	private String dbAuthId;
	private String SerialNumber;
	private long length;
	private String displayName;
	private Integer appOrder;
	private Set dbdsParamPoolValueSet;
	private String sqlId;
	SQLParamPoolValueData sqlData = new SQLParamPoolValueData();
	List<SQLPoolValueData> lstSQLPoolValue = new ArrayList();
	
	public SQLParamPoolValueData getSqlData() {
		return sqlData;
	}
	public void setSqlData(SQLParamPoolValueData sqlData) {
		this.sqlData = sqlData;
	}
	
	public String getSqlId() {
		return sqlId;
	}
	public void setSqlId(String sqlId) {
		this.sqlId = sqlId;
	}
	
	public Integer getAppOrder() {
		return appOrder;
	}
	public void setAppOrder(Integer appOrder) {
		this.appOrder = appOrder;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	

	public String getDbAuthId() {
		return dbAuthId;
	}
	public void setDbAuthId(String dbAuthId) {
		this.dbAuthId = dbAuthId;
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
		this.fieldName = fieldName.trim();
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public long getLength() {
		return length;
	}
	public void setLength(long length) {
		this.length = length;
	}
	public String getSerialNumber() {
		return SerialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		SerialNumber = serialNumber;
	}
	public Set getDbdsParamPoolValueSet() {
		return dbdsParamPoolValueSet;
	}
	public void setDbdsParamPoolValueSet(Set dbdsParamPoolValueSet) {
		this.dbdsParamPoolValueSet = dbdsParamPoolValueSet;
	}
	public List<SQLPoolValueData> getLstSQLPoolValue() {
		return lstSQLPoolValue;
	}
	public void setLstSQLPoolValue(List<SQLPoolValueData> lstSQLPoolValue) {
		this.lstSQLPoolValue = lstSQLPoolValue;
	}
	
}
