package com.elitecore.elitesm.web.radius.radiuspolicygroup;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.radius.policies.radiuspolicy.RadiusPolicyBLManager;
import com.elitecore.elitesm.blmanager.radius.radiuspolicygroup.RadiusPolicyGroupBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.radius.policies.radiuspolicy.data.IRadiusPolicyData;
import com.elitecore.elitesm.datamanager.radius.policies.radiuspolicy.data.RadiusPolicyData;
import com.elitecore.elitesm.datamanager.radius.radiuspolicygroup.data.RadiusPolicyGroup;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.radius.radiuspolicygroup.forms.RadiusPolicyGroupForm;

public class ViewRadiusPolicyGroupAction extends BaseWebAction{
	protected static final String MODULE = "CreateRadiusPolicyGroupAction";
	private static final String ACTION_ALIAS = ConfigConstant.VIEW_RADIUS_POLICY_GROUP;
	private static final String VIEW_FORWARD = "viewRadiusPolicyGroupDetail";

	public ActionForward execute( ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws Exception{
		if(checkAccess(request, ACTION_ALIAS)){
			try {
				Logger.logTrace(MODULE,"Enter execute method of"+getClass().getName());
				RadiusPolicyGroupBLManager radisuGroupBLManager = new  RadiusPolicyGroupBLManager();
				String strPolicyId = request.getParameter("policyId");
				String policyId = "";
				if(Strings.isNullOrBlank(strPolicyId) == false){
					policyId = strPolicyId;
				}
				if(Strings.isNullOrBlank(policyId) == false){
					
					RadiusPolicyGroup radiusPolicyGroup;

					IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
					String actionAlias = ACTION_ALIAS;

					radiusPolicyGroup = radisuGroupBLManager.getRadiusPolicyGroupById(policyId);
					request.setAttribute("radiusPolicyGroup",radiusPolicyGroup);
					
				}
			}catch (DataManagerException managerExp) {
				Logger.logError(MODULE, "Error during Data Manager operation , reason : " + managerExp.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
				request.setAttribute("errorDetails", errorElements);

				ActionMessage message = new ActionMessage("radiuspolicygroup.view.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
				return mapping.findForward(FAILURE);

			}
			return mapping.findForward(VIEW_FORWARD);
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
