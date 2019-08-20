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

import com.elitecore.elitesm.blmanager.radius.dictionary.RadiusDictionaryBLManager;
import com.elitecore.elitesm.datamanager.radius.dictionary.data.IDictionaryParameterDetailData;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.radius.base.BaseDictionaryAction;
import com.elitecore.elitesm.web.radius.policies.radiuspolicy.forms.AddRadiusPolicyForm;
import com.elitecore.elitesm.web.radius.policies.radiuspolicy.forms.AddRadiusPolicyRejectItemForm;


public class AddRadiusPolicyRejectItemAction extends BaseDictionaryAction {
    protected static final String MODULE = "RADIUSPOLICY ACTION";
    private static final String FAILURE_FORWARD = "failure";
    private static final String CREATE_RADIUSPOLICY_FORWARD = "createRadiusPolicy";    
    private static final String CREATE_REJECTITEM_FORWARD = "createRadiusPolicyRejectItem";
    private static final String CREATE_REPLYITEM_FORWARD = "createRadiusPolicyReplyItem";    
	private static final String ACTION_ALIAS = "CREATE_RADIUS_POLICY_ACTION";
    
	public ActionForward execute( ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws Exception{
		if(checkAccess(request, ACTION_ALIAS)){
			Logger.logTrace(MODULE, "Entered execute method of " + getClass().getName());
			AddRadiusPolicyRejectItemForm radiusPolicyRejectItemForm = (AddRadiusPolicyRejectItemForm)form;
			AddRadiusPolicyForm radiusPolicyForm = (AddRadiusPolicyForm)request.getSession().getAttribute("addRadiusPolicyForm");
			ActionErrors errors = new ActionErrors();
			String action = radiusPolicyRejectItemForm.getRejectAction();

			RadiusDictionaryBLManager dictionaryBLManager = new RadiusDictionaryBLManager();
			request.setAttribute("preDefinedValueMap",new HashMap());
			request.setAttribute("addRadiusPolicyRejectItemForm",radiusPolicyRejectItemForm);
			if(action != null){
				if(action.equalsIgnoreCase("next")){
					radiusPolicyForm.setRejectItem(radiusPolicyRejectItemForm.getRejectItem());
					radiusPolicyRejectItemForm.setParameterValue("");
					request.getSession().setAttribute("addRadiusPolicyForm", radiusPolicyForm);
					if(radiusPolicyForm.isReplyItemForward())
						return mapping.findForward(CREATE_REPLYITEM_FORWARD);
					return mapping.findForward(CREATE_RADIUSPOLICY_FORWARD);
				}else if(action.equalsIgnoreCase("Reload")){
					IDictionaryParameterDetailData dicParamDetailData = dictionaryBLManager.getOnlyDictionaryParametersByAttributeId(radiusPolicyRejectItemForm.getStrAttributeId());
					radiusPolicyRejectItemForm.setStatus(radiusPolicyForm.getStrStatus());
					radiusPolicyRejectItemForm.setParameterValue("");
					if(dicParamDetailData != null) {
						request.setAttribute("preDefinedValueMap",dicParamDetailData.getPreDefinedValueMap());
					}
					return mapping.findForward(CREATE_REJECTITEM_FORWARD);
				}
			}else{
				radiusPolicyRejectItemForm.setStatus(radiusPolicyForm.getStrStatus());
				radiusPolicyRejectItemForm.setParameterValue("");
				return mapping.findForward(CREATE_REJECTITEM_FORWARD);        		
			}
			//TODO : Baiju - set error message and then forward to error page.
			Logger.logTrace(MODULE, "Returning error forward from " + getClass().getName());
			errors.add("fatal", new ActionError("general.error")); 
			saveErrors(request,errors);
			return mapping.findForward(FAILURE_FORWARD);
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
