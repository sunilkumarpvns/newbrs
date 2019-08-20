package com.elitecore.elitesm.web.driver;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.driver.forms.CreateDriverForm;

public class InitCreateDriverAction extends BaseWebAction {
	
	private static final String SUCCESS_FORWARD = "InitCreateDriver";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = "INIT_CREATE_DRIVER_ACTION";
	private static final String MODULE = "INIT_CREATE_DRIVER_ACTION";
	

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
	
		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		String userName = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getSystemUserName();
		
		DriverBLManager driverBLManager = new DriverBLManager();
		
			CreateDriverForm driverForm = (CreateDriverForm)form;
			List serviceList = driverBLManager.getListOfAllServices();		
			driverForm.setServiceList(serviceList);
			driverForm.setDescription(getDefaultDescription(userName));	
		
		return mapping.findForward(SUCCESS_FORWARD);
	}

	
}
