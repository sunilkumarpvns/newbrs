package com.elitecore.elitesm.web.servicepolicy.diameter.creditcontrolpolicy; 
  
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.servermgr.plugins.PluginBLManager;
import com.elitecore.elitesm.blmanager.servermgr.script.ScriptBLManager;
import com.elitecore.elitesm.blmanager.servicepolicy.diameter.CreditControlPolicyBLManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.data.PluginInstData;
import com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptInstanceData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.creditcontrolpolicy.data.CCPolicyPluginConfig;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.creditcontrolpolicy.data.CreditControlPolicyData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.ScriptTypesConstants;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servicepolicy.diameter.creditcontrolpolicy.forms.UpdateCreditControlPolicyForm;
                                                                               
public class InitUpdateCreditControlPolicyAction extends BaseWebAction { 
	                                                                       
	private static final String FAILURE_FORWARD = "failure";               
	private static final String MODULE ="CREDITCONTROLPOLICY";
	private static final String INITUPDATECREDITCONTROLPOLICY = "initUpdateCreditControlPolicy"; 
	private static final String VIEW_ACTION=ConfigConstant.VIEW_CREDIT_CONTROL_SERVICE_POLICY;
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{ 
		try {
			 Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName()); 

			 UpdateCreditControlPolicyForm policyForm = (UpdateCreditControlPolicyForm)form;
			 
			 CreditControlPolicyBLManager blManager = new CreditControlPolicyBLManager();			 
			 IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			 CreditControlPolicyData policyData = blManager.getPolicyDataByPolicyId(policyForm.getPolicyId(), staffData);
				
			 policyForm.setName(policyData.getName());
			 policyForm.setDescription(policyData.getDescription());
			 policyForm.setPolicyId(policyData.getPolicyId());
			 policyForm.setRuleSet(policyData.getRuleSet());
			 policyForm.setSessionManagement(policyData.getSessionManagement());
			 policyForm.setStatus(policyData.getStatus());
			 policyForm.setDriversList(policyData.getDriverList());
			 policyForm.setScript(policyData.getScript());
			 policyForm.setAuditUId(policyData.getAuditUId());
			 policyForm.setCcResponseAttributesSet(policyData.getCcResponseAttributesSet());
			 policyForm.setDefaultResponseBehaviorArgument(policyData.getDefaultResponseBehaviorArgument());
			 policyForm.setDefaultResponseBehaviour(policyData.getDefaultResponseBehaviour());
			 /** fetching pre and post plugins */
			 List<CCPolicyPluginConfig> ccPolicyPluginConfigList= blManager.getPolicyPluginConfigList(policyForm.getPolicyId());
			 policyData.setCcPolicyPluginConfigList(ccPolicyPluginConfigList);
			 
			 /* Driver Script */
			 ScriptBLManager scriptBLManager = new ScriptBLManager();
			 List<ScriptInstanceData> driverScriptList = (List<ScriptInstanceData>) scriptBLManager.getScriptInstanceDataByTypeId(ScriptTypesConstants.DRIVER_SCRIPT);
			
			 policyForm.setDriverScriptList(driverScriptList);
			 
			 request.setAttribute("ccPolicyData",policyData);
			 request.setAttribute("policyForm",policyForm);
			 
			 String pageAction = request.getParameter("pageAction");
			 if(pageAction.equals("update")){
				 	/* Fetching diameter related plugin list */
					PluginBLManager pluginBLManager = new PluginBLManager();
					List<PluginInstData> pluginInstDataList = pluginBLManager.getDiameterPluginList();
				
					request.setAttribute("pluginInstDataList", pluginInstDataList);
			 }
			 request.setAttribute("pageAction",pageAction);
			 
		     return mapping.findForward(INITUPDATECREDITCONTROLPOLICY);             
		}catch (Exception authExp) {                                                                                           
		    Logger.logError(MODULE, "Error during Data Manager operation , reason :" + authExp.getMessage());                              
		    Logger.logTrace(MODULE, authExp);                                                                                               
		    ActionMessage message = new ActionMessage("~~~~key~~~");                                                         
		    ActionMessages messages = new ActionMessages();                                                                                 
		    messages.add("information", message);                                                                                           
		    saveErrors(request, messages);                                                                                                  
		}                                                                                                                                   
		return mapping.findForward(FAILURE_FORWARD); 
	}
}
