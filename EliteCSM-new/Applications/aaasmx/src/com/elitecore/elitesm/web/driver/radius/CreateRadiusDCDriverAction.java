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
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.dcdriver.data.DiameterChargingDriverData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.TranslationMappingConfigConstants;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.driver.radius.forms.CreateRadiusDCDriverForm;

public class CreateRadiusDCDriverAction extends BaseWebAction{
	private static final String MODULE = CreateRadiusDCDriverAction.class.getSimpleName();
	private static final String Next = "cDCDriver";
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_DRIVER;
	private static final String Next_Mapping = "cDCDriverMapping";
	private static final String SUCCESS_FORWARD = "success";

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{

		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		try{
			checkActionPermission(request, ACTION_ALIAS);
			CreateRadiusDCDriverForm createRadiusDCDriverForm = (CreateRadiusDCDriverForm)form;
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
			DriverBLManager driverblManager = new DriverBLManager();
			DriverInstanceData driverInstanceData = null;
			DiameterChargingDriverData diameterChargingDriverData = null;
			if(!"".equalsIgnoreCase(createRadiusDCDriverForm.getAction())&& createRadiusDCDriverForm.getAction() != null){
				if(createRadiusDCDriverForm.getAction().equals("next")){
					TranslationMappingConfBLManager translationMappingConfBLManager = new TranslationMappingConfBLManager();
					List<TranslationMappingConfData> translationMappingConfDataList = translationMappingConfBLManager.getTranslationMappingConfigList(TranslationMappingConfigConstants.DIAMETER, TranslationMappingConfigConstants.RADIUS);
					createRadiusDCDriverForm.setTranslationMappingConfDataList(translationMappingConfDataList);
					
				}else if(createRadiusDCDriverForm.getAction().equals("nextMapping")){
					TranslationMappingConfBLManager translationMappingConfBLManager = new TranslationMappingConfBLManager();
					List<TranslationMappingConfData> translationMappingConfDataList = translationMappingConfBLManager.getTranslationMappingConfigList(TranslationMappingConfigConstants.DIAMETER, TranslationMappingConfigConstants.DIAMETER);
					request.getSession().setAttribute("transConfList", translationMappingConfDataList);
					diameterChargingDriverData = new DiameterChargingDriverData();
					driverInstanceData = new DriverInstanceData();
					
					convertFromFormToBean(createRadiusDCDriverForm,diameterChargingDriverData,driverInstanceData);
					driverInstanceData.setLastModifiedByStaffId(currentUser);
					driverInstanceData.setCreatedByStaffId(currentUser);
					driverblManager.createDiameterChargingDriver(driverInstanceData, diameterChargingDriverData, staffData);
					doAuditing(staffData, ACTION_ALIAS);
					request.getSession().removeAttribute("createRadiusDCDriverForm");
					request.setAttribute("responseUrl", "/initSearchDriver");
					ActionMessage message = new ActionMessage("driver.create.success");
					ActionMessages messages = new ActionMessages();
					messages.add("information",message);
					saveMessages(request, messages);
					return mapping.findForward(SUCCESS_FORWARD);
				}		
			}

			if(createRadiusDCDriverForm.getDriverRelatedId() == null || createRadiusDCDriverForm.getDriverInstanceName() == null || createRadiusDCDriverForm.getDriverDesp() == null){
				createRadiusDCDriverForm.setDriverInstanceName((String)request.getAttribute("instanceName"));
				createRadiusDCDriverForm.setDriverDesp((String)request.getAttribute("desp"));
				Long driverId =(Long)request.getAttribute("driverId");
				createRadiusDCDriverForm.setDriverRelatedId(driverId.toString());
				
				request.setAttribute("createRadiusDCDriverForm", createRadiusDCDriverForm);
			}
			return mapping.findForward(Next);
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

	private void convertFromFormToBean(CreateRadiusDCDriverForm createDcDriverForm,DiameterChargingDriverData diameterChargingDriverData,DriverInstanceData driverInstanceData) {
		diameterChargingDriverData.setTransMapConfId(createDcDriverForm.getTranslationMapConfigId());
		diameterChargingDriverData.setDisConnectUrl(createDcDriverForm.getDisConnectUrl());
		
		driverInstanceData.setName(createDcDriverForm.getDriverInstanceName());
		driverInstanceData.setDescription(createDcDriverForm.getDriverDesp());
		driverInstanceData.setDriverTypeId(Long.parseLong(createDcDriverForm.getDriverRelatedId()));
		driverInstanceData.setCreateDate(getCurrentTimeStemp());
		driverInstanceData.setLastModifiedDate(getCurrentTimeStemp());		
		driverInstanceData.setStatus("CST01");
	}
}
