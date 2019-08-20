package com.elitecore.netvertexsm.datamanager.wsconfig.data;

import com.elitecore.core.commons.util.db.datasource.DBDataSource;

public class ProvisioningAPIConfiguration {

	private Integer quotaMgrDatabaseDSID;
	private String batchUpdate;
	private Integer batchSize;
	private Integer batchUpdateInterval;
	private Integer dbQueryTimeout;
	private DBDataSource dbDataSource;

	public Integer getQuotaMgrDatabaseDSID() {
		return quotaMgrDatabaseDSID;
	}
	public void setQuotaMgrDatabaseDSID(Integer quotaMgrDatabaseDSID) {
		this.quotaMgrDatabaseDSID = quotaMgrDatabaseDSID;
	}
	public String getBatchUpdate() {
		return batchUpdate;
	}
	public void setBatchUpdate(String batchUpdate) {
		this.batchUpdate = batchUpdate;
	}
	public Integer getBatchSize() {
		return batchSize;
	}
	public void setBatchSize(Integer batchSize) {
		this.batchSize = batchSize;
	}
	public Integer getBatchUpdateInterval() {
		return batchUpdateInterval;
	}
	public void setBatchUpdateInterval(Integer batchUpdateInterval) {
		this.batchUpdateInterval = batchUpdateInterval;
	}
	public Integer getDbQueryTimeout() {
		return dbQueryTimeout;
	}
	public void setDbQueryTimeout(Integer dbQueryTimeout) {
		this.dbQueryTimeout = dbQueryTimeout;
	}
	public DBDataSource getDbDataSource() {
		return dbDataSource;
	}
	public void setDbDataSource(DBDataSource dbDataSource) {
		this.dbDataSource = dbDataSource;
	}
	public boolean batchUpdateEnabled(){
		return Boolean.valueOf(batchUpdate);
	}
}
