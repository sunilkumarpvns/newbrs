package com.elitecore.netvertexsm.web.gateway.profile;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.gateway.GatewayBLManager;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.gateway.profile.form.CreateProfileForm;

public class InitCreateProfileAction extends BaseWebAction{
	private static final String SUCCESS_FORWARD = "initCreateProfile";
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_GATEWAY_PROFILE;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		
		if(checkAccess(request, ACTION_ALIAS)){
		    CreateProfileForm createProfileForm = (CreateProfileForm) form;
		    GatewayBLManager gatewayBLManager = new GatewayBLManager();
		    
		    createProfileForm.setStatus("Active");
		    List vendorList = gatewayBLManager.getVendorList();
		    createProfileForm.setVendorList(vendorList);		    
		    createProfileForm.setDescription(getDefaultDescription(request));
			
		    return mapping.findForward(SUCCESS_FORWARD);  
		}else{
            Logger.logWarn(MODULE,"No Access On this Operation.");
	        ActionMessage message = new ActionMessage("general.user.restricted");
            ActionMessages messages = new ActionMessages();
            messages.add("information", message);
            saveErrors(request, messages);
            return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
}
