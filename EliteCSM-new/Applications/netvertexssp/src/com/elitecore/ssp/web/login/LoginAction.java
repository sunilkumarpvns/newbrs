package com.elitecore.ssp.web.login;

import java.rmi.RemoteException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;


import com.elitecore.ssp.subscriber.SubscriberProfile;
import com.elitecore.ssp.util.EliteUtility;
//import com.elitecore.netvertexsm.ws.cxfws.subscriberprovisioning.SubscriberProfile;
//import com.elitecore.netvertexsm.ws.cxfws.ssp.parental.SubscriberProfile;
import com.elitecore.ssp.util.constants.SessionAttributeKeyConstant;
import com.elitecore.ssp.util.exception.DataManagerException;
import com.elitecore.ssp.util.logger.Logger;
import com.elitecore.ssp.web.core.base.BaseWebAction;
import com.elitecore.ssp.web.login.forms.LoginForm;
import com.elitecore.ssp.web.subscriber.SubscriberProfilerManager;
import com.elitecore.ssp.webservice.login.LoginDataManager;

public class LoginAction extends BaseWebAction {

	private static final String MODULE = LoginAction.class.getSimpleName();
	private static final String LOGIN_HOME="loginHome";
	private static final String LOGIN_FAILED = "loginFailed";

	public ActionForward execute(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws DataManagerException {
		LoginForm loginForm = (LoginForm) form;
		Logger.logTrace(MODULE, "Entered execute method of " + getClass().getName());
		try{
			
			LoginDataManager loginDataManager = new LoginDataManager();
			SubscriberProfile currentUser = (SubscriberProfile)request.getSession().getAttribute(SessionAttributeKeyConstant.CURRENT_USER);					
			
			Logger.logDebug(MODULE, "Current User: " + currentUser);
			if(currentUser==null){
				Logger.logDebug(MODULE, "Authenticating user : " + currentUser);
			
				List<com.elitecore.netvertexsm.ws.cxfws.subscriberprovisioning.SubscriberProfile> subscriberProfileList = loginDataManager.authenticate(loginForm.getUserName(),null);
				if(subscriberProfileList==null||subscriberProfileList.isEmpty()){
					Logger.logWarn(MODULE, "Login Failed.");
					request.setAttribute("errorCode", "Invalid User Id");
					return mapping.findForward(LOGIN_FAILED);
				}
				currentUser=EliteUtility.getSubscriberProfile(subscriberProfileList.get(0));
				if(!currentUser.getPassword().equals(loginForm.getPassword())){
					Logger.logWarn(MODULE, "Login Failed.");
					request.setAttribute("errorCode", "Invalid User Name or Password");
					return mapping.findForward(LOGIN_FAILED);
				}
				Logger.logDebug(MODULE, " Done : " + currentUser);
			}
			
			if(currentUser!=null){
				Logger.logDebug(MODULE, "Existing User: " + currentUser);
				HttpSession session = request.getSession();
				session.setAttribute(SessionAttributeKeyConstant.CURRENT_USER,currentUser);
				session.setAttribute(SessionAttributeKeyConstant.TEMP_CURRENT_USER,currentUser);
				session.setAttribute(SessionAttributeKeyConstant.CHILD_OBJECT,currentUser);
				session.setAttribute(SessionAttributeKeyConstant.SELECTED_LINK,"acctinfo");
				session.setAttribute(SessionAttributeKeyConstant.SUBSCRIBEPROPERTYFIELD, SubscriberProfilerManager.getSubscriberProfileSearchFilelds());
				return mapping.findForward(LOGIN_HOME);
			}
			
		}catch(DataManagerException e){
			request.setAttribute("errorCode", "Internal Error");
			Logger.logTrace(MODULE, e);
		}catch(RemoteException e){
			if(e.getMessage().equalsIgnoreCase("Account-Expired")){
				request.setAttribute("errorCode", "User Account Expired");
			}else{
				request.setAttribute("errorCode", "Communication Failed With Webservice");
				Logger.logTrace(MODULE, e);
			}
		}catch(Exception e){
			request.setAttribute("errorCode", "Internal Error");
			Logger.logTrace(MODULE, e);
		}
		return mapping.findForward(LOGIN_FAILED);
	}
	
	
	
}
