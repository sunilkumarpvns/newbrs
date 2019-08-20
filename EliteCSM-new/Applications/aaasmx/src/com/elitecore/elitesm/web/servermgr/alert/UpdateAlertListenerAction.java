
package com.elitecore.elitesm.web.servermgr.alert;
import java.util.ArrayList;
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
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.DuplicateEntityFoundException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.alert.data.AlertFileListenerData;
import com.elitecore.elitesm.datamanager.servermgr.alert.data.AlertListenerData;
import com.elitecore.elitesm.datamanager.servermgr.alert.data.AlertListenerRelData;
import com.elitecore.elitesm.datamanager.servermgr.alert.data.AlertListenerTypeData;
import com.elitecore.elitesm.datamanager.servermgr.alert.data.AlertTrapListenerData;
import com.elitecore.elitesm.datamanager.servermgr.alert.data.AlertTypeData;
import com.elitecore.elitesm.datamanager.servermgr.alert.data.BaseAlertListener;
import com.elitecore.elitesm.datamanager.servermgr.alert.data.SYSLogAlertListenerData;
import com.elitecore.elitesm.datamanager.servermgr.alert.data.SYSLogNameValuePoolData;
import com.elitecore.elitesm.util.constants.AlertListenerConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servermgr.alert.forms.UpdateAlertListenerForm;

public class UpdateAlertListenerAction extends BaseWebAction {
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String UPDATE_FORWARD = "updatealertlistenerdetail";
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_ALERT_LISTENER;
	private static final String MODULE = "UPDATE_ALERT_LISTENER_ACTION";


	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		if(checkAccess(request, ACTION_ALIAS)){
			Logger.logTrace(MODULE,"Enter execute method of :"+getClass().getName());

			AlertListenerBLManager alertListenerBLManager = new AlertListenerBLManager();
			UpdateAlertListenerForm updateAlertListenerForm = (UpdateAlertListenerForm)form;
			List<AlertListenerRelData> alertListenerRelDataLists = null;
			
			try{	
				AlertListenerBLManager alertListBLManager = new AlertListenerBLManager();
				List<AlertTypeData> alertTypeDataList = alertListBLManager.getAlertTypeData();
				request.setAttribute("alertTypeDataList",alertTypeDataList);
				String strListenerId = request.getParameter("listenerId");
				String listenerId;
				if(strListenerId == null){
					listenerId = updateAlertListenerForm.getListenerId();
				}else{
					listenerId = strListenerId;
				}
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));

				if(updateAlertListenerForm.getAction() == null){
					if(Strings.isNullOrBlank(listenerId) == false){
						AlertListenerData alertListenerData = new AlertListenerData();
						alertListenerData.setListenerId(listenerId);
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
						updateAlertListenerForm = convertBeanToForm(alertListenerData,selectedAlertTypes);
						List<AlertListenerTypeData> availableListenerType = alertListenerBLManager.getAvailableListenerType();
						List<SYSLogNameValuePoolData> sysLogNameValuePoolDataList = alertListBLManager.getSysLogNameValuePoolList();
						updateAlertListenerForm.setSysLogNameValuePoolDataList(sysLogNameValuePoolDataList);
						updateAlertListenerForm.setAvailableListenerTypes(availableListenerType);
						request.setAttribute("updateAlertListenerForm", updateAlertListenerForm);
						request.setAttribute("alertListenerData",alertListenerData);

					}
					return mapping.findForward(UPDATE_FORWARD);
				}else if(updateAlertListenerForm.getAction().equalsIgnoreCase("update")){
					String[] alertTypeId = request.getParameterValues("alerts");
					String strAlertTypeId = "";
					
					if(alertTypeId != null && alertTypeId.length > 0) {
						for(int i=0;i<alertTypeId.length;i++) {
							if(i == alertTypeId.length-1)
								strAlertTypeId = strAlertTypeId + alertTypeId[i];
							else
								strAlertTypeId = strAlertTypeId + alertTypeId[i] + ",";
						}
					}
					
					AlertListenerRelData alertListenerRelData;
					alertListenerRelDataLists = new ArrayList<AlertListenerRelData>(); 
					if(alertTypeId != null && alertTypeId.length > 0) {
						for(int i=0;i<alertTypeId.length;i++) {
							alertListenerRelData = new AlertListenerRelData();
							alertListenerRelData.setTypeId(alertTypeId[i]);
							alertListenerRelDataLists.add(alertListenerRelData);
						}
					}
					
					updateAlertListenerForm.setEnabledAlerts(strAlertTypeId);
					
					AlertListenerData alertListenerData = new AlertListenerData();
					alertListenerData.setListenerId(listenerId);
					alertListenerData = alertListenerBLManager.getAlertListenerDataById(listenerId);
				
					alertListenerData = convertFormToBean(updateAlertListenerForm,alertListenerData);
					alertListenerData.setAlertListenerRelDataList(alertListenerRelDataLists);
					
					alertListenerBLManager.updateById(alertListenerData,staffData);
					
					request.setAttribute("alertListenerData",alertListenerData);

					request.setAttribute("responseUrl","/viewAlertListener.do?listenerId="+listenerId); 
					ActionMessage message = new ActionMessage("alert.update.success");
					ActionMessages messages = new ActionMessages();
					messages.add("information",message);
					saveMessages(request,messages);
					return mapping.findForward(SUCCESS_FORWARD);

				}
			}catch (DuplicateEntityFoundException dpfExp) {
				Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
				Logger.logTrace(MODULE,dpfExp);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(dpfExp);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("alert.update.duplicate.failure",updateAlertListenerForm.getName());
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveErrors(request,messages);
			}catch(Exception e){
				Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("alert.update.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
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

	private UpdateAlertListenerForm convertBeanToForm(AlertListenerData alertListenerData,String[] selectedAlertTypes){
		UpdateAlertListenerForm updateAlertListenerForm = null;
		if(alertListenerData!=null){
			updateAlertListenerForm = new UpdateAlertListenerForm();
			if(alertListenerData.getTypeId().equals(AlertListenerConstant.FILE_ALERT_LISTENER_TYPE_ID)){
				  AlertFileListenerData fileAlertListener = (AlertFileListenerData)alertListenerData.getAlertListener();
				  
				  updateAlertListenerForm.setFileName(fileAlertListener.getFileName());
				  updateAlertListenerForm.setCompRollingUnit(fileAlertListener.getCompRollingUnit());
				  updateAlertListenerForm.setRollingType(fileAlertListener.getRollingType());
				  updateAlertListenerForm.setRollingUnit(fileAlertListener.getRollingUnit());
				  updateAlertListenerForm.setMaxRollingUnit(fileAlertListener.getMaxRollingUnit());
				  updateAlertListenerForm.setListenerId(fileAlertListener.getListenerId());
				  updateAlertListenerForm.setName(alertListenerData.getName());
				  updateAlertListenerForm.setTypeId(alertListenerData.getTypeId());
				  updateAlertListenerForm.setSelectedAlertsType(selectedAlertTypes);
				  updateAlertListenerForm.setAuditUId(alertListenerData.getAuditUId());
				  updateAlertListenerForm.setRepeatedMessageReduction(fileAlertListener.getRepeatedMessageReduction());
				  
			}else if(alertListenerData.getTypeId().equals(AlertListenerConstant.TRAP_ALERT_LISTENER_TYPE_ID)){
				
				  AlertTrapListenerData trapAlertListener = (AlertTrapListenerData)alertListenerData.getAlertListener();
				  updateAlertListenerForm.setTrapServer(trapAlertListener.getTrapServer());
				  updateAlertListenerForm.setTrapVersion(trapAlertListener.getTrapVersion());
				  updateAlertListenerForm.setCommunity(trapAlertListener.getCommunity());
				  updateAlertListenerForm.setListenerId(trapAlertListener.getListenerId());
				  updateAlertListenerForm.setName(alertListenerData.getName());
				  updateAlertListenerForm.setTypeId(alertListenerData.getTypeId());
				  updateAlertListenerForm.setAdvanceTrap(trapAlertListener.getAdvanceTrap());
				  updateAlertListenerForm.setSelectedAlertsType(selectedAlertTypes);
				  updateAlertListenerForm.setAuditUId(alertListenerData.getAuditUId());
				  updateAlertListenerForm.setRepeatedMessageReduction(trapAlertListener.getRepeatedMessageReduction());
			}else if(alertListenerData.getTypeId().equals(AlertListenerConstant.SYS_ALERT_LISTENER_TYPE_ID)){	
				  SYSLogAlertListenerData sysLogAlertListenerData = (SYSLogAlertListenerData)alertListenerData.getAlertListener();
				  updateAlertListenerForm.setAddress(sysLogAlertListenerData.getAddress());
				  updateAlertListenerForm.setFacility(sysLogAlertListenerData.getFacility());
				  updateAlertListenerForm.setListenerId(sysLogAlertListenerData.getListenerId());
				  updateAlertListenerForm.setName(alertListenerData.getName());
				  updateAlertListenerForm.setTypeId(alertListenerData.getTypeId());
				  updateAlertListenerForm.setSelectedAlertsType(selectedAlertTypes);
				  updateAlertListenerForm.setAuditUId(alertListenerData.getAuditUId());
				  updateAlertListenerForm.setRepeatedMessageReduction(sysLogAlertListenerData.getRepeatedMessageReduction());
			}
		}

		return updateAlertListenerForm;
	}

	private AlertListenerData convertFormToBean(UpdateAlertListenerForm updateAlertListenerForm,AlertListenerData alertListenerData){
		BaseAlertListener alertListener = null;
		alertListenerData.setName(updateAlertListenerForm.getName());
		alertListenerData.setTypeId(updateAlertListenerForm.getTypeId());
		alertListenerData.setListenerId(updateAlertListenerForm.getListenerId());
		alertListenerData.setAuditUId(updateAlertListenerForm.getAuditUId());
		
		if(updateAlertListenerForm.getTypeId().equals(AlertListenerConstant.FILE_ALERT_LISTENER_TYPE_ID)){
			AlertFileListenerData alertFileListenerData = new AlertFileListenerData();
			alertFileListenerData.setFileName(updateAlertListenerForm.getFileName());
			alertFileListenerData.setCompRollingUnit(updateAlertListenerForm.getCompRollingUnit());
			alertFileListenerData.setRollingType(updateAlertListenerForm.getRollingType());
			alertFileListenerData.setRollingUnit(updateAlertListenerForm.getRollingUnit());
			alertFileListenerData.setMaxRollingUnit(updateAlertListenerForm.getMaxRollingUnit());
			alertFileListenerData.setListenerId(updateAlertListenerForm.getListenerId());
			alertFileListenerData.setRepeatedMessageReduction(updateAlertListenerForm.getRepeatedMessageReduction());
			alertListener = alertFileListenerData;

		}else if(updateAlertListenerForm.getTypeId().equals(AlertListenerConstant.TRAP_ALERT_LISTENER_TYPE_ID)){
			AlertTrapListenerData alertTrapListenerData = new AlertTrapListenerData();
			alertTrapListenerData.setCommunity(updateAlertListenerForm.getCommunity());
			alertTrapListenerData.setTrapServer(updateAlertListenerForm.getTrapServer());
			alertTrapListenerData.setTrapVersion(updateAlertListenerForm.getTrapVersion());
			alertTrapListenerData.setListenerId(updateAlertListenerForm.getListenerId());
			alertTrapListenerData.setAdvanceTrap(updateAlertListenerForm.getAdvanceTrap());
			alertTrapListenerData.setRepeatedMessageReduction(updateAlertListenerForm.getRepeatedMessageReduction());
			alertListener = alertTrapListenerData;			
		}else if(updateAlertListenerForm.getTypeId().equals(AlertListenerConstant.SYS_ALERT_LISTENER_TYPE_ID)){
			SYSLogAlertListenerData sysLogAlertListenerData = new SYSLogAlertListenerData();
			sysLogAlertListenerData.setAddress(updateAlertListenerForm.getAddress());
			sysLogAlertListenerData.setFacility(updateAlertListenerForm.getFacility());
			sysLogAlertListenerData.setListenerId(updateAlertListenerForm.getListenerId());
			sysLogAlertListenerData.setRepeatedMessageReduction(updateAlertListenerForm.getRepeatedMessageReduction());
			alertListener = sysLogAlertListenerData;			
		}
		alertListenerData.setAlertListener(alertListener);

		return alertListenerData;
	}		


}
