package com.elitecore.corenetvertex.spr;

import java.util.LinkedHashMap;

import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;

public interface SubscriptionProvider {
	LinkedHashMap<String, Subscription> getSubscriptions(SPRInfo sprInfo) throws OperationFailedException;
    LinkedHashMap<String, Subscription> getSubscriptions(String subscriberId) throws OperationFailedException;
	
	public static class DummySubscriptionProvider implements SubscriptionProvider {

		private static final SubscriptionProvider DUMMY_SUBSCRIPTION_PROVIDER = new DummySubscriptionProvider();

		private DummySubscriptionProvider() {	}

		@Override
		public LinkedHashMap<String, Subscription> getSubscriptions(SPRInfo sprInfo) throws OperationFailedException {
			throw new OperationFailedException("getSubscription operation is not supported for subscriber ID: " + sprInfo.getSubscriberIdentity(), ResultCode.OPERATION_NOT_SUPPORTED);
		}

        @Override
        public LinkedHashMap<String, Subscription> getSubscriptions(String subscriberId) throws OperationFailedException {
            throw new OperationFailedException("getSubscription operation is not supported for subscriber ID: "
                    + subscriberId, ResultCode.OPERATION_NOT_SUPPORTED);
        }

        public static SubscriptionProvider instance() {
			return DUMMY_SUBSCRIPTION_PROVIDER;
		}
	}
}