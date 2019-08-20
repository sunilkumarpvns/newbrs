package com.elitecore.elitesm.web.servermgr.alert;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.servermgr.alert.AlertListenerBLManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.alert.data.AlertListenerData;
import com.elitecore.elitesm.datamanager.servermgr.alert.data.AlertListenerRelData;
import com.elitecore.elitesm.datamanager.servermgr.alert.data.AlertTypeData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servermgr.alert.forms.ViewAlertListenerForm;

public class ViewAlertListenerAction extends BaseWebAction {
	private static final String VIEW_FORWARD = "viewalertlitenerdetail";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.VIEW_ALERT_LISTENER;
	private static final String MODULE = "VIEW _ALERT_LISTENER";

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of"+getClass().getName());
		if(checkAccess(request, ACTION_ALIAS)){
			try{
				ViewAlertListenerForm viewAlertListenerForm = (ViewAlertListenerForm)form;
				AlertListenerBLManager alertListenerBLManager = new AlertListenerBLManager();
				AlertListenerData alertListenerData = new AlertListenerData();
				String strListenerId = request.getParameter("listenerId");
				String listenerId = strListenerId;
				if(listenerId == null){
					listenerId = viewAlertListenerForm.getListenerId();
				}

				if(Strings.isNullOrBlank(listenerId) == false){

					alertListenerData.setListenerId(listenerId);
					IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
					alertListenerData = alertListenerBLManager.getAlertListenerDataById(listenerId);
					List<AlertListenerRelData> alertListenerRelDataList;
					alertListenerRelDataList = alertListenerBLManager.getAlertListenerRelDataList(listenerId);
					String[] selectedAlertTypes = null;
					if(alertListenerRelDataList != null && !alertListenerRelDataList.isEmpty()){
						selectedAlertTypes = new String[alertListenerRelDataList.size()];
					    for(int i=0;i<alertListenerRelDataList.size();i++) {
					    	selectedAlertTypes[i] = alertListenerRelDataList.get(i).getTypeId();
					    }
					}
					List<AlertTypeData> alertTypeDataList = alertListenerBLManager.getAlertTypeData();
					request.setAttribute("alertTypeDataList",alertTypeDataList);
					request.setAttribute("selectedAlertTypes",selectedAlertTypes);
					
					request.setAttribute("alertListenerData",alertListenerData);
					request.setAttribute("viewAlertListenerForm",viewAlertListenerForm);
				}
				return mapping.findForward(VIEW_FORWARD);
			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessages messages = new ActionMessages();
				ActionMessage message1 = new ActionMessage("alert.view.failure");
				messages.add("information",message1);
				saveErrors(request,messages);
			} 
			return mapping.findForward(FAILURE_FORWARD);
		}else{
			Logger.logWarn(MODULE, "No Access on this Operation ");
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
}
