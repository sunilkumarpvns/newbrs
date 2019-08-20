package com.elitecore.elitesm.web.driver.radius;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.core.commons.tls.cipher.CipherSuites;
import com.elitecore.diameterapi.core.common.transport.constant.SecurityStandard;
import com.elitecore.elitesm.blmanager.diameter.routingconf.DiameterRoutingConfBLManager;
import com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.elitesm.blmanager.servermgr.transmapconf.TranslationMappingConfBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.certificate.data.ServerCertificateData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.dcdriver.data.DiameterChargingDriverData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.TranslationMappingConfigConstants;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.driver.radius.forms.UpdateRadiusDCDriverForm;

public class UpdateRadiusDCDriverAction extends BaseWebAction {

	private static final String FAILURE_FORWARD = "failure";
	private static final String UPDATE_FORWARD = "viewRadiusDCDriverInstance";
	private static final String INIT_UPDATE_FORWARD = "updateRadiusDCDriver";
	private static final String UPDATE_ACTION_ALIAS = ConfigConstant.UPDATE_DRIVER;
	private static final String VIEW_ACTION_ALIAS = ConfigConstant.VIEW_DRIVER;

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());		
		try{
			UpdateRadiusDCDriverForm updateRadiusDCDriverForm = (UpdateRadiusDCDriverForm)form;
			if("view".equals(updateRadiusDCDriverForm.getAction())) {
				checkActionPermission(request, VIEW_ACTION_ALIAS);
			} else {
				checkActionPermission(request, UPDATE_ACTION_ALIAS);
			}
			DriverBLManager driverBlManager = new DriverBLManager();
			DiameterChargingDriverData diameterChargingDriverData = driverBlManager.getDiameterChargingDataByDriverInstanceId(updateRadiusDCDriverForm.getDriverInstanceId());
			TranslationMappingConfData translationMappingConfData = diameterChargingDriverData.getTranslationMappingConfData();
			DriverInstanceData driverInstanceData = driverBlManager.getDriverInstanceByDriverInstanceId(updateRadiusDCDriverForm.getDriverInstanceId());
			String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			if(updateRadiusDCDriverForm.getAction() != null && !"view".equals(updateRadiusDCDriverForm.getAction())){
				DiameterChargingDriverData diameterChargingData = new DiameterChargingDriverData();
				DriverInstanceData driverInstdata = new DriverInstanceData();
				updateRadiusDCDriverForm.setSelectedTranslationMappingConfData(translationMappingConfData);
				
				convertFormToBean(updateRadiusDCDriverForm,diameterChargingData,driverInstdata);
				
				driverInstdata.setLastModifiedByStaffId(currentUser);
				
				
				driverInstanceData = driverBlManager.getDriverInstanceByDriverInstanceId(updateRadiusDCDriverForm.getDriverInstanceId());
				request.getSession().setAttribute("diameterChargingDriverData", diameterChargingDriverData);
				request.setAttribute("driverInstanceData",driverInstanceData);
				
				Set<DiameterChargingDriverData> set = driverInstanceData.getDiameterChargingDriverSet();
				Iterator<DiameterChargingDriverData> iterator = set.iterator();
				if(iterator.hasNext()){
					diameterChargingDriverData = iterator.next();
				}
				
				staffData.setAuditId(driverInstanceData.getAuditUId());
				staffData.setAuditName(driverInstanceData.getName());
				driverInstdata.setAuditUId(driverInstanceData.getAuditUId());
				
				driverBlManager.updateDCDriverData(driverInstdata,diameterChargingData,staffData,UPDATE_ACTION_ALIAS);
				
	 			request.setAttribute("diameterChargingDriverData",diameterChargingDriverData);
				return mapping.findForward(UPDATE_FORWARD);
			}else{
				
				TranslationMappingConfBLManager translationMappingConfBLManager = new TranslationMappingConfBLManager();
				List<TranslationMappingConfData> translationMappingConfDataList = translationMappingConfBLManager.getTranslationMappingConfigList(TranslationMappingConfigConstants.DIAMETER, TranslationMappingConfigConstants.RADIUS);
				updateRadiusDCDriverForm.setTranslationMappingConfDataList(translationMappingConfDataList);
				
				setFormToBean(updateRadiusDCDriverForm,diameterChargingDriverData);
				
				updateRadiusDCDriverForm.setSelectedTranslationMappingConfData(translationMappingConfData);
				updateRadiusDCDriverForm.setDriverInstanceName(driverInstanceData.getName());
				updateRadiusDCDriverForm.setDriverDesp(driverInstanceData.getDescription());
				updateRadiusDCDriverForm.setDriverRelatedId(String.valueOf(driverInstanceData.getDriverInstanceId()));
			
				request.setAttribute("updateRadiusDCDriverForm", updateRadiusDCDriverForm);
				request.getSession().setAttribute("diameterChargingDriverData", diameterChargingDriverData);
				request.setAttribute("driverInstanceData",driverInstanceData);
				request.setAttribute("diameterChargingDriverData",diameterChargingDriverData);
				
				return mapping.findForward(INIT_UPDATE_FORWARD);
				
			}
		}catch(ActionNotPermitedException e){
			Logger.logError(MODULE,"Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
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
		}catch(Exception dme){

			Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
			Logger.logTrace(MODULE,dme);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(dme);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("driver.update.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			saveErrors(request,messages);
			return mapping.findForward(FAILURE_FORWARD);
		}		
	}
	private void setFormToBean(UpdateRadiusDCDriverForm updateRadiusDCDriverForm,DiameterChargingDriverData diameterChargingDriverData) {
		updateRadiusDCDriverForm.setDisConnectUrl(diameterChargingDriverData.getDisConnectUrl());
		updateRadiusDCDriverForm.setTranslationMapConfigId(diameterChargingDriverData.getTransMapConfId());
	}
	
	private void convertFormToBean(UpdateRadiusDCDriverForm updateRadiusDCDriverForm,DiameterChargingDriverData diameterChargingDriverData,DriverInstanceData driverInstdata) {
		diameterChargingDriverData.setTransMapConfId(updateRadiusDCDriverForm.getTranslationMapConfigId());
		diameterChargingDriverData.setDisConnectUrl(updateRadiusDCDriverForm.getDisConnectUrl());
		
		// driver related 
		driverInstdata.setName(updateRadiusDCDriverForm.getDriverInstanceName());
		driverInstdata.setDescription(updateRadiusDCDriverForm.getDriverDesp());
		driverInstdata.setLastModifiedDate(getCurrentTimeStemp());
		driverInstdata.setDriverInstanceId(updateRadiusDCDriverForm.getDriverInstanceId());
		driverInstdata.setAuditUId(updateRadiusDCDriverForm.getAuditUId());
	}
	
}
