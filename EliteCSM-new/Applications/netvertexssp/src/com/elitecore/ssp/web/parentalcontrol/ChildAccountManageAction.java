package com.elitecore.ssp.web.parentalcontrol;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.ws.cxfws.ssp.parental.ParentalPolicy;
import com.elitecore.ssp.subscriber.SubscriberProfile;
import com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.AddOnPackage;
import com.elitecore.ssp.util.ChildAccountUtility;
import com.elitecore.ssp.util.constants.SessionAttributeKeyConstant;
import com.elitecore.ssp.util.exception.DataManagerException;
import com.elitecore.ssp.util.logger.Logger;
import com.elitecore.ssp.web.core.base.BaseWebAction;
import com.elitecore.ssp.web.parentalcontrol.forms.ChildAccountManageForm;
import com.elitecore.ssp.webservice.parentalcontrol.ChildAccountDataManager;


public class ChildAccountManageAction extends BaseWebAction {
	
	private static final String MODULE = ChildAccountManageAction.class.getSimpleName();
	private static final String CHILD_ACCOUNT_MANAGE="childaccountManageSuccess";
	
	public ActionForward execute(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws DataManagerException {
		
		Logger.logTrace(MODULE, "Entered execute method of " + getClass().getName());
		ChildAccountManageForm childAccountManageForm = (ChildAccountManageForm)form;
				
		try
		{
			SubscriberProfile subscriberProfileData = (SubscriberProfile)request.getSession().getAttribute(SessionAttributeKeyConstant.CURRENT_USER);
			String subscriberName= request.getParameter("subscriberName");
			String subscriberIdentity= request.getParameter("subscriberIdentity");
		 
			HttpSession session=request.getSession();
			if(subscriberName!=null && subscriberName.trim().length()>0){
				session.setAttribute("subscriberName", subscriberName);
				session.setAttribute("subscriberIdentity", subscriberIdentity);
				subscriberIdentity=(String)session.getAttribute("subscriberIdentity");
			}

			
			String simpleDataTable=request.getParameter("simpleDataTable");
			ChildAccountDataManager childAccountDataManager=new ChildAccountDataManager();		
			
			 
			
			if(simpleDataTable!=null && simpleDataTable.equalsIgnoreCase("true")){
				Logger.logTrace(MODULE, " code to save data as per the simple layout Table : " +simpleDataTable);
				List<ParentalPolicy> parentalPolicyDatas=ChildAccountUtility.prepareDayAndTimeSlots(request);
				int i=childAccountDataManager.saveAccessControlPolicy(parentalPolicyDatas, (String)session.getAttribute("subscriberIdentity"));
				Logger.logTrace(MODULE, "  DATA SAVED SUCCESSFULLY 1 : " +i);				
				ActionMessage message = new ActionMessage("ssp.polices.added");
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveMessages(request,messages);
	            request.setAttribute("responseUrl","/childAccountManage.do");	            
	            return mapping.findForward(SUCCESS);
	            	            							
			}
			
			Logger.logTrace(MODULE, "  Code to fetch data from database/webservices and display accordingly in the simple/color table  ");			
			AddOnPackage[] promotionalData=childAccountDataManager.getParentalAddOns(subscriberProfileData);
			List<ParentalPolicy> parentalPolicyData=childAccountDataManager.getParentalPolicy(subscriberProfileData,(String)session.getAttribute("subscriberIdentity"));			
			Logger.logTrace(MODULE, "  subscriberIdentity -> " +(String)session.getAttribute("subscriberIdentity"));
			childAccountManageForm.setPromotionalData(promotionalData);
			childAccountManageForm.setParentalPolicyData(parentalPolicyData);			
			Logger.logTrace(MODULE, "  parameter set in childAccountManageForm");
			request.setAttribute("childAccountManageForm", childAccountManageForm);
			request.setAttribute("parentalPolicyData", parentalPolicyData);
			request.setAttribute("promotionalData", promotionalData);							
		
			return mapping.findForward(CHILD_ACCOUNT_MANAGE);		
			
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
