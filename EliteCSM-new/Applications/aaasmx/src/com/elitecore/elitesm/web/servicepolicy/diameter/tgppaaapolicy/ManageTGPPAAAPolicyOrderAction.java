package com.elitecore.elitesm.web.servicepolicy.diameter.tgppaaapolicy;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.servicepolicy.diameter.TGPPAAAPolicyBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.tgppaaapolicy.data.TGPPAAAPolicyData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.servicepolicy.diameter.tgppaaapolicy.form.ManageTGPPAAAPolicyOrderForm;

public class ManageTGPPAAAPolicyOrderAction extends BaseWebAction {

	private static final String ORDER_FORWARD = "tgppAAAServicePolicyOrderList";
	private static final String ACTION_ALIAS = ConfigConstant.MANAGE_TGPP_AAA_SERVICE_POLICY_ORDER;
	private static final String MODULE = "MANAGE_ACCT_POLICY_ORDER_ACTION";

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response) throws Exception {
		Logger.logInfo(MODULE, "Enter execute method of " + getClass().getName());
		try {
			checkActionPermission(request, ACTION_ALIAS);
			ManageTGPPAAAPolicyOrderForm policyForm = (ManageTGPPAAAPolicyOrderForm) form;
			TGPPAAAPolicyBLManager policyBLManager = new TGPPAAAPolicyBLManager();

			List<TGPPAAAPolicyData> list = policyBLManager.searchActiveTGPPAAAServicePolicy();

			policyForm.setPolicyList(list);
			policyForm.setAction("list");

			return mapping.findForward(ORDER_FORWARD);

		} catch (ActionNotPermitedException e) {
			Logger.logError(MODULE, "Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		} catch (Exception e) {
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE);
	}

}
