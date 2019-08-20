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
import com.elitecore.elitesm.datamanager.servermgr.drivers.httpdriver.data.HttpAuthDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.httpdriver.data.HttpAuthDriverFieldMapData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.driver.radius.forms.UpdateRadiusHttpAuthDriverForm;

public class UpdateRadiusHttpAuthDriverAction extends BaseWebAction {
	private static final String HTTP_FORWARD = "updateRadiusHttpAuthDriver";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_DRIVER;
	private static final String MODULE=UpdateRadiusHttpAuthDriverAction.class.getSimpleName();
	private static final String VIEW_ACTION_ALIAS = ConfigConstant.VIEW_DRIVER;

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		try{
			UpdateRadiusHttpAuthDriverForm updateRadiusHttpAuthDriverForm = (UpdateRadiusHttpAuthDriverForm)form;
			
			if("view".equals(updateRadiusHttpAuthDriverForm.getAction())) {
				checkActionPermission(request, VIEW_ACTION_ALIAS);
			} else {
				checkActionPermission(request, ACTION_ALIAS);
			}
			
			DriverBLManager driverBLManager = new DriverBLManager();
			String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			DriverInstanceData driverInstanceData = driverBLManager.getDriverInstanceByDriverInstanceId(updateRadiusHttpAuthDriverForm.getDriverInstanceId());
			HttpAuthDriverData httpAuthDriverData = driverBLManager.getHttpDriverByDriverInstanceId(updateRadiusHttpAuthDriverForm.getDriverInstanceId());

			List<LogicalNameValuePoolData> logicalNameList = driverBLManager.getLogicalNameValuePoolList();

			if(httpAuthDriverData != null){
				updateRadiusHttpAuthDriverForm.setHttpAuthDriverId(httpAuthDriverData.getHttpAuthDriverId());
			}
			request.getSession().setAttribute("driverInstanceData", driverInstanceData);

			if(updateRadiusHttpAuthDriverForm.getAction() != null){
				if(updateRadiusHttpAuthDriverForm.getAction().equals("Update")){

					DriverInstanceData updatedDriverInstance = new DriverInstanceData();
					HttpAuthDriverData updateHttpAuthDriverData = new HttpAuthDriverData();

					convertFromFormToData(updateRadiusHttpAuthDriverForm,updatedDriverInstance,updateHttpAuthDriverData);		
					
					updateHttpAuthDriverData.setHttpFieldMapList(getHttpAuthDriverFieldMapData(request));
					
					updatedDriverInstance.setLastModifiedDate(getCurrentTimeStemp());
					updatedDriverInstance.setLastModifiedByStaffId(currentUser);
					
					try{
						staffData.setAuditId(updatedDriverInstance.getAuditUId());
						staffData.setAuditName(updatedDriverInstance.getName());
						
						driverBLManager.updateHttpAuthDriverById(updatedDriverInstance,updateHttpAuthDriverData,staffData);
						doAuditing(staffData, ACTION_ALIAS);
						request.setAttribute("responseUrl", "/viewDriverInstance.do?driverInstanceId=" + updateRadiusHttpAuthDriverForm.getDriverInstanceId());
						ActionMessage message = new ActionMessage("driver.update.success");
						ActionMessages messages = new ActionMessages();
						messages.add("information",message);
						saveMessages(request, messages);
						return mapping.findForward(SUCCESS);
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
					}
				}
			}

			updateRadiusHttpAuthDriverForm.setDriverInstanceName(driverInstanceData.getName());
			updateRadiusHttpAuthDriverForm.setDriverInstanceDesc(driverInstanceData.getDescription());
			updateRadiusHttpAuthDriverForm.setAuditUId(driverInstanceData.getAuditUId());
			
			updateRadiusHttpAuthDriverForm.setDriverInstanceId(httpAuthDriverData.getDriverInstanceId());
			updateRadiusHttpAuthDriverForm.setLogicalNameList(logicalNameList);
			updateRadiusHttpAuthDriverForm.setMaxQueryTimeoutCount(httpAuthDriverData.getMaxQueryTimeoutCount());
			updateRadiusHttpAuthDriverForm.setHttp_url(httpAuthDriverData.getHttp_url());
			updateRadiusHttpAuthDriverForm.setStatusCheckDuration(httpAuthDriverData.getStatusCheckDuration());
			updateRadiusHttpAuthDriverForm.setExpiryDateFormats(httpAuthDriverData.getExpiryDateFormat());
			updateRadiusHttpAuthDriverForm.setUserIdentityAttributes(httpAuthDriverData.getUserIdentityAttributes());
			
			List<String> logicalNameMultipleAllowList = driverBLManager.getLogicalValueDriverRelList(driverInstanceData.getDriverTypeId());
			updateRadiusHttpAuthDriverForm.setLogicalNameMultipleAllowList(logicalNameMultipleAllowList);
			
			request.setAttribute("logicalNameData", driverBLManager.getLogicalNameJson(logicalNameList,logicalNameMultipleAllowList));
			request.getSession().setAttribute("httpAuthFieldMapList", httpAuthDriverData.getHttpFieldMapList());
			request.getSession().setAttribute("driverInstance",driverInstanceData);
			if("view".equals(updateRadiusHttpAuthDriverForm.getAction())) {
				doAuditing(staffData, VIEW_ACTION_ALIAS);
			}
			return mapping.findForward(HTTP_FORWARD);
		}catch(ActionNotPermitedException e){
			Logger.logError(MODULE,"Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}catch(Exception e){
			Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
			Logger.logTrace(MODULE,e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("driver.update.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			saveErrors(request,messages);
			return mapping.findForward(FAILURE_FORWARD);
		}
	}

	private void convertFromFormToData(UpdateRadiusHttpAuthDriverForm updateRadiusHttpAuthDriverForm,DriverInstanceData updatedDriverInstance,HttpAuthDriverData httpAuthDriverData) {
		updatedDriverInstance.setName(updateRadiusHttpAuthDriverForm.getDriverInstanceName());
		updatedDriverInstance.setDescription(updateRadiusHttpAuthDriverForm.getDriverInstanceDesc());
		updatedDriverInstance.setDriverInstanceId(updateRadiusHttpAuthDriverForm.getDriverInstanceId());
		updatedDriverInstance.setAuditUId(updateRadiusHttpAuthDriverForm.getAuditUId());
		
		httpAuthDriverData.setHttp_url(updateRadiusHttpAuthDriverForm.getHttp_url());
		httpAuthDriverData.setStatusCheckDuration(updateRadiusHttpAuthDriverForm.getStatusCheckDuration());
		httpAuthDriverData.setMaxQueryTimeoutCount(updateRadiusHttpAuthDriverForm.getMaxQueryTimeoutCount());
		httpAuthDriverData.setExpiryDateFormat(updateRadiusHttpAuthDriverForm.getExpiryDateFormats());
		httpAuthDriverData.setDriverInstanceId(updateRadiusHttpAuthDriverForm.getDriverInstanceId());
		httpAuthDriverData.setUserIdentityAttributes(updateRadiusHttpAuthDriverForm.getUserIdentityAttributes());
	}
	private List<HttpAuthDriverFieldMapData> getHttpAuthDriverFieldMapData(HttpServletRequest request){
		List<HttpAuthDriverFieldMapData> httpAuthDriveFieldMapDataList = new ArrayList<HttpAuthDriverFieldMapData>();
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
				httpAuthDriveFieldMapDataList.add(httpAuthDriverFieldMapData);
			}
		}
		return httpAuthDriveFieldMapDataList;
	}
}