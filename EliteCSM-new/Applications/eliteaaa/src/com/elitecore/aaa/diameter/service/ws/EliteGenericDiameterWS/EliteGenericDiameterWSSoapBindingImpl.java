package com.elitecore.aaa.diameter.service.ws.EliteGenericDiameterWS;

import java.rmi.RemoteException;
import java.util.Map;

import com.elitecore.aaa.core.manager.EliteAAAServiceExposerManager;



public class EliteGenericDiameterWSSoapBindingImpl  implements EliteGenericDiameterWS {

	@Override
	public Map<String, String> wsDiameterGenericRequest(int commandCode, int applicationId, int hopByHopId, int endToEndId, byte flagByte, Map in1) throws RemoteException {
		return EliteAAAServiceExposerManager.getInstance().requestGenericDiameterRequest(commandCode,applicationId,hopByHopId, endToEndId, flagByte,in1);
	}

	
}
