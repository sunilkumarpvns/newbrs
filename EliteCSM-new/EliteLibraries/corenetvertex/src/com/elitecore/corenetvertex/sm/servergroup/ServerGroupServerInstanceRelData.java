package com.elitecore.corenetvertex.sm.servergroup;

import com.elitecore.corenetvertex.sm.serverinstance.ServerInstanceData;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Created by kirpalsinh on 2/8/17.
 */

@Entity (name = "com.elitecore.corenetvertex.sm.servergroup.ServerGroupServerInstanceRelData")
@Table( name = "TBLM_SERVER_INSTANCE_GROUP_REL")
public class ServerGroupServerInstanceRelData implements Serializable{

    private static final long serialVersionUID = 1L;

    private String id;
    private Integer serverWeightage;
    private transient  ServerInstanceData serverInstanceData;
    private transient  ServerGroupData serverGroupData;

    @Id
    @Column(name = "ID", nullable = false)
    @GeneratedValue(generator = "eliteSequenceGenerator")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    @OneToOne( fetch = FetchType.EAGER )
    @JoinColumn( name = "SERVER_INSTANCE_ID")
    public ServerInstanceData getServerInstanceData() {
        return serverInstanceData;
    }

    public void setServerInstanceData(ServerInstanceData serverInstanceData) {
        this.serverInstanceData = serverInstanceData;
    }

    @ManyToOne( fetch = FetchType.EAGER  )
    @JoinColumn( name = "SERVER_INSTANCE_GROUP_ID")
    public ServerGroupData getServerGroupData() {
        return serverGroupData;
    }

    public void setServerGroupData(ServerGroupData serverGroupData) {
        this.serverGroupData = serverGroupData;
    }

    @Column ( name = "SERVER_WEIGHTAGE", nullable = false )
    public Integer getServerWeightage() {
        return serverWeightage;
    }

    public void setServerWeightage(Integer serverWeightage) {
        this.serverWeightage = serverWeightage;
    }


}
