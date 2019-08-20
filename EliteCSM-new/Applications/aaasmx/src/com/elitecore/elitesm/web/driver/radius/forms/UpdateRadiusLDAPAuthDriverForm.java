package com.elitecore.elitesm.web.driver.radius.forms;

import java.util.List;
import java.util.Map;

import com.elitecore.elitesm.datamanager.datasource.ldap.data.LDAPDatasourceData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.LogicalNameValuePoolData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class UpdateRadiusLDAPAuthDriverForm extends BaseWebForm{
	
	/// driver related properties .....
	private String driverInstanceName;
	private String driverDesp;
	
	
	private String ldapDriverId;
	//private long dsStatusCheckInterval;
	private String expiryDatePattern;	
	private long passwordDecryptType;
	private long queryMaxExecTime;
	private String driverInstanceId;	
	private String ldapDsId;
	private String ldapAttribute;
	private List<LDAPDatasourceData> ldapDSList;
	private List<LogicalNameValuePoolData>logicalNameList;
	private String logicalName;
	private String action;
	private int itemIndex;
	private String ldapName;
	private Long maxQueryTimeoutCnt;
	private String defaultValue;
	private String valueMapping;
	private String userIdentityAttributes; 
	private String searchScope;
	private String searchFilter;
	private Map<String, String> subTreeOptions;
	private String auditUId;
	
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	public String getValueMapping() {
		return valueMapping;
	}
	public void setValueMapping(String valueMapping) {
		this.valueMapping = valueMapping;
	}
	public Long getMaxQueryTimeoutCnt() {
		return maxQueryTimeoutCnt;
	}
	public void setMaxQueryTimeoutCnt(Long maxQueryTimeoutCnt) {
		this.maxQueryTimeoutCnt = maxQueryTimeoutCnt;
	}
	public String getLdapDriverId() {
		return ldapDriverId;
	}
	public void setLdapDriverId(String ldapDriverId) {
		this.ldapDriverId = ldapDriverId;
	}
	/*public long getDsStatusCheckInterval() {
		return dsStatusCheckInterval;
	}
	public void setDsStatusCheckInterval(long dsStatusCheckInterval) {
		this.dsStatusCheckInterval = dsStatusCheckInterval;
	}*/
	public String getExpiryDatePattern() {
		return expiryDatePattern;
	}
	public void setExpiryDatePattern(String expiryDatePattern) {
		this.expiryDatePattern = expiryDatePattern;
	}
	public long getPasswordDecryptType() {
		return passwordDecryptType;
	}
	public void setPasswordDecryptType(long passwordDecryptType) {
		this.passwordDecryptType = passwordDecryptType;
	}
	public long getQueryMaxExecTime() {
		return queryMaxExecTime;
	}
	public void setQueryMaxExecTime(long queryMaxExecTime) {
		this.queryMaxExecTime = queryMaxExecTime;
	}
	public String getDriverInstanceId() {
		return driverInstanceId;
	}
	public void setDriverInstanceId(String driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
	}
	public String getLdapDsId() {
		return ldapDsId;
	}
	public void setLdapDsId(String ldapDsId) {
		this.ldapDsId = ldapDsId;
	}
	public String getLdapAttribute() {
		return ldapAttribute;
	}
	public void setLdapAttribute(String ldapAttribute) {
		this.ldapAttribute = ldapAttribute;
	}
	public List<LDAPDatasourceData> getLdapDSList() {
		return ldapDSList;
	}
	public void setLdapDSList(List<LDAPDatasourceData> ldapDSList) {
		this.ldapDSList = ldapDSList;
	}
	public List<LogicalNameValuePoolData> getLogicalNameList() {
		return logicalNameList;
	}
	public void setLogicalNameList(List<LogicalNameValuePoolData> logicalNameList) {
		this.logicalNameList = logicalNameList;
	}
	public String getLogicalName() {
		return logicalName;
	}
	public void setLogicalName(String logicalName) {
		this.logicalName = logicalName;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public int getItemIndex() {
		return itemIndex;
	}
	public void setItemIndex(int itemIndex) {
		this.itemIndex = itemIndex;
	}
	public String getDriverInstanceName() {
		return driverInstanceName;
	}
	public void setDriverInstanceName(String driverInstanceName) {
		this.driverInstanceName = driverInstanceName;
	}
	public String getDriverDesp() {
		return driverDesp;
	}
	public void setDriverDesp(String driverDesp) {
		this.driverDesp = driverDesp;
	}
	public String getLdapName() {
		return ldapName;
	}
	public void setLdapName(String ldapName) {
		this.ldapName = ldapName;
	}
	public String getUserIdentityAttributes() {
		return userIdentityAttributes;
	}
	public void setUserIdentityAttributes(String userIdentityAttributes) {
		this.userIdentityAttributes = userIdentityAttributes;
	}
	public String getSearchScope() {
		return searchScope;
	}
	public void setSearchScope(String searchScope) {
		this.searchScope = searchScope;
	}
	public String getSearchFilter() {
		return searchFilter;
	}
	public void setSearchFilter(String searchFilter) {
		this.searchFilter = searchFilter;
	}
	public Map<String, String> getSubTreeOptions() {
		return subTreeOptions;
	}
	public void setSubTreeOptions(Map<String, String> subTreeOptions) {
		this.subTreeOptions = subTreeOptions;
	}
	public String getAuditUId() {
		return auditUId;
	}
	public void setAuditUId(String auditUId) {
		this.auditUId = auditUId;
	}
	
}
