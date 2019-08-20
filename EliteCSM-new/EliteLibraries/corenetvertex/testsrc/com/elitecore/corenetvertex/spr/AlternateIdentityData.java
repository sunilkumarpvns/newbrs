package com.elitecore.corenetvertex.spr;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name = "com.elitecore.corenetvertex.spr.AlternateIdentityData")
@Table(name = "TBLM_ALTERNATE_IDENTITY")
public class AlternateIdentityData {

    private String subscriberId;
    private String alternateId;
    private String type;
    private String status;

    @Column(name = "SUBSCRIBER_ID")
    public String getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(String subscriberId) {
        this.subscriberId = subscriberId;
    }

    @Id
    @Column(name = "ALTERNATE_ID", unique = true)
    public String getAlternateId() {
        return alternateId;
    }

    public void setAlternateId(String alternateId) {
        this.alternateId = alternateId;
    }

    @Column(name="TYPE" ,columnDefinition = "varchar(255) default 'SPR'" )
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Column(name="STATUS",columnDefinition = "varchar(255) default 'ACTIVE'")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
