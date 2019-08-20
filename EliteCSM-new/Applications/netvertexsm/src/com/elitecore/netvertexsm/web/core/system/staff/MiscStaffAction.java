package com.elitecore.netvertexsm.web.core.system.staff;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.core.system.staff.StaffBLManager;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.util.constants.AccessPolicyConstant;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.constants.StaffConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.netvertexsm.web.core.system.staff.forms.MiscStaffForm;
import com.elitecore.netvertexsm.web.core.system.staff.forms.SearchStaffForm;

public class MiscStaffAction extends BaseWebAction {
    
    private static final String SUCCESS_FORWARD     = "success";
    private static final String LIST_FORWARD        = "searchStaffList";
    private static final String FAILURE_FORWARD     = "failure";
    private static final String ACTION_ALIAS        = ConfigConstant.CHANGE_STAFF_STATUS_ACTION;
    private static final String ACTION_ALIAS_DELETE = ConfigConstant.DELETE_STAFF_ACTION;
    private static final String MODULE    = "MiscStaffAction";
    
    public ActionForward execute( ActionMapping mapping , ActionForm actionForm , HttpServletRequest request , HttpServletResponse response ) {
        Logger.logTrace(MODULE, "Enter execute method of " + getClass().getName());
        ActionMessages messages = new ActionMessages();
        try {
            if ((checkAccess(request, ACTION_ALIAS)) || (checkAccess(request, ACTION_ALIAS_DELETE))){
                
	            MiscStaffForm miscStaffForm = (MiscStaffForm) actionForm;
	            miscStaffForm.setStatus("All");
	            StaffBLManager blManager = new StaffBLManager();
	            
	            if (miscStaffForm.getAction() != null) {
	                String[] strSelectedIds = request.getParameterValues("select");
	                if (strSelectedIds != null) {
	                    if (miscStaffForm.getAction().equalsIgnoreCase("active")) {
	                    	checkActionPermission(request, ACTION_ALIAS);
	                    	IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
	        				String actionAlias = ACTION_ALIAS;
	                    	
	                        blManager.updateStatus(Arrays.asList(strSelectedIds), AccessPolicyConstant.SHOW_STATUS_ID,staffData,actionAlias);
	                    } else if (miscStaffForm.getAction().equalsIgnoreCase("inactive")) {
	                    	checkActionPermission(request, ACTION_ALIAS);
	                    	IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
	        				String actionAlias = ACTION_ALIAS;
	                        blManager.updateStatus(Arrays.asList(strSelectedIds), AccessPolicyConstant.HIDE_STATUS_ID,staffData,actionAlias);
	                    } else if (miscStaffForm.getAction().equalsIgnoreCase("delete")) {
	                    	checkActionPermission(request, ACTION_ALIAS_DELETE);
	                    	IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
	        				String actionAlias = ACTION_ALIAS;
	                        blManager.delete(Arrays.asList(strSelectedIds),staffData,actionAlias);
	                    }
	                }
	            }
	            
	            SearchStaffForm searchStaffForm = new SearchStaffForm();
	            searchStaffForm.setName(miscStaffForm.getName());
	            searchStaffForm.setPageNumber(miscStaffForm.getPagetNumber());
	            searchStaffForm.setTotalPages(miscStaffForm.getTotalPages());
	            searchStaffForm.setTotalRecords(miscStaffForm.getTotalRecords());
	            searchStaffForm.setAction(StaffConstant.LISTACTION);
	            searchStaffForm.setStatus("All");
	            request.setAttribute("searchStaffForm", searchStaffForm);
	            
	            return mapping.findForward(LIST_FORWARD);
        	}else{
        		 Logger.logError(MODULE, "Error during Data Manager operation ");
                 ActionMessage message = new ActionMessage("general.user.restricted");
                 messages.add("information", message);
                 saveErrors(request, messages);
                 
                 ActionMessages errorHeadingMessage = new ActionMessages();
                 message = new ActionMessage("staff.error.heading","deleting");
                 errorHeadingMessage.add("errorHeading",message);
                 saveMessages(request,errorHeadingMessage);	        
                 
                 return mapping.findForward(INVALID_ACCESS_FORWARD);
        	}
            
        }catch (DataManagerException managerExp) {
            Logger.logError(MODULE, "Error during Data Manager operation , reason : " + managerExp.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("general.error");
            messages.add("information", message);
            
        }
        catch (Exception exp) {
            Logger.logError(MODULE, "Error during Data Manager operation , reason : " + exp.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(exp);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("general.error");
            messages.add("information", message);
        }
        saveErrors(request, messages);
        
        ActionMessages errorHeadingMessage = new ActionMessages();
        ActionMessage message = new ActionMessage("staff.error.heading","deleting");
        errorHeadingMessage.add("errorHeading",message);
        saveMessages(request,errorHeadingMessage);	        
        
        return mapping.findForward(FAILURE_FORWARD);
        
    }
}
