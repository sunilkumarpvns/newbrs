package com.elitecore.elitesm.web.servicepolicy.diameter.eappolicy;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.servicepolicy.diameter.EAPPolicyBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.eappolicy.data.EAPPolicyData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servicepolicy.diameter.eappolicy.forms.SearchEAPPolicyForm;

public class SearchEAPPolicyAction extends BaseWebAction {
	private static final String ACTION_ALIAS = ConfigConstant.SEARCH_DIAMETER_EAP_POLICY;
	private static final String FAILURE_FORWARD = "failure";               
	private static final String MODULE ="EAPPOLICY";
	private static final String SEARCHEAPPOLICY = "searchEAPPolicy"; 
	private static final String SUCCESS_FORWARD = "success";
	private static final String UPDATE_STATUS = ConfigConstant.UPDATE_DIAMETER_EAP_POLICY_STATUS;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{ 
		String actionMessage="eappolicy.search.failure";
		try {
			checkActionPermission(request, ACTION_ALIAS);
			Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName()); 
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			SearchEAPPolicyForm searchEapPolicyForm = (SearchEAPPolicyForm)form;
			String []policyIds = request.getParameterValues("select");
			EAPPolicyBLManager blManager = new EAPPolicyBLManager();
			EAPPolicyData eapPolicyData = new EAPPolicyData();
			eapPolicyData.setStatus("All");
			
			int requiredPageNo;
			if(request.getParameter("pageNo") != null){
				requiredPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNo")));
			}else{
				requiredPageNo = new Long(searchEapPolicyForm.getPageNumber()).intValue();
			}

			if(requiredPageNo == 0)
				requiredPageNo = 1;
			Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
			
			if(searchEapPolicyForm.getAction() != null) {
				if(searchEapPolicyForm.getAction().equals("delete")){
					actionMessage="eappolicy.delete.failure";
					String actionAlias = ConfigConstant.DELETE_DIAMETER_EAP_POLICY;
					checkActionPermission(request,actionAlias);

					long currentPageNumber = 0;
					if(policyIds != null && policyIds.length > 0){
						blManager.delete(policyIds, staffData);
						int strPolicyLen = policyIds.length;
						currentPageNumber = getCurrentPageNumberAfterDel(strPolicyLen,searchEapPolicyForm.getPageNumber(),searchEapPolicyForm.getTotalPages(),searchEapPolicyForm.getTotalRecords());
					}
					doAuditing(staffData, actionAlias);
					request.setAttribute("responseUrl","/searchEAPPolicy.do?name="+searchEapPolicyForm.getName()+"&pageNumber="+currentPageNumber+"&totalPages="+searchEapPolicyForm.getTotalPages()+"&totalRecords="+searchEapPolicyForm.getTotalRecords());
					ActionMessage message = new ActionMessage("diameter.eappolicy.delete");
					ActionMessages messages1 = new ActionMessages();
					messages1.add("information",message);
					saveMessages(request,messages1);
					return mapping.findForward(SUCCESS_FORWARD);
				}else if(searchEapPolicyForm.getAction().equalsIgnoreCase("show")){
					String actionAlias =UPDATE_STATUS;
					blManager.updateEAPPolicyStatus(Arrays.asList(policyIds),BaseConstant.SHOW_STATUS_ID);		
					doAuditing(staffData, actionAlias);
				}else if(searchEapPolicyForm.getAction().equalsIgnoreCase("hide")){
					checkActionPermission(request, UPDATE_STATUS);
					String actionAlias =UPDATE_STATUS;
					blManager.updateEAPPolicyStatus(Arrays.asList(policyIds),BaseConstant.HIDE_STATUS_ID);	
					doAuditing(staffData, actionAlias);
				}
			}
			
			if(request.getParameter("resultStatus")!= null){
				searchEapPolicyForm.setStatus(request.getParameter("resultStatus"));
			}	

			if(searchEapPolicyForm.getStatus() != null){

				if(searchEapPolicyForm.getStatus().equals("Active")){				
					eapPolicyData.setStatus("CST01");

				}else if(searchEapPolicyForm.getStatus().equals("Inactive")){
					eapPolicyData.setStatus("CST02");
				}
			}
			 
			searchEapPolicyForm.setAction("list");
			
			String strPolicyName =request.getParameter("name");
			if(strPolicyName != null){
				eapPolicyData.setName(strPolicyName);
			}else if(searchEapPolicyForm.getName() != null){
				eapPolicyData.setName(searchEapPolicyForm.getName());
			}else{
				eapPolicyData.setName("");
			}
			
			PageList pageList = blManager.searchEAPPolicy(eapPolicyData,requiredPageNo,pageSize);
			
			searchEapPolicyForm.setPageNumber(pageList.getCurrentPage());
			searchEapPolicyForm.setTotalPages(pageList.getTotalPages());
			searchEapPolicyForm.setTotalRecords(pageList.getTotalItems());
			searchEapPolicyForm.setSearchList(pageList.getListData());
			
			request.setAttribute("searEapPolicyForm",searchEapPolicyForm);	
			doAuditing(staffData, ACTION_ALIAS);
			return mapping.findForward(SEARCHEAPPOLICY);

		}catch(ActionNotPermitedException e){
			Logger.logError(MODULE,"Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}catch(DataManagerException e){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage(actionMessage);
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}catch (Exception authExp) {                                                                                           
			Logger.logError(MODULE, "Error during Data Manager operation , reason :" + authExp.getMessage());                              
			Logger.logTrace(MODULE, authExp);                                                                                               
			ActionMessage message = new ActionMessage("diameter.eappolicy.search");                                                         
			ActionMessages messages = new ActionMessages();                                                                                 
			messages.add("information", message);                                                                                           
			saveErrors(request, messages);                                                                                                  
		}                                                                                                                                   
		return mapping.findForward(FAILURE_FORWARD); 
	}
}
