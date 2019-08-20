package com.elitecore.elitesm.web.rm.ippool;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.rm.ippool.IPPoolBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.rm.ippool.forms.IPPoolForm;

public class MiscIPPoolAction extends BaseWebAction{
	private static final String SUCCESS_FORWARD = "success";	
	private static final String LIST_FORWARD = "searchIPPool";
    private static final String FAILURE_FORWARD = "failure";
    private static final String ACTION_ALIAS = ConfigConstant.CHANGE_IP_POOL_STATUS_ACTION;
	private static final String ACTION_ALIAS_DELETE = ConfigConstant.DELETE_IP_POOL_ACTION;

    public ActionForward execute(ActionMapping mapping,ActionForm actionForm,HttpServletRequest request,HttpServletResponse response) throws Exception {
    	Logger.logTrace(MODULE,"Enter execute method of "+getClass().getName());
    	
    	if((checkAccess(request, ACTION_ALIAS)) || (checkAccess(request,ACTION_ALIAS_DELETE))){
	    	try{
	    		IPPoolForm iPPoolForm=(IPPoolForm)actionForm;
	    		IPPoolBLManager blManager = new IPPoolBLManager();
	    		if(iPPoolForm.getAction() != null){
					String[] strSelectedIds = request.getParameterValues("select");
	    			if(strSelectedIds != null){
	    				if(iPPoolForm.getAction().equalsIgnoreCase("show")){
		    				checkActionPermission(request, ACTION_ALIAS);
		    				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
		    				
		    				staffData.setAuditName(iPPoolForm.getName());
		    				staffData.setAuditId(iPPoolForm.getAuditUId());
		    				
		    				blManager.updateStatus(Arrays.asList(strSelectedIds),BaseConstant.SHOW_STATUS_ID,staffData,ACTION_ALIAS);
	    				}else if(iPPoolForm.getAction().equalsIgnoreCase("hide")){
		    				checkActionPermission(request, ACTION_ALIAS);
		    				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
		    				String actionAlias = ACTION_ALIAS;
		    				
		    				staffData.setAuditName(iPPoolForm.getName());
		    				staffData.setAuditId(iPPoolForm.getAuditUId());
		    				
		    				blManager.updateStatus(Arrays.asList(strSelectedIds),BaseConstant.HIDE_STATUS_ID,staffData,actionAlias);
		    			}else if(iPPoolForm.getAction().equalsIgnoreCase("delete")){
		    				checkActionPermission(request, ACTION_ALIAS_DELETE);
		    				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
		    				
		    				blManager.deleteById(Arrays.asList(strSelectedIds),staffData);
		    				
		    				int strSelectedIdsLen = strSelectedIds.length;
		    				long currentPageNumber = getCurrentPageNumberAfterDel(strSelectedIdsLen,iPPoolForm.getPageNumber(),iPPoolForm.getTotalPages(),iPPoolForm.getTotalRecords());
		    				request.setAttribute("responseUrl","/searchIPPool.do?name="+iPPoolForm.getName()+"&pageNumber="+currentPageNumber+"&totalPages="+iPPoolForm.getTotalPages()+"&totalRecords="+iPPoolForm.getTotalRecords()+"&status="+iPPoolForm.getStatus()+"&ruleSet="+iPPoolForm.getRuleSet());
							ActionMessage message = new ActionMessage("rm.ippool.delete.success");
							ActionMessages messages1 = new ActionMessages();
							messages1.add("information",message);
							saveMessages(request,messages1);
							
							return mapping.findForward(SUCCESS_FORWARD);
		    			}
	    			}
	    		}
		    		
	    		IPPoolForm searchIPPoolForm = new IPPoolForm();
	    		searchIPPoolForm.setStatus(iPPoolForm.getStatus());    		
	    		searchIPPoolForm.setName(iPPoolForm.getName());
	    		searchIPPoolForm.setPageNumber(iPPoolForm.getPageNumber());
	    		searchIPPoolForm.setTotalPages(iPPoolForm.getTotalPages());
	    		searchIPPoolForm.setTotalRecords(iPPoolForm.getTotalRecords());
	    		searchIPPoolForm.setAction(BaseConstant.LISTACTION);
	    		request.setAttribute("searchIPPoolForm",searchIPPoolForm);
	    		return mapping.findForward(LIST_FORWARD);
	    		
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
