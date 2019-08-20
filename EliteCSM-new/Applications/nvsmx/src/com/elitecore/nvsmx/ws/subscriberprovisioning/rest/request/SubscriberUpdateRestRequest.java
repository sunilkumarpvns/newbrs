package com.elitecore.nvsmx.ws.subscriberprovisioning.rest.request;

import com.elitecore.nvsmx.ws.subscriberprovisioning.data.SubscriberProfile;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SubscriberUpdateRestRequest{

    private SubscriberProfile subscriberProfile;
    private String subscriberId;
    private String alternateId;
    private String updateAction;


    public SubscriberProfile getSubscriberProfile() {
        return subscriberProfile;
    }

    public void setSubscriberProfile(SubscriberProfile subscriberProfile) {
        this.subscriberProfile = subscriberProfile;
    }

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

    public String getUpdateAction() {
        return updateAction;
    }

    public void setUpdateAction(String updateAction) {
        this.updateAction = updateAction;
    }
}
