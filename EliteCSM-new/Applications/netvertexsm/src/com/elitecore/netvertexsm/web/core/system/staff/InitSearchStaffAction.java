package com.elitecore.netvertexsm.web.core.system.staff;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.staff.forms.SearchStaffForm;

public class InitSearchStaffAction extends BaseWebAction {
    
    private static final String SUCCESS_FORWARD = "initSearchStaff";
    private static final String FAILURE_FORWARD = "failure";
    private static final String ACTION_ALIAS    = ConfigConstant.SEARCH_STAFF_ACTION;
    private static final String MODULE    = "InitSearchStaffAction";
    
    public ActionForward execute( ActionMapping mapping , ActionForm form , HttpServletRequest request , HttpServletResponse response ) {
        Logger.logTrace(MODULE, "Enter execute method of " + getClass().getName());
        ActionMessages messages = new ActionMessages();
        
        try {
            if (!checkAccess(request, ACTION_ALIAS))
                throw new ActionNotPermitedException(ACTION_ALIAS);
			
            SearchStaffForm searchStaffForm = (SearchStaffForm) form;
            searchStaffForm.setStatus("All");
            return mapping.findForward(SUCCESS_FORWARD);
            
        }
        catch (ActionNotPermitedException e) {
        	
            Logger.logError(MODULE, "Error during Data Manager operation ");
            Logger.logTrace(MODULE, e);
            ActionMessage message = new ActionMessage("general.user.restricted");
            messages.add("information", message);
            saveErrors(request, messages);
            return mapping.findForward(INVALID_ACCESS_FORWARD);
            
        }  catch (DataManagerException managerExp) {
        	Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
        	request.setAttribute("errorDetails", errorElements);
            Logger.logError(MODULE, "Error during Data Manager operation , reason : " + managerExp.getMessage());
            ActionMessage message = new ActionMessage("general.error");
            messages.add("information", message);
            
        }  catch (Exception exp) {
        	Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(exp);
        	request.setAttribute("errorDetails", errorElements);
        	Logger.logError(MODULE, "Error during Data Manager operation , reason : " + exp.getMessage());
            ActionMessage message = new ActionMessage("general.error");
            messages.add("information", message);
        }
        saveErrors(request, messages);
        return mapping.findForward(FAILURE_FORWARD);
        
    }
}
