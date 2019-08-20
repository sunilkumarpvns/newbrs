package com.elitecore.netvertexsm.datamanager.servermgr.data;

public interface INetServiceInstanceConfMapData {
	public long getConfigInstanceId() ;
	public void setConfigInstanceId(long configInstanceId);
	public long getNetServiceId();
	public void setNetServiceId(long netServiceId);
	public INetConfigurationInstanceData getNetConfigurationInstance();
	public void setNetConfigurationInstance(
			INetConfigurationInstanceData netConfigurationInstance);
	public String toString();
}
