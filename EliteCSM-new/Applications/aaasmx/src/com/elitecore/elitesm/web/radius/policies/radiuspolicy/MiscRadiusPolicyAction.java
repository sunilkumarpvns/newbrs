package com.elitecore.elitesm.web.radius.policies.radiuspolicy;

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

import com.elitecore.elitesm.blmanager.radius.policies.radiuspolicy.RadiusPolicyBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.radius.base.BaseDictionaryAction;
import com.elitecore.elitesm.web.radius.policies.radiuspolicy.forms.MiscRadiusPolicyForm;
import com.elitecore.elitesm.web.radius.policies.radiuspolicy.forms.SearchRadiusPolicyForm;

public class MiscRadiusPolicyAction extends BaseDictionaryAction{
	
	private static final String SUCCESS_FORWARD = "success";	
	private static final String LIST_FORWARD = "radiusPolicySearchList";
    private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS_DELETE = ConfigConstant.DELETE_RADIUS_POLICY_ACTION; 
    private static final String ACTION_ALIAS=ConfigConstant.CHANGE_RADIUS_POLICY_STATUS;
    
    public ActionForward execute(ActionMapping mapping,ActionForm actionForm,HttpServletRequest request,HttpServletResponse response) throws Exception {
    	Logger.logTrace(MODULE,"Enter execute method of "+getClass().getName());
    	
    	if((checkAccess(request, ACTION_ALIAS)) || (checkAccess(request,ACTION_ALIAS_DELETE))){
	    	try{
	    		MiscRadiusPolicyForm miscRadiusPolicyForm=(MiscRadiusPolicyForm)actionForm;
	    		RadiusPolicyBLManager blManager = new RadiusPolicyBLManager();
	    		if(miscRadiusPolicyForm.getAction() != null){
					String[] strSelectedIds = request.getParameterValues("select");
	    			if(strSelectedIds != null){
	    				List<String> listSelectedIDs = new ArrayList<String>();
	    				for(int i=0;i<strSelectedIds.length;i++){
    						listSelectedIDs.add(strSelectedIds[i]);
    					}

		    			if(miscRadiusPolicyForm.getAction().equalsIgnoreCase("show")){
		    				checkActionPermission(request, ACTION_ALIAS);
	//	    				blManager.updateStatus(Arrays.asList(strSelectedIds),BaseConstant.SHOW_STATUS_ID,new Timestamp(currDate.getTime()));
		    				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
		    				
		    				blManager.updateStatus(listSelectedIDs,staffData,BaseConstant.SHOW_STATUS_ID);
		    			}else if(miscRadiusPolicyForm.getAction().equalsIgnoreCase("hide")){
		    				checkActionPermission(request, ACTION_ALIAS);
	//	    				blManager.updateStatus(Arrays.asList(strSelectedIds),BaseConstant.HIDE_STATUS_ID, new Timestamp(currDate.getTime()));
		    				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
		    				
		    				blManager.updateStatus(listSelectedIDs,staffData,BaseConstant.HIDE_STATUS_ID);
		    			}else if(miscRadiusPolicyForm.getAction().equalsIgnoreCase("delete")){
		    				checkActionPermission(request, ACTION_ALIAS_DELETE);
		    				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
		    				
		    				blManager.deleteById(Arrays.asList(strSelectedIds),staffData);
		    				int strSelectedIdsLen = strSelectedIds.length;
		    				long currentPageNumber = getCurrentPageNumberAfterDel(strSelectedIdsLen,miscRadiusPolicyForm.getPageNumber(),miscRadiusPolicyForm.getTotalPages(),miscRadiusPolicyForm.getTotalRecords());
		    				
		    				String status = "All";
							
					        request.setAttribute("responseUrl","/searchRadiusPolicy.do?name="+miscRadiusPolicyForm.getName()+"&pageNumber="+currentPageNumber+"&totalPages="+miscRadiusPolicyForm.getTotalPages()+"&totalRecords="+miscRadiusPolicyForm.getTotalRecords()+"&status="+status);
							//request.setAttribute("responseUrl","/searchRadiusPolicy.do?name="+miscRadiusPolicyForm.getName()+"&pageNumber="+miscRadiusPolicyForm.getPageNumber()+"&totalPages="+miscRadiusPolicyForm.getTotalPages()+"&totalRecords="+miscRadiusPolicyForm.getTotalRecords()+"&status="+status);
							ActionMessage message = new ActionMessage("radius.radiuspolicy.delete.success");
							ActionMessages messages1 = new ActionMessages();
							messages1.add("information",message);
							saveMessages(request,messages1);
							
							System.out.println("MiscRadius Policy Action");
							//return mapping.findForward(LIST_FORWARD);
							return mapping.findForward(SUCCESS_FORWARD);
		    			}
	    			}
	    		}
		    		
	    		SearchRadiusPolicyForm searchRadiusPolicyForm = new SearchRadiusPolicyForm();
	    		searchRadiusPolicyForm.setStatus(miscRadiusPolicyForm.getStatus());    		
	    		searchRadiusPolicyForm.setName(miscRadiusPolicyForm.getName());
	    		searchRadiusPolicyForm.setPageNumber(miscRadiusPolicyForm.getPageNumber());
	    		searchRadiusPolicyForm.setTotalPages(miscRadiusPolicyForm.getTotalPages());
	    		searchRadiusPolicyForm.setTotalRecords(miscRadiusPolicyForm.getTotalRecords());
	    		searchRadiusPolicyForm.setAction(BaseConstant.LISTACTION);
	    		request.setAttribute("searchRadiusPolicyForm",searchRadiusPolicyForm);
	    		return mapping.findForward(LIST_FORWARD);
	    		
	    	}catch(ActionNotPermitedException e){
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
