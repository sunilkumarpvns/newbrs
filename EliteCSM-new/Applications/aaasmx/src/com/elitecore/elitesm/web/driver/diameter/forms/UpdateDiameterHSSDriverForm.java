package com.elitecore.elitesm.web.driver.diameter.forms;

import java.util.List;
import java.util.Set;

import com.elitecore.elitesm.datamanager.servermgr.drivers.data.LogicalNameValuePoolData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class UpdateDiameterHSSDriverForm extends BaseWebForm{
	
	// driver instance related properties
	private String driverInstanceName;
	private String driverInstanceDesc;
	
	
	/// db driver related properties 
	private long dbAuthId;
	private long databaseId;
	private String databaseName;
	//private long dbScanTime;
	private String tableName;
	private long dbQueryTimeout;
	private long maxQueryTimeoutCount;
	private long driverInstanceId;
	private List databaseDSList;	
	private String logicalName;
	private String dbFiled;
	private List<LogicalNameValuePoolData> logicalNameList;
	private String action;
	private int itemIndex;
	private Set dbfeildMapList;
	private String defaultValue;
	private String valueMapping;
	private String userIdentityAttributes;
	private Long auditUId;
	
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	public String getValueMapping() {
		return valueMapping;
	}
	public void setValueMapping(String valueMapping) {
		this.valueMapping = valueMapping;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public long getDbAuthId() {
		return dbAuthId;
	}
	public void setDbAuthId(long dbAuthId) {
		this.dbAuthId = dbAuthId;
	}
	public long getDatabaseId() {
		return databaseId;
	}
	public void setDatabaseId(long databaseId) {
		this.databaseId = databaseId;
	}
	/*public long getDbScanTime() {
		return dbScanTime;
	}
	public void setDbScanTime(long dbScanTime) {
		this.dbScanTime = dbScanTime;
	}*/
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public long getDbQueryTimeout() {
		return dbQueryTimeout;
	}
	public void setDbQueryTimeout(long dbQueryTimeout) {
		this.dbQueryTimeout = dbQueryTimeout;
	}
	public long getMaxQueryTimeoutCount() {
		return maxQueryTimeoutCount;
	}
	public void setMaxQueryTimeoutCount(long maxQueryTimeoutCount) {
		this.maxQueryTimeoutCount = maxQueryTimeoutCount;
	}
	public long getDriverInstanceId() {
		return driverInstanceId;
	}
	public void setDriverInstanceId(long driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
	}
	public List getDatabaseDSList() {
		return databaseDSList;
	}
	public void setDatabaseDSList(List databaseDSList) {
		this.databaseDSList = databaseDSList;
	}
	public String getLogicalName() {
		return logicalName;
	}
	public void setLogicalName(String logicalName) {
		this.logicalName = logicalName;
	}
	public String getDbFiled() {
		return dbFiled;
	}
	public void setDbFiled(String dbFiled) {
		this.dbFiled = dbFiled;
	}
	public List<LogicalNameValuePoolData> getLogicalNameList() {
		return logicalNameList;
	}
	public void setLogicalNameList(List<LogicalNameValuePoolData> logicalNameList) {
		this.logicalNameList = logicalNameList;
	}
	public int getItemIndex() {
		return itemIndex;
	}
	public void setItemIndex(int itemIndex) {
		this.itemIndex = itemIndex;
	}
	public String getDriverInstanceName() {
		return driverInstanceName;
	}
	public void setDriverInstanceName(String driverInstanceName) {
		this.driverInstanceName = driverInstanceName;
	}
	public String getDriverInstanceDesc() {
		return driverInstanceDesc;
	}
	public void setDriverInstanceDesc(String driverInstanceDesc) {
		this.driverInstanceDesc = driverInstanceDesc;
	}
	public Set getDbfeildMapList() {
		return dbfeildMapList;
	}
	public void setDbfeildMapList(Set dbfeildMapList) {
		this.dbfeildMapList = dbfeildMapList;
	}
	public String getDatabaseName() {
		return databaseName;
	}
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}
	public String getUserIdentityAttributes() {
		return userIdentityAttributes;
	}
	public void setUserIdentityAttributes(String userIdentityAttributes) {
		this.userIdentityAttributes = userIdentityAttributes;
	}
	public Long getAuditUId() {
		return auditUId;
	}
	public void setAuditUId(Long auditUId) {
		this.auditUId = auditUId;
	}
	
	

}
