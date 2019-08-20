/**
 * EliteGenericRadiusWS_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.elitecore.elitesm.ws.ytl.cal.aaa.dm;

public interface EliteGenericRadiusWS_PortType extends java.rmi.Remote {
    public java.util.HashMap requestGenericRadiusWS(java.lang.Integer packetType, java.lang.Integer identifier, java.util.HashMap attrMap) throws java.rmi.RemoteException;
    public java.lang.Object[] getOperationDescs() throws java.rmi.RemoteException;
    public java.lang.Object[] getOperationDescByName(java.lang.String methodName) throws java.rmi.RemoteException;
}
