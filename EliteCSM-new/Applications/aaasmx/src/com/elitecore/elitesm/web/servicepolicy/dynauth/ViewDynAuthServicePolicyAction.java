package com.elitecore.elitesm.web.servicepolicy.dynauth;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.servicepolicy.ServicePolicyBLManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servicepolicy.dynauth.data.DynAuthPolicyInstData;
import com.elitecore.elitesm.datamanager.servicepolicy.dynauth.data.DynaAuthPolicyESIRelData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servicepolicy.dynauth.forms.ViewDynAuthServicePolicyForm;

public class ViewDynAuthServicePolicyAction extends BaseWebAction{
	private static final String VIEW_FORWARD = "viewDynAuthPolicy";
	private static final String FAILURE_FORWARD = "failure";
	private static final String MODULE="ViewDynAuthServicePolicy";
	private static final String ACTION_ALIAS=ConfigConstant.VIEW_DYNAUTH_POLICY;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		if((checkAccess(request, ACTION_ALIAS))){		
			Logger.logTrace(MODULE,"Enter execute method of"+getClass().getName());
			try{
				ViewDynAuthServicePolicyForm viewDynAuthSerivicePolicyForm = (ViewDynAuthServicePolicyForm)form;

				ServicePolicyBLManager servicePolicyBLManager = new ServicePolicyBLManager();
				
				String strDynAuthPolicyID = request.getParameter("dynauthpolicyid");
				String dynAuthPolicyID;
				
				if(strDynAuthPolicyID != null){
					dynAuthPolicyID = strDynAuthPolicyID;
				}else{
					dynAuthPolicyID=viewDynAuthSerivicePolicyForm.getDynAuthPolicyId();
				}
				
				if( Strings.isNullOrBlank(dynAuthPolicyID) == false){
					DynAuthPolicyInstData dynAuthPolicyInstData = new DynAuthPolicyInstData();
					
					IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
					String actionAlias = ACTION_ALIAS;
					
					dynAuthPolicyInstData = servicePolicyBLManager.getDynAuthPolicyInstData(dynAuthPolicyID);
					doAuditing(staffData, actionAlias);
					request.setAttribute("dynAuthPolicyInstData",dynAuthPolicyInstData);
	
				}
				
				return mapping.findForward(VIEW_FORWARD);
			}catch(Exception e){
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
			}
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
