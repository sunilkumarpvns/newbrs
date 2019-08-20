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
import com.elitecore.elitesm.web.radius.policies.radiuspolicy.forms.UpdateRadiusPolicyReplyItemForm;

public class UpdateRadiusPolicyReplyItemAction extends BaseDictionaryAction {
    
    private static final String SUCCESS_FORWARD     = "success";
    private static final String UPDATE_FORWARD      = "updateRadiusPolicyDetail";
    private static final String VIEW_FORWARD        = "viewRadiusPolicyDetail";
    private static final String FAILURE_FORWARD     = "failure";
    private static final String strUpdateReplyItems = "updateReplyItems";
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
                UpdateRadiusPolicyReplyItemForm updateRadiusPolicyReplyItemForm = (UpdateRadiusPolicyReplyItemForm) form;
                if (Strings.isNullOrBlank(radiusPolicyId) == true) {
                    radiusPolicyId = updateRadiusPolicyReplyItemForm.getRadiusPolicyId();
                }
                
                if (updateRadiusPolicyReplyItemForm == null) {
                    updateRadiusPolicyReplyItemForm = new UpdateRadiusPolicyReplyItemForm();
                }
                RadiusDictionaryBLManager dictionaryBLManager = new RadiusDictionaryBLManager();
                
                String action = updateRadiusPolicyReplyItemForm.getAction();
                if (Strings.isNullOrBlank(radiusPolicyId) == false) {
                    updateRadiusPolicyReplyItemForm.setRadiusPolicyId(radiusPolicyId);
                    IRadiusPolicyData radiusPolicyData = new RadiusPolicyData();
                    radiusPolicyData = blManager.getRadiusPolicyDataById(radiusPolicyId);
                           	
                    request.setAttribute("preDefinedValueMap", new HashMap());
       
                    request.setAttribute("radiusPolicyData", radiusPolicyData);
                    
                    if (action != null) {
                        if (action.equalsIgnoreCase("update")) {
            				blManager.updateReplyItem(radiusPolicyId, updateRadiusPolicyReplyItemForm.getReplyItem());
            				request.getSession().removeAttribute("replyItemsList");
                            return mapping.findForward(VIEW_FORWARD);
                            
                        }else if (action.equalsIgnoreCase("Reload")) {
							IDictionaryParameterDetailData dicParamDetailData = dictionaryBLManager.getOnlyDictionaryParametersByAttributeId(updateRadiusPolicyReplyItemForm.getStrAttributeId());
                            if(dicParamDetailData != null) {
                            	request.setAttribute("preDefinedValueMap", dicParamDetailData.getPreDefinedValueMap());
                            }
                            request.setAttribute("action", strUpdateReplyItems);
                            updateRadiusPolicyReplyItemForm.setParameterValue("");
                            request.setAttribute("replyItem", radiusPolicyData.getReplyItem());
							request.getSession().setAttribute("updateRadiusPolicyReplyItemForm",updateRadiusPolicyReplyItemForm);
							return mapping.findForward(UPDATE_FORWARD);
                        }       
                    }
                    else {
                        request.setAttribute("action", strUpdateReplyItems);
                        updateRadiusPolicyReplyItemForm.setReplyItem(radiusPolicyData.getReplyItem());
                        request.setAttribute("replyItem", radiusPolicyData.getReplyItem());
						request.getSession().setAttribute("updateRadiusPolicyReplyItemForm",updateRadiusPolicyReplyItemForm);
                        return mapping.findForward(UPDATE_FORWARD);
                    }
                }
                else
                {
                	Logger.logTrace(MODULE, "  UpdateRadiusPolicyReplyItem: radiusPolicyId = "+radiusPolicyId);

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
