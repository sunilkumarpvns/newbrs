package com.elitecore.elitesm.web.servicepolicy.diameter.tgppaaapolicy;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.servicepolicy.diameter.TGPPAAAPolicyBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.tgppaaapolicy.data.TGPPAAAPolicyData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servicepolicy.diameter.tgppaaapolicy.form.TGPPAAAPolicyForm;

public class SearchTGPPAAAPolicyAction  extends BaseWebAction{

	private static final String SUCCESS_FORWARD = "success";
	private static final String MODULE = "SearchTGPPAAAPolicyAction";
	private static final String LIST_FORWARD = "listTGPPAAAPolicy";
	private static final String ACTION_ALIAS = ConfigConstant.SEARCH_TGPP_AAA_SERVICE_POLICY;
	private static final String UPDATE_STATUS = ConfigConstant.UPDATE_TGPP_AAA_SERVICE_POLICY;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		String actionMessage="tgppaaaservicepolicy.search.failure";
		try{
			checkActionPermission(request, ACTION_ALIAS);
			Logger.logTrace(MODULE,"Execute Method Of :"+getClass().getName());
			
			TGPPAAAPolicyForm tgppAAAPolicyForm = (TGPPAAAPolicyForm)form;
			
			TGPPAAAPolicyBLManager policyBLManager = new TGPPAAAPolicyBLManager();
			TGPPAAAPolicyData tgppAAAPolicyData = new TGPPAAAPolicyData();

			String[] tgppAAAPolicyIds = request.getParameterValues("select");
			tgppAAAPolicyData.setStatus("All");

			int requiredPageNo;
			if(request.getParameter("pageNo") != null){
				requiredPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNo")));
			}else{
				requiredPageNo = new Long(tgppAAAPolicyForm.getPageNumber()).intValue();
			}
			if(requiredPageNo == 0)
				requiredPageNo = 1;
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			
			if(tgppAAAPolicyForm.getAction() != null){

				if(tgppAAAPolicyForm.getAction().equals("delete")){
					actionMessage="tgppaaaservicepolicy.delete.failure";
					String actionAlias = ConfigConstant.DELETE_TGPP_AAA_SERVICE_POLICY;
					
					checkAccess(request,actionAlias);
					policyBLManager.deleteTGPPAAAPolicyById(Arrays.asList(tgppAAAPolicyIds), staffData);
					doAuditing(staffData, actionAlias);
					int strSelectedIdsLen = tgppAAAPolicyIds.length;
					long currentPageNumber = getCurrentPageNumberAfterDel(strSelectedIdsLen,tgppAAAPolicyForm.getPageNumber(),tgppAAAPolicyForm.getTotalPages(),tgppAAAPolicyForm.getTotalRecords());

					tgppAAAPolicyForm.setAction("list");

					request.setAttribute("responseUrl","/searchTGPPAAAPolicy.do?name="+tgppAAAPolicyForm.getName()+"&pageNumber="+currentPageNumber+"&totalPages="+tgppAAAPolicyForm.getTotalPages()+"&totalRecords="+tgppAAAPolicyForm.getTotalRecords()+"&resultStatus="+tgppAAAPolicyForm.getStatus());
					ActionMessage message = new ActionMessage("tgppaaaservicepolicy.delete.success");
					ActionMessages messages1 = new ActionMessages();
					messages1.add("information",message);
					saveMessages(request,messages1);
					return mapping.findForward(SUCCESS_FORWARD);

				}else if(tgppAAAPolicyForm.getAction().equalsIgnoreCase("show")){
					actionMessage="tgppaaaservicepolicy.statuschange.failure";
					String actionAlias =UPDATE_STATUS;
					policyBLManager.updateTgppAAAServicePolicyStatus(Arrays.asList(tgppAAAPolicyIds),BaseConstant.SHOW_STATUS_ID);			
					doAuditing(staffData, actionAlias);
				}else if(tgppAAAPolicyForm.getAction().equalsIgnoreCase("hide")){
					actionMessage="tgppaaaservicepolicy.statuschange.failure";
					checkActionPermission(request, UPDATE_STATUS);
					String actionAlias =UPDATE_STATUS;
					policyBLManager.updateTgppAAAServicePolicyStatus(Arrays.asList(tgppAAAPolicyIds),BaseConstant.HIDE_STATUS_ID);	
					doAuditing(staffData, actionAlias);
				}
			}
			if(request.getParameter("resultStatus")!= null){
				tgppAAAPolicyForm.setStatus(request.getParameter("resultStatus"));
			}	

			if(tgppAAAPolicyForm.getStatus() != null){

				if(tgppAAAPolicyForm.getStatus().equals("Active")){				
					tgppAAAPolicyData.setStatus("CST01");

				}else if(tgppAAAPolicyForm.getStatus().equals("Inactive")){
					tgppAAAPolicyData.setStatus("CST02");
				}
			}


			Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));

			String strPolicyName =request.getParameter("name");
			if(strPolicyName != null){
				tgppAAAPolicyData.setName(strPolicyName);
			}else if(tgppAAAPolicyForm.getName() != null){
				tgppAAAPolicyData.setName(tgppAAAPolicyForm.getName());
			}else{
				tgppAAAPolicyData.setName("");
			}

			PageList pageList = policyBLManager.searchTGPPAAAPolicy(tgppAAAPolicyData,requiredPageNo, pageSize);

			tgppAAAPolicyForm.setPageNumber(pageList.getCurrentPage());
			tgppAAAPolicyForm.setTotalPages(pageList.getTotalPages());
			tgppAAAPolicyForm.setTotalRecords(pageList.getTotalItems());
			tgppAAAPolicyForm.setPolicyList(pageList.getListData());
			
			tgppAAAPolicyForm.setAction("list");
			request.setAttribute("policyForm",tgppAAAPolicyForm);

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
