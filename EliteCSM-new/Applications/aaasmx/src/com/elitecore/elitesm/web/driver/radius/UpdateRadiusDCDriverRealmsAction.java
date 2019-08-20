package com.elitecore.elitesm.web.driver.radius;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.dcdriver.data.DiameterChargingDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.dcdriver.data.DiameterChargingDriverPeerData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.dcdriver.data.DiameterChargingDriverRealmsData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.dcdriver.data.DiameterChargingDriverVendorData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.TranslationMappingConfigConstants;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.driver.radius.forms.UpdateRadiusDCDriverRealmsForm;

public class UpdateRadiusDCDriverRealmsAction extends BaseWebAction {

	private static final String FAILURE_FORWARD = "failure";
	private static final String INIT_UPDATE_FORWARD = "updateRadiusDCDriverRealms";
	private static final String INIT_VIEW_FORWARD = "viewRadiusDCDriverInstance";
	private static final String UPDATE_ACTION_ALIAS = ConfigConstant.UPDATE_DRIVER;
	private static final String VIEW_ACTION_ALIAS = ConfigConstant.VIEW_DRIVER;

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());		
		try{
			UpdateRadiusDCDriverRealmsForm updateRadiusDCDriverRealmsForm= (UpdateRadiusDCDriverRealmsForm)form;
			if("view".equals(updateRadiusDCDriverRealmsForm.getAction())) {
				checkAccess(request, VIEW_ACTION_ALIAS);
			} else {
				checkAccess(request, UPDATE_ACTION_ALIAS);
			}
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
			DriverBLManager driverBlManager = new DriverBLManager();
			DiameterChargingDriverData diameterChargingDriverData = null;
			DriverInstanceData driverInstanceData = null;
			String driverId = request.getParameter("driverInstanceId");
			String driverInstanceId = driverId;
			if(updateRadiusDCDriverRealmsForm.getAction() == null){				
				updateRadiusDCDriverRealmsForm.setDriverInstanceId(driverInstanceId);
				driverInstanceData = driverBlManager.getDriverInstanceByDriverInstanceId(driverInstanceId);
				TranslationMappingConfBLManager translationMappingConfBLManager = new TranslationMappingConfBLManager();
				List<TranslationMappingConfData> translationMappingConfDataList = translationMappingConfBLManager.getTranslationMappingConfigList(TranslationMappingConfigConstants.DIAMETER, TranslationMappingConfigConstants.DIAMETER);
				updateRadiusDCDriverRealmsForm.setDriverInstanceName(driverInstanceData.getName());
				updateRadiusDCDriverRealmsForm.setDriverDesp(driverInstanceData.getDescription());
				DiameterChargingDriverData diameterChargingData = driverBlManager.getDiameterChargingDataByDriverInstanceId(driverInstanceId);
				request.setAttribute("diameterChargingDriverData", diameterChargingData);
				request.setAttribute("transConfList",translationMappingConfDataList);
				return mapping.findForward(INIT_UPDATE_FORWARD);
			}else if(updateRadiusDCDriverRealmsForm.getAction().equalsIgnoreCase("save")){
				diameterChargingDriverData = new DiameterChargingDriverData();
				driverInstanceData = new DriverInstanceData();
				driverInstanceData.setDriverInstanceId(updateRadiusDCDriverRealmsForm.getDriverInstanceId());
				driverInstanceData.setLastModifiedByStaffId(currentUser);
				driverInstanceData.setStatus("CST01");
				driverInstanceData.setLastModifiedDate(getCurrentTimeStemp());

				String strRealmsIndex = request.getParameter("realmIndex");
				int realmsIndex = Integer.parseInt(strRealmsIndex);
				Set<DiameterChargingDriverRealmsData> diameterChargingDriverRealmsDataSet = new LinkedHashSet<DiameterChargingDriverRealmsData>();
				DiameterChargingDriverRealmsData diameterChargingDriverRealmsData = null;
				for(int i=0;i<realmsIndex ;i++){
					int index = i+1;
					String realmName = request.getParameter("rname"+index);
					String routingActionName = request.getParameter("routingaction"+index);
					String authAppIdValue = request.getParameter("authApp"+index);
					String acctAppIdValue = request.getParameter("acctApp"+index);
					
					if(realmName != null && realmName.length() > 0 && routingActionName != null && routingActionName.length() > 0) {
						diameterChargingDriverRealmsData = new DiameterChargingDriverRealmsData();
						diameterChargingDriverRealmsData.setRealmName(realmName);
						diameterChargingDriverRealmsData.setRoutingAction(Long.parseLong(routingActionName));
						diameterChargingDriverRealmsData.setAuthApplicationId(authAppIdValue);
						diameterChargingDriverRealmsData.setAcctApplicationId(acctAppIdValue);
						diameterChargingDriverRealmsData.setRealmVendorRelSet(getVendorDataSet(request,index));
						diameterChargingDriverRealmsData.setRealmPeerRelSet(getPeerDataSet(request,index));
						diameterChargingDriverRealmsDataSet.add(diameterChargingDriverRealmsData);
					}																
				}
				DiameterChargingDriverData diameterChargingData = driverBlManager.getDiameterChargingDataByDriverInstanceId(driverInstanceId);
				driverInstanceData = driverBlManager.getDriverInstanceByDriverInstanceId(driverInstanceId);
				request.setAttribute("diameterChargingDriverData", diameterChargingData);
				request.setAttribute("driverInstanceData",driverInstanceData);
				
				staffData.setAuditName(driverInstanceData.getName());
				staffData.setAuditId(driverInstanceData.getAuditUId());
				
				driverBlManager.updateDiameterChargingDriverDataById(driverInstanceData, diameterChargingDriverData, staffData);
				
				ActionMessage message = new ActionMessage("realmsconf.create.success");
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveMessages(request, messages);
				return mapping.findForward(INIT_VIEW_FORWARD);
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
		return mapping.findForward(FAILURE);
	}
	
	private Set<DiameterChargingDriverVendorData> getVendorDataSet(HttpServletRequest request, int index){
		Set<DiameterChargingDriverVendorData> vendorDataSet = new LinkedHashSet<DiameterChargingDriverVendorData>();
		DiameterChargingDriverVendorData diameterChargingDriverVendorData = null;

		String vendorIds[] = request.getParameterValues("vendorIdvendorTable"+index);
		String authAppIds[] = request.getParameterValues("authAppIdvendorTable"+index);
		String acctAppIds[] = request.getParameterValues("acctAppIdvendorTable"+index);

		if( vendorIds!=null && vendorIds.length > 0){
			for (int j = 0; j < vendorIds.length; j++) {
				if(vendorIds[j]!=null && vendorIds[j].trim().length()>0){
					diameterChargingDriverVendorData = new DiameterChargingDriverVendorData();
					diameterChargingDriverVendorData.setVendorId(Long.parseLong(vendorIds[j]));
					diameterChargingDriverVendorData.setAuthApplicationId(Long.parseLong(authAppIds[j]));
					diameterChargingDriverVendorData.setAcctApplicationId(Long.parseLong(acctAppIds[j]));
					vendorDataSet.add(diameterChargingDriverVendorData);
					Logger.logInfo(MODULE, "vendorData: "+ diameterChargingDriverVendorData);
				}
			}
		}
		return vendorDataSet;
	}
	
	private Set<DiameterChargingDriverPeerData> getPeerDataSet(HttpServletRequest request, int index){
		Set<DiameterChargingDriverPeerData> peerDataSet = new LinkedHashSet<DiameterChargingDriverPeerData>();
		DiameterChargingDriverPeerData diameterChargingDriverPeerData = null;
		
		String peerName[] = request.getParameterValues("peerNamepeerTable"+index);
		String communicationPort[] = request.getParameterValues("communicationPortpeerTable"+index);
		String attemptConnection[] = request.getParameterValues("attemptConnectionpeerTable"+index);
		String watchdogInterval[] = request.getParameterValues("watchdogIntervalpeerTable"+index);

		if( watchdogInterval!=null && watchdogInterval.length > 0){
			for (int j = 0; j < peerName.length; j++) {
				if(peerName[j]!=null && peerName[j].trim().length()>0){
					diameterChargingDriverPeerData = new DiameterChargingDriverPeerData();
					diameterChargingDriverPeerData.setName(peerName[j]);
					diameterChargingDriverPeerData.setCommunicationPort(Long.parseLong(communicationPort[j]));
					diameterChargingDriverPeerData.setAttemptConnection(attemptConnection[j]);
					diameterChargingDriverPeerData.setWatchDogInterval(Long.parseLong(watchdogInterval[j]));
					peerDataSet.add(diameterChargingDriverPeerData);
					Logger.logInfo(MODULE, "peerData: "+ diameterChargingDriverPeerData);
				}
			}
		}
		return peerDataSet;
	}
}
