package com.elitecore.corenetvertex.spr;

import com.elitecore.corenetvertex.core.db.exception.DBDownException;
import com.elitecore.corenetvertex.core.transaction.Transaction;
import com.elitecore.corenetvertex.core.transaction.TransactionException;
import com.elitecore.corenetvertex.spr.data.SPRFields;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * SPInterface represents Subscriber profile look up interface.
 * 
 * @author chetan.sankhala
 */
public interface SPInterface {

	public SPRInfo getProfile(String userIdentity) throws OperationFailedException, DBDownException;

	public void addProfile(SPRInfo info) throws OperationFailedException;
	
	public void addProfile(SPRInfo info, Transaction transaction) throws OperationFailedException, TransactionException;
	
	public int updateProfile(String subscriberIdentity, EnumMap<SPRFields, String> updatedProfile) throws OperationFailedException;

	public int changeIMSpackage(String subscriberIdentity, String packageName) throws OperationFailedException;
	public int purgeProfile(String subscriberIdentity) throws OperationFailedException;
	
	public abstract int purgeProfile(String subscriberIdentity, Transaction transaction) throws OperationFailedException, TransactionException;
	
	public int markForDeleteProfile(String subscriberIdentity) throws OperationFailedException;
	
	public int restoreProfile(String subscriberIdentity) throws OperationFailedException;
	
	public Map<String, Integer> restoreProfile(List<String> subscriberIdentities) throws OperationFailedException;
	
	public abstract List<SPRInfo> getDeleteMarkedProfiles() throws OperationFailedException;	
	
	public Transaction createTransaction() throws OperationFailedException;

	int updateProfile(String subscriberIdentity, EnumMap<SPRFields, String> updatedProfile, Transaction transaction) throws OperationFailedException,
			TransactionException;
}