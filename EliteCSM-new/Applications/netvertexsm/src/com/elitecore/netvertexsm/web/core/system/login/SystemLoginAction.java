package com.elitecore.netvertexsm.web.core.system.login;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.sm.acl.GroupData;
import com.elitecore.netvertexsm.blmanager.core.system.group.GroupDataBLManager;
import com.elitecore.netvertexsm.blmanager.core.system.login.LoginBLManager;
import com.elitecore.netvertexsm.blmanager.core.system.staff.StaffBLManager;
import com.elitecore.netvertexsm.blmanager.core.system.systemparameter.PasswordSelectionPolicyBLManager;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.system.systemparameter.data.PasswordPolicyConfigData;
import com.elitecore.netvertexsm.util.constants.ServermgrConstant;
import com.elitecore.netvertexsm.util.constants.SystemLoginConstants;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.system.base.BaseSystemAction;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.netvertexsm.web.core.system.profilemanagement.ProfileManager;

import org.apache.struts.action.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SystemLoginAction extends BaseSystemAction {

	private static final String MODULE = "LOGIN ACTION";
	private static final String FAILURE = "failure";

    private static final String TRUE = "true";
    private static final String FALSE = "false";

	private static final String ADMIN = "admin";


    public ActionForward execute(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response)
	throws Exception {
    	 
		Logger.logTrace(MODULE, "Enter execute method of :"+ getClass().getName());
		String strReturnPath = "success";
		String strFirstLoginPath ="/serverGroupManagement.do?method=initSearch&isMenuCall=yes";
		SystemLoginForm radiusLoginForm = (SystemLoginForm) form;
        String changePwdOnFirstLogin = (String) request.getSession().getAttribute(SystemLoginConstants.CHANGEPWDONFIRSTLOGIN);
		if(Strings.isNullOrBlank(changePwdOnFirstLogin)==true){
			PasswordSelectionPolicyBLManager passwordBlManager  = new PasswordSelectionPolicyBLManager();
			PasswordPolicyConfigData passwordPolicySelectionData=passwordBlManager.getPasswordSelectionPolicy();
			changePwdOnFirstLogin = passwordPolicySelectionData.getChangePwdOnFirstLogin();
		}
		try
		{
			long startTime = System.currentTimeMillis();
			LoginBLManager loginBLManager = new LoginBLManager();
			StaffBLManager staffBLManager = new StaffBLManager();
			HttpSession session = request.getSession();
			//String userId = loginBLManager.validateLogin(radiusLoginForm.getUserName(), radiusLoginForm.getPassword());
			IStaffData staffData = staffBLManager.validateLoginUser(radiusLoginForm.getUserName(), radiusLoginForm.getPassword());
			if (staffData != null) {
				String userId = String.valueOf(staffData.getStaffId());
				radiusLoginForm.setUserId(userId);
				HashMap userControlActionMap = staffBLManager.getUserControlAction(radiusLoginForm.getUserId());
				HashMap modelMap = (HashMap) userControlActionMap.get("modelMap");
				session.setAttribute("modelMap", modelMap);
				ProfileManager.setActionMap(userControlActionMap);
				Set<String> actionaliasSet = staffBLManager.getActionAliasSets(userId);
				session.setAttribute("__action_Alias_Set_", actionaliasSet);
				//IStaffData staffData = staffBLManager.getStaffData(userId);
                if (staffData.getPasswordChangeDate() == null) {
                    if (TRUE.equalsIgnoreCase(changePwdOnFirstLogin) ||
                            (FALSE.equalsIgnoreCase(changePwdOnFirstLogin) && staffData.getLastLoginTime() == null)) {
						staffBLManager.updateLoginInfo(userId);
                        strFirstLoginPath = "/initChangePassword.do";
                    }
                }
				staffBLManager.updateLoginInfo(userId);
				strReturnPath = "loginHome";
				Timestamp systemLoginTime = getCurrentTimeStemp();
				Timestamp passwordChangeDate = staffData.getPasswordChangeDate();
				session.setAttribute("passwordChangeDate", passwordChangeDate);
				session.setAttribute("strFirstLoginPath",strFirstLoginPath);
				session.setAttribute("radiusLoginForm", radiusLoginForm);
				session.setAttribute("systemLoginTime",systemLoginTime );
				
				session.setAttribute(ServermgrConstant.STAFFGROUPSBELONGINGROLES,staffBLManager.getGroupIdVsRoleMap(staffData));

				setSSOParameters(request, radiusLoginForm.getUserName(), radiusLoginForm.getPassword());
				
				Set<GroupData> staffGroups = Collectionz.newHashSet();
				if(staffData.getUserName().equals(ADMIN)){
					GroupDataBLManager groupDataBLManager = GroupDataBLManager.getInstance();
					staffGroups.addAll(groupDataBLManager.getGroupDatas());
				}else{
					staffGroups.addAll(staffData.getGroupDatas());
				}
				Map<String,GroupData> groupMap = new HashMap<String,GroupData>();
				StringBuilder staffBelongingGroups =  new StringBuilder();				
				for(GroupData groupData : staffGroups){
				    groupMap.put(groupData.getId(), groupData);
				    String groupId = groupData.getId();				    
				    staffBelongingGroups.append(groupId).append(",");				    
				}
				
				if(staffBelongingGroups.toString().contains(",")){				    				    				   
				    staffBelongingGroups = staffBelongingGroups.deleteCharAt(staffBelongingGroups.lastIndexOf(","));
				}				
				
				session.setAttribute(ServermgrConstant.STAFF_BELONGING_GROUP_IDS, staffBelongingGroups.toString());
				session.setAttribute(ServermgrConstant.STAFF_BELONGING_GROUPS, staffGroups);
				
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

	public static void setSSOParameters(HttpServletRequest request,String username, String password){
		Logger.logDebug(MODULE, "Method called setSSOParameters()");

		ServletContext smContext = ((HttpServletRequest) request).getSession().getServletContext();
		String pdContextPath = smContext.getInitParameter("pdContextPath");

		String pdSSOURL  = null;
		String smSSOURL  = null;

		if( Strings.isNullOrBlank(pdContextPath) == false ){
			pdSSOURL = "http://"+request.getServerName()+":"+request.getServerPort()+"/"+pdContextPath+"/commons/login/Login/ssoLogin";

			ServletContext pdContext = ((HttpServletRequest) request).getSession().getServletContext().getContext("/"+pdContextPath);
			if(pdContext!=null){
				String smContextPath = smContext.getContextPath();
				smSSOURL  = "http://"+request.getServerName()+":"+request.getServerPort()+smContextPath+"/SSOLogin";

				pdContext.setAttribute(ServermgrConstant.SSO_USERNAME, username);
				pdContext.setAttribute(ServermgrConstant.SSO_PASSWORD, password);
				pdContext.setAttribute("smSSOURL", smSSOURL);
				String  smContextPathFromCtxInit = (String) request.getAttribute("smContexPathFromCtxInit");
				if(Strings.isNullOrBlank(smContextPathFromCtxInit)) {
					pdContext.setAttribute("smContexPathFromCtxInit", smContextPath);
				}
				Logger.logDebug(MODULE, "Attributes successfully set in PolicyDesigner's Context");

			}else{
				Logger.logDebug(MODULE,"PolicyDesigner's Context is Null");
			}

			if(smContext!=null){
				smContext.setAttribute(ServermgrConstant.SSO_USERNAME, username);
				smContext.setAttribute(ServermgrConstant.SSO_PASSWORD, password);
				smContext.setAttribute("pdSSOURL", pdSSOURL);
				Logger.logDebug(MODULE, "Attributes successfully set in ServerManager's Context");
			}

		}else{
			Logger.logDebug(MODULE, "PolicyDesigner's Context Path is not configured");
		}

		Logger.logDebug(MODULE, "PD-SSO-URL: "+pdSSOURL);
		Logger.logDebug(MODULE, "SM-SSO-URL: "+smSSOURL);

	}
}
