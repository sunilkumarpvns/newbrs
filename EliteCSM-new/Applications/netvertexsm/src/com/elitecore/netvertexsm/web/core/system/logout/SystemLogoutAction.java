package com.elitecore.netvertexsm.web.core.system.logout;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.elitecore.netvertexsm.blmanager.core.system.staff.StaffBLManager;
import com.elitecore.netvertexsm.util.EliteUtility;
import com.elitecore.netvertexsm.util.constants.ServermgrConstant;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.system.base.BaseSystemAction;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;


public class SystemLogoutAction extends BaseSystemAction{

	public ActionForward execute( ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws Exception 
	{
		Logger.logTrace(MODULE, "Enter execute method of :"+ getClass().getName());
		String strReturnPath = "logout";
	    HttpSession session = request.getSession(false);
	    StaffBLManager staffBLManager =  new StaffBLManager();
	    if(session!=null){
	    	
	    	ServletContext smContext = ((HttpServletRequest) request).getSession().getServletContext();  
	    	smContext.removeAttribute(ServermgrConstant.SSO_USERNAME);
			smContext.removeAttribute(ServermgrConstant.SSO_PASSWORD);		
			smContext.removeAttribute(ServermgrConstant.PD_SSO_URL);
			Logger.logDebug(MODULE, "SSO attributes cleared from sm context");
			
	    	SystemLoginForm sessionform = (SystemLoginForm)session.getAttribute("radiusLoginForm");
	    	staffBLManager.updateLogoutInfo(sessionform.getUserId());
	    	EliteUtility.cleanMDC();
	    	if(sessionform!=null)
	    		Logger.logDebug(MODULE,"Logout called for "+ sessionform.getUserName());
	    	session.invalidate();
			Logger.logDebug(MODULE,"Session is invalidated.");
	    }else{
	    	Logger.logDebug(MODULE,"Session not found");
	    }
	    return mapping.findForward(strReturnPath);
	}
}
