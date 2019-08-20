package com.elitecore.elitesm.web.diameter.diameterpolicygroup;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.diameter.diameterpolicygroup.DiameterPolicyGroupBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.diameter.diameterpolicygroup.data.DiameterPolicyGroup;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;

public class ViewDiameterPolicyGroupAction extends BaseWebAction{
	protected static final String MODULE = "ViewDiameterPolicyGroupAction";
	private static final String ACTION_ALIAS = ConfigConstant.VIEW_DIAMETER_POLICY_GROUP;
	private static final String VIEW_FORWARD = "viewDiameterPolicyGroupDetail";

	public ActionForward execute( ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws Exception{
		if(checkAccess(request, ACTION_ALIAS)){
			try {
				Logger.logTrace(MODULE,"Enter execute method of"+getClass().getName());
				DiameterPolicyGroupBLManager diameterPolicyGroupBLManager = new DiameterPolicyGroupBLManager();
				String strPolicyId = request.getParameter("policyId");
				String policyId = "";
				if(Strings.isNullOrBlank(strPolicyId) == false){
					policyId = strPolicyId;
				}
				if(Strings.isNullOrBlank(policyId) == false){
					
					DiameterPolicyGroup diameterPolicyGroup;

					IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
					String actionAlias = ACTION_ALIAS;

					diameterPolicyGroup = diameterPolicyGroupBLManager.getDiameterPolicyGroupDataById(policyId);
					request.setAttribute("diameterPolicyGroup",diameterPolicyGroup);
					
				}
			}catch (DataManagerException managerExp) {
				Logger.logError(MODULE, "Error during Data Manager operation , reason : " + managerExp.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
				request.setAttribute("errorDetails", errorElements);

				ActionMessage message = new ActionMessage("diameterpolicygroup.view.failure");
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
