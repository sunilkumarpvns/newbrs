package com.elitecore.nvsmx.ws.subscriberprovisioning.rest.request;

import java.util.List;

public class PurgeSubscribersRestRequest {

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
