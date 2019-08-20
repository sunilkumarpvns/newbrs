package com.elitecore.elitesm.web.radius.policies.radiuspolicy;

import java.util.HashMap;
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
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.radius.base.BaseDictionaryAction;
import com.elitecore.elitesm.web.radius.policies.radiuspolicy.forms.UpdateRadiusPolicyReplaceItemForm;

public class UpdateRadiusPolicyReplaceItemAction extends BaseDictionaryAction{
	private static final String SUCCESS_FORWARD = "success";	
	private static final String UPDATE_FORWARD = "updateRadiusPolicyDetail";
	private static final String VIEW_FORWARD = "viewRadiusPolicyDetail";	
	private static final String FAILURE_FORWARD = "failure";
	private static final String strParameterUsage = "A";	
	private static final String strUpdateReplaceItems = "updateReplaceItems";
	private static final String ACTION_ALIAS = "UPDATE_RADIUS_POLICY_ACTION";
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		if((checkAccess(request, ACTION_ALIAS))){
			Logger.logTrace(MODULE,"Enter execute method of"+getClass().getName());
			RadiusPolicyBLManager blManager = new RadiusPolicyBLManager();
	        ActionErrors errors = new ActionErrors();
			String strRadiusPolicyId = request.getParameter("radiusPolicyId");		
			try{
				String radiusPolicyId = "";
				if(Strings.isNullOrBlank(strRadiusPolicyId) == false){
					radiusPolicyId =strRadiusPolicyId;
				}
				UpdateRadiusPolicyReplaceItemForm updateRadiusPolicyReplaceItemForm=(UpdateRadiusPolicyReplaceItemForm)form;
				if(Strings.isNullOrBlank(radiusPolicyId) == true){
					radiusPolicyId = updateRadiusPolicyReplaceItemForm.getRadiusPolicyId();
				}
				
				if(updateRadiusPolicyReplaceItemForm == null){
					updateRadiusPolicyReplaceItemForm = new UpdateRadiusPolicyReplaceItemForm(); 
				}
				
				String action = updateRadiusPolicyReplaceItemForm.getAction();
					if(Strings.isNullOrBlank(radiusPolicyId) == false){
						updateRadiusPolicyReplaceItemForm.setRadiusPolicyId(radiusPolicyId);
						IRadiusPolicyData radiusPolicyData = new RadiusPolicyData();
						radiusPolicyData = blManager.getRadiusPolicyDataById(radiusPolicyId);

                         request.setAttribute("preDefinedValueMap",new HashMap());
						
						request.setAttribute("radiusPolicyData",radiusPolicyData);
	
						if(action != null){
				        	if(action.equalsIgnoreCase("add") ){
								request.setAttribute("action",strUpdateReplaceItems);
                                                                updateRadiusPolicyReplaceItemForm.setParameterId(0);
                                                                updateRadiusPolicyReplaceItemForm.setParameterValue("");
                                                                updateRadiusPolicyReplaceItemForm.setOperatorId("");
	
								return mapping.findForward(UPDATE_FORWARD);
								
				        	}
				        	else if(action.equalsIgnoreCase("update")){
				        		
				        		IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
	            				String actionAlias = ACTION_ALIAS;
				        		blManager.updateRadiusPolicyParamDetails(((List)request.getSession().getAttribute("replaceItemsList")), radiusPolicyId,strParameterUsage,staffData,actionAlias);
				        		request.getSession().removeAttribute("replaceItemsList");			        		
				        		return mapping.findForward(VIEW_FORWARD);				
				        	}
						}else{
			        		request.getSession().removeAttribute("replaceItemsList");						
							request.setAttribute("action",strUpdateReplaceItems);
							
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
