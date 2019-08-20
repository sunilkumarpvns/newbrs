package com.elitecore.elitesm.web.driver.radius;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.LogicalNameValuePoolData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.ProfileFieldValuePoolData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.mapgatewaydriver.data.GatewayFieldMapData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.mapgatewaydriver.data.MappingGatewayAuthDriverData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.driver.radius.forms.UpdateRadiusMappingGWAuthDriverForm;

public class UpdateRadiusMappingGWAuthDriverAction extends BaseWebAction {
	private static final String FAILURE_FORWARD = "failure";
	private static final String INIT_UPDATE_FORWARD = "updateRadiusMappingGWAuthDriver";
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
			String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			List<LogicalNameValuePoolData> logicalNameList = driverBlManager.getLogicalNameValuePoolList();
			if( "update".equalsIgnoreCase(updateRadiusMappingGWAuthDriverForm.getAction())){
				MappingGatewayAuthDriverData mapGatewayData = new MappingGatewayAuthDriverData();
				DriverInstanceData driverInstdata = new DriverInstanceData();
				
				mapGatewayData.setGatewayFieldList(getGatewayFieldMapData(request));
				convertBeanToForm(updateRadiusMappingGWAuthDriverForm,mapGatewayData,driverInstdata);
				driverInstdata.setLastModifiedByStaffId(currentUser);
				
				staffData.setAuditId(driverInstanceData.getAuditUId());
				staffData.setAuditName(driverInstanceData.getName());
				
				driverBlManager.updateMapGWAuthDriverDataById(driverInstdata,mapGatewayData,staffData);
				
				request.setAttribute("responseUrl", "/viewDriverInstance.do?driverInstanceId=" + updateRadiusMappingGWAuthDriverForm.getDriverInstanceId());
				ActionMessage message = new ActionMessage("driver.update.success");
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveMessages(request, messages);
				return mapping.findForward(SUCCESS);
			}else{
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
				
				request.setAttribute("logicalNameData", driverBlManager.getLogicalNameJson(logicalNameList,logicalNameMultipleAllowList));
				request.getSession().setAttribute("mapGWAuthFieldMapList", mappingGatewayAuthDriverData.getGatewayFieldList());
				request.getSession().setAttribute("driverInstanceData",driverInstanceData);
				request.getSession().setAttribute("driverInstanceData",driverInstanceData);
				request.getSession().setAttribute("mappingGatewayAuthDriverData",mappingGatewayAuthDriverData);
				
				return mapping.findForward(INIT_UPDATE_FORWARD);
			}
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


	private void convertBeanToForm(UpdateRadiusMappingGWAuthDriverForm updateRadiusMappingGWAuthDriverForm,MappingGatewayAuthDriverData driverData,DriverInstanceData driverInstdata) {
		driverData.setLocalHostId(updateRadiusMappingGWAuthDriverForm.getLocalHostId());
		driverData.setLocalHostIp(updateRadiusMappingGWAuthDriverForm.getLocalHostIp());
		driverData.setLocalHostPort(updateRadiusMappingGWAuthDriverForm.getLocalHostPort());
		driverData.setRemoteHostId(updateRadiusMappingGWAuthDriverForm.getRemoteHostId());
		driverData.setRemoteHostIp(updateRadiusMappingGWAuthDriverForm.getRemoteHostIp());
		driverData.setRemoteHostPort(updateRadiusMappingGWAuthDriverForm.getRemoteHostPort());
		driverData.setDriverInstanceId(updateRadiusMappingGWAuthDriverForm.getDriverInstanceId());
		driverData.setMaxQueryTimeoutCount(updateRadiusMappingGWAuthDriverForm.getMaxQueryTimeoutCount());
		driverData.setMapGwConnPoolSize(updateRadiusMappingGWAuthDriverForm.getMapGwConnPoolSize());
		driverData.setRequestTimeout(updateRadiusMappingGWAuthDriverForm.getRequestTimeout());
		driverData.setStatusCheckDuration(updateRadiusMappingGWAuthDriverForm.getStatusCheckDuration());
		driverData.setUserIdentityAttributes(updateRadiusMappingGWAuthDriverForm.getUserIdentityAttributes());
		driverData.setSendAuthInfo(updateRadiusMappingGWAuthDriverForm.getSendAuthInfo());
		driverData.setNumberOfTriplets(updateRadiusMappingGWAuthDriverForm.getNumberOfTriplets());
		// driver related 
		driverInstdata.setName(updateRadiusMappingGWAuthDriverForm.getDriverInstanceName());
		driverInstdata.setDescription(updateRadiusMappingGWAuthDriverForm.getDriverDesp());
		driverInstdata.setLastModifiedDate(getCurrentTimeStemp());
		driverInstdata.setDriverInstanceId(updateRadiusMappingGWAuthDriverForm.getDriverInstanceId());
		driverInstdata.setAuditUId(updateRadiusMappingGWAuthDriverForm.getAuditUId());
	}
	
	private List<GatewayFieldMapData> getGatewayFieldMapData(HttpServletRequest request){
		List<GatewayFieldMapData> gatewayFieldMapDataList = new ArrayList<GatewayFieldMapData>();
		String[] logicalNames = request.getParameterValues("logicalnmVal");
		String[] profileField = request.getParameterValues("profileField");
		String defaultValues[] = request.getParameterValues("defaultValue");
		String valueMappings[] = request.getParameterValues("valueMapping");
		if(logicalNames != null && profileField!=null && defaultValues!=null && valueMappings!=null){
			for(int index=0; index<logicalNames.length; index++){
				GatewayFieldMapData gatewayFieldMapData = new GatewayFieldMapData();
				gatewayFieldMapData.setLogicalName(logicalNames[index]);
				gatewayFieldMapData.setProfileField(profileField[index]);
				gatewayFieldMapData.setDefaultValue(defaultValues[index]);
				gatewayFieldMapData.setValueMapping(valueMappings[index]);
				gatewayFieldMapDataList.add(gatewayFieldMapData);
			}
		}
		return gatewayFieldMapDataList;
	}
}
