package com.elitecore.elitesm.web.servicepolicy.diameter.eappolicy;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.diameter.diameterconcurrency.DiameterConcurrencyBLManager;
import com.elitecore.elitesm.blmanager.servermgr.eap.EAPConfigBLManager;
import com.elitecore.elitesm.blmanager.servermgr.gracepolicy.GracePolicyBLManager;
import com.elitecore.elitesm.blmanager.servermgr.plugins.PluginBLManager;
import com.elitecore.elitesm.blmanager.servermgr.script.ScriptBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.diameter.diameterconcurrency.data.DiameterConcurrencyData;
import com.elitecore.elitesm.datamanager.servermgr.eap.data.EAPConfigData;
import com.elitecore.elitesm.datamanager.servermgr.gracepolicy.data.GracepolicyData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.data.PluginInstData;
import com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptInstanceData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.ScriptTypesConstants;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servicepolicy.diameter.eappolicy.forms.CreateEAPPolicyForm;

public class InitCreateEAPPolicyAction extends BaseWebAction {
	private static final String MODULE ="EAPPOLICY";
	private static final String FAILURE_FORWARD = "failure";               
	private static final String INITCREATEEAPPOLICY = "initCreateEAPPolicy"; 
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_DIAMETER_EAP_POLICY;

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{ 
		try{
			checkActionPermission(request,ACTION_ALIAS);	
			Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName()); 
			CreateEAPPolicyForm createEAPPolicyForm = (CreateEAPPolicyForm)form;
			createEAPPolicyForm.setStatus("1");
			EAPConfigBLManager eapConfigBLManager = new EAPConfigBLManager();
			String userName = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getSystemUserName();
			createEAPPolicyForm.setDescription(getDefaultDescription(userName));
			List<EAPConfigData> eapConfigurationList = eapConfigBLManager.getEapConfigurationList();

			createEAPPolicyForm.setEapConfigurationList(eapConfigurationList);
			createEAPPolicyForm.setRejectOnRejectItemNotFound(String.valueOf(Boolean.TRUE));
			createEAPPolicyForm.setTrimUserIdentity(Boolean.TRUE.toString());
			
			/** Fetching grace policy **/
			GracePolicyBLManager gracePolicyBLManager = new GracePolicyBLManager();
			List<GracepolicyData> gracePolicyList = gracePolicyBLManager.getGracePolicyList();
			createEAPPolicyForm.setGracePolicyList(gracePolicyList);
			
			/** Fetching Diameter Concurrency **/
			DiameterConcurrencyBLManager diameterConcurrencyBLManager = new DiameterConcurrencyBLManager();
			List<DiameterConcurrencyData> diameterConcurrencyDataList = diameterConcurrencyBLManager.getDiameterConcurrencyDataList();
			createEAPPolicyForm.setDiameterConcurrencyDataList(diameterConcurrencyDataList);
			
			/* Driver Script */
			ScriptBLManager scriptBLManager = new ScriptBLManager();
			List<ScriptInstanceData> driverScriptList = (List<ScriptInstanceData>) scriptBLManager.getScriptInstanceDataByTypeId(ScriptTypesConstants.DRIVER_SCRIPT);
		
			createEAPPolicyForm.setDriverScriptList(driverScriptList);
			
			/* Fetching diameter related plugin list */
			PluginBLManager pluginBLManager = new PluginBLManager();
			List<PluginInstData> pluginInstDataList = pluginBLManager.getDiameterPluginList();
			request.setAttribute("pluginInstDataList", pluginInstDataList);
			
			request.getSession().setAttribute("createEAPPolicyForm", createEAPPolicyForm);
			return mapping.findForward(INITCREATEEAPPOLICY);
		}catch(ActionNotPermitedException e){
			Logger.logError(MODULE, "Restricted to do action.");
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
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
