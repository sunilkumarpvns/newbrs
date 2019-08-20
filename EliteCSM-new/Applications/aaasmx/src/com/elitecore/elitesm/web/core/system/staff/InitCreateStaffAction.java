package com.elitecore.elitesm.web.core.system.staff;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.staff.forms.CreateStaffForm;

public class InitCreateStaffAction extends BaseWebAction{
	private static final String SUCCESS_FORWARD = "initCreateStaff";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = "CREATE_STAFF_ACTION";
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		
		if(checkAccess(request, ACTION_ALIAS)){
		    CreateStaffForm createStaffForm = (CreateStaffForm)form;
		    createStaffForm.setStatus("1");
		    return mapping.findForward(SUCCESS_FORWARD);
		}else{
                    Logger.logWarn(MODULE,"No Access On this Operation.");
	            ActionMessage message = new ActionMessage("general.user.restricted");
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", message);
	            saveErrors(request, messages);
	            return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
}
