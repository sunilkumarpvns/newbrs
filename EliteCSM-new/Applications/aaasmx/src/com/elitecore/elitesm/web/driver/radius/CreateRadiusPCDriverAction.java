package com.elitecore.elitesm.web.driver.radius;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.elitesm.blmanager.servermgr.transmapconf.TranslationMappingConfBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.exceptions.server.DuplicateParameterFoundExcpetion;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.pcdriver.data.ParleyChargingDriverData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.TranslationMappingConfigConstants;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.driver.radius.forms.CreateRadiusPCDriverForm;
import com.elitecore.passwordutil.PasswordEncryption;

public class CreateRadiusPCDriverAction extends BaseWebAction{

	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String MODULE = CreateRadiusPCDriverAction.class.getSimpleName();
	private static final String CREATE = "cRadiusPCDriver";
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_DRIVER;

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{

		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		try{
			checkActionPermission(request, ACTION_ALIAS);
			CreateRadiusPCDriverForm createRadiusPCDriverForm = (CreateRadiusPCDriverForm)form;
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
			DriverBLManager driverblManager = new DriverBLManager();
			DriverInstanceData driverInstanceData = null;
			TranslationMappingConfBLManager translationMappingConfBLManager = new TranslationMappingConfBLManager();
			List<TranslationMappingConfData> translationMappingConfDataList = translationMappingConfBLManager.getTranslationMappingConfigList(TranslationMappingConfigConstants.PARLEY_CHARGING, TranslationMappingConfigConstants.RADIUS);

			createRadiusPCDriverForm.setTranslationMappingConfDataList(translationMappingConfDataList);
			if(createRadiusPCDriverForm.getAction() != null){
				if(createRadiusPCDriverForm.getAction().equals("create")){
					try{	
						driverInstanceData = new DriverInstanceData();
						ParleyChargingDriverData parleyChargingDriverData =  new ParleyChargingDriverData();
						convertFromFormToData(createRadiusPCDriverForm,parleyChargingDriverData,driverInstanceData);

						/* Encrypt  password */
						String encryptedPassword = PasswordEncryption.getInstance().crypt(parleyChargingDriverData.getPassword(),PasswordEncryption.ELITE_PASSWORD_CRYPT);
						parleyChargingDriverData.setPassword(encryptedPassword);
						
						driverInstanceData.setLastModifiedByStaffId(currentUser);
						driverInstanceData.setCreatedByStaffId(currentUser);
						driverblManager.createRadiusPCDriver(driverInstanceData, parleyChargingDriverData);
						doAuditing(staffData, ACTION_ALIAS);
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
					if(createRadiusPCDriverForm.getDriverRelatedId() == null || createRadiusPCDriverForm.getDriverInstanceName() == null || createRadiusPCDriverForm.getDriverDesp() == null){
						createRadiusPCDriverForm.setDriverInstanceName((String)request.getAttribute("instanceName"));
						createRadiusPCDriverForm.setDriverDesp((String)request.getAttribute("desp"));
						Long driverId =(Long)request.getAttribute("driverId");
						createRadiusPCDriverForm.setDriverRelatedId(driverId.toString());
					}
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

	public void convertFromFormToData(CreateRadiusPCDriverForm createRadiusPCDriverForm,ParleyChargingDriverData parleyChargingDriverData,DriverInstanceData driverInstanceData){
		parleyChargingDriverData.setWsAddress(createRadiusPCDriverForm.getWsAddress());
		parleyChargingDriverData.setSmServiceName(createRadiusPCDriverForm.getSmServiceName());
		parleyChargingDriverData.setParleyServiceName(createRadiusPCDriverForm.getParleyServiceName());
		parleyChargingDriverData.setUserName(createRadiusPCDriverForm.getUserName());
		parleyChargingDriverData.setPassword(createRadiusPCDriverForm.getPassword());
		parleyChargingDriverData.setTransMapConfId(createRadiusPCDriverForm.getTranslationMapConfigId());
		driverInstanceData.setName(createRadiusPCDriverForm.getDriverInstanceName());
		driverInstanceData.setDescription(createRadiusPCDriverForm.getDriverDesp());
		driverInstanceData.setDriverTypeId(Long.parseLong(createRadiusPCDriverForm.getDriverRelatedId()));
		driverInstanceData.setCreateDate(getCurrentTimeStemp());
		driverInstanceData.setLastModifiedDate(getCurrentTimeStemp());		
		driverInstanceData.setStatus("CST01");
	}
}
