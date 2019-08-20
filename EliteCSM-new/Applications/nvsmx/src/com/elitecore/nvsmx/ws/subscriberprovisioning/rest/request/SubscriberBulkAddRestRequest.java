package com.elitecore.nvsmx.ws.subscriberprovisioning.rest.request;

import com.elitecore.nvsmx.ws.subscriberprovisioning.data.SubscriberProfileData;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class SubscriberBulkAddRestRequest {

   private List<SubscriberProfileData> subscriberProfile;

    public List<SubscriberProfileData> getSubscriberProfile() {
        return subscriberProfile;
    }

    public void setSubscriberProfile(List<SubscriberProfileData> subscriberProfile) {
        this.subscriberProfile = subscriberProfile;
    }
}
