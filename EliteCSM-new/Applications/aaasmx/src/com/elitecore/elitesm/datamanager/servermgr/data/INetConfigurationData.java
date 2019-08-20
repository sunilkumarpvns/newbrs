package com.elitecore.elitesm.datamanager.servermgr.data;

import java.util.Set;

public interface INetConfigurationData {
    public String getNetConfigId();
    public void setNetConfigId(String netConfigId);
    public int getSerialNo();
    public void setSerialNo(int serialNo);
    public String getName();
    public void setName(String name);
    public String getDisplayName();
    public void setDisplayName(String displayName);
    public String getFileName();
    public void setFileName(String fileName) ;
    public Set<INetConfigurationParameterData> getNetConfigParameters();
    public void setNetConfigParameters(Set<INetConfigurationParameterData> netConfigParameters);
    public String getAlias();
    public void setAlias(String alias);
    public String toString();
}
