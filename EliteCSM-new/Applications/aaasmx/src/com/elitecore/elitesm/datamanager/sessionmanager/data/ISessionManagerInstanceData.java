package com.elitecore.elitesm.datamanager.sessionmanager.data;

import java.sql.Timestamp;

public interface ISessionManagerInstanceData {
	
	
	public String getSmInstanceId(); 
	public void setSmInstanceId(String smInstanceId); 
	public String getName();
	public void setName(String name);
	public String getDescription();
	public void setDescription(String description);
	public String getStatus();
	public void setStatus(String status);
	public String getCreatedbystaffid();
	public void setCreatedbystaffid(String createdbystaffid);
	public String getLastmodifiedbystaffid();
	public void setLastmodifiedbystaffid(String lastmodifiedbystaffid);
	public Timestamp getLastmodifieddate();
	public void setLastmodifieddate(Timestamp lastmodifieddate);
	public Timestamp getCreatedate();
	public void setCreatedate(Timestamp createdate);
	public SMConfigInstanceData getSmConfigInstanceData();
	public void setSmConfigInstanceData(SMConfigInstanceData smConfigInstanceData);
	public String getAuditUId();
	public void setAuditUId(String auditUId);
}
