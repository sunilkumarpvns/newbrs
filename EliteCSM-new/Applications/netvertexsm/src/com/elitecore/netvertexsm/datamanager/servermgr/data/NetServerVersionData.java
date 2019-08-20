
package com.elitecore.netvertexsm.datamanager.servermgr.data;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

public class NetServerVersionData implements INetServerVersionData, Serializable {

    private static final long serialVersionUID = -5980544015031193371L;

    private String netServerTypeId;
    private String netServerVersion;
    private String configVersion;
    private INetServerTypeData netServerType;

    public String getConfigVersion( ) {
        return configVersion;
    }

    public void setConfigVersion( String configVersion ) {
        this.configVersion = configVersion;
    }

    public String getNetServerTypeId( ) {
        return netServerTypeId;
    }

    public void setNetServerTypeId( String netServerTypeId ) {
        this.netServerTypeId = netServerTypeId;
    }

    public String getNetServerVersion( ) {
        return netServerVersion;
    }

    public void setNetServerVersion( String netServerVersion ) {
        this.netServerVersion = netServerVersion;
    }

    public INetServerTypeData getNetServerType( ) {
        return netServerType;
    }

    public void setNetServerType( INetServerTypeData netServerType ) {
        this.netServerType = netServerType;
    }

    @Override
    public String toString() {
        StringWriter out = new StringWriter();
        PrintWriter writer = new PrintWriter(out);
        writer.println();
        writer.println("------------NetServerVersionData-----------------");
        writer.println("netServerTypeId=" +netServerTypeId);                                     
        writer.println("netServerVersion=" +netServerVersion);                                     
        writer.println("configVersion=" +configVersion);                                     
        writer.println("netServerType=" +netServerType);                                     
        writer.println("----------------------------------------------------");
        writer.close();
        return out.toString();
    }

}
