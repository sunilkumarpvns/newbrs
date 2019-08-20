package com.elitecore.netvertexsm.web.servermgr.spinterface.ldapinterface.form;

import java.util.List;

import com.elitecore.netvertexsm.datamanager.datasource.ldap.data.LDAPDatasourceData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.LogicalNameValuePoolData;
import com.elitecore.netvertexsm.datamanager.servermgr.spinterface.ldapinterface.data.LDAPFieldMapData;
import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;

public class LDAPSPInterfaceForm extends BaseWebForm {

	private static final long serialVersionUID = 1L;
	private String name;
	private String description;
	private String expiryDatePattern;
	private Integer passwordDecryptType;
	private Long driverInstanceId;
	private Long ldapDSId;
	private Long ldapSPInterfaceId;
	private Long queryMaxExecTime;
	private List<LDAPDatasourceData> ldapDatasourceList;
	private List<LDAPFieldMapData> ldapFieldList;
	private List<LogicalNameValuePoolData> logicalNamePoolList;
	
	//Temparoray variables
	private String logicalName;
	private String ldapAttribute;
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
	public String getExpiryDatePattern() {
		return expiryDatePattern;
	}
	public void setExpiryDatePattern(String expiryDatePattern) {
		this.expiryDatePattern = expiryDatePattern;
	}
	public Integer getPasswordDecryptType() {
		return passwordDecryptType;
	}
	public void setPasswordDecryptType(Integer passwordDecryptType) {
		this.passwordDecryptType = passwordDecryptType;
	}
	
	public Long getLdapDSId() {
		return ldapDSId;
	}
	public void setLdapDSId(Long ldapDSId) {
		this.ldapDSId = ldapDSId;
	}
	public Long getQueryMaxExecTime() {
		return queryMaxExecTime;
	}
	public void setQueryMaxExecTime(Long queryMaxExecTime) {
		this.queryMaxExecTime = queryMaxExecTime;
	}
	public List<LDAPDatasourceData> getLdapDatasourceList() {
		return ldapDatasourceList;
	}
	public void setLdapDatasourceList(List<LDAPDatasourceData> ldapDatasourceList) {
		this.ldapDatasourceList = ldapDatasourceList;
	}
	public List<LDAPFieldMapData> getLdapFieldList() {
		return ldapFieldList;
	}
	public void setLdapFieldList(List<LDAPFieldMapData> ldapFieldList) {
		this.ldapFieldList = ldapFieldList;
	}
	public List<LogicalNameValuePoolData> getLogicalNamePoolList() {
		return logicalNamePoolList;
	}
	public void setLogicalNamePoolList(
			List<LogicalNameValuePoolData> logicalNamePoolList) {
		this.logicalNamePoolList = logicalNamePoolList;
	}
	public Long getLdapSPInterfaceId() {
		return ldapSPInterfaceId;
	}
	public void setLdapSPInterfaceId(Long ldapSPRId) {
		this.ldapSPInterfaceId = ldapSPRId;
	}
	public Long getDriverInstanceId() {
		return driverInstanceId;
	}
	public void setDriverInstanceId(Long driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
	}
	public String getLogicalName() {
		return logicalName;
	}
	public void setLogicalName(String logicalName) {
		this.logicalName = logicalName;
	}
	public String getLdapAttribute() {
		return ldapAttribute;
	}
	public void setLdapAttribute(String ldapAttribute) {
		this.ldapAttribute = ldapAttribute;
	}
	
	
}
