package com.elitecore.corenetvertex.spr;

import java.util.Collection;
import java.util.Map;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;

public interface UsageProvider {
	Map<String, Map<String, SubscriberUsage>> getCurrentUsage(SPRInfo sprInfo) throws OperationFailedException;
	void insertNew(String subscriberIdentity, Collection<SubscriberUsage> usages) throws OperationFailedException;
	void addToExisting(String subscriberIdentity, Collection<SubscriberUsage> usages) throws OperationFailedException;
	void replace(String subscriberIdentity, Collection<SubscriberUsage> usages) throws OperationFailedException;
	
	
	public static class DummyUsageProvider implements UsageProvider {

		private static final DummyUsageProvider DUMMY_USAGE_PROVIDER = new DummyUsageProvider();
		
		private DummyUsageProvider() {	}

		@Override
		public Map<String, Map<String, SubscriberUsage>> getCurrentUsage(SPRInfo sprInfo) throws OperationFailedException {
			throw new OperationFailedException("getCurrentUsage operation is not supported for subscriber ID: " + sprInfo.getSubscriberIdentity(), ResultCode.OPERATION_NOT_SUPPORTED);
		}

		@Override
		public void insertNew(String subscriberIdentity, Collection<SubscriberUsage> usages) throws OperationFailedException {
			throw new OperationFailedException("insertNew operation is not supported for subscriber ID: " + subscriberIdentity, ResultCode.OPERATION_NOT_SUPPORTED);
		}

		@Override
		public void addToExisting(String subscriberIdentity, Collection<SubscriberUsage> usages) throws OperationFailedException {
			throw new OperationFailedException("addToExisting operation is not supported for subscriber ID: " + subscriberIdentity, ResultCode.OPERATION_NOT_SUPPORTED);
		}

		@Override
		public void replace(String subscriberIdentity, Collection<SubscriberUsage> usages) throws OperationFailedException {
			throw new OperationFailedException("replace operation is not supported for subscriber ID: " + subscriberIdentity, ResultCode.OPERATION_NOT_SUPPORTED);
		}

		public static DummyUsageProvider instance() {
			return DUMMY_USAGE_PROVIDER;
		}
	}
	
}
