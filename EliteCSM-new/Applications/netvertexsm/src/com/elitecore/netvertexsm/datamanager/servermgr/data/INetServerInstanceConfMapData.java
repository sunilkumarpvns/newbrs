package com.elitecore.netvertexsm.datamanager.servermgr.data;

public interface INetServerInstanceConfMapData {
	public long getConfigInstanceId();
	public void setConfigInstanceId(long configInstanceId);
	public long getNetServerId();
	public void setNetServerId(long netServerId);
	public INetConfigurationInstanceData getNetConfigurationInstance();
	public void setNetConfigurationInstance(INetConfigurationInstanceData netConfigurationInstance);
	public String toString();
}
