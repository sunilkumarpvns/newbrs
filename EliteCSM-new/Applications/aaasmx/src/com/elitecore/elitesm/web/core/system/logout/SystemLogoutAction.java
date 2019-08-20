package com.elitecore.elitesm.web.core.system.logout;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.base.BaseSystemAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;


public class SystemLogoutAction extends BaseSystemAction{

	public ActionForward execute( ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws Exception 
	{
		String strReturnPath = "logout";
	    HttpSession session = request.getSession(false);
	    if(session!=null){
	    	
	    	SystemLoginForm sessionform = (SystemLoginForm)session.getAttribute("radiusLoginForm");
	    	if(sessionform!=null)
	    		Logger.logDebug(MODULE,"Logout called for "+ sessionform.getSystemUserName());
	    	session.removeAttribute("userLoggedIn");
	    	session.invalidate();
	    	resetToken(request);
			Logger.logDebug(MODULE,"Session is invalidated.");
	    }else{
	    	Logger.logDebug(MODULE,"Session not found");
	    }
	    return mapping.findForward(strReturnPath);
	}
}
