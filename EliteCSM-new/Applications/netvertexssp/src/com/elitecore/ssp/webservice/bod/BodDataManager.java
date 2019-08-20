package com.elitecore.ssp.webservice.bod;

import java.rmi.RemoteException;
import java.util.List;


import com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.BoDPackage;
import com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.BoDSubscriptionData;
import com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.BoDSubscriptionHistoryResponse;
import com.elitecore.ssp.subscriber.SubscriberProfile;
import com.elitecore.ssp.util.EliteUtility;
import com.elitecore.ssp.util.constants.SubscriptionState;
import com.elitecore.ssp.util.exception.DataManagerException;
import com.elitecore.ssp.util.logger.Logger;
import com.elitecore.ssp.webservice.WebServiceManager;

public class BodDataManager {
	
	
	private static String MODULE = BodDataManager.class.getSimpleName();
	
	public List<BoDSubscriptionData> getActiveBodService(SubscriberProfile subscriberProfileData) throws RemoteException,DataManagerException
	{
		List<BoDSubscriptionData> activeBoDList=null;
		try{
			                        
			 activeBoDList=WebServiceManager.getInstance().getSubscriptionWs_Port().wsListBoDSubscriptions(subscriberProfileData.getSubscriberID(), SubscriptionState.STARTED.getStringVal(), null, null).getBoDSubscriptionDatas();
			/*if(tempBoDList!=null && !tempBoDList.isEmpty())
				bodSubscriptionDatas=new BoDSubscriptionData[tempBoDList.size()];
			tempBoDList.toArray(bodSubscriptionDatas);*/
			
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage());
		}
		return activeBoDList;
	}
	
	public void subscribeBodService(String parentID, String subscriberID, BoDPackage bodServiceData,String startDateTime, Long validityPeriod, String  validityPeriodUnit) throws RemoteException,Exception{
		try{			
				WebServiceManager.getInstance().getSubscriptionWs_Port().wsSubscribeBoDByID(parentID, subscriberID, bodServiceData.getBodPackageID(), SubscriptionState.SUBSCRIBED.getStringVal(), EliteUtility.convertDateToMilliSeconds(startDateTime, "dd-MM-yyyy HH:mm:ss"), validityPeriod, validityPeriodUnit, null, null);//getSspService().wsSubscribeBoDService(subscriberProfileData, bodServiceData);
		}catch(Exception exp){
			throw new Exception(exp.getMessage());
		}
	}
	
	public void approveBodService(SubscriberProfile childObj, BoDSubscriptionData bodServiceData,String startDateTime, Long validityPeriod, String  validityPeriodUnit) throws RemoteException,Exception{
		try{			
				WebServiceManager.getInstance().getSubscriptionWs_Port().wsChangeBoDSubscription(childObj.getSubscriberID(), bodServiceData.getBodSubscriptionID(),SubscriptionState.SUBSCRIBED.getStringVal() , null, EliteUtility.convertDateToMilliSeconds(startDateTime, "dd-MM-yyyy HH:mm:ss"), null, null, null, null);
		}catch(Exception exp){
			throw new Exception(exp.getMessage());
		}
	}
	
	// to do make changes
	public void rejectBodServiceRequest(SubscriberProfile childObj, BoDSubscriptionData bodServiceData,String startDateTime, Long validityPeriod, String  validityPeriodUnit) throws RemoteException,Exception{
		try{
			WebServiceManager.getInstance().getSubscriptionWs_Port().wsChangeBoDSubscription(childObj.getSubscriberID(), bodServiceData.getBodSubscriptionID(),SubscriptionState.REJECTED.getStringVal() , bodServiceData.getRejectReason(), EliteUtility.convertDateToMilliSeconds(startDateTime, "dd-MM-yyyy HH:mm:ss"), null, null, null, null);
		  // WebServiceManager.getInstance().getSspService().wsRejectBoDServiceRequest(subscriberProfileData, bodServiceData);
		
		}catch(Exception exp){
			throw new Exception(exp.getMessage());
		}
		
	}	
	//to do make changes
  	public List<BoDPackage> getBodPackageData(SubscriberProfile subscriberProfileData) throws RemoteException,Exception{
		List<BoDPackage> bodPackageDataList =null;
		try{
			
			bodPackageDataList = WebServiceManager.getInstance().getSubscriptionWs_Port().wsListBoDPackages(null, null).getBoDPackages();
			//bodPackageDataArray = WebServiceManager.getInstance().getSspService().wsGetBoDPackageData(subscriberProfileData);
		
		}catch(Exception exp){
			throw new Exception(exp.getMessage());
		}
		return bodPackageDataList;
	}
	
	public void unsubscribeBodService(SubscriberProfile subscriberProfileData, BoDSubscriptionData  bodSubscriptionData) throws RemoteException,Exception{
		try{
			
			WebServiceManager.getInstance().getSubscriptionWs_Port().wsChangeBoDSubscription(subscriberProfileData.getSubscriberID(), bodSubscriptionData.getBodSubscriptionID(), SubscriptionState.UNSUBSCRIBED.getStringVal(), bodSubscriptionData.getRejectReason(), bodSubscriptionData.getBodStartTime(), null, null, null, null);
		}catch(Exception exp){
			throw new Exception(exp.getMessage());
		}
	}
	
	public void submitBodRequest(BoDPackage bodPackageData,SubscriberProfile childObj, String bodStartTime, Long validityPeriod, String validityPeriodUnit ) throws RemoteException,DataManagerException {
		try{
			Logger.logDebug(MODULE, "Calling ssp web service to submit child request for BOD Service: " + bodPackageData);
			WebServiceManager.getInstance().getSubscriptionWs_Port().wsSubscribeBoDByID(childObj.getParentID(), childObj.getSubscriberID(), bodPackageData.getBodPackageID(),  String.valueOf(SubscriptionState.APPROVAL_PENDING.getVal()), EliteUtility.convertDateToMilliSeconds(bodStartTime,  "dd-MM-yyyy HH:mm:ss"), validityPeriod, validityPeriodUnit, null, null);		 
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage());
		}
	}
	
	public List<BoDSubscriptionData> getPendingBodReq(SubscriberProfile childObj) throws RemoteException,DataManagerException {
		List<BoDSubscriptionData> pendingBodReqData = null;
		try{
			Logger.logDebug(MODULE, "Calling ssp web service to get pending child request : SubscriberID : " + childObj.getSubscriberID());
		 	pendingBodReqData=WebServiceManager.getInstance().getSubscriptionWs_Port().wsListBoDSubscriptions(childObj.getSubscriberID(), SubscriptionState.APPROVAL_PENDING.getStringVal(), null, null).getBoDSubscriptionDatas();
			//pendingBodReqData = WebServiceManager.getInstance().getSspService().wsGetPendingBodReq(childObj, parentObj);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage());
		}
		return pendingBodReqData;
	}
	
	
	public List<BoDSubscriptionData> getBoDSubscriptionHistory(String subscriberID){
		BoDSubscriptionHistoryResponse  response = WebServiceManager.getInstance().getSubscriptionWs_Port().wsListBoDSubscriptionHistory(subscriberID, null, null);
		return response.getBoDSubscriptionDatas();
	}
}
