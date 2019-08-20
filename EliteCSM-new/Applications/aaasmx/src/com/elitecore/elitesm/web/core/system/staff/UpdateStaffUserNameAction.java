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
import com.elitecore.elitesm.datamanager.core.exceptions.server.DuplicateParameterFoundExcpetion;
import com.elitecore.elitesm.datamanager.core.system.accessgroup.data.GroupData;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.core.system.staff.forms.UpdateStaffUserNameForm;

public class UpdateStaffUserNameAction extends BaseWebAction{
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String CHANGE_USERNAME = "changeUserName";
	private static final String VIEW_FORWARD = "viewStaffDetail";
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_STAFF_USERNAME;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		if(checkAccess(request, ACTION_ALIAS)){
			Logger.logTrace(MODULE,"Enter execute method of :"+getClass().getName());
			StaffBLManager staffBLManager = new StaffBLManager();

			ActionErrors errors = new ActionErrors();
			IStaffData istaffData = null;
			try{
				UpdateStaffUserNameForm updateUserNameForm = (UpdateStaffUserNameForm)form;
				String strStaffId = request.getParameter("staffid");
				String staffId;
				if(strStaffId == null){
					staffId = updateUserNameForm.getStaffId();
				}else{
					staffId = request.getParameter("staffid");
				}
				if(Strings.isNullOrBlank(staffId) == false) {
					IStaffData staffData = new StaffData();
					staffData.setStaffId(staffId);
					staffData = staffBLManager.getStaff(staffData);
					request.setAttribute("staffData",staffData);
					List<GroupData> groupDataList  =staffBLManager.getStaffGroupRelList(staffData.getStaffId());
					request.setAttribute("groupDataList",groupDataList);
					if(updateUserNameForm.getAction() == null){
						if(Strings.isNullOrBlank(staffId) == false){
							updateUserNameForm = convertBeanToForm(staffData);
							request.setAttribute("changeUserNameForm",updateUserNameForm);
						}
						return mapping.findForward(CHANGE_USERNAME);
					}else if(updateUserNameForm.getAction().equalsIgnoreCase("update")){
						
						istaffData = convertFormToBean(updateUserNameForm,staffData);
						
						IStaffData staffData1 = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
						String actionAlias = ACTION_ALIAS;
						
						istaffData.setAuditName(istaffData.getName());
						istaffData.setAuditId(istaffData.getAuditUId());
						
						staffBLManager.changeUserName(istaffData,actionAlias);
						return mapping.findForward(VIEW_FORWARD);
					}
				}
			}catch (DuplicateParameterFoundExcpetion dpfExp) {
		        Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
                Logger.logTrace(MODULE,dpfExp);
    			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(dpfExp);
    			request.setAttribute("errorDetails", errorElements);
                ActionMessage message = new ActionMessage("staff.create.duplicate.failure",istaffData.getUsername());
                ActionMessages messages = new ActionMessages();
                messages.add("information",message);
                saveErrors(request,messages);
           }catch(Exception e){
				e.printStackTrace();
    			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
    			request.setAttribute("errorDetails", errorElements);
    			errors.add("fatal", new ActionError("staff.updateusername.failure")); 
    	        saveErrors(request,errors);
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
	
	private UpdateStaffUserNameForm convertBeanToForm(IStaffData staffData){
		UpdateStaffUserNameForm updateUserNameForm = null;
			if(staffData != null){
				updateUserNameForm = new UpdateStaffUserNameForm();
				updateUserNameForm.setStaffId(staffData.getStaffId());
				updateUserNameForm.setUsername(staffData.getUsername());
				updateUserNameForm.setReason(staffData.getReason());
				updateUserNameForm.setAuditUId(staffData.getAuditUId());
				updateUserNameForm.setName(staffData.getName());
			}
			return updateUserNameForm;
	}
	
	private IStaffData convertFormToBean(UpdateStaffUserNameForm updateUserNameForm,IStaffData staffData){
		if(updateUserNameForm!= null){
			staffData.setStaffId(updateUserNameForm.getStaffId());
			staffData.setUsername(updateUserNameForm.getUsername());
			staffData.setAuditUId(updateUserNameForm.getAuditUId());
			staffData.setAuditName(updateUserNameForm.getUsername());
		}
		return staffData;
	}

}
