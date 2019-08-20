package com.elitecore.elitesm.web.servicepolicy.rm.cgpolicy;

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
import com.elitecore.elitesm.web.servicepolicy.rm.cgpolicy.forms.SearchCGPolicyForm;

public class InitSearchCGPolicyAction extends BaseWebAction {
	private static final String MODULE ="CGPOLICY";
	private static final String FAILURE_FORWARD = "failure";               
	private static final String INITSEARCHCGPOLICY = "initSearchCGPolicy"; 
	private static final String ACTION_ALIAS=ConfigConstant.SEARCH_CG_POLICY;
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{ 
		ActionMessages messages = new ActionMessages();
		try {
			checkActionPermission(request,ACTION_ALIAS);
			Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName());
			SearchCGPolicyForm searchCGPolicyForm = (SearchCGPolicyForm)form;
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			
			searchCGPolicyForm.setAction("");
			searchCGPolicyForm.setStatus("All");
			doAuditing(staffData, ACTION_ALIAS);
			request.setAttribute("searchCGPolicyForm", searchCGPolicyForm);
			return mapping.findForward(INITSEARCHCGPOLICY);             

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
