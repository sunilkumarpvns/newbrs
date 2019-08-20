package com.elitecore.aaa.radius.service.ws.eliteGenericRadiusWS;

import java.rmi.RemoteException;
import java.util.Map;

import com.elitecore.aaa.core.manager.EliteAAAServiceExposerManager;

public class EliteGenericRadiusWSSoapBindingImpl implements EliteGenericRadiusWS{

	@Override
	public Map<String, String[]> requestGenericRadiusWS(java.lang.Integer packetType,java.lang.Integer identifier, Map attrMap)
			throws RemoteException {
		return EliteAAAServiceExposerManager.getInstance().requestGenericRadiusWS(packetType,identifier, attrMap);
	}

}
