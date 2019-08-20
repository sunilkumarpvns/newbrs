package com.elitecore.elitesm.datamanager.servermgr.plugins.data;


public interface IPluginTypesData {

	public String getPluginTypeId();
	public void setPluginTypeId(String pluginTypeId);
	
	public String getPluginServiceTypeId();
	public void setPluginServiceTypeId(String pluginServiceTypeId);
	
	public String getName();
	public void setName(String name);
	
	public String getDisplayName();
	public void setDisplayName(String displayName);
	
	public String getAlias();
	public void setAlias(String alias);
	
	public long getSerialNo();
	public void setSerialNo(long serialNo);
	
	public String getDescription();
	public void setDescription(String description);
	
	public String getStatus();
	public void setStatus(String status);
}
