package com.elitecore.netvertexsm.web.servermgr.spinterface.dbinterface.form;

import java.util.List;

import com.elitecore.netvertexsm.datamanager.datasource.database.data.DatabaseDSData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.LogicalNameValuePoolData;
import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;

public class DBSPInterfaceForm extends BaseWebForm {

	private static final long serialVersionUID = 1L;
	private String name;
	private String description;
	private Long databaseDSId;
	private Long driverInstanceId;
	private Long databaseSPInterfaceId;
	private Long dbQueryTimeout;
	private Long maxQueryTimeoutCnt;
	private String tableName;
	private String identityField;
	private List<DatabaseDSData> databaseDSList;
	private List<LogicalNameValuePoolData> logicalNamePoolList;
	
	//purpose of temporary storage of Logical Name and DB Field
	private String logicalName;
	private String dbField;
	
	public Long getDriverInstanceId() {
		return driverInstanceId;
	}
	public void setDriverInstanceId(Long driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Long getDatabaseDSId() {
		return databaseDSId;
	}
	public void setDatabaseDSId(Long databaseDSId) {
		this.databaseDSId = databaseDSId;
	}
	public Long getDbQueryTimeout() {
		return dbQueryTimeout;
	}
	public void setDbQueryTimeout(Long dbQueryTimeout) {
		this.dbQueryTimeout = dbQueryTimeout;
	}
	public Long getMaxQueryTimeoutCnt() {
		return maxQueryTimeoutCnt;
	}
	public void setMaxQueryTimeoutCnt(Long maxQueryTimeoutCnt) {
		this.maxQueryTimeoutCnt = maxQueryTimeoutCnt;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getIdentityField() {
		return identityField;
	}
	public void setIdentityField(String identityField) {
		this.identityField = identityField;
	}
	public List<DatabaseDSData> getDatabaseDSList() {
		return databaseDSList;
	}
	public void setDatabaseDSList(List<DatabaseDSData> databaseDSList) {
		this.databaseDSList = databaseDSList;
	}
	public List<LogicalNameValuePoolData> getLogicalNamePoolList() {
		return logicalNamePoolList;
	}
	public void setLogicalNamePoolList(List<LogicalNameValuePoolData> logicalNamePoolList) {
		this.logicalNamePoolList = logicalNamePoolList;
	}
	public String getLogicalName() {
		return logicalName;
	}
	public void setLogicalName(String logicalName) {
		this.logicalName = logicalName;
	}
	public String getDbField() {
		return dbField;
	}
	public void setDbField(String dbField) {
		this.dbField = dbField;
	}
	public Long getDatabaseSPInterfaceId() {
		return databaseSPInterfaceId;
	}
	public void setDatabaseSPInterfaceId(Long databaseSPRId) {
		this.databaseSPInterfaceId = databaseSPRId;
	}
		
}
