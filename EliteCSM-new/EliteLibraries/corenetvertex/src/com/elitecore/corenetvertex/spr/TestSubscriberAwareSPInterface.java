package com.elitecore.corenetvertex.spr;

import java.util.Iterator;
import java.util.List;

import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;

public interface TestSubscriberAwareSPInterface extends SPInterface {

	public abstract void addTestSubscriber(String subscriberIdentity) throws OperationFailedException;

	public abstract int removeTestSubscriber(String subscriberIdentity, SubscriptionProvider subscriptionProvider) throws OperationFailedException;

    /**Performs validation related to the configured package in spr info while adding
     * e.g. Live subscriber should not have Test packages etc...
     * @param sprInfo
     * @throws OperationFailedException
     */
	public abstract void validate(SPRInfo sprInfo) throws OperationFailedException;

	public abstract boolean isTestSubscriber(String subscriberIdentity) throws OperationFailedException;

	public abstract Iterator<String> getTestSubscriberIterator() throws OperationFailedException;

	public abstract int removeTestSubscriber(List<String> subscriberIdentities, SubscriptionProvider subscriptionProvider) throws OperationFailedException;
	
	public abstract void refreshTestSubscriberCache() throws OperationFailedException;

}