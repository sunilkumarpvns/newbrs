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
import com.elitecore.elitesm.blmanager.servicepolicy.diameter.DiameterNASPolicyBLManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.diameter.diameterconcurrency.data.DiameterConcurrencyData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyAcctDriverRelData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyAdditionalDriverRelData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyAuthDriverRelData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyAuthMethodRelData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyInstData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servicepolicy.diameter.naspolicy.forms.ViewNASServicePolicyForm;

public class ViewNASServicePolicyAction extends BaseWebAction{
	private static final String SUCCESS_FORWARD = "viewNASServicePolicyDetail";
	private static String ACTION_ALIAS = ConfigConstant.VIEW_NAS_SERVICE_POLICY;
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		try{
			ViewNASServicePolicyForm viewNASServicePolicyForm = (ViewNASServicePolicyForm) form;
			DiameterNASPolicyBLManager diameterNasBlManager = new DiameterNASPolicyBLManager();
			NASPolicyInstData policyData = diameterNasBlManager.getDiameterServicePolicyDataByPolicyId(viewNASServicePolicyForm.getNasPolicyId());
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			
			// Authentication DriverList 
			List<NASPolicyAuthDriverRelData> authDriverRelDataList = diameterNasBlManager.getDiameterServicePolicyAuthDriverRel(viewNASServicePolicyForm.getNasPolicyId());
			policyData.setNasPolicyAuthDriverRelList(authDriverRelDataList);
			
			//Additional Driver 
			 List<NASPolicyAdditionalDriverRelData> nasPolicyAdditionalDriverRelDataList = diameterNasBlManager.getDiameterServicePolicyAdditionalDriver(viewNASServicePolicyForm.getNasPolicyId());
			 policyData.setNasPolicyAdditionalDriverRelDataList(nasPolicyAdditionalDriverRelDataList);
			 
			// Authentication DriverList
			List<NASPolicyAuthMethodRelData> authMethodRelDataList = diameterNasBlManager.getDiameterServicePolicyAuthMethodRel(viewNASServicePolicyForm.getNasPolicyId());
			policyData.setNasPolicyAuthMethodRelList(authMethodRelDataList);
			
			// Accounting Driver
			List<NASPolicyAcctDriverRelData> acctdriverRelDataList = diameterNasBlManager.getDiameterServicePolicyAcctDriverRel(viewNASServicePolicyForm.getNasPolicyId());
			policyData.setNasPolicyAcctDriverRelList(acctdriverRelDataList);
			
			/* fetch Diameter Concurrency Data */
			DiameterConcurrencyData diameterConcurrencyData = new  DiameterConcurrencyData();
			DiameterConcurrencyData additionalDiameterConcurrencyData = new  DiameterConcurrencyData();
			DiameterConcurrencyBLManager blManager = new DiameterConcurrencyBLManager();
			if( policyData.getDiameterConcurrency() != null ){
				diameterConcurrencyData = blManager.getDiameterConcurrencyDataById(policyData.getDiameterConcurrency());
			}
			
			if( policyData.getAdditionalDiameterConcurrency() != null ){
				additionalDiameterConcurrencyData = blManager.getDiameterConcurrencyDataById(policyData.getAdditionalDiameterConcurrency());
			}
			
			request.setAttribute("nasPolicyInstData",policyData);
			request.setAttribute("diameterConcurrencyData", diameterConcurrencyData);
			request.setAttribute("additionalDiameterConcurrencyData", additionalDiameterConcurrencyData);
		
			doAuditing(staffData, ACTION_ALIAS);
			return mapping.findForward(SUCCESS_FORWARD);
		} catch(Exception e){
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
