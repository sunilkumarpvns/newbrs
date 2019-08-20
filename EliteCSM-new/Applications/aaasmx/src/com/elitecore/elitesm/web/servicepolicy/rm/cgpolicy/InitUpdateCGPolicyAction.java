package com.elitecore.elitesm.web.servicepolicy.rm.cgpolicy;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.servermgr.script.ScriptBLManager;
import com.elitecore.elitesm.blmanager.servicepolicy.rm.CGPolicyBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptInstanceData;
import com.elitecore.elitesm.datamanager.servicepolicy.rm.cgpolicy.data.CGPolicyData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.ScriptTypesConstants;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servicepolicy.rm.cgpolicy.forms.UpdateCGPolicyForm;

public class InitUpdateCGPolicyAction extends BaseWebAction {
	private static final String FAILURE_FORWARD = "failure";               
	private static final String MODULE ="CGPOLICY";
	private static final String INITUPDATECGPOLICY = "initUpdateCGPolicy"; 
	private static final String ACTION_ALIAS = ConfigConstant.VIEW_CG_POLICY;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		ActionMessages messages = new ActionMessages();
		try {
						
		//	 checkActionPermission(request,ACTION_ALIAS);
			 Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName()); 
			 
			 UpdateCGPolicyForm policyForm = (UpdateCGPolicyForm)form;
			 
			 CGPolicyBLManager blManager = new CGPolicyBLManager();			 
			 CGPolicyData cgPolicyData = blManager.getPolicyDataByPolicyId(policyForm.getPolicyId());
			 IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				
			 policyForm.setName(cgPolicyData.getName());
			 policyForm.setDescription(cgPolicyData.getDescription());
			 policyForm.setPolicyId(cgPolicyData.getPolicyId());
			 policyForm.setRuleSet(cgPolicyData.getRuleSet());
			 policyForm.setStatus(cgPolicyData.getStatus());
			 
			 if(cgPolicyData.getStatus().equals(BaseConstant.SHOW_STATUS_ID)){
				 policyForm.setStatus(BaseConstant.SHOW_STATUS);
			 }else{
				 policyForm.setStatus(BaseConstant.HIDE_STATUS);
			 }
			 
			 policyForm.setDriversList(cgPolicyData.getDriverList());
			 policyForm.setScript(cgPolicyData.getScript());
			 policyForm.setAuditUId(cgPolicyData.getAuditUId());
			 
			 /* Driver Script */
			 ScriptBLManager scriptBLManager = new ScriptBLManager();
			 List<ScriptInstanceData> driverScriptList = (List<ScriptInstanceData>) scriptBLManager.getScriptInstanceDataByTypeId(ScriptTypesConstants.DRIVER_SCRIPT);
			
			 policyForm.setDriverScriptList(driverScriptList);
			 
			 request.setAttribute("cgPolicyData",cgPolicyData);
			 request.setAttribute("policyForm",policyForm);
		     return mapping.findForward(INITUPDATECGPOLICY);             
		}catch (ActionNotPermitedException e) {
            Logger.logError(MODULE, "Error during Data Manager operation ");
            Logger.logTrace(MODULE, e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("general.user.restricted");
            messages.add("information", message);
        }catch (Exception authExp) {                                                                                           
		    Logger.logError(MODULE, "Error during Data Manager operation , reason :" + authExp.getMessage());                              
		    Logger.logTrace(MODULE, authExp);                                                                                               
		    ActionMessage message = new ActionMessage("~~~~key~~~");                                                         
		    messages.add("information", message);                                                                                           
		                                                                                                     
		}                    
        saveErrors(request, messages); 
		return mapping.findForward(FAILURE_FORWARD); 
	}
}
