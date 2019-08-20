package com.elitecore.elitesm.web.servicepolicy.diameter.creditcontrolpolicy; 
  
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
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servicepolicy.diameter.creditcontrolpolicy.forms.SearchCreditControlPolicyForm;
                                                                               
public class InitSearchCreditControlPolicyAction extends BaseWebAction { 
	                                                                       
	private static final String FAILURE_FORWARD = "failure";               
	private static final String MODULE ="CREDITCONTROLPOLICY";
	private static final String ACTION_ALIAS=ConfigConstant.SEARCH_CREDIT_CONTROL_SERVICE_POLICY;
	private static final String INITSEARCHCREDITCONTROLPOLICY = "initSearchCreditControlPolicy"; 
 
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{ 
		try {
			 
			 checkActionPermission(request,ACTION_ALIAS);
			 Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName());
			 SearchCreditControlPolicyForm ccForm = (SearchCreditControlPolicyForm)form;
			 IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			 ccForm.setAction("");
			// ccForm.setStatus("All");
			 request.setAttribute("searchCcpolicyForm", ccForm);
			 doAuditing(staffData, ACTION_ALIAS);
			 return mapping.findForward(INITSEARCHCREDITCONTROLPOLICY);             
		
		}catch(ActionNotPermitedException e){
			Logger.logError(MODULE,"Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}catch (Exception authExp) {                                                                                           
		    Logger.logError(MODULE, "Error during Data Manager operation , reason :" + authExp.getMessage());                              
		    Logger.logTrace(MODULE, authExp);                                                                                               
		    ActionMessage message = new ActionMessage("~~~~key~~~");                                                         
		    ActionMessages messages = new ActionMessages();                                                                                 
		    messages.add("information", message);                                                                                           
		    saveErrors(request, messages);                                                                                                  
		}                                                                                                                                   
		return mapping.findForward(FAILURE_FORWARD); 
	}	
}
