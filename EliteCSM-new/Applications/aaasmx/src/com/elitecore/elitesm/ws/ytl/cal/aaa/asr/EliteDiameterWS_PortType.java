/**
 * EliteDiameterWS_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.elitecore.elitesm.ws.ytl.cal.aaa.asr;

public interface EliteDiameterWS_PortType extends java.rmi.Remote {
    public java.lang.Object[] getOperationDescs() throws java.rmi.RemoteException;
    public java.lang.Object[] getOperationDescByName(java.lang.String methodName) throws java.rmi.RemoteException;
    public int wsDiameterReAuthRequest(java.util.HashMap in1) throws java.rmi.RemoteException;
    public int wsDiameterAbortSessionRequest(java.util.HashMap in1) throws java.rmi.RemoteException;
}
