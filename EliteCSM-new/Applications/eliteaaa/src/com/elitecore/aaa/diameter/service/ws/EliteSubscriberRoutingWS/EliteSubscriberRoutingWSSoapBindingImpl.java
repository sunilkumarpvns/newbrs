package com.elitecore.aaa.diameter.service.ws.EliteSubscriberRoutingWS;

import java.rmi.RemoteException;
import java.util.Map;

import com.elitecore.aaa.core.manager.EliteAAAServiceExposerManager;


public class EliteSubscriberRoutingWSSoapBindingImpl  implements EliteSubcriberRoutingWS {

	@Override
	public Map<String, String> getPeerByIMSI(String imsi, String imsiTableName)
			throws RemoteException {
		return EliteAAAServiceExposerManager.getInstance().getPeerByIMSI(imsi, imsiTableName);
	}

	@Override
	public Map<String, String> getPeerByMSISDN(String msisdn,
			String msisdnTableName) throws RemoteException {
		return EliteAAAServiceExposerManager.getInstance().getPeerByMSISDN(msisdn, msisdnTableName);
	}


	
}
