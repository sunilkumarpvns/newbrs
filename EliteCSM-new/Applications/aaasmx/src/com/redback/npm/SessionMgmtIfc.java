/**
 * SessionMgmtIfc.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.redback.npm;

public interface SessionMgmtIfc extends java.rmi.Remote {
    public com.redback.npm.SubscriberSession[] getSubscriberSessions(java.lang.String string_1) throws java.rmi.RemoteException, com.redback.npm.InvalidSubscriberAccountException, com.redback.npm.SQLException;
    public void setStaticIpAddresses(java.lang.String string_1, com.redback.npm.SessionIP[] arrayOfSessionIP_2) throws java.rmi.RemoteException, com.redback.npm.InvalidSubscriberAccountException, com.redback.npm.SQLException;
}
