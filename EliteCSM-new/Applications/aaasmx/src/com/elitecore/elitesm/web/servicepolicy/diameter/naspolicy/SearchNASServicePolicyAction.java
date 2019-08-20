package com.elitecore.elitesm.web.servicepolicy.diameter.naspolicy;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.servicepolicy.diameter.DiameterNASPolicyBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyInstData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servicepolicy.diameter.naspolicy.forms.SearchNASServicePolicyForm;

public class SearchNASServicePolicyAction  extends BaseWebAction{


	private static final String SUCCESS_FORWARD = "success";
	private static final String ACTION_ALIAS = ConfigConstant.SEARCH_NAS_SERVICE_POLICY;
	private static final String MODULE = "SEARCH NAS POLICY ACTION";
	private static final String LIST_FORWARD = "listnaspolicies";

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		String actionMessage="diameter.naspolicy.search.failure";
		try{
			checkActionPermission(request, ACTION_ALIAS);
			Logger.logTrace(MODULE,"Execute Method Of :"+getClass().getName());
			SearchNASServicePolicyForm searchNASServicePolicyForm =(SearchNASServicePolicyForm)form;
			DiameterNASPolicyBLManager diameterNASPolicyBLManager = new DiameterNASPolicyBLManager();
			NASPolicyInstData nasPolicyInstData = new NASPolicyInstData();
            
			String[] nasPolicyIds = request.getParameterValues("select");
			nasPolicyInstData.setStatus("All");

			int requiredPageNo;
			if(request.getParameter("pageNo") != null){
				requiredPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNo")));
			}else{
				requiredPageNo = new Long(searchNASServicePolicyForm.getPageNumber()).intValue();
			}
			if(requiredPageNo == 0)
				requiredPageNo = 1;
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			
			if(searchNASServicePolicyForm.getAction() != null){

				if(searchNASServicePolicyForm.getAction().equals("delete")){
					actionMessage="diameter.naspolicy.delete.failure";
					String actionAlias =ConfigConstant.DELETE_NAS_SERVICE_POLICY;
					checkActionPermission(request, actionAlias);
					diameterNASPolicyBLManager.deleteNASPolicy(Arrays.asList(nasPolicyIds), staffData);
					doAuditing(staffData, actionAlias);
					int strSelectedIdsLen = nasPolicyIds.length;
					long currentPageNumber = getCurrentPageNumberAfterDel(strSelectedIdsLen,searchNASServicePolicyForm.getPageNumber(),searchNASServicePolicyForm.getTotalPages(),searchNASServicePolicyForm.getTotalRecords());

					searchNASServicePolicyForm.setAction("list");

					request.setAttribute("responseUrl","/searchNASServicePolicy.do?name="+searchNASServicePolicyForm.getName()+"&pageNumber="+currentPageNumber+"&totalPages="+searchNASServicePolicyForm.getTotalPages()+"&totalRecords="+searchNASServicePolicyForm.getTotalRecords()+"&resultStatus="+searchNASServicePolicyForm.getStatus());
					ActionMessage message = new ActionMessage("diameter.naspolicy.delete.success");
					ActionMessages messages1 = new ActionMessages();
					messages1.add("information",message);
					saveMessages(request,messages1);
					return mapping.findForward(SUCCESS_FORWARD);

				}else if(searchNASServicePolicyForm.getAction().equalsIgnoreCase("show")){
					actionMessage="diameter.naspolicy.statuschange.failure";
					String actionAlias =ConfigConstant.UPDATE_NAS_SERVICE_POLICY_STATUS;
					checkActionPermission(request, actionAlias);
					diameterNASPolicyBLManager.updateNASPolicyStatus(Arrays.asList(nasPolicyIds),BaseConstant.SHOW_STATUS_ID);			
					doAuditing(staffData, actionAlias);
				}else if(searchNASServicePolicyForm.getAction().equalsIgnoreCase("hide")){
					actionMessage="diameter.naspolicy.statuschange.failure";
					String actionAlias =ConfigConstant.UPDATE_NAS_SERVICE_POLICY_STATUS;
					checkActionPermission(request, actionAlias);
					diameterNASPolicyBLManager.updateNASPolicyStatus(Arrays.asList(nasPolicyIds),BaseConstant.HIDE_STATUS_ID);			
					doAuditing(staffData, actionAlias);
				}
			}
			if(request.getParameter("resultStatus")!= null){
				searchNASServicePolicyForm.setStatus(request.getParameter("resultStatus"));	

			}
			if(searchNASServicePolicyForm.getStatus() != null){

				if(searchNASServicePolicyForm.getStatus().equals("Active")){				
					nasPolicyInstData.setStatus("CST01");

				}else if(searchNASServicePolicyForm.getStatus().equals("Inactive")){
					nasPolicyInstData.setStatus("CST02");
				}
			}


			Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
			Logger.logDebug(MODULE, "PAGENO IS:"+requiredPageNo+"");

			String strPolicyName =request.getParameter("name");
			if(strPolicyName != null){
				nasPolicyInstData.setName(strPolicyName);
			}else if(searchNASServicePolicyForm.getName() != null){
				nasPolicyInstData.setName(searchNASServicePolicyForm.getName());
			}else{
				nasPolicyInstData.setName("");
			}

			PageList pageList = diameterNASPolicyBLManager.searchNASServicePolicy(nasPolicyInstData,requiredPageNo, pageSize);

			searchNASServicePolicyForm.setPageNumber(pageList.getCurrentPage());
			searchNASServicePolicyForm.setTotalPages(pageList.getTotalPages());
			searchNASServicePolicyForm.setTotalRecords(pageList.getTotalItems());
			searchNASServicePolicyForm.setPolicyList(pageList.getListData());


			searchNASServicePolicyForm.setAction("list");
			request.setAttribute("policyForm",searchNASServicePolicyForm);
			doAuditing(staffData, ACTION_ALIAS);
			
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
			ActionMessage message = new ActionMessage(actionMessage);
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}catch(Exception e){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage(actionMessage);
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE);
	}
}
