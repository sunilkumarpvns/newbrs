package com.elitecore.elitesm.web.core.system.staff;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.core.system.staff.StaffBLManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.StaffConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.core.system.staff.forms.SearchStaffForm;

public class SearchStaffAction extends BaseWebAction{
	private static final String SUCCESS_FORWARD = "success";
	private static final String LIST_FORWARD = "searchStaffList";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.SEARCH_STAFF_ACTION;

	public ActionForward execute(ActionMapping mapping,ActionForm actionForm,HttpServletRequest request,HttpServletResponse response) throws Exception{

		Logger.logInfo(MODULE,"Enter Execute method of "+getClass().getName());
		if(checkAccess(request, ACTION_ALIAS)){
			try{
				SearchStaffForm searchStaffForm = (SearchStaffForm)actionForm;
				StaffBLManager blManager = new StaffBLManager();
				IStaffData staffSearchData = new StaffData();
				staffSearchData.setCommonStatusId("All");

				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				
				int requiredPageNo;
				if(request.getParameter("pageNo") != null){
					requiredPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNo")));
				}else{
					requiredPageNo = new Long(searchStaffForm.getPageNumber()).intValue();
				}
				if(requiredPageNo == 0)
					requiredPageNo=1;

				Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));

				if(request.getParameter("resultStatus")!= null){
					searchStaffForm.setStatus(request.getParameter("resultStatus"));
				}	

				if(searchStaffForm.getStatus() != null){

					if(searchStaffForm.getStatus().equals("Active")){				
						staffSearchData.setCommonStatusId("CST01");

					}else if(searchStaffForm.getStatus().equals("Inactive")){
						staffSearchData.setCommonStatusId("CST02");
					}
				}

				searchStaffForm.setAction("list");

				String strPolicyName =request.getParameter("name");
				if(strPolicyName != null){
					staffSearchData.setName(strPolicyName);
				}else if(searchStaffForm.getName() != null){
					staffSearchData.setName(searchStaffForm.getName());
				}else{
					staffSearchData.setName("");
				}

				String strUserName =request.getParameter("username");
				if(strUserName != null){
					staffSearchData.setUsername(strUserName);
				}else if(searchStaffForm.getUsername() != null){
					staffSearchData.setUsername(searchStaffForm.getUsername());
				}else{
					staffSearchData.setUsername("");
				}

				String actionAlias = ACTION_ALIAS;
				PageList pageList = blManager.search(staffSearchData,requiredPageNo,pageSize);
				doAuditing(staffData, actionAlias);
				searchStaffForm.setPageNumber(pageList.getCurrentPage());
				searchStaffForm.setTotalPages(pageList.getTotalPages());
				searchStaffForm.setTotalRecords(pageList.getTotalItems());
				searchStaffForm.setListSearchStaff(pageList.getListData());
				searchStaffForm.setAction(StaffConstant.LISTACTION);
				return mapping.findForward(LIST_FORWARD);
			}catch(Exception managerExp){

				Logger.logError(MODULE,"Error during Data Manager operation, reason : "+ managerExp.getMessage());
				Logger.logTrace(MODULE,managerExp);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("staff.search.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
			}
			return mapping.findForward(FAILURE_FORWARD);
		}else{
			Logger.logWarn(MODULE, "Error during Data Manager operation ");
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
}
