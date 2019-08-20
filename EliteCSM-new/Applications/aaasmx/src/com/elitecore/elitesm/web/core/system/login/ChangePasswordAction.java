package com.elitecore.elitesm.web.core.system.login;

import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.core.system.staff.StaffBLManager;
import com.elitecore.elitesm.blmanager.core.system.systemparameter.PasswordSelectionPolicyBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.InvalidPasswordException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.systemparameter.data.PasswordPolicyConfigData;
import com.elitecore.elitesm.util.PasswordUtility;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.base.BaseSystemAction;
import com.elitecore.elitesm.web.core.system.login.forms.ChangePasswordForm;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.passwordutil.PasswordEncryption;


public class ChangePasswordAction extends BaseSystemAction{

	private static final String MODULE = "CHANGE PASSWORD";
	private static final String CHANGE_PASSWORD = "changePassword";
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";	
	private static final String ACTION_ALIAS    = ConfigConstant.UPDATE_STAFF_PASSWORD;
	
	public ActionForward execute( ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws Exception 
	{
        Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName());
        StaffBLManager staffBLManager = new StaffBLManager();
        	try{
        		ChangePasswordForm changePasswordForm=(ChangePasswordForm)form;
        		SystemLoginForm loginForm = (SystemLoginForm)request.getSession().getAttribute("radiusLoginForm");

        		if(changePasswordForm.getAction() == null || changePasswordForm.getAction().equalsIgnoreCase("view")){
        			changePasswordForm.setUserId(loginForm.getUserId()); 
        			changePasswordForm.setUserName(loginForm.getSystemUserName());			  
        			changePasswordForm.setPassword(loginForm.getPassword());
        			return mapping.findForward(CHANGE_PASSWORD);
        		}else if(changePasswordForm.getAction().equalsIgnoreCase("change")){
        			PasswordSelectionPolicyBLManager passwordBLManager=new PasswordSelectionPolicyBLManager();
    				PasswordPolicyConfigData passwordPolicySelectionData=passwordBLManager.getPasswordSelectionPolicy();
    				
        			String passwordValue=changePasswordForm.getNewPassword();
        			try{
        				//check for valid password policy or not
        				PasswordUtility.validatePassword(passwordValue, passwordPolicySelectionData);
        				
        				/* Encrypt User password */
        				String encryptedOldPassword = PasswordEncryption.getInstance().crypt(changePasswordForm.getOldPassword(), PasswordEncryption.ELITE_PASSWORD_CRYPT);
	        			String encryptedNewPassword = PasswordEncryption.getInstance().crypt(changePasswordForm.getNewPassword(), PasswordEncryption.ELITE_PASSWORD_CRYPT);
	
	        			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
	        			String actionAlias = ACTION_ALIAS;  
	        			staffData.setLastChangePasswordDate(getCurrentTimeStemp());
	        			staffBLManager.changePassword(changePasswordForm.getUserId(),encryptedOldPassword,encryptedNewPassword,actionAlias);
	        			loginForm.setPassword(changePasswordForm.getNewPassword());
	        			Timestamp lastChangePasswordDate = staffData.getLastChangePasswordDate();
	        			request.getSession().setAttribute("lastChangePasswordDate",lastChangePasswordDate);
	        			request.getSession().setAttribute("radiusLoginForm",loginForm);
        			}catch(InvalidPasswordException in){
        				Logger.logError(MODULE, "Error during Data Manager operation ");
    					ActionMessage message = new ActionMessage("systemparameter.pwdpolicy.failure");
    					ActionMessages messages = new ActionMessages();
    					messages.add("information", message);
    					
    					message=new ActionMessage(in.getMessage(),in.getSourceField());
						messages.add("passwordlength", message);
    					
    					saveErrors(request, messages);
    					return mapping.findForward(INVALID_ACCESS_FORWARD);
        			}
        		}
        		request.setAttribute("responseUrl","/listNetServerInstance.do");
        		ActionMessage message = new ActionMessage("login.user.changepassword.success");
        		ActionMessages messages = new ActionMessages();
        		messages.add("information",message);
        		saveMessages(request,messages);
        		return mapping.findForward(SUCCESS_FORWARD);
        	}catch(Exception e){
        		Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
        		
        		ActionMessage message1;
        		if(e.getMessage().equals("Data Manager implementation not found for Old Password(DB) and Old Passwords(GUI) Do not match")){
        			message1 = new ActionMessage("login.user.changepassword.notmatch");
        		} else if(e.getMessage().equals("Data Manager implementation not found for Do not enter any historical password")){
        			message1 = new ActionMessage("login.user.changepassword.historyPassword",staffBLManager.maxHistoricalPassword());
        		} else{
        			message1 = new ActionMessage("login.user.changepassword.failure");
        		}
        		
        		Logger.logTrace(MODULE,e);
        		Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
        		request.setAttribute("errorDetails", errorElements);
        		ActionMessages messages = new ActionMessages();
        		messages.add("information",message1);
        		saveErrors(request,messages);

        		return mapping.findForward(FAILURE_FORWARD);
        	}
	}
}
