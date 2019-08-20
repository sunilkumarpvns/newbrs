package com.elitecore.elitesm.web.servicepolicy.diameter.creditcontrolpolicy; 

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.servicepolicy.diameter.CreditControlPolicyBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.creditcontrolpolicy.data.CreditControlPolicyData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servicepolicy.diameter.creditcontrolpolicy.forms.SearchCreditControlPolicyForm;

public class SearchCreditControlPolicyAction extends BaseWebAction { 

	private static final String SUCCESS_FORWARD = "success";               
	private static final String FAILURE_FORWARD = "failure";               
	private static final String MODULE ="CREDITCONTROLPOLICY";
	private static final String SEARCHCREDITCONTROLPOLICY = "searchCreditControlPolicy";
	private static final String ACTION_ALIAS = ConfigConstant.SEARCH_CREDIT_CONTROL_SERVICE_POLICY;
	private static final String UPDATE_STATUS = ConfigConstant.UPDATE_CREDIT_CONTROL_SERVICE_POLICY_STATUS;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{ 
		try {

			checkActionPermission(request, ACTION_ALIAS);
			Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName()); 

			SearchCreditControlPolicyForm ccpolicyForm = (SearchCreditControlPolicyForm)form;
			CreditControlPolicyBLManager blManager = new CreditControlPolicyBLManager();
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			CreditControlPolicyData creditControlPolicyData = new CreditControlPolicyData();
			creditControlPolicyData.setStatus("All");
			
			
			int requiredPageNo;
			if(request.getParameter("pageNo") != null){
				requiredPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNo")));
			}else{
				requiredPageNo = new Long(ccpolicyForm.getPageNumber()).intValue();
			}

			if(requiredPageNo == 0)
				requiredPageNo = 1;
			String[] policyIds = request.getParameterValues("select");
			
			Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
			
			if(ccpolicyForm.getAction() != null) {
				if(ccpolicyForm.getAction() != null && ccpolicyForm.getAction().equals("delete")){
					String actionAlias=ConfigConstant.DELETE_CREDIT_CONTROL_SERVICE_POLICY;
					checkActionPermission(request,actionAlias); 
					long currentPageNumber = 0;
					if(policyIds != null && policyIds.length > 0){

						blManager.deleteByID(policyIds, staffData);
						int strPolicyLen = policyIds.length;
						currentPageNumber = getCurrentPageNumberAfterDel(strPolicyLen,ccpolicyForm.getPageNumber(),ccpolicyForm.getTotalPages(),ccpolicyForm.getTotalRecords());
					}
					doAuditing(staffData, actionAlias);
					request.setAttribute("responseUrl","/searchCcpolicy.do?name="+ccpolicyForm.getName()+"&pageNumber="+currentPageNumber+"&totalPages="+ccpolicyForm.getTotalPages()+"&totalRecords="+ccpolicyForm.getTotalRecords());
					ActionMessage message = new ActionMessage("diameter.ccpolicy.delete");
					ActionMessages messages1 = new ActionMessages();
					messages1.add("information",message);
					saveMessages(request,messages1);
					return mapping.findForward(SUCCESS_FORWARD);
				}else if(ccpolicyForm.getAction() != null && ccpolicyForm.getAction().equalsIgnoreCase("show")){
					String actionAlias =UPDATE_STATUS;
					blManager.updateCreditControlPolicyStatus(Arrays.asList(policyIds),BaseConstant.SHOW_STATUS_ID);			
					doAuditing(staffData, actionAlias);
				}else if(ccpolicyForm.getAction() != null && ccpolicyForm.getAction().equalsIgnoreCase("hide")){
					checkActionPermission(request, UPDATE_STATUS);
					String actionAlias =UPDATE_STATUS;
					blManager.updateCreditControlPolicyStatus(Arrays.asList(policyIds),BaseConstant.HIDE_STATUS_ID);			
					doAuditing(staffData, actionAlias);
				}
			}
    		
			if(request.getParameter("resultStatus")!= null){
				ccpolicyForm.setStatus(request.getParameter("resultStatus"));
			}	

			if(ccpolicyForm.getStatus() != null){

				if(ccpolicyForm.getStatus().equals("Active")){				
					creditControlPolicyData.setStatus("CST01");

				}else if(ccpolicyForm.getStatus().equals("Inactive")){
					creditControlPolicyData.setStatus("CST02");
				}
			}
			
			ccpolicyForm.setAction("list");

			String strPolicyName =request.getParameter("name");
			if(strPolicyName != null){
				creditControlPolicyData.setName(strPolicyName);
			}else if(ccpolicyForm.getName() != null){
				creditControlPolicyData.setName(ccpolicyForm.getName());
			}else{
				creditControlPolicyData.setName("");
			}

			PageList pageList = blManager.searchCreditControlPolicy(creditControlPolicyData,requiredPageNo,pageSize);

			ccpolicyForm.setPageNumber(pageList.getCurrentPage());
			ccpolicyForm.setTotalPages(pageList.getTotalPages());
			ccpolicyForm.setTotalRecords(pageList.getTotalItems());
			ccpolicyForm.setSearchList(pageList.getListData());

			request.setAttribute("ccpolicyform",ccpolicyForm);	
			doAuditing(staffData, ACTION_ALIAS);
			return mapping.findForward(SEARCHCREDITCONTROLPOLICY);


		}catch(ActionNotPermitedException e){
			Logger.logError(MODULE,"Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}catch (Exception authExp) {                                                                                           
			Logger.logError(MODULE, "Error during Data Manager operation , reason :" + authExp.getMessage());                              
			Logger.logTrace(MODULE, authExp);  
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(authExp);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.error");                                                         
			ActionMessages messages = new ActionMessages();                                                                                 
			messages.add("information", message);                                                                                           
			saveErrors(request, messages);                                                                                                  
		}                                                                                                                                   
		return mapping.findForward(FAILURE_FORWARD); 
	}
}
