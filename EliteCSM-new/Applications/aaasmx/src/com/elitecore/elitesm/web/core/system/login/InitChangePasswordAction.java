package com.elitecore.elitesm.web.core.system.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.core.system.staff.StaffBLManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.base.BaseSystemAction;
import com.elitecore.elitesm.web.core.system.login.forms.ChangePasswordForm;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;


public class InitChangePasswordAction extends BaseSystemAction{

	private static final String MODULE = "CHANGE PASSWORD";
	private static final String CHANGE_PASSWORD = "changePassword";
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";	
	
	public ActionForward execute( ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws Exception 
	{
	        Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName());
        	try{
			ChangePasswordForm changePasswordForm=(ChangePasswordForm)form;
			StaffBLManager staffBLManager = new StaffBLManager();
			SystemLoginForm loginForm = (SystemLoginForm)request.getSession().getAttribute("radiusLoginForm");
			
			if(changePasswordForm.getAction() == null || changePasswordForm.getAction().equalsIgnoreCase("view") || changePasswordForm.getAction().equalsIgnoreCase("changepwd")){
			  IStaffData staffData = staffBLManager.getStaffData(loginForm.getUserId());
			  changePasswordForm.setUserId(loginForm.getUserId()); 
			  changePasswordForm.setUserName(loginForm.getSystemUserName());			  
			  changePasswordForm.setPassword(loginForm.getPassword());
			  request.getSession().setAttribute("staffData", staffData);
			  return mapping.findForward(CHANGE_PASSWORD);
			}
			ActionMessage message = new ActionMessage("login.user.changepassword.success");
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			saveMessages(request,messages);
			return mapping.findForward(SUCCESS_FORWARD);
		}catch(Exception e){
	        Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
	        Logger.logTrace(MODULE,e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
	        ActionMessages messages = new ActionMessages();
	        ActionMessage message1 = new ActionMessage("login.user.changepassword.failure");
	        messages.add("information",message1);
	        saveErrors(request,messages);
	   	}
        return mapping.findForward(FAILURE_FORWARD);
	}
}
