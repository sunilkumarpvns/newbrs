package com.elitecore.elitesm.datamanager.datasource.ldap.data;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

public interface ILDAPDatasourceData{

	public String getName();
	public void setName(String name);
	
	public Long getTimeout();
	public void setTimeout(Long timeout);
	
	public Long getLdapSizeLimit();
	public void setLdapSizeLimit(Long ldapSizeLimit);
	
	public String getAdministrator();
	public void setAdministrator(String administrator);
	
	public String getPassword();
	public void setPassword(String password);
	
	public String getUserDNPrefix();
	public void setUserDNPrefix(String userDNPrefix);
	
	public Long getMinimumPool();
	public void setMinimumPool(Long minimumPool);
	
	public Long getMaximumPool();
	public void setMaximumPool(Long maximumPool);
	
	public String getCommonStatusId();
	public void setCommonStatusId(String commonStatusId);
	
	public String getLastModifiedByStaffId();
	public void setLastModifiedByStaffId(String lastModifiedByStaffId);
	
	public String getCreatedByStaffId();
	public void setCreatedByStaffId(String createdByStaffId);
	
	public Timestamp getLastModifiedDate();
	public void setLastModifiedDate(Timestamp lastModifiedDate);
	
	public Timestamp getCreateDate();
	public void setCreateDate(Timestamp createDate);
	
	public String getLdapDsId();
	public void setLdapDsId(String ldapDsId);
	
	public Set getLdapBaseDnDetail();
	public void setLdapBaseDnDetail(Set ldapBaseDnDetailValues);
	
	public List getSearchDnDetailList();
	public void setSearchDnDetailList(List searchDnDetailList);
	
	public void setStatusCheckDuration(Long statusCheckDuration);
	public Long getStatusCheckDuration();
	
	public String getAddress();
	public void setAddress(String address);
	
	public Integer getLdapVersion(); 
	public void setLdapVersion(Integer ldapVersion);
	
	public String getAuditUId();
	public void setAuditUId(String auditUId);
	
}