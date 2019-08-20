package com.elitecore.netvertexsm.ws;


public class BaseDBConfiguration {
	private String connectionUrl;
	private String driverClass;
	private String userName;
	private String password;
	private int recordFetchLimit = 100;
	private String tableName;

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


	
}
