package com.elitecore.nvsmx.remotecommunications.data;

import com.elitecore.corenetvertex.sm.servergroup.ServerGroupData;
import com.elitecore.corenetvertex.sm.serverinstance.ServerInstanceData;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by aditya on 4/19/17.
 */
@XmlRootElement
public class ServerInformation {


    private String name;
    private String netServerCode;
    private String id;

    public ServerInformation(String name, String netServerCode, String id) {
        this.name = name;
        this.netServerCode = netServerCode;
        this.id = id;
    }
    public ServerInformation(String name,  String id) {
        this.name = name;
        this.id = id;
    }

    public ServerInformation(){}

    public String getName() {
        return name;
    }

    public String getNetServerCode() {
        return netServerCode;
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }


    public static ServerInformation from(ServerGroupData serverInstanceGroupData){
        return new ServerInformation(serverInstanceGroupData.getName(),serverInstanceGroupData.getId());
    }

    public static ServerInformation from(ServerInstanceData netServerInstanceData){
        return new ServerInstanceInformation(netServerInstanceData.getName(),netServerInstanceData.getId(),netServerInstanceData.getId(), netServerInstanceData.getRestApiUrl());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }
        if (!(o instanceof ServerInformation)){
            return false;
        }

        ServerInformation that = (ServerInformation) o;

        if (!getName().equals(that.getName())){
            return false;
        }
        if (!getNetServerCode().equals(that.getNetServerCode())){
            return false;
        }
        return getId().equals(that.getId());

    }

    @Override
    public int hashCode() {
        int result = getName().hashCode();
        result = 31 * result + getNetServerCode().hashCode();
        result = 31 * result + getId().hashCode();
        return result;
    }
}
