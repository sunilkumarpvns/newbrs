package com.elitecore.netvertexsm.web.core.system.staff;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.core.system.staff.StaffBLManager;
import com.elitecore.netvertexsm.datamanager.core.system.accessgroup.data.RoleData;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.StaffGroupRoleRelData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.netvertexsm.web.core.system.staff.forms.ViewStaffForm;


public class ViewStaffAction extends BaseWebAction {
	private static final String VIEW_FORWARD = "viewStaffDetail";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.VIEW_STAFF;
        private static final String MODULE = "VIEW STAFF";
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
            Logger.logInfo(MODULE,"Enter execute method of"+getClass().getName());
		if(checkAccess(request, ACTION_ALIAS)){
			try{
				ViewStaffForm viewStaffForm = (ViewStaffForm)form;
				StaffBLManager blManager = new StaffBLManager();
				IStaffData staffData  = new StaffData();
				 

				String strStaffId = request.getParameter("staffid");
				long staffId;
				if(strStaffId == null){
					staffId = viewStaffForm.getStaffId();
					if(staffId == 0){
						staffId = (Long) request.getAttribute("staffId"); 
					}
				}else{
					staffId = Long.parseLong(request.getParameter("staffid"));
				}
				
				if( staffId > 0){
					staffData.setStaffId(staffId);
					IStaffData staffData1 = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
					String actionAlias = ACTION_ALIAS;
					staffData= blManager.getStaff(staffData,staffData1,actionAlias);
					List<StaffGroupRoleRelData> staffGroupRoleRelList = blManager.getStaffGroupRoleRelList(staffData.getStaffId());
					staffData.setStaffGroupRoleRelationList(staffGroupRoleRelList);
					request.setAttribute("staffGroupRoleRelationList",staffGroupRoleRelList);
					request.setAttribute("staffData",staffData);
					request.setAttribute("staffId", staffId);
					IStaffData loggedInUserData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
					request.setAttribute("loggedInUserData", loggedInUserData);
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
