package com.elitecore.elitesm.web.datasource.ldap.forms;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class CreateLDAPDatasourceForm extends BaseWebForm{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private  String ipAddress;
	private long port=389;
	private Long ldapSizeLimit=1l;
	private  String administrator;
	private String password;
	private String userDnPrefix="uid=";
	private long minimumPool=2;
	private long maximumPool=5;
	private String searcBaseDn;
	private String checkAction;
	private int itemIndex;
	private Long statusCheckDuration=120L;
	private Long timeout=1000L;
	private String searchFilter;
	private String address;
	private String ldapVersion;
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getSearchFilter() {
		return searchFilter;
	}
	public void setSearchFilter(String searchFilter) {
		this.searchFilter = searchFilter;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public long getPort() {
		return port;
	}
	public void setPort(long port) {
		this.port = port;
	}
	public Long getTimeout() {
		return timeout;
	}
	public void setTimeout(Long timeout) {
		this.timeout = timeout;
	}
	public Long getLdapSizeLimit() {
		return ldapSizeLimit;
	}
	public void setLdapSizeLimit(Long ldapSizeLimit) {
		this.ldapSizeLimit = ldapSizeLimit;
	}	
	public String getAdministrator() {
		return administrator;
	}
	public void setAdministrator(String administrator) {
		this.administrator = administrator;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUserDnPrefix() {
		return userDnPrefix;
	}
	public void setUserDnPrefix(String userDnPrefix) {
		this.userDnPrefix = userDnPrefix;
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
	public String getSearcBaseDn() {
		return searcBaseDn;
	}
	public void setSearcBaseDn(String searcBaseDn) {
		this.searcBaseDn = searcBaseDn;
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
	public Long getStatusCheckDuration() {
		return statusCheckDuration;
	}
	public void setStatusCheckDuration(Long statusCheckDuration) {
		this.statusCheckDuration = statusCheckDuration;
	}
	public String getLdapVersion() {
		return ldapVersion;
	}
	public void setLdapVersion(String ldapVersion) {
		this.ldapVersion = ldapVersion;
	}
	
}