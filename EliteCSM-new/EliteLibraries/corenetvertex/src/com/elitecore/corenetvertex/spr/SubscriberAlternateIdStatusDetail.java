package com.elitecore.corenetvertex.spr;

public class SubscriberAlternateIdStatusDetail {
    private String alternateId;
    private String status;
    private String type;

    public SubscriberAlternateIdStatusDetail(String alternateId, String status, String type) {
        this.alternateId = alternateId;
        this.status = status;
        this.type = type;
    }

    public String getAlternateId() {
        return alternateId;
    }

    public String getStatus() {
        return status;
    }

    public String getType() {
        return type;
    }
}
