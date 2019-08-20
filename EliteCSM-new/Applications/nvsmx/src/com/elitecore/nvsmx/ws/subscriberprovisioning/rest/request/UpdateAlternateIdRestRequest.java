package com.elitecore.nvsmx.ws.subscriberprovisioning.rest.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UpdateAlternateIdRestRequest {
    private String subscriberId;
    private String currentAlternateId;
    private String newAlternateId;


    @XmlElement(required = true)
    @JsonProperty(required = true)
    public String getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(String subscriberId) {
        this.subscriberId = subscriberId;
    }

    @XmlElement(required = true)
    @JsonProperty(required = true)
    public String getCurrentAlternateId() {
        return currentAlternateId;
    }

    public void setCurrentAlternateId(String currentAlternateId) {
        this.currentAlternateId = currentAlternateId;
    }

    @XmlElement(required = true)
    @JsonProperty(required = true)
    public String getNewAlternateId() {
        return newAlternateId;
    }

    public void setNewAlternateId(String newAlternateId) {
        this.newAlternateId = newAlternateId;
    }
}
