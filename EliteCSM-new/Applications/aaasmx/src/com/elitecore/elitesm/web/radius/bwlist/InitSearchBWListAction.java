package com.elitecore.elitesm.web.radius.bwlist;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.radius.bwlist.forms.SearchBWListForm;

public class InitSearchBWListAction extends BaseWebAction {
	   private static final String SUCCESS_FORWARD = "searchBWLst";
	    private static final String FAILURE_FORWARD = "failure";
	    private static final String ACTION_ALIAS    = ConfigConstant.SEARCH_BLACKLIST_CANDIDATES_ACTION;
	    private static final String MODULE    = "InitSearchBWListAction";
	    
	    public ActionForward execute( ActionMapping mapping , ActionForm form , HttpServletRequest request , HttpServletResponse response ) {
	        Logger.logTrace(MODULE, "Enter execute method of " + getClass().getName());
	        ActionMessages messages = new ActionMessages();
	        
	        try {
//   			if (!checkAccess(request, ACTION_ALIAS))
	        	//       throw new ActionNotPermitedException(ACTION_ALIAS);
	        	IStaffData staffData =getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
	    		
	            SearchBWListForm searchBWListForm = (SearchBWListForm) form;
	            searchBWListForm.setStatus("All");
	            doAuditing(staffData, ACTION_ALIAS);
	            return mapping.findForward(SUCCESS_FORWARD);
	            
	        }
//	        catch (ActionNotPermitedException e) {
//	        	
//	            Logger.logError(MODULE, "Error during Data Manager operation ");
//	            Logger.logTrace(MODULE, e);
//	            ActionMessage message = new ActionMessage("general.user.restricted");
//	            messages.add("information", message);
//	            saveErrors(request, messages);
//	            return mapping.findForward(INVALID_ACCESS_FORWARD);
//	            
//	        } 
//	        catch (DataManagerException managerExp) {
	        
//	        	Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
//	        	request.setAttribute("errorDetails", errorElements);
//	            Logger.logError(MODULE, "Error during Data Manager operation , reason : " + managerExp.getMessage());
//	            ActionMessage message = new ActionMessage("general.error");
//	            messages.add("information", message);
//	            
//	        } 
	        catch (Exception exp) {
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
