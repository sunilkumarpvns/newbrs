package com.elitecore.elitesm.datamanager.servermgr.plugins.universalplugin.data;


public interface IUniversalPluginData {

	public String getPluginId();
	public void setPluginId(String pluginId);

	public String getPluginInstanceId();
	public void setPluginInstanceId(String pluginInstanceId);

	public byte[] getPluginData();
	public void setPluginData(byte[] pluginData);
	
}
