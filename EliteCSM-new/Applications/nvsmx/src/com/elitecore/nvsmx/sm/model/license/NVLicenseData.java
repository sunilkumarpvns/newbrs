package com.elitecore.nvsmx.sm.model.license;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author Kartik Prajapati
 */

@Entity(name="com.elitecore.nvsmx.sm.model.license.NVLicenseData")
@Table(name = "TBLM_LICENSE")
public class NVLicenseData implements Serializable {

    private String id;
    private String instanceName;
    private String ip;
    private Timestamp lastUpdateTime;

    private String version;
    private String key;

    public NVLicenseData(){
        super();
    }

    @Id
    @Column( name = "ID" )
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column( name = "INSTANCE_NAME" )
    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    @Column( name = "IP" )
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Transient
    public String getVersion(){
        return version;
    }

    public void setVersion(String version){
        this.version = version;
    }

    @Transient
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Column( name = "LAST_UPDATE_TIME" )
    public Timestamp getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Timestamp lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
}


