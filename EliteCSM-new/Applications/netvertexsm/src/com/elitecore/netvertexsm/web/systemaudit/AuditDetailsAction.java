package com.elitecore.netvertexsm.web.systemaudit;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.systemaudit.SystemAuditBLManager;
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.systemaudit.Data.ISystemAuditData;
import com.elitecore.netvertexsm.datamanager.systemaudit.Data.SystemAuditData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.cache.ConfigManager;
import com.elitecore.netvertexsm.web.core.system.staff.forms.StaffAuditForm;

public class AuditDetailsAction extends BaseWebAction{
	
	private static final String FAILURE_FORWARD = "failure";
	private static final String SUCCESS_FORWARD = "success";
	private static final String VIEW_FORWARD = "AuditDetails";
	private static final String SUB_MODULE_ACTIONALIAS = ConfigConstant.STAFF_AUDIT;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		Logger.logTrace(MODULE,"Enter the execute method of :"+getClass().getName());
		StaffAuditForm staffAuditForm = (StaffAuditForm)form;
		SystemAuditBLManager systemAuditBLManager = new SystemAuditBLManager();
		
		Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
		
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
			
			if(actionId != null && !("".equalsIgnoreCase(actionId))){
				systemAuditData.setActionId(actionId);
			}else if(staffAuditForm.getActionId() != null){
				systemAuditData.setActionId(staffAuditForm.getActionId());
			}else{
				systemAuditData.setActionId("");
			}
			
			/* user Id*/
            
			String userId=request.getParameter("userId");
			
			if(userId != null && !("".equalsIgnoreCase(userId))){
				systemAuditData.setSystemUserId(Long.parseLong(userId));
			}else if(staffAuditForm.getUserId() != null){
				systemAuditData.setSystemUserId(Long.parseLong(staffAuditForm.getUserId()));
			}else{
				systemAuditData.setSystemUserId(null);
			}
			Logger.logDebug(MODULE, "User Id:"+systemAuditData.getSystemUserId());
			
			
			/* Audit Data*/
			String strAuditDate=request.getParameter("strAuditDate");
			if(strAuditDate != null && !("".equals(strAuditDate))){
				 Date auditDate = getDate(strAuditDate);
				 systemAuditData.setAuditDate(auditDate);
			}else if(staffAuditForm.getAuditDate() != null){
				systemAuditData.setAuditDate(staffAuditForm.getAuditDate());
			}
		
			
			PageList auditDetails = systemAuditBLManager.getAuditDetails(systemAuditData,requiredPageNo,pageSize);
	
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
			 staffAuditForm.setUserId(Long.toString(systemAuditData.getSystemUserId()));
			 staffAuditForm.setAuditDate(systemAuditData.getAuditDate());
			 staffAuditForm.setActionName("search");
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
		java.text.DateFormat formatter = new java.text.SimpleDateFormat("MM-dd-yyyy");
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
