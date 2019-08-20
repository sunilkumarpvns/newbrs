package com.elitecore.ssp.webservice.subscriber;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elitecore.netvertexsm.ws.cxfws.subscriberprovisioning.WsListSubscriberProfiles.SubscriberProfileCriteria;
import com.elitecore.netvertexsm.ws.cxfws.subscriberprovisioning.WsListSubscriberProfiles.SubscriberProfileCriteria.Entry;
import com.elitecore.ssp.subscriber.SubscriberProfile;
import com.elitecore.ssp.util.EliteUtility;
import com.elitecore.ssp.util.exception.DataManagerException;
import com.elitecore.ssp.webservice.WebServiceManager;



public class SubscriberProfilerDataManager {
	private static final String MODULE = SubscriberProfilerDataManager.class.getSimpleName();
	
	public SubscriberProfile[] getChildAccountsExt(SubscriberProfile subscriberProfileData, Map<String, String> searchCriteriaMap) throws RemoteException, DataManagerException{
		SubscriberProfile[] childAccountsExtData=null;
		try{
			List<com.elitecore.netvertexsm.ws.cxfws.subscriberprovisioning.SubscriberProfile> tempSubscriberProfiles=null;
				searchCriteriaMap.put("PARENTID", subscriberProfileData.getSubscriberID());
				SubscriberProfileCriteria subscriberProfileCriteria = new SubscriberProfileCriteria();
		        subscriberProfileCriteria.getEntry().addAll(convertMapToList(searchCriteriaMap));
		        tempSubscriberProfiles=WebServiceManager.getInstance().getSubscriberProvisioningWS().wsListSubscriberProfiles(subscriberProfileCriteria, null, null).getSubscriberProfile();
		        childAccountsExtData=new SubscriberProfile[tempSubscriberProfiles.size()];
		        for (int i = 0; i < tempSubscriberProfiles.size(); i++) {
		        	childAccountsExtData[i]=EliteUtility.getSubscriberProfile(tempSubscriberProfiles.get(i));	  
		 		}   		
			
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage());
		}
		return childAccountsExtData;
	}
	
	public SubscriberProfile[] getChildAccounts(SubscriberProfile subscriberProfileData) throws RemoteException, DataManagerException{
		SubscriberProfile[] childAccounts=null;
		try{
			List<com.elitecore.netvertexsm.ws.cxfws.subscriberprovisioning.SubscriberProfile> tempSubscriberProfiles=null;
			
			 Map<String,String> searhCriteria=new HashMap<String,String>();
		        searhCriteria.put("PARENTID", subscriberProfileData.getSubscriberID());
		        SubscriberProfileCriteria subscriberProfileCriteria = new SubscriberProfileCriteria();
		        subscriberProfileCriteria.getEntry().addAll(convertMapToList(searhCriteria));
		        tempSubscriberProfiles=WebServiceManager.getInstance().getSubscriberProvisioningWS().wsListSubscriberProfiles(subscriberProfileCriteria, null, null).getSubscriberProfile();
		        childAccounts=new SubscriberProfile[tempSubscriberProfiles.size()];
		        for (int i = 0; i < tempSubscriberProfiles.size(); i++) {
		       childAccounts[i]=EliteUtility.getSubscriberProfile(tempSubscriberProfiles.get(i));	  
		 		}   
            	
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage());
		}
		return childAccounts;
	}
	
	 private List<Entry> convertMapToList(Map<String, String> searchCriteriaMap) {
			List<Entry> entryList=null;
			if (searchCriteriaMap != null && !searchCriteriaMap.isEmpty()) {
				entryList=new ArrayList<Entry>();
				for(Map.Entry<String ,String> temp:searchCriteriaMap.entrySet()){
	                  Entry entry=new Entry();
	                  entry.setKey(temp.getKey());
	                  entry.setValue(temp.getValue());
	                  entryList.add(entry);
				}
			}
			return entryList;
		}
}
