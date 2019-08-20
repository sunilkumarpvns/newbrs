package com.elitecore.elitesm.web.sessionmanager; 
  
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.sessionmanager.SessionManagerBLManager;
import com.elitecore.elitesm.datamanager.sessionmanager.data.ISessionManagerInstanceData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.sessionmanager.forms.SearchASMForm;
                                                                               
public class InitSearchASMAction extends BaseWebAction{
	                                                                       
	private static final String SEARCH_FORWARD = "searchASM";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.SEARCH_ACTIVE_SESSION;
 
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{ 
		Logger.logTrace(MODULE,"Enter execute method of "+getClass().getName());
		if(checkAccess(request, ACTION_ALIAS)){
			
			SearchASMForm searchASMForm = (SearchASMForm) form;
			String strSmInstancId = request.getParameter("sminstanceid");
			Logger.logDebug(MODULE,"strSmInstancId: "+strSmInstancId);
			String sessionManagerId;
			if(strSmInstancId!=null){
				sessionManagerId = strSmInstancId;
			}else{
				sessionManagerId = searchASMForm.getSessionManagerId();
			}
			searchASMForm.setSessionManagerId(sessionManagerId);
			SessionManagerBLManager blManager = new SessionManagerBLManager();
			ISessionManagerInstanceData sessionManagerInstanceData = blManager.getSessionManagerDataById(sessionManagerId);
			Logger.logDebug(MODULE,"sessionManagerInstanceData: "+sessionManagerInstanceData);
			request.setAttribute("sessionManagerInstanceData",sessionManagerInstanceData);
			searchASMForm.setStatus("All");
			request.setAttribute("searchASMForm", searchASMForm);
			return mapping.findForward(SEARCH_FORWARD);
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
		 