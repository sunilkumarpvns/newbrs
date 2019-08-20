package com.elitecore.aaa.diameter.service.ws.EliteGenericDiameterWS;

import java.util.Map;

public interface EliteGenericDiameterWS {
	public Map<String, String> wsDiameterGenericRequest(int commandCode, int applicationId, int hopByHopId, int endToEndId, byte flagByte ,java.util.Map in1) throws java.rmi.RemoteException;
}
