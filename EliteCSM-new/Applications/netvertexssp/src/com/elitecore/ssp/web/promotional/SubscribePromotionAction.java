package com.elitecore.ssp.web.promotional;

import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.ssp.subscriber.SubscriberProfile;
import com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.AddOnApprovalReqResponse;
import com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.AddOnPackage;

import com.elitecore.ssp.util.constants.ErrorMessageConstants;
import com.elitecore.ssp.util.constants.RequestAttributeKeyConstant;
import com.elitecore.ssp.util.constants.ResultCodes;
import com.elitecore.ssp.util.constants.SessionAttributeKeyConstant;
import com.elitecore.ssp.util.exception.DataManagerException;
import com.elitecore.ssp.util.logger.Logger;
import com.elitecore.ssp.web.core.base.BaseWebAction;
import com.elitecore.ssp.web.promotional.forms.SubscribePromotionForm;
import com.elitecore.ssp.webservice.promotional.PromotionalDataManager;

public class SubscribePromotionAction extends BaseWebAction {
	private static final String MODULE = SubscribePromotionAction.class.getSimpleName();

	public ActionForward execute(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws DataManagerException {
		Logger.logTrace(MODULE, "Entered execute method of " + getClass().getName());
		SubscribePromotionForm subscribePromotionForm = (SubscribePromotionForm)form;
		try{			
			//System.out.println(" (String)request.getSession().getAttribute(SessionAttributeKeyConstant.SELECTED_LINK) ------ > "+(String)request.getSession().getAttribute(SessionAttributeKeyConstant.SELECTED_LINK));
			//request.getSession().setAttribute(SessionAttributeKeyConstant.SELECTED_LINK, (String)request.getSession().getAttribute(SessionAttributeKeyConstant.SELECTED_LINK));
			Long addOnPackageId=subscribePromotionForm.getAddOnPackageId();
			Logger.logDebug(MODULE, "Subscribe Promotion, id:"+subscribePromotionForm.getAddOnPackageId());
			AddOnPackage promotionalData = null;
			PromotionalDataManager promotinalDataManager = new PromotionalDataManager();
			SubscriberProfile subscriberProfileData = (SubscriberProfile)request.getSession().getAttribute(SessionAttributeKeyConstant.CHILD_OBJECT);			
			SubscriberProfile currentUser = (SubscriberProfile)request.getSession().getAttribute(SessionAttributeKeyConstant.CURRENT_USER);
			
			
			promotionalData = getAvailablePromotionData(addOnPackageId,request);
			String childID = request.getParameter("childID");
			SubscriberProfile childObject = null;

			String selectedPortal =(String)request.getSession().getServletContext().getAttribute("selectedPortal");
			String addOnSubReq = request.getParameter("addOnSubReqID");
			Long addOnSubRequestID = null;

			if(addOnSubReq != null){
				addOnSubRequestID = Long.parseLong(addOnSubReq);
			}

			if(selectedPortal != null && selectedPortal.equalsIgnoreCase(RequestAttributeKeyConstant.ENTERPRISE_PORTAL)){
				Map<String,SubscriberProfile> subscriberProfileDataMap = (Map<String,SubscriberProfile>)request.getSession().getAttribute(SessionAttributeKeyConstant.SUBSCRIBER_PROFILE_MAP);
				childObject = subscriberProfileDataMap.get(childID);				
			}
              
			
			
			SubscriberProfile parentObj = null;

			if(promotionalData==null){
				throw new Exception("Promotional Data Not Found");
			}
            
			
			Long endDate=calucateEndDate(subscriberProfileData);
			
			ActionMessage successMessage = null;
			if(selectedPortal != null && selectedPortal.equalsIgnoreCase(RequestAttributeKeyConstant.ENTERPRISE_PORTAL) && currentUser.getParentID() != null){				
				/*parentObj = new SubscriberProfile();
				parentObj.setSubscriberID(subscriberProfileData.getParentID());
				promotinalDataManager.submitPromotionRequest(promotionalData,subscriberProfileData, parentObj);*/
				promotinalDataManager.submitPromotionRequest(promotionalData,subscriberProfileData,endDate);
				request.setAttribute("responseUrl","/childPromotional.do?"+SessionAttributeKeyConstant.SELECTED_LINK+"prmoffer");
				//request.setAttribute("responseUrl","/promotional.do?"+SessionAttributeKeyConstant.SELECTED_LINK+"prmoffer");
				ActionMessage message = new ActionMessage("enterprise.promotional.request.submit.success");
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveMessages(request,messages);
				return mapping.findForward(SUCCESS);
			}else{	
				if(childID != null && selectedPortal !=null && selectedPortal.equalsIgnoreCase(RequestAttributeKeyConstant.ENTERPRISE_PORTAL)){
					AddOnApprovalReqResponse  addOnApprovalReqResponse =  promotinalDataManager.approveAddOnSubReq(childID,addOnSubRequestID,addOnPackageId);
					if(addOnApprovalReqResponse.getResponseCode()==ResultCodes.SUCCESS){
					    successMessage = new ActionMessage("enterprise.promotion.approve.success");
					}else{
						successMessage=new ActionMessage("enterprise.promotion.approve.status",addOnApprovalReqResponse.getResponseMessage());
					}
				}else{
					promotinalDataManager.subscribePromotion(promotionalData, subscriberProfileData,endDate);
					successMessage = new ActionMessage("promotional.subscribe.success");
				}
			}

			request.setAttribute("responseUrl","/promotional.do");
			//request. setAttribute("selectedPortal", "enterprisessp");
			ActionMessages messages = new ActionMessages();
			messages.add("information",successMessage);
			saveMessages(request,messages);
			return mapping.findForward(SUCCESS);
		}catch(RemoteException e){
			ActionMessages messages = new ActionMessages();
			System.out.println("Error Message :"+e.getMessage());
			if(e.getMessage().equals(ErrorMessageConstants.ALREADY_SUBSCRIBED)){
				request.setAttribute(ErrorMessageConstants.ALREADY_SUBSCRIBED,ErrorMessageConstants.ALREADY_SUBSCRIBED);
				ActionMessage message = new ActionMessage("promotional.subscribe.alreadysubscribed");
				ActionMessage suggestionMessage = new ActionMessage("promotional.subscribe.alreadysubscribed.suggestion");
				messages.add("information", message);
				messages.add("information", suggestionMessage);
			}else if(e.getMessage().equals(ErrorMessageConstants.OUT_OF_SUBSCRIPTOIN_VALIDITY)){
				ActionMessage message = new ActionMessage("promotional.subscribe.invalidsubscription");
				messages.add("information", message);
			}else if(e.getMessage().equalsIgnoreCase(ErrorMessageConstants.ACCOUNT_EXPIRED)){
				ActionMessage message = new ActionMessage("general.account.expired");
				messages.add("information", message);
			}else{
				Logger.logTrace(MODULE, e);
				ActionMessage message = new ActionMessage("promotional.subscribe.failure");
				messages.add("information", message);
			}
			saveErrors(request, messages);
		}catch(Exception e){
			Logger.logTrace(MODULE, e);
			ActionMessage	message = new ActionMessage("promotional.subscribe.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE);
	}
	
	private Long calucateEndDate(SubscriberProfile subscriberProfile){
		if(subscriberProfile.getCustomerType()==null){
				Logger.logDebug(MODULE, "Skipping Calculating end date for Subscriber :" +subscriberProfile.getSubscriberID() +". Reason: Susbcriber is Type is not mentioned , Considering as Prepaid");
				return null;
		}
		if(subscriberProfile.getCustomerType().equalsIgnoreCase("prepaid")){
			Logger.logDebug(MODULE, "Skipping Calculating end date for Subscriber :" +subscriberProfile.getSubscriberID() +". Reason: Susbcriber is prepaid");
			return null;
		}
		int billingDate=convertStringToInt(subscriberProfile.getBillingDate());
		if(billingDate==0){
			return null;
		}
	    Calendar calender=Calendar.getInstance();
	    if(billingDate>calender.get(Calendar.DAY_OF_MONTH)){
	    	int lastDateOfTheMonth = calender.getActualMaximum(Calendar.DAY_OF_MONTH);
	    	if(billingDate > lastDateOfTheMonth){
	    		calender.set(Calendar.DAY_OF_MONTH , lastDateOfTheMonth);
	    	}else{
	    		calender.set(Calendar.DAY_OF_MONTH , billingDate);
	    	}
	    	return calender.getTimeInMillis();
	    }else{
	    	calender.add(Calendar.MONTH,1);
	    	return calender.getTimeInMillis();
	    }
	}
	private int convertStringToInt(String billingDate){
		int billDate=0;
         if(billingDate!=null){
        	 billDate=Integer.parseInt(billingDate);
         }
    	return billDate;
	}
	
}