package com.elitecore.netvertexsm.datamanager.datasource.ldap.data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

import com.elitecore.netvertexsm.web.core.base.BaseData;


public class LDAPDatasourceData extends BaseData implements Serializable, ILDAPDatasourceData{
	
	private long ldapDsId;
	private String name;
/*	private String ipAddress;
	private long port;	*/
	private long timeout;
	private long ldapSizeLimit;
	private  String administrator;
	private String password;
	private String userDNPrefix;
	private long minimumPool;
	private long maximumPool;
	private String commonStatusId;
	private Long statusCheckDuration;	
	private Set ldapBaseDnDetail;
	private List searchDnDetailList;	
	private String address;
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getAdministrator() {
		return administrator;
	}
	public String getCommonStatusId() {
		return commonStatusId;
	}
	public long getLdapSizeLimit() {
		return ldapSizeLimit;
	}
	public long getMaximumPool() {
		return maximumPool;
	}
	public long getMinimumPool() {
		return minimumPool;
	}
	public String getName() {
		return name;
	}
	public String getPassword() {
		return password;
	}
/*	public long getPort() {
		return port;
	}*/
	public long getTimeout() {
		return timeout;
	}
	public String getUserDNPrefix() {
		return userDNPrefix;
	}
	public long getLdapDsId() {
		return ldapDsId;		
	}
	public Set getLdapBaseDnDetail() {
		return ldapBaseDnDetail;
	}
	
	public void setAdministrator(String administrator) {
		this.administrator = administrator;		
	}
	public void setCommonStatusId(String commonStatusId) {
		this.commonStatusId = commonStatusId;		
	}
	public void setLdapSizeLimit(long ldapSizeLimit) {
		this.ldapSizeLimit = ldapSizeLimit;
		
	}
	public void setMaximumPool(long maximumPool) {
		this.maximumPool = maximumPool;		
	}
	public void setMinimumPool(long minimumPool) {
		this.minimumPool = minimumPool;		
	}
	public void setName(String name) {
		this.name = name;
		
	}
	public void setPassword(String password) {
		this.password = password;		
	}
/*	public void setPort(long port) {
		this.port = port;	
	}*/
	public void setTimeout(long timeout) {
		this.timeout = timeout;
		
	}
	public void setUserDNPrefix(String userDNPrefix) {
		this.userDNPrefix = userDNPrefix;
		
	}
	public void setLdapDsId(long ldapDsId) {
		this.ldapDsId = ldapDsId;
	}
	
	public void setLdapBaseDnDetail(Set ldapBaseDnDetail) {	
		this.ldapBaseDnDetail = ldapBaseDnDetail;
	}
/*	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
		
	}*/
	public List getSearchBaseDnList() {
		return this.searchDnDetailList;
	}
	public void setSearchDnDetailList(List searchDnDetailList) {
		this.searchDnDetailList = searchDnDetailList;
		
	}
	public Long getStatusCheckDuration() {
		return statusCheckDuration;
	}
	public void setStatusCheckDuration(Long statusCheckDuration) {
		this.statusCheckDuration = statusCheckDuration;
	}
}