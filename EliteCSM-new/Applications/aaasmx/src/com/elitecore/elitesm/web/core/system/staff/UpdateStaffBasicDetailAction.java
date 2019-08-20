package com.elitecore.elitesm.web.core.system.staff;

import java.sql.Timestamp;
import java.util.Date;
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
import com.elitecore.elitesm.web.core.system.staff.forms.UpdateStaffBasicDetailForm;

public class UpdateStaffBasicDetailAction extends BaseWebAction {
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String UPDATE_FORWARD = "updateStaffBasicDetail";
	private static final String VIEW_FORWARD = "viewStaffDetail";
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_STAFF_ACTION;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		if(checkAccess(request, ACTION_ALIAS)){
			Logger.logTrace(MODULE,"Enter execute method of :"+getClass().getName());
			
			StaffBLManager staffBLManager = new StaffBLManager();

			ActionErrors errors = new ActionErrors();
	
			try{
				UpdateStaffBasicDetailForm updateStaffBasicDetailForm = (UpdateStaffBasicDetailForm)form;
				String strStaffId = request.getParameter("staffid");
				String staffId;
				if(strStaffId == null){
					staffId = updateStaffBasicDetailForm.getStaffId();
				}else{
					staffId = request.getParameter("staffid");
				}
				if(updateStaffBasicDetailForm.getAction() == null){
					if(Strings.isNullOrBlank(staffId) == false){
						IStaffData staffData = new StaffData();
						staffData.setStaffId(staffId);
						staffData = staffBLManager.getStaff(staffData);
						request.setAttribute("staffData",staffData);
						List<GroupData> groupDataList  =staffBLManager.getStaffGroupRelList(staffData.getStaffId());
						request.setAttribute("groupDataList",groupDataList);
						updateStaffBasicDetailForm = convertBeanToForm(staffData);
						request.setAttribute("updateStaffBasicDetailForm",updateStaffBasicDetailForm);
					}
					return mapping.findForward(UPDATE_FORWARD);
				}else if(updateStaffBasicDetailForm.getAction().equalsIgnoreCase("update")){
					System.out.println("Staffid : "+updateStaffBasicDetailForm.getStaffId());
					
					IStaffData staffData = new StaffData();
					staffData.setStaffId(staffId);
					staffData = staffBLManager.getStaff(staffData);
					
					IStaffData istaffData = convertFormToBean(updateStaffBasicDetailForm,staffData);
					istaffData.setLastModifiedDate(new Timestamp((new Date()).getTime()));
					
					istaffData.setAuditName(istaffData.getName());
					istaffData.setAuditId(istaffData.getAuditUId());
					
					String actionAlias = ACTION_ALIAS;
					staffBLManager.updateBasicDetail(istaffData,actionAlias);
					return mapping.findForward(VIEW_FORWARD);
				}
			}catch(Exception e){
				e.printStackTrace();
    			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
    			request.setAttribute("errorDetails", errorElements);
			}
			errors.add("fatal", new ActionError("staff.updatebasicdetail.failure")); 
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
	
	private UpdateStaffBasicDetailForm convertBeanToForm(IStaffData staffData){
		UpdateStaffBasicDetailForm updateStaffBasicDetailForm = null;
		if(staffData!=null){
			updateStaffBasicDetailForm = new UpdateStaffBasicDetailForm();
			updateStaffBasicDetailForm.setStaffId(staffData.getStaffId());
			updateStaffBasicDetailForm.setName(staffData.getName());
			updateStaffBasicDetailForm.setUsername(staffData.getUsername());
			updateStaffBasicDetailForm.setBirthDate(staffData.getBirthDate());
			updateStaffBasicDetailForm.setAddress1(staffData.getAddress1());
			updateStaffBasicDetailForm.setAddress2(staffData.getAddress2());
			updateStaffBasicDetailForm.setCity(staffData.getCity());
			updateStaffBasicDetailForm.setState(staffData.getState());
			updateStaffBasicDetailForm.setCountry(staffData.getCountry());
			updateStaffBasicDetailForm.setZip(staffData.getZip());
			updateStaffBasicDetailForm.setEmailAddress(staffData.getEmailAddress());
			updateStaffBasicDetailForm.setPhone(staffData.getPhone());
			updateStaffBasicDetailForm.setMobile(staffData.getMobile());
			updateStaffBasicDetailForm.setAuditUId(staffData.getAuditUId());
			updateStaffBasicDetailForm.setAuditName(staffData.getAuditName());
		}
		return updateStaffBasicDetailForm;
	}
	
	private IStaffData convertFormToBean(UpdateStaffBasicDetailForm updateStaffBasicDetailForm,IStaffData staffData){
		if(updateStaffBasicDetailForm!=null){
			staffData.setStaffId(updateStaffBasicDetailForm.getStaffId());
			staffData.setName(updateStaffBasicDetailForm.getName());
			staffData.setUsername(updateStaffBasicDetailForm.getUsername());
			staffData.setBirthDate(updateStaffBasicDetailForm.getBirthDate());
			staffData.setAddress1(updateStaffBasicDetailForm.getAddress1());
			staffData.setAddress2(updateStaffBasicDetailForm.getAddress2());
			staffData.setCity(updateStaffBasicDetailForm.getCity());
			staffData.setState(updateStaffBasicDetailForm.getState());
			staffData.setCountry(updateStaffBasicDetailForm.getCountry());
			staffData.setZip(updateStaffBasicDetailForm.getZip());
			staffData.setEmailAddress(updateStaffBasicDetailForm.getEmailAddress());
			staffData.setPhone(updateStaffBasicDetailForm.getPhone());
			staffData.setMobile(updateStaffBasicDetailForm.getMobile());
			staffData.setAuditName(updateStaffBasicDetailForm.getAuditName());
			staffData.setAuditUId(updateStaffBasicDetailForm.getAuditUId());
		}
		return staffData;
	}
	
}
