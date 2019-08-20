package com.elitecore.elitesm.web.servicepolicy.diameter.naspolicy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servicepolicy.diameter.naspolicy.forms.SearchNASServicePolicyForm;

public class InitSearchNASServicePolicyAction extends BaseWebAction{

	private static final String SUCCESS_FORWARD = "searchnasservicepolicy";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.SEARCH_NAS_SERVICE_POLICY;

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{


		ActionMessages messages = new ActionMessages();
		try{
			checkActionPermission(request,ACTION_ALIAS);	
			Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());

			SearchNASServicePolicyForm policyForm = (SearchNASServicePolicyForm)form;
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			
			policyForm.setAction("nolist");
			//policyForm.setStatus("All");
			request.setAttribute("policyForm",policyForm);
			doAuditing(staffData, ACTION_ALIAS);
			return mapping.findForward(SUCCESS_FORWARD);
		}catch (ActionNotPermitedException e) {
			Logger.logError(MODULE, "Error during Data Manager operation ");
			Logger.logTrace(MODULE, e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.user.restricted");
			messages.add("information", message);
		}catch (Exception authExp) {                                                                                           
			Logger.logError(MODULE, "Error during Data Manager operation , reason :" + authExp.getMessage());                              
			Logger.logTrace(MODULE, authExp);                                                                                               
			ActionMessage message = new ActionMessage("general.user.restricted");                                                         
			messages.add("information", message);                                                                                           

		}   
		saveErrors(request, messages);
		return mapping.findForward(FAILURE_FORWARD);
	}

}	
