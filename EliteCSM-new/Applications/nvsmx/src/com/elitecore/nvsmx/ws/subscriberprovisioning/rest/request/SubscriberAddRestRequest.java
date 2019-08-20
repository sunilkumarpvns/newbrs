package com.elitecore.nvsmx.ws.subscriberprovisioning.rest.request;

import com.elitecore.nvsmx.ws.subscriberprovisioning.data.SubscriberProfileData;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SubscriberAddRestRequest {
    private SubscriberProfileData subscriberProfileData;


    @XmlElement(name="subscriberProfile")
    @JsonProperty("subscriberProfile")
    public SubscriberProfileData getSubscriberProfileData() {
        return subscriberProfileData;
    }

    public void setSubscriberProfileData(SubscriberProfileData subscriberProfileData) {
        this.subscriberProfileData = subscriberProfileData;
    }

}
