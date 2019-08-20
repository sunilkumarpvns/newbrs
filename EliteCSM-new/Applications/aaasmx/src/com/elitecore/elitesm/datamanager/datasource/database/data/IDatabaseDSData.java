package com.elitecore.elitesm.datamanager.datasource.database.data;

import java.sql.Timestamp;


public interface IDatabaseDSData {
	
	public String getDatabaseId();
	public void setDatabaseId(String datasourceId);
			
	public String getName();
	public void setName(String Name);
	
	public String getConnectionUrl();
	public void setConnectionUrl(String connectionUrl);
	
	public String getUserName();
	public void setUserName(String userName);
	
	public String getPassword();
	public void setPassword(String password);
					
	public Long getMaximumPool();
	public void setMaximumPool(Long maximumPool);
	
	public Long getMinimumPool();
	public void setMinimumPool(Long minimumPool);
	
	
	
	public String getLastmodifiedByStaffId();
	public void setLastmodifiedByStaffId(String lastmodifiedByStaffId);
	
	public String getCreatedByStaffId();
	public void setCreatedByStaffId(String createdByStaffId);
	
	public Timestamp getLastmodifiedDate();
	public void setLastmodifiedDate(Timestamp lastModifiedDate);
	
	public Timestamp getCreateDate();
	public void setCreateDate(Timestamp createDate);
	
	public Long getTimeout();
	public void setTimeout(Long timeout);
	
	public Long getStatusCheckDuration();
	public void setStatusCheckDuration(Long statusCheckDuration);
	
	public String getAuditUId();
	public void setAuditUId(String auditUId);
	

}
