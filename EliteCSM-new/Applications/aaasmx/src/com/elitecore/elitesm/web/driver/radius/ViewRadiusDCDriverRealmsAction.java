package com.elitecore.elitesm.web.driver.radius;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.dcdriver.data.DiameterChargingDriverData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.driver.radius.forms.ViewRadiusDCDriverRealmsForm;
import com.elitecore.elitesm.web.servermgr.transmapconf.ViewCrestelRatingTransMapConfAction;

public class ViewRadiusDCDriverRealmsAction extends BaseWebAction {
	
	private static final String MODULE = ViewCrestelRatingTransMapConfAction.class.getSimpleName();
	private static final String ACTION_ALIAS = ConfigConstant.VIEW_DRIVER;
	private static final String VIEW_FORWARD = "viewRadiusDCDriverRealms";
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception {
		ViewRadiusDCDriverRealmsForm viewRadiusDCDriverRealmsForm = (ViewRadiusDCDriverRealmsForm)form;
		try{
			checkActionPermission(request, ACTION_ALIAS);
			Logger.logTrace(MODULE,"Execute Method Of :"+getClass().getName());
			String strDCDriverId = request.getParameter("dcDriverId");
			String strDriverInstanceId = request.getParameter("driverInstanceId");
			String dcDriverId = null;
			String driverInstanceId = null;
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			
			if(strDCDriverId != null){
				dcDriverId = strDCDriverId;
			}
			
			if(strDriverInstanceId != null){
				driverInstanceId = strDriverInstanceId;
			}
			
			DriverBLManager blManager = new DriverBLManager();
			DriverInstanceData driverInstanceData = blManager.getDriverInstanceByDriverInstanceId(driverInstanceId);
			viewRadiusDCDriverRealmsForm.setDriverInstanceName(driverInstanceData.getName());
			viewRadiusDCDriverRealmsForm.setDriverDesp(driverInstanceData.getDescription());
			DiameterChargingDriverData diameterChargingDriverData = blManager.getDiameterChargingDataByDriverInstanceId(driverInstanceId);
			request.setAttribute("diameterChargingDriverData",diameterChargingDriverData);
			doAuditing(staffData, ACTION_ALIAS);
			return mapping.findForward(VIEW_FORWARD);
		}catch(ActionNotPermitedException e){
			Logger.logError(MODULE, "Restricted to do action.");
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);	
		}catch(Exception e){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("transmapconf.update.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
	   return mapping.findForward(FAILURE);
	}
	
}
