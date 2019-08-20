package com.elitecore.netvertexsm.web.servermgr.spinterface.form;

import java.util.List;

import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverTypeData;
import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;

public class CreateSPInterfaceForm extends BaseWebForm {
	private static final long serialVersionUID = 1L;
	private String name;
	private String description;
	private String status = "CST01";
	private Long driverTypeId;
	private List<DriverTypeData> driverTypeList;
	
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Long getDriverTypeId() {
		return driverTypeId;
	}
	public void setDriverTypeId(Long driverTypeId) {
		this.driverTypeId = driverTypeId;
	}
	public List<DriverTypeData> getDriverTypeList() {
		return driverTypeList;
	}
	public void setDriverTypeList(List<DriverTypeData> driverTypeList) {
		this.driverTypeList = driverTypeList;
	}
	
	
//	
//	public String getIdentityField() {
//		return identityField;
//	}
//	public void setIdentityField(String identityField) {
//		this.identityField = identityField;
//	}
//	public String getLdapAttribute() {
//		return ldapAttribute;
//	}
//	public void setLdapAttribute(String ldapAttribute) {
//		this.ldapAttribute = ldapAttribute;
//	}
//	public List getLogicalNamePoolList() {
//		return logicalNamePoolList;
//	}
//	public void setLogicalNamePoolList(List logicalNamePoolList) {
//		this.logicalNamePoolList = logicalNamePoolList;
//	}
//	public String getLogicalName() {
//		return logicalName;
//	}
//	public void setLogicalName(String logicalName) {
//		this.logicalName = logicalName;
//	}
//	public String getDbField() {
//		return dbField;
//	}
//	public void setDbField(String dbField) {
//		this.dbField = dbField;
//	}
//	public List getLdapFieldMapList() {
//		return ldapFieldMapList;
//	}
//	public void setLdapFieldMapList(List ldapFieldMapList) {
//		this.ldapFieldMapList = ldapFieldMapList;
//	}
//	public List getDbFieldMapList() {
//		return dbFieldMapList;
//	}
//	public void setDbFieldMapList(List dbFieldMapList) {
//		this.dbFieldMapList = dbFieldMapList;
//	}
//	public String getExpiryDatePattern() {
//		return expiryDatePattern;
//	}
//	public void setExpiryDatePattern(String expiryDatePattern) {
//		this.expiryDatePattern = expiryDatePattern;
//	}
//	public Integer getPassDecryptType() {
//		return passDecryptType;
//	}
//	public void setPassDecryptType(Integer passDecryptType) {
//		this.passDecryptType = passDecryptType;
//	}
//	public Long getQueryMaxExecTime() {
//		return queryMaxExecTime;
//	}
//	public void setQueryMaxExecTime(Long queryMaxExecTime) {
//		this.queryMaxExecTime = queryMaxExecTime;
//	}
//	public Long getDriverInstanceId() {
//		return driverInstanceId;
//	}
//	public void setDriverInstanceId(Long driverInstanceId) {
//		this.driverInstanceId = driverInstanceId;
//	}
//	public Long getLdapDsId() {
//		return ldapDsId;
//	}
//	public void setLdapDsId(Long ldapDsId) {
//		this.ldapDsId = ldapDsId;
//	}
//	public List getDriverInstanceList() {
//		return driverInstanceList;
//	}
//	public void setDriverInstanceList(List driverInstanceList) {
//		this.driverInstanceList = driverInstanceList;
//	}
//	public List getLdapDsList() {
//		return ldapDsList;
//	}
//	public void setLdapDsList(List ldapDsList) {
//		this.ldapDsList = ldapDsList;
//	}
//	public Long getDatabaseDsId() {
//		return databaseDsId;
//	}
//	public void setDatabaseDsId(Long databaseDsId) {
//		this.databaseDsId = databaseDsId;
//	}
//	public String getTableName() {
//		return tableName;
//	}
//	public void setTableName(String tableName) {
//		this.tableName = tableName;
//	}
//	public Long getDbQueryTimeout() {
//		return dbQueryTimeout;
//	}
//	public void setDbQueryTimeout(Long dbQueryTimeout) {
//		this.dbQueryTimeout = dbQueryTimeout;
//	}
//	public Long getMaxQueryTimeoutCnt() {
//		return maxQueryTimeoutCnt;
//	}
//	public void setMaxQueryTimeoutCnt(Long maxQueryTimeoutCnt) {
//		this.maxQueryTimeoutCnt = maxQueryTimeoutCnt;
//	}
//	public List getDatabaseDsList() {
//		return databaseDsList;
//	}
//	public void setDatabaseDsList(List databaseDsList) {
//		this.databaseDsList = databaseDsList;
//	}
//	public List getServiceTypeList() {
//		return serviceTypeList;
//	}
//	public void setServiceTypeList(List serviceTypeList) {
//		this.serviceTypeList = serviceTypeList;
//	}
//	public List getDriverTypeList() {
//		return driverTypeList;
//	}
//	public void setDriverTypeList(List driverTypeList) {
//		this.driverTypeList = driverTypeList;
//	}
//	public String getName() {
//		return name;
//	}
//	public void setName(String name) {
//		this.name = name;
//	}
//	public String getDescription() {
//		return description;
//	}
//	public void setDescription(String description) {
//		this.description = description;
//	}
//	public String getStatus() {
//		return status;
//	}
//	public void setStatus(String status) {
//		this.status = status;
//	}
//	public Long getDriverTypeId() {
//		return driverTypeId;
//	}
//	public void setDriverTypeId(Long driverTypeId) {
//		this.driverTypeId = driverTypeId;
//	}
}
