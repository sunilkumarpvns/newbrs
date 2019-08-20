package com.elitecore.netvertexsm.web.core.system.staff;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.core.system.staff.StaffBLManager;
import com.elitecore.netvertexsm.datamanager.core.system.accessgroup.data.RoleData;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.netvertexsm.web.core.system.staff.forms.ViewStaffAdvanceDetailForm;

public class ViewStaffAdvanceDetailAction extends BaseWebAction{
	private static final String VIEW_ADVANCE_FORWARD = "viewStaffAdvanceDetail";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.VIEW_STAFF;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		if(checkAccess(request, ACTION_ALIAS)){
			Logger.logTrace(MODULE,"Enter execute method of :"+getClass().getName());
	//		System.out.println("Here accessing the VIEWSTAFFADVANDEDETAIL action class:");
			
			StaffBLManager staffBLManager = new StaffBLManager();
			ActionErrors errors = new ActionErrors();
			
			try{
				ViewStaffAdvanceDetailForm viewStaffAdvanceDetailForm = (ViewStaffAdvanceDetailForm)form;
				String strStaffId = request.getParameter("staffid");
				long staffId;
				if(strStaffId == null){
					staffId = viewStaffAdvanceDetailForm.getStaffId();
				}else{
					staffId = Long.parseLong(request.getParameter("staffid"));
				}
				
				
				if(viewStaffAdvanceDetailForm.getAction() == null){
					if( staffId > 0){
						IStaffData staffData = new StaffData();
						staffData.setStaffId(staffId);
						
						IStaffData staffData1 = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
						String actionAlias = ACTION_ALIAS;
						staffData=staffBLManager.getStaff(staffData,staffData1,actionAlias);
						List<RoleData> roleDataList  =staffBLManager.getStaffRoleRelList(staffData.getStaffId());
						request.setAttribute("roleDataList",roleDataList);
						request.setAttribute("staffData",staffData);
						viewStaffAdvanceDetailForm = convertBeanToForm(staffData);
						request.setAttribute("viewStaffAdvanceDetailForm",viewStaffAdvanceDetailForm);
					}
					return mapping.findForward(VIEW_ADVANCE_FORWARD);
				}else{
					 staffId = viewStaffAdvanceDetailForm.getStaffId();
				}
				
			  }catch(Exception e){
				e.printStackTrace();
     			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
    			request.setAttribute("errorDetails", errorElements);
				return mapping.findForward(FAILURE_FORWARD);
			  }
			  errors.add("fatal", new ActionError("staff.viewstaffadvancedetail.failure")); 
		      saveErrors(request,errors);
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
	private ViewStaffAdvanceDetailForm convertBeanToForm(IStaffData staffData){
		ViewStaffAdvanceDetailForm viewStaffAdvanceDetailForm = null;
		if(staffData!=null){
			viewStaffAdvanceDetailForm = new ViewStaffAdvanceDetailForm();
			viewStaffAdvanceDetailForm.setStaffId(staffData.getStaffId());
		}
		return viewStaffAdvanceDetailForm;
	}
}
