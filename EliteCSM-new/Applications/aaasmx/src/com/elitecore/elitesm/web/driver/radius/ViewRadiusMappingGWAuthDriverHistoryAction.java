package com.elitecore.elitesm.web.driver.radius;

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
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.LogicalNameValuePoolData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.ProfileFieldValuePoolData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.mapgatewaydriver.data.MappingGatewayAuthDriverData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.driver.radius.forms.UpdateRadiusMappingGWAuthDriverForm;
import com.elitecore.elitesm.web.history.DatabaseHistoryData;

public class ViewRadiusMappingGWAuthDriverHistoryAction extends BaseWebAction {
	private static final String FAILURE_FORWARD = "failure";
	private static final String VIEW_HISTORY_FORWARD = "viewRadiusMappingGWAuthDriverHistory";
	private static final String UPDATE_ACTION_ALIAS = ConfigConstant.UPDATE_DRIVER;
	private static final String VIEW_ACTION_ALIAS = ConfigConstant.VIEW_DRIVER;

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{

		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());		
		try{

			UpdateRadiusMappingGWAuthDriverForm updateRadiusMappingGWAuthDriverForm = (UpdateRadiusMappingGWAuthDriverForm)form;
			if("view".equals(updateRadiusMappingGWAuthDriverForm.getAction())) {
				checkActionPermission(request, VIEW_ACTION_ALIAS);
			} else {
				checkActionPermission(request, UPDATE_ACTION_ALIAS);
			}

			DriverBLManager driverBlManager = new DriverBLManager();
			MappingGatewayAuthDriverData mappingGatewayAuthDriverData = driverBlManager.getMappingGWDataByDriverInstanceId(updateRadiusMappingGWAuthDriverForm.getDriverInstanceId());
			DriverInstanceData driverInstanceData = driverBlManager.getDriverInstanceByDriverInstanceId(updateRadiusMappingGWAuthDriverForm.getDriverInstanceId());
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			List<LogicalNameValuePoolData> logicalNameList = driverBlManager.getLogicalNameValuePoolList();
		
			updateRadiusMappingGWAuthDriverForm.setLocalHostId(mappingGatewayAuthDriverData.getLocalHostId());
			updateRadiusMappingGWAuthDriverForm.setLocalHostIp(mappingGatewayAuthDriverData.getLocalHostIp());
			updateRadiusMappingGWAuthDriverForm.setLocalHostPort(mappingGatewayAuthDriverData.getLocalHostPort());
			updateRadiusMappingGWAuthDriverForm.setRemoteHostId(mappingGatewayAuthDriverData.getRemoteHostId());
			updateRadiusMappingGWAuthDriverForm.setRemoteHostIp(mappingGatewayAuthDriverData.getRemoteHostIp());
			updateRadiusMappingGWAuthDriverForm.setRemoteHostPort(mappingGatewayAuthDriverData.getRemoteHostPort());
			updateRadiusMappingGWAuthDriverForm.setMaxQueryTimeoutCount(mappingGatewayAuthDriverData.getMaxQueryTimeoutCount());
			updateRadiusMappingGWAuthDriverForm.setMapGwConnPoolSize(mappingGatewayAuthDriverData.getMapGwConnPoolSize());
			updateRadiusMappingGWAuthDriverForm.setRequestTimeout(mappingGatewayAuthDriverData.getRequestTimeout());
			updateRadiusMappingGWAuthDriverForm.setStatusCheckDuration(mappingGatewayAuthDriverData.getStatusCheckDuration());
			updateRadiusMappingGWAuthDriverForm.setUserIdentityAttributes(mappingGatewayAuthDriverData.getUserIdentityAttributes());
			updateRadiusMappingGWAuthDriverForm.setSendAuthInfo(mappingGatewayAuthDriverData.getSendAuthInfo());
			updateRadiusMappingGWAuthDriverForm.setNumberOfTriplets(mappingGatewayAuthDriverData.getNumberOfTriplets());
			updateRadiusMappingGWAuthDriverForm.setDriverInstanceName(driverInstanceData.getName());
			updateRadiusMappingGWAuthDriverForm.setDriverDesp(driverInstanceData.getDescription());
			updateRadiusMappingGWAuthDriverForm.setDriverRelatedId(String.valueOf(driverInstanceData.getDriverInstanceId()));
			updateRadiusMappingGWAuthDriverForm.setLogicalNameList(logicalNameList);
			updateRadiusMappingGWAuthDriverForm.setAuditUId(driverInstanceData.getAuditUId());
			
			List<String> logicalNameMultipleAllowList = driverBlManager.getLogicalValueDriverRelList(driverInstanceData.getDriverTypeId());
			updateRadiusMappingGWAuthDriverForm.setLogicalNameMultipleAllowList(logicalNameMultipleAllowList);

			List<ProfileFieldValuePoolData> profileFieldValuePoolList = driverBlManager.getProfileFieldValuePoolList();
			updateRadiusMappingGWAuthDriverForm.setProfileFieldList(profileFieldValuePoolList);
			
			HistoryBLManager historyBlManager= new HistoryBLManager();
			String strDriverInstanceId = request.getParameter("driverInstanceId");
			String driverInstanceId = strDriverInstanceId;
			if(driverInstanceId == null){
				driverInstanceId = updateRadiusMappingGWAuthDriverForm.getDriverInstanceId();
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
				
			request.setAttribute("logicalNameData", driverBlManager.getLogicalNameJson(logicalNameList,logicalNameMultipleAllowList));
			request.getSession().setAttribute("mapGWAuthFieldMapList", mappingGatewayAuthDriverData.getGatewayFieldList());
			request.getSession().setAttribute("driverInstanceData",driverInstanceData);
			request.getSession().setAttribute("driverInstanceData",driverInstanceData);
			request.getSession().setAttribute("mappingGatewayAuthDriverData",mappingGatewayAuthDriverData);
			return mapping.findForward(VIEW_HISTORY_FORWARD);
		}catch(ActionNotPermitedException e){
			Logger.logError(MODULE,"Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}catch(DataManagerException e){

			Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
			Logger.logTrace(MODULE,e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("driver.update.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			saveErrors(request,messages);
			return mapping.findForward(FAILURE_FORWARD);
		}catch(Exception dme){

			Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
			Logger.logTrace(MODULE,dme);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(dme);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("driver.update.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			saveErrors(request,messages);
			return mapping.findForward(FAILURE_FORWARD);
		}		
	}
}
