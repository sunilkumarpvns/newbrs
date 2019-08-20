package com.elitecore.elitesm.web.driver.diameter;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.history.HistoryBLManager;
import com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.mapgatewaydriver.data.MappingGatewayAuthDriverData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.driver.diameter.forms.DiameterMappingGWAuthDriverForm;
import com.elitecore.elitesm.web.history.DatabaseHistoryData;

public class ViewDiameterMappingGWAuthDriverHistoryAction  extends BaseWebAction {
	
	private static final String VIEW_FORWARD = "viewDiameterMapGWAuthDriverHistory";
	private static final String ACTION_ALIAS = ConfigConstant.VIEW_DRIVER;

	public ActionForward execute(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());	 
		try{
			checkAccess(request, ACTION_ALIAS);
			
			DiameterMappingGWAuthDriverForm diameterMappingGWAuthDriverForm = (DiameterMappingGWAuthDriverForm) form;
			DriverBLManager driverBLManager = new DriverBLManager();
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			
			DriverInstanceData driverInstanceData = (DriverInstanceData) request.getSession().getAttribute("driverInstance");
			driverInstanceData.setDriverTypeData(driverBLManager.getDriverTypeDataById(driverInstanceData.getDriverTypeId()));
			MappingGatewayAuthDriverData mappingGatewayAuthDriverData = driverBLManager.getMappingGWDataByDriverInstanceId(diameterMappingGWAuthDriverForm.getDriverInstanceId());
			
			HistoryBLManager historyBlManager= new HistoryBLManager();
			String strDriverInstanceId = request.getParameter("driverInstanceId");
			String driverInstanceId = strDriverInstanceId;
			if(driverInstanceId == null){
				driverInstanceId = diameterMappingGWAuthDriverForm.getDriverInstanceId();
			}

			String strAuditUid = request.getParameter("auditUid");
			String strSytemAuditId=request.getParameter("systemAuditId");
			String name=request.getParameter("name");
			
			if(strSytemAuditId != null){
				request.setAttribute("systemAuditId", strSytemAuditId);
			}
			
			if(driverInstanceId != null && Strings.isNullOrBlank(strAuditUid) == false){
				
				staffData.setAuditName(driverInstanceData.getName());
				staffData.setAuditId(driverInstanceData.getAuditUId());
					
				List<DatabaseHistoryData> lstDatabaseDSHistoryDatas=historyBlManager.getHistoryData(strAuditUid);
					
				request.setAttribute("name", name);
				request.setAttribute("lstDatabaseDSHistoryDatas", lstDatabaseDSHistoryDatas);
			}
			
			request.setAttribute("mappingGatewayAuthDriverData", mappingGatewayAuthDriverData);
			request.setAttribute("driverInstanceData", driverInstanceData);
			return mapping.findForward(VIEW_FORWARD);
		}catch(ActionNotPermitedException e){
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
