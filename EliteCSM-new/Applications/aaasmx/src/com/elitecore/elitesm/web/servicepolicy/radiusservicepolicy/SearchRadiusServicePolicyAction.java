package com.elitecore.elitesm.web.servicepolicy.radiusservicepolicy;

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
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.RadServicePolicyData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servicepolicy.radiusservicepolicy.form.SearchRadiusServicePolicyForm;

public class SearchRadiusServicePolicyAction  extends BaseWebAction{

	private static final String SUCCESS_FORWARD = "success";
	private static final String MODULE = "RADIUS SERVICE POLICY ACTION";
	private static final String LIST_FORWARD = "radiusServicePolicyList";
	private static final String ACTION_ALIAS = ConfigConstant.SEARCH_RADIUS_SERVICE_POLICY;
	private static final String UPDATE_STATUS = ConfigConstant.UPDATE_RADIUS_SERVICE_POLICY_STATUS;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		String actionMessage="radiusservicepolicy.search.failure";
		try{
			checkActionPermission(request, ACTION_ALIAS);
			Logger.logTrace(MODULE,"Execute Method Of :"+getClass().getName());
			SearchRadiusServicePolicyForm searchRadiusPolicyForm =(SearchRadiusServicePolicyForm)form;
			ServicePolicyBLManager policyBLManager = new ServicePolicyBLManager();
			RadServicePolicyData radServicePolicyData =new RadServicePolicyData();

			String[] radiusServicePolicyIds = request.getParameterValues("select");
			radServicePolicyData.setStatus("All");

			int requiredPageNo;
			if(request.getParameter("pageNo") != null){
				requiredPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNo")));
			}else{
				requiredPageNo = new Long(searchRadiusPolicyForm.getPageNumber()).intValue();
			}
			if(requiredPageNo == 0)
				requiredPageNo = 1;
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			
			if(searchRadiusPolicyForm.getAction() != null){

				if(searchRadiusPolicyForm.getAction().equals("delete")){
					actionMessage="radiusservicepolicy.delete.failure";
					String actionAlias = ConfigConstant.DELETE_RADIUS_SERVICE_POLICY;
					
					checkAccess(request,actionAlias);
					policyBLManager.deleteRadiusServicePolicyById(Arrays.asList(radiusServicePolicyIds), staffData);
					doAuditing(staffData, actionAlias);
					int strSelectedIdsLen = radiusServicePolicyIds.length;
					long currentPageNumber = getCurrentPageNumberAfterDel(strSelectedIdsLen,searchRadiusPolicyForm.getPageNumber(),searchRadiusPolicyForm.getTotalPages(),searchRadiusPolicyForm.getTotalRecords());

					searchRadiusPolicyForm.setAction("list");

					request.setAttribute("responseUrl","/searchRadiusServicePolicy.do?name="+searchRadiusPolicyForm.getName()+"&pageNumber="+currentPageNumber+"&totalPages="+searchRadiusPolicyForm.getTotalPages()+"&totalRecords="+searchRadiusPolicyForm.getTotalRecords()+"&resultStatus="+searchRadiusPolicyForm.getStatus());
					ActionMessage message = new ActionMessage("radiusservicepolicy.delete.success");
					ActionMessages messages1 = new ActionMessages();
					messages1.add("information",message);
					saveMessages(request,messages1);
					return mapping.findForward(SUCCESS_FORWARD);

				}else if(searchRadiusPolicyForm.getAction().equalsIgnoreCase("show")){
					actionMessage="radiusservicepolicy.statuschange.failure";
					String actionAlias =UPDATE_STATUS;
					policyBLManager.updateRadiusServicePolicyStatus(Arrays.asList(radiusServicePolicyIds),BaseConstant.SHOW_STATUS_ID);			
					doAuditing(staffData, actionAlias);
				}else if(searchRadiusPolicyForm.getAction().equalsIgnoreCase("hide")){
					actionMessage="radiusservicepolicy.statuschange.failure";
					checkActionPermission(request, UPDATE_STATUS);
					String actionAlias =UPDATE_STATUS;
					policyBLManager.updateRadiusServicePolicyStatus(Arrays.asList(radiusServicePolicyIds),BaseConstant.HIDE_STATUS_ID);	
					doAuditing(staffData, actionAlias);
				}
			}
			if(request.getParameter("resultStatus")!= null){
				searchRadiusPolicyForm.setStatus(request.getParameter("resultStatus"));
			}	

			if(searchRadiusPolicyForm.getStatus() != null){

				if(searchRadiusPolicyForm.getStatus().equals("Active")){				
					radServicePolicyData.setStatus("CST01");

				}else if(searchRadiusPolicyForm.getStatus().equals("Inactive")){
					radServicePolicyData.setStatus("CST02");
				}
			}


			Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));

			String strPolicyName =request.getParameter("name");
			if(strPolicyName != null){
				radServicePolicyData.setName(strPolicyName);
			}else if(searchRadiusPolicyForm.getName() != null){
				radServicePolicyData.setName(searchRadiusPolicyForm.getName());
			}else{
				radServicePolicyData.setName("");
			}

			PageList pageList = policyBLManager.searchRadiusServicePolicy(radServicePolicyData, staffData,requiredPageNo, pageSize);

			searchRadiusPolicyForm.setPageNumber(pageList.getCurrentPage());
			searchRadiusPolicyForm.setTotalPages(pageList.getTotalPages());
			searchRadiusPolicyForm.setTotalRecords(pageList.getTotalItems());
			searchRadiusPolicyForm.setPolicyList(pageList.getListData());
			
			searchRadiusPolicyForm.setAction("list");
			request.setAttribute("policyForm",searchRadiusPolicyForm);

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
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}

		return mapping.findForward(FAILURE);
	}

}
