package com.elitecore.netvertexsm.datamanager.servermgr.data;

/**
 * 
 * @author dhavalraval
 *
 */
public interface INetConfigurationInstanceData extends Comparable<INetConfigurationInstanceData>{
	public long getConfigInstanceId();
	public void setConfigInstanceId(long configInstanceId);
	public void setConfigId(String configId);
	public String getConfigId();
	public INetConfigurationData getNetConfiguration();
	public void setNetConfiguration(INetConfigurationData netConfiguration);
	public String toString();

}
