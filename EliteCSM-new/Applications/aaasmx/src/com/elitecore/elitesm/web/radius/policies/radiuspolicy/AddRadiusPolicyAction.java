package com.elitecore.elitesm.web.radius.policies.radiuspolicy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.radius.policies.radiuspolicy.RadiusPolicyBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.radius.policies.radiuspolicy.exception.DuplicateRadiusPolicyNameException;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.radius.base.BaseDictionaryAction;
import com.elitecore.elitesm.web.radius.policies.radiuspolicy.forms.RadiusPolicyForm;

public class AddRadiusPolicyAction extends BaseDictionaryAction {
	protected static final String MODULE = "RADIUSPOLICY ACTION";
	private static final String CREATE_FORWARD = "createRadiusPolicy";
	private static final String ACTION_ALIAS = "CREATE_RADIUS_POLICY_ACTION";


	public ActionForward execute( ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws Exception{
		if(checkAccess(request, ACTION_ALIAS)){  
			Logger.logTrace(MODULE, "Entered execute method of " + getClass().getName());
			RadiusPolicyForm radiusPolicyForm = (RadiusPolicyForm)form;
			String userName = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getSystemUserName();
					
			radiusPolicyForm.setDescription(getDefaultDescription(userName));
			radiusPolicyForm.setCheckItem("");
			radiusPolicyForm.setAddItem("");
			radiusPolicyForm.setRejectItem("");
			radiusPolicyForm.setReplyItem("");
			radiusPolicyForm.setStrStatus("1");
			
			radiusPolicyForm.setDescription(getDefaultDescription(userName));
						
			request.setAttribute("radiusPolicyForm",radiusPolicyForm);
			return mapping.findForward(CREATE_FORWARD);	
		} else{
			Logger.logError(MODULE, "Error during Data Manager operation ");
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);

			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
}
