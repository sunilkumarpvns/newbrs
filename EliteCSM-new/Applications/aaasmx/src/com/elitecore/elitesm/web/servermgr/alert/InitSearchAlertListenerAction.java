package com.elitecore.elitesm.web.servermgr.alert;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.servermgr.alert.AlertListenerBLManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.alert.data.AlertListenerTypeData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servermgr.alert.forms.SearchAlertListenerForm;

public class InitSearchAlertListenerAction extends BaseWebAction{
	
	private static final String SUCCESS_FORWARD = "searchAlertListener";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS=ConfigConstant.SEARCH_ALERT_LISTENER;

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		ActionMessages messages = new ActionMessages();
		try{
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
			AlertListenerBLManager alertListenerBLManager = new AlertListenerBLManager();
			List<AlertListenerTypeData> alertListenerTypeList = alertListenerBLManager.getAvailableListenerType();
			SearchAlertListenerForm searchAlertListenerForm  = (SearchAlertListenerForm)form;
			//request.getSession().setAttribute("searchAlertListenerInstance", searchAlertListenerForm);
			searchAlertListenerForm.setTypeList(alertListenerTypeList);
			searchAlertListenerForm.setName("");
			searchAlertListenerForm.setAction("");
			doAuditing(staffData, ACTION_ALIAS);
			return mapping.findForward(SUCCESS_FORWARD);
		}catch (Exception e) {
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			Logger.logTrace(MODULE, e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
	
			ActionMessage message = new ActionMessage("general.error");
			messages.add("information", message);
		}
		saveErrors(request, messages);
		return mapping.findForward(FAILURE_FORWARD);
	}

}
