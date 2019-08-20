package com.elitecore.elitesm.web.core.system.staff;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.core.system.accessgroup.AccessGroupBLManager;
import com.elitecore.elitesm.blmanager.core.system.systemparameter.PasswordSelectionPolicyBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.InvalidPasswordException;
import com.elitecore.elitesm.datamanager.core.system.systemparameter.data.PasswordPolicyConfigData;
import com.elitecore.elitesm.util.PasswordUtility;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.staff.forms.CreateStaffForm;

public class CreateStaffAction extends BaseWebAction {
	
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String LIST_FORWARD = "createStaff";
	private static final String SEARCH_FORWARD = "searchStaffList";
	private static final String LIST_FORWARD_DETAIL = "createStaffDetail";
	private static final String MODULE = "CREAET STAFF ACTION";
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_STAFF_ACTION;

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
            Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
            if(checkAccess(request, ACTION_ALIAS)){
          		try{
			    CreateStaffForm staffForm = (CreateStaffForm)form;
			    AccessGroupBLManager accessGroupBLManager = new AccessGroupBLManager();
				PasswordSelectionPolicyBLManager passwordBLManager=new PasswordSelectionPolicyBLManager();
				PasswordPolicyConfigData passwordPolicySelectionData=passwordBLManager.getPasswordSelectionPolicy();
				
				String passwordValue=staffForm.getPassword();
				
				try{
    				//check for valid password policy or not
    				PasswordUtility.validatePassword(passwordValue, passwordPolicySelectionData);
    				 
    				List listSubscribedGroup = new ArrayList(); 
					List listAccessGroupList = accessGroupBLManager.getAccessGroupList();
					request.setAttribute("listAccessGroupList",listAccessGroupList);
					request.setAttribute("listSubscribedGroup",listSubscribedGroup);
					   
					return mapping.findForward(LIST_FORWARD_DETAIL);
				
				}catch(InvalidPasswordException in){
    				Logger.logError(MODULE, "Error during Data Manager operation ");
					ActionMessage message = new ActionMessage("systemparameter.pwdpolicy.failure");
					ActionMessages messages = new ActionMessages();
					messages.add("information", message);
					
					message=new ActionMessage(in.getMessage(),in.getSourceField());
					messages.add("passwordlength", message);
					
					saveErrors(request, messages);
					return mapping.findForward(INVALID_ACCESS_FORWARD);
    			}
			}catch(Exception managerExp){
		        Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
                Logger.logTrace(MODULE,managerExp);
    			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
    			request.setAttribute("errorDetails", errorElements);
                ActionMessages messages = new ActionMessages();
                ActionMessage message = new ActionMessage("staff.create.failure");
                messages.add("information", message);
                saveErrors(request, messages);
			}
		   return mapping.findForward(FAILURE_FORWARD);
		}else{
	        Logger.logWarn(MODULE,"No Access On this Operation.");
	        ActionMessages messages = new ActionMessages();
	        ActionMessage message = new ActionMessage("general.user.restricted");
	        messages.add("information", message);
	        saveErrors(request, messages);
	        return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
}

