package com.elitecore.ssp.webservice.parentalcontrol;

import java.rmi.RemoteException;
import java.util.List;

import com.elitecore.netvertexsm.ws.cxfws.ssp.parental.ParentalPolicy;
import com.elitecore.netvertexsm.ws.cxfws.ssp.parental.PolicyGroup;
import com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.AddOnPackage;
import com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.UsageMonotoringInformation;
import com.elitecore.ssp.subscriber.SubscriberProfile;
import com.elitecore.ssp.util.constants.AggregationKey;
import com.elitecore.ssp.util.constants.MeteringLevel;
import com.elitecore.ssp.util.exception.DataManagerException;
import com.elitecore.ssp.util.logger.Logger;
import com.elitecore.ssp.webservice.WebServiceManager;

public class ChildAccountDataManager {
	private static String MODULE=ChildAccountDataManager.class.getSimpleName();
	
	public AddOnPackage[] getParentalAddOns(SubscriberProfile subscriberProfileData) throws RemoteException, DataManagerException{
		AddOnPackage[] addOnPackages=null;
		try{
			Logger.logTrace(MODULE, "Called getParentalAddOns().");
			List<AddOnPackage> tempAddOnList=WebServiceManager.getInstance().getSubscriptionWs_Port().wsListAddOnPackages(null,subscriberProfileData.getSubscriberPackage(),null,null).getAddOnPackage();
		    if (tempAddOnList != null && !tempAddOnList.isEmpty()){
		    	addOnPackages=new AddOnPackage[tempAddOnList.size()];
		    	tempAddOnList.toArray(addOnPackages);
			}
			
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(),exp);
		}
		return addOnPackages;
	}
	
	public List<ParentalPolicy> getParentalPolicy(SubscriberProfile subscriberProfileData,String subscriberIdentity) throws RemoteException, DataManagerException{
		//ParentalPolicy[] parentalPolicyDatas=null;
		List<ParentalPolicy> tempParentalPolicies;
		try{
			Logger.logTrace(MODULE, "called getParentalPolicy().");
			tempParentalPolicies=WebServiceManager.getInstance().getParentalWs_Port().wsListParentalPolicies(subscriberIdentity,null,null).getParentalPolicy();
			/*if (tempParentalPolicies != null && !tempParentalPolicies.isEmpty()) {
				parentalPolicyDatas=new ParentalPolicy[tempParentalPolicies.size()];
				tempParentalPolicies.toArray(parentalPolicyDatas);
			}*/
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage());
		}
		return tempParentalPolicies;
	}	
	
	
	public int saveAccessControlPolicy(List<ParentalPolicy> parentalPolicyDataList,String subscriberIdentity) throws RemoteException, DataManagerException{
		
		try{
			Logger.logTrace(MODULE, "Called saveAccessControlPolicy().");
			long responseCode=WebServiceManager.getInstance().getParentalWs_Port().wsApplyAccessControlPolicies(subscriberIdentity, parentalPolicyDataList, null,null).getResponseCode();
			int i=(int)responseCode;
			return i;
			
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage());
		}		
	}
   
	// to do need to update	
/*	public QuotaUsageData[] getChildAccountQuotaUsage(SubscriberProfile subscriberProfileData) throws RemoteException, DataManagerException{
		QuotaUsageData[] quoaUsageData=null;
		try{
			Logger.logTrace(MODULE, " getChildAccountQuotaUsage().");
			return quoaUsageData;
			//return WebServiceManager.getInstance().getSspService().wsGetQuotaUsage(subscriberProfileData);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage());
		}
		
	}*/	
	
	public UsageMonotoringInformation[] getChildAccountUsageMeteringInfo(String subscriberIdentity, String packageName) throws RemoteException, DataManagerException{
		UsageMonotoringInformation[] usageMonitInformations=null;
		try{
			Logger.logTrace(MODULE, " Method called getChildAccountUsageMeteringInfo()");
			//List<UsageMonotoringInformation> tempUsageInformations=WebServiceManager.getInstance().getSubscriptionWs_Port().wsListUsageMonitoringInformation(subscriberIdentity, MeteringLevel.PACKAGE_LEVEL.getVal(), AggregationKey.BILLING_CYCLE.getVal(), packageName, null, null).getUsageMonotoringInformation();
			List<UsageMonotoringInformation> tempUsageInformations=WebServiceManager.getInstance().getSubscriptionWs_Port().wsListUsageMonitoringInformation(subscriberIdentity, MeteringLevel.PACKAGE_LEVEL.getVal(), null, null, null, null).getUsageMonotoringInformation();
			if (tempUsageInformations != null&& !tempUsageInformations.isEmpty()) {
				usageMonitInformations=new UsageMonotoringInformation[tempUsageInformations.size()];
				tempUsageInformations.toArray(usageMonitInformations);
			}
			return usageMonitInformations;
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage());
		}
	}	
	
	
	// to do make changes
	public long[] getSubscriberAccountAllowedUsageInfo(SubscriberProfile subscriberProfileData) throws RemoteException, DataManagerException{		
		long[] allowedUsageInfoArray = {0L,0L,0L,0L,0L,0L};
		try{
			Logger.logTrace(MODULE, " getSubscriberAccountAllowedUsageInfo.");
			
			PolicyGroup policyGroup = getPolicyGroup(subscriberProfileData);
			if(policyGroup!=null){
				allowedUsageInfoArray[2] = policyGroup.getHsqValue();
				Logger.logTrace(MODULE, " PG: "+policyGroup.getPolicyGroupName());
				Logger.logTrace(MODULE, " HSQ Value : "+policyGroup.getHsqValue());
			}
			List<UsageMonotoringInformation> usageMonitoringList = WebServiceManager.getInstance().getSubscriptionWs_Port().wsListUsageMonitoringInformation(subscriberProfileData.getSubscriberID(),MeteringLevel.PACKAGE_LEVEL.getVal(),  AggregationKey.BILLING_CYCLE.getVal(),subscriberProfileData.getSubscriberPackage(), null, null).getUsageMonotoringInformation();
			if(usageMonitoringList!=null && usageMonitoringList.size()>0){
				UsageMonotoringInformation usageMonInfo = usageMonitoringList.get(0);			  		
	  			if(usageMonInfo!=null){
	  				allowedUsageInfoArray[5] = usageMonInfo.getTotalOctets();
	  			}
			}
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage());
		}
		return allowedUsageInfoArray;
	}	
	// to do need to update
	public long getChildAccountUsageControlAddOn(String subscriberIdentity) throws RemoteException, DataManagerException{
		
		try{
			Logger.logTrace(MODULE, " getChildAccountUsageControlAddOn().");
			return 0;
			//return WebServiceManager.getInstance().getSspService().wsGetUsageControlAddOn(subscriberIdentity);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(),exp);
		}
		
	}
	
	// to do need to update
	public boolean saveUsageControlPolicy( String addOnId, String subscriberIdentity) throws RemoteException, DataManagerException{
		
		try{
			Logger.logTrace(MODULE, " saveUsageControlPolicy().");
			return false;//WebServiceManager.getInstance().getSspService().wsSaveUsageControlPolicy(addOnId,subscriberIdentity);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(),exp);
		}
	}
	
	public PolicyGroup getPolicyGroup(SubscriberProfile subscriberProfileData) throws RemoteException, DataManagerException {
			PolicyGroup policyGroup = null;
			try {	
				policyGroup =	WebServiceManager.getInstance().getParentalWs_Port().wsGetPolicyGroup(subscriberProfileData.getSubscriberID(), subscriberProfileData.getSubscriberPackage(), null, null).getPolicyGroup();
			} catch (Exception exp) {
				throw new DataManagerException(exp.getMessage(), exp);
			}
			return policyGroup;
	}
}
