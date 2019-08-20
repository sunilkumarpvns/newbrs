package com.elitecore.elitesm.web.core.system.staff;

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
import com.elitecore.elitesm.web.core.system.staff.forms.ViewStaffAdvanceDetailForm;

public class ViewStaffAdvanceDetailAction extends BaseWebAction{
	private static final String VIEW_ADVANCE_FORWARD = "viewStaffAdvanceDetail";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.VIEW_STAFF_ACTION;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		if(checkAccess(request, ACTION_ALIAS)){
			Logger.logTrace(MODULE,"Enter execute method of :"+getClass().getName());
	//		System.out.println("Here accessing the VIEWSTAFFADVANDEDETAIL action class:");
			
			StaffBLManager staffBLManager = new StaffBLManager();
			ActionErrors errors = new ActionErrors();
			
			try{
				ViewStaffAdvanceDetailForm viewStaffAdvanceDetailForm = (ViewStaffAdvanceDetailForm)form;
				String strStaffId = request.getParameter("staffid");
				String staffId;
				if(strStaffId == null){
					staffId = viewStaffAdvanceDetailForm.getStaffId();
				}else{
					staffId = request.getParameter("staffid");
				}
				
				
				if(viewStaffAdvanceDetailForm.getAction() == null){
					if(Strings.isNullOrBlank(staffId) == false){
						IStaffData staffData = new StaffData();
						staffData.setStaffId(staffId);
						
						IStaffData staffData1 = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
						String actionAlias = ACTION_ALIAS;
						staffData=staffBLManager.getStaffDetails(staffData);
						doAuditing(staffData1, actionAlias);
						List<GroupData> groupDataList  =staffBLManager.getStaffGroupRelList(staffData.getStaffId());
						request.setAttribute("groupDataList",groupDataList);
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
