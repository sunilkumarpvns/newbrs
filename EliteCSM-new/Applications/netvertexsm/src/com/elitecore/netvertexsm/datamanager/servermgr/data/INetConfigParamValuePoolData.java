package com.elitecore.netvertexsm.datamanager.servermgr.data;

/**
 * @author dhavalraval
 */
public interface INetConfigParamValuePoolData {
    
    public String getParamPoolId( );    
    public void setParamPoolId( String paramPoolId );    
    public String getParameterId( );    
    public void setParameterId( String parameterId );    
    public String getName( );    
    public void setName( String name );    
    public String getValue( );    
    public void setValue( String value );    
    public String getConfigId( );    
    public void setConfigId( String configId );
    public String toString();
}