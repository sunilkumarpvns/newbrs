package com.elitecore.elitesm.web.core.system.staff;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.core.system.staff.StaffBLManager;
import com.elitecore.elitesm.datamanager.core.system.accessgroup.data.GroupData;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.core.system.staff.forms.ViewStaffForm;


public class ViewStaffAction extends BaseWebAction {
	private static final String VIEW_FORWARD = "viewStaffDetail";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.VIEW_STAFF_ACTION;
        private static final String MODULE = "VIEW STAFF";
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
            Logger.logInfo(MODULE,"Enter execute method of"+getClass().getName());
		if(checkAccess(request, ACTION_ALIAS)){
			try{
				ViewStaffForm viewStaffForm = (ViewStaffForm)form;
				StaffBLManager blManager = new StaffBLManager();
				IStaffData staffData  = new StaffData();
				

				String strStaffId = request.getParameter("staffid");
				String staffId;
				if(strStaffId == null){
					staffId = viewStaffForm.getStaffId();
				}else{
					staffId = request.getParameter("staffid");
				}
				
				if(Strings.isNullOrBlank(staffId) == false){
					staffData.setStaffId(staffId);
					
					
					IStaffData staffData1 = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
					String actionAlias = ACTION_ALIAS;
					staffData= blManager.getStaffDetails(staffData);
					doAuditing(staffData1, actionAlias);
					List<GroupData> groupDataList  =blManager.getStaffGroupRelList(staffData.getStaffId());
					request.setAttribute("groupDataList",groupDataList);
					request.setAttribute("staffData",staffData);
					
				}
				return mapping.findForward(VIEW_FORWARD);
			}catch(Exception e){
                Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
                Logger.logTrace(MODULE,e);
    			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
    			request.setAttribute("errorDetails", errorElements);
                ActionMessages messages = new ActionMessages();
                ActionMessage message1 = new ActionMessage("staff.viewstaff.failure");
                messages.add("information",message1);
                saveErrors(request,messages);
			} 
			return mapping.findForward(FAILURE_FORWARD);
		}else{
            Logger.logWarn(MODULE, "No Access on this Operation ");
	        ActionMessage message = new ActionMessage("general.user.restricted");
	        ActionMessages messages = new ActionMessages();
	        messages.add("information", message);
	        saveErrors(request, messages);
		return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
}
