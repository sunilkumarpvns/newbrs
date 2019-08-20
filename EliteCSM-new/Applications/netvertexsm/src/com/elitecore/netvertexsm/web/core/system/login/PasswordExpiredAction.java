package com.elitecore.netvertexsm.web.core.system.login;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.core.system.login.LoginBLManager;
import com.elitecore.netvertexsm.blmanager.core.system.staff.StaffBLManager;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.system.base.BaseSystemAction;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.netvertexsm.web.core.system.profilemanagement.ProfileManager;

public class PasswordExpiredAction extends BaseSystemAction {

	private static final String MODULE = "PASSWORD EXPIRED ACTION";
	private static final String FAILURE = "failure";

	public ActionForward execute(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response)
	throws Exception {
		Logger.logTrace(MODULE, "Enter execute method of :"+ getClass().getName());
		String strReturnPath = "success";
		String strFirstLoginPath ="/serverGroupManagement.do?method=initSearch";
		SystemLoginForm radiusLoginForm = (SystemLoginForm) form;

		try
		{
			
			long startTime = System.currentTimeMillis();
			LoginBLManager loginBLManager = new LoginBLManager();
			StaffBLManager staffBLManager = new StaffBLManager();
			HttpSession session = request.getSession();
			String userId = loginBLManager.validateLogin(radiusLoginForm.getUserName(), radiusLoginForm.getPassword());
			if (userId != null) {
				radiusLoginForm.setUserId(userId);

				HashMap userControlActionMap = staffBLManager.getUserControlAction(radiusLoginForm.getUserId());
				HashMap modelMap = (HashMap) userControlActionMap.get("modelMap");
				session.setAttribute("modelMap", modelMap);
				ProfileManager.setActionMap(userControlActionMap);

				Set<String> actionaliasSet = staffBLManager.getActionAliasSets(userId);
				session.setAttribute("__action_Alias_Set_", actionaliasSet);
				IStaffData staffData = staffBLManager.getStaffData(userId);				 				
				strReturnPath = "changePassword";						
				Timestamp systemLoginTime = getCurrentTimeStemp();
				Timestamp passwordChangeDate = staffData.getPasswordChangeDate();
				session.setAttribute("passwordChangeDate", passwordChangeDate);
				session.setAttribute("strFirstLoginPath",strFirstLoginPath);
				session.setAttribute("radiusLoginForm", radiusLoginForm);
				session.setAttribute("systemLoginTime",systemLoginTime );
				
				SystemLoginAction.setSSOParameters(request, null, null);
			} else {
				Logger.logWarn(MODULE, "Login Failed.");
				request.setAttribute("errorCode", "LOGINFAILED");
				strReturnPath = "loginFailed";
			}
			Logger.logDebug(MODULE, "Total login time:"+(System.currentTimeMillis()-startTime));
		}catch(DataManagerException dme){
			Logger.logError(MODULE, " Error in login action :" +dme);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(dme);
			request.setAttribute("errorDetails", errorElements);
			if(dme.getMessage()!=null && dme.getMessage().equalsIgnoreCase("Cannot open connection")){
				strReturnPath = FAILURE;
				ActionMessage message = new ActionMessage("datasource.exception");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);

			}else{
				strReturnPath = FAILURE;
				ActionMessage message = new ActionMessage("general.error");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
			}

		}catch(Exception e){
			Logger.logError(MODULE, " Error in login action :" +e);
			strReturnPath = FAILURE;
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		
		return mapping.findForward(strReturnPath);
	}

}
