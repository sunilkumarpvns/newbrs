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
import com.elitecore.elitesm.datamanager.core.exceptions.server.DuplicateParameterFoundExcpetion;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
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
import com.elitecore.elitesm.web.driver.radius.forms.CreateRadiusMappingGWAuthDriverForm;

public class CreateRadiusMappingGWAuthDriverAction extends BaseWebAction {
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String MODULE = CreateRadiusMappingGWAuthDriverAction.class.getSimpleName();
	private static final String CREATE = "cRadiusMapGWAuthDriver";
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_DRIVER;

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{

		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		try{
			CreateRadiusMappingGWAuthDriverForm createRadiusMappingGWAuthDriverForm = (CreateRadiusMappingGWAuthDriverForm)form;
			String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
			DriverBLManager driverBLManager = new DriverBLManager();
			DriverInstanceData driverInstanceData = null;
			checkActionPermission(request, ACTION_ALIAS);
			if(createRadiusMappingGWAuthDriverForm.getAction() != null){
				if(createRadiusMappingGWAuthDriverForm.getAction().equals("create")){
					try{
						
						StaffData staffData = (StaffData) getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
						driverInstanceData = new DriverInstanceData();
						driverInstanceData.setCreatedByStaffId(currentUser);        	
						driverInstanceData.setLastModifiedDate(getCurrentTimeStemp());
						driverInstanceData.setLastModifiedByStaffId(currentUser);
						MappingGatewayAuthDriverData mappingGatewayAuthDriverData = new MappingGatewayAuthDriverData();
						
						mappingGatewayAuthDriverData.setGatewayFieldList(getGatewayFieldMapData(request));
						
						convertFromToBean(createRadiusMappingGWAuthDriverForm,mappingGatewayAuthDriverData,driverInstanceData);
						driverBLManager.createMAPGatewayAuthDriver(driverInstanceData, mappingGatewayAuthDriverData, staffData);
						request.setAttribute("responseUrl", "/initSearchDriver");
						ActionMessage message = new ActionMessage("driver.create.success");
						ActionMessages messages = new ActionMessages();
						messages.add("information",message);
						saveMessages(request, messages);
						return mapping.findForward(SUCCESS_FORWARD);

					}catch(DuplicateParameterFoundExcpetion dpf){
						Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
						Logger.logTrace(MODULE,dpf);
						Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(dpf);
						request.setAttribute("errorDetails", errorElements);
						ActionMessage message = new ActionMessage("driver.create.duplicate.failure",driverInstanceData.getName());
						ActionMessages messages = new ActionMessages();
						messages.add("information",message);
						saveErrors(request,messages);
						return mapping.findForward(FAILURE_FORWARD);
					}catch(DataManagerException dme){

						Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
						Logger.logTrace(MODULE,dme);
						Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(dme);
						request.setAttribute("errorDetails", errorElements);
						ActionMessage message = new ActionMessage("driver.create.failure");
						ActionMessages messages = new ActionMessages();
						messages.add("information",message);
						saveErrors(request,messages);
						return mapping.findForward(FAILURE_FORWARD);
					}catch(Exception e) {					
						Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
						Logger.logTrace(MODULE,e);
						Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
						request.setAttribute("errorDetails", errorElements);
						ActionMessage message = new ActionMessage("driver.create.failure");
						ActionMessages messages = new ActionMessages();
						messages.add("information",message);
						saveErrors(request,messages);
						return mapping.findForward(FAILURE_FORWARD);
					}
				}else{
					if(createRadiusMappingGWAuthDriverForm.getDriverRelatedId() == null || createRadiusMappingGWAuthDriverForm.getDriverInstanceName() == null || createRadiusMappingGWAuthDriverForm.getDriverDesp() == null){
						createRadiusMappingGWAuthDriverForm.setDriverInstanceName((String)request.getAttribute("instanceName"));
						createRadiusMappingGWAuthDriverForm.setDriverDesp((String)request.getAttribute("desp"));
						Long driverId =(Long)request.getAttribute("driverId");
						createRadiusMappingGWAuthDriverForm.setDriverRelatedId(driverId.toString());
					}
					
					List<LogicalNameValuePoolData> naveValuePoolList = driverBLManager.getLogicalNameValuePoolList();
					createRadiusMappingGWAuthDriverForm.setLogicalNameList(naveValuePoolList);
					List<ProfileFieldValuePoolData> profileFieldValuePoolList = driverBLManager.getProfileFieldValuePoolList();
					createRadiusMappingGWAuthDriverForm.setProfileFieldList(profileFieldValuePoolList);
					
					List<String> logicalNameMultipleAllowList = driverBLManager.getLogicalValueDriverRelList(Long.valueOf(createRadiusMappingGWAuthDriverForm.getDriverRelatedId()));
					createRadiusMappingGWAuthDriverForm.setLogicalNameMultipleAllowList(logicalNameMultipleAllowList);
				
					request.setAttribute("logicalNameData", driverBLManager.getLogicalNameJson(naveValuePoolList,logicalNameMultipleAllowList));
				
					request.setAttribute("defaultMapping", createRadiusMappingGWAuthDriverForm.getDefaultmapping());
					return mapping.findForward(CREATE); 
				}
			}
		}catch(ActionNotPermitedException e){
			Logger.logError(MODULE,"Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}catch(Exception e){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("driver.create.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE);
	}

	public void convertFromToBean(CreateRadiusMappingGWAuthDriverForm form,MappingGatewayAuthDriverData mappingGatewayAuthDriverData,DriverInstanceData driverInstanceData){
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
