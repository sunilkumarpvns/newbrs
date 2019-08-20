package com.elitecore.netvertexsm.web.datasource.ldap.form;

import java.sql.Timestamp;

import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;

public class UpdateLDAPDatasourceForm extends BaseWebForm{
	
	private long LdapDsId;	
	private String name; 
	private long ldapSizeLimit;
	private  String administrator;
	private String password;
	private String userDNPrefix;
	private long minimumPool;
	private long maximumPool;
	private char commonStatusId;
	private long lastModifiedByStaffId;
	private long createdByStaffId;
	private Timestamp lastModifiedDate;
	private Timestamp createDate;
	private String searchBaseDn;
	private String checkAction;
	private int itemIndex;
	private Long statusCheckDuration=120L;
	private Long timeout=1000L;
	private String address;
	// mapping related arrays
	private String[] searchDn;
	
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public long getLdapDsId() {
		return LdapDsId;
	}
	public void setLdapDsId(long ldapDsId) {
		LdapDsId = ldapDsId;
	}	
	public String getCheckAction() {
		return checkAction;
	}
	public void setCheckAction(String checkAction) {
		this.checkAction = checkAction;
	}
	public int getItemIndex() {
		return itemIndex;
	}
	public void setItemIndex(int itemIndex) {
		this.itemIndex = itemIndex;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	} 
	public long getTimeout() {
		return timeout;
	}
	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}
	public long getLdapSizeLimit() {
		return ldapSizeLimit;
	}
	public void setLdapSizeLimit(long ldapSizeLimit) {
		this.ldapSizeLimit = ldapSizeLimit;
	}
	public String getAdministrator() {
		return administrator;
	}
	public void setAdministrator(String administraor) {
		this.administrator = administraor;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUserDNPrefix() {
		return userDNPrefix;
	}
	public void setUserDNPrefix(String userDNPrefix) {
		this.userDNPrefix = userDNPrefix;
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
	public char getCommonStatusId() {
		return commonStatusId;
	}
	public void setCommonStatusId(char commonStatusId) {
		this.commonStatusId = commonStatusId;
	}
	public long getLastModifiedByStaffId() {
		return lastModifiedByStaffId;
	}
	public void setLastModifiedByStaffId(long lastModifiedByStaffId) {
		this.lastModifiedByStaffId = lastModifiedByStaffId;
	}
	public long getCreatedByStaffId() {
		return createdByStaffId;
	}
	public void setCreatedByStaffId(long createdByStaffId) {
		this.createdByStaffId = createdByStaffId;
	}
	public Timestamp getLastModifiedDate() {
		return lastModifiedDate;
	}
	public void setLastModifiedDate(Timestamp lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	public Timestamp getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}
	public String getSearchBaseDn() {
		return searchBaseDn;
	}
	public void setSearchBaseDn(String searchBaseDn) {
		this.searchBaseDn = searchBaseDn;
	}
	public String[] getSearchDn() {
		return searchDn;
	}
	public void setSearchDn(String[] searchDn) {
		this.searchDn = searchDn;
	}
	public Long getStatusCheckDuration() {
		return statusCheckDuration;
	}
	public void setStatusCheckDuration(Long statusCheckDuration) {
		this.statusCheckDuration = statusCheckDuration;
	}
	
	
}
