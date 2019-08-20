package com.elitecore.elitesm.ws;

import java.util.HashMap;
import java.util.Map;

import com.elitecore.core.commons.util.db.DBConnectionManager;

public abstract class BaseDBConfiguration {
	private String connectionUrl;
	private String driverClass;
	private String userName;
	private String password;
	private int recordFetchLimit = 100;
	private String tableName;	
	private long maxIdle = 5;
    private long maximumPool = 50;
   

	
	final private Map<String,String> dbFieldMap = new HashMap<String,String>();
	
	protected BaseDBConfiguration(String driverClass,String connectionUrl,String userName, String password, String tableName){
		this.driverClass=driverClass;
		this.connectionUrl=connectionUrl;
		this.userName = userName;
		this.password = password;
		this.tableName =tableName;
	}
		

	public String getDriverClass() {
		return driverClass;
	}
	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}

	public String getConnectionUrl() {
		return connectionUrl;
	}
	public void setConnectionUrl(String connectionUrl) {
		this.connectionUrl = connectionUrl;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public int getRecordFetchLimit() {
		return recordFetchLimit;
	}

	public void setRecordFetchLimit(int recordFetchLimit) {
		this.recordFetchLimit = recordFetchLimit;
	}


	public Map<String, String> getDbFieldMap() {
		return dbFieldMap;
	}


	public long getMaxIdle() {
		return maxIdle;
	}


	public void setMaxIdle(long maxIdle) {
		this.maxIdle = maxIdle;
	}


	public long getMaximumPool() {
		return maximumPool;
	}


	public void setMaximumPool(long maximumPool) {
		this.maximumPool = maximumPool;
	}

	public abstract String getCache() ;


	public abstract DBConnectionManager getDbConnectionManager();


	public abstract void setDbConnectionManager(DBConnectionManager dbConnectionManager);
	
	
}
