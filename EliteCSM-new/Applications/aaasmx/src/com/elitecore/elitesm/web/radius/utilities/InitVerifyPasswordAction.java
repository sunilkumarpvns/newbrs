package com.elitecore.elitesm.web.radius.utilities;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.radius.radtest.forms.CreateRadiusTestForm;
import com.elitecore.elitesm.web.radius.utilities.forms.VerifyPasswordForm;

public class InitVerifyPasswordAction extends BaseWebAction{
	private static final String SUCCESS_FORWARD = "success";               
	private static final String FAILURE_FORWARD = "failure";               
	private static final String MODULE = "InitVerifyPasswordAction";
	private static final String VIEW_FORWARD = "verifyPassword";
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{ 
	    Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName());
        VerifyPasswordForm verifyPasswordForm = (VerifyPasswordForm)form;

        return mapping.findForward(VIEW_FORWARD); 
	}
}
