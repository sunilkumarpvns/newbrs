package com.elitecore.elitesm.datamanager.servermgr.data;

import java.io.Serializable;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

public class NetServiceInstanceConfMapData extends BaseData implements INetServiceInstanceConfMapData,Serializable {
    
    private static final long serialVersionUID = 5863792472044785623L;
    
    private String netServiceId;
    private String configInstanceId;
    private INetConfigurationInstanceData netConfigurationInstance;
    
    public String getConfigInstanceId() {
        return configInstanceId;
    }
    
    public void setConfigInstanceId(String configInstanceId) {
        this.configInstanceId = configInstanceId;
    }
    
    public String getNetServiceId() {
        return netServiceId;
    }
    
    public void setNetServiceId(String netServiceId) {
        this.netServiceId = netServiceId;
    }
    
    public INetConfigurationInstanceData getNetConfigurationInstance() {
        return netConfigurationInstance;
    }
    
    public void setNetConfigurationInstance(INetConfigurationInstanceData netConfigurationInstance) {
        this.netConfigurationInstance = netConfigurationInstance;
    }
    
}
