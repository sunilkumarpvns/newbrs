package com.elitecore.netvertexsm.web.servermgr.alert;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.servermgr.alert.AlertListenerBLManager;
import com.elitecore.netvertexsm.datamanager.servermgr.alert.data.AlertListenerTypeData;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.servermgr.alert.forms.CreateAlertListenerForm;

public class InitCreateAlertListenerAction extends BaseWebAction {
	
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
             return mapping.findForward(CREATE_LISTER_FORWARD);
		 }catch(Exception exp){
			 
			   Logger.logTrace(MODULE,"Error during data Manager operation,reason :"+exp.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(exp);
				request.setAttribute("errorDetails", errorElements);
		 }

         ActionMessages errorHeadingMessage = new ActionMessages();
         ActionMessage message = new ActionMessage("alert.error.heading","creating");
         errorHeadingMessage.add("errorHeading",message);
         saveMessages(request,errorHeadingMessage); 
		return mapping.findForward(FAILURE_FORWARD);
	}
}
