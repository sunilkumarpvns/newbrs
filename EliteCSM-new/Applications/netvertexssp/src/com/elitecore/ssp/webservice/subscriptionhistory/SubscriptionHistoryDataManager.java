package com.elitecore.ssp.webservice.subscriptionhistory;

import java.rmi.RemoteException;
import java.util.List;


import com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.AddOnSubscriptionData;
import com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.AddOnSubscriptionResponse;
import com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.BoDSubscriptionData;
import com.elitecore.ssp.subscriber.SubscriberProfile;
import com.elitecore.ssp.util.exception.DataManagerException;
import com.elitecore.ssp.webservice.WebServiceManager;


public class SubscriptionHistoryDataManager {
	
	private static String MODULE=SubscriptionHistoryDataManager.class.getSimpleName();
	
	public  List<AddOnSubscriptionData> getPromotionSubscriptionHistory(SubscriberProfile subscriberProfileData) throws RemoteException, DataManagerException{
			
			//ParentalPolicyResponse response = getParentalWS().wsListParentalPolicies(currentUser.getSubscriberID(), null, null);
			AddOnSubscriptionResponse response = WebServiceManager.getInstance().getSubscriptionWs_Port().wsListAddOnSubscriptionHistory(subscriberProfileData.getSubscriberID(), null, null);		
			return response.getAddOnSubscription();
			 
	}
		
	public List<BoDSubscriptionData> getBodSubscriptionHistory(SubscriberProfile subscriberProfileData) throws RemoteException, DataManagerException{
		List<BoDSubscriptionData> bodSubscriptionHistoryList=null;
		try{
			bodSubscriptionHistoryList= WebServiceManager.getInstance().getSubscriptionWs_Port().wsListBoDSubscriptionHistory(subscriberProfileData.getSubscriberID(),null,null).getBoDSubscriptionDatas();
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage());
		}
		return bodSubscriptionHistoryList;
	}
}
