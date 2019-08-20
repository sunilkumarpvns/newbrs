package com.elitecore.ssp.webservice.login;



import java.rmi.RemoteException;
import java.util.List;

import com.elitecore.netvertexsm.ws.cxfws.subscriberprovisioning.SubscriberProfile;
import com.elitecore.ssp.util.exception.DataManagerException;
import com.elitecore.ssp.util.logger.Logger;
import com.elitecore.ssp.webservice.WebServiceManager;


public class LoginDataManager {
	
	private static String MODULE = LoginDataManager.class.getSimpleName();
	
	
	public List<SubscriberProfile> authenticate(String userName, String password) throws RemoteException,DataManagerException{
		Logger.logDebug(MODULE, "Calling Authentication WebService for User: "+userName);
		
		//return WebServiceManager.getInstance().getParentalWs_Port().wsAuthenticate(userName, password, null,null).getSubscriberProfile();

		return WebServiceManager.getInstance().getSubscriberProvisioningWS().wsGetSubscriberProfileByID(userName, null, null).getSubscriberProfile();
	}
	
}
