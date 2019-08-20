/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   InitCreateDiameterpolicyAction.java                 		
 * ModualName diameterpolicy    			      		
 * Created on 16 May, 2011
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.web.diameter.policies.diameterpolicy; 

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.diameter.policies.diameterpolicy.forms.InitCreateDiameterPolicyForm;

public class InitCreateDiameterPolicyAction extends BaseWebAction { 

	private static final String SUCCESS_FORWARD = "success";               
	private static final String FAILURE_FORWARD = "failure";               
	private static final String MODULE ="DIAMETERPOLICY";
	private static final String INITCREATEDIAMETERPOLICY = "initCreateDiameterPolicy"; 

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{ 
		try {
			Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName()); 
			InitCreateDiameterPolicyForm createDiameterPolicyForm = (InitCreateDiameterPolicyForm)form; 
			createDiameterPolicyForm.setStatus("1");
			String userName = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getSystemUserName();
			createDiameterPolicyForm.setDescription(getDefaultDescription(userName));
			request.setAttribute("createDiameterPolicyForm", createDiameterPolicyForm);
		return mapping.findForward(INITCREATEDIAMETERPOLICY);
		}catch(Exception e){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			
		}                                                                                           
		return mapping.findForward(FAILURE_FORWARD); 
	}
}
