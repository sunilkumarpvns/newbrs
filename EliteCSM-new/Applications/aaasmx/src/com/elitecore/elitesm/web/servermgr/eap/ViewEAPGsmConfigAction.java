package com.elitecore.elitesm.web.servermgr.eap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.servermgr.eap.EAPConfigBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.servermgr.eap.data.EAPConfigData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.eapconfig.EAPConfigUtils;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.servermgr.eap.forms.EAPGsmConfigForm;

public class ViewEAPGsmConfigAction extends BaseWebAction {
	private static final String ACTION_ALIAS = ConfigConstant.VIEW_EAP_CONFIGURATION;
	private static final String VIEW_GSMFORWARD = "vieweapgsmdetails";

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		Logger.logInfo(MODULE, "Enter execute method of "+getClass().getName());
		try{
			checkAccess(request, ACTION_ALIAS);
			EAPGsmConfigForm eapGsmConfigForm = (EAPGsmConfigForm) form;
			
			if(eapGsmConfigForm.getEapId() != null){
				EAPConfigData eapConfigData = new EAPConfigData();
				EAPConfigBLManager eapconConfigBLManager = new EAPConfigBLManager();
				eapConfigData = eapconConfigBLManager.getEapConfigurationDataById(eapGsmConfigForm.getEapId());
				eapGsmConfigForm.setDefaultGenMethodMap();
				String labelDefaultNegotiationMethod = EAPConfigUtils.convertDefaultNegotiationMethodToLabel(eapConfigData.getDefaultNegiotationMethod());
				request.setAttribute("eapConfigData",eapConfigData);
				request.setAttribute("labelDefaultNegotiationMethod", labelDefaultNegotiationMethod);
				return mapping.findForward(VIEW_GSMFORWARD);
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
		}catch (Exception e) {
			Logger.logError(MODULE, "Restricted to do action.");
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("servermgr.eap.create.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		} 
		return mapping.findForward(FAILURE);
	}
}
