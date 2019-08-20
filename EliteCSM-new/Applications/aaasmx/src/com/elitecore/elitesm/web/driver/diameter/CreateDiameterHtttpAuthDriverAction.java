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
import com.elitecore.elitesm.datamanager.servermgr.drivers.httpdriver.data.HttpAuthDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.httpdriver.data.HttpAuthDriverFieldMapData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.driver.CreateDriverConfig;
import com.elitecore.elitesm.web.driver.diameter.forms.DiameterHttpAuthDriverForm;

public class CreateDiameterHtttpAuthDriverAction  extends BaseWebAction{

	private final static String CREATE = "cDiameterHttpAuthDriver";
	private static final String SUCCESS_FORWARD = "success";
	private final static String ACTION_ALIAS = ConfigConstant.CREATE_DRIVER;
	
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		try{
			checkActionPermission(request, ACTION_ALIAS);
			DiameterHttpAuthDriverForm createDiameterHttpAuthDriverForm = (DiameterHttpAuthDriverForm) form ;
			String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
			DriverBLManager driverBLManager = new DriverBLManager();
			
			if("create".equals(createDiameterHttpAuthDriverForm.getAction())){
				DriverInstanceData driverInstanceData = new DriverInstanceData();
				CreateDriverConfig driverConfig = new CreateDriverConfig();
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				
				HttpAuthDriverData httpAuthDriverData = new HttpAuthDriverData();
				httpAuthDriverData.setHttpFieldMapList(getSelectedHttpAuthDriverFieldMapDataList(request));
				
				convertFromFormToData(createDiameterHttpAuthDriverForm, httpAuthDriverData, driverInstanceData);
				
				driverInstanceData.setCreatedByStaffId(currentUser);        	
				driverInstanceData.setLastModifiedDate(getCurrentTimeStemp());
				driverInstanceData.setLastModifiedByStaffId(currentUser);
				
				driverConfig.setHttpAuthDriverData(httpAuthDriverData);
				driverConfig.setDriverInstanceData(driverInstanceData);

				driverBLManager.createHttpAuthDriver(driverConfig,staffData);
				Logger.logDebug(MODULE, "httpAuthDriverData :"+httpAuthDriverData.getHttpAuthDriverId());

				request.setAttribute("responseUrl", "/initSearchDriver");
				ActionMessage message = new ActionMessage("driver.create.success");
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveMessages(request, messages);
				return mapping.findForward(SUCCESS_FORWARD);

			}else {
				if(createDiameterHttpAuthDriverForm.getDriverRelatedId() == null || createDiameterHttpAuthDriverForm.getDriverInstanceName() == null || createDiameterHttpAuthDriverForm.getDriverInstanceDesc() == null){
					createDiameterHttpAuthDriverForm.setDriverInstanceName((String)request.getAttribute("instanceName"));
					createDiameterHttpAuthDriverForm.setDriverInstanceDesc((String)request.getAttribute("desp"));
					Long driverId =(Long)request.getAttribute("driverId");
					createDiameterHttpAuthDriverForm.setDriverRelatedId(driverId.toString()); 
				}	
				
				List<LogicalNameValuePoolData> nameValuePoolList = driverBLManager.getLogicalNameValuePoolList();
				createDiameterHttpAuthDriverForm.setLogicalNameList(nameValuePoolList);
				
				List<String> logicalNameMultipleAllowList = driverBLManager.getLogicalValueDriverRelList(Long.valueOf(createDiameterHttpAuthDriverForm.getDriverRelatedId()));
				createDiameterHttpAuthDriverForm.setLogicalNameMultipleAllowList(logicalNameMultipleAllowList);
				
				request.setAttribute("logicalNameData", driverBLManager.getLogicalNameJson(nameValuePoolList,logicalNameMultipleAllowList));
				request.setAttribute("defaultMapping", createDiameterHttpAuthDriverForm.getDefaultmapping());
				return  mapping.findForward(CREATE);
			}
		}catch(ActionNotPermitedException e){
            Logger.logError(MODULE,"Error :-" + e.getMessage());
            printPermitedActionAlias(request);
            ActionMessages messages = new ActionMessages();
            messages.add("information", new ActionMessage("general.user.restricted"));
            saveErrors(request, messages);
            return mapping.findForward(INVALID_ACCESS_FORWARD);
		}catch(Exception e){
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
	
	private List<HttpAuthDriverFieldMapData> getSelectedHttpAuthDriverFieldMapDataList(HttpServletRequest request){
		List<HttpAuthDriverFieldMapData>  httpAuthDriverFieldMapDataList = new ArrayList<HttpAuthDriverFieldMapData>();
		String[] logicalName = request.getParameterValues("logicalnmVal");
		String[] responseParamIndex = request.getParameterValues("responseParamIndex");
		String defaultValues[] = request.getParameterValues("defaultValue");
		String valueMappings[] = request.getParameterValues("valueMapping");
		if(logicalName != null) {
			for(int index=0 ;index<logicalName.length;index++){
				HttpAuthDriverFieldMapData httpAuthDriverFieldMapData = new HttpAuthDriverFieldMapData();
				httpAuthDriverFieldMapData.setLogicalName(logicalName[index]);
				httpAuthDriverFieldMapData.setResponseParamIndex(Long.valueOf(responseParamIndex[index]));
				httpAuthDriverFieldMapData.setDefaultValue(defaultValues[index]);
				httpAuthDriverFieldMapData.setValueMapping(valueMappings[index]);
				httpAuthDriverFieldMapDataList.add(httpAuthDriverFieldMapData);
			}
		}
		return httpAuthDriverFieldMapDataList;
	}
	
	public void convertFromFormToData(DiameterHttpAuthDriverForm form,HttpAuthDriverData httpAuthDriverData,DriverInstanceData driverInstanceData){
		httpAuthDriverData.setHttp_url(form.getHttp_url());
		httpAuthDriverData.setStatusCheckDuration(form.getStatusCheckDuration());
		httpAuthDriverData.setMaxQueryTimeoutCount(form.getMaxQueryTimeoutCount());
		httpAuthDriverData.setExpiryDateFormat(form.getExpiryDateFormats());
		httpAuthDriverData.setUserIdentityAttributes(form.getUserIdentityAttributes());
		
		driverInstanceData.setName(form.getDriverInstanceName());
		driverInstanceData.setDescription(form.getDriverInstanceDesc());
		driverInstanceData.setDriverTypeId(Long.parseLong(form.getDriverRelatedId()));		
		driverInstanceData.setCreateDate(getCurrentTimeStemp());
		driverInstanceData.setStatus("CST01");
	}
}
