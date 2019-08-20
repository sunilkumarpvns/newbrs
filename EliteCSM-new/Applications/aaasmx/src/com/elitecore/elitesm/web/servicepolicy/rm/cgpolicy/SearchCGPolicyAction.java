package com.elitecore.elitesm.web.servicepolicy.rm.cgpolicy;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.servicepolicy.rm.CGPolicyBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.servicepolicy.rm.cgpolicy.data.CGPolicyData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servicepolicy.rm.cgpolicy.forms.SearchCGPolicyForm;

public class SearchCGPolicyAction extends BaseWebAction {
	private static final String ACTION_ALIAS = ConfigConstant.SEARCH_CG_POLICY;
	private static final String FAILURE_FORWARD = "failure";               
	private static final String MODULE ="CGPOLICY";
	private static final String SEARCHCGPOLICY = "searchCGPolicy"; 
	private static final String SUCCESS_FORWARD = "success";
	private static final String UPDATE_STATUS = ConfigConstant.UPDATE_CG_POLICY_STATUS;

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{ 
		String actionMessage="cgpolicy.search.failure";
		try {
			checkActionPermission(request, ACTION_ALIAS);
			Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName()); 
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			SearchCGPolicyForm searchCGPolicyForm = (SearchCGPolicyForm)form;
			String []policyIds = request.getParameterValues("select");
			CGPolicyBLManager blManager = new CGPolicyBLManager();
			CGPolicyData cgPolicyData = new CGPolicyData();
			cgPolicyData.setStatus("All");

			int requiredPageNo;
			if(request.getParameter("pageNo") != null){
				requiredPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNo")));
			}else{
				requiredPageNo = new Long(searchCGPolicyForm.getPageNumber()).intValue();
			}

			if(requiredPageNo == 0)
				requiredPageNo = 1;
			Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));

			if(searchCGPolicyForm.getAction() != null) {
				if(searchCGPolicyForm.getAction().equals("delete")){
					actionMessage="cgpolicy.delete.failure";
					String actionAlias = ConfigConstant.DELETE_CG_POLICY;
					checkActionPermission(request,actionAlias);

					long currentPageNumber = 0;
					if(policyIds != null && policyIds.length > 0){
						blManager.delete(policyIds, staffData);
						doAuditing(staffData, actionAlias);
						int strPolicyLen = policyIds.length;
						currentPageNumber = getCurrentPageNumberAfterDel(strPolicyLen,searchCGPolicyForm.getPageNumber(),searchCGPolicyForm.getTotalPages(),searchCGPolicyForm.getTotalRecords());
					}
					request.setAttribute("responseUrl","/searchCGPolicy.do?name="+searchCGPolicyForm.getName()+"&pageNumber="+currentPageNumber+"&totalPages="+searchCGPolicyForm.getTotalPages()+"&totalRecords="+searchCGPolicyForm.getTotalRecords());
					ActionMessage message = new ActionMessage("rm.cgpolicy.delete.success");
					ActionMessages messages1 = new ActionMessages();
					messages1.add("information",message);
					saveMessages(request,messages1);
					return mapping.findForward(SUCCESS_FORWARD);
				}else if(searchCGPolicyForm.getAction().equalsIgnoreCase("show")){
					String actionAlias =UPDATE_STATUS;
					blManager.updateCGPolicyStatus(Arrays.asList(policyIds),BaseConstant.SHOW_STATUS_ID);		
					doAuditing(staffData, actionAlias);
				}else if(searchCGPolicyForm.getAction().equalsIgnoreCase("hide")){
					checkActionPermission(request, UPDATE_STATUS);
					String actionAlias =UPDATE_STATUS;
					blManager.updateCGPolicyStatus(Arrays.asList(policyIds),BaseConstant.HIDE_STATUS_ID);
					doAuditing(staffData, actionAlias);
				}
			}

			if(request.getParameter("resultStatus")!= null){
				searchCGPolicyForm.setStatus(request.getParameter("resultStatus"));
			}	

			if(searchCGPolicyForm.getStatus() != null){
				if(searchCGPolicyForm.getStatus().equals("Active")){				
					cgPolicyData.setStatus("CST01");
				}else if(searchCGPolicyForm.getStatus().equals("Inactive")){
					cgPolicyData.setStatus("CST02");
				}
			}

			searchCGPolicyForm.setAction("list");

			String strPolicyName =request.getParameter("name");
			if(strPolicyName != null){
				cgPolicyData.setName(strPolicyName);
			}else if(searchCGPolicyForm.getName() != null){
				cgPolicyData.setName(searchCGPolicyForm.getName());
			}else{
				cgPolicyData.setName("");
			}

			PageList pageList = blManager.searchCGPolicy(cgPolicyData,requiredPageNo,pageSize);

			searchCGPolicyForm.setPageNumber(pageList.getCurrentPage());
			searchCGPolicyForm.setTotalPages(pageList.getTotalPages());
			searchCGPolicyForm.setTotalRecords(pageList.getTotalItems());
			searchCGPolicyForm.setSearchList(pageList.getListData());

			request.setAttribute("searCGPolicyForm",searchCGPolicyForm);	
			doAuditing(staffData, ACTION_ALIAS);
			return mapping.findForward(SEARCHCGPOLICY);

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
