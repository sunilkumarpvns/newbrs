package com.elitecore.nvsmx.sm.model.license;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity(name="com.elitecore.nvsmx.sm.model.license.SMLicenseData")
@Table(name = "TBLM_SM_LICENSE")
public class SMLicenseData {

    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "LICENSE")
    private byte[] license;

    @Column(name = "UPDATE_TIME")
    private Timestamp updatedTime;

    public SMLicenseData(){
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public byte[] getLicense() {
        return license;
    }

    public void setLicense(byte[] license) {
        this.license = license;
    }

    public Timestamp getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Timestamp updatedTime) {
        this.updatedTime = updatedTime;
    }
}
