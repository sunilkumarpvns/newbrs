package com.elitecore.elitesm.web.core.system.accessgroup;

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

import com.elitecore.elitesm.blmanager.core.system.accessgroup.AccessGroupBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.accessgroup.forms.MiscListAccessGroupForm;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;

public class MiscListAccessGroupAction extends BaseWebAction{
	private static final String SUCCESS_FORWARD = "miscListAccessGroup";
	private static final String FAILURE_FORWARD = "failure";
	private static final String MODULE = "MISC LIST ACCESS GROUP ACTION";
	private static final String ACTION_ALIAS = "DELETE_ACCESS_GROUP_ACTION";
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logTrace(MODULE,"Entered execute method of "+getClass().getName());
		if(checkAccess(request, ACTION_ALIAS)){
		try {
			AccessGroupBLManager accessGroupBLManager = new AccessGroupBLManager();
			MiscListAccessGroupForm miscListAccessGroupForm = (MiscListAccessGroupForm)form;
			
			if(miscListAccessGroupForm.getAction() != null){
				String[] strSelectedIds = request.getParameterValues("select");
				
				if(strSelectedIds != null){
					List<String> selectedIDList = new ArrayList<String>();
		                
		            for (int i = 0; i < strSelectedIds.length; i++) {
		                  	selectedIDList.add(strSelectedIds[i]);
		            }
					if(miscListAccessGroupForm.getAction().equalsIgnoreCase("delete")){
						
						
						IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
						String actionAlias = ACTION_ALIAS;
						accessGroupBLManager.deleteAccessGroup(selectedIDList,staffData,actionAlias);
					}
				}
			}
			Logger.logTrace(MODULE, "Returning success forward from " + getClass().getName());            
			return mapping.findForward(SUCCESS_FORWARD);
		} catch (DataManagerException hExp) {
			Logger.logError(MODULE,"Error during Data Manager operation, reason: "+hExp.getMessage());
			Logger.logTrace(MODULE,"Returning error forward from "+ getClass().getName());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(hExp);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("accessgroup.delete.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			saveErrors(request,messages);
			return mapping.findForward(FAILURE_FORWARD);
		}
	}
		else{
			Logger.logError(MODULE, "Error during Data Manager operation ");
	        ActionMessage message = new ActionMessage("general.user.restricted");
	        ActionMessages messages = new ActionMessages();
	        messages.add("information", message);
	        saveErrors(request, messages);
			
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
}
