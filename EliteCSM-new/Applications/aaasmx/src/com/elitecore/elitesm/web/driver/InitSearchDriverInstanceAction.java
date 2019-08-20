package com.elitecore.elitesm.web.driver;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.driver.forms.SearchDriverInstanceForm;

public class InitSearchDriverInstanceAction  extends BaseWebAction{
	

	private static final String SUCCESS_FORWARD = "SearchDriverInstance";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.SEARCH_DRIVER;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());		
		List driversList = new ArrayList();
		DriverBLManager driverBLManager = new DriverBLManager();
		SearchDriverInstanceForm driverInstanceForm = (SearchDriverInstanceForm)form;
		IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
		
		List serviceList = driverBLManager.getListOfAllServices();		
		
		driverInstanceForm.setAction("nolist");
		driverInstanceForm.setServiceList(serviceList);
		request.getSession().setAttribute("driverList",driversList);
		request.getSession().setAttribute("searchForm",driverInstanceForm);
		doAuditing(staffData, ACTION_ALIAS);
		return mapping.findForward(SUCCESS_FORWARD);
	}

}
