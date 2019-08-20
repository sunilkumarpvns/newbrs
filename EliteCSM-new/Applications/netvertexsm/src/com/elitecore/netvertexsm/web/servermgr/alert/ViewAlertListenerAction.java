package com.elitecore.netvertexsm.web.servermgr.alert;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.netvertexsm.blmanager.servermgr.alert.AlertListenerBLManager;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.servermgr.alert.data.AlertListenerData;
import com.elitecore.netvertexsm.datamanager.servermgr.alert.data.AlertListenerRelData;
import com.elitecore.netvertexsm.datamanager.servermgr.alert.data.AlertTypeData;
import com.elitecore.netvertexsm.util.constants.AlertListenerConstant;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.netvertexsm.web.servermgr.alert.forms.ViewAlertListenerForm;

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
				Long listenerId=null;
				if(Strings.isNullOrBlank(strListenerId)==false){
					listenerId = Long.parseLong(strListenerId);
				}else{
					listenerId = viewAlertListenerForm.getListenerId();
				}
				if(listenerId!=null ){
					List<AlertTypeData> alertTypeDataList = alertListenerBLManager.getAlertTypeData();
					request.setAttribute("alertTypeDataList",alertTypeDataList);
					alertListenerData.setListenerId(listenerId);
					IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
					String actionAlias = ACTION_ALIAS;
					alertListenerData = alertListenerBLManager.getAlertListenerData(alertListenerData,staffData,actionAlias);
					List<AlertListenerRelData> alertListenerRelDataList = alertListenerBLManager.getAlertListenerRelDataList(listenerId, staffData, actionAlias);
					String[] selectedAlertTypes = null;
					List<String> floodControl=Collectionz.newArrayList();
					List<String> selectedAlertTypeList=Collectionz.newArrayList();
					if(Collectionz.isNullOrEmpty(alertListenerRelDataList)==false){
						selectedAlertTypes = new String[alertListenerRelDataList.size()];
						for(int i=0;i<alertListenerRelDataList.size();i++) {
							selectedAlertTypes[i] = alertListenerRelDataList.get(i).getTypeId();
							selectedAlertTypeList.add(alertListenerRelDataList.get(i).getTypeId());
							if(alertListenerRelDataList.get(i).getFloodControl().equals(AlertListenerConstant.FLOODCONTROL_ENABLE)){
								floodControl.add(alertListenerRelDataList.get(i).getTypeId());
							}
						}
						List<AlertTypeData> enabledAlertsList = alertListenerBLManager.getEnabledAlertTypeData(selectedAlertTypes);
						viewAlertListenerForm.setEnabledAlertsList(enabledAlertsList);
					}
					viewAlertListenerForm.setSelectedAlertsTypeList(selectedAlertTypeList);
			        viewAlertListenerForm.setSelectedFloodControl(floodControl);
			        viewAlertListenerForm.setSelectedAlertsType(selectedAlertTypes);
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
            ActionMessages errorHeadingMessage = new ActionMessages();
            ActionMessage message = new ActionMessage("alert.error.heading","viewing");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage); 
			return mapping.findForward(FAILURE_FORWARD);
		}else{
			Logger.logWarn(MODULE, "No Access on this Operation ");
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			
            ActionMessages errorHeadingMessage = new ActionMessages();
            message = new ActionMessage("alert.error.heading","viewing");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage); 
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
}