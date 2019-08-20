package com.elitecore.elitesm.web.rm.concurrentloginpolicy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.radius.base.BaseDictionaryAction;
import com.elitecore.elitesm.web.rm.concurrentloginpolicy.forms.SearchConcurrentLoginPolicyForm;

public class InitSearchConcurrentLoginPolicyAction extends BaseWebAction {
	private static final String INIT_FORWARD = "initSearchConcurrentLog";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.SEARCH_CONCURRENT_LOGIN_POLICY_ACTION;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logTrace(MODULE,"Enter execute method of"+getClass().getName());
		
		if(checkAccess(request, ACTION_ALIAS)){
			try {
				SearchConcurrentLoginPolicyForm searchConcurrentLoginPolicyForm=(SearchConcurrentLoginPolicyForm)form;
				searchConcurrentLoginPolicyForm.setStatus("All");
				searchConcurrentLoginPolicyForm.setConcurrentLoginPolicyId("A");
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				
				//System.out.println("Forwarding to JSP");
				doAuditing(staffData, ACTION_ALIAS);
				return mapping.findForward(INIT_FORWARD);	
			} catch (Exception e) {
				// TODO: handle exception
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
