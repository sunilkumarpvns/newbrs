package com.elitecore.ssp.web.logout;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.elitecore.ssp.subscriber.SubscriberProfile;
import com.elitecore.ssp.util.exception.DataManagerException;
import com.elitecore.ssp.util.logger.Logger;
import com.elitecore.ssp.web.core.base.BaseWebAction;


public class LogoutAction extends BaseWebAction {
          
	

	private static final String MODULE = LogoutAction.class.getSimpleName();
	private static final String FAILURE = "failure";
	private static final String LOGOUT="logout";

	public ActionForward execute(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws DataManagerException {
		try{
	    HttpSession session = request.getSession(false);
	    if(session!=null){	    	
	    	SubscriberProfile sessionform = (SubscriberProfile)session.getAttribute("sspLoginForm");
	    	if(sessionform!=null)
	        Logger.logDebug(MODULE,"Logout called for "+ sessionform.getUserName());
	    	session.invalidate();
		    Logger.logDebug(MODULE,"Session is invalidated.");
	    }else{
	    	//Logger.logDebug(MODULE,"Session not found");
	    }
		}catch(Exception exp){
		  Logger.logTrace(MODULE,exp);	
		}
	    return mapping.findForward(LOGOUT);
	}

}
