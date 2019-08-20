package com.elitecore.elitesm.web.core.system.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;

public class ExceptionAction extends BaseWebAction {
	private final static String MODULE = "ExceptionAction";
	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Logger.logDebug(MODULE, "Enter Execute Method");
		boolean showError  = false;
		if(checkAccess(request, ConfigConstant.VIEW_ERROR_DETAIL )){
			showError = true;
		}
		request.setAttribute("showError", showError);
		return mapping.findForward(SUCCESS);
	}
}
