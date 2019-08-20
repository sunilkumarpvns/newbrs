package com.elitecore.corenetvertex.spr;

import java.util.LinkedHashMap;
import com.elitecore.commons.base.Maps;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;

public class DummySubscriptionProvider implements SubscriptionProvider {

    private LinkedHashMap<String, Subscription> subscriptions;

    public DummySubscriptionProvider() {
        this.subscriptions = (LinkedHashMap) Maps.newLinkedHashMap();
    }

    @Override
    public LinkedHashMap<String, Subscription> getSubscriptions(SPRInfo sprInfo) throws OperationFailedException {
        return subscriptions;
    }

    @Override
    public LinkedHashMap<String, Subscription> getSubscriptions(String subscriberId) throws OperationFailedException {
        return subscriptions;
    }

    public void setSubscriptions(LinkedHashMap<String, Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }
}
