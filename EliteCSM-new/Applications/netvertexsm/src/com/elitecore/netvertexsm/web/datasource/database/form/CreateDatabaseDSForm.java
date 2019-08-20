package com.elitecore.netvertexsm.web.datasource.database.form;

import java.sql.Timestamp;

import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;

public class CreateDatabaseDSForm extends BaseWebForm {
	
	private long databaseId;
	private String name;
    private String connectionUrl;
    private String userName;
    private String password;
    private Long statusCheckDuration=120L;
	private Long timeout=1000L;
    private long minimumPool=2;
    private long maximumPool=5;
    private long lastmodifiedByStaffId;
    private long createdByStaffId;
    private Timestamp lastmodifiedDate;
    private Timestamp createDate;
    
	
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
	
	public long getLastmodifiedByStaffId() {
		return lastmodifiedByStaffId;
	}
	public void setLastmodifiedByStaffId(long lastmodifiedByStaffId) {
		this.lastmodifiedByStaffId = lastmodifiedByStaffId;
	}
	public long getCreatedByStaffId() {
		return createdByStaffId;
	}
	public void setCreatedByStaffId(long createdByStaffId) {
		this.createdByStaffId = createdByStaffId;
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
	public Timestamp getLastmodifiedDate() {
		return lastmodifiedDate;
	}
	public void setLastmodifiedDate(Timestamp lastmodifiedDate) {
		this.lastmodifiedDate = lastmodifiedDate;
	}
	public Timestamp getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
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