package com.elitecore.elitesm.web.servicepolicy.diameter.naspolicy;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.diameter.diameterconcurrency.DiameterConcurrencyBLManager;
import com.elitecore.elitesm.blmanager.servermgr.gracepolicy.GracePolicyBLManager;
import com.elitecore.elitesm.blmanager.servicepolicy.ServicePolicyBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.diameter.diameterconcurrency.data.DiameterConcurrencyData;
import com.elitecore.elitesm.datamanager.servermgr.gracepolicy.data.GracepolicyData;
import com.elitecore.elitesm.datamanager.servicepolicy.auth.data.AuthMethodTypeData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servicepolicy.diameter.naspolicy.forms.AddNASServicePolicyForm;

public class InitAddNASServicePolicyAction extends BaseWebAction{

	private static final String SUCCESS_FORWARD = "addNASServicePolicy";
	private static final String FAILURE_FORWARD = "failure";
	private static final String MODULE = "InitNASAuthServicePolicyAction";
	private static final String ACTION_ALIAS=ConfigConstant.CREATE_NAS_SERVICE_POLICY;
	
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
    
        
		Logger.logTrace(MODULE,"Enter execute method of "+getClass().getName());
		try{
			checkActionPermission(request, ACTION_ALIAS);
			request.getSession().removeAttribute("addNASServicePolicyForm");
			request.getSession().removeAttribute("addNASServicePolicyDetailForm");
			String userName = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getSystemUserName();
			AddNASServicePolicyForm addNASServicePolicyForm=(AddNASServicePolicyForm)form;
			addNASServicePolicyForm.setStatus("1");
			addNASServicePolicyForm.setDescription(getDefaultDescription(userName));
			
			ServicePolicyBLManager servicePoilcyBLManager = new ServicePolicyBLManager();
			List<AuthMethodTypeData> authMethodTypeDataList = servicePoilcyBLManager.getAuthMethodTypeList();
			
			GracePolicyBLManager gracePolicyBLManager = new GracePolicyBLManager();
			List<GracepolicyData> gracePolicyList = gracePolicyBLManager.getGracePolicyList();
			addNASServicePolicyForm.setGracePolicyList(gracePolicyList);
			
			/** Fetching Diameter Concurrency **/
			DiameterConcurrencyBLManager diameterConcurrencyBLManager = new DiameterConcurrencyBLManager();
			List<DiameterConcurrencyData> diameterConcurrencyDataList = diameterConcurrencyBLManager.getDiameterConcurrencyDataList();
			addNASServicePolicyForm.setDiameterConcurrencyDataList(diameterConcurrencyDataList);
			
			request.setAttribute("addNASServicePolicyForm", addNASServicePolicyForm);
			return mapping.findForward(SUCCESS_FORWARD);

		}catch(ActionNotPermitedException e){
            Logger.logError(MODULE,"Error :-" + e.getMessage());
            printPermitedActionAlias(request);
            ActionMessages messages = new ActionMessages();
            messages.add("information", new ActionMessage("general.user.restricted"));
            saveErrors(request, messages);
            return mapping.findForward(INVALID_ACCESS_FORWARD);
		}catch(DataManagerException e){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			
		}catch(Exception e){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			
		}
		return mapping.findForward(FAILURE_FORWARD);
	}
}