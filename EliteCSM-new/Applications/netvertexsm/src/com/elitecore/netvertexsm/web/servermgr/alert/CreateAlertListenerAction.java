package com.elitecore.netvertexsm.web.servermgr.alert;

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
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.constraintviolation.DuplicateEntityFoundException;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.servermgr.alert.data.AlertFileListenerData;
import com.elitecore.netvertexsm.datamanager.servermgr.alert.data.AlertListenerData;
import com.elitecore.netvertexsm.datamanager.servermgr.alert.data.AlertListenerRelData;
import com.elitecore.netvertexsm.datamanager.servermgr.alert.data.AlertTrapListenerData;
import com.elitecore.netvertexsm.datamanager.servermgr.alert.data.AlertTypeData;
import com.elitecore.netvertexsm.datamanager.servermgr.alert.data.BaseAlertListener;
import com.elitecore.netvertexsm.util.constants.AlertListenerConstant;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.netvertexsm.web.servermgr.alert.forms.CreateAlertListenerForm;


public class CreateAlertListenerAction extends BaseWebAction {
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_ALERT_LISTENER;
	private static final String CREATE_FILE_ALERT_LISTENER="createFileAlertListener";
	private static final String CREATE_TRAP_ALERT_LISTENER="createTrapAlertListener";


	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logTrace(MODULE,"Enter the execute method of :"+getClass().getName());

		CreateAlertListenerForm createAlertListenerForm = (CreateAlertListenerForm)form; 
		HashSet<AlertListenerRelData> alertListenerRelDataSet = null;
		
		try{
			checkActionPermission(request, ACTION_ALIAS);
			String typeId=createAlertListenerForm.getTypeId();
			Logger.logTrace(MODULE,"Type Id :"+ createAlertListenerForm.getTypeId());
			AlertListenerData alertListenerData = new AlertListenerData();
			AlertListenerBLManager alertListBLManager = new AlertListenerBLManager();
			List<AlertTypeData> alertTypeDataList = alertListBLManager.getAlertTypeData();

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

			}else{
				String[] alertTypeId = request.getParameterValues("alertEnable");
				String[] floodControlIds = request.getParameterValues("floodcontrolenable");
			    String enablefloodControlIds=Arrays.toString(floodControlIds);
			  
			    AlertListenerRelData alertListenerRelData;
				alertListenerRelDataSet = Collectionz.newHashSet(); 
				if(alertTypeId != null && alertTypeId.length > 0) {
					for(int i=0; 	i<alertTypeId.length; 	i++) {
						alertListenerRelData = new AlertListenerRelData();
						alertListenerRelData.setTypeId(alertTypeId[i]);
						if(enablefloodControlIds.contains(alertTypeId[i])){
							alertListenerRelData.setFloodControl(AlertListenerConstant.FLOODCONTROL_ENABLE);
						}else{
							alertListenerRelData.setFloodControl(AlertListenerConstant.FLOODCONTROL_DISABLE);
						}
						alertListenerRelDataSet.add(alertListenerRelData);
					}
				}
				AlertListenerData listenerData=(AlertListenerData)request.getSession().getAttribute("alertListenerData");
				listenerData.setAlertListenerRelDataSet(alertListenerRelDataSet);
				convertFormToBean(createAlertListenerForm, listenerData);
				
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				alertListBLManager.create(listenerData,staffData,ACTION_ALIAS);

				//removing attribute from session.
				request.getSession().removeAttribute("alertListenerData");
				request.getSession().removeAttribute("createAlertListenerForm");

				request.setAttribute("responseUrl","/initSearchAlertListener"); 
				ActionMessage message = new ActionMessage("alert.create.success",listenerData.getName());
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
			
            ActionMessages errorHeadingMessage = new ActionMessages();
            message = new ActionMessage("alert.error.heading","creating");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage); 
                        
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

        ActionMessages errorHeadingMessage = new ActionMessages();
        ActionMessage message = new ActionMessage("alert.error.heading","creating");
        errorHeadingMessage.add("errorHeading",message);
        saveMessages(request,errorHeadingMessage); 
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
			alertListener = alertFileListenerData;

		}else if(alertListenerData.getTypeId().equals(AlertListenerConstant.TRAP_ALERT_LISTENER_TYPE_ID)){
			AlertTrapListenerData alertTrapListenerData = new AlertTrapListenerData();
			alertTrapListenerData.setCommunity(createAlertListenerForm.getCommunity());
			alertTrapListenerData.setTrapServer(createAlertListenerForm.getTrapServer());
			if(Integer.parseInt(createAlertListenerForm.getTrapVersion())==0 || createAlertListenerForm.getSnmpRequestType()==1){
				createAlertListenerForm.setSnmpRequestType((byte)1);
				createAlertListenerForm.setTimeout(3000);
				createAlertListenerForm.setRetryCount((byte)3);
			}
			alertTrapListenerData.setTrapVersion(createAlertListenerForm.getTrapVersion());
			alertTrapListenerData.setSnmpRequestType(createAlertListenerForm.getSnmpRequestType());
			alertTrapListenerData.setTimeout(createAlertListenerForm.getTimeout());
			alertTrapListenerData.setRetryCount(createAlertListenerForm.getRetryCount());
			alertTrapListenerData.setAdvanceTrap(createAlertListenerForm.getAdvanceTrap());
			alertListener = alertTrapListenerData;			
		}
		alertListenerData.setAlertListener(alertListener);
	}
}
