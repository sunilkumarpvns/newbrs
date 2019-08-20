package com.elitecore.elitesm.web.datasource.database.forms;

import java.sql.Timestamp;
import java.util.List;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class SearchDatabaseDSForm extends BaseWebForm {
	
	private long databaseId;
	private String name;
    private String connectionUrl;
    private String userName;
    private String password;
    private long minimumPool;
    private long maximumPool;
    private long lastmodifiedByStaffId;
    private long createdByStaffId;
    private Timestamp lastmodifiedDate;
    private Timestamp createDate;
    private String action;
    private List lstDatabaseDS;
    private String[] select;
    private String status="All";
    
    private long pageNumber;
    private long totalPages;
	private long totalRecords;
    
    
    
	
	public long getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(long pageNumber) {
		this.pageNumber = pageNumber;
	}
	public long getTotalPages() {
		return totalPages;
	}
	public void setTotalPages(long totalPages) {
		this.totalPages = totalPages;
	}
	public long getTotalRecords() {
		return totalRecords;
	}
	public void setTotalRecords(long totalRecords) {
		this.totalRecords = totalRecords;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	
	public List getLstDatabaseDS() {
		return lstDatabaseDS;
	}
	public void setLstDatabaseDS(List lstDatabaseDS) {
		this.lstDatabaseDS = lstDatabaseDS;
	}
	public String[] getSelect() {
		return select;
	}
	public void setSelect(String[] select) {
		this.select = select;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
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

}
