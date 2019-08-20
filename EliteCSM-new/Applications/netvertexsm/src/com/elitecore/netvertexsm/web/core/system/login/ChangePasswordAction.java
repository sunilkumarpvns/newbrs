package com.elitecore.netvertexsm.web.core.system.login;

import java.sql.Timestamp;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.core.system.staff.StaffBLManager;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.util.constants.BaseConstant;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.constants.StaffConstant;
import com.elitecore.netvertexsm.util.constants.SystemLoginConstants;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.system.base.BaseSystemAction;
import com.elitecore.netvertexsm.web.core.system.cache.ConfigManager;
import com.elitecore.netvertexsm.web.core.system.login.forms.ChangePasswordForm;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.passwordutil.PasswordEncryption;


public class ChangePasswordAction extends BaseSystemAction{

	private static final String MODULE = "CHANGE PASSWORD";
	private static final String CHANGE_PASSWORD = "changePassword";
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";	
	private static final String ACTION_ALIAS    = ConfigConstant.CHANGE_PASSWORD_ACTION;

	public ActionForward execute( ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws Exception 
	{
		Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName());
		if(checkAccess(request, ACTION_ALIAS)){	
			try{
				ChangePasswordForm changePasswordForm=(ChangePasswordForm)form;
				StaffBLManager staffBLManager = new StaffBLManager();
				SystemLoginForm loginForm = (SystemLoginForm)request.getSession().getAttribute("radiusLoginForm");

				if(changePasswordForm.getAction() == null || changePasswordForm.getAction().equalsIgnoreCase("view")){
					changePasswordForm.setUserId(loginForm.getUserId()); 
					changePasswordForm.setUserName(loginForm.getUserName());			  
					changePasswordForm.setPassword(loginForm.getPassword());
					return mapping.findForward(CHANGE_PASSWORD);
				}else if(changePasswordForm.getAction().equalsIgnoreCase("change")) {

					IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
					staffData.setPasswordChangeDate(new Timestamp(new Date().getDate()));
					String actionAlias = ACTION_ALIAS;   
					staffBLManager.changePassword(Long.parseLong(changePasswordForm.getUserId()),changePasswordForm.getOldPassword(),changePasswordForm.getNewPassword(),staffData,actionAlias);
					loginForm.setPassword(changePasswordForm.getNewPassword());					
					Timestamp passwordChangeDate = staffData.getPasswordChangeDate();
					request.getSession().setAttribute("passwordChangeDate", passwordChangeDate);
					request.getSession().setAttribute(SystemLoginConstants.PASSWORD_EXPIRY_FLAG,"NO");
					request.getSession().setAttribute("radiusLoginForm",loginForm);					
				}
				request.setAttribute("responseUrl","/serverGroupManagement.do?method=initSearch");
				ActionMessage message = new ActionMessage("login.user.changepassword.success");
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveMessages(request,messages);
				
				SystemLoginAction.setSSOParameters(request, changePasswordForm.getUserName(), changePasswordForm.getNewPassword());
				
				return mapping.findForward(SUCCESS_FORWARD);
			}catch(DataManagerException de){
				Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
				Logger.logTrace(MODULE,de);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(de);
				request.setAttribute("errorDetails", errorElements);
				ActionMessages messages = new ActionMessages();
				ActionMessage message1 = new ActionMessage("login.user.changepassword.failure");
				messages.add("information",message1);
				if(de.getMessage().contains(StaffConstant.SAME_OLD_AND_NEW_PASSWORDS)){
					ActionMessage messageReason = new ActionMessage("login.user.changepassword.failure.reason.sameoldandnewpassword");
					messages.add("information", messageReason);
				}
				if(de.getMessage().contains(StaffConstant.SAME_NEW_AND_HISTORICAL_PASSWORD)){
					ActionMessage messageReason = new ActionMessage("login.user.changepassword.failure.reason.samehistoricalpassword",de.getMessage().split(":")[1]);
					messages.add("information", messageReason);
				}
				if(de.getMessage().contains(StaffConstant.OLD_PASSWORD_DB_AND_GUI_DONT_MATCH)){
					ActionMessage messageReason = new ActionMessage("login.user.changepassword.failure.reason.oldpassworddbandguidontmatch");
					messages.add("information", messageReason);
				}
				saveErrors(request,messages);
				return mapping.findForward(FAILURE_FORWARD);

			}catch(Exception e){
				Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessages messages = new ActionMessages();
				ActionMessage message1 = new ActionMessage("login.user.changepassword.failure");
				messages.add("information",message1);
				saveErrors(request,messages);
				return mapping.findForward(FAILURE_FORWARD);
			}  
			
		}else{
				Logger.logError(MODULE, "Error during Data Manager operation ");
				ActionMessage message = new ActionMessage("general.user.restricted");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);

				return mapping.findForward(INVALID_ACCESS_FORWARD);
			}
		}
	}