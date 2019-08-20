
package com.elitecore.elitesm.datamanager.servermgr.data;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Set;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

/**
 * @author dhavalraval
 */
public class NetConfigurationData extends BaseData implements INetConfigurationData {
    
    private String netConfigId;
    private int serialNo;
    private String name;
    private String displayName;
    private String fileName;
    private String alias;
    private String configVersion;
    
    private Set<INetConfigurationParameterData> netConfigParameters;
    
    public String getConfigVersion( ) {
        return configVersion;
    }
    
    public void setConfigVersion( String configVersion ) {
        this.configVersion = configVersion;
    }
    
    public String getNetConfigId( ) {
        return netConfigId;
    }
    
    public void setNetConfigId( String netConfigId ) {
        this.netConfigId = netConfigId;
    }
    
    public String getName( ) {
        return name;
    }
    
    public void setName( String name ) {
        this.name = name;
    }
    
    public String getDisplayName( ) {
        return displayName;
    }
    
    public void setDisplayName( String displayName ) {
        this.displayName = displayName;
    }
    
    public String getFileName( ) {
        return fileName;
    }
    
    public void setFileName( String fileName ) {
        this.fileName = fileName;
    }
    
    public int getSerialNo( ) {
        return serialNo;
    }
    
    public void setSerialNo( int serialNo ) {
        this.serialNo = serialNo;
    }
    
    public String getAlias( ) {
        return alias;
    }
    
    public void setAlias( String alias ) {
        this.alias = alias;
    }
    
    
    public Set<INetConfigurationParameterData> getNetConfigParameters( ) {
        return netConfigParameters;
    }
    
    public void setNetConfigParameters( Set<INetConfigurationParameterData> netConfigParameters ) {
        this.netConfigParameters = netConfigParameters;
    }
    
    @Override
    public String toString() {
        StringWriter out = new StringWriter();
        PrintWriter writer = new PrintWriter(out);
        writer.println();
        writer.println("------------NetConfigurationData-----------------");
        writer.println("netConfigId=" +netConfigId);  
        writer.println("serialNo=" +serialNo);  
        writer.println("name=" +name);  
        writer.println("displayName=" +displayName);  
        writer.println("fileName=" +fileName);  
        writer.println("alias=" +alias);  
        writer.println("configVersion=" +configVersion);  

        writer.println("----------------------------------------------------");
        writer.close();
        return out.toString();
    }
    
    
}
