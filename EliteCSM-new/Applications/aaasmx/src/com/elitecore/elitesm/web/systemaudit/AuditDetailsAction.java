package com.elitecore.elitesm.web.systemaudit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.systemaudit.SystemAuditBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.systemaudit.Data.ISystemAuditData;
import com.elitecore.elitesm.datamanager.systemaudit.Data.SystemAuditData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.core.system.staff.forms.StaffAuditForm;

public class AuditDetailsAction extends BaseWebAction{
	
	private static final String FAILURE_FORWARD = "failure";
	private static final String SUCCESS_FORWARD = "success";
	private static final String VIEW_FORWARD = "AuditDetails";
	private static final String SUB_MODULE_ACTIONALIAS = ConfigConstant.STAFF_AUDIT_ACTION;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		Logger.logTrace(MODULE,"Enter the execute method of :"+getClass().getName());
		StaffAuditForm staffAuditForm = (StaffAuditForm)form;
		SystemAuditBLManager systemAuditBLManager = new SystemAuditBLManager();
		
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
		
		try{
			checkActionPermission(request,SUB_MODULE_ACTIONALIAS);
			/*
					 * String strPolicyName =request.getParameter("name");
					if(strPolicyName != null){
						authPolicyInstData.setName(strPolicyName);
					}else if(searchAuthServicePolicyForm.getName() != null){
						authPolicyInstData.setName(searchAuthServicePolicyForm.getName());
					}else{
						authPolicyInstData.setName("");
					}
			 */
			/*Action Id*/
			
			String actionId=request.getParameter("actionId");
			
			System.out.println("Audit action id : "+actionId);
			
			
			
			if(actionId != null && !("".equalsIgnoreCase(actionId)) && !(actionId.trim().equals("0"))){
				systemAuditData.setActionId(actionId);
			}else if(staffAuditForm.getActionId() != null && !(actionId.trim().equals("0"))){
				systemAuditData.setActionId(staffAuditForm.getActionId());
			}else{
				systemAuditData.setActionId("");
			}
			/* user Id*/
            
			String userId=request.getParameter("userId");
			
			if(userId != null && !("".equalsIgnoreCase(userId))){
				systemAuditData.setSystemUserId(userId);
			}else if(staffAuditForm.getUserId() != null){
				systemAuditData.setSystemUserId(staffAuditForm.getUserId());
			}else{
				systemAuditData.setSystemUserId("");
			}
       
			/* Audit Data*/
			
			
			
			String strAuditDate=request.getParameter("strAuditDate");
			if(strAuditDate != null && !("".equals(strAuditDate))){
				 Date auditDate = getDate(strAuditDate);
				 systemAuditData.setAuditDate(auditDate);
			}else if(staffAuditForm.getAuditDate() != null){
				systemAuditData.setAuditDate(staffAuditForm.getAuditDate());
			}
			
			String strAuditDateFrom=request.getParameter("strAuditDateFrom");
			if(strAuditDateFrom != null && !("".equals(strAuditDateFrom))){
				 Date auditDateFrom = getDate(strAuditDateFrom);
				 systemAuditData.setAuditDateFrom(auditDateFrom);
			}else if(staffAuditForm.getAuditDateFrom() != null){
				systemAuditData.setAuditDateFrom(staffAuditForm.getAuditDateFrom());
			}
			
			
			String strName=request.getParameter("name");
			
			if(strName != null && !("".equalsIgnoreCase(strName))){
				systemAuditData.setAuditName(strName);
			}else{
				systemAuditData.setAuditName("");
			}
			
			
			PageList auditDetails = systemAuditBLManager.getAuditDetails(systemAuditData,requiredPageNo,pageSize,staffData);
			
			if(auditDetails!=null){
				staffAuditForm.setPageNumber(auditDetails.getCurrentPage());
				staffAuditForm.setTotalPages(auditDetails.getTotalPages());
				staffAuditForm.setTotalRecords(auditDetails.getTotalItems());								
				// this is List without Group BY
				staffAuditForm.setAuditDetailList(auditDetails.getListData());
				
				
			}
			 List actionListInCombo = systemAuditBLManager.getAllAction();
			 List usersListInCombo = systemAuditBLManager.getAllUsers();
			 
			 staffAuditForm.setActionListInCombo(actionListInCombo);
			 staffAuditForm.setUsersListInCombo(usersListInCombo);
			 staffAuditForm.setActionId(systemAuditData.getActionId());
			 staffAuditForm.setUserId(systemAuditData.getSystemUserId());
			 staffAuditForm.setAuditDate(systemAuditData.getAuditDate());
			 staffAuditForm.setActionName("search");
			 doAuditing(staffData, SUB_MODULE_ACTIONALIAS);
		}
		 catch(ActionNotPermitedException e){
             Logger.logError(MODULE,"Error :-" + e.getMessage());
             printPermitedActionAlias(request);
             ActionMessages messages = new ActionMessages();
             messages.add("information", new ActionMessage("general.user.restricted"));
             saveErrors(request, messages);
             return mapping.findForward(INVALID_ACCESS_FORWARD);
         }
		catch(Exception e){
			e.printStackTrace();
		}
		return mapping.findForward(VIEW_FORWARD);
	}

	private Date getDate(String strAuditDate) {
	    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		Date date=null;
		try {  
			if(!"".equalsIgnoreCase(strAuditDate)){
				date = (Date)formatter.parse(strAuditDate);
			}

		} catch (ParseException e)
		{
			e.printStackTrace();
		}
		return date;
	}

}
