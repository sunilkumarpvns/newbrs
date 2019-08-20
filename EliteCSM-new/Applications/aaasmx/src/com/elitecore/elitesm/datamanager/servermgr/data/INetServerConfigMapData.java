package com.elitecore.elitesm.datamanager.servermgr.data;

public interface INetServerConfigMapData {
	public String getNetConfigId();
	public void setNetConfigId(String netConfigId);
	public String getNetServerTypeId();
	public void setNetServerTypeId(String netServerTypeId);
	public String getNetServerInstance();
	public void setNetServerInstance(String netServerInstance);
	public String getNetConfigurationInstance();
	public void setNetConfigurationInstance(String netConfigurationInstance);
	public String getNetConfigMapTypeId();
	public void setNetConfigMapTypeId(String netConfigMapTypeId);
	public String toString();
}
