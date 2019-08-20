/**
 * EliteDynAuthWS.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.elitecore.aaa.radius.service.ws.eliteRadiusDynAuthWS;



public interface EliteDynAuthWS extends java.rmi.Remote {
    public int requestCOA(java.lang.String in0, java.util.HashMap<String,String> in1) throws java.rmi.RemoteException;
    public int requestDisconnect(java.lang.String in0, java.util.HashMap<String,String>  in1) throws java.rmi.RemoteException;
    public java.lang.Object[] getOperationDescs() throws java.rmi.RemoteException;
    public java.lang.Object[] getOperationDescByName(java.lang.String methodName) throws java.rmi.RemoteException;
}
