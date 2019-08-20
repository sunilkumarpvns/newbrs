package com.elitecore.elitesm.datamanager.servermgr.plugins.data;

import java.sql.Timestamp;

public interface IPluginInstData {
	
	public String getPluginInstanceId();
	public void setPluginInstanceId(String pluginInstanceId);
	
	public String getName();
	public void setName(String name);
	
	public String getDescription();
	public void setDescription(String description);
	
	public String getStatus();
	public void setStatus(String status);
	
	public String getCreatedByStaffId();
	public void setCreatedByStaffId(String createdByStaffId);
	
	public String getLastModifiedByStaffId();
	public void setLastModifiedByStaffId(String lastModifiedByStaffId);
	
	public Timestamp getLastModifiedDate();
	public void setLastModifiedDate(Timestamp lastModifiedDate);
	
	public Timestamp getCreateDate();
	public void setCreateDate(Timestamp createDate);
	
	public String getPluginTypeId();
	public void setPluginTypeId(String pluginTypeId);
	
	public PluginTypesData getPluginTypesData();
	public void setPluginTypesData(PluginTypesData pluginTypesData);
	
	public String getPluginTypeName();
	public void setPluginTypeName(String pluginTypeName);
	
	public String getAuditUId();
	public void setAuditUId(String auditUId);
		
}
