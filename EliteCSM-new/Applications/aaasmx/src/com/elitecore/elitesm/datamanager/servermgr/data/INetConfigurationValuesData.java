package com.elitecore.elitesm.datamanager.servermgr.data;

public interface INetConfigurationValuesData extends Comparable,Cloneable{
    public String getParameterValueId();
    public void setParameterValueId(String parameterValueId);
    public String getParameterId() ;
    public void setParameterId(String parameterId);
    public String getConfigInstanceId();
    public void setConfigInstanceId(String configInstanceId);
    public String getInstanceId() ;
    public void setInstanceId(String instanceId);
    public String getValue();
    public void setValue(String value);
    public String getConfigId();
    public void setConfigId(String configId);
    public INetConfigurationValuesData clone();
    
    @Deprecated
    public NetConfigurationParameterData getNetConfigurationParameterData() ;
    @Deprecated
    public void setNetConfigurationParameterData(NetConfigurationParameterData netConfigurationParameterData) ;
    public String toString();
    
}
