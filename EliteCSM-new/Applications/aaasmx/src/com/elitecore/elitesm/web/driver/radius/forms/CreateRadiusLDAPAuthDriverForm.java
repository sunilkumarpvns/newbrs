package com.elitecore.elitesm.web.driver.radius.forms;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.elitecore.elitesm.datamanager.datasource.ldap.data.LDAPDatasourceData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.LogicalNameValuePoolData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.ldapdriver.data.LDAPAuthFieldMapData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class CreateRadiusLDAPAuthDriverForm extends BaseWebForm{

	
	private long ldapDriverId;
	//private long dsStatusCheckInterval = 5;
	private String expiryDatePattern;	
	private long passwordDecryptType;
	private long queryMaxExecTime = 1;
	private long driverInstanceId;	
	private String ldapDsId;
	private String ldapAttribute;
	private List<LDAPDatasourceData> ldapDSList;
	private List<LogicalNameValuePoolData>logicalNameList;
	private String logicalName;
	private String action;
	private int itemIndex;
	///request Parameters
	
	private String driverInstanceName;
	private String driverDesp;
	private String driverRelatedId;
	private Long maxQueryTimeoutCnt=100L;
	private String defaultValue;
	private String valueMapping;
	private String userIdentityAttributes; 
	private String searchScope;
	private String searchFilter;
	private Map<String, String> subTreeOptions;
	
	public Long getMaxQueryTimeoutCnt() {
		return maxQueryTimeoutCnt;
	}
	public void setMaxQueryTimeoutCnt(Long maxQueryTimeoutCnt) {
		this.maxQueryTimeoutCnt = maxQueryTimeoutCnt;
	}
	public long getLdapDriverId() {
		return ldapDriverId;
	}
	public void setLdapDriverId(long ldapDriverId) {
		this.ldapDriverId = ldapDriverId;
	}
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
	public long getDriverInstanceId() {
		return driverInstanceId;
	}
	public void setDriverInstanceId(long driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
	}
	public String getLdapDsId() {
		return ldapDsId;
	}
	public void setLdapDsId(String ldapDsId) {
		this.ldapDsId = ldapDsId;
	}
	public List<LDAPDatasourceData> getLdapDSList() {
		return ldapDSList;
	}
	public void setLdapDSList(List<LDAPDatasourceData> ldapDSList) {
		this.ldapDSList = ldapDSList;
	}
	public String getLdapAttribute() {
		return ldapAttribute;
	}
	public void setLdapAttribute(String ldapAttribute) {
		this.ldapAttribute = ldapAttribute;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
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
	public String getDriverRelatedId() {
		return driverRelatedId;
	}
	public void setDriverRelatedId(String driverRelatedId) {
		this.driverRelatedId = driverRelatedId;
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
	public int getItemIndex() {
		return itemIndex;
	}
	public void setItemIndex(int itemIndex) {
		this.itemIndex = itemIndex;
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
	public List<LDAPAuthFieldMapData> getDefaultmapping(){
		List<LDAPAuthFieldMapData> defaultMappingList = new ArrayList<LDAPAuthFieldMapData>();		
		String[] logicalnm = {"User Name","CUI","User Password","Password Check","Encryption Type","Customer Status","Calling Station ID"
							,"Authorization Policy","Customer Check Items","Customer Reply Items","Expiry Date","Credit Limit"};
		String[] ldapAttribute = {"USERNAME","CUI","PASSWORD","PASSWORDCHECK","ENCRYPTIONTYPE","CUSTOMERSTATUS","CALLINGSTATIONID","RADIUSPOLICY"
                			,"CUSTOMERCHECKITEM","CUSTOMERREPLYITEM","EXPIRYDATE","CREDITLIMIT"};
		String[] logicalv = {"User-Name","CUI","User-Password","PasswordCheck","EncryptionType","CustomerStatus","Calling-Station-ID","AuthorizationPolicy"
							,"CustomerCheckItems","CustomerReplyItems","ExpiryDate","CreditLimit"};
		for(int index = 0 ; index < logicalnm.length ; index++){
			LDAPAuthFieldMapData ldapAuthFieldMapData = new LDAPAuthFieldMapData();
			LogicalNameValuePoolData nameValuePoolData = new LogicalNameValuePoolData();
			nameValuePoolData.setName(logicalnm[index]);
			nameValuePoolData.setValue(logicalv[index]);
			ldapAuthFieldMapData.setNameValuePoolData(nameValuePoolData);
			ldapAuthFieldMapData.setLdapAttribute(ldapAttribute[index]);
			//httpAuthDriverFieldMapData.setHttpAuthFieldMapId(Long.parseLong(responseParameterIndex[index]));
			defaultMappingList.add(ldapAuthFieldMapData);
		}
		return defaultMappingList;
	}
	
}
