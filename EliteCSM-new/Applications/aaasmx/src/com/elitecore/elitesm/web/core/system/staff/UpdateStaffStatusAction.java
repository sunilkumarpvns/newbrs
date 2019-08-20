package com.elitecore.elitesm.web.core.system.staff;

import java.util.ArrayList;
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
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.core.system.staff.forms.UpdateStaffStatusForm;

public class UpdateStaffStatusAction extends BaseWebAction{
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String UPDATE_FORWARD = "changeStaffStatus";
	private static final String VIEW_FORWARD = "viewStaffDetail";
	private static final String ACTION_ALIAS = "UPDATE_STAFF_STATUS_ACTION";
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		if(checkAccess(request, ACTION_ALIAS)){
			Logger.logTrace(MODULE,"Enter execute method of :"+getClass().getName());
			StaffBLManager staffBLManager = new StaffBLManager();
			
			ActionErrors errors = new ActionErrors();
	
			try{
				UpdateStaffStatusForm updateStaffStatusForm = (UpdateStaffStatusForm)form;
				String strStaffId = request.getParameter("staffid");
				String staffId;
				if(strStaffId == null){
					staffId = updateStaffStatusForm.getStaffId();
				}else{
					staffId = request.getParameter("staffid");
				}
				if(updateStaffStatusForm.getAction() == null){
					if(Strings.isNullOrBlank(staffId) == false){
						IStaffData staffData = new StaffData();
						staffData.setStaffId(staffId);
						staffData=staffBLManager.getStaff(staffData);
						request.setAttribute("staffData",staffData);
						List<GroupData> groupDataList  =staffBLManager.getStaffGroupRelList(staffData.getStaffId());
						request.setAttribute("groupDataList",groupDataList);
						updateStaffStatusForm = convertBeanToForm(staffData);
						request.setAttribute("changeStaffStatusForm",updateStaffStatusForm);
					}
					return mapping.findForward(UPDATE_FORWARD);
				}else if(updateStaffStatusForm.getAction().equalsIgnoreCase("update")){
					staffId = updateStaffStatusForm.getStaffId();
					List lstStaffId = new ArrayList();
					lstStaffId.add(staffId);
					String status = updateStaffStatusForm.getStatus();
	
						if(status.equalsIgnoreCase(BaseConstant.SHOW_STATUS_ID))
							status = BaseConstant.HIDE_STATUS_ID;
						else if(status.equalsIgnoreCase(BaseConstant.HIDE_STATUS_ID))
							status = BaseConstant.SHOW_STATUS_ID;
	                
					IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
					String actionAlias = ACTION_ALIAS;
					staffBLManager.updateStatus(lstStaffId,status,staffData,actionAlias);
					
					return mapping.findForward(VIEW_FORWARD);
				}
			}catch(Exception e){
				e.printStackTrace();
    			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
    			request.setAttribute("errorDetails", errorElements);
			}
			errors.add("fatal", new ActionError("staff.updatestatus.failure")); 
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
	private UpdateStaffStatusForm convertBeanToForm(IStaffData staffData){
		UpdateStaffStatusForm changeStatusForm = null;
		if(staffData!=null){
			changeStatusForm = new UpdateStaffStatusForm();
			changeStatusForm.setStaffId(staffData.getStaffId());
			changeStatusForm.setStatus(staffData.getCommonStatusId());
		}
		return changeStatusForm;
	}

}
