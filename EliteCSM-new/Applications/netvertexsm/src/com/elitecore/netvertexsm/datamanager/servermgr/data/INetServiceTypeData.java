package com.elitecore.netvertexsm.datamanager.servermgr.data;

/**
 * 
 * @author dhavalraval
 *
 */
public interface INetServiceTypeData {
	public String getNetServiceTypeId();
	public void setNetServiceTypeId(String netServiceTypeId);
	public String getNetServerTypeId();
	public void setNetServerTypeId(String netServerTypeId);
	public String getName();
	public void setName(String name);
	public String getAlias();
	public void setAlias(String alias);
	public String getDescription();
	public void setDescription(String description);
	public int getMaxInstances();
	public void setMaxInstances(int maxInstances);
	public String getSystemGenerated();
	public void setSystemGenerated(String systemGenerated);
	public String toString();
	
}
