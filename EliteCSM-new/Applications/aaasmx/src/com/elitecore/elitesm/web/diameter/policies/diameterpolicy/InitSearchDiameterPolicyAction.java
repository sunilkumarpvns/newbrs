/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   InitSearchDiameterpolicyAction.java                 		
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

import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.diameter.policies.diameterpolicy.forms.SearchDiameterPolicyForm;

public class InitSearchDiameterPolicyAction extends BaseWebAction { 

	private static final String FAILURE_FORWARD = "failure";               
	private static final String MODULE ="DIAMETERPOLICY";
	private static final String INITSEARCHDIAMETERPOLICY = "searchDiameterPolicy"; 
	private static final String ACTION_ALIAS=ConfigConstant.SEARCH_AUTHORIZATION_POLICY;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{ 
		try {
			Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName());
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			SearchDiameterPolicyForm searchDiameterPolicyForm = (SearchDiameterPolicyForm)form; 			
			searchDiameterPolicyForm.setStatus("All");
			doAuditing(staffData, ACTION_ALIAS);
			return mapping.findForward(INITSEARCHDIAMETERPOLICY);             
		}catch (Exception exp) {                                                                                                             
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + exp.getMessage());                                 
			ActionMessage message = new ActionMessage("diameter.policy.search.failure");                                                         
			ActionMessages messages = new ActionMessages();                                                                                 
			messages.add("information", message);                                                                                           
			saveErrors(request, messages);                                                                                                  
		}                                                                                                                                                                                                                                                                  
		return mapping.findForward(FAILURE_FORWARD); 
	}
}
