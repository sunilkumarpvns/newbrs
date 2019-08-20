package com.elitecore.netvertexsm.datamanager.servermgr.data;

public interface INetServiceConfigMapData {
	public String getNetConfigId();
	public void setNetConfigId(String netConfigId);
	public String getNetServiceTypeId();
	public void setNetServiceTypeId(String netServiceTypeId);
	public String getNetServiceInstance() ;
	public void setNetServiceInstance(String netServiceInstance) ;
	public String getNetConfigurationInstance();
	public void setNetConfigurationInstance(String netConfigurationInstance);
	public String getNetConfigMapTypeId();
	public void setNetConfigMapTypeId(String netConfigMapTypeId) ;
	public String toString();
}
