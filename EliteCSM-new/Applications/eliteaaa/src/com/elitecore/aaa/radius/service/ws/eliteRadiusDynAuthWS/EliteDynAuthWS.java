package com.elitecore.aaa.radius.service.ws.eliteRadiusDynAuthWS;

import java.util.Map;

public interface EliteDynAuthWS extends java.rmi.Remote {
    public int requestCOA(java.lang.String in0, java.util.Map in1) throws java.rmi.RemoteException;
    public int requestDisconnect(java.lang.String in0, java.util.Map in1) throws java.rmi.RemoteException;
   // public int requestHotline(java.lang.String in0, java.util.Map in1) throws java.rmi.RemoteException;
   // public int wimaxDynAuthRequest(int in0, java.lang.String in1, java.util.Map in2) throws java.rmi.RemoteException;
	public Map<String, String[]> requestCOAExt(String strUserName,Map<String, String[]> attrMap)  throws java.rmi.RemoteException;
	public Map<String, String[]> requestDisconnectExt(String strUserName, Map<String, String[]> attrMap) throws java.rmi.RemoteException;
    
}
