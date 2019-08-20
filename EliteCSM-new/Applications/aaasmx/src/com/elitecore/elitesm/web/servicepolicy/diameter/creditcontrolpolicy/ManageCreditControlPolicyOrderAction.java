package com.elitecore.elitesm.web.servicepolicy.diameter.creditcontrolpolicy;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.servicepolicy.diameter.CreditControlPolicyBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.creditcontrolpolicy.data.CreditControlPolicyData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.servicepolicy.diameter.creditcontrolpolicy.forms.ManageCreditControlPolicyOrderForm;

public class ManageCreditControlPolicyOrderAction extends BaseWebAction{
	
	private static final String MODULE ="CREDITCONTROLPOLICY";
	private static final String MANAGEORDERCCPOLICY = "manageOrderCcPolicy"; 
	private static final String ACTION_ALIAS = ConfigConstant.MANAGE_CREDIT_CONTROL_SERVICE_POLICY_ORDER;
 
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{ 
		try {
			 checkActionPermission(request,ACTION_ALIAS);
			 Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName());
			 
			 ManageCreditControlPolicyOrderForm manageOrForm = (ManageCreditControlPolicyOrderForm)form;
			 CreditControlPolicyBLManager blManager = new CreditControlPolicyBLManager();
			 
			 List<CreditControlPolicyData> policyList = blManager.getCreditControlPolicies();
				
			 manageOrForm.setPolicyList(policyList);

			return mapping.findForward(MANAGEORDERCCPOLICY);
		}catch(ActionNotPermitedException e){
			Logger.logError(MODULE,"Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
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
		return mapping.findForward(FAILURE);		
	}
}
