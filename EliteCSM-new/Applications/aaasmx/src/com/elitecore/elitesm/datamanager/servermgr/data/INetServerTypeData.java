package com.elitecore.elitesm.datamanager.servermgr.data;

/**
 * @author dhavalraval
 *
 */
public interface INetServerTypeData {
	public String getNetServerTypeId();
	public void setNetServerTypeId(String netServerTypeId);
	public String getName();
	public void setName(String name);
	public String getAlias();
	public void setAlias(String alias);
	public String getDescription();
	public void setDescription(String description);
	public String getSystemGenerated();
	public void setSystemGenerated(String systemGenerated);
	public String getVersion();
	public void setVersion(String version);
	public String getStartupScriptName();
	public void setStartupScriptName(String startupScriptName);
	public String toString();
	
}
