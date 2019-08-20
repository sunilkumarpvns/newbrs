package com.elitecore.elitesm.web.radius.bwlist;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.radius.bwlist.forms.CreateBWListForm;

public class InitCreateBWListAction extends BaseWebAction {
	private static final String MODULE = "INIT CREATE BWLIST ACTION";
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_BLACKLIST_CANDIDATES_ACTION;
	private static final String SUCCESS_FORWARD= "createBWList";

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(checkAccess(request, ACTION_ALIAS)){
		 CreateBWListForm createBWListForm=(CreateBWListForm)form;
		 //createBWListForm.setActiveStatus("1");
		 Logger.logTrace(MODULE,"Entered execute method of "+getClass().getName());
		 
		 return mapping.findForward(SUCCESS_FORWARD);
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
