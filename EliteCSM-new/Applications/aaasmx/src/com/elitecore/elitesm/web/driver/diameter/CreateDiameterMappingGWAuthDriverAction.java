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
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.LogicalNameValuePoolData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.ProfileFieldValuePoolData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.mapgatewaydriver.data.GatewayFieldMapData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.mapgatewaydriver.data.MappingGatewayAuthDriverData;
import com.elitecore.elitesm.util.constants.AccountFieldConstants;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.driver.diameter.forms.DiameterMappingGWAuthDriverForm;


public class CreateDiameterMappingGWAuthDriverAction extends BaseWebAction {
	
	private static final String CREATE = "cDiameterMapGWAuthDriver";
	private static final String SUCCESS_FORWARD = "success";
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_DRIVER;
	
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		try{
			checkActionPermission(request, ACTION_ALIAS);
			DiameterMappingGWAuthDriverForm createDiameterMappingGWAuthDriverForm = (DiameterMappingGWAuthDriverForm) form;
			DriverBLManager driverBLManager = new DriverBLManager();
			
			if("create".equals(createDiameterMappingGWAuthDriverForm.getAction())){
				List<GatewayFieldMapData> gatewayFieldMapDataList = getSelectedGatewayFieldMapDataList(request);
				MappingGatewayAuthDriverData mappingGatewayAuthDriverData = new MappingGatewayAuthDriverData();
				mappingGatewayAuthDriverData.setGatewayFieldList(gatewayFieldMapDataList);
				
				String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
				StaffData staffData = (StaffData) getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				DriverInstanceData driverInstanceData = new DriverInstanceData();
				driverInstanceData.setCreatedByStaffId(currentUser);        	
				driverInstanceData.setLastModifiedDate(getCurrentTimeStemp());
				driverInstanceData.setLastModifiedByStaffId(currentUser);
				
				
				convertFromToBean(createDiameterMappingGWAuthDriverForm,mappingGatewayAuthDriverData,driverInstanceData);
				driverBLManager.createMAPGatewayAuthDriver(driverInstanceData, mappingGatewayAuthDriverData, staffData);
				doAuditing(staffData, ACTION_ALIAS);

				request.setAttribute("responseUrl", "/initSearchDriver");
				ActionMessage message = new ActionMessage("driver.create.success");
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveMessages(request, messages);
				return mapping.findForward(SUCCESS_FORWARD);
			} else {
				if(createDiameterMappingGWAuthDriverForm.getDriverRelatedId() == null || createDiameterMappingGWAuthDriverForm.getDriverInstanceName() == null || createDiameterMappingGWAuthDriverForm.getDriverDesp() == null){
					createDiameterMappingGWAuthDriverForm.setDriverInstanceName((String)request.getAttribute("instanceName"));
					createDiameterMappingGWAuthDriverForm.setDriverDesp((String)request.getAttribute("desp"));
					Long driverId =(Long)request.getAttribute("driverId");
					createDiameterMappingGWAuthDriverForm.setDriverRelatedId(driverId.toString());
				}	
		
				List<LogicalNameValuePoolData> naveValuePoolList = driverBLManager.getLogicalNameValuePoolList();
				createDiameterMappingGWAuthDriverForm.setLogicalNameList(naveValuePoolList);
			
				List<ProfileFieldValuePoolData> profileFieldValuePoolList = driverBLManager.getProfileFieldValuePoolList();
				createDiameterMappingGWAuthDriverForm.setProfileFieldList(profileFieldValuePoolList);
			
				List<GatewayFieldMapData> gatewayFieldMapDataList = getDefaultGatewayFieldMapDataList();
				createDiameterMappingGWAuthDriverForm.setDefaultGatewayFieldMapDataList(gatewayFieldMapDataList);
			
				List<String> logicalNameMultipleAllowList = driverBLManager.getLogicalValueDriverRelList(Long.valueOf(createDiameterMappingGWAuthDriverForm.getDriverRelatedId()));
				createDiameterMappingGWAuthDriverForm.setLogicalNameMultipleAllowList(logicalNameMultipleAllowList);
			
				request.setAttribute("logicalNameData", driverBLManager.getLogicalNameJson(naveValuePoolList));
				request.setAttribute("defaultMapping", createDiameterMappingGWAuthDriverForm.getDefaultmapping());
				return mapping.findForward(CREATE);
			 }
			} catch(ActionNotPermitedException e){
	            Logger.logError(MODULE,"Error :-" + e.getMessage());
	            printPermitedActionAlias(request);
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("general.user.restricted"));
	            saveErrors(request, messages);
	            return mapping.findForward(INVALID_ACCESS_FORWARD);
			} catch (Exception e) {
				Logger.logError(MODULE, "Returning error forward from" +getClass().getName());
				Logger.logTrace(MODULE, e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("driver.create.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE);
	}
	
	private List<GatewayFieldMapData> getDefaultGatewayFieldMapDataList(){
		List<GatewayFieldMapData> gatewayFieldMapDataList = new ArrayList<GatewayFieldMapData>();
		gatewayFieldMapDataList.add(new GatewayFieldMapData("","",new LogicalNameValuePoolData("IMSI", AccountFieldConstants.IMSI),new ProfileFieldValuePoolData("IMSI","IMSI")));
		gatewayFieldMapDataList.add(new GatewayFieldMapData("","",new LogicalNameValuePoolData("MSISDN",AccountFieldConstants.MSISDN),new ProfileFieldValuePoolData("MSISDN","MSISDN")));
		gatewayFieldMapDataList.add(new GatewayFieldMapData("","",new LogicalNameValuePoolData("Customer Status",AccountFieldConstants.CUSTOMER_STATUS),new ProfileFieldValuePoolData("CUSTOMERSTATUS","CustomerStatus")));
		return gatewayFieldMapDataList;
	}
	
	private List<GatewayFieldMapData> getSelectedGatewayFieldMapDataList(HttpServletRequest request){
		List<GatewayFieldMapData> gatewayFieldMapDataList = new ArrayList<GatewayFieldMapData>();
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
	
	private void convertFromToBean(DiameterMappingGWAuthDriverForm form,MappingGatewayAuthDriverData mappingGatewayAuthDriverData,DriverInstanceData driverInstanceData){
		mappingGatewayAuthDriverData.setLocalHostId(form.getLocalHostId());
		mappingGatewayAuthDriverData.setLocalHostIp(form.getLocalHostIp());
		mappingGatewayAuthDriverData.setLocalHostPort(form.getLocalHostPort());
		mappingGatewayAuthDriverData.setRemoteHostId(form.getRemoteHostId());
		mappingGatewayAuthDriverData.setRemoteHostIp(form.getRemoteHostIp());
		mappingGatewayAuthDriverData.setRemoteHostPort(form.getRemoteHostPort());
		mappingGatewayAuthDriverData.setMaxQueryTimeoutCount(form.getMaxQueryTimeoutCount());
		mappingGatewayAuthDriverData.setMapGwConnPoolSize(form.getMapGwConnPoolSize());
		mappingGatewayAuthDriverData.setRequestTimeout(form.getRequestTimeout());
		mappingGatewayAuthDriverData.setStatusCheckDuration(form.getStatusCheckDuration());
		mappingGatewayAuthDriverData.setUserIdentityAttributes(form.getUserIdentityAttributes());
		mappingGatewayAuthDriverData.setSendAuthInfo(form.getSendAuthInfo());
		mappingGatewayAuthDriverData.setNumberOfTriplets(form.getNumberOfTriplets());
		driverInstanceData.setName(form.getDriverInstanceName());
		driverInstanceData.setDescription(form.getDriverDesp());
		driverInstanceData.setDriverTypeId(Long.parseLong(form.getDriverRelatedId()));
		driverInstanceData.setCreateDate(getCurrentTimeStemp());
		driverInstanceData.setLastModifiedDate(getCurrentTimeStemp());		
		driverInstanceData.setStatus("CST01");
	}
	
}
