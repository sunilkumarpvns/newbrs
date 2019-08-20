package com.elitecore.elitesm.web.servermgr.eap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.servermgr.eap.EAPConfigBLManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.eap.data.EAPConfigData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.eapconfig.EAPConfigUtils;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;

public class ViewEAPConfigAction extends BaseEAPConfigAction {
	private static  String VIEW_FORWARD = null;
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.VIEW_EAP_CONFIGURATION;
	private static final String MODULE = "VIEW_EAP_CONFIG";

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of"+getClass().getName());
		if(checkAccess(request, ACTION_ALIAS)){
			try{


				EAPConfigBLManager eapconConfigBLManager = new EAPConfigBLManager();

				EAPConfigData eapConfigData = new EAPConfigData();
				String strEapId = request.getParameter("eapId");
				String eapId = strEapId;
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));

				if("basic".equals(request.getParameter("viewType"))){
					VIEW_FORWARD="vieweapbasicdetails";
				}else{
					VIEW_FORWARD = "viewsummarydetails";
				}

				if(Strings.isNullOrBlank(eapId) == false){

					eapConfigData = eapconConfigBLManager.getEapConfigurationDataById(eapId);
					String labelDefaultNegotiationMethod = EAPConfigUtils.convertDefaultNegotiationMethodToLabel(eapConfigData.getDefaultNegiotationMethod());
					request.setAttribute("eapConfigData",eapConfigData);
					request.setAttribute("labelDefaultNegotiationMethod",labelDefaultNegotiationMethod);
				}
			
				return mapping.findForward(VIEW_FORWARD);
			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessages messages = new ActionMessages();
				ActionMessage message1 = new ActionMessage("servermgr.eap.view.failure");
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
