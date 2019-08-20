package com.elitecore.elitesm.web.digestconf;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.digestconf.DigestConfBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.digestconf.forms.MiscDigestConfForm;

public class MiscDigestConfAction extends BaseWebAction{
	
	private static final String SUCCESS_FORWARD = "success";	
	private static final String LIST_FORWARD = "list";
    private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS_DELETE = ConfigConstant.DELETE_DIGEST_CONFIGURATION;

    public ActionForward execute(ActionMapping mapping,ActionForm actionForm,HttpServletRequest request,HttpServletResponse response) throws Exception {
    	Logger.logTrace(MODULE,"Enter execute method of "+getClass().getName());
    	
    	if(checkAccess(request,ACTION_ALIAS_DELETE)){
	    	try{
	    		
	    		MiscDigestConfForm miscDigestConfForm=(MiscDigestConfForm)actionForm;
	    		DigestConfBLManager digestConfBLManager = new DigestConfBLManager();
	    		
	    		if(miscDigestConfForm.getAction() != null){
					String[] strSelectedIds = request.getParameterValues("select");
	    			if(strSelectedIds != null){
		    			if(miscDigestConfForm.getAction().equalsIgnoreCase("delete")){
		    				checkActionPermission(request, ACTION_ALIAS_DELETE);
		    				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));

		    				digestConfBLManager.deleteById(Arrays.asList(strSelectedIds), staffData);
		    				int strSelectedIdsLen = strSelectedIds.length;
		    				long currentPageNumber = getCurrentPageNumberAfterDel(strSelectedIdsLen,miscDigestConfForm.getPageNumber(),miscDigestConfForm.getTotalPages(),miscDigestConfForm.getTotalRecords());
		    				
					        request.setAttribute("responseUrl","/searchDigestConf.do?name="+miscDigestConfForm.getName()+"&draftAAASipEnable="+miscDigestConfForm.getDraftAAASipEnable()+"&pageNumber="+currentPageNumber+"&totalPages="+miscDigestConfForm.getTotalPages()+"&totalRecords="+miscDigestConfForm.getTotalRecords());
							ActionMessage message = new ActionMessage("digestconf.delete.success");
							ActionMessages messages1 = new ActionMessages();
							messages1.add("information",message);
							saveMessages(request,messages1);
							
							return mapping.findForward(SUCCESS_FORWARD);
		    			}
	    			}
	    		}
	    		
	    		return mapping.findForward(LIST_FORWARD);
	    		
	    	}catch(ActionNotPermitedException e){
	             Logger.logError(MODULE,"Error :-" + e.getMessage());
	             printPermitedActionAlias(request);
	             ActionMessages messages = new ActionMessages();
	             messages.add("information", new ActionMessage("general.user.restricted"));
	             saveErrors(request, messages);
	             return mapping.findForward(INVALID_ACCESS_FORWARD);
	        }catch (DataManagerException managerExp) {
			
	            Logger.logError(MODULE, "Error during Data Manager operation , reason : " + managerExp.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
				request.setAttribute("errorDetails", errorElements);
				ActionMessages messages = new ActionMessages();
				if(managerExp.getCause() instanceof com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.ConstraintViolationException){
					ActionMessage message = new ActionMessage("digestconf.delete.failure");
		            ActionMessage messageReason = new ActionMessage("digestconf.delete.constraint.failure");
		            messages.add("information", message);
		            messages.add("information", messageReason);
		            saveErrors(request, messages);
				}else{
	               ActionMessage message = new ActionMessage("general.error");
	               messages.add("information", message);
	               saveErrors(request, messages);
				}
	        	
	        }catch(Exception managerExp){
	            Logger.logError(MODULE, "Error during Data Manager operation , reason : " + managerExp.getMessage());            
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
				request.setAttribute("errorDetails", errorElements);
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
