package com.elitecore.elitesm.web.core.system.staff;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.core.system.staff.StaffBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.util.constants.AccessPolicyConstant;
import com.elitecore.elitesm.util.constants.StaffConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.core.system.staff.forms.MiscStaffForm;
import com.elitecore.elitesm.web.core.system.staff.forms.SearchStaffForm;

public class MiscStaffAction extends BaseWebAction {
    
    private static final String SUCCESS_FORWARD     = "success";
    private static final String LIST_FORWARD        = "searchStaffList";
    private static final String FAILURE_FORWARD     = "failure";
    private static final String ACTION_ALIAS        = "CHANGE_STAFF_STATUS_ACTION";
    private static final String ACTION_ALIAS_DELETE = "DELETE_STAFF_ACTION";
    private static final String MODULE    = "MiscStaffAction";
    
    public ActionForward execute( ActionMapping mapping , ActionForm actionForm , HttpServletRequest request , HttpServletResponse response ) {
        Logger.logTrace(MODULE, "Enter execute method of " + getClass().getName());
        ActionMessages messages = new ActionMessages();
        try {
            if ((checkAccess(request, ACTION_ALIAS)) || (checkAccess(request, ACTION_ALIAS_DELETE))){
                
            MiscStaffForm miscStaffForm = (MiscStaffForm) actionForm;
            miscStaffForm.setStatus("All");
            StaffBLManager blManager = new StaffBLManager();
            SearchStaffForm searchStaffForm = new SearchStaffForm();
            long currentPageNumber = 0;
            
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
        				String actionAlias = ACTION_ALIAS_DELETE;
                        blManager.delete(Arrays.asList(strSelectedIds),staffData,actionAlias);
                        int strPolicyLen = strSelectedIds.length;
                        currentPageNumber = getCurrentPageNumberAfterDel(strPolicyLen,searchStaffForm.getPageNumber(),searchStaffForm.getTotalPages(),searchStaffForm.getTotalRecords());
                    }
                    request.setAttribute("responseUrl","/searchStaff.do?name="+searchStaffForm.getName()+"&pageNumber="+currentPageNumber+"&totalPages="+searchStaffForm.getTotalPages()+"&totalRecords="+searchStaffForm.getTotalRecords());
                }
            }
            
            
            searchStaffForm.setName(miscStaffForm.getName());
            searchStaffForm.setPageNumber(miscStaffForm.getPagetNumber());
            searchStaffForm.setTotalPages(miscStaffForm.getTotalPages());
            searchStaffForm.setTotalRecords(miscStaffForm.getTotalRecords());
            searchStaffForm.setAction(StaffConstant.LISTACTION);
            searchStaffForm.setStatus("All");
            request.setAttribute("searchStaffForm", searchStaffForm);
            
            return mapping.findForward(LIST_FORWARD);
        	}
            
        }
        catch (ActionNotPermitedException e) {
            Logger.logError(MODULE, "Error during Data Manager operation ");
            Logger.logTrace(MODULE, e);
            ActionMessage message = new ActionMessage("general.user.restricted");
            messages.add("information", message);
            saveErrors(request, messages);
            return mapping.findForward(INVALID_ACCESS_FORWARD);
            
        }
        catch (DataManagerException managerExp) {
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
        return mapping.findForward(FAILURE_FORWARD);
        
    }
}
