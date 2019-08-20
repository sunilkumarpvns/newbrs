package com.elitecore.elitesm.datamanager.servermgr.plugins.data;

import java.util.Set;

public interface IPluginServiceTypeData {

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
	
	public char getStatus();
	public void setStatus(char status);
	
	public Set getPluginTypeSet();
	public void setPluginTypeSet(Set pluginTypeSet);
	
	@Override
	public String toString();
	
}
