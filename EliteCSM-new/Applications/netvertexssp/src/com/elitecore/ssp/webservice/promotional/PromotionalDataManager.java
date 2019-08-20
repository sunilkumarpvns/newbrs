package com.elitecore.ssp.webservice.promotional;

import java.rmi.RemoteException;
import java.util.List;




import com.elitecore.commons.base.Numbers;
import com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.AddOnApprovalReqResponse;
import com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.AddOnPackage;
import com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.AddOnSubRequestData;
import com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.AddOnSubscriptionData;
import com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.AddOnSubscriptionResponse;
import com.elitecore.ssp.subscriber.SubscriberProfile;
import com.elitecore.ssp.util.constants.ApprovalStates;
import com.elitecore.ssp.util.constants.SubscriptionState;
import com.elitecore.ssp.util.exception.DataManagerException;
import com.elitecore.ssp.util.logger.Logger;
import com.elitecore.ssp.webservice.WebServiceManager;

public class PromotionalDataManager {

	private static String MODULE=PromotionalDataManager.class.getSimpleName();
	
	public List<AddOnSubscriptionData> getActivatedPromotions(SubscriberProfile subscriberProfile) throws RemoteException,DataManagerException {
		List<AddOnSubscriptionData> tempAddOnList=null;
		try{
		tempAddOnList=WebServiceManager.getInstance().getSubscriptionWs_Port().wsListAddOnSubscriptions(subscriberProfile.getSubscriberID(),SubscriptionState.STARTED.getStringVal(), null,null, null).getAddOnSubscription();
		/*if (tempAddOnList!=null && !tempAddOnList.isEmpty()) {
			addOnSubscriptions=new AddOnSubscriptionData[tempAddOnList.size()];
			tempAddOnList.toArray(addOnSubscriptions);
		}*/
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage());
		}
		return tempAddOnList;
	}
	
	public List<AddOnPackage> getPromotions(SubscriberProfile subscriberProfileData) throws RemoteException,DataManagerException {
		List<AddOnPackage> addOnList=null;
		try{
			if(subscriberProfileData!=null){
			 addOnList=WebServiceManager.getInstance().getSubscriptionWs_Port().wsListAddOnPackages(subscriberProfileData.getSubscriberID(), null, null, null).getAddOnPackage();
			}else
			{
				addOnList=WebServiceManager.getInstance().getSubscriptionWs_Port().wsListAddOnPackages(null, null, null, null).getAddOnPackage();
		  }
/*			 if(tempAddOnList!=null && !tempAddOnList.isEmpty()){
				 addOnPackages=new AddOnPackage[tempAddOnList.size()];
				 tempAddOnList.toArray(addOnPackages);
			 }
*/		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage());
		}
		return addOnList;
	}

		// to do pass parent and child object in argument
	public void subscribePromotion(AddOnPackage addOnPackage,SubscriberProfile subscriberProfileData, Long endDate) throws RemoteException,DataManagerException {
		try{			
			String endDateString=null;
			if(endDate!=null){  
				endDateString=Long.toString(endDate);
		     }
			Logger.logDebug(MODULE, addOnPackage);
			WebServiceManager.getInstance().getSubscriptionWs_Port().
			                               wsSubscribeAddOnByID(subscriberProfileData.getParentID(), subscriberProfileData.getSubscriberID(),
			                               null, addOnPackage.getAddOnPackageID(),SubscriptionState.SUBSCRIBED.getStringVal() ,null, null,endDateString,null,null);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage());
		}
		
	}
	
	public void unsubscribePromotion(Long addOnSubscriptionId,SubscriberProfile subscriberProfile) throws RemoteException,DataManagerException {
		try{
			Logger.logDebug(MODULE, "Calling ssp web service to UnSubscribe promotion: " + addOnSubscriptionId);
			WebServiceManager.getInstance().getSubscriptionWs_Port().wsChangeAddOnSubscription(addOnSubscriptionId,SubscriptionState.UNSUBSCRIBED.getStringVal(), null, null, null,null, null,null);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage());
		}
		
	}
	
	public void submitPromotionRequest(AddOnPackage addOnPackage,SubscriberProfile childObj,Long endDate) throws RemoteException,DataManagerException {
		try{
			Logger.logDebug(MODULE, "Calling ssp web service to submit child request for promotional offer: " + addOnPackage);
			//WebServiceManager.getInstance().getSubscriptionWs_Port().wsSubscribeAddOnByID(childObj.getParentID(), childObj.getSubscriberID(), null, addOnPackage.getAddOnPackageID(),SubscriptionState.APPROVAL_PENDING.getStringVal() ,null, null,String.valueOf(endDate), null,null);
			WebServiceManager.getInstance().getSubscriptionWs_Port().
			                               wsSubmitAddOnSubscriptionRequest(childObj.getSubscriberID(),
			                               childObj.getParentID(),addOnPackage.getAddOnPackageID() , ApprovalStates.APPROVAL_PENDING.getStringVal(), null, null, null, null);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage());
		}
	}
	
	public List<AddOnSubRequestData> getPendingPromotionalReq(SubscriberProfile childObj,SubscriberProfile parentObj) throws RemoteException,DataManagerException {
		List<AddOnSubRequestData> addOnSubscriptionDataList = null;
		try{
			Logger.logDebug(MODULE, "Calling ssp web service to get pending child request : SubscriberID : " + childObj.getSubscriberID());
		//	addOnSubscriptionDataList= WebServiceManager.getInstance().getSubscriptionWs_Port().wsListAddOnSubscriptions(childObj.getSubscriberID(),SubscriptionState.APPROVAL_PENDING.getStringVal(),null, null, null).getAddOnSubscription();
			AddOnApprovalReqResponse addOnApprovalReqResponse= WebServiceManager.getInstance().getSubscriptionWs_Port().
			                                                  wsGetAddOnSubscriptionRequest(childObj.getSubscriberID(), null, null, null, null, null);
		    addOnSubscriptionDataList=addOnApprovalReqResponse.getAddOnSubRequestDataList(); 	
/*			if (addOnSubscriptionDataList != null
					&& !addOnSubscriptionDataList.isEmpty()) {
				pendingAddOnData=new AddOnSubscriptionData[addOnSubscriptionDataList.size()];
				addOnSubscriptionDataList.toArray(pendingAddOnData);
			}
*/		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage());
		}
		return addOnSubscriptionDataList;
	}
	
	//to do need to update
	public void rejectAddOnSubReq(String subscriberID,Long addOnSubReqID,String rejectReason) throws RemoteException,DataManagerException {
		try{
			Logger.logDebug(MODULE, "Calling web service to Reject AddOn Subscription Request");
			//changeAddOnSubscriptionStatus(addOnSubscriptionID, SubscriptionState.REJECTED, rejectReason);
			
			WebServiceManager.getInstance().getSubscriptionWs_Port().
			                 wsSubmitAddOnSubscriptionRequest(subscriberID, null, null, ApprovalStates.REJECTED.getStringVal(), addOnSubReqID, rejectReason, null, null);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage());
		}
	}
	
	public AddOnApprovalReqResponse approveAddOnSubReq(String subscriberID,Long addOnSubReqID, Long addOnPackageID) throws RemoteException,DataManagerException {
		try{
			Logger.logDebug(MODULE, "Calling  web service to Approve AddOn Subscription Request");
			//changeAddOnSubscriptionStatus(addOnSubscriptionID, SubscriptionState.SUBSCRIBED, null);
			return  WebServiceManager.getInstance().getSubscriptionWs_Port().
			                wsSubmitAddOnSubscriptionRequest(subscriberID, null, addOnPackageID, ApprovalStates.APPROVED.getStringVal(), addOnSubReqID, null, null, null);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage());
		}
	}
	
	
	public List<AddOnSubscriptionData> getSubscription(SubscriberProfile childObj,SubscriberProfile parentObj) throws RemoteException,DataManagerException {
		List<AddOnSubscriptionData> addOnSubscriptionDataList = null;
		try{
			Logger.logDebug(MODULE, "Calling ssp web service to getSubscription : for SubscriberID : " + childObj.getSubscriberID());
		//	addOnSubscriptionDataList= WebServiceManager.getInstance().getSubscriptionWs_Port().wsListAddOnSubscriptions(childObj.getSubscriberID(),SubscriptionState.APPROVAL_PENDING.getStringVal(),null, null, null).getAddOnSubscription();
			AddOnSubscriptionResponse addonSubscriptionResponse= WebServiceManager.getInstance().getSubscriptionWs_Port().
			                                                    wsListAddOnSubscriptions(childObj.getSubscriberID(), null, null, null, null);
		    addOnSubscriptionDataList=addonSubscriptionResponse.getAddOnSubscription(); 	
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage());
		}
		return addOnSubscriptionDataList;
	}
	private void changeAddOnSubscriptionStatus(Long addOnSubscriptionID,SubscriptionState subcriptionState,String rejectReason) throws RemoteException,DataManagerException{
		try{
			Logger.logDebug(MODULE, "Calling changeAddOnSubscriptionStatus Method");
			WebServiceManager.getInstance().getSubscriptionWs_Port().wsChangeAddOnSubscription(addOnSubscriptionID, subcriptionState.getStringVal(), null,null,null,rejectReason, null, null);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage());
		}		
		
	}
}