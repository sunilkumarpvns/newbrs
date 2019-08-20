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
import com.elitecore.elitesm.web.driver.diameter.forms.DiameterHttpAuthDriverForm;

public class UpdateDiameterHttpAuthDriverAction extends BaseWebAction{
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_DRIVER;
	private static final String SUCCESS_FORWARD = "success";
	private static final String UPDATE_FORWARD = "updateDiameterHttpAuthDriverInstance";

	
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		Logger.logInfo(MODULE, "Enter execute method of: "+getClass().getName());
		try{
			checkActionPermission(request, ACTION_ALIAS);
			
			DiameterHttpAuthDriverForm updateDiameterHttpAuthDriverForm = (DiameterHttpAuthDriverForm) form;
			DriverBLManager driverBLManager = new DriverBLManager();
			DriverInstanceData driverInstanceData =null;
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			
			if("Update".equals(updateDiameterHttpAuthDriverForm.getAction())){
				driverInstanceData = new DriverInstanceData();
				String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
				
				HttpAuthDriverData httpAuthDriverData = new HttpAuthDriverData();
				httpAuthDriverData.setHttpFieldMapList(getSelectedHttpAuthDriverFieldMapDataSet(request));
				
				driverInstanceData.setLastModifiedDate(getCurrentTimeStemp());
				driverInstanceData.setLastModifiedByStaffId(currentUser);
				
			    convertFromFormToBean(updateDiameterHttpAuthDriverForm, httpAuthDriverData, driverInstanceData);
			    
				staffData.setAuditId(driverInstanceData.getAuditUId());
				staffData.setAuditName(driverInstanceData.getName());
			    
				driverBLManager.updateHttpAuthDriverById(driverInstanceData, httpAuthDriverData,staffData);

				request.setAttribute("responseUrl", "/viewDriverInstance.do?driverInstanceId=" + updateDiameterHttpAuthDriverForm.getDriverInstanceId());
				ActionMessage message = new ActionMessage("driver.update.success");
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveMessages(request, messages);
				return mapping.findForward(SUCCESS_FORWARD);
				
			} else {
				
				driverInstanceData = (DriverInstanceData) request.getSession().getAttribute("driverInstance");
				driverInstanceData.setDriverTypeData(driverBLManager.getDriverTypeDataById(driverInstanceData.getDriverTypeId()));
				HttpAuthDriverData httpAuthDriverData = driverBLManager.getHttpDriverByDriverInstanceId(updateDiameterHttpAuthDriverForm.getDriverInstanceId());
				
				convertFromBeanToForm(updateDiameterHttpAuthDriverForm, driverInstanceData, httpAuthDriverData);
				
				List<LogicalNameValuePoolData> naveValuePoolList = driverBLManager.getLogicalNameValuePoolList();
				updateDiameterHttpAuthDriverForm.setLogicalNameList(naveValuePoolList);
				List<String> logicalNameMultipleAllowList = driverBLManager.getLogicalValueDriverRelList(driverInstanceData.getDriverTypeId());
				updateDiameterHttpAuthDriverForm.setLogicalNameMultipleAllowList(logicalNameMultipleAllowList);
				
				request.setAttribute("httpAuthDriverData", httpAuthDriverData);
				request.setAttribute("driverInstanceData", driverInstanceData);
				request.setAttribute("logicalNameData", driverBLManager.getLogicalNameJson(naveValuePoolList,logicalNameMultipleAllowList));
				request.getSession().setAttribute("httpAuthFieldMapList", httpAuthDriverData.getHttpFieldMapList());
				
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
	
	private  void convertFromBeanToForm(DiameterHttpAuthDriverForm form, DriverInstanceData driverInstanceData, HttpAuthDriverData httpAuthDriverData){
		form.setDriverInstanceName(driverInstanceData.getName());
		form.setDriverInstanceDesc(driverInstanceData.getDescription());
		form.setDriverRelatedId(Long.toString(driverInstanceData.getDriverTypeId()));
		form.setAuditUId(driverInstanceData.getAuditUId());
		
		form.setHttp_url(httpAuthDriverData.getHttp_url());
		form.setMaxQueryTimeoutCount(httpAuthDriverData.getMaxQueryTimeoutCount());
		form.setStatusCheckDuration(httpAuthDriverData.getStatusCheckDuration());
		form.setUserIdentityAttributes(httpAuthDriverData.getUserIdentityAttributes());
		form.setExpiryDateFormats(httpAuthDriverData.getExpiryDateFormat());
	}
	
	private void convertFromFormToBean(DiameterHttpAuthDriverForm form,HttpAuthDriverData httpAuthDriverData,DriverInstanceData driverInstanceData){
		httpAuthDriverData.setHttp_url(form.getHttp_url());
		httpAuthDriverData.setStatusCheckDuration(form.getStatusCheckDuration());
		httpAuthDriverData.setMaxQueryTimeoutCount(form.getMaxQueryTimeoutCount());
		httpAuthDriverData.setExpiryDateFormat(form.getExpiryDateFormats());
		httpAuthDriverData.setUserIdentityAttributes(form.getUserIdentityAttributes());
		httpAuthDriverData.setDriverInstanceId(form.getDriverInstanceId());
		
		driverInstanceData.setDriverInstanceId(form.getDriverInstanceId());
		driverInstanceData.setName(form.getDriverInstanceName());
		driverInstanceData.setDescription(form.getDriverInstanceDesc());
		driverInstanceData.setDriverTypeId(Long.parseLong(form.getDriverRelatedId()));		
		driverInstanceData.setCreateDate(getCurrentTimeStemp());
		driverInstanceData.setStatus("CST01");
		driverInstanceData.setAuditUId(form.getAuditUId());
	}
	
	private List<HttpAuthDriverFieldMapData> getSelectedHttpAuthDriverFieldMapDataSet(HttpServletRequest request){
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
}
