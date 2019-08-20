/*package com.elitecore.elitesm.web.core.system.staff;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.elitecore.elitesm.blmanager.core.system.staff.StaffBLManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.radius.base.BaseDictionaryAction;
import com.elitecore.elitesm.web.core.system.staff.forms.EditStaffForm;

public class EditStaffAction extends BaseDictionaryAction {
	
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
//	private static final String LIST_FORWARD = "editStaff";
	private static final String LIST_FORWARD = "updateStaffBasicDetail";
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
			Logger.logTrace(MODULE,"Enter the execute method of :"+getClass().getName());
			ActionErrors errors = new ActionErrors();
//			System.out.println("here accessing the execute method of editstaffAction class :");
			try{
				EditStaffForm editStaffForm = (EditStaffForm)form;
				StaffBLManager blManager = new StaffBLManager();
				IStaffData iStaffData = new StaffData();
				
				String staffId = request.getParameter("staffid");
				
				if(staffId == null){
					staffId = editStaffForm.getStaffId();
				}
				
				String action = editStaffForm.getAction();
				
				if(action!=null && action.equalsIgnoreCase("list")){
					if(staffId != null && staffId.length() >0){
						
						editStaffForm.setStaffId(staffId);
						iStaffData.setStaffId(staffId);
						List staffList = blManager.getList(iStaffData);
						
						if(staffList.size()>0){

						}
						return mapping.findForward(LIST_FORWARD);
					}else{
						return mapping.findForward(FAILURE_FORWARD);
					}
				}else if(action !=null && action.equalsIgnoreCase("update")){
					
					
					blManager.update(iStaffData,staffId);
				}
				return mapping.findForward(SUCCESS_FORWARD);
			}catch(Exception managerExp){
				managerExp.printStackTrace();
				Logger.logTrace(MODULE,"Error during data Manager operation,reason :"+managerExp.getMessage());
			}
			errors.add("fatal", new ActionError("staff.update.failure")); 
	         saveErrors(request,errors);
		return mapping.findForward(FAILURE_FORWARD);
	}
}
*/