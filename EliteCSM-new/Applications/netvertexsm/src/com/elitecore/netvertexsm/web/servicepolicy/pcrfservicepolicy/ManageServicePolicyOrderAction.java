package com.elitecore.netvertexsm.web.servicepolicy.pcrfservicepolicy;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.servicepolicy.ServicePolicyBLManager;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.netvertexsm.web.servicepolicy.pcrfservicepolicy.form.ManageServicePolicyOrderForm;

public class ManageServicePolicyOrderAction extends BaseWebAction{

	private static final String ORDER_FORWARD = "orderlist";
	private static final String SUCCESS_FORWARD = "success";
	private static final String ACTION_ALIAS = ConfigConstant.MANAGE_ORDER_PCRF_POLICY;
	private static final String MODULE = "MANAGE_SERVICE_POLICY_ORDER_ACTION";

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		
		try{
			checkActionPermission(request, ACTION_ALIAS);
			ManageServicePolicyOrderForm policyForm = (ManageServicePolicyOrderForm)form;
			ServicePolicyBLManager policyBLManager = new ServicePolicyBLManager();

			String[] order = request.getParameterValues("order");
			if(order != null){
				// change the order and save the data
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				String actionAlias = ACTION_ALIAS;
				policyBLManager.changeServicePolicyOrder(order, staffData, actionAlias);
				
				request.setAttribute("responseUrl", "/initSearchPCRFService");
				ActionMessage message = new ActionMessage("alert.manage.success");
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveMessages(request, messages);
				return mapping.findForward(SUCCESS_FORWARD);
			}
			List list = policyBLManager.searchServicePolicy();

			policyForm.setPolicyList(list);
			policyForm.setAction("list");

			request.getSession().setAttribute("policyList",policyForm.getPolicyList());
			request.getSession().setAttribute("policyForm",policyForm);

			return mapping.findForward(ORDER_FORWARD);
		}catch(ActionNotPermitedException e){
			Logger.logError(MODULE,"Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}catch(DataManagerException de){
			Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
			Logger.logTrace(MODULE,de);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(de);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("alert.manage.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			saveErrors(request,messages);

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