package com.elitecore.elitesm.web.radius.policies.radiuspolicy;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.radius.dictionary.RadiusDictionaryBLManager;
import com.elitecore.elitesm.blmanager.radius.policies.radiuspolicy.RadiusPolicyBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.radius.dictionary.data.IDictionaryParameterDetailData;
import com.elitecore.elitesm.datamanager.radius.policies.radiuspolicy.data.IRadiusPolicyData;
import com.elitecore.elitesm.datamanager.radius.policies.radiuspolicy.data.RadiusPolicyData;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.radius.base.BaseDictionaryAction;
import com.elitecore.elitesm.web.radius.policies.radiuspolicy.forms.UpdateRadiusPolicyCheckItemForm;

public class UpdateRadiusPolicyCheckItemAction extends BaseDictionaryAction {

	private static final String SUCCESS_FORWARD     = "success";
	private static final String UPDATE_FORWARD      = "updateRadiusPolicyDetail";
	private static final String VIEW_FORWARD        = "viewRadiusPolicyDetail";
	private static final String FAILURE_FORWARD     = "failure";
	private static final String strUpdateCheckItems = "updateCheckItems";
	private static final String ACTION_ALIAS        = "UPDATE_RADIUS_POLICY_PARAM_DETAILS_ACTION";

	public ActionForward execute( ActionMapping mapping , ActionForm form , HttpServletRequest request , HttpServletResponse response ) throws Exception {

		if ((checkAccess(request, ACTION_ALIAS))) {
			Logger.logTrace(MODULE, "Enter execute method of" + getClass().getName());
			RadiusPolicyBLManager blManager = new RadiusPolicyBLManager();
			ActionErrors errors = new ActionErrors();
			String strRadiusPolicyId = request.getParameter("radiusPolicyId");
			
			try {
				String radiusPolicyId = "";
				if(Strings.isNullOrBlank(strRadiusPolicyId) == false){
					radiusPolicyId = strRadiusPolicyId;
				}
				UpdateRadiusPolicyCheckItemForm updateRadiusPolicyCheckItemForm = (UpdateRadiusPolicyCheckItemForm) form;
				if (Strings.isNullOrBlank(radiusPolicyId) == true) {
					radiusPolicyId = updateRadiusPolicyCheckItemForm.getRadiusPolicyId();
				}

				if (updateRadiusPolicyCheckItemForm == null) {
					updateRadiusPolicyCheckItemForm = new UpdateRadiusPolicyCheckItemForm();
				}
				RadiusDictionaryBLManager dictionaryBLManager = new RadiusDictionaryBLManager();

				String action = updateRadiusPolicyCheckItemForm.getAction();
				if (Strings.isNullOrBlank(radiusPolicyId) == false) {
					updateRadiusPolicyCheckItemForm.setRadiusPolicyId(radiusPolicyId);
					IRadiusPolicyData radiusPolicyData = new RadiusPolicyData();
					radiusPolicyData.setRadiusPolicyId(radiusPolicyId);
					radiusPolicyData = blManager.getRadiusPolicyDataById(radiusPolicyId);

					request.setAttribute("preDefinedValueMap", new HashMap());

					//Setting selected Values for Static Detail Display
					request.setAttribute("radiusPolicyData", radiusPolicyData);

					if (action != null) {
						if (action.equalsIgnoreCase("update")) {
							blManager.updateCheckItem(radiusPolicyId, updateRadiusPolicyCheckItemForm.getCheckItem());
							request.getSession().removeAttribute("checkItemsList");
							return mapping.findForward(VIEW_FORWARD);
						} 
						else if (action.equalsIgnoreCase("Reload")) {
							IDictionaryParameterDetailData dicParamDetailData = dictionaryBLManager.getOnlyDictionaryParametersByAttributeId(updateRadiusPolicyCheckItemForm.getStrAttributeId());
							if(dicParamDetailData != null) {
								request.setAttribute("preDefinedValueMap", dicParamDetailData.getPreDefinedValueMap());
							}
							updateRadiusPolicyCheckItemForm.setParameterValue("");
							request.setAttribute("action", strUpdateCheckItems);
							updateRadiusPolicyCheckItemForm.setParameterValue("");
							request.setAttribute("checkItem", radiusPolicyData.getCheckItem());
							request.getSession().setAttribute("updateRadiusPolicyCheckItemForm",updateRadiusPolicyCheckItemForm);
							return mapping.findForward(UPDATE_FORWARD);
						}
						
					} else {
						request.getSession().removeAttribute("checkItemsList");
						request.setAttribute("action", strUpdateCheckItems);
						updateRadiusPolicyCheckItemForm.setCheckItem(radiusPolicyData.getCheckItem());
						request.setAttribute("checkItem", radiusPolicyData.getCheckItem());
						request.getSession().setAttribute("updateRadiusPolicyCheckItemForm", updateRadiusPolicyCheckItemForm);
						return mapping.findForward(UPDATE_FORWARD);
					}
				}
				else
				{
					Logger.logTrace(MODULE, " UpdateRadiusPolicyCheckItem : radiusPolicyId = "+radiusPolicyId);

				}
			}
			catch (DataManagerException managerExp) {
				Logger.logError(MODULE, "Error during Data Manager operation , reason : " + managerExp.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
				request.setAttribute("errorDetails", errorElements);

				errors.add("fatal", new ActionError("general.error"));
				saveErrors(request, errors);
				return mapping.findForward(FAILURE_FORWARD);

			}
			catch (Exception e) {
				e.printStackTrace();
			}

			return mapping.findForward(SUCCESS_FORWARD);
		} else {
			Logger.logError(MODULE, "Error during Data Manager operation ");
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);

			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}

}
