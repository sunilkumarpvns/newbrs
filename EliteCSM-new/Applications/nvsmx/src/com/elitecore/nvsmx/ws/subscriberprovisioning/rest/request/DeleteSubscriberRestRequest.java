package com.elitecore.nvsmx.ws.subscriberprovisioning.rest.request;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class DeleteSubscriberRestRequest  {
    private List<String> subscriberIds;
    private List<String> alternateIds;

    public List<String> getSubscriberIds() {
        return subscriberIds;
    }

    public void setSubscriberIds(List<String> subscriberIds) {
        this.subscriberIds = subscriberIds;
    }

    public List<String> getAlternateIds() {
        return alternateIds;
    }

    public void setAlternateIds(List<String> alternateIds) {
        this.alternateIds = alternateIds;
    }
}
