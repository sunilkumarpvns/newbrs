package com.elitecore.elitesm.web.servicepolicy.dynauth;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.servicepolicy.ServicePolicyBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.servicepolicy.dynauth.data.DynAuthPolicyInstData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servicepolicy.dynauth.forms.SearchDynAuthServicePolicyForm;

public class SearchDynAuthServicePolicyAction  extends BaseWebAction{


	private static final String SUCCESS_FORWARD = "success";
	private static final String MODULE = "LIST DATABASEDS ACTION";
	private static final String LIST_FORWARD = "listdynauthpolicies";
	private static final String ACTION_ALIAS = ConfigConstant.SEARCH_DYNAUTH_POLICY;
	private static final String UPDATE_STATUS = ConfigConstant.UPDATE_DYNAUTH_POLICY_STATUS;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		try{
			checkActionPermission(request, ACTION_ALIAS);
			Logger.logTrace(MODULE,"Execute Method Of :"+getClass().getName());
			SearchDynAuthServicePolicyForm searchDynAuthServicePolicyForm =(SearchDynAuthServicePolicyForm)form;
			ServicePolicyBLManager policyBLManager = new ServicePolicyBLManager();
			DynAuthPolicyInstData dynAuthPolicyInstData = new DynAuthPolicyInstData();

			String[] dynAuthPolicyIds = request.getParameterValues("select");
			dynAuthPolicyInstData.setStatus("All");

			int requiredPageNo;
			if(request.getParameter("pageNo") != null){
				requiredPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNo")));
			}else{
				requiredPageNo = new Long(searchDynAuthServicePolicyForm.getPageNumber()).intValue();
			}
			if(requiredPageNo == 0)
				requiredPageNo = 1;
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			
			if(searchDynAuthServicePolicyForm.getAction() != null){

				if(searchDynAuthServicePolicyForm.getAction().equals("delete")){
					String actionAlias = ConfigConstant.DELETE_DYNAUTH_POLICY;
					
					checkAccess(request,actionAlias);
					policyBLManager.deleteDynAuthPolicyById(Arrays.asList(dynAuthPolicyIds), staffData);
					int strSelectedIdsLen = dynAuthPolicyIds.length;
					long currentPageNumber = getCurrentPageNumberAfterDel(strSelectedIdsLen,searchDynAuthServicePolicyForm.getPageNumber(),searchDynAuthServicePolicyForm.getTotalPages(),searchDynAuthServicePolicyForm.getTotalRecords());

					searchDynAuthServicePolicyForm.setAction("list");

					request.setAttribute("responseUrl","/searchDynAuthServicePolicy.do?name="+searchDynAuthServicePolicyForm.getName()+"&pageNumber="+currentPageNumber+"&totalPages="+searchDynAuthServicePolicyForm.getTotalPages()+"&totalRecords="+searchDynAuthServicePolicyForm.getTotalRecords()+"&resultStatus="+searchDynAuthServicePolicyForm.getStatus());
					ActionMessage message = new ActionMessage("dynauthpolicy.delete.success");
					ActionMessages messages1 = new ActionMessages();
					messages1.add("information",message);
					saveMessages(request,messages1);
					return mapping.findForward(SUCCESS_FORWARD);

				}else if(searchDynAuthServicePolicyForm.getAction().equalsIgnoreCase("show")){
					
					String actionAlias =UPDATE_STATUS;
					policyBLManager.updateDynAuthPolicyStatus(Arrays.asList(dynAuthPolicyIds),BaseConstant.SHOW_STATUS_ID);			
					doAuditing(staffData, actionAlias);
				}else if(searchDynAuthServicePolicyForm.getAction().equalsIgnoreCase("hide")){
					checkActionPermission(request, UPDATE_STATUS);
					String actionAlias =UPDATE_STATUS;
					policyBLManager.updateDynAuthPolicyStatus(Arrays.asList(dynAuthPolicyIds),BaseConstant.HIDE_STATUS_ID);			
					doAuditing(staffData, actionAlias);
				}
			}
			if(request.getParameter("resultStatus")!= null){
				searchDynAuthServicePolicyForm.setStatus(request.getParameter("resultStatus"));
			}	

			if(searchDynAuthServicePolicyForm.getStatus() != null){

				if(searchDynAuthServicePolicyForm.getStatus().equals("Active")){				
					dynAuthPolicyInstData.setStatus("CST01");

				}else if(searchDynAuthServicePolicyForm.getStatus().equals("Inactive")){
					dynAuthPolicyInstData.setStatus("CST02");
				}
			}


			Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));


			String strPolicyName =request.getParameter("name");
			if(strPolicyName != null){
				dynAuthPolicyInstData.setName(strPolicyName);
			}else if(searchDynAuthServicePolicyForm.getName() != null){
				dynAuthPolicyInstData.setName(searchDynAuthServicePolicyForm.getName());
			}else{
				dynAuthPolicyInstData.setName("");
			}

	


			PageList pageList = policyBLManager.searchDynAuthServicePolicy(dynAuthPolicyInstData,requiredPageNo, pageSize, staffData);

			searchDynAuthServicePolicyForm.setPageNumber(pageList.getCurrentPage());
			searchDynAuthServicePolicyForm.setTotalPages(pageList.getTotalPages());
			searchDynAuthServicePolicyForm.setTotalRecords(pageList.getTotalItems());
			searchDynAuthServicePolicyForm.setPolicyList(pageList.getListData());

			
			searchDynAuthServicePolicyForm.setAction("list");
			request.setAttribute("policyForm",searchDynAuthServicePolicyForm);
			return mapping.findForward(LIST_FORWARD);
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
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
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
