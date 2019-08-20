/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   InitCreateGracepolicyAction.java                 		
 * ModualName GracePolicy    			      		
 * Created on 23 December, 2010
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.web.servermgr.gracepolicy; 

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.servermgr.gracepolicy.GracePolicyBLManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.gracepolicy.data.GracepolicyData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;

public class InitGracepolicyAction extends BaseWebAction { 

	private static final String FAILURE_FORWARD = "failure";               
	private static final String GRACE_POLICY_MODULE ="GRACEPOLICY";
	private static final String LIST_FORWARD = "listgracepolicy"; 
	private static final String ACTION_ALIAS = ConfigConstant.LIST_GRACE_POLICY;
	
	@Override
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{ 
		Logger.logInfo(GRACE_POLICY_MODULE, "Entered execute method of " + getClass().getName());
		try {
			checkActionPermission(request, ACTION_ALIAS);
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));

			GracePolicyBLManager gracePolicyBLManager = new GracePolicyBLManager();
			List<GracepolicyData> gracePolicyList = gracePolicyBLManager.getGracePolicyList(staffData);			
			request.setAttribute("gracePolicyList", gracePolicyList); 				                                                                                                        
			
			return mapping.findForward(LIST_FORWARD);             
		}
 
		catch (Exception exp) {                                                                                                             
			Logger.logError(GRACE_POLICY_MODULE, "Error during Data Manager operation, Reason : " + exp.getMessage());                                 
			ActionMessage message = new ActionMessage("gracepolicy.view.failure");                                                         
			ActionMessages messages = new ActionMessages();                                                                                 
			messages.add("information", message);                                                                                           
			saveErrors(request, messages);                                                                                                  
		}                                                                                                                                   
		return mapping.findForward(FAILURE_FORWARD); 
	}
	
}
