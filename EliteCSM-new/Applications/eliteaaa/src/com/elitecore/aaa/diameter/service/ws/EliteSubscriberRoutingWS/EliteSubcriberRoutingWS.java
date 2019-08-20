package com.elitecore.aaa.diameter.service.ws.EliteSubscriberRoutingWS;

import java.util.Map;

public interface EliteSubcriberRoutingWS {
	public Map<String, String> getPeerByIMSI(String imsi, String imsiTableName) throws java.rmi.RemoteException;
	public Map<String, String> getPeerByMSISDN(String msisdn, String msisdnTableName) throws java.rmi.RemoteException;
}
