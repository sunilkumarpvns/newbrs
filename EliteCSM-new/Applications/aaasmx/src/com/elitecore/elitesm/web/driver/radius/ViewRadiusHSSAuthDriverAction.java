package com.elitecore.elitesm.web.driver.radius;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.diameter.diameterpeer.DiameterPeerBLManager;
import com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerData;
import com.elitecore.elitesm.datamanager.servermanager.drivers.hssdriver.data.HssAuthDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;

public class ViewRadiusHSSAuthDriverAction extends BaseWebAction{
	private static final String ACTION_ALIAS = ConfigConstant.VIEW_DRIVER;
	private static final String VIEW_FORWARD = "viewRadiusHssAuthDriverInstance";

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response){
		Logger.logInfo(MODULE, "Enter execute Method Of "+getClass().getName());
		try{
			checkAccess(request, ACTION_ALIAS);
			DriverBLManager driverBLManager = new DriverBLManager();  
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			
			DriverInstanceData driverInstanceData = (DriverInstanceData) request.getSession().getAttribute("driverInstance");
			driverInstanceData.setDriverTypeData(driverBLManager.getDriverTypeDataById(driverInstanceData.getDriverTypeId()));
			HssAuthDriverData hssAuthDriverData =driverBLManager.getHSSDriverData(driverInstanceData.getDriverInstanceId());
			
			DiameterPeerBLManager diameterPeerBLManager=new DiameterPeerBLManager();
			List<DiameterPeerData> diameterPeerDataList=diameterPeerBLManager.getDiameterPeerList();
			
			request.setAttribute("driverInstanceData", driverInstanceData);
			request.setAttribute("hssAuthDriverData", hssAuthDriverData);
			request.setAttribute("diameterPeerDataList", diameterPeerDataList);
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
			ActionMessage message = new ActionMessage("driver.update.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			saveErrors(request,messages);
		}
		return mapping.findForward(FAILURE);
	}
}
