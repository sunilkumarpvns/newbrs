package com.elitecore.nvsmx.ws.subscriberprovisioning.rest.request;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AddAlternateIdRestRequest {
    private String subscriberId;
    private String alternateId;




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

}
