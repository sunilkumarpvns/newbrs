package com.elitecore.elitesm.web.servicepolicy.diameter.eappolicy;

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
import com.elitecore.elitesm.blmanager.history.HistoryBLManager;
import com.elitecore.elitesm.blmanager.servicepolicy.diameter.EAPPolicyBLManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.diameter.diameterconcurrency.data.DiameterConcurrencyData;
import com.elitecore.elitesm.datamanager.servermgr.eap.data.EAPConfigData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.eappolicy.data.EAPPolicyData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.history.DatabaseHistoryData;
import com.elitecore.elitesm.web.servicepolicy.diameter.eappolicy.forms.ViewEAPPolicyDetailsForm;

public class ViewEAPPolicyDetailsHistoryAction extends BaseWebAction {
	private static final String VIEW_FORWARD = "viewEAPDetailsHistoryData";
	private static final String FAILURE_FORWARD = "failure";
	private static final String MODULE="ViewEAPPolicyDetails";
	private static final String ACTION_ALIAS=ConfigConstant.VIEW_DIAMETER_EAP_POLICY;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		if((checkAccess(request, ACTION_ALIAS))){		
			Logger.logTrace(MODULE,"Enter execute method of"+getClass().getName());
			try{
				ViewEAPPolicyDetailsForm viewEAPPolicyDetailsForm = (ViewEAPPolicyDetailsForm)form;

				EAPPolicyBLManager eapPolicyBLManager = new EAPPolicyBLManager();
			
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				String actionAlias = ACTION_ALIAS;
				
				String strEAPPolicyID = request.getParameter("policyId");
				String eapPolicyId;
				
				if(strEAPPolicyID != null){
					eapPolicyId = strEAPPolicyID;
				}else{
					eapPolicyId=viewEAPPolicyDetailsForm.getPolicyId();
				}
				
				if(Strings.isNullOrBlank(eapPolicyId) == false){
					EAPPolicyData eapPolicyInstData = new EAPPolicyData();
					eapPolicyInstData.setEapPolicyId(eapPolicyId);
					eapPolicyInstData = eapPolicyBLManager.getEAPPolicyById(eapPolicyId);
					
					EAPConfigData eapConfigData = eapPolicyBLManager.getEAPConfigInstance(eapPolicyInstData);
					if(eapConfigData==null){
						eapConfigData= new EAPConfigData();
						eapConfigData.setName("");
					}
					
					eapPolicyInstData = eapPolicyBLManager.getEAPPolicyById(eapPolicyId);
					
					HistoryBLManager historyBlManager= new HistoryBLManager();
					
					String strAuditUid = request.getParameter("auditUid");
					String strSytemAuditId=request.getParameter("systemAuditId");
					String name=request.getParameter("name");
					
					if(strSytemAuditId != null){
						request.setAttribute("systemAuditId", strSytemAuditId);
					}
					
					if(eapPolicyId != null && Strings.isNullOrBlank(strAuditUid) == false){
						
						staffData.setAuditName(eapPolicyInstData.getName());
						staffData.setAuditId(eapPolicyInstData.getAuditUId());
						
						List<DatabaseHistoryData> lstDatabaseDSHistoryDatas=historyBlManager.getHistoryData(strAuditUid);
						
						request.setAttribute("name", name);
						request.setAttribute("lstDatabaseDSHistoryDatas", lstDatabaseDSHistoryDatas);
					}
					
					/* fetch Diameter Concurrency Data */
					DiameterConcurrencyData diameterConcurrencyData = new  DiameterConcurrencyData();
					DiameterConcurrencyData additionalDiameterConcurrencyData = new  DiameterConcurrencyData();
					DiameterConcurrencyBLManager blManager = new DiameterConcurrencyBLManager();
					if( eapPolicyInstData.getDiameterConcurrency() != null ){
						diameterConcurrencyData = blManager.getDiameterConcurrencyDataById(eapPolicyInstData.getDiameterConcurrency());
					}
					
					if( eapPolicyInstData.getAdditionalDiameterConcurrency() != null ){
						additionalDiameterConcurrencyData = blManager.getDiameterConcurrencyDataById(eapPolicyInstData.getAdditionalDiameterConcurrency());
					}
					
					request.setAttribute("eapConfigData",eapConfigData);
					request.setAttribute("eapPolicyData",eapPolicyInstData);
					request.setAttribute("diameterConcurrencyData", diameterConcurrencyData);
					request.setAttribute("additionalDiameterConcurrencyData", additionalDiameterConcurrencyData);
				}
				return mapping.findForward(VIEW_FORWARD);
			}catch(Exception e){
				e.printStackTrace();
				Logger.logTrace(MODULE,e.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);

			}
			return mapping.findForward(FAILURE_FORWARD);
		}else{
	        Logger.logError(MODULE, "Error during Data Manager operation ");
	        ActionMessage message = new ActionMessage("general.user.restricted");
	        ActionMessages messages = new ActionMessages();
	        messages.add("information", message);
	        saveErrors(request, messages);
			
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}

}
