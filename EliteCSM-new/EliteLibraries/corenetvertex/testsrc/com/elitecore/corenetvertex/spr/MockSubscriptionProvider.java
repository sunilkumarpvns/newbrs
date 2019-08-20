package com.elitecore.corenetvertex.spr;

import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;

import java.util.LinkedHashMap;

public class MockSubscriptionProvider implements SubscriptionProvider {
    private LinkedHashMap<String, Subscription> subscriptions;

    public MockSubscriptionProvider() {
        this.subscriptions = new LinkedHashMap<>();
    }

    @Override
    public LinkedHashMap<String, Subscription> getSubscriptions(SPRInfo sprInfo) throws OperationFailedException {
        return subscriptions;
    }

    @Override
    public LinkedHashMap<String, Subscription> getSubscriptions(String subscriberId) throws OperationFailedException {
        return subscriptions;
    }

    public void addSubscription(Subscription subscription) {
        this.subscriptions.put(subscription.getId(), subscription);
    }
}
