package com.elitecore.elitesm.web.core.system.staff;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.systemaudit.SystemAuditBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.systemaudit.Data.ISystemAuditData;
import com.elitecore.elitesm.datamanager.systemaudit.Data.SystemAuditData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.core.system.staff.forms.StaffAuditForm;

public class InitStaffAuditAction extends BaseWebAction {
	
	 private static final String SUCCESS_FORWARD = "initStaffAudit";
	 private static final String MODULE    = "Staff Audit Action";
	 private static final String ACTION_ALIAS=ConfigConstant.STAFF_AUDIT_ACTION;
	 
	 public ActionForward execute( ActionMapping mapping , ActionForm form , HttpServletRequest request , HttpServletResponse response ) {
		 Logger.logTrace(MODULE, "Enter execute method of " + getClass().getName());
		 ActionMessages messages = new ActionMessages();
		 try{
			 SystemAuditBLManager systemAuditBLManager = new SystemAuditBLManager();
			 StaffAuditForm staffAuditForm = (StaffAuditForm)form;
				
			 List actionListInCombo = systemAuditBLManager.getAllAction();
			 List usersListInCombo = systemAuditBLManager.getAllUsers();
			 
			 staffAuditForm.setActionListInCombo(actionListInCombo);
			 staffAuditForm.setUsersListInCombo(usersListInCombo);
			 
			 Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
			 IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			
			 int requiredPageNo;
			 if(request.getParameter("pageNo") != null){
				requiredPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNo")));
			}else{
				requiredPageNo = new Long(staffAuditForm.getPageNumber()).intValue();
			}
			if(requiredPageNo == 0)
				requiredPageNo = 1;
				
			ISystemAuditData systemAuditData = new SystemAuditData();
			systemAuditData.setActionId("");
			systemAuditData.setSystemUserId("");
			systemAuditData.setAuditName("");
			PageList auditDetails = systemAuditBLManager.getAuditDetails(systemAuditData,requiredPageNo,pageSize,staffData);
			
			if(auditDetails!=null){
				staffAuditForm.setPageNumber(auditDetails.getCurrentPage());
				staffAuditForm.setTotalPages(auditDetails.getTotalPages());
				staffAuditForm.setTotalRecords(auditDetails.getTotalItems());								
				// this is List without Group BY
				staffAuditForm.setAuditDetailList(auditDetails.getListData());
			}
			
			return mapping.findForward(SUCCESS_FORWARD);
		 }catch(DataManagerException dme){
			 	Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(dme);
	        	request.setAttribute("errorDetails", errorElements);
	        	Logger.logError(MODULE, "Error during Data Manager operation , reason : " + dme.getMessage());
	            ActionMessage message = new ActionMessage("general.error");
	            messages.add("information", message);
		 }
		 return mapping.findForward(FAILURE);
	 }

}
