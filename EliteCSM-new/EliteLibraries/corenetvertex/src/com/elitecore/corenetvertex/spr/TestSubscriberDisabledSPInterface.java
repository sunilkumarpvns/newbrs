package com.elitecore.corenetvertex.spr;

import com.elitecore.corenetvertex.core.db.exception.DBDownException;
import com.elitecore.corenetvertex.core.transaction.Transaction;
import com.elitecore.corenetvertex.core.transaction.TransactionException;
import com.elitecore.corenetvertex.spr.data.SPRFields;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TestSubscriberDisabledSPInterface implements TestSubscriberAwareSPInterface {
	
	private SPInterface spInterface;
	
	public TestSubscriberDisabledSPInterface(SPInterface spInterface) {
		this.spInterface = spInterface;
	}

	@Override
	public SPRInfo getProfile(String userIdentity) throws OperationFailedException, DBDownException {
		return spInterface.getProfile(userIdentity);
	}

	@Override
	public void addProfile(SPRInfo sprInfo) throws OperationFailedException {
		spInterface.addProfile(sprInfo);
	}

	@Override
	public int purgeProfile(String subscriberIdentity) throws OperationFailedException {
		return spInterface.purgeProfile(subscriberIdentity);
	}

	@Override
	public boolean isTestSubscriber(String subscriberIdentity) throws OperationFailedException {
		return false;
	}

	@Override
	public void addTestSubscriber(String subscriberIdentity) throws OperationFailedException {
		throw new OperationFailedException("Test subscriber functionality not supported", ResultCode.OPERATION_NOT_SUPPORTED);
	}

	@Override
	public int removeTestSubscriber(String subscriberIdentity, SubscriptionProvider subscriptionProvider) throws OperationFailedException {
		throw new OperationFailedException("Test subscriber functionality not supported", ResultCode.OPERATION_NOT_SUPPORTED);
	}

	@Override
	public void validate(SPRInfo sprInfo) throws OperationFailedException {
         //No Need to implement this method as this Test Disabled Sp Interface Doesn't support operation related to test subscriber
	}

	@Override
	public Iterator<String> getTestSubscriberIterator() throws OperationFailedException {
		throw new OperationFailedException("Test subscriber functionality not supported", ResultCode.OPERATION_NOT_SUPPORTED);
	}

	@Override
	public int markForDeleteProfile(String subscriberIdentity) throws OperationFailedException {
		return spInterface.markForDeleteProfile(subscriberIdentity);
	}

	@Override
	public int removeTestSubscriber(List<String> subscriberIdentities, SubscriptionProvider subscriptionProvider) throws OperationFailedException {
		throw new OperationFailedException("Test subscriber functionality not supported", ResultCode.OPERATION_NOT_SUPPORTED);
	}

	@Override
	public List<SPRInfo> getDeleteMarkedProfiles() throws OperationFailedException {
		return spInterface.getDeleteMarkedProfiles();
	}

	@Override
	public int purgeProfile(String subscriberIdentity, Transaction transaction) throws OperationFailedException, TransactionException {
		return spInterface.purgeProfile(subscriberIdentity, transaction);
	}

	@Override
	public void refreshTestSubscriberCache() throws OperationFailedException {
		throw new OperationFailedException("Test subscriber functionality not supported", ResultCode.OPERATION_NOT_SUPPORTED);
	}

	@Override
	public int restoreProfile(String subscriberIdentity) throws OperationFailedException {
		return spInterface.restoreProfile(subscriberIdentity);
	}

	@Override
	public Map<String, Integer> restoreProfile(List<String> subscriberIdentities) throws OperationFailedException {
		return spInterface.restoreProfile(subscriberIdentities);
	}

	@Override
	public int updateProfile(String subscriberIdentity, EnumMap<SPRFields, String> updatedProfile) throws OperationFailedException {
		return spInterface.updateProfile(subscriberIdentity, updatedProfile);
	}

	@Override
	public int changeIMSpackage(String subscriberIdentity, String packageName) throws OperationFailedException {
		return spInterface.changeIMSpackage(subscriberIdentity, packageName);
	}

	@Override
	public Transaction createTransaction() throws OperationFailedException {
		return spInterface.createTransaction();
	}

	@Override
	public void addProfile(SPRInfo info, Transaction transaction) throws OperationFailedException, TransactionException {
		spInterface.addProfile(info, transaction);
	}

	@Override
	public int updateProfile(String subscriberIdentity, EnumMap<SPRFields, String> updatedProfile, Transaction transaction) throws OperationFailedException, TransactionException {
		return spInterface.updateProfile(subscriberIdentity, updatedProfile, transaction);
	}
}
