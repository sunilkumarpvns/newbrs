package com.elitecore.netvertexsm.web.servermgr.alert;

import java.util.Arrays;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.servermgr.alert.AlertListenerBLManager;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.util.constants.BaseConstant;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.netvertexsm.web.servermgr.alert.forms.MiscAlertListenerForm;
import com.elitecore.netvertexsm.web.servermgr.alert.forms.SearchAlertListenerForm;

public class MiscAlertListenerAction extends BaseWebAction{
	
	private static final String SUCCESS_FORWARD = "success";	
	private static final String LIST_FORWARD = "list";
    private static final String FAILURE_FORWARD = "failure";
    private static final String ACTION_ALIAS=ConfigConstant.SEARCH_ALERT_LISTENER;
	private static final String ACTION_ALIAS_DELETE = ConfigConstant.DELETE_ALERT_LISTENER;

    public ActionForward execute(ActionMapping mapping,ActionForm actionForm,HttpServletRequest request,HttpServletResponse response) throws Exception {
    	Logger.logTrace(MODULE,"Enter execute method of "+getClass().getName());
    	
    	if((checkAccess(request, ACTION_ALIAS)) || (checkAccess(request,ACTION_ALIAS_DELETE))){
	    	try{
	    		MiscAlertListenerForm miscAlertListenerForm=(MiscAlertListenerForm)actionForm;
	    		
	    		AlertListenerBLManager blManager = new AlertListenerBLManager();
	    		if(miscAlertListenerForm.getAction() != null){
					String[] strSelectedIds = request.getParameterValues("select");
	    			if(strSelectedIds != null){
	    				Date currDate = new Date();
	    				if(miscAlertListenerForm.getAction().equalsIgnoreCase("delete")){
		    				checkActionPermission(request, ACTION_ALIAS_DELETE);
		    				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
		    				String actionAlias = ACTION_ALIAS_DELETE;
		    				
		    				blManager.delete(Arrays.asList(strSelectedIds),staffData,actionAlias);
		    				int strSelectedIdsLen = strSelectedIds.length;
		    				long currentPageNumber = getCurrentPageNumberAfterDel(strSelectedIdsLen,miscAlertListenerForm.getPageNumber(),miscAlertListenerForm.getTotalPages(),miscAlertListenerForm.getTotalRecords());
		    				
							
					        request.setAttribute("responseUrl","/searchAlertListener.do?name="+miscAlertListenerForm.getName()+"&typeId="+miscAlertListenerForm.getTypeId()+"&pageNumber="+currentPageNumber+"&totalPages="+miscAlertListenerForm.getTotalPages()+"&totalRecords="+miscAlertListenerForm.getTotalRecords());
							ActionMessage message = new ActionMessage("alert.delete.success");
							ActionMessages messages1 = new ActionMessages();
							messages1.add("information",message);
							saveMessages(request,messages1);
							
							
							return mapping.findForward(SUCCESS_FORWARD);
		    			}
	    			}
	    		}
		    		
	    		SearchAlertListenerForm searchAlertListenerForm = new SearchAlertListenerForm();
	      		searchAlertListenerForm.setName(miscAlertListenerForm.getName());
	      		searchAlertListenerForm.setTypeId(miscAlertListenerForm.getTypeId());
	    		searchAlertListenerForm.setPageNumber(miscAlertListenerForm.getPageNumber());
	    		searchAlertListenerForm.setTotalPages(miscAlertListenerForm.getTotalPages());
	    		searchAlertListenerForm.setTotalRecords(miscAlertListenerForm.getTotalRecords());
	    		searchAlertListenerForm.setLstAlertListener(miscAlertListenerForm.getAlertListenerLst());
	    		searchAlertListenerForm.setAction(BaseConstant.LISTACTION);
	    		request.setAttribute("searchAlertListenerForm",searchAlertListenerForm);
	    		return mapping.findForward(LIST_FORWARD);
	    		
	    	}catch(ActionNotPermitedException e){
				Logger.logError(MODULE, "Restricted to do action.");
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("general.user.restricted");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
				
	            ActionMessages errorHeadingMessage = new ActionMessages();
	            message = new ActionMessage("alert.error.heading","deleting");
	            errorHeadingMessage.add("errorHeading",message);
	            saveMessages(request,errorHeadingMessage); 				
				return mapping.findForward(INVALID_ACCESS_FORWARD);	

			}catch (DataManagerException managerExp) {
				
	            Logger.logError(MODULE, "Error during Data Manager operation , reason : " + managerExp.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
				request.setAttribute("errorDetails", errorElements);
				ActionMessages messages = new ActionMessages();
				if(managerExp.getCause() instanceof com.elitecore.netvertexsm.datamanager.core.exceptions.constraintviolation.ConstraintViolationException){
					ActionMessage message = new ActionMessage("database.datasource.delete.failure");
		            ActionMessage messageReason = new ActionMessage("database.datasource.delete.constraint.failure");
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
            ActionMessages errorHeadingMessage = new ActionMessages();
            ActionMessage message = new ActionMessage("alert.error.heading","deleting");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage); 
	    	return mapping.findForward(FAILURE_FORWARD);
	    }else{
			Logger.logError(MODULE, "Error during Data Manager operation ");
	        ActionMessage message = new ActionMessage("general.user.restricted");
	        ActionMessages messages = new ActionMessages();
	        messages.add("information", message);
	        saveErrors(request, messages);
			
            ActionMessages errorHeadingMessage = new ActionMessages();
            message = new ActionMessage("alert.error.heading","deleting");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage); 
	        
			return mapping.findForward(INVALID_ACCESS_FORWARD);	
		}
    }
}
