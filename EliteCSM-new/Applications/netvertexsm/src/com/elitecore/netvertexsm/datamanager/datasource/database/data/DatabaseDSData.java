package com.elitecore.netvertexsm.datamanager.datasource.database.data;

import com.elitecore.netvertexsm.web.core.base.BaseData;


public class DatabaseDSData extends BaseData implements IDatabaseDSData  {
	
	private long databaseId;
	private String name;
    private String connectionUrl;
    private String userName;
    private String password;
    private long minimumPool;
    private long maximumPool;
    private Long timeout;
    private Long statusCheckDuration;
    
    
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
	
	public long getMinimumPool() {
		return minimumPool;
	}
	public void setMinimumPool(long minimumPool) {
		this.minimumPool = minimumPool;
	}
	public long getMaximumPool() {
		return maximumPool;
	}
	public void setMaximumPool(long maximumPool) {
		this.maximumPool = maximumPool;
	}
	public long getDatabaseId() {
		return databaseId;
	}
	public void setDatabaseId(long databaseId) {
		this.databaseId = databaseId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getTimeout() {
		return timeout;
	}
	public void setTimeout(Long timeout) {
		this.timeout = timeout;
	}
	public Long getStatusCheckDuration() {
		return statusCheckDuration;
	}
	public void setStatusCheckDuration(Long statusCheckDuration) {
		this.statusCheckDuration = statusCheckDuration;
	}
}