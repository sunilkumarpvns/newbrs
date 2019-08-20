package com.elitecore.netvertexsm.web.core.system.systemparameter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.core.system.systemparameter.PasswordSelectionPolicyBLManager;
import com.elitecore.netvertexsm.blmanager.core.system.systemparameter.SystemParameterBLManager;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.system.systemparameter.data.ISystemParameterData;
import com.elitecore.netvertexsm.datamanager.core.system.systemparameter.data.PasswordPolicyConfigData;
import com.elitecore.netvertexsm.datamanager.core.system.systemparameter.data.SystemParameterData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.cache.ConfigManager;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.netvertexsm.web.core.system.systemparameter.forms.ViewSystemParameterForm;


public class ViewSystemParameterAction extends BaseWebAction{
	private static final String VIEW_FORWARD = "viewSystemParameter";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.VIEW_SYSTEM_PARAMETERS_ACTION;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{

		if(checkAccess(request, ACTION_ALIAS)){
			Logger.logTrace(MODULE,"Enter execute method of"+getClass().getName());
			ActionErrors errors = new ActionErrors();
			SystemParameterBLManager blManager = new SystemParameterBLManager();
			PasswordSelectionPolicyBLManager passwordBLManager=new PasswordSelectionPolicyBLManager();
			try{
				ISystemParameterData systemParameterData = new SystemParameterData();
				ViewSystemParameterForm viewSystemParameterForm = (ViewSystemParameterForm)form;
				String action = viewSystemParameterForm.getAction();
				if(action != null && action.equalsIgnoreCase("refresh")){
					ConfigManager.refreshSystemParameter();
				}
				
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				String actionAlias = ACTION_ALIAS;
				List lstParameterValue = blManager.getList(staffData,actionAlias);
				PasswordPolicyConfigData passwordPolicySelectionData=passwordBLManager.viewPasswordSelectionPolicy();
				viewSystemParameterForm.setPasswordSelectionConfigData(passwordPolicySelectionData);
				viewSystemParameterForm.setLstParameterValue(lstParameterValue);
				viewSystemParameterForm.setAction("listTable");
				return mapping.findForward(VIEW_FORWARD);
				
			}
			catch(Exception e){
				Logger.logTrace(MODULE,e.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
			} 
			
			errors.add("fatal", new ActionError("systemparameter.view.failure")); 
			saveErrors(request,errors);
			return mapping.findForward(FAILURE_FORWARD);
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
