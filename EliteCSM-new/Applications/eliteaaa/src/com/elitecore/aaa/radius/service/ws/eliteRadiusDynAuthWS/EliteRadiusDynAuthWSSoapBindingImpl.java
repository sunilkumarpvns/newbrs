package com.elitecore.aaa.radius.service.ws.eliteRadiusDynAuthWS;

import java.rmi.RemoteException;
import java.util.Map;

import com.elitecore.aaa.core.manager.EliteAAAServiceExposerManager;

public class EliteRadiusDynAuthWSSoapBindingImpl implements EliteDynAuthWS{
	public int requestCOA(String userName, Map attrMap) throws java.rmi.RemoteException {
		return EliteAAAServiceExposerManager.getInstance().requestCOA(userName, attrMap);
	}

    public int requestDisconnect(String userName, Map attrMap) throws java.rmi.RemoteException {
		return EliteAAAServiceExposerManager.getInstance().requestDisconnect(userName, attrMap);
	}
/*
	@Override
	public int requestHotline(String in0, Map in1)
			throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int wimaxDynAuthRequest(int in0, String in1, Map in2)
			throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}
	*/

	@Override
	public Map<String, String[]> requestCOAExt(String strUserName,Map<String, String[]> attrMap) throws RemoteException {
		return EliteAAAServiceExposerManager.getInstance().requestCOAExt(strUserName, attrMap);
	}

	@Override
	public Map<String, String[]> requestDisconnectExt(String strUserName, Map<String, String[]> attrMap) throws RemoteException {
		return EliteAAAServiceExposerManager.getInstance().requestDisconnectExt(strUserName, attrMap);
	}

}

