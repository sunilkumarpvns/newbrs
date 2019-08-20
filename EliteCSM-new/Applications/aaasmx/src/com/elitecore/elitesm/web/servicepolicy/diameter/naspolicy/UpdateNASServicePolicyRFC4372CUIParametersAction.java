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
import com.elitecore.elitesm.blmanager.servicepolicy.diameter.DiameterNASPolicyBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.diameter.diameterconcurrency.data.DiameterConcurrencyData;
import com.elitecore.elitesm.datamanager.servermgr.gracepolicy.data.GracepolicyData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyInstData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servicepolicy.diameter.naspolicy.forms.UpdateNASServicePolicyAuthorizationParamsForm;
import com.elitecore.elitesm.web.servicepolicy.diameter.naspolicy.forms.UpdateNASServicePolicyRFC4372CUIParamsForm;

public class UpdateNASServicePolicyRFC4372CUIParametersAction extends BaseWebAction{
	private static String ACTION_ALIAS = ConfigConstant.UPDATE_NAS_SERVICE_POLICY_AUTHORIZATION_PARAMS;
	private static final String SUCCESS_FORWARD = "updateNASServicePolicyRFC4372Params";
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		try {
			checkActionPermission(request, ACTION_ALIAS);
			Logger.logTrace(MODULE,"Enter execute method of "+getClass().getName());
			UpdateNASServicePolicyRFC4372CUIParamsForm nasPolicyForm = (UpdateNASServicePolicyRFC4372CUIParamsForm)form;
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			
			if(nasPolicyForm.getAction() == null) {
				DiameterNASPolicyBLManager diameterNasBlManager = new DiameterNASPolicyBLManager();
				NASPolicyInstData policyData = diameterNasBlManager.getDiameterServicePolicyDataByPolicyId(nasPolicyForm.getNasPolicyId());
				
				nasPolicyForm.setCui(policyData.getCui());
				nasPolicyForm.setCuiResponseAttributes(policyData.getCuiResponseAttributes());
				nasPolicyForm.setAdvancedCuiExpression(policyData.getAdvancedCuiExpression());
				nasPolicyForm.setAuditUId(policyData.getAuditUId());
				nasPolicyForm.setName(policyData.getName());
				
				request.setAttribute("nasPolicyForm",nasPolicyForm);
				request.setAttribute("nasPolicyInstData",policyData);
				return mapping.findForward(SUCCESS_FORWARD);
			} else if(nasPolicyForm.getAction().equals("update")){
				DiameterNASPolicyBLManager diameterNasBlManager = new DiameterNASPolicyBLManager();
				NASPolicyInstData policyData = diameterNasBlManager.getDiameterServicePolicyDataByPolicyId(nasPolicyForm.getNasPolicyId());
				
				policyData.setNasPolicyId(nasPolicyForm.getNasPolicyId());
				policyData.setCui(nasPolicyForm.getCui());
				policyData.setCuiResponseAttributes(nasPolicyForm.getCuiResponseAttributes());
				policyData.setAdvancedCuiExpression(nasPolicyForm.getAdvancedCuiExpression());
				policyData.setName(nasPolicyForm.getName());
				policyData.setAuditUId(nasPolicyForm.getAuditUId());
				
				staffData.setAuditId(policyData.getAuditUId());
				staffData.setAuditName(policyData.getName());
				
				diameterNasBlManager.updateRFC4372CUIParams(policyData,staffData,ACTION_ALIAS);
				
				request.setAttribute("responseUrl","/viewNASServicePolicyDetail.do?nasPolicyId="+nasPolicyForm.getNasPolicyId());
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
