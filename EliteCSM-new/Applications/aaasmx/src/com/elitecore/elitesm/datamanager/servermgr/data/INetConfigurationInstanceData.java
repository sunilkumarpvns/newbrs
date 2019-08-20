package com.elitecore.elitesm.datamanager.servermgr.data;

/**
 * 
 * @author dhavalraval
 *
 */
public interface INetConfigurationInstanceData extends Comparable<INetConfigurationInstanceData>{
	public String getConfigInstanceId();
	public void setConfigInstanceId(String configInstanceId);
	public void setConfigId(String configId);
	public String getConfigId();
	public INetConfigurationData getNetConfiguration();
	public void setNetConfiguration(INetConfigurationData netConfiguration);
	public String toString();

}
