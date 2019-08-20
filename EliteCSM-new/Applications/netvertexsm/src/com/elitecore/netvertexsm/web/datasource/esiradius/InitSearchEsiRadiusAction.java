package com.elitecore.netvertexsm.web.datasource.esiradius;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.datasource.esiradius.form.SearchEsiRadiusForm;

public class InitSearchEsiRadiusAction extends BaseWebAction {
	private static final String SUCCESS_FORWARD = "initSearchEsiRadius";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.SEARCH_ESIRADIUS_ACTION;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		
		if(checkAccess(request, ACTION_ALIAS)){
//		    SearchEsiRadiusForm searchEsiRadiusForm = (SearchEsiRadiusForm) form;		    
//		    searchEsiRadiusForm.setStatus("All"); 
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
