package com.elitecore.ssp.web.parentalcontrol;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.ws.cxfws.ssp.parental.PolicyGroup;
import com.elitecore.ssp.subscriber.SubscriberProfile;
import com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.UsageMonotoringInformation;
import com.elitecore.ssp.util.constants.RequestAttributeKeyConstant;
import com.elitecore.ssp.util.constants.SessionAttributeKeyConstant;
import com.elitecore.ssp.util.exception.DataManagerException;
import com.elitecore.ssp.util.logger.Logger;
import com.elitecore.ssp.web.core.base.BaseWebAction;
import com.elitecore.ssp.web.home.forms.HomeForm;
import com.elitecore.ssp.web.parentalcontrol.forms.ChildAccountInfoForm;
import com.elitecore.ssp.webservice.parentalcontrol.ChildAccountDataManager;

public class EnterpriseMemberAcctInfoAction extends BaseWebAction {
	private static final String MODULE = ChildAccountInfoAction.class.getSimpleName();
	private static final String ENTERPRISE_MEMBER_ACCOUNT_INFO_PAGE="enterpriseMemberAccountInfoSuccess";
	private static final String VIEW_ACCOUNT_INFO_PAGE="childAccountInfoSuccess";
	
	public ActionForward execute(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws DataManagerException {		
					
		Logger.logTrace(MODULE, " entered into execute method of : "+ getClass().getName());
		ChildAccountDataManager childAccountDataManager=new ChildAccountDataManager();								
		ChildAccountInfoForm childAccountInfoForm=(ChildAccountInfoForm)form;
		String[] subscriberIdentityArray = request.getParameterValues("select");
		request.setAttribute(RequestAttributeKeyConstant.SELECTED_SUBSCRIBER, subscriberIdentityArray);
		Logger.logTrace(MODULE, "SubscriberIdentityArray : "+ subscriberIdentityArray);
		String selectedLink = request.getParameter(SessionAttributeKeyConstant.SELECTED_LINK);
		Logger.logTrace(MODULE, " selectedLink  : "+ selectedLink );		
		request.getSession().setAttribute(SessionAttributeKeyConstant.SELECTED_LINK,selectedLink);
		HomeForm homeForm = (HomeForm)request.getSession().getAttribute(SessionAttributeKeyConstant.HOME_FORM);
		SubscriberProfile childAccounts[]=null; 
		try{
			List<ChildAccountUsageInfo> childAccountUsageInfoDataList = new ArrayList<ChildAccountUsageInfo>();		  			
	  		childAccounts = homeForm.getChildAccounts();
	  		SubscriberProfile currentUser=(SubscriberProfile)request.getSession().getAttribute(SessionAttributeKeyConstant.TEMP_CURRENT_USER);
	  		SubscriberProfile currentSelectedUser=(SubscriberProfile)request.getSession().getAttribute(SessionAttributeKeyConstant.CHILD_OBJECT);
	  		if(subscriberIdentityArray != null && subscriberIdentityArray.length > 0){
		  		if(childAccounts!=null && childAccounts.length>0){
		  			for(String subscriberId : subscriberIdentityArray){
		  				for(SubscriberProfile childObject:childAccounts){				  		
					  		if(childObject.getSubscriberID().equals(subscriberId)){
					  			long[] allowedUsageInfoForChild = childAccountDataManager.getSubscriberAccountAllowedUsageInfo(childObject);

					  			Logger.logTrace(MODULE, " PASSING OBJECT TO getChildAccountAllowedUsageInfo.");
					  			ChildAccountUsageInfo childAccountUsageInfo = new ChildAccountUsageInfo();
					  			//long[] allowedUsageInfoForChild = childAccountDataManager.getChildAccountAllowedUsageInfo(childObject);
					  			Logger.logInfo(MODULE, " Retrieved usage info of child : "+childObject.getUserName());
					  			childAccountUsageInfo.setAllowedUsageInfo(allowedUsageInfoForChild);
					  			childAccountUsageInfo.setChildObject(childObject);
					  			childAccountUsageInfoDataList.add(childAccountUsageInfo);					  								  							  			 
					  		}else if(currentUser.getSubscriberID().equals(subscriberId)){
					  			long[] allowedUsageInfoForChild = childAccountDataManager.getSubscriberAccountAllowedUsageInfo(currentUser);

					  			Logger.logTrace(MODULE, " PASSING PARENT OBJECT TO getChildAccountAllowedUsageInfo.");
					  			ChildAccountUsageInfo childAccountUsageInfo = new ChildAccountUsageInfo();
					  			//long[] allowedUsageInfoForChild = childAccountDataManager.getChildAccountAllowedUsageInfo(currentUser);
					  			Logger.logInfo(MODULE, " Retrieved usage info of child : "+currentUser.getUserName());
					  			childAccountUsageInfo.setAllowedUsageInfo(allowedUsageInfoForChild);
					  			childAccountUsageInfo.setChildObject(currentUser);
					  			childAccountUsageInfoDataList.add(childAccountUsageInfo);
					  			break;
					  		}
				  		}
				  	}
		  		}
	  		}else if(currentSelectedUser != null){
	  		 
	  			long[] allowedUsageInfoForChild =  childAccountDataManager.getSubscriberAccountAllowedUsageInfo(currentSelectedUser);
	  			
	  			//long[] allowedUsageInfoForChild = childAccountDataManager.getChildAccountAllowedUsageInfo(currentSelectedUser);
	  			Logger.logInfo(MODULE, " Retrieved usage info of child : "+currentSelectedUser.getUserName());
	  			childAccountInfoForm.setAllowedUsageInfo(allowedUsageInfoForChild);
	  			request.getSession().setAttribute(SessionAttributeKeyConstant.CHILD_OBJECT, currentSelectedUser);
	  			request.getSession().setAttribute(SessionAttributeKeyConstant.CHILD_ACCOUNT_INFO_FORM, childAccountInfoForm);
	  			return mapping.findForward(VIEW_ACCOUNT_INFO_PAGE);
	  		}
	  		
	  		request.getSession().setAttribute(SessionAttributeKeyConstant.CHILD_ACCOUNT_INFO_FORM, childAccountInfoForm);
	  		childAccountInfoForm.setChildAccountUsageInfoDataList(childAccountUsageInfoDataList);
		  	return mapping.findForward(ENTERPRISE_MEMBER_ACCOUNT_INFO_PAGE);		
		}catch(Exception e){
			Logger.logTrace(MODULE, e);
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
			return mapping.findForward(FAILURE);
	}
}