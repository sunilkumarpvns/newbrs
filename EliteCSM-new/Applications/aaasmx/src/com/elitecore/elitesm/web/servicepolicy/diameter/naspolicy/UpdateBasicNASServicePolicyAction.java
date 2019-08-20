package com.elitecore.elitesm.web.servicepolicy.diameter.naspolicy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.servicepolicy.diameter.DiameterNASPolicyBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyInstData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servicepolicy.diameter.naspolicy.forms.UpdateNasServicePolicyBasicForm;

public class UpdateBasicNASServicePolicyAction extends BaseWebAction{
	
	private static final String SUCCESS_FORWARD = "updateBasicNASServicePolicyDetail";
	private static String ACTION_ALIAS = ConfigConstant.UPDATE_NAS_SERVICE_POLICY;

	private static final String MODULE = "UpdateBasicNASServicePolicyAction";
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		try{
			checkActionPermission(request, ACTION_ALIAS);
			Logger.logTrace(MODULE,"Enter execute method of "+getClass().getName());
			UpdateNasServicePolicyBasicForm updateBasicNasServicePolicyForm = (UpdateNasServicePolicyBasicForm)form;
			Logger.logDebug(MODULE, "updateASServicePolicyForm     : "+updateBasicNasServicePolicyForm);
			DiameterNASPolicyBLManager diameterNasBlManager = new DiameterNASPolicyBLManager();
			
			if(updateBasicNasServicePolicyForm.getAction() == null) {
				NASPolicyInstData policyData = diameterNasBlManager.getDiameterServicePolicyDataByPolicyId(updateBasicNasServicePolicyForm.getNasPolicyId());			
				
				updateBasicNasServicePolicyForm.setDescription(policyData.getDescription());
				updateBasicNasServicePolicyForm.setName(policyData.getName());
				updateBasicNasServicePolicyForm.setNasPolicyId(policyData.getNasPolicyId());
				updateBasicNasServicePolicyForm.setRuleSet(policyData.getRuleSet());
				updateBasicNasServicePolicyForm.setSessionManagement(policyData.getSessionManagement());
				updateBasicNasServicePolicyForm.setRequestType(policyData.getRequestType());
				updateBasicNasServicePolicyForm.setAuditUId(policyData.getAuditUId());
				
				updateBasicNasServicePolicyForm.setDefaultResponseBehaviourArgument(policyData.getDefaultResponseBehaviourArgument());
				updateBasicNasServicePolicyForm.setDefaultResponseBehaviour(policyData.getDefaultResponseBehaviour());
				
				request.setAttribute("nasPolicyInstData",policyData);
				return mapping.findForward(SUCCESS_FORWARD);
			}else if(updateBasicNasServicePolicyForm.getAction().equals("update")){
				
				NASPolicyInstData data = diameterNasBlManager.getDiameterServicePolicyDataByPolicyId(updateBasicNasServicePolicyForm.getNasPolicyId());			
				data.setName(updateBasicNasServicePolicyForm.getName());
				data.setDescription(updateBasicNasServicePolicyForm.getDescription());
				data.setRuleSet(updateBasicNasServicePolicyForm.getRuleSet());
				data.setSessionManagement(updateBasicNasServicePolicyForm.getSessionManagement());
				data.setNasPolicyId(updateBasicNasServicePolicyForm.getNasPolicyId());
				data.setRequestType(updateBasicNasServicePolicyForm.getRequestType());
				data.setAuditUId(updateBasicNasServicePolicyForm.getAuditUId());
				data.setDefaultResponseBehaviourArgument(updateBasicNasServicePolicyForm.getDefaultResponseBehaviourArgument());
				data.setDefaultResponseBehaviour(updateBasicNasServicePolicyForm.getDefaultResponseBehaviour());
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				
				DiameterNASPolicyBLManager blmanager = new DiameterNASPolicyBLManager();
				
				staffData.setAuditId(data.getAuditUId());
				staffData.setAuditName(data.getName());
				
				blmanager.updateNasPolicyBasicDetails(data,staffData,ACTION_ALIAS);
				
				request.setAttribute("responseUrl","/viewNASServicePolicyDetail.do?nasPolicyId="+updateBasicNasServicePolicyForm.getNasPolicyId());
                ActionMessage message = new ActionMessage("diameter.naspolicy.basic.update.success");
                ActionMessages messages = new ActionMessages();
                messages.add("information", message);
                saveMessages(request, messages);
                return mapping.findForward(SUCCESS);
			}			
		}catch(ActionNotPermitedException e){
			Logger.logError(MODULE,"Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}catch(Exception e){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);

		}
		return mapping.findForward(FAILURE);
	}

}
