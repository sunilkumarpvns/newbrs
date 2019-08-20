package com.elitecore.elitesm.web.servicepolicy.diameter.naspolicy;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
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

public class UpdateNASServicePolicyAuthorizationParametersAction extends BaseWebAction{
	private static String ACTION_ALIAS = ConfigConstant.UPDATE_NAS_SERVICE_POLICY_AUTHORIZATION_PARAMS;
	private static final String SUCCESS_FORWARD = "updateNASServicePolicyAuthorizationParams";
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		try {
			checkActionPermission(request, ACTION_ALIAS);
			Logger.logTrace(MODULE,"Enter execute method of "+getClass().getName());
			UpdateNASServicePolicyAuthorizationParamsForm nasPolicyForm = (UpdateNASServicePolicyAuthorizationParamsForm) form;
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			
			if(nasPolicyForm.getAction() == null) {
				DiameterNASPolicyBLManager diameterNasBlManager = new DiameterNASPolicyBLManager();
				NASPolicyInstData policyData = diameterNasBlManager.getDiameterServicePolicyDataByPolicyId(nasPolicyForm.getNasPolicyId());
				
				nasPolicyForm.setActionOnPolicyNotFound(policyData.getActionOnPolicyNotFound());
				nasPolicyForm.setRejectOnCheckItemNotFound(policyData.getRejectOnCheckItemNotFound());
				nasPolicyForm.setRejectOnRejectItemNotFound(policyData.getRejectOnRejectItemNotFound());
				nasPolicyForm.setAuditUId(policyData.getAuditUId());
				nasPolicyForm.setName(policyData.getName());
				nasPolicyForm.setWimax(policyData.getWimax());
				nasPolicyForm.setGracePolicy(policyData.getGracePolicy());
				nasPolicyForm.setDefaultSessionTimeout(policyData.getDefaultSessionTimeout());
				
				if(policyData.getDiameterConcurrency() == null){
					nasPolicyForm.setDiameterConcurrency(null);
				}else{
					nasPolicyForm.setDiameterConcurrency(policyData.getDiameterConcurrency());
				}
				
				if(policyData.getAdditionalDiameterConcurrency() == null){
					nasPolicyForm.setAdditionalDiameterConcurrency(null);
				}else{
					nasPolicyForm.setAdditionalDiameterConcurrency(policyData.getAdditionalDiameterConcurrency());
				}
				
				GracePolicyBLManager gracePolicyBLManager = new GracePolicyBLManager();
				List<GracepolicyData> gracePolicyList = gracePolicyBLManager.getGracePolicyList();
				nasPolicyForm.setGracePolicyList(gracePolicyList);
				
				/** Fetching Diameter Concurrency **/
				DiameterConcurrencyBLManager diameterConcurrencyBLManager = new DiameterConcurrencyBLManager();
				List<DiameterConcurrencyData> diameterConcurrencyDataList = diameterConcurrencyBLManager.getDiameterConcurrencyDataList();
				nasPolicyForm.setDiameterConcurrencyDataList(diameterConcurrencyDataList);
				
				request.setAttribute("nasPolicyForm",nasPolicyForm);
				request.setAttribute("nasPolicyInstData",policyData);
				return mapping.findForward(SUCCESS_FORWARD);
			} else if(nasPolicyForm.getAction().equals("update")){
				DiameterNASPolicyBLManager diameterNasBlManager = new DiameterNASPolicyBLManager();
				NASPolicyInstData policyData = diameterNasBlManager.getDiameterServicePolicyDataByPolicyId(nasPolicyForm.getNasPolicyId());
				
				policyData.setNasPolicyId(nasPolicyForm.getNasPolicyId());
				policyData.setRejectOnCheckItemNotFound(nasPolicyForm.getRejectOnCheckItemNotFound());
				policyData.setRejectOnRejectItemNotFound(nasPolicyForm.getRejectOnRejectItemNotFound());
				policyData.setActionOnPolicyNotFound(nasPolicyForm.getActionOnPolicyNotFound());
				policyData.setName(nasPolicyForm.getName());
				policyData.setAuditUId(nasPolicyForm.getAuditUId());
				
				policyData.setWimax(nasPolicyForm.getWimax());
				policyData.setGracePolicy(nasPolicyForm.getGracePolicy());
				
				if(Strings.isNullOrBlank(nasPolicyForm.getDiameterConcurrency()) || "0".equals(nasPolicyForm.getDiameterConcurrency())) {
					policyData.setDiameterConcurrency(null);
				}else{
					policyData.setDiameterConcurrency(nasPolicyForm.getDiameterConcurrency());
				}
				
				if(Strings.isNullOrBlank(nasPolicyForm.getAdditionalDiameterConcurrency()) || "0".equals(nasPolicyForm.getAdditionalDiameterConcurrency())) {
					policyData.setAdditionalDiameterConcurrency(null);
				}else{
					policyData.setAdditionalDiameterConcurrency(nasPolicyForm.getAdditionalDiameterConcurrency());
				}
				policyData.setDefaultSessionTimeout(nasPolicyForm.getDefaultSessionTimeout());
				
				staffData.setAuditId(policyData.getAuditUId());
				staffData.setAuditName(policyData.getName());
				
				diameterNasBlManager.updateAuthorizationParams(policyData,staffData,ACTION_ALIAS);
				
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
