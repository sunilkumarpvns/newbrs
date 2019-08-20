package com.elitecore.elitesm.web.sessionmanager;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.sessionmanager.SessionManagerBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.sessionmanager.data.ISessionManagerInstanceData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.sessionmanager.forms.ViewSessionManagerForm;

public class ViewSessionManagerAction extends BaseWebAction{
	private static String MODULE = "ViewSessionManagerAction";
	
	private static String ACTION_ALIAS = ConfigConstant.VIEW_SESSION_MANAGER;
	
	private static String viewForward ="viewSessionManagerInstance";
	
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest  request, HttpServletResponse response) throws IOException{
		Logger.logTrace(MODULE,"Enter execute method of"+getClass().getName());
		
		try{
			checkActionPermission(request, ACTION_ALIAS);
			ViewSessionManagerForm viewSessionManagerForm = (ViewSessionManagerForm) form;
			String strSmInstancId = request.getParameter("sminstanceid");
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			
			String sessionManagerId;
			if(strSmInstancId!=null){
				sessionManagerId = strSmInstancId;
			}else{
				sessionManagerId = viewSessionManagerForm.getSessionManagerId();
			}
			
			SessionManagerBLManager blManager = new SessionManagerBLManager();
			ISessionManagerInstanceData sessionManagerInstanceData = blManager.getSessionManagerDataById(sessionManagerId);
			Logger.logDebug(MODULE,"sessionManagerInstanceData: "+sessionManagerInstanceData);
			request.setAttribute("sessionManagerInstanceData",sessionManagerInstanceData);
			doAuditing(staffData, ACTION_ALIAS);
			return mapping.findForward(viewForward);
			
		}catch(ActionNotPermitedException e){
            Logger.logError(MODULE,"Error :-" + e.getMessage());
            printPermitedActionAlias(request);
            ActionMessages messages = new ActionMessages();
            messages.add("information", new ActionMessage("general.user.restricted"));
            saveErrors(request, messages);
            return mapping.findForward(INVALID_ACCESS_FORWARD);
        }catch(DataManagerException managerExp){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + managerExp.getMessage());            
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
			request.setAttribute("errorDetails", errorElements);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("sessionmanager.view.failure"));
			saveErrors(request, messages);
			return mapping.findForward(FAILURE);
		}catch(Exception e){
			Logger.logError(MODULE, "Error, reason : " + e.getMessage());            
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("sessionmanager.view.failure"));
			saveErrors(request, messages);
			return mapping.findForward(FAILURE);
		}
	}
	
}
