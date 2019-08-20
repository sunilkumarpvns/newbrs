/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   SearchASMAction.java                 		
 * ModualName ASM    			      		
 * Created on 6 December, 2007
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.web.reports.userstat; 
  
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.reports.userstat.UserStatisticsBLManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.reports.userstat.data.IUserStatisticsData;
import com.elitecore.elitesm.datamanager.reports.userstat.data.UserStatisticsData;

import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.reports.userstat.forms.SearchUserStatisticsForm;
                                                                               
public class SearchUserStatisticsAction extends BaseWebAction { 
	
	
	private static final String LIST_FORWARD = "searchUserStatistics";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.SEARCH_USER_STATISTICS_ACTION;
                                                                                   
	private static final String MODULE ="USER_STATISTICS";
	 
 
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{ 
		Logger.logTrace(MODULE,"Enter execute method of"+getClass().getName());
		
//		if(checkAccess(request, ACTION_ALIAS)){	
		if(true){
			try{
				String[] s = request.getParameterValues("selected");
				
				SearchUserStatisticsForm searchUserStatisticsForm = (SearchUserStatisticsForm)form;
				UserStatisticsBLManager userStatisticsBLManager  = new UserStatisticsBLManager();
				
				String strAction = searchUserStatisticsForm.getAction();
				int requiredPageNo = 0;
				requiredPageNo = Integer.parseInt(String.valueOf(searchUserStatisticsForm.getPageNumber()));
				if(requiredPageNo == 0)
					requiredPageNo = 1;

				IUserStatisticsData searchData = new UserStatisticsData();
				String strUserIdentity = searchUserStatisticsForm.getUserIdentity();
				searchData.setUserIdentity(strUserIdentity);
				
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				String actionAlias = ACTION_ALIAS;
				Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
				PageList pageList = userStatisticsBLManager.search(searchData,requiredPageNo,pageSize);
				doAuditing(staffData, actionAlias);
				if(pageList!=null){
					searchUserStatisticsForm.setPageNumber(pageList.getCurrentPage());
					searchUserStatisticsForm.setTotalPages(pageList.getTotalPages());
					searchUserStatisticsForm.setTotalRecords(pageList.getTotalItems());								
					// this is List without Group BY
					searchUserStatisticsForm.setUserStatisticsList(pageList.getListData());
					
		     	}
				searchUserStatisticsForm.setAction(strAction);	
				request.setAttribute("searchUserStatisticsForm",searchUserStatisticsForm );
				return mapping.findForward(LIST_FORWARD);
			}catch(Exception managerExp){
				managerExp.printStackTrace();
				Logger.logError(MODULE,"Error during Data Manager operation, reason : "+ managerExp.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
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