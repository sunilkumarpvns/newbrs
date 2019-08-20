package com.elitecore.netvertexsm.datamanager.datasource.database.data;

import java.sql.Timestamp;


public interface IDatabaseDSData {
	
	public long getDatabaseId();
	public void setDatabaseId(long datasourceId);
			
	public String getName();
	public void setName(String Name);
	
	public String getConnectionUrl();
	public void setConnectionUrl(String connectionUrl);
	
	public String getUserName();
	public void setUserName(String userName);
	
	public String getPassword();
	public void setPassword(String password);
					
	public long getMaximumPool();
	public void setMaximumPool(long maximumPool);
	
	public long getMinimumPool();
	public void setMinimumPool(long minimumPool);
	
	public Long getTimeout();
	public void setTimeout(Long timeout);
	
	public Long getStatusCheckDuration();
	public void setStatusCheckDuration(Long statusCheckDuration);

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