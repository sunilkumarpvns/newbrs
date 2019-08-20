package com.elitecore.netvertexsm.datamanager.servermgr.data;

import java.io.Serializable;

public class NetServiceInstanceConfMapData implements INetServiceInstanceConfMapData,Serializable {
    
    private static final long serialVersionUID = 5863792472044785623L;
    
    private long netServiceId;
    private long configInstanceId;
    private INetConfigurationInstanceData netConfigurationInstance;
    
    public long getConfigInstanceId() {
        return configInstanceId;
    }
    
    public void setConfigInstanceId(long configInstanceId) {
        this.configInstanceId = configInstanceId;
    }
    
    public long getNetServiceId() {
        return netServiceId;
    }
    
    public void setNetServiceId(long netServiceId) {
        this.netServiceId = netServiceId;
    }
    
    public INetConfigurationInstanceData getNetConfigurationInstance() {
        return netConfigurationInstance;
    }
    
    public void setNetConfigurationInstance(INetConfigurationInstanceData netConfigurationInstance) {
        this.netConfigurationInstance = netConfigurationInstance;
    }
    
}
