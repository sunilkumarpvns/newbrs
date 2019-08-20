package com.elitecore.elitesm.web.servicepolicy.rm.cgpolicy;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.servermgr.script.ScriptBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptInstanceData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.ScriptTypesConstants;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servicepolicy.rm.cgpolicy.forms.CreateCGPolicyForm;

public class InitCreateCGPolicyAction extends BaseWebAction {
	private static final String MODULE ="CGPOLICY";
	private static final String FAILURE_FORWARD = "failure";               
	private static final String INITCREATECGPOLICY = "initCreateCGPolicy"; 
	private static final String ACTION_ALIAS =ConfigConstant.CREATE_CG_POLICY;

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{ 
		try{
			checkActionPermission(request,ACTION_ALIAS);	
			Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName());
			String userName = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getSystemUserName();
			CreateCGPolicyForm createCGPolicyForm = (CreateCGPolicyForm)form;
			createCGPolicyForm.setStatus("1");
			createCGPolicyForm.setDescription(getDefaultDescription(userName));
			
			/* Driver Script */
			ScriptBLManager scriptBLManager = new ScriptBLManager();
			List<ScriptInstanceData> driverScriptList = (List<ScriptInstanceData>) scriptBLManager.getScriptInstanceDataByTypeId(ScriptTypesConstants.DRIVER_SCRIPT);
		
			createCGPolicyForm.setDriverScriptList(driverScriptList);
			
			request.getSession().setAttribute("createCGPolicyForm", createCGPolicyForm);
			return mapping.findForward(INITCREATECGPOLICY);
		}catch(ActionNotPermitedException e){
			Logger.logError(MODULE, "Restricted to do action.");
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);	

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
