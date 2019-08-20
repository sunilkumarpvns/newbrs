package com.elitecore.netvertexsm.web.core.system.staff;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.elitecore.netvertexsm.blmanager.systemaudit.SystemAuditBLManager;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.staff.forms.StaffAuditForm;

public class InitStaffAuditAction extends BaseWebAction {
	
	 private static final String SUCCESS_FORWARD = "initStaffAudit";
	 private static final String MODULE    = "Staff Audit Action";
	
	 public ActionForward execute( ActionMapping mapping , ActionForm form , HttpServletRequest request , HttpServletResponse response ) {
		 Logger.logTrace(MODULE, "Enter execute method of " + getClass().getName());
		 
		 try{
		 
		 SystemAuditBLManager systemAuditBLManager = new SystemAuditBLManager();
		 StaffAuditForm staffAuditForm = (StaffAuditForm)form;
		 
		 List actionListInCombo = systemAuditBLManager.getAllAction();
		 List usersListInCombo = systemAuditBLManager.getAllUsers();
		 
		 staffAuditForm.setActionListInCombo(actionListInCombo);
		 staffAuditForm.setUsersListInCombo(usersListInCombo);
		 
		 }
		 catch(DataManagerException dme){
			 
		 }
		 return mapping.findForward(SUCCESS_FORWARD);
	 }

}
