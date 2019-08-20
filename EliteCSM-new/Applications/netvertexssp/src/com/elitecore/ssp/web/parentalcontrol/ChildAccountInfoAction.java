package com.elitecore.ssp.web.parentalcontrol;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.ws.cxfws.ssp.parental.PolicyGroup;
import com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.UsageMonotoringInformation;
import com.elitecore.ssp.subscriber.SubscriberProfile;
import com.elitecore.netvertexsm.ws.cxfws.ssp.parental.WsListChildProfiles.ChildProfileCriteria;
import com.elitecore.netvertexsm.ws.cxfws.ssp.parental.WsListChildProfiles.ChildProfileCriteria.Entry;
import com.elitecore.netvertexsm.ws.cxfws.subscriberprovisioning.SubscriberProvisioningResponse;
import com.elitecore.netvertexsm.ws.cxfws.subscriberprovisioning.WsUpdateSubscriberProfile;
import com.elitecore.ssp.util.ChildAccountUtility;
import com.elitecore.ssp.util.DataManagerUtils;
import com.elitecore.ssp.util.constants.SessionAttributeKeyConstant;
import com.elitecore.ssp.util.exception.DataManagerException;
import com.elitecore.ssp.util.logger.Logger;
import com.elitecore.ssp.web.core.base.BaseWebAction;
import com.elitecore.ssp.web.home.forms.HomeForm;
import com.elitecore.ssp.web.parentalcontrol.forms.ChildAccountInfoForm;
import com.elitecore.ssp.webservice.WebServiceManager;
import com.elitecore.ssp.webservice.parentalcontrol.ChildAccountDataManager;

public class ChildAccountInfoAction extends BaseWebAction {
	private static final String MODULE = ChildAccountInfoAction.class.getSimpleName();
	private static final String VIEW_ACCOUNT_INFO_PAGE="childAccountInfoSuccess";
	
	public ActionForward execute(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws DataManagerException {		
					
		Logger.logTrace(MODULE, " entered into execute method of : "+ getClass().getName());
		//ParentalWSController loginDataManager = new ParentalWSController();
		ChildAccountDataManager childAccountDataManager = new ChildAccountDataManager();								
		String subscriberIdentity= request.getParameter("subscriberIdentity");
		String childIndex= request.getParameter("childIndex");
		request.getSession().setAttribute("childIndex", childIndex);
		ChildAccountInfoForm childAccountInfoForm=(ChildAccountInfoForm)form;
		
		Logger.logTrace(MODULE, " subscriberIdentity : "+ subscriberIdentity);
	 
		HomeForm homeForm = (HomeForm)request.getSession().getAttribute(SessionAttributeKeyConstant.HOME_FORM);	
		
		ChildAccountInfoForm childAccountFormFromSession = (ChildAccountInfoForm) request.getSession().getAttribute(SessionAttributeKeyConstant.CHILD_ACCOUNT_INFO_FORM);
		try{		
			
			SubscriberProfile childAccounts[]=null; 
			
			System.out.println("Action Performed is : "+ childAccountInfoForm.getActionPerformed());
			
			if(childAccountInfoForm!=null && childAccountInfoForm.getActionPerformed()!=null && !childAccountInfoForm.getActionPerformed().trim().isEmpty()){
				
				request.setAttribute("successCode", null);
				
				request.setAttribute("errorCode", null);
				
				SubscriberProfile subscriberProfile = childAccountFormFromSession.getProfile();
				if(childAccountInfoForm.getActionPerformed().trim().equalsIgnoreCase("blockURL")){					
					String blockedURL = childAccountInfoForm.getBlockedUrlString();
					subscriberProfile.setParam1(blockedURL);
					
					HashMap<String , String> map = new HashMap<String, String>();
					map.put("PARAM1", subscriberProfile.getParam1());
					map.put("SUBSCRIBERIDENTITY", subscriberProfile.getSubscriberID());
					System.out.println("Subscriber ID : "+subscriberProfile.getSubscriberID());
					
					WsUpdateSubscriberProfile.SubscriberProfile wsUpdateSubscriberProfile = getWSSubscriberProfile(map);
					
					SubscriberProvisioningResponse responseFromWS = WebServiceManager.getInstance().getSubscriberProvisioningWS().wsUpdateSubscriberProfile(wsUpdateSubscriberProfile, subscriberProfile.getSubscriberID(), "", "");
					System.out.println("Response Code : "+responseFromWS.getResponseCode());
					System.out.println("Response Message : "+responseFromWS.getResponseCode());
					return mapping.findForward(VIEW_ACCOUNT_INFO_PAGE);	
				}else if(childAccountInfoForm.getActionPerformed().trim().equalsIgnoreCase("quotaRollOver")){
			
					System.out.println("Do Quota RollOver");
					System.out.println("Quota RollOver is : "+childAccountInfoForm.getQuotaRollOver());
					
					try{
						updateQuotaRollOverDetails(subscriberProfile.getSubscriberID(),childAccountInfoForm.getQuotaRollOver());
						SubscriberProfile currentUser=(SubscriberProfile)request.getSession().getAttribute(SessionAttributeKeyConstant.TEMP_CURRENT_USER);
						currentUser.setParam10("no");
						currentUser.setParam11(childAccountInfoForm.getQuotaRollOver());
						request.getSession().setAttribute(SessionAttributeKeyConstant.TEMP_CURRENT_USER,currentUser);
						request.setAttribute("successCode", "Your request for Quota Rollover has been accepted.");
					}catch(Exception exception){
						exception.printStackTrace();
						request.setAttribute("errorCode", "Unable to process your request due to a technical error. Please try again later.");
					}
					
					
					return mapping.findForward(VIEW_ACCOUNT_INFO_PAGE);	
				}else if(childAccountInfoForm.getActionPerformed().trim().equalsIgnoreCase("notificationPercentageValue")){
					updateNotificationPercentage(subscriberProfile.getSubscriberID(), childAccountInfoForm.getNotificationPercentage());
					SubscriberProfile currentUser=(SubscriberProfile)request.getSession().getAttribute(SessionAttributeKeyConstant.TEMP_CURRENT_USER);
					currentUser.setParam3(String.valueOf(childAccountInfoForm.getNotificationPercentage()));
					request.getSession().setAttribute(SessionAttributeKeyConstant.TEMP_CURRENT_USER,currentUser);
					request.setAttribute("successCode", "Your request for Notification Percentage has been accepted");
					
					return mapping.findForward(VIEW_ACCOUNT_INFO_PAGE);	
				}
				
				
			}
		  	if( homeForm != null ){
		  		SubscriberProfile currentUser=(SubscriberProfile)request.getSession().getAttribute(SessionAttributeKeyConstant.TEMP_CURRENT_USER);
		  		request.getSession().setAttribute(SessionAttributeKeyConstant.SELECTED_LINK, request.getParameter(SessionAttributeKeyConstant.SELECTED_LINK));
		  		
	  			
	  			
		  		if(subscriberIdentity.equalsIgnoreCase(currentUser.getSubscriberID())){
		  			long[] allowedUsageInfo =  childAccountDataManager.getSubscriberAccountAllowedUsageInfo(currentUser);			  			
		  			Logger.logInfo(MODULE, " Retrieved usage info of current user : "+currentUser.getUserName());
		  			childAccountInfoForm.setAllowedUsageInfo(allowedUsageInfo);			  			
		  			request.getSession().setAttribute(SessionAttributeKeyConstant.CHILD_OBJECT, currentUser);
		  			request.getSession().setAttribute(SessionAttributeKeyConstant.TEMP_CHILD_OBJECT, currentUser);
		  			request.getSession().setAttribute(SessionAttributeKeyConstant.CHILD_ACCOUNT_INFO_FORM, childAccountInfoForm);
		  			Logger.logInfo(MODULE, " ATTRIBUTES SET for "+currentUser.getUserName());
		  		}else{
			  		childAccounts = homeForm.getChildAccounts();
			  		if(childAccounts!=null && childAccounts.length>0){
					  	for(SubscriberProfile childObject:childAccounts){
					  		if(childObject.getSubscriberID().equals(subscriberIdentity)){
					  			long[] allowedUsageInfo =  childAccountDataManager.getSubscriberAccountAllowedUsageInfo(childObject);
					  			Logger.logInfo(MODULE, " Retrieved usage info of child : "+childObject.getUserName());
					  			childAccountInfoForm.setAllowedUsageInfo(allowedUsageInfo);	
					  			request.getSession().setAttribute(SessionAttributeKeyConstant.CHILD_OBJECT, childObject);
					  			request.getSession().setAttribute(SessionAttributeKeyConstant.TEMP_CHILD_OBJECT, childObject);
					  			request.getSession().setAttribute(SessionAttributeKeyConstant.CHILD_ACCOUNT_INFO_FORM, childAccountInfoForm);				  			 
					  			break;
					  		}
					  	}
			  		}
		  		}
		  	} 		  			  	
		  	return mapping.findForward(VIEW_ACCOUNT_INFO_PAGE);		
		}catch(Exception e){
			Logger.logTrace(MODULE, e);
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
			return mapping.findForward(FAILURE);
	}
	
	
	private void updateQuotaRollOverDetails(String subscriberID,String rollOverValue) throws Exception{
			String updateQuery1 = "update tblnetvertexcustomer set PARAM10 = 'no', PARAM11 ='"+rollOverValue+"' where subscriberidentity = '"+subscriberID+"'";
			DataManagerUtils dataManagerUtils = new DataManagerUtils();
			dataManagerUtils.executeUpdateOrInsert(updateQuery1);
	}
	
	private void updateNotificationPercentage(String subscriberID,int notificationPercentage) throws Exception{
		String updateQuery1 = "update tblnetvertexcustomer set PARAM3 ='"+notificationPercentage+"' where subscriberidentity = '"+subscriberID+"'";
		DataManagerUtils dataManagerUtils = new DataManagerUtils();
		dataManagerUtils.executeUpdateOrInsert(updateQuery1);
	}
	
	
	public WsUpdateSubscriberProfile.SubscriberProfile getWSSubscriberProfile(HashMap<String, String> hashMap) throws RemoteException, DataManagerException{
		
		WsUpdateSubscriberProfile.SubscriberProfile subscriberProfile = new WsUpdateSubscriberProfile.SubscriberProfile();
		subscriberProfile.getEntry().addAll(ChildAccountUtility.convertMapToEntryList(hashMap));
				
		return subscriberProfile;
	}
	
}
