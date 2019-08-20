package com.elitecore.elitesm.web.sessionmanager;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.hibernate.exception.ConstraintViolationException;

import com.elitecore.elitesm.blmanager.sessionmanager.SessionManagerBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.sessionmanager.forms.MiscSessionManagerForm;
import com.elitecore.elitesm.web.sessionmanager.forms.SearchSessionManagerForm;

public class MiscSessionManagerAction extends BaseWebAction{
	private static final String SUCCESS_FORWARD = "success";	
	private static final String LIST_FORWARD = "sessionManagerSearchList";
    private static final String FAILURE_FORWARD = "failure";
   	private static final String ACTION_ALIAS_DELETE = ConfigConstant.DELETE_SESSION_MANAGER;

    public ActionForward execute(ActionMapping mapping,ActionForm actionForm,HttpServletRequest request,HttpServletResponse response) throws Exception {
    	Logger.logTrace(MODULE,"Enter execute method of "+getClass().getName());
    	
    	if((checkAccess(request,ACTION_ALIAS_DELETE))){
	    	try{
	    		MiscSessionManagerForm miscSessionManagerForm=(MiscSessionManagerForm)actionForm;
	    		SessionManagerBLManager blManager = new SessionManagerBLManager();
	    		if(miscSessionManagerForm.getAction() != null){
					String[] strSelectedIds = request.getParameterValues("select");

	    			if(strSelectedIds != null){
		    			
	    				if(miscSessionManagerForm.getAction().equalsIgnoreCase("delete")){
		    				
		    				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
		    				blManager.deleteById(Arrays.asList(strSelectedIds),staffData);
		    				int strSelectedIdsLen = strSelectedIds.length;
		    				long currentPageNumber = getCurrentPageNumberAfterDel(strSelectedIdsLen,miscSessionManagerForm.getPageNumber(),miscSessionManagerForm.getTotalPages(),miscSessionManagerForm.getTotalRecords());
		    				
		    				request.setAttribute("responseUrl","/searchSessionManager.do?name="+miscSessionManagerForm.getName()+"&pageNumber="+currentPageNumber+"&totalPages="+miscSessionManagerForm.getTotalPages()+"&totalRecords="+miscSessionManagerForm.getTotalRecords());
							ActionMessage message = new ActionMessage("sessionmanager.delete.success");
							ActionMessages messages1 = new ActionMessages();
							messages1.add("information",message);
							saveMessages(request,messages1);
							
							return mapping.findForward(SUCCESS_FORWARD);
		    			}
	    			}
	    		}
		    		
	    		SearchSessionManagerForm searchSessionManagerForm = new SearchSessionManagerForm();
	    		searchSessionManagerForm.setName(miscSessionManagerForm.getName());
	    		searchSessionManagerForm.setPageNumber(miscSessionManagerForm.getPageNumber());
	    		searchSessionManagerForm.setTotalPages(miscSessionManagerForm.getTotalPages());
	    		searchSessionManagerForm.setTotalRecords(miscSessionManagerForm.getTotalRecords());
	    		searchSessionManagerForm.setAction(BaseConstant.LISTACTION);
	    		request.setAttribute("searchSessionManagerForm",searchSessionManagerForm);
	    		return mapping.findForward(LIST_FORWARD);
	    		
	    	} catch(ConstraintViolationException e){
                Logger.logError(MODULE,"Error :-" + e.getMessage());
                printPermitedActionAlias(request);
                Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
    			request.setAttribute("errorDetails", errorElements);
                ActionMessages messages = new ActionMessages();
                messages.add("information", new ActionMessage("sessionmanager.childrecord"));
                saveErrors(request, messages);
                return mapping.findForward(INVALID_ACCESS_FORWARD);
            } catch(ActionNotPermitedException e){
                Logger.logError(MODULE,"Error :-" + e.getMessage());
                printPermitedActionAlias(request);
                ActionMessages messages = new ActionMessages();
                messages.add("information", new ActionMessage("general.user.restricted"));
                saveErrors(request, messages);
                return mapping.findForward(INVALID_ACCESS_FORWARD);
            }catch(Exception managerExp){
	            Logger.logError(MODULE, "Error during Data Manager operation , reason : " + managerExp.getMessage());            
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
				request.setAttribute("errorDetails", errorElements);
			    ActionMessages messages = new ActionMessages();
                messages.add("information", new ActionMessage("sessionmanager.delete.failure"));
                saveErrors(request, messages);
	    	}
	    	
	    	return mapping.findForward(FAILURE_FORWARD);
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
