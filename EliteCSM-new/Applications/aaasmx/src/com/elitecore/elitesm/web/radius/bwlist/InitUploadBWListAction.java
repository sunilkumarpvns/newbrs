package com.elitecore.elitesm.web.radius.bwlist;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.radius.bwlist.forms.CreateBWListForm;

public class InitUploadBWListAction extends BaseWebAction {
	private static final String MODULE = "INIT CREATE BWLIST ACTION";
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_BLACKLIST_CANDIDATES_ACTION;
	private static final String SUCCESS_FORWARD= "uploadBWList";

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		 CreateBWListForm createBWListForm=(CreateBWListForm)form;
		 
		 Logger.logTrace(MODULE,"Entered execute method of "+getClass().getName());
		 
		 return mapping.findForward(SUCCESS_FORWARD);
	}

}
