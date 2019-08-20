package com.elitecore.elitesm.web.diameter.policies.diameterpolicy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.diameter.diameterpolicy.DiameterPolicyBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.diameter.diameterpolicy.data.DiameterPolicyData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;

public class ViewDiameterPolicyAction extends BaseWebAction {

	private static final String VIEW_FORWARD = "viewDiameterPolicyDetail";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.VIEW_AUTHORIZATION_POLICY;

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
			try {
				checkActionPermission(request, ACTION_ALIAS);
				Logger.logTrace(MODULE,"Enter execute method of"+getClass().getName());
				DiameterPolicyBLManager diameterPolicyBLManager = new DiameterPolicyBLManager();
				String strDiameterPolicyId = request.getParameter("diameterPolicyId");
								
				if(Strings.isNullOrBlank(strDiameterPolicyId) == false){
					DiameterPolicyData diameterPolicyData = new DiameterPolicyData();

					diameterPolicyData = diameterPolicyBLManager.getDiameterPolicyDataById(strDiameterPolicyId);
					request.setAttribute("diameterPolicyData",diameterPolicyData);
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
			}catch (DataManagerException managerExp) {
				Logger.logError(MODULE, "Error during Data Manager operation , reason : " + managerExp.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("general.error");                                                         
				ActionMessages messages = new ActionMessages();                                                                                 
				messages.add("information", message);                                                                                           
				saveErrors(request, messages);                                         
				return mapping.findForward(FAILURE_FORWARD);
			}
			return mapping.findForward(VIEW_FORWARD);
	}
}
