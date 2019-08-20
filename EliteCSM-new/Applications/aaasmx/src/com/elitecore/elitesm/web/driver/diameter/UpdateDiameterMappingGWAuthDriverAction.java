package com.elitecore.elitesm.web.driver.diameter;

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
import com.elitecore.elitesm.web.driver.diameter.forms.DiameterMappingGWAuthDriverForm;

public class UpdateDiameterMappingGWAuthDriverAction extends BaseWebAction{
	private static final String UPDATE_FORWARD = "updateDiameterMapGWAuthDriverInstance";
	private static final String SUCCESS_FORWARD = "success";
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_DRIVER;

	public ActionForward execute(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());	 
		try{
			checkActionPermission(request, ACTION_ALIAS);
			DiameterMappingGWAuthDriverForm diameterMappingGWAuthDriverForm = (DiameterMappingGWAuthDriverForm) form;
			DriverBLManager driverBLManager = new DriverBLManager(); 
			List<LogicalNameValuePoolData> logicalNameList = driverBLManager.getLogicalNameValuePoolList();
			if("Update".equals(diameterMappingGWAuthDriverForm.getAction())){
				
				List<GatewayFieldMapData> gatewayFieldMapDataSet = getSelectedGatewayFieldMapDataList(request);
				MappingGatewayAuthDriverData mappingGatewayAuthDriverData = new MappingGatewayAuthDriverData();
				mappingGatewayAuthDriverData.setGatewayFieldList(gatewayFieldMapDataSet);
				
				String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				DriverInstanceData driverInstanceData = new DriverInstanceData();
				driverInstanceData.setLastModifiedDate(getCurrentTimeStemp());
				driverInstanceData.setLastModifiedByStaffId(currentUser);
				
				convertFormToBean(diameterMappingGWAuthDriverForm,mappingGatewayAuthDriverData,driverInstanceData);
				
				staffData.setAuditId(driverInstanceData.getAuditUId());
				staffData.setAuditName(driverInstanceData.getName());
				
				driverBLManager.updateMapGWAuthDriverDataById(driverInstanceData,mappingGatewayAuthDriverData,staffData);
				
				request.setAttribute("responseUrl", "/viewDriverInstance.do?driverInstanceId=" + diameterMappingGWAuthDriverForm.getDriverInstanceId());
				ActionMessage message = new ActionMessage("driver.update.success");
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveMessages(request, messages);
				return mapping.findForward(SUCCESS_FORWARD);
			} else {
				DriverInstanceData driverInstanceData = (DriverInstanceData) request.getSession().getAttribute("driverInstance");
				driverInstanceData.setDriverTypeData(driverBLManager.getDriverTypeDataById(driverInstanceData.getDriverTypeId()));
				MappingGatewayAuthDriverData mappingGatewayAuthDriverData = driverBLManager.getMappingGWDataByDriverInstanceId(diameterMappingGWAuthDriverForm.getDriverInstanceId());
				
				convertBeanToForm(diameterMappingGWAuthDriverForm, driverInstanceData, mappingGatewayAuthDriverData);
				request.setAttribute("mappingGatewayAuthDriverData", mappingGatewayAuthDriverData);
				request.setAttribute("driverInstanceData", driverInstanceData);
				
				List<LogicalNameValuePoolData> naveValuePoolList = driverBLManager.getLogicalNameValuePoolList();
				diameterMappingGWAuthDriverForm.setLogicalNameList(naveValuePoolList);
			
				List<ProfileFieldValuePoolData> profileFieldValuePoolList = driverBLManager.getProfileFieldValuePoolList();
				diameterMappingGWAuthDriverForm.setProfileFieldList(profileFieldValuePoolList);
			
				List<String> logicalNameMultipleAllowList = driverBLManager.getLogicalValueDriverRelList(driverInstanceData.getDriverTypeId());
				diameterMappingGWAuthDriverForm.setLogicalNameMultipleAllowList(logicalNameMultipleAllowList);
				
				request.setAttribute("logicalNameData", driverBLManager.getLogicalNameJson(logicalNameList));
				request.getSession().setAttribute("mapGWAuthFieldMapList", mappingGatewayAuthDriverData.getGatewayFieldList());
				return mapping.findForward(UPDATE_FORWARD);
			}
			
		}catch(ActionNotPermitedException e){
            Logger.logError(MODULE,"Error :-" + e.getMessage());
            printPermitedActionAlias(request);
            ActionMessages messages = new ActionMessages();
            messages.add("information", new ActionMessage("general.user.restricted"));
            saveErrors(request, messages);
            return mapping.findForward(INVALID_ACCESS_FORWARD);
		} catch (Exception e) {
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
	
	private DiameterMappingGWAuthDriverForm convertBeanToForm(DiameterMappingGWAuthDriverForm diameterMappingGWAuthDriverForm,DriverInstanceData driverInstanceData,MappingGatewayAuthDriverData mappingGatewayAuthDriverData){
		diameterMappingGWAuthDriverForm.setLocalHostId(mappingGatewayAuthDriverData.getLocalHostId());
		diameterMappingGWAuthDriverForm.setLocalHostIp(mappingGatewayAuthDriverData.getLocalHostIp());
		diameterMappingGWAuthDriverForm.setLocalHostPort(mappingGatewayAuthDriverData.getLocalHostPort());
		diameterMappingGWAuthDriverForm.setRemoteHostId(mappingGatewayAuthDriverData.getRemoteHostId());
		diameterMappingGWAuthDriverForm.setRemoteHostIp(mappingGatewayAuthDriverData.getRemoteHostIp());
		diameterMappingGWAuthDriverForm.setRemoteHostPort(mappingGatewayAuthDriverData.getRemoteHostPort());
		diameterMappingGWAuthDriverForm.setMaxQueryTimeoutCount(mappingGatewayAuthDriverData.getMaxQueryTimeoutCount());
		diameterMappingGWAuthDriverForm.setMapGwConnPoolSize(mappingGatewayAuthDriverData.getMapGwConnPoolSize());
		diameterMappingGWAuthDriverForm.setRequestTimeout(mappingGatewayAuthDriverData.getRequestTimeout());
		diameterMappingGWAuthDriverForm.setStatusCheckDuration(mappingGatewayAuthDriverData.getStatusCheckDuration());
		diameterMappingGWAuthDriverForm.setSendAuthInfo(mappingGatewayAuthDriverData.getSendAuthInfo());
		diameterMappingGWAuthDriverForm.setNumberOfTriplets(mappingGatewayAuthDriverData.getNumberOfTriplets());
		diameterMappingGWAuthDriverForm.setUserIdentityAttributes(mappingGatewayAuthDriverData.getUserIdentityAttributes());
		diameterMappingGWAuthDriverForm.setDriverInstanceName(driverInstanceData.getName());
		diameterMappingGWAuthDriverForm.setDriverDesp(driverInstanceData.getDescription());
		diameterMappingGWAuthDriverForm.setDriverRelatedId(String.valueOf(driverInstanceData.getDriverInstanceId()));
		diameterMappingGWAuthDriverForm.setAuditUId(driverInstanceData.getAuditUId());
		return diameterMappingGWAuthDriverForm;
	}
	
	private List<GatewayFieldMapData> getSelectedGatewayFieldMapDataList(HttpServletRequest request){
		List<GatewayFieldMapData> gatewayFieldMapDataList = new  ArrayList<GatewayFieldMapData>();
		String[] logicalName = request.getParameterValues("logicalnmVal");
		String[] profileField = request.getParameterValues("profileField");
		String defaultValues[] = request.getParameterValues("defaultValue");
		String valueMappings[] = request.getParameterValues("valueMapping");
		if(logicalName != null) {
			for(int index=0 ;index<logicalName.length;index++){
				GatewayFieldMapData gatewayFieldMapData = new GatewayFieldMapData();
				gatewayFieldMapData.setLogicalName(logicalName[index]);
				gatewayFieldMapData.setProfileField(profileField[index]);
				gatewayFieldMapData.setDefaultValue(defaultValues[index]);
				gatewayFieldMapData.setValueMapping(valueMappings[index]);
				gatewayFieldMapDataList.add(gatewayFieldMapData);
			}
		}
		return gatewayFieldMapDataList;
	}
	
	private void convertFormToBean(DiameterMappingGWAuthDriverForm diameterMappingGWAuthDriverForm,MappingGatewayAuthDriverData driverData,DriverInstanceData driverInstdata) {
		driverData.setLocalHostId(diameterMappingGWAuthDriverForm.getLocalHostId());
		driverData.setLocalHostIp(diameterMappingGWAuthDriverForm.getLocalHostIp());
		driverData.setLocalHostPort(diameterMappingGWAuthDriverForm.getLocalHostPort());
		driverData.setRemoteHostId(diameterMappingGWAuthDriverForm.getRemoteHostId());
		driverData.setRemoteHostIp(diameterMappingGWAuthDriverForm.getRemoteHostIp());
		driverData.setRemoteHostPort(diameterMappingGWAuthDriverForm.getRemoteHostPort());
		driverData.setDriverInstanceId(diameterMappingGWAuthDriverForm.getDriverInstanceId());
		driverData.setMaxQueryTimeoutCount(diameterMappingGWAuthDriverForm.getMaxQueryTimeoutCount());
		driverData.setMapGwConnPoolSize(diameterMappingGWAuthDriverForm.getMapGwConnPoolSize());
		driverData.setRequestTimeout(diameterMappingGWAuthDriverForm.getRequestTimeout());
		driverData.setStatusCheckDuration(diameterMappingGWAuthDriverForm.getStatusCheckDuration());
		driverData.setUserIdentityAttributes(diameterMappingGWAuthDriverForm.getUserIdentityAttributes());
		driverData.setSendAuthInfo(diameterMappingGWAuthDriverForm.getSendAuthInfo());
		driverData.setNumberOfTriplets(diameterMappingGWAuthDriverForm.getNumberOfTriplets());
		// driver related 
		driverInstdata.setName(diameterMappingGWAuthDriverForm.getDriverInstanceName());
		driverInstdata.setDescription(diameterMappingGWAuthDriverForm.getDriverDesp());
		driverInstdata.setLastModifiedDate(getCurrentTimeStemp());
		driverInstdata.setDriverInstanceId(diameterMappingGWAuthDriverForm.getDriverInstanceId());
		driverInstdata.setAuditUId(diameterMappingGWAuthDriverForm.getAuditUId());
	}
}
