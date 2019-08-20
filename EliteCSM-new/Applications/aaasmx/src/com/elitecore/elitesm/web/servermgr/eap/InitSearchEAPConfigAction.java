/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   InitSearchDigestconfAction.java                 		
 * ModualName digestconf    			      		
 * Created on 7 January, 2011
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.web.servermgr.eap; 

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servermgr.eap.forms.SearchEAPConfigForm;

public class InitSearchEAPConfigAction extends BaseWebAction { 

	private static final String SUCCESS_FORWARD = "initSearchEapConfig";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.SEARCH_EAP_CONFIGURATION;

	public ActionForward execute( ActionMapping mapping , ActionForm form , HttpServletRequest request , HttpServletResponse response ) throws Exception {
		Logger.logTrace(MODULE, "Enter execute method of " + getClass().getName());
		ActionMessages messages = new ActionMessages();

		try {
			//if (!checkAccess(request, ACTION_ALIAS))
				//throw new ActionNotPermitedException(ACTION_ALIAS);
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			
			
			SearchEAPConfigForm searchEAPConfigForm = (SearchEAPConfigForm)form;
			
			/*searchEAPConfigForm.setTls(0);
			searchEAPConfigForm.setTtls(0);
			searchEAPConfigForm.setAka(0);
			searchEAPConfigForm.setSim(0);
			*/
			searchEAPConfigForm.setName("");
			doAuditing(staffData, ACTION_ALIAS);
			return mapping.findForward(SUCCESS_FORWARD);

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