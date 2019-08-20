package com.elitecore.elitesm.web.datasource.database.forms;

import java.sql.Timestamp;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class ViewDatabaseDSForm extends BaseWebForm {
	
	private String databaseId;
	private String name;
    private String connectionUrl;
    private String userName;
    private String password;
    private long minimumPool=1;
    private long maximumPool=1;
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
	
	public String getDatabaseId() {
		return databaseId;
	}
	public void setDatabaseId(String databaseId) {
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

}
