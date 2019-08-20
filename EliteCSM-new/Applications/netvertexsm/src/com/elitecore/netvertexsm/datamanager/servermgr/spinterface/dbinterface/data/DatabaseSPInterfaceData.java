package com.elitecore.netvertexsm.datamanager.servermgr.spinterface.dbinterface.data;

import java.io.Serializable;
import java.util.Set;

import com.elitecore.netvertexsm.datamanager.datasource.database.data.DatabaseDSData;

public class DatabaseSPInterfaceData  implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private Long databaseSpInterfaceId;
	private Long databaseDsId;
	private String tableName;
	private String identityField;
	private Long dbQueryTimeout;
	private Long maxQueryTimeoutCnt;
	private Long driverInstanceId;
	private DatabaseDSData databaseDs;
	private Set<DBFieldMapData> dbFieldMapSet;
	
	public DatabaseDSData getDatabaseDs() {
		return databaseDs;
	}
	public void setDatabaseDs(DatabaseDSData databaseDs) {
		this.databaseDs = databaseDs;
	}
	public String getIdentityField() {
		return identityField;
	}
	public void setIdentityField(String identityField) {
		this.identityField = identityField;
	}
	public Set<DBFieldMapData> getDbFieldMapSet() {
		return dbFieldMapSet;
	}
	public void setDbFieldMapSet(Set<DBFieldMapData> dbFieldMapSet) {
		this.dbFieldMapSet = dbFieldMapSet;
	}
	public Long getDatabaseSpInterfaceId() {
		return databaseSpInterfaceId;
	}
	public void setDatabaseSpInterfaceId(Long databaseSpInterfaceId) {
		this.databaseSpInterfaceId = databaseSpInterfaceId;
	}
	public Long getDatabaseDsId() {
		return databaseDsId;
	}
	public void setDatabaseDsId(Long databaseDsId) {
		this.databaseDsId = databaseDsId;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
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
	public Long getDriverInstanceId() {
		return driverInstanceId;
	}
	public void setDriverInstanceId(Long driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
	}
}
