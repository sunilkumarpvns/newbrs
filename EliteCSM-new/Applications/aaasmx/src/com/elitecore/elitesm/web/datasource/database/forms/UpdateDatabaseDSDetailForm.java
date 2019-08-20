package com.elitecore.elitesm.web.datasource.database.forms;

import java.sql.Timestamp;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class UpdateDatabaseDSDetailForm extends BaseWebForm {
	
	private String databaseId;
	private String name;
    private String connectionUrl;
    private String userName;
    private String password;
    private Long statusCheckDuration=120L;
	private Long timeout=1000L;
    private long minimumPool;
    private long maximumPool;
    private String lastmodifiedByStaffId;
    private String createdByStaffId;
    private Timestamp lastmodifiedDate;
    private Timestamp createDate;
    private String action;
    private String auditUId;
    
	
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
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
	
	public String getLastmodifiedByStaffId() {
		return lastmodifiedByStaffId;
	}
	public void setLastmodifiedByStaffId(String lastmodifiedByStaffId) {
		this.lastmodifiedByStaffId = lastmodifiedByStaffId;
	}
	public String getCreatedByStaffId() {
		return createdByStaffId;
	}
	public void setCreatedByStaffId(String createdByStaffId) {
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
	public String getAuditUId() {
		return auditUId;
	}
	public void setAuditUId(String auditUId) {
		this.auditUId = auditUId;
	}
	
	

}
