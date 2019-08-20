package com.elitecore.netvertexsm.web.servermgr.spinterface.dbinterface.form;

import java.util.List;

import org.apache.struts.action.ActionForm;

import com.elitecore.netvertexsm.datamanager.datasource.database.data.DatabaseDSData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData;

public class SearchDBSPInterfaceForm extends ActionForm {
	private Long databaseDsId;
	private String tableName;
	private Long dbQueryTimeout;
	private Long maxQueryTimeoutCnt;
	private Long driverInstanceId;
	
	private List<DatabaseDSData> databaseDSList;
	private List<DriverInstanceData> driverInstanceList;
	
	private long pageNumber;		
	private long totalPages;
	private long totalRecords;
	private String action;
	
	private List listSearchDBDriver;
	
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
	public List<DatabaseDSData> getDatabaseDSList() {
		return databaseDSList;
	}
	public void setDatabaseDSList(List<DatabaseDSData> databaseDSList) {
		this.databaseDSList = databaseDSList;
	}
	public List<DriverInstanceData> getDriverInstanceList() {
		return driverInstanceList;
	}
	public void setDriverInstanceList(List<DriverInstanceData> driverInstanceList) {
		this.driverInstanceList = driverInstanceList;
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
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public List getListSearchDBDriver() {
		return listSearchDBDriver;
	}
	public void setListSearchDBDriver(List listSearchDBDriver) {
		this.listSearchDBDriver = listSearchDBDriver;
	}

}
