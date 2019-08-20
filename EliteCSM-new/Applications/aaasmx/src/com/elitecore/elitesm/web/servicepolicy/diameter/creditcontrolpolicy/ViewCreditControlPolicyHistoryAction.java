package com.elitecore.elitesm.web.servicepolicy.diameter.creditcontrolpolicy; 
  
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
import com.elitecore.elitesm.blmanager.servicepolicy.diameter.CreditControlPolicyBLManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servicepolicy.auth.data.AuthPolicyInstData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.creditcontrolpolicy.data.CreditControlPolicyData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.history.DatabaseHistoryData;
import com.elitecore.elitesm.web.servicepolicy.diameter.creditcontrolpolicy.forms.UpdateCreditControlPolicyForm;
                                                                               
public class ViewCreditControlPolicyHistoryAction extends BaseWebAction { 
	                                                                       
	private static final String FAILURE_FORWARD = "failure";               
	private static final String MODULE ="CREDITCONTROLPOLICY";
	private static final String VIEW_HISTORY = "viewCcpolicyHistory"; 
	private static final String ACTION_ALIAS=ConfigConstant.VIEW_CREDIT_CONTROL_SERVICE_POLICY;
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
			 policyForm.setStatus(policyData.getStatus());
			 policyForm.setDriversList(policyData.getDriverList());
			 policyForm.setScript(policyData.getScript());
		
			 String strPolicyID = request.getParameter("policyId");
				String policyID;
				
				if(strPolicyID != null){
					policyID = strPolicyID;
				}else{
					policyID=policyForm.getPolicyId();
				}
				
				if( Strings.isNullOrBlank(policyID) == false){
					AuthPolicyInstData authPolicyInstData = new AuthPolicyInstData();
					
					authPolicyInstData.setAuthPolicyId(policyID);
					
					String actionAlias = ACTION_ALIAS;
					
					HistoryBLManager historyBlManager= new HistoryBLManager();
					
					String strAuditUid = request.getParameter("auditUid");
					String strSytemAuditId=request.getParameter("systemAuditId");
					String name=request.getParameter("name");
					
					if(strSytemAuditId != null){
						request.setAttribute("systemAuditId", strSytemAuditId);
					}
					
					if(policyID != null && Strings.isNullOrBlank(strAuditUid) == false){
						
						staffData.setAuditName(policyData.getName());
						staffData.setAuditId(policyData.getAuditUId());
						
						List<DatabaseHistoryData> lstDatabaseDSHistoryDatas=historyBlManager.getHistoryData(strAuditUid);
						
						request.setAttribute("name", name);
						request.setAttribute("lstDatabaseDSHistoryDatas", lstDatabaseDSHistoryDatas);
					}
				}
			 
			 request.setAttribute("ccPolicyData",policyData);
			 request.setAttribute("policyForm",policyForm);
			 request.setAttribute("pageAction","view");
			 
		     return mapping.findForward(VIEW_HISTORY);             
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
