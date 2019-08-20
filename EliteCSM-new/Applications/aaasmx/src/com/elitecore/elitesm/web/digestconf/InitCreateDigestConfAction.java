package com.elitecore.elitesm.web.digestconf;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.digestconf.forms.CreateDigestConfForm;


public class InitCreateDigestConfAction extends BaseWebAction {
	private static final String SUCCESS_FORWARD = "createDigestConf";
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logTrace(MODULE,"Execute Method Of :"+getClass().getName());
		CreateDigestConfForm createDigestConfForm = (CreateDigestConfForm)form;
		String userName = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getSystemUserName();
		createDigestConfForm.setDescription(getDefaultDescription(userName));
		request.setAttribute("createDigestConfForm", createDigestConfForm);
		return mapping.findForward(SUCCESS_FORWARD);
	}
}
