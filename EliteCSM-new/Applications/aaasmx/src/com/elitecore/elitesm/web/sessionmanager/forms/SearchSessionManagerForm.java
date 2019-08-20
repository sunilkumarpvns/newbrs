package com.elitecore.elitesm.web.sessionmanager.forms;

import java.sql.Timestamp;
import java.util.List;

import com.elitecore.elitesm.datamanager.datasource.database.data.IDatabaseDSData;
import com.elitecore.elitesm.datamanager.sessionmanager.data.ISessionManagerInstanceData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class SearchSessionManagerForm extends BaseWebForm {
	
	
	
	private String name;
	private String description;
	private long createdbystaffid;
	private long lastmodifiedbystaffid;
	private Timestamp lastmodifieddate;
	private Timestamp createdate;
	private String action;
    private List<ISessionManagerInstanceData> listSessionManager;
    private List<IDatabaseDSData> lstDatasource = null;
	private long databaseId;
	
	
	private long pageNumber;
    private long totalPages;
    private long totalRecords;
     
     
    public List<ISessionManagerInstanceData> getListSessionManager() {
 		return listSessionManager;
 	}
    
 	public void setListSessionManager(
 			List<ISessionManagerInstanceData> listSessionManager) {
 		this.listSessionManager = listSessionManager;
 	}
     
     
     
    
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
	
	
	
	public List<IDatabaseDSData> getLstDatasource() {
		return lstDatasource;
	}
	
	public void setLstDatasource(List<IDatabaseDSData> lstDatasource) {
		this.lstDatasource = lstDatasource;
	}
	
	public long getDatabaseId() {
		return databaseId;
	}
	
	public void setDatabaseId(long databaseId) {
		this.databaseId = databaseId;
	}
	
	public String getAction() {
		return action;
	}
	
	public void setAction(String action) {
		this.action = action;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public long getCreatedbystaffid() {
		return createdbystaffid;
	}
	
	public void setCreatedbystaffid(long createdbystaffid) {
		this.createdbystaffid = createdbystaffid;
	}
	
	public long getLastmodifiedbystaffid() {
		return lastmodifiedbystaffid;
	}
	
	public void setLastmodifiedbystaffid(long lastmodifiedbystaffid) {
		this.lastmodifiedbystaffid = lastmodifiedbystaffid;
	}
	
	public Timestamp getLastmodifieddate() {
		return lastmodifieddate;
	}
	
	public void setLastmodifieddate(Timestamp lastmodifieddate) {
		this.lastmodifieddate = lastmodifieddate;
	}
	
	public Timestamp getCreatedate() {
		return createdate;
	}
	
	public void setCreatedate(Timestamp createdate) {
		this.createdate = createdate;
	}
	
	
	
	

}
