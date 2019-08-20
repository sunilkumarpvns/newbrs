package com.elitecore.netvertexsm.web.servermgr.spinterface.form;

import java.util.ArrayList;
import java.util.List;

import org.apache.struts.action.ActionForm;

import com.elitecore.netvertexsm.datamanager.datasource.database.data.DatabaseDSData;
import com.elitecore.netvertexsm.datamanager.datasource.ldap.data.LDAPDatasourceData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData;

public class SearchSPInterfaceForm extends ActionForm {
	private String name;
	private Long driverTypeId;
	private List listSearchDriver = new ArrayList();
	
// DB Driver Properties....	
	private Long databaseDsId;
	private String tableName;
	private Long dbQueryTimeout;
	private Long maxQueryTimeoutCnt;
	
	private List<DatabaseDSData> databaseDSList;
	private List listSearchDBDriver;

//	LDAP Driver Properties....
	private String expiryDatePattern;
	private Integer passwordEncryptType;
	private Long queryMaxExecTime;
	private Long ldapDsId;
	
	private List<LDAPDatasourceData> ldapDsList;
	private List listSearchLDAPDriver;
	
//	General Properties....
	private List<DriverInstanceData> driverInstanceList;
	private Long driverInstanceId;
	private long pageNumber;		
	private long totalPages;
	private long totalRecords;	
	private String action;
	private List driverTypeList;
	private List serviceTypeList;
	
	
	public List getListSearchDriver() {
		return listSearchDriver;
	}
	public void setListSearchDriver(List listSearchDriver) {
		this.listSearchDriver = listSearchDriver;
	}
	public List getServiceTypeList() {
		return serviceTypeList;
	}
	public void setServiceTypeList(List serviceTypeList) {
		this.serviceTypeList = serviceTypeList;
	}
	public List getDriverTypeList() {
		return driverTypeList;
	}
	public void setDriverTypeList(List driverTypeList) {
		this.driverTypeList = driverTypeList;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getDriverTypeId() {
		return driverTypeId;
	}
	public void setDriverTypeId(Long driverTypeId) {
		this.driverTypeId = driverTypeId;
	}
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
	public List<DatabaseDSData> getDatabaseDSList() {
		return databaseDSList;
	}
	public void setDatabaseDSList(List<DatabaseDSData> databaseDSList) {
		this.databaseDSList = databaseDSList;
	}
	public List getListSearchDBDriver() {
		return listSearchDBDriver;
	}
	public void setListSearchDBDriver(List listSearchDBDriver) {
		this.listSearchDBDriver = listSearchDBDriver;
	}
	public String getExpiryDatePattern() {
		return expiryDatePattern;
	}
	public void setExpiryDatePattern(String expiryDatePattern) {
		this.expiryDatePattern = expiryDatePattern;
	}
	public Integer getPasswordEncryptType() {
		return passwordEncryptType;
	}
	public void setPasswordEncryptType(Integer passwordEncryptType) {
		this.passwordEncryptType = passwordEncryptType;
	}
	public Long getQueryMaxExecTime() {
		return queryMaxExecTime;
	}
	public void setQueryMaxExecTime(Long queryMaxExecTime) {
		this.queryMaxExecTime = queryMaxExecTime;
	}
	public Long getLdapDsId() {
		return ldapDsId;
	}
	public void setLdapDsId(Long ldapDsId) {
		this.ldapDsId = ldapDsId;
	}
	public List<LDAPDatasourceData> getLdapDsList() {
		return ldapDsList;
	}
	public void setLdapDsList(List<LDAPDatasourceData> ldapDsList) {
		this.ldapDsList = ldapDsList;
	}
	public List getListSearchLDAPDriver() {
		return listSearchLDAPDriver;
	}
	public void setListSearchLDAPDriver(List listSearchLDAPDriver) {
		this.listSearchLDAPDriver = listSearchLDAPDriver;
	}
	public List<DriverInstanceData> getDriverInstanceList() {
		return driverInstanceList;
	}
	public void setDriverInstanceList(List<DriverInstanceData> driverInstanceList) {
		this.driverInstanceList = driverInstanceList;
	}
	public Long getDriverInstanceId() {
		return driverInstanceId;
	}
	public void setDriverInstanceId(Long driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
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
}
