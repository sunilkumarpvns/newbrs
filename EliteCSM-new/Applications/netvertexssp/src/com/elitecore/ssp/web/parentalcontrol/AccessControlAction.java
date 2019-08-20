package com.elitecore.ssp.web.parentalcontrol;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.ws.cxfws.ssp.parental.ParentalPolicy;
import com.elitecore.ssp.subscriber.SubscriberProfile;
import com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.AddOnPackage;
import com.elitecore.ssp.util.ChildAccountUtility;
import com.elitecore.ssp.util.constants.RequestAttributeKeyConstant;
import com.elitecore.ssp.util.constants.SessionAttributeKeyConstant;
import com.elitecore.ssp.util.exception.DataManagerException;
import com.elitecore.ssp.util.logger.Logger;
import com.elitecore.ssp.web.core.base.BaseWebAction;
import com.elitecore.ssp.web.parentalcontrol.forms.AccessControlForm;
import com.elitecore.ssp.webservice.parentalcontrol.ChildAccountDataManager;

public class AccessControlAction extends BaseWebAction {

	private static final String MODULE = AccessControlAction.class.getSimpleName();
	private static final String ACCESS_CONTROL="accessControl";

	public ActionForward execute(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws DataManagerException {

		Logger.logInfo(MODULE, "called execute method");
		AccessControlForm accessControlForm = (AccessControlForm)form;

		try
		{			
			SubscriberProfile currentUser=(SubscriberProfile)request.getSession().getAttribute(SessionAttributeKeyConstant.CURRENT_USER);
			SubscriberProfile childObject=(SubscriberProfile)request.getSession().getAttribute(SessionAttributeKeyConstant.CHILD_OBJECT);			
			request.getSession().setAttribute(SessionAttributeKeyConstant.SELECTED_LINK, request.getParameter(SessionAttributeKeyConstant.SELECTED_LINK));
			String[] subscriberIdentityArray = request.getParameterValues("select");
			request.setAttribute(RequestAttributeKeyConstant.SELECTED_SUBSCRIBER, subscriberIdentityArray);
			 Logger.logDebug(MODULE, "currentUser username is: "+currentUser.getUserName());
			 Logger.logDebug(MODULE, "childObject username is: "+childObject.getUserName());
			ChildAccountDataManager childAccountDataManager=new ChildAccountDataManager();
			AddOnPackage[] addonPackageArray = childAccountDataManager.getParentalAddOns(currentUser);
		 	 		 	
			List<ParentalPolicy> parentalPolicyDataList = null;
			String boxesNameStr=request.getParameter("boxesNameArray");
			boolean areConfiguredPoliciesOverloaded = false;
			
			if(boxesNameStr!=null && boxesNameStr.length()>0){
				String usageControlAddOnId=request.getParameter("usageControlAddOnId");
				if(addonPackageArray  != null){
					parentalPolicyDataList=ChildAccountUtility.parseToDayAndTimeSlots(boxesNameStr,addonPackageArray);
					
					if(parentalPolicyDataList.size() > 10){
						areConfiguredPoliciesOverloaded = true;
					}else{
						if(subscriberIdentityArray != null && subscriberIdentityArray.length > 0){
							for (String subscriberIdentity : subscriberIdentityArray) {
								childAccountDataManager.saveAccessControlPolicy(parentalPolicyDataList,subscriberIdentity);
								childAccountDataManager.saveUsageControlPolicy(usageControlAddOnId,subscriberIdentity);							
							}					
						}else{
							Logger.logDebug(MODULE, " Total Configured policy: "+parentalPolicyDataList.size());							
							int i=childAccountDataManager.saveAccessControlPolicy(parentalPolicyDataList, childObject.getSubscriberID());
							//childAccountDataManager.wsApplyAccessControlPolicies( childObject.getSubscriberID(),parentalPolicyDataList);
							boolean saveFlag=childAccountDataManager.saveUsageControlPolicy(usageControlAddOnId,childObject.getSubscriberID());
							Logger.logDebug(MODULE, " Confiured Policies Saved Successfully :"+(i==0?"No":"Yes"));
							Logger.logDebug(MODULE, " Usage Control Saved Successfully :"+(saveFlag==false?"No":"Yes"));
						}		
					}
				}
			}

			// Code to fetch configured policies data and display accordingly in the color-box table			
			
			List<ParentalPolicy> parentalPolicyList = null;
			if(subscriberIdentityArray != null && subscriberIdentityArray.length == 1){
				//parentalPolicyData=childAccountDataManager.wsListAddOnPackages(currentUser,subscriberIdentityArray[0]);
			}else{
				parentalPolicyList = childAccountDataManager.getParentalPolicy(childObject,childObject.getSubscriberID());
			}
			Logger.logDebug(MODULE, "Total Policies: "+parentalPolicyList.size());
			
			if(parentalPolicyList==null || (subscriberIdentityArray != null && subscriberIdentityArray.length > 1)){
				Logger.logDebug(MODULE, " No Parental Policy Configured");
				ParentalPolicy obj = new ParentalPolicy();
				obj.setAddOnPackageID(0L);
				obj.setDaysOfTheWeek("0");							
				obj.setTimePeriod("0-0");				
				parentalPolicyList.add(obj);
			}					

			try{								
				//long usageControlAddOn=childAccountDataManager.getChildAccountUsageControlAddOn(childObject.getSubscriberID());
				accessControlForm.setPromotionalData(addonPackageArray ); // Parental Addons
				
				if(areConfiguredPoliciesOverloaded == true) {
					accessControlForm.setParentalPolicyList(parentalPolicyDataList); // Polocies configured on to the ssp portal
					request.setAttribute("parentalPolicyData", parentalPolicyDataList);
					request.setAttribute("areConfiguredPoliciesOverloaded", ""+areConfiguredPoliciesOverloaded);
				}else{
					accessControlForm.setParentalPolicyList(parentalPolicyList);
					request.setAttribute("parentalPolicyData", parentalPolicyList);
					request.setAttribute("areConfiguredPoliciesOverloaded", ""+areConfiguredPoliciesOverloaded);
				}
				//accessControlForm.setUsageControlAddOn(usageControlAddOn);
			}catch(Throwable exception){
				Logger.logError(MODULE, " Error while processing configured policies, Reason :" +exception.getMessage());
				Logger.logTrace(MODULE, exception);
			}

			request.setAttribute("accessControlForm", accessControlForm);
			request.setAttribute("parentalPolicyData", parentalPolicyList);
			request.setAttribute("promotionalData", addonPackageArray );									
			return mapping.findForward(ACCESS_CONTROL);		

		}catch(Exception e){
			e.printStackTrace();
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE);
	}
}