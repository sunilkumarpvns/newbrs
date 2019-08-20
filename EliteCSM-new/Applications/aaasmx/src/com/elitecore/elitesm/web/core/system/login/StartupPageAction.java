package com.elitecore.elitesm.web.core.system.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.base.BaseSystemAction;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.core.system.startup.EliteSMStartupStatus;
import com.elitecore.elitesm.web.core.system.startup.EliteSMStartupStatus.SM_MODULE_CONSTANTS;
import com.elitecore.elitesm.web.core.system.startup.EliteSMStartupStatus.SM_MODULE_STATUS;

public class StartupPageAction extends BaseSystemAction {
	
	private static final String MODULE = "LOGIN ACTION";
	private static final String STARTUP_SUCCESS = "startup_success";
	private static final String STARTUP_FAILURE = "startup_failure";
	private static boolean preventStartingConfigManagerThread = false;
	private static boolean isFirstTimeSetupParameter = false;
	public ActionForward execute(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response)throws Exception {
		Logger.logTrace(MODULE, "Enter execute method of :"+ getClass().getName());
		
		/*** 
		 *  Check for Initial Process Completed or not
		 *  If it is completed then Redirect to Login.Jsp
		 *  Else Redirect to Startup.jsp 
		 ***/
		
		if (EliteSMStartupStatus.DB_SETUP_COMPLETED == true) {
			 
			String isSetupAlreadyCompleted = (String) request.getAttribute("isSetupAlreadyCompleted");
			if(Strings.isNullOrBlank(isSetupAlreadyCompleted)){
				isSetupAlreadyCompleted = "false";
			}
			
			String isSetupFirstTime = (String) request.getAttribute("isSetupFirstTime");
			if(Strings.isNullOrBlank(isSetupFirstTime)){
				isSetupFirstTime = "false";
			}
			
			if("false".equals(isSetupAlreadyCompleted) && "true".equals(isSetupFirstTime)){
				request.setAttribute("isSetupAlreadyCompleted", "false");
				isFirstTimeSetupParameter = true;
			}else{
				if(isFirstTimeSetupParameter == true){
					request.setAttribute("isSetupAlreadyCompleted", "false");
				}
			}
			
			
			if(preventStartingConfigManagerThread == false){
				
				preventStartingConfigManagerThread = true;
				if(ConfigManager.isInitCompleted() == false){
					new Thread(){
						public void run() {
							ConfigManager.init();
						}
					}.start();
					
					EliteSMStartupStatus.updateStatus(SM_MODULE_CONSTANTS.SMSPC, SM_MODULE_STATUS.SUCCESS);
				}
			}
		}else{
			return mapping.findForward("forwardToDefaultStartup");
		}
		
		if(ConfigManager.isInitCompleted()){
			
			Boolean isUserLoggedIn = (Boolean)request.getSession().getAttribute("userLoggedIn");

			if( isUserLoggedIn != null && isUserLoggedIn.equals(true)){
				return mapping.findForward("login");
			}
			
			String gologin = (String)request.getParameter("loginMode");
			if(Strings.isNullOrBlank(gologin) == false){
				return mapping.findForward("login");
			}
			
			request.setAttribute("isSetupCompleted", "true");
        	return mapping.findForward(STARTUP_SUCCESS);
    	}else{
    		request.setAttribute("isSetupCompleted", "false");
    		return mapping.findForward(STARTUP_FAILURE);
    	}
	}
}
