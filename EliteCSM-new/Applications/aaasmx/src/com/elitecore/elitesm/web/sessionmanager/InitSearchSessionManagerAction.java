package com.elitecore.elitesm.web.sessionmanager;

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
import com.elitecore.elitesm.web.sessionmanager.forms.SearchSessionManagerForm;


public class InitSearchSessionManagerAction extends BaseWebAction {
	
	private static final String SUCCESS_FORWARD = "searchSessionManager";
	private static final String FAILURE_FORWARD = "failure";
	private static final String MODULE = "INIT_SEARCH_SESSION_MANAGER_ACTION";
	private static final String ACTION_ALIAS = ConfigConstant.SEARCH_SESSION_MANAGER;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		try{
			Logger.logTrace(MODULE,"Execute Method Of :"+getClass().getName());
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			SearchSessionManagerForm searchSessionManagerForm=(SearchSessionManagerForm)form;
			searchSessionManagerForm.setName("");
			doAuditing(staffData, ACTION_ALIAS);
			return mapping.findForward(SUCCESS_FORWARD);
		
		}catch(ActionNotPermitedException e){
			Logger.logError(MODULE,"Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}catch(Exception e){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE);
	}
}
