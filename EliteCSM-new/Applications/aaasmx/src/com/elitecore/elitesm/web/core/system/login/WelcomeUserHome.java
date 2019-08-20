package com.elitecore.elitesm.web.core.system.login;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.elitecore.elitesm.blmanager.core.system.login.LoginBLManager;
import com.elitecore.elitesm.blmanager.core.system.staff.StaffBLManager;
import com.elitecore.elitesm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServerTypeData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.base.BaseSystemAction;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.core.system.profilemanagement.ProfileManager;

public class WelcomeUserHome  extends BaseSystemAction {
	private static final String MODULE = "WelcomeUserHome";
	private static final String FAILURE = "failure";
	private static final String USER_HOME_FORWARD = "loginHome";
	private static int idleIntervalForSession=0;
	public ActionForward execute(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response)throws Exception {
		Logger.logTrace(MODULE, "Enter execute method of :"+ getClass().getName());
		
		String strReturnToStarupPage = "loginFailedInitFailed";
		
		if(ConfigManager.isInitCompleted() == false){
			return mapping.findForward(strReturnToStarupPage);
		}
		
		SystemLoginForm radiusLoginForm = (SystemLoginForm)request.getSession().getAttribute("radiusLoginForm");
		Boolean isUserLoggedIn = (Boolean)request.getSession().getAttribute("userLoggedIn");
		LoginBLManager loginBLManager = new LoginBLManager();
		StaffBLManager staffBLManager = new StaffBLManager();
		HttpSession session = request.getSession();
		String strReturnPath = "loginFailed";
		idleIntervalForSession = Integer.parseInt(ConfigManager.get(ConfigConstant.SESSION_IDLE_TIME));
		
		if( isUserLoggedIn != null ){
			
			String userId = loginBLManager.validateLogin(radiusLoginForm.getSystemUserName(), radiusLoginForm.getPassword());
			if (userId != null) {
				 radiusLoginForm.setUserId(userId);
				
				 if(session.getMaxInactiveInterval() < 0){
					 session.setMaxInactiveInterval(900);
				 }
				 
				 HashMap userControlActionMap = staffBLManager.getUserControlAction(radiusLoginForm.getUserId());
				 HashMap modelMap = (HashMap) userControlActionMap.get("modelMap");
				 session.setAttribute("modelMap", modelMap);
				 ProfileManager.setActionMap(userControlActionMap);

				 Set<String> actionaliasSet = staffBLManager.getActionAliasSets(userId);
				 session.setAttribute("__action_Alias_Set_", actionaliasSet);
				 session.setAttribute("radiusLoginForm", radiusLoginForm);
				 request.setAttribute("enabledCaptcha", null);
				 licenseExpiryValidation(request);
				 
				 IStaffData staffData = staffBLManager.getStaffData(userId);
				 Timestamp lastChangePasswordDate = staffData.getLastChangePasswordDate();
				 request.getSession().setAttribute("lastChangePasswordDate", lastChangePasswordDate);
				 
				 session.setAttribute("userLoggedIn", true);
				 session.setMaxInactiveInterval(idleIntervalForSession);
				 
				 return mapping.findForward(USER_HOME_FORWARD);
			}
		}
		return mapping.findForward(strReturnPath);
	}
	
	public void licenseExpiryValidation(HttpServletRequest request) {
		boolean bLicenseAlert = false;
		boolean bLicenseAlertForPopup = false;
		boolean bLicenseExpireAlert = false;
		NetServerBLManager netServerBLManager = new NetServerBLManager();
		try{
			List<NetServerInstanceData> netServerListForLicense = netServerBLManager.getNetServerInstanceListForLicense();
			List<NetServerTypeData> netServerTypeList = netServerBLManager.getNetServerTypeList();

			if(netServerListForLicense != null) {
				for(NetServerInstanceData netServerInstanceData : netServerListForLicense) {
					Integer licenseExpireDays = 0;
					if(netServerInstanceData != null) {
						licenseExpireDays = netServerInstanceData.getLicenseExpiryDays();
					}
					if(licenseExpireDays != null) {
						if(licenseExpireDays > 0 && licenseExpireDays <= 30)
							bLicenseAlert = true;
						if(licenseExpireDays < 3)
							bLicenseAlertForPopup = true;
						if(licenseExpireDays < 0)
							bLicenseExpireAlert = true;
					}
				}
			}

			request.getSession().setAttribute("bLicenseAlert", String.valueOf(bLicenseAlert));
			request.getSession().setAttribute("bLicenseAlertForPopup", String.valueOf(bLicenseAlertForPopup));
			request.getSession().setAttribute("bLicenseExpireAlert", String.valueOf(bLicenseExpireAlert));
		}catch (DataManagerException hExp) {
			Logger.logError(MODULE,"Error during data Manager operation, reason : "+hExp.getMessage());
			Logger.logTrace(MODULE,hExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(hExp);
			request.setAttribute("errorDetails", errorElements);
		}catch (Exception hExp) {
			Logger.logError(MODULE,"Error during data Manager operation, reason : "+hExp.getMessage());
			Logger.logTrace(MODULE,hExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(hExp);
			request.setAttribute("errorDetails", errorElements);
		}
	}
}
