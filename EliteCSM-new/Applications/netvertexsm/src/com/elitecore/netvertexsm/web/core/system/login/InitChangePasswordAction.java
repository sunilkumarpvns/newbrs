package com.elitecore.netvertexsm.web.core.system.login;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.core.system.staff.StaffBLManager;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.system.base.BaseSystemAction;
import com.elitecore.netvertexsm.web.core.system.login.forms.ChangePasswordForm;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;


public class InitChangePasswordAction extends BaseSystemAction{

	private static final String MODULE = "INIT CHANGE PASSWORD";
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
			IStaffData staffData = null;
			if(changePasswordForm.getAction() == null || changePasswordForm.getAction().equalsIgnoreCase("view")){
			  changePasswordForm.setUserId(loginForm.getUserId()); 
			  changePasswordForm.setUserName(loginForm.getUserName());			  
			  changePasswordForm.setPassword(loginForm.getPassword());
			  List<StaffData> staffList = staffBLManager.getList();
  				changePasswordForm.setStaffList(staffList);
  				staffData = staffBLManager.getStaffData(loginForm.getUserId());
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
