package com.elitecore.elitesm.datamanager.servermgr.data;

public interface INetServerVersionData {
    
    public String getConfigVersion( );
    
    public void setConfigVersion( String configVersion );
    
    public String getNetServerTypeId( );
    
    public void setNetServerTypeId( String netServerTypeId );
    
    public String getNetServerVersion( );
    
    public void setNetServerVersion( String netServerVersion );
    public String toString();
}
