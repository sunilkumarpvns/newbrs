package com.elitecore.aaa.radius.service.ws.eliteGenericRadiusWS;

import java.util.Map;

public interface EliteGenericRadiusWS extends java.rmi.Remote {
	public Map<String, String[]> requestGenericRadiusWS(java.lang.Integer packetType,java.lang.Integer identifier, java.util.Map attrMap) throws java.rmi.RemoteException;
}
