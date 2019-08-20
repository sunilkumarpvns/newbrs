package com.elitecore.elitesm.web.servermgr.alert;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.elitecore.elitesm.blmanager.servermgr.alert.AlertListenerBLManager;
import com.elitecore.elitesm.datamanager.servermgr.alert.data.AlertListenerTypeData;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.servermgr.alert.forms.CreateAlertListenerForm;

public class InitCreateAlertListenerAction extends BaseWebAction {
	
	private static final String SUCCESS_FORWARD = "createAlertListener";
	//private static final String CREATE_FILE_LISTER_FORWARD = "createFileAlertListener";
	//private static final String CREATE_TRAP_LISTER_FORWARD = "createTrapAlertListener";
	private static final String CREATE_LISTER_FORWARD="createAlertListener";
	private static final String FAILURE_FORWARD = "failure";
	private static final String MODULE="INIT_CREATE_ALERT_LISTENER_ACTION";
	
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		 Logger.logTrace(MODULE,"Execute Method Of :"+getClass().getName());

		 request.getSession().removeAttribute("alertListenerData");
		 request.getSession().removeAttribute("createAlertListenerForm");
		 
		 AlertListenerBLManager alertListenerBLManager = new AlertListenerBLManager();
		 CreateAlertListenerForm createAlertListenerForm = (CreateAlertListenerForm)form;
		 
		 try{
			 List<AlertListenerTypeData> availableListenerTypes = alertListenerBLManager.getAvailableListenerType();
			 Logger.logDebug(MODULE, "IN ACTION CLASS SIZE IS:"+availableListenerTypes.size());
			 Logger.logDebug(MODULE,"Type Id :"+availableListenerTypes.get(0).getTypeId());
			 Logger.logDebug(MODULE,"Type Id :"+availableListenerTypes.get(1).getTypeId());
			 Logger.logDebug(MODULE,"Create Alert Listener form:"+createAlertListenerForm);
			 createAlertListenerForm.setAvailableListenerTypes(availableListenerTypes);
	
     /*		if(createAlertListenerForm.getTypeId().equals("TYP0001")){
				 return mapping.findForward(CREATE_FILE_LISTER_FORWARD);
			 }else if(createAlertListenerForm.getTypeId().equals("TYP0002")){
				 return mapping.findForward(CREATE_TRAP_LISTER_FORWARD);
			 }*/
              return mapping.findForward(CREATE_LISTER_FORWARD);
			 
			 
			 
		 }catch(Exception exp){
			 
			   Logger.logTrace(MODULE,"Error during data Manager operation,reason :"+exp.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(exp);
				request.setAttribute("errorDetails", errorElements);
		 }

		return mapping.findForward(FAILURE_FORWARD);
	}
}
