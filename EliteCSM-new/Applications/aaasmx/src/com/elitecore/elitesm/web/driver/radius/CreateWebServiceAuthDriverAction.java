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
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.LogicalNameValuePoolData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.webserviceauthdriver.data.WebMethodKeyMapRelData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.webserviceauthdriver.data.WebServiceAuthDriverData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.driver.radius.forms.CreateWebServiceAuthDriverForm;

public class CreateWebServiceAuthDriverAction extends BaseWebAction{

	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_DRIVER;
	private static final String MODULE = CreateRadiusUserFileAuthDriverAction.class.getSimpleName();
	private static final String CREATE = "cWebServiceAuthDriver";


	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{

		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		try{

			checkActionPermission(request, ACTION_ALIAS);
			CreateWebServiceAuthDriverForm driverForm = (CreateWebServiceAuthDriverForm)form;
			DriverBLManager driverBlManager = new DriverBLManager();
			WebServiceAuthDriverData webServiceAuthDriverData = new WebServiceAuthDriverData();
			DriverInstanceData driverInstance = new DriverInstanceData();
			
			String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			Logger.logDebug(MODULE, "Action: "+driverForm.getAction());
			if(driverForm.getAction() != null){
				
				if(driverForm.getAction().equals("create")){

					try{
					
						convertFromFormToData(driverForm,webServiceAuthDriverData,driverInstance);
			
						webServiceAuthDriverData.setWebMethodKeyDataList(getWebMethodKeyMapRelData(request));
						driverInstance.setCreatedByStaffId(currentUser);        	
						driverInstance.setLastModifiedDate(getCurrentTimeStemp());
						driverInstance.setLastModifiedByStaffId(currentUser);

					
						driverBlManager.createWebServiceAuthDriver( driverInstance, webServiceAuthDriverData, staffData);
						doAuditing(staffData,ACTION_ALIAS);
						
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
						ActionMessage message = new ActionMessage("driver.create.duplicate.failure",driverInstance.getName());
						ActionMessages messages = new ActionMessages();
						messages.add("information",message);
						saveErrors(request,messages);
						return mapping.findForward(FAILURE_FORWARD);
					}catch(Exception e){

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
			
					driverForm.setWebMethodKeyList(driverBlManager.getWebMethodKeyDataList());
					driverForm.setLogicalNameList(driverBlManager.getLogicalNameValuePoolList());
					driverForm.setDriverInstanceName((String)request.getAttribute("instanceName"));
					driverForm.setDriverDesp((String)request.getAttribute("desp"));
					Long driverId =(Long)request.getAttribute("driverId");
					driverForm.setDriverRelatedId(driverId.toString());
					
					List<String> logicalNameMultipleAllowList = driverBlManager.getLogicalValueDriverRelList(Long.valueOf(driverForm.getDriverRelatedId()));
					driverForm.setLogicalNameMultipleAllowList(logicalNameMultipleAllowList);
					
					List<LogicalNameValuePoolData> nameValuePoolList = driverBlManager.getLogicalNameValuePoolList();
					
					request.setAttribute("logicalNameData", driverBlManager.getLogicalNameJson(nameValuePoolList,logicalNameMultipleAllowList));
					request.setAttribute("defaultMapping", driverForm.getDefaultmapping());
					
					return mapping.findForward(CREATE);
				}
			}

			return mapping.findForward(SUCCESS_FORWARD);
		}catch(ActionNotPermitedException e){
			Logger.logError(MODULE,"Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
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
		}catch(Exception dme){

			Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
			Logger.logTrace(MODULE,dme);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(dme);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("driver.create.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			saveErrors(request,messages);
			return mapping.findForward(FAILURE_FORWARD);
		}
	}


	private void convertFromFormToData(CreateWebServiceAuthDriverForm driverForm,WebServiceAuthDriverData webServiceAuthDriverData,DriverInstanceData driverInstance) {

		driverInstance.setName(driverForm.getDriverInstanceName());
		driverInstance.setDescription(driverForm.getDriverDesp());
		driverInstance.setDriverTypeId(Long.parseLong(driverForm.getDriverRelatedId()));		
		driverInstance.setCreateDate(getCurrentTimeStemp());
		driverInstance.setStatus("CST01");

		webServiceAuthDriverData.setServiceAddress(driverForm.getServiceAddress());
		webServiceAuthDriverData.setImsiAttribute(driverForm.getImsiAttribute());
		webServiceAuthDriverData.setMaxQueryTimeoutCnt(driverForm.getMaxQueryTimeoutCnt());
		webServiceAuthDriverData.setStatusChkDuration(driverForm.getStatusChkDuration());
		webServiceAuthDriverData.setUserIdentityAttributes(driverForm.getUserIdentityAttributes());
	}
	
	private List<WebMethodKeyMapRelData> getWebMethodKeyMapRelData(HttpServletRequest request){
		List<WebMethodKeyMapRelData> webMethodKeyMapDataList = new ArrayList<WebMethodKeyMapRelData>();
		String[] logicalNames = request.getParameterValues("logicalnmVal");
		String[] profileField = request.getParameterValues("webMethodKey");
		String defaultValues[] = request.getParameterValues("defaultValue");
		String valueMappings[] = request.getParameterValues("valueMapping");
		if(logicalNames != null && profileField!=null && defaultValues!=null && valueMappings!=null){
			for(int index=0; index<logicalNames.length; index++){
				WebMethodKeyMapRelData gatewayFieldMapData = new WebMethodKeyMapRelData();
				gatewayFieldMapData.setLogicalName(logicalNames[index]);
				gatewayFieldMapData.setWebMethodKey(profileField[index]);
				gatewayFieldMapData.setDefaultValue(defaultValues[index]);
				gatewayFieldMapData.setValueMapping(valueMappings[index]);
				webMethodKeyMapDataList.add(gatewayFieldMapData);
			}
		}
		return webMethodKeyMapDataList;
	}
}
