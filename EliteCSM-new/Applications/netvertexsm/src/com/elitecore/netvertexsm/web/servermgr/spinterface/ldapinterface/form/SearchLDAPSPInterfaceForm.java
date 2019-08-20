package com.elitecore.netvertexsm.web.servermgr.spinterface.ldapinterface.form;

import java.util.List;

import org.apache.struts.action.ActionForm;

import com.elitecore.netvertexsm.datamanager.datasource.ldap.data.LDAPDatasourceData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData;

public class SearchLDAPSPInterfaceForm extends ActionForm {
	private String expiryDatePattern;
	private Integer passwordEncryptType;
	private Long queryMaxExecTime;
	private Long driverInstanceId;
	private Long ldapDsId;
	
	private List<LDAPDatasourceData> ldapDsList;
	private List<DriverInstanceData> driverInstanceList;
	
	private long pageNumber;		
	private long totalPages;
	private long totalRecords;
	private String action;
	
	private List listSearchLDAPDriver;
	
	public List getListSearchLDAPDriver() {
		return listSearchLDAPDriver;
	}
	public void setListSearchLDAPDriver(List listSearchLDAPDriver) {
		this.listSearchLDAPDriver = listSearchLDAPDriver;
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
	public List<LDAPDatasourceData> getLdapDsList() {
		return ldapDsList;
	}
	public void setLdapDsList(List<LDAPDatasourceData> ldapDsList) {
		this.ldapDsList = ldapDsList;
	}
	public List<DriverInstanceData> getDriverInstanceList() {
		return driverInstanceList;
	}
	public void setDriverInstanceList(List<DriverInstanceData> driverInstanceList) {
		this.driverInstanceList = driverInstanceList;
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
	public Long getDriverInstanceId() {
		return driverInstanceId;
	}
	public void setDriverInstanceId(Long driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
	}
	public Long getLdapDsId() {
		return ldapDsId;
	}
	public void setLdapDsId(Long ldapDsId) {
		this.ldapDsId = ldapDsId;
	}
}
