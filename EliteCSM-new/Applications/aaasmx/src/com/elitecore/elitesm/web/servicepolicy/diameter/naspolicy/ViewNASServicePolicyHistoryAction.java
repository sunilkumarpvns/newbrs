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
import com.elitecore.elitesm.blmanager.history.HistoryBLManager;
import com.elitecore.elitesm.blmanager.servicepolicy.diameter.DiameterNASPolicyBLManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
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
import com.elitecore.elitesm.web.history.DatabaseHistoryData;
import com.elitecore.elitesm.web.servicepolicy.diameter.naspolicy.forms.ViewNASServicePolicyForm;

public class ViewNASServicePolicyHistoryAction extends BaseWebAction{
	private static final String VIEW_HISTORY_FORWARD = "viewNASServicePolicyHistory";
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
			
			String strNasPolicyID = request.getParameter("nasPolicyId");
			String nasPolicyID;
			
			if(strNasPolicyID != null){
				nasPolicyID = strNasPolicyID;
			}else{
				nasPolicyID=viewNASServicePolicyForm.getNasPolicyId();
			}
			
			if( Strings.isNullOrBlank(nasPolicyID) == false ){
				
				String actionAlias = ACTION_ALIAS;
				
				HistoryBLManager historyBlManager= new HistoryBLManager();
				
				String strAuditUid = request.getParameter("auditUid");
				String strSytemAuditId=request.getParameter("systemAuditId");
				String name=request.getParameter("name");
				
				if(strSytemAuditId != null){
					request.setAttribute("systemAuditId", strSytemAuditId);
				}
				
				if(nasPolicyID != null && Strings.isNullOrBlank(strAuditUid) == false){
					
					staffData.setAuditName(policyData.getName());
					staffData.setAuditId(policyData.getAuditUId());
					
					List<DatabaseHistoryData> lstDatabaseDSHistoryDatas=historyBlManager.getHistoryData(strAuditUid);
					
					request.setAttribute("name", name);
					request.setAttribute("lstDatabaseDSHistoryDatas", lstDatabaseDSHistoryDatas);
				}
			}
			request.setAttribute("nasPolicyInstData",policyData);
			return mapping.findForward(VIEW_HISTORY_FORWARD);
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
