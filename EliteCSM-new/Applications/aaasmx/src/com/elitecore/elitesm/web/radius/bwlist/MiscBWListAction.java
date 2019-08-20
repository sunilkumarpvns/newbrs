package com.elitecore.elitesm.web.radius.bwlist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.radius.bwlist.BWListBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.StaffConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.radius.bwlist.forms.MiscBWListForm;
import com.elitecore.elitesm.web.radius.bwlist.forms.SearchBWListForm;

public class MiscBWListAction extends BaseWebAction {
	 private static final String SUCCESS_FORWARD     = "success";
	    private static final String LIST_FORWARD        = "searchBlacklistedList";
	    private static final String FAILURE_FORWARD     = "failure";
	    private static final String ACTION_ALIAS        = ConfigConstant.CHANGE_BLACKLIST_CANDIDATES_STATUS_ACTION;
	    private static final String ACTION_ALIAS_DELETE = ConfigConstant.DELETE_BLACKLIST_CANDIDATES_ACTION;
	    private static final String MODULE    = "MiscBWListAction";
	    
	    public ActionForward execute( ActionMapping mapping , ActionForm actionForm , HttpServletRequest request , HttpServletResponse response ) {
	        Logger.logTrace(MODULE, "Enter execute method of " + getClass().getName());
	        ActionMessages messages = new ActionMessages();
	        try {
	            if ((checkAccess(request, ACTION_ALIAS)) || (checkAccess(request, ACTION_ALIAS_DELETE))){
	                
	            MiscBWListForm miscBWListForm = (MiscBWListForm) actionForm;
	            miscBWListForm.setStatus("All");
	            
	            BWListBLManager blManager = new BWListBLManager();
	            long currentPageNumber = 0;
	            if (miscBWListForm.getAction() != null) {
	                String[] strSelectedIds = request.getParameterValues("select");
	                List<String> selectedIds = new ArrayList<String>();
	                for (int i = 0; i < strSelectedIds.length; i++) {
	                	selectedIds.add(strSelectedIds[i]);
					}
	                
	                if (strSelectedIds != null) {
	                    if (miscBWListForm.getAction().equalsIgnoreCase("active")) {
	                    	checkActionPermission(request, ACTION_ALIAS);
	                    	IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
	        				String actionAlias = ACTION_ALIAS;
	                        blManager.updateStatus(Arrays.asList(strSelectedIds), BaseConstant.SHOW_STATUS_ID);
	                        doAuditing(staffData, actionAlias);
	                    } else if (miscBWListForm.getAction().equalsIgnoreCase("inactive")) {
	                    	checkActionPermission(request, ACTION_ALIAS);
	                    	IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
	        				String actionAlias = ACTION_ALIAS;
	                        blManager.updateStatus(Arrays.asList(strSelectedIds), BaseConstant.HIDE_STATUS_ID);
	                        doAuditing(staffData, actionAlias);
	                    } else if (miscBWListForm.getAction().equalsIgnoreCase("delete")) {
	                    	checkActionPermission(request, ACTION_ALIAS_DELETE);
	                    	IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
	        				String actionAlias = ACTION_ALIAS_DELETE;
	        				blManager.delete(selectedIds);
	        				doAuditing(staffData, actionAlias);
	        				int strSelectedIdsLen = strSelectedIds.length;
	        				currentPageNumber = getCurrentPageNumberAfterDel(strSelectedIdsLen, miscBWListForm.getPagetNumber(), miscBWListForm.getTotalPages(), miscBWListForm.getTotalRecords());
	        				
	                    }
	                }
	            }
	            
	            SearchBWListForm searchBWListForm = new SearchBWListForm();
	            searchBWListForm.setAttribute(miscBWListForm.getAttribute());
	            searchBWListForm.setPageNumber(miscBWListForm.getPagetNumber());
	            searchBWListForm.setPageNumber(currentPageNumber);
	            searchBWListForm.setTotalPages(miscBWListForm.getTotalPages());
	            searchBWListForm.setTotalRecords(miscBWListForm.getTotalRecords());
	            searchBWListForm.setAction(StaffConstant.LISTACTION);
	            searchBWListForm.setStatus("All");
	            request.setAttribute("searchBWListForm", searchBWListForm);
	            
	            return mapping.findForward(LIST_FORWARD);
	        	}
	            
	        }catch (ActionNotPermitedException e) {
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

	        	ActionMessage message = new ActionMessage("bwlist.delete.failure");
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
