package com.elitecore.elitesm.datamanager.servermgr.data;

public interface INetServiceInstanceConfMapData {
	public String getConfigInstanceId() ;
	public void setConfigInstanceId(String configInstanceId);
	public String getNetServiceId();
	public void setNetServiceId(String netServiceId);
	public INetConfigurationInstanceData getNetConfigurationInstance();
	public void setNetConfigurationInstance(
			INetConfigurationInstanceData netConfigurationInstance);
	public String toString();
}
