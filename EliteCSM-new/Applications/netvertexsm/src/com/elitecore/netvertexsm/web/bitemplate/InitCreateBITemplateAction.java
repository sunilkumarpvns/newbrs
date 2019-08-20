package com.elitecore.netvertexsm.web.bitemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.bitemplate.form.BITemplateForm;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;

public class InitCreateBITemplateAction extends BaseWebAction {
	private static final String SUCCESS_FORWARD = "createBITemplate";
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_BICEA_TEMPLATE;
	private static final String MODULE = "BI-TEMPLATE";
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		
		setDefaultValues((BITemplateForm)form,request);
		
		if(checkAccess(request, ACTION_ALIAS)){
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
	
	private void setDefaultValues(BITemplateForm form,HttpServletRequest request){
		form.setDescription(getDefaultDescription(request));
}
}
