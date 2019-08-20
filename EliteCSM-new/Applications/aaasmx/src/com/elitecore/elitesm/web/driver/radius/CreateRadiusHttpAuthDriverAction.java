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
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.exceptions.server.DuplicateParameterFoundExcpetion;
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
import com.elitecore.elitesm.web.driver.radius.forms.CreateRadiusHttpAuthDriverForm;

public class CreateRadiusHttpAuthDriverAction extends BaseWebAction {
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_DRIVER;
	private static final String MODULE = CreateRadiusHttpAuthDriverAction.class.getSimpleName();
	private static final String CREATE = "cRadiusHttpAuthDriver";


	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{

		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		try{
			checkActionPermission(request, ACTION_ALIAS);
			CreateRadiusHttpAuthDriverForm createRadiusHttpAuthDriverForm = (CreateRadiusHttpAuthDriverForm)form;
			String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
			DriverBLManager driverBlManager = new DriverBLManager();
			HttpAuthDriverData httpAuthDriverData = new HttpAuthDriverData();
			DriverInstanceData driverInstanceData = new DriverInstanceData();
			CreateDriverConfig driverConfig = new CreateDriverConfig();

			if(createRadiusHttpAuthDriverForm.getAction() != null){
				if(createRadiusHttpAuthDriverForm.getAction().equals("create")){

					try{
						IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
						convertFromFormToData(createRadiusHttpAuthDriverForm,httpAuthDriverData,driverInstanceData);

						driverInstanceData.setCreatedByStaffId(currentUser);        	
						driverInstanceData.setLastModifiedDate(getCurrentTimeStemp());
						driverInstanceData.setLastModifiedByStaffId(currentUser);

						
						httpAuthDriverData.setHttpFieldMapList(getHttpAuthDriverFieldMapData(request));
						
						driverConfig.setHttpAuthDriverData(httpAuthDriverData);					
						driverConfig.setDriverInstanceData(driverInstanceData);

						driverBlManager.createHttpAuthDriver(driverConfig, staffData);
						Logger.logDebug(MODULE, "httpAuthDriverData :"+httpAuthDriverData.getHttpAuthDriverId());

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
					if(createRadiusHttpAuthDriverForm.getDriverRelatedId() == null || createRadiusHttpAuthDriverForm.getDriverInstanceName() == null || createRadiusHttpAuthDriverForm.getDriverDesp() == null){
						createRadiusHttpAuthDriverForm.setDriverInstanceName((String)request.getAttribute("instanceName"));
						createRadiusHttpAuthDriverForm.setDriverDesp((String)request.getAttribute("desp"));
						Long driverId =(Long)request.getAttribute("driverId");
						createRadiusHttpAuthDriverForm.setDriverRelatedId(driverId.toString());
					}

					// populating list of logical names ....
					List<LogicalNameValuePoolData> nameValuePoolList = driverBlManager.getLogicalNameValuePoolList();
					createRadiusHttpAuthDriverForm.setLogicalNameList(nameValuePoolList);
					
					List<String> logicalNameMultipleAllowList = driverBlManager.getLogicalValueDriverRelList(Long.valueOf(createRadiusHttpAuthDriverForm.getDriverRelatedId()));
					createRadiusHttpAuthDriverForm.setLogicalNameMultipleAllowList(logicalNameMultipleAllowList);
					
					request.setAttribute("logicalNameData", driverBlManager.getLogicalNameJson(nameValuePoolList,logicalNameMultipleAllowList));
					request.setAttribute("defaultMapping", createRadiusHttpAuthDriverForm.getDefaultmapping());
					
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

	public void convertFromFormToData(CreateRadiusHttpAuthDriverForm form,HttpAuthDriverData httpAuthDriverData,DriverInstanceData driverInstanceData){
		httpAuthDriverData.setHttp_url(form.getHttp_url());
		httpAuthDriverData.setStatusCheckDuration(form.getStatusCheckDuration());
		httpAuthDriverData.setMaxQueryTimeoutCount(form.getMaxQueryTimeoutCount());
		httpAuthDriverData.setExpiryDateFormat(form.getExpiryDateFormats());
		httpAuthDriverData.setUserIdentityAttributes(form.getUserIdentityAttributes());
		
		driverInstanceData.setName(form.getDriverInstanceName());
		driverInstanceData.setDescription(form.getDriverDesp());
		driverInstanceData.setDriverTypeId(Long.parseLong(form.getDriverRelatedId()));		
		driverInstanceData.setCreateDate(getCurrentTimeStemp());
		driverInstanceData.setStatus("CST01");
	}
	
	private List<HttpAuthDriverFieldMapData> getHttpAuthDriverFieldMapData(HttpServletRequest request){
		List<HttpAuthDriverFieldMapData> httpAuthDriverFieldMapDataList = new ArrayList<HttpAuthDriverFieldMapData>();		
		String[] logicalNames = request.getParameterValues("logicalnmVal");
		String[] responseParameterIndex = request.getParameterValues("responseParamIndex");
		String defaultValues[] = request.getParameterValues("defaultValue");
		String valueMappings[] = request.getParameterValues("valueMapping");
		if(logicalNames != null && responseParameterIndex!=null && defaultValues!=null && valueMappings!=null){
			for(int index=0; index<logicalNames.length; index++){
				HttpAuthDriverFieldMapData httpAuthDriverFieldMapData = new HttpAuthDriverFieldMapData();
				httpAuthDriverFieldMapData.setLogicalName(logicalNames[index]);
				httpAuthDriverFieldMapData.setResponseParamIndex(Long.parseLong(responseParameterIndex[index]));
				httpAuthDriverFieldMapData.setDefaultValue(defaultValues[index]);
				httpAuthDriverFieldMapData.setValueMapping(valueMappings[index]);
				httpAuthDriverFieldMapDataList.add(httpAuthDriverFieldMapData);
			}
		}
		return httpAuthDriverFieldMapDataList;
	}
}
