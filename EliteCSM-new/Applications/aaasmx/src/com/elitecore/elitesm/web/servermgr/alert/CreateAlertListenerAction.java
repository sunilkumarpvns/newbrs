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

import com.elitecore.elitesm.blmanager.servermgr.alert.AlertListenerBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.DuplicateEntityFoundException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.alert.data.AlertFileListenerData;
import com.elitecore.elitesm.datamanager.servermgr.alert.data.AlertListenerData;
import com.elitecore.elitesm.datamanager.servermgr.alert.data.AlertListenerRelData;
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
import com.elitecore.elitesm.web.servermgr.alert.forms.CreateAlertListenerForm;

public class CreateAlertListenerAction extends BaseWebAction {
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_ALERT_LISTENER;
	private static final String CREATE_FILE_ALERT_LISTENER="createFileAlertListener";
	private static final String CREATE_TRAP_ALERT_LISTENER="createTrapAlertListener";
	private static final String CREATE_SYS_ALERT_LISTENER="createSYSAlertListener";

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logTrace(MODULE,"Enter the execute method of :"+getClass().getName());

		CreateAlertListenerForm createAlertListenerForm = (CreateAlertListenerForm)form; 
		List<AlertListenerRelData> alertListenerRelDataList = null;
		
		try{
			checkActionPermission(request, ACTION_ALIAS);
			String typeId=createAlertListenerForm.getTypeId();
			Logger.logTrace(MODULE,"Type Id :"+ createAlertListenerForm.getTypeId());
			AlertListenerData alertListenerData = new AlertListenerData();
			AlertListenerBLManager alertListBLManager = new AlertListenerBLManager();
			List<AlertTypeData> alertTypeDataList = alertListBLManager.getAlertTypeData();
			List<SYSLogNameValuePoolData> sysLogNameValuePoolDataList = alertListBLManager.getSysLogNameValuePoolList();
			createAlertListenerForm.setSysLogNameValuePoolDataList(sysLogNameValuePoolDataList);
			if(AlertListenerConstant.FILE_ALERT_LISTENER_TYPE_ID.equals(typeId)){
				alertListenerData.setName(createAlertListenerForm.getName());
				alertListenerData.setTypeId(createAlertListenerForm.getTypeId());
				request.getSession().setAttribute("alertListenerData",alertListenerData);
				request.setAttribute("alertTypeDataList", alertTypeDataList);
				return mapping.findForward(CREATE_FILE_ALERT_LISTENER);

			}else if(AlertListenerConstant.TRAP_ALERT_LISTENER_TYPE_ID.equals(typeId)){
				alertListenerData.setName(createAlertListenerForm.getName());
				alertListenerData.setTypeId(createAlertListenerForm.getTypeId());
				request.setAttribute("alertTypeDataList", alertTypeDataList);
				request.getSession().setAttribute("alertListenerData",alertListenerData);
				return mapping.findForward(CREATE_TRAP_ALERT_LISTENER);

			}else if(AlertListenerConstant.SYS_ALERT_LISTENER_TYPE_ID.equals(typeId)){
				alertListenerData.setName(createAlertListenerForm.getName());
				alertListenerData.setTypeId(createAlertListenerForm.getTypeId());
				request.setAttribute("sysLogNameValuePoolDataList", sysLogNameValuePoolDataList);
				request.setAttribute("alertTypeDataList", alertTypeDataList);
				request.getSession().setAttribute("alertListenerData",alertListenerData);
				return mapping.findForward(CREATE_SYS_ALERT_LISTENER);

			}else{

				String[] alertTypeId = request.getParameterValues("alerts");
				
				AlertListenerRelData alertListenerRelData;
				alertListenerRelDataList = new ArrayList<AlertListenerRelData>(); 
				if(alertTypeId != null && alertTypeId.length > 0) {
					for(int i=0;i<alertTypeId.length;i++) {
						alertListenerRelData = new AlertListenerRelData();
						alertListenerRelData.setTypeId(alertTypeId[i]);
						alertListenerRelDataList.add(alertListenerRelData);
					}
				}

				AlertListenerBLManager alertListenerBLManager = new AlertListenerBLManager();

				AlertListenerData listenerData=(AlertListenerData)request.getSession().getAttribute("alertListenerData");
				listenerData.setAlertListenerRelDataList(alertListenerRelDataList);
				convertFormToBean(createAlertListenerForm, listenerData);

				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				alertListenerBLManager.create(listenerData, staffData);
				//removing attribute from session.
				request.getSession().removeAttribute("alertListenerData");
				request.getSession().removeAttribute("createAlertListenerForm");

				request.setAttribute("responseUrl","/initSearchAlertListener"); 
				ActionMessage message = new ActionMessage("alert.create.success");
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveMessages(request,messages);
				return mapping.findForward(SUCCESS_FORWARD);		
			}

		}catch(ActionNotPermitedException e){
			Logger.logError(MODULE, "Restricted to do action.");
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);	

		}catch (DuplicateEntityFoundException dpfExp) {
			Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
			Logger.logTrace(MODULE,dpfExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(dpfExp);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("alert.create.duplicate.failure",createAlertListenerForm.getName());
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			saveErrors(request,messages);
		}catch(Exception e){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("alert.create.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}

		return mapping.findForward(FAILURE_FORWARD);
	}

	private void convertFormToBean(CreateAlertListenerForm createAlertListenerForm,AlertListenerData alertListenerData){
		BaseAlertListener alertListener = null;
		/*alertListenerData.setName(alertListenerData.getName());
		alertListenerData.setTypeId(alertListenerData.getTypeId());*/
		if(alertListenerData.getTypeId().equals(AlertListenerConstant.FILE_ALERT_LISTENER_TYPE_ID)){
			AlertFileListenerData alertFileListenerData = new AlertFileListenerData();
			alertFileListenerData.setFileName(createAlertListenerForm.getFileName());
			alertFileListenerData.setCompRollingUnit(createAlertListenerForm.getCompRollingUnit());
			alertFileListenerData.setRollingType(createAlertListenerForm.getRollingType());
			alertFileListenerData.setRollingUnit(createAlertListenerForm.getRollingUnit());
			alertFileListenerData.setMaxRollingUnit(createAlertListenerForm.getMaxRollingUnit());
			alertFileListenerData.setRepeatedMessageReduction(createAlertListenerForm.getRepeatedMessageReduction());
			alertListener = alertFileListenerData;
		}else if(alertListenerData.getTypeId().equals(AlertListenerConstant.TRAP_ALERT_LISTENER_TYPE_ID)){
			AlertTrapListenerData alertTrapListenerData = new AlertTrapListenerData();
			alertTrapListenerData.setCommunity(createAlertListenerForm.getCommunity());
			alertTrapListenerData.setTrapServer(createAlertListenerForm.getTrapServer());
			alertTrapListenerData.setTrapVersion(createAlertListenerForm.getTrapVersion());
			alertTrapListenerData.setAdvanceTrap(createAlertListenerForm.getAdvanceTrap());
			alertTrapListenerData.setRepeatedMessageReduction(createAlertListenerForm.getRepeatedMessageReduction());
			alertListener = alertTrapListenerData;			
		}else if(alertListenerData.getTypeId().equals(AlertListenerConstant.SYS_ALERT_LISTENER_TYPE_ID)){
			SYSLogAlertListenerData sysLogAlertListenerData = new SYSLogAlertListenerData();
			sysLogAlertListenerData.setAddress(createAlertListenerForm.getAddress());
			sysLogAlertListenerData.setFacility(createAlertListenerForm.getFacility());
			sysLogAlertListenerData.setRepeatedMessageReduction(createAlertListenerForm.getRepeatedMessageReduction());
			alertListener = sysLogAlertListenerData;			
		}
		alertListenerData.setAlertListener(alertListener);
	}
}