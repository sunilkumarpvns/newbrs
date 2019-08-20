package com.elitecore.netvertexsm.datamanager.datasource.ldap.data;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

public interface ILDAPDatasourceData {
	public String getName();
	public void setName(String name);
	
	public String getAddress();
	public void setAddress(String ipAddress);
	
/*	public long getPort();
	public void setPort(long port);*/
	
	public long getTimeout();
	public void setTimeout(long timeout);
	
	public long getLdapSizeLimit();
	public void setLdapSizeLimit(long ldapSizeLimit);
	
	public String getAdministrator();
	public void setAdministrator(String administrator);
	
	public String getPassword();
	public void setPassword(String password);
	
	public String getUserDNPrefix();
	public void setUserDNPrefix(String userDNPrefix);
	
	public long getMinimumPool();
	public void setMinimumPool(long minimumPool);
	
	public long getMaximumPool();
	public void setMaximumPool(long maximumPool);
	
	public String getCommonStatusId();
	public void setCommonStatusId(String commonStatusId);
	
	public long getLdapDsId();
	public void setLdapDsId(long ldapDsId);
	
	public Set getLdapBaseDnDetail();
	public void setLdapBaseDnDetail(Set ldapBaseDnDetailValues);
	
	public List getSearchBaseDnList();
	public void setSearchDnDetailList(List searchDnDetailList);
	
	public void setStatusCheckDuration(Long statusCheckDuration);
	public Long getStatusCheckDuration();
	
	public Timestamp getCreatedDate();
	public void setCreatedDate(Timestamp createdDate);
	
	public Timestamp getModifiedDate();
	public void setModifiedDate(Timestamp modifiedDate);
	
	public Long getCreatedByStaffId();
	public void setCreatedByStaffId(Long createdByStaffId);
	
	public Long getModifiedByStaffId();
	public void setModifiedByStaffId(Long modifiedByStaffId);
	
	public String getClientIp();
	public void setClientIp(String clientIp);
}