package com.elitecore.elitesm.web.radius.policies.radiuspolicy;

import java.util.ArrayList;
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
import com.elitecore.elitesm.blmanager.radius.policies.radiuspolicy.RadiusPolicyBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.radius.policies.radiuspolicy.data.IRadiusPolicyData;
import com.elitecore.elitesm.datamanager.radius.policies.radiuspolicy.data.RadiusPolicyData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.radius.base.BaseDictionaryAction;
import com.elitecore.elitesm.web.radius.policies.radiuspolicy.forms.UpdateRadiusPolicyStatusForm;

public class UpdateRadiusPolicyStatusAction extends BaseDictionaryAction{
	private static final String SUCCESS_FORWARD = "success";	
	private static final String UPDATE_FORWARD = "updateRadiusPolicyDetail";
	private static final String VIEW_FORWARD = "viewRadiusPolicyDetail";	
	private static final String FAILURE_FORWARD = "failure";
	private static final String strUpdateStatus = "updateStatus";
	private static final String ACTION_ALIAS=ConfigConstant.CHANGE_RADIUS_POLICY_STATUS;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		if((checkAccess(request, ACTION_ALIAS))){
			Logger.logTrace(MODULE,"Enter execute method of"+getClass().getName());
			RadiusPolicyBLManager blManager = new RadiusPolicyBLManager();
	        ActionErrors errors = new ActionErrors();
			String strRadiusPolicyId = request.getParameter("radiusPolicyId");		
			try{
				String radiusPolicyId = "";
				if(Strings.isNullOrBlank(strRadiusPolicyId) == false){
					radiusPolicyId = strRadiusPolicyId;
				}
				UpdateRadiusPolicyStatusForm updateRadiusPolicyStatusForm=(UpdateRadiusPolicyStatusForm)form;
				/*
				if(updateRadiusPolicyStatusForm == null){
					updateRadiusPolicyStatusForm = new UpdateRadiusPolicyStatusForm(); 
				}
				*/
				if(Strings.isNullOrBlank(radiusPolicyId) == true){
					radiusPolicyId = updateRadiusPolicyStatusForm.getRadiusPolicyId();
				}
				String action = updateRadiusPolicyStatusForm.getAction();
			
					if(Strings.isNullOrBlank(radiusPolicyId) == false){
						updateRadiusPolicyStatusForm.setRadiusPolicyId(radiusPolicyId);
						IRadiusPolicyData radiusPolicyData = new RadiusPolicyData();
						radiusPolicyData.setRadiusPolicyId(radiusPolicyId);
						radiusPolicyData = blManager.getRadiusPolicyDataById(radiusPolicyId);
						request.setAttribute("radiusPolicyData",radiusPolicyData);
						//System.out.println("Status Change Date-- : "+updateRadiusPolicyStatusForm.getStatusChangeDate());
						if(action != null){
							if(action.equalsIgnoreCase("update")){
								Date currentDate = new Date();
								List lstRadiusPolicyId = new ArrayList();
								lstRadiusPolicyId.add(radiusPolicyId);
								String status = updateRadiusPolicyStatusForm.getStatus();
								if(status.equalsIgnoreCase(BaseConstant.SHOW_STATUS_ID)){
									status = BaseConstant.HIDE_STATUS_ID;
								}else{
									status = BaseConstant.SHOW_STATUS_ID;								
								}
	//							blManager.updateStatus(lstRadiusPolicyId,status,new Timestamp(currentDate.getTime()));
								IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
								
								blManager.updateStatus(lstRadiusPolicyId,staffData,status);
								return mapping.findForward(VIEW_FORWARD);				
				        		
				        	}			        	
						}else{
	
							updateRadiusPolicyStatusForm.setStatus(radiusPolicyData.getCommonStatusId());
							updateRadiusPolicyStatusForm.setRadiusPolicyId(radiusPolicyData.getRadiusPolicyId());
							updateRadiusPolicyStatusForm.setStatusChangeDate(radiusPolicyData.getStatusChangeDate());
							request.setAttribute("action",strUpdateStatus);
							return mapping.findForward(UPDATE_FORWARD);
						}
					}
			
			}catch(DataManagerException managerExp){
	            Logger.logError(MODULE, "Error during Data Manager operation , reason : " + managerExp.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
				request.setAttribute("errorDetails", errorElements);
	
	            errors.add("fatal", new ActionError("general.error")); 
	            saveErrors(request,errors);
	            return mapping.findForward(FAILURE_FORWARD);
	
	        }
			return mapping.findForward(SUCCESS_FORWARD);
		}else{
	        Logger.logError(MODULE, "Error during Data Manager operation ");
	        ActionMessage message = new ActionMessage("general.user.restricted");
	        ActionMessages messages = new ActionMessages();
	        messages.add("information", message);
	        saveErrors(request, messages);
			
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
}
