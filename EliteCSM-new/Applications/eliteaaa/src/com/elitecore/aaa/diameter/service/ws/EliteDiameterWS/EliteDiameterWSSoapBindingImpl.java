package com.elitecore.aaa.diameter.service.ws.EliteDiameterWS;

import java.rmi.RemoteException;
import java.util.Map;

import com.elitecore.aaa.core.manager.EliteAAAServiceExposerManager;

public class EliteDiameterWSSoapBindingImpl implements EliteDiameterWS {

	@Override
	public int wsDiameterReAuthRequest(Map in1)
			throws RemoteException {
		return EliteAAAServiceExposerManager.getInstance().requestDiameterReAuth(in1);
	}

	@Override
	public int wsDiameterAbortSessionRequest(Map in1) throws RemoteException {
		return EliteAAAServiceExposerManager.getInstance().requestDiameterAbortSession(in1);
	}

}
