/**
 * UserMgmtIfc.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.redback.npm;


public interface UserMgmtIfc extends java.rmi.Remote {
    public void addSubscriberAccount(com.redback.npm.SubscriberAccount subscriberAccount_1) throws java.rmi.RemoteException, com.redback.npm.InvalidSubscriberAccountException, com.redback.npm.ResourceRequestDeniedException, com.redback.npm.DuplicateServiceSubscriptionException, com.redback.npm.DuplicateSubscriberAccountException, com.redback.npm.UnauthorizedException, com.redback.npm.InvalidServiceException, com.redback.npm.SQLException, com.redback.npm.InvalidLicenseException, com.redback.npm.NoAccessServiceException, com.redback.npm.InvalidNASException, com.redback.npm.InvalidServiceSubscriptionException, com.redback.npm.IncompatibleServiceException;
    public void removeSubscriberAccount(java.lang.String string_1) throws java.rmi.RemoteException, com.redback.npm.InvalidSubscriberAccountException, com.redback.npm.ActiveSubSessionException, com.redback.npm.SQLException;
    public void updateSubscriberAccount(com.redback.npm.SubscriberAccount subscriberAccount_1) throws java.rmi.RemoteException, com.redback.npm.InvalidSubscriberAccountException, com.redback.npm.ResourceRequestDeniedException, com.redback.npm.DuplicateServiceSubscriptionException, com.redback.npm.SessionLimitException, com.redback.npm.UnauthorizedException, com.redback.npm.InvalidServiceException, com.redback.npm.SQLException, com.redback.npm.InvalidLicenseException, com.redback.npm.NoAccessServiceException, com.redback.npm.InvalidNASException, com.redback.npm.InvalidServiceSubscriptionException, com.redback.npm.IncompatibleServiceException;
}
