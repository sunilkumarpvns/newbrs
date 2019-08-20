package com.elitecore.corenetvertex.spr.ddf;

import com.elitecore.corenetvertex.spr.SubscriberUsage;
import com.elitecore.corenetvertex.spr.SubscriptionNonMonitoryBalance;
import com.elitecore.corenetvertex.spr.SubscriptionRnCNonMonetaryBalance;
import com.elitecore.corenetvertex.spr.data.Subscription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SubscriptionResult {

    private List<Subscription> subscriptions;
    private Map<String, SubscriptionRnCNonMonetaryBalance> subscriptionToRnCNonMonetaryBalance;
    private Map<String, SubscriptionNonMonitoryBalance> subscriptionToNonMonetaryBalance;
    private Map<String, List<SubscriberUsage>> subscriptionToSubscriberUsage;

    public SubscriptionResult() {
        subscriptions = new ArrayList<>(2);
    }

    public List<Subscription> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(List<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public void addSubscription(Subscription subscription) {
        subscriptions.add(subscription);
    }

    public void addSubscription(Subscription subscription, SubscriptionRnCNonMonetaryBalance subscriptionRnCNonMonetaryBalance) {
        addSubscription(subscription);

        if(Objects.isNull(subscriptionToRnCNonMonetaryBalance)) {
            subscriptionToRnCNonMonetaryBalance = new HashMap<>(2);
        }
        subscriptionToRnCNonMonetaryBalance.put(subscription.getId(), subscriptionRnCNonMonetaryBalance);
    }

    public void addSubscription(Subscription subscription, SubscriptionNonMonitoryBalance subscriptionNonMonitoryBalance) {
        addSubscription(subscription);

        if(Objects.isNull(subscriptionToNonMonetaryBalance)) {
            subscriptionToNonMonetaryBalance = new HashMap<>(2);
        }
        subscriptionToNonMonetaryBalance.put(subscription.getId(), subscriptionNonMonitoryBalance);

    }

    public void addSubscription(Subscription subscription, List<SubscriberUsage> subscriberUsages) {
        addSubscription(subscription);

        if(Objects.isNull(subscriptionToSubscriberUsage)) {
            subscriptionToSubscriberUsage = new HashMap<>(2);
        }
        subscriptionToSubscriberUsage.put(subscription.getId(), subscriberUsages);
    }

    public SubscriptionRnCNonMonetaryBalance getRnCBalance(String id) {
        if(Objects.isNull(subscriptionToRnCNonMonetaryBalance)) {
            return null;
        }
        return subscriptionToRnCNonMonetaryBalance.get(id);
    }

    public SubscriptionNonMonitoryBalance getBalance(String id) {
        if(Objects.isNull(subscriptionToNonMonetaryBalance)) {
            return null;
        }
        return subscriptionToNonMonetaryBalance.get(id);
    }
}
