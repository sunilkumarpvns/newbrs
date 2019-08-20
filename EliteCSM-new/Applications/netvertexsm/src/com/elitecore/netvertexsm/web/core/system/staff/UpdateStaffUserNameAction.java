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
import com.elitecore.netvertexsm.datamanager.core.exceptions.server.DuplicateParameterFoundExcpetion;
import com.elitecore.netvertexsm.datamanager.core.system.accessgroup.data.RoleData;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.netvertexsm.web.core.system.staff.forms.UpdateStaffUserNameForm;

public class UpdateStaffUserNameAction extends BaseWebAction{
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String CHANGE_USERNAME = "changeUserName";
	private static final String VIEW_FORWARD = "viewStaffDetail";
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_STAFF_USER_NAME_ACTION;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		if(checkAccess(request, ACTION_ALIAS)){
			Logger.logTrace(MODULE,"Enter execute method of :"+getClass().getName());
			StaffBLManager staffBLManager = new StaffBLManager();

			ActionErrors errors = new ActionErrors();
			IStaffData istaffData = null;
			try{
				UpdateStaffUserNameForm updateUserNameForm = (UpdateStaffUserNameForm)form;
				String strStaffId = request.getParameter("staffid");
				long staffId;
				if(strStaffId == null){
					staffId = updateUserNameForm.getStaffId();
				}else{
					staffId = Long.parseLong(request.getParameter("staffid"));
				}
				if(staffId > 0){
					IStaffData staffData = new StaffData();
					staffData.setStaffId(staffId);
					staffData = staffBLManager.getStaff(staffData);
					request.setAttribute("staffData",staffData);
					List<RoleData> roleDataList  =staffBLManager.getStaffRoleRelList(staffData.getStaffId());
					request.setAttribute("roleDataList",roleDataList);
					if(updateUserNameForm.getAction() == null){
						if( staffId > 0){
							updateUserNameForm = convertBeanToForm(staffData);
							request.setAttribute("changeUserNameForm",updateUserNameForm);
						}
						return mapping.findForward(CHANGE_USERNAME);
					}else if(updateUserNameForm.getAction().equalsIgnoreCase("update")){
						
						istaffData = convertFormToBean(updateUserNameForm);
						
						IStaffData staffData1 = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
						String actionAlias = ACTION_ALIAS;
						
						
						staffBLManager.changeUserName(istaffData,staffData1,actionAlias);
						return mapping.findForward(VIEW_FORWARD);
					}
				}
			}catch (DuplicateParameterFoundExcpetion dpfExp) {
		        Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
                Logger.logTrace(MODULE,dpfExp);
    			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(dpfExp);
    			request.setAttribute("errorDetails", errorElements);
                ActionMessage message = new ActionMessage("staff.create.duplicate.failure",istaffData.getUserName());
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
				updateUserNameForm.setuserName(staffData.getUserName());
				updateUserNameForm.setReason(staffData.getReason());
			}
			return updateUserNameForm;
	}
	
	private IStaffData convertFormToBean(UpdateStaffUserNameForm updateUserNameForm){
		IStaffData staffData = null;
		if(updateUserNameForm!= null){
			staffData = new StaffData();
			staffData.setStaffId(updateUserNameForm.getStaffId());
			staffData.setUserName(updateUserNameForm.getuserName());
		}
		return staffData;
	}

}
