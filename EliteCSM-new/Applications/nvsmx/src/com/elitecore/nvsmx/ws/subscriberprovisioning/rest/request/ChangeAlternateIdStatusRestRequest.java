package com.elitecore.nvsmx.ws.subscriberprovisioning.rest.request;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ChangeAlternateIdStatusRestRequest {
    private String subscriberId;
    private String alternateId;
    private String status;




    public String getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(String subscriberId) {
        this.subscriberId = subscriberId;
    }

    public String getAlternateId() {
        return alternateId;
    }

    public void setAlternateId(String alternateId) {
        this.alternateId = alternateId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
