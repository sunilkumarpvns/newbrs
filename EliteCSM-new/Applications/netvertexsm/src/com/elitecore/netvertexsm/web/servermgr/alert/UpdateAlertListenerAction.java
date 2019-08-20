package com.elitecore.netvertexsm.web.servermgr.alert;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.netvertexsm.blmanager.servermgr.alert.AlertListenerBLManager;
import com.elitecore.netvertexsm.datamanager.core.exceptions.constraintviolation.DuplicateEntityFoundException;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.servermgr.alert.data.AlertFileListenerData;
import com.elitecore.netvertexsm.datamanager.servermgr.alert.data.AlertListenerData;
import com.elitecore.netvertexsm.datamanager.servermgr.alert.data.AlertListenerRelData;
import com.elitecore.netvertexsm.datamanager.servermgr.alert.data.AlertListenerTypeData;
import com.elitecore.netvertexsm.datamanager.servermgr.alert.data.AlertTrapListenerData;
import com.elitecore.netvertexsm.datamanager.servermgr.alert.data.AlertTypeData;
import com.elitecore.netvertexsm.datamanager.servermgr.alert.data.BaseAlertListener;
import com.elitecore.netvertexsm.util.constants.AlertListenerConstant;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.netvertexsm.web.servermgr.alert.forms.UpdateAlertListenerForm;

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
			HashSet<AlertListenerRelData> alertListenerRelDataSet = null;
			
			try{
				AlertListenerBLManager alertListBLManager = new AlertListenerBLManager();
				List<AlertTypeData> alertTypeDataList = alertListBLManager.getAlertTypeData();
				request.setAttribute("alertTypeDataList",alertTypeDataList);
				String strListenerId = request.getParameter("listenerId");
				Long listenerId;
				if(strListenerId == null){
					listenerId = updateAlertListenerForm.getListenerId();
				}else{
					listenerId = Long.parseLong(strListenerId);
				}
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				String actionAlias = ACTION_ALIAS;


				if(updateAlertListenerForm.getAction() == null){
					if(listenerId!=null ){
						AlertListenerData alertListenerData = new AlertListenerData();
						alertListenerData.setListenerId(listenerId);
						alertListenerData = alertListenerBLManager.getAlertListenerData(alertListenerData, staffData, actionAlias);
						List<AlertListenerRelData> alertListenerRelDataList;
						alertListenerRelDataList = alertListenerBLManager.getAlertListenerRelDataList(listenerId, staffData, actionAlias);
						String[] selectedAlertTypes = null;
						List<String> floodControl=Collectionz.newArrayList();
						List<String> selectedAlertTypeList=Collectionz.newArrayList();
						if(Collectionz.isNullOrEmpty(alertListenerRelDataList )==false){
							
							selectedAlertTypes = new String[alertListenerRelDataList.size()];
							for(int i=0, size=alertListenerRelDataList.size();  i<size;   i++) {
								
								selectedAlertTypes[i] = alertListenerRelDataList.get(i).getTypeId();
								selectedAlertTypeList.add(alertListenerRelDataList.get(i).getTypeId());
								
								if(alertListenerRelDataList.get(i).getFloodControl().equals(AlertListenerConstant.FLOODCONTROL_ENABLE)){
									floodControl.add(alertListenerRelDataList.get(i).getTypeId());
								}
							}
						}
						updateAlertListenerForm = convertBeanToForm(alertListenerData,selectedAlertTypes);
						updateAlertListenerForm.setSelectedFloodControl(floodControl);
						updateAlertListenerForm.setSelectedAlertsTypeList(selectedAlertTypeList);
						List<AlertListenerTypeData> availableListenerType = alertListenerBLManager.getAvailableListenerType();
						updateAlertListenerForm.setAvailableListenerTypes(availableListenerType);
						request.setAttribute("updateAlertListenerForm", updateAlertListenerForm);
						request.setAttribute("alertListenerData",alertListenerData);

					}
					return mapping.findForward(UPDATE_FORWARD);
				}else if(updateAlertListenerForm.getAction().equalsIgnoreCase("update")){
					String[] alertTypeId = request.getParameterValues("alertEnable");
					String[] floodControlIds = request.getParameterValues("floodcontrolenable");
				    List<String> floodControlIdList=new ArrayList<String>();
					if(floodControlIds!=null && floodControlIds.length>0){
						floodControlIdList=Arrays.asList(floodControlIds);
					}
					
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
					alertListenerRelDataSet = new HashSet<AlertListenerRelData>(); 
					if(alertTypeId != null && alertTypeId.length > 0) {
						for(int i=0;i<alertTypeId.length;i++) {
							alertListenerRelData = new AlertListenerRelData();
							alertListenerRelData.setTypeId(alertTypeId[i]);
							if(floodControlIdList.contains(alertTypeId[i])){
								alertListenerRelData.setFloodControl(AlertListenerConstant.FLOODCONTROL_ENABLE);
							}else{
								alertListenerRelData.setFloodControl(AlertListenerConstant.FLOODCONTROL_DISABLE);
							}
							alertListenerRelDataSet.add(alertListenerRelData);
						}
					}
					
					updateAlertListenerForm.setEnabledAlerts(strAlertTypeId);
					AlertListenerData alertListenerData = convertFormToBean(updateAlertListenerForm);
					alertListenerData.setAlertListenerRelDataSet(alertListenerRelDataSet);
					alertListenerBLManager.updateAlertListener(alertListenerData,staffData,actionAlias);
					request.setAttribute("alertListenerData",alertListenerData);
					request.setAttribute("responseUrl","/initSearchAlertListener.do?listenerId="+listenerId); 
					ActionMessage message = new ActionMessage("alert.update.success",alertListenerData.getName());
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

            ActionMessages errorHeadingMessage = new ActionMessages();
            ActionMessage message = new ActionMessage("alert.error.heading","updating");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage); 

			return mapping.findForward(FAILURE_FORWARD);
		}else{
			Logger.logError(MODULE, "Error during Data Manager operation ");
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);

            ActionMessages errorHeadingMessage = new ActionMessages();
            message = new ActionMessage("alert.error.heading","updating");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage); 
            
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
				  
			}else if(alertListenerData.getTypeId().equals(AlertListenerConstant.TRAP_ALERT_LISTENER_TYPE_ID)){
				
				  AlertTrapListenerData trapAlertListener = (AlertTrapListenerData)alertListenerData.getAlertListener();
				  updateAlertListenerForm.setTrapServer(trapAlertListener.getTrapServer());
				  updateAlertListenerForm.setTrapVersion(trapAlertListener.getTrapVersion());
				  updateAlertListenerForm.setSnmpRequestType(trapAlertListener.getSnmpRequestType());
				  updateAlertListenerForm.setTimeout(trapAlertListener.getTimeout());
				  updateAlertListenerForm.setRetryCount(trapAlertListener.getRetryCount());				  
				  updateAlertListenerForm.setCommunity(trapAlertListener.getCommunity());
				  updateAlertListenerForm.setListenerId(trapAlertListener.getListenerId());
				  updateAlertListenerForm.setName(alertListenerData.getName());
				  updateAlertListenerForm.setTypeId(alertListenerData.getTypeId());
				  updateAlertListenerForm.setAdvanceTrap(trapAlertListener.getAdvanceTrap());
				  updateAlertListenerForm.setSelectedAlertsType(selectedAlertTypes);
			}
		}

		return updateAlertListenerForm;
	}

	private AlertListenerData convertFormToBean(UpdateAlertListenerForm updateAlertListenerForm){
		AlertListenerData alertListenerData = new AlertListenerData();
		BaseAlertListener alertListener = null;
		alertListenerData.setName(updateAlertListenerForm.getName());
		alertListenerData.setTypeId(updateAlertListenerForm.getTypeId());
		alertListenerData.setListenerId(updateAlertListenerForm.getListenerId());
		if(updateAlertListenerForm.getTypeId().equals(AlertListenerConstant.FILE_ALERT_LISTENER_TYPE_ID)){
			AlertFileListenerData alertFileListenerData = new AlertFileListenerData();
			alertFileListenerData.setFileName(updateAlertListenerForm.getFileName());
			alertFileListenerData.setCompRollingUnit(updateAlertListenerForm.getCompRollingUnit());
			alertFileListenerData.setRollingType(updateAlertListenerForm.getRollingType());
			alertFileListenerData.setRollingUnit(updateAlertListenerForm.getRollingUnit());
			alertFileListenerData.setMaxRollingUnit(updateAlertListenerForm.getMaxRollingUnit());
			alertFileListenerData.setListenerId(updateAlertListenerForm.getListenerId());
			
			alertListener = alertFileListenerData;

		}else if(updateAlertListenerForm.getTypeId().equals(AlertListenerConstant.TRAP_ALERT_LISTENER_TYPE_ID)){
			AlertTrapListenerData alertTrapListenerData = new AlertTrapListenerData();
			alertTrapListenerData.setCommunity(updateAlertListenerForm.getCommunity());
			alertTrapListenerData.setTrapServer(updateAlertListenerForm.getTrapServer());
			alertTrapListenerData.setTrapVersion(updateAlertListenerForm.getTrapVersion());
			if(Integer.parseInt(updateAlertListenerForm.getTrapVersion())==0 || updateAlertListenerForm.getSnmpRequestType()==1){
				updateAlertListenerForm.setSnmpRequestType((byte)1);
				updateAlertListenerForm.setTimeout(3000);
				updateAlertListenerForm.setRetryCount((byte)3);
			}
			alertTrapListenerData.setListenerId(updateAlertListenerForm.getListenerId());
			alertTrapListenerData.setAdvanceTrap(updateAlertListenerForm.getAdvanceTrap());
			alertTrapListenerData.setSnmpRequestType(updateAlertListenerForm.getSnmpRequestType());
			alertTrapListenerData.setTimeout(updateAlertListenerForm.getTimeout());
			alertTrapListenerData.setRetryCount(updateAlertListenerForm.getRetryCount());
			alertListener = alertTrapListenerData;			
		}
		alertListenerData.setAlertListener(alertListener);

		return alertListenerData;
	}		
}
