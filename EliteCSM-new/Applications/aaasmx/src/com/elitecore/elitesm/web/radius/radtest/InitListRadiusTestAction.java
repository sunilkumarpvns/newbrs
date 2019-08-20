/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   InitListNtradAction.java                 		
 * ModualName radius    			      		
 * Created on 2 April, 2008
 * Last Modified on                                     
 * @author :  Himanshu Dobaria
 */                                                     
package com.elitecore.elitesm.web.radius.radtest; 
  
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.radius.radtest.RadiusTestBLManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
                                                                               
public class InitListRadiusTestAction extends BaseWebAction { 
	                                                                       
	private static final String SUCCESS_FORWARD = "success";               
	private static final String FAILURE_FORWARD = "failure";               
	private static final String MODULE ="InitListRadiusTestAction";
	private static final String VIEW_FORWARD = "initListRadiusPacket"; 
	private static final String ACTION_ALIAS    = ConfigConstant.LIST_RADIUS_PACKET;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{ 
			Logger.logTrace(MODULE, "Enter execute method of " + getClass().getName());
			ActionMessages messages = new ActionMessages();
		try{
			Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName());
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			  
			RadiusTestBLManager radiusTestBLManager = new RadiusTestBLManager();
			request.setAttribute("radiusTestList", radiusTestBLManager.getRadiusTestList());
            doAuditing(staffData, ACTION_ALIAS);
			return mapping.findForward(VIEW_FORWARD); 
		}catch (Exception e) {
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			Logger.logTrace(MODULE, e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
	
			ActionMessage message = new ActionMessage("general.error");
			messages.add("information", message);
		}
		saveErrors(request, messages);
		return mapping.findForward(FAILURE_FORWARD);
	}     
}
