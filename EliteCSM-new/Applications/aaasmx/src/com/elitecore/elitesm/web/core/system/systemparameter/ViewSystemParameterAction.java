package com.elitecore.elitesm.web.core.system.systemparameter;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.core.system.systemparameter.PasswordSelectionPolicyBLManager;
import com.elitecore.elitesm.blmanager.core.system.systemparameter.SystemParameterBLManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.systemparameter.data.ISystemParameterData;
import com.elitecore.elitesm.datamanager.core.system.systemparameter.data.PasswordPolicyConfigData;
import com.elitecore.elitesm.datamanager.core.system.systemparameter.data.SystemParameterData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.core.system.systemparameter.forms.ViewSystemParameterForm;
import com.elitecore.elitesm.web.systemstartup.defaultsetup.controller.CaseSensitivity;
import org.apache.struts.action.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ViewSystemParameterAction extends BaseWebAction{
	private static final String VIEW_FORWARD = "viewSystemParameter";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.LIST_SYSTEM_PARAMETER_ACTION;
	@Override
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{

		if(checkAccess(request, ACTION_ALIAS)) {
			Logger.logTrace(MODULE,"Enter execute method of"+getClass().getName());
			ActionErrors errors = new ActionErrors();
			SystemParameterBLManager blManager = new SystemParameterBLManager();
			PasswordSelectionPolicyBLManager passwordBLManager=new PasswordSelectionPolicyBLManager();
			try{
				ViewSystemParameterForm viewSystemParameterForm = (ViewSystemParameterForm)form;
				String action = viewSystemParameterForm.getAction();
				if(action != null && action.equalsIgnoreCase("refresh")){
					ConfigManager.refreshSystemParameter();
				}
				
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				String actionAlias = ACTION_ALIAS;
				List<SystemParameterData> lstParameterValue = blManager.getParameterList();

				for (ISystemParameterData parameter:lstParameterValue){
					if(CaseSensitivity.POLICY_CASESENSITIVITY.equalsIgnoreCase(parameter.getAlias())){
						parameter.setValue(convertIdToName(parameter.getValue()));
					}

					if(CaseSensitivity.SUBSCRIBER_CASESENSITIVITY.equalsIgnoreCase(parameter.getAlias())){
						parameter.setValue(convertIdToName(parameter.getValue()));
					}
				}
				PasswordPolicyConfigData passwordPolicySelectionData=passwordBLManager.getPasswordSelectionPolicy();
				doAuditing(staffData, actionAlias);
				viewSystemParameterForm.setLstParameterValue(lstParameterValue);
				viewSystemParameterForm.setPasswordSelectionConfigData(passwordPolicySelectionData);
				viewSystemParameterForm.setAction("listTable");
				return mapping.findForward(VIEW_FORWARD);
				
			}
			catch(Exception e){
				Logger.logTrace(MODULE,e.getMessage());
				Object[] errorElements = EliteExceptionUtils.getFullStackTraceAsArray(e);
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

	private String convertIdToName(String paramId) {
		String paramVal = null;
		if(Strings.isNullOrBlank(CaseSensitivity.getCaseValueFromId(paramId)) == false){
			paramVal = CaseSensitivity.getCaseValueFromId(paramId);
		}
		return paramVal;
	}
}
