package com.elitecore.netvertexsm.web.core.system.staff;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.core.system.staff.StaffBLManager;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.constants.StaffConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.netvertexsm.web.core.system.staff.forms.SearchStaffForm;

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
					
					int requiredPageNo = Integer.parseInt(String.valueOf(searchStaffForm.getPageNumber()));
					
					if(requiredPageNo == 0)
						requiredPageNo=1;
					
					IStaffData staffSearchData = new StaffData();
					String strName = searchStaffForm.getName();
					String strUserName = searchStaffForm.getUserName();
					
					if(strName !=null)
						staffSearchData.setName("%"+strName+"%");
					else
						staffSearchData.setName("");
					
					if(strUserName != null)
						staffSearchData.setUserName("%"+strUserName+"%");
					else
						staffSearchData.setUserName("");
					
					if(searchStaffForm.getStatus() != null){
						if(searchStaffForm.getStatus().equalsIgnoreCase("Active"))
							staffSearchData.setCommonStatusId("CST01");
						else if(searchStaffForm.getStatus().equalsIgnoreCase("InActive"))
							staffSearchData.setCommonStatusId("CST02");
					}
					
					
					
					IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
					String actionAlias = ACTION_ALIAS;
					PageList pageList = blManager.search(staffSearchData,requiredPageNo,10,staffData,actionAlias);
					
					searchStaffForm.setName(strName);
					searchStaffForm.setUserName(strUserName);
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
				
	            ActionMessages errorHeadingMessage = new ActionMessages();
	            ActionMessage message = new ActionMessage("staff.error.heading","searching");
	            errorHeadingMessage.add("errorHeading",message);
	            saveMessages(request,errorHeadingMessage);	            
				
		        return mapping.findForward(FAILURE_FORWARD);
		}else{
	        Logger.logWarn(MODULE, "Error during Data Manager operation ");
	        ActionMessage message = new ActionMessage("general.user.restricted");
	        ActionMessages messages = new ActionMessages();
	        messages.add("information", message);
	        saveErrors(request, messages);
	        
            ActionMessages errorHeadingMessage = new ActionMessages();
            message = new ActionMessage("staff.error.heading","searching");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage);	            	        
		return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
}
