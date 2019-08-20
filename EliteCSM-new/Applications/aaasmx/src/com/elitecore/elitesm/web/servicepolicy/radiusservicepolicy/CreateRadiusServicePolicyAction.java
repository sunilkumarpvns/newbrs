package com.elitecore.elitesm.web.servicepolicy.radiusservicepolicy;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.digestconf.DigestConfBLManager;
import com.elitecore.elitesm.blmanager.servermgr.eap.EAPConfigBLManager;
import com.elitecore.elitesm.blmanager.servermgr.gracepolicy.GracePolicyBLManager;
import com.elitecore.elitesm.blmanager.servicepolicy.ServicePolicyBLManager;
import com.elitecore.elitesm.blmanager.sessionmanager.SessionManagerBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.digestconf.data.DigestConfigInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.eap.data.EAPConfigData;
import com.elitecore.elitesm.datamanager.servermgr.gracepolicy.data.GracepolicyData;
import com.elitecore.elitesm.datamanager.servicepolicy.auth.data.AuthMethodTypeData;
import com.elitecore.elitesm.datamanager.sessionmanager.data.ISessionManagerInstanceData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servicepolicy.radiusservicepolicy.form.CreateRadiusServicePolicyForm;

public class CreateRadiusServicePolicyAction extends BaseWebAction{

	private static final String SUCCESS_FORWARD = "addRadiusServicePolicy";
	private static final String FAILURE_FORWARD = "failure";
	private static final String MODULE = "InitAddProxyPolicyPolicyAction";
	private static final String ACTION_ALIAS=ConfigConstant.CREATE_RADIUS_SERVICE_POLICY;
	
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{


		Logger.logTrace(MODULE,"Enter execute method of "+getClass().getName());
		try{
			String userName = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getSystemUserName();
			checkActionPermission(request, ACTION_ALIAS);
			request.getSession().removeAttribute("createRadiusServicePolicyForm");
			CreateRadiusServicePolicyForm createRadiusServicePolicyForm=(CreateRadiusServicePolicyForm)form;
			createRadiusServicePolicyForm.setDescription(getDefaultDescription(userName));
			ServicePolicyBLManager servicePoilcyBLManager = new ServicePolicyBLManager();
			SessionManagerBLManager sessionManagerBLManager = new SessionManagerBLManager();
			EAPConfigBLManager eapConfigBLManager = new EAPConfigBLManager();
			DigestConfBLManager digestConfigBLManager = new DigestConfBLManager();
			GracePolicyBLManager gracePolicyBLManager = new GracePolicyBLManager();
			
			List<AuthMethodTypeData> authMethodTypeDataList = servicePoilcyBLManager.getAuthMethodTypeList();
			List<DigestConfigInstanceData> digestConfigInstanceDataList = digestConfigBLManager.getDigestConfigInstanceList();
			List<ISessionManagerInstanceData> sessionManagerInstanceDataList = sessionManagerBLManager.getSessionManagerInstanceList();
            List<EAPConfigData> eapConfigurationList = eapConfigBLManager.getEapConfigurationList();
            List<GracepolicyData> gracePolicyList = gracePolicyBLManager.getGracePolicyList();
            
            String[] selectedAuthMethodTypes = new String[authMethodTypeDataList.size()];
            List<String> selectedAuthMethodTypesList = new ArrayList<String>(); 
            for (int i = 0; i < authMethodTypeDataList.size(); i++) {
				long authTypeId=authMethodTypeDataList.get(i).getAuthMethodTypeId();
            	if(authTypeId != 5){
            		selectedAuthMethodTypesList.add(String.valueOf(authTypeId));
            	}
            	
			}
            selectedAuthMethodTypes=selectedAuthMethodTypesList.toArray(new String[0]); 
            
            createRadiusServicePolicyForm.setSelectedAuthMethodTypes(selectedAuthMethodTypes);
			createRadiusServicePolicyForm.setAuthMethodTypeDataList(authMethodTypeDataList);
			createRadiusServicePolicyForm.setSessionManagerInstanceDataList(sessionManagerInstanceDataList);
			createRadiusServicePolicyForm.setDigestConfigInstanceDataList(digestConfigInstanceDataList);
			createRadiusServicePolicyForm.setEapConfigurationList(eapConfigurationList);
			createRadiusServicePolicyForm.setGracePolicyList(gracePolicyList);
			
			
			
			request.getSession().setAttribute("createRadiusServicePolicyForm", createRadiusServicePolicyForm);
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
