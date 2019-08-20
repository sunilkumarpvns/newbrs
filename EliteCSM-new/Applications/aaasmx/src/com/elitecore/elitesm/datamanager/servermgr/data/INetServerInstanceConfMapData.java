package com.elitecore.elitesm.datamanager.servermgr.data;

public interface INetServerInstanceConfMapData {
	public String getConfigInstanceId();
	public void setConfigInstanceId(String configInstanceId);
	public String getNetServerId();
	public void setNetServerId(String netServerId);
	public INetConfigurationInstanceData getNetConfigurationInstance();
	public void setNetConfigurationInstance(INetConfigurationInstanceData netConfigurationInstance);
	public String toString();
}
