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
import com.elitecore.elitesm.datamanager.servermgr.drivers.chargingdriver.data.CrestelChargingDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.driver.radius.forms.CrestelOCSv2DriverForm;

public class ViewCrestelOCSv2DriverAction extends BaseWebAction {
	private static final String ACTION_ALIAS = ConfigConstant.VIEW_DRIVER;
	private static final String VIEW_FORWARD = "viewCrestelOCSv2DriverInstance";
	
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response){
		Logger.logInfo(MODULE, "Enter execute method of "+getClass().getName());
		try{
			checkAccess(request, ACTION_ALIAS);
			CrestelOCSv2DriverForm crestelOCSv2DriverForm = (CrestelOCSv2DriverForm) form;
			DriverBLManager driverBlManager = new DriverBLManager();
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			
			CrestelChargingDriverData crestelChargingDriverData = driverBlManager.getCrestelChargingDriverData(crestelOCSv2DriverForm.getDriverInstanceId());
			DriverInstanceData driverInstanceData = driverBlManager.getDriverInstanceByDriverInstanceId(crestelOCSv2DriverForm.getDriverInstanceId());
			driverInstanceData.setDriverTypeData(driverBlManager.getDriverTypeDataById(driverInstanceData.getDriverTypeId()));
			
			request.setAttribute("crestelChargingDriverData", crestelChargingDriverData);
			request.setAttribute("driverInstanceData", driverInstanceData);
			doAuditing(staffData, ACTION_ALIAS);
			return mapping.findForward(VIEW_FORWARD);
		}catch (ActionNotPermitedException e) {
			Logger.logError(MODULE,"Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}catch (Exception e) {
			Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
			Logger.logTrace(MODULE,e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("driver.create.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			saveErrors(request,messages);
		}
		return mapping.findForward(FAILURE);
	}
	
}
