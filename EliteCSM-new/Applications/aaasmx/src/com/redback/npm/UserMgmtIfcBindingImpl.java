/**
 * UserMgmtIfcBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.redback.npm;

import java.util.HashMap;
import java.util.Map;

import com.elitecore.elitesm.ws.SubscriberProfileWS;
import com.elitecore.elitesm.ws.logger.Logger;

public class UserMgmtIfcBindingImpl implements com.redback.npm.UserMgmtIfc{
	private static final String MODULE = "User-Mgmt-Ifc-Binding-Impl";
	private SubscriberProfileWS subscriberProfileWS = new SubscriberProfileWS();
	private Map<String,Object> subscriberAccountMap = new HashMap<String, Object>();

	public void addSubscriberAccount(com.redback.npm.SubscriberAccount subscriberAccount_1) throws java.rmi.RemoteException, com.redback.npm.InvalidSubscriberAccountException, com.redback.npm.ResourceRequestDeniedException, com.redback.npm.DuplicateServiceSubscriptionException, com.redback.npm.DuplicateSubscriberAccountException, com.redback.npm.UnauthorizedException, com.redback.npm.InvalidServiceException, com.redback.npm.SQLException, com.redback.npm.InvalidLicenseException, com.redback.npm.NoAccessServiceException, com.redback.npm.InvalidNASException, com.redback.npm.InvalidServiceSubscriptionException, com.redback.npm.IncompatibleServiceException {
		Logger.logInfo(MODULE, "Enter into addSubscriberAccount Method :");
		setAccountSubscriberFieldMap(subscriberAccountMap, subscriberAccount_1);
		subscriberProfileWS.addSubscriber(subscriberAccountMap);
	}

	public void removeSubscriberAccount(java.lang.String string_1) throws java.rmi.RemoteException, com.redback.npm.InvalidSubscriberAccountException, com.redback.npm.ActiveSubSessionException, com.redback.npm.SQLException {
		Logger.logInfo(MODULE, "Enter into addSubscriberAccount Method :");
		int noOfDelete = subscriberProfileWS.delSubscriber(string_1.trim());
		if(noOfDelete == 0){
			throw new InvalidSubscriberAccountException("Unknown Subscriber:"+string_1.trim());
		}
	}

	public void updateSubscriberAccount(com.redback.npm.SubscriberAccount subscriberAccount_1) throws java.rmi.RemoteException, com.redback.npm.InvalidSubscriberAccountException, com.redback.npm.ResourceRequestDeniedException, com.redback.npm.DuplicateServiceSubscriptionException, com.redback.npm.SessionLimitException, com.redback.npm.UnauthorizedException, com.redback.npm.InvalidServiceException, com.redback.npm.SQLException, com.redback.npm.InvalidLicenseException, com.redback.npm.NoAccessServiceException, com.redback.npm.InvalidNASException, com.redback.npm.InvalidServiceSubscriptionException, com.redback.npm.IncompatibleServiceException {
		Logger.logInfo(MODULE, "Enter into addSubscriberAccount Method :");
		setAccountSubscriberFieldMap(subscriberAccountMap, subscriberAccount_1);
	 	int noOfUpdates = subscriberProfileWS.updateSubscriber(subscriberAccountMap,subscriberAccount_1.getName().trim());
	 	if(noOfUpdates == 0){
	 		throw new InvalidSubscriberAccountException("Unknown Subscriber:"+subscriberAccount_1.getName().trim());
	 	}
	}

	private void setAccountSubscriberFieldMap(Map<String,Object> subscriberAcctMap,com.redback.npm.SubscriberAccount subscriberAccount){		
		if(String.valueOf(subscriberAccount.isActivated()) != null && !(String.valueOf(subscriberAccount.isActivated()).equalsIgnoreCase(""))){
			subscriberAcctMap.put("activated",String.valueOf(subscriberAccount.isActivated()));
		}
		
		if(subscriberAccount.getName() != null && !subscriberAccount.getName().equalsIgnoreCase("")){
			subscriberAcctMap.put("name", subscriberAccount.getName().trim());
		}

		if(subscriberAccount.getPassword() != null && !subscriberAccount.getPassword().equalsIgnoreCase("")){
			subscriberAcctMap.put("password", subscriberAccount.getPassword().trim());
		}

		if(subscriberAccount.getSubscriptionIds() != null && !subscriberAccount.getSubscriptionIds().isEmpty() && subscriberAccount.getSubscriptionIds().get(0) != null){
			
			subscriberAcctMap.put("subscriptionIds", String.valueOf(subscriberAccount.getSubscriptionIds().get(0)).trim());
		}		
	}
}