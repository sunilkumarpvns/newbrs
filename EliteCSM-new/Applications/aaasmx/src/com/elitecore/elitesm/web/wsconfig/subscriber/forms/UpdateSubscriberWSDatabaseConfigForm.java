package com.elitecore.elitesm.web.wsconfig.subscriber.forms;

import java.util.List;

import com.elitecore.elitesm.datamanager.datasource.database.data.IDatabaseDSData;
import com.elitecore.elitesm.datamanager.wsconfig.data.WSKeyMappingData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class UpdateSubscriberWSDatabaseConfigForm extends BaseWebForm {

	private static final long serialVersionUID = 1L;
	private String action;
	private String wsConfigId;
	private String databaseId;
	private transient List<IDatabaseDSData> lstDatasource;
	private String tableName;
	private String userIdentityFieldName;
	private Integer recordFetchLimit;
	private String primaryKeyColumn;
	private String sequenceName;
	private transient List<WSKeyMappingData> wsKeyMappingList;
	
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getWsConfigId() {
		return wsConfigId;
	}
	public void setWsConfigId(String wsConfigId) {
		this.wsConfigId = wsConfigId;
	}
	public String getDatabaseId() {
		return databaseId;
	}
	public void setDatabaseId(String databaseId) {
		this.databaseId = databaseId;
	}
	public List<IDatabaseDSData> getLstDatasource() {
		return lstDatasource;
	}
	public void setLstDatasource(List<IDatabaseDSData> lstDatasource) {
		this.lstDatasource = lstDatasource;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getUserIdentityFieldName() {
		return userIdentityFieldName;
	}
	public void setUserIdentityFieldName(String userIdentityFieldName) {
		this.userIdentityFieldName = userIdentityFieldName;
	}
	public Integer getRecordFetchLimit() {
		return recordFetchLimit;
	}
	public void setRecordFetchLimit(Integer recordFetchLimit) {
		this.recordFetchLimit = recordFetchLimit;
	}
	public String getPrimaryKeyColumn() {
		return primaryKeyColumn;
	}
	public void setPrimaryKeyColumn(String primaryKeyColumn) {
		this.primaryKeyColumn = primaryKeyColumn;
	}
	public String getSequenceName() {
		return sequenceName;
	}
	public void setSequenceName(String sequenceName) {
		this.sequenceName = sequenceName;
	}
	public List<WSKeyMappingData> getWsKeyMappingList() {
		return wsKeyMappingList;
	}
	public void setWsKeyMappingList(
			List<WSKeyMappingData> wsAddFieldMapDataList) {
		this.wsKeyMappingList = wsAddFieldMapDataList;
	}
	
	
}
