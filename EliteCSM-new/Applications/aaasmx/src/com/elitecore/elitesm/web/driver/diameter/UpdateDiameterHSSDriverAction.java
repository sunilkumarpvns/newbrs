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

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.diameter.diameterpeer.DiameterPeerBLManager;
import com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerData;
import com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerRelData;
import com.elitecore.elitesm.datamanager.servermanager.drivers.hssdriver.data.HssAuthDriverData;
import com.elitecore.elitesm.datamanager.servermanager.drivers.hssdriver.data.HssAuthDriverFieldMapData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.LogicalNameValuePoolData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.driver.diameter.forms.UpdateDiameterHSSAuthDriverForm;

public class UpdateDiameterHSSDriverAction extends BaseWebAction{


	private static final String OPEN_FORWARD = "openUpdateDiameterHssAuthDriver";
	private static final String FAILURE_FORWARD = "failure";
	private static final String UPDATE_ACTION_ALIAS = ConfigConstant.UPDATE_DRIVER;
	private static final String VIEW_ACTION_ALIAS = ConfigConstant.VIEW_DRIVER;
	private static final String MODULE=UpdateDiameterHSSDriverAction.class.getSimpleName();

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{

		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		try{
			UpdateDiameterHSSAuthDriverForm diameterHSSAuthDriverForm = (UpdateDiameterHSSAuthDriverForm)form;
			if("view".equals(diameterHSSAuthDriverForm.getAction())) {
				checkActionPermission(request, VIEW_ACTION_ALIAS);
			} else {
				checkActionPermission(request, UPDATE_ACTION_ALIAS);
			}
			
			DriverBLManager driverBLManager = new DriverBLManager();
			String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			
			DriverInstanceData driverInstanceData = driverBLManager.getDriverInstanceByDriverInstanceId(diameterHSSAuthDriverForm.getDriverInstanceId());
			HssAuthDriverData diameterHSSAuthDriver = driverBLManager.getHSSDriverData(diameterHSSAuthDriverForm.getDriverInstanceId());
			
			List<HssAuthDriverFieldMapData> hssAuthDriverFieldMapDatas = diameterHSSAuthDriver.getHssAuthFieldMapList();
			List<DiameterPeerRelData> diameterPeerRelDatas = diameterHSSAuthDriver.getDiameterPeerRelDataList();
			
			request.setAttribute("driverInstanceData", driverInstanceData);

			if(diameterHSSAuthDriverForm.getAction() != null){
				if(diameterHSSAuthDriverForm.getAction().equals("update")){

					DriverInstanceData updatedDriverInstance = new DriverInstanceData();
					HssAuthDriverData hssAuthDriverData = new  HssAuthDriverData();

					convertFromFormToData(diameterHSSAuthDriverForm,updatedDriverInstance,hssAuthDriverData);

					driverInstanceData.setLastModifiedDate(getCurrentTimeStemp());
					driverInstanceData.setLastModifiedByStaffId(currentUser);
					driverInstanceData.setDescription(diameterHSSAuthDriverForm.getDriverInstanceDesc());
					
					hssAuthDriverData.setHssAuthFieldMapList(getDbAuthFieldMapData(request));
					hssAuthDriverData.setDiameterPeerRelDataList(getPeerSetData(request));

					// updating in database....
					try{
						staffData.setAuditId(updatedDriverInstance.getAuditUId());
						staffData.setAuditName(updatedDriverInstance.getName());
						
						driverBLManager.updateHSSDataById(updatedDriverInstance, hssAuthDriverData, staffData);
						
						request.setAttribute("responseUrl", "/viewDriverInstance.do?driverInstanceId=" + diameterHSSAuthDriverForm.getDriverInstanceId());
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

			diameterHSSAuthDriverForm.setDriverInstanceName(driverInstanceData.getName());
			diameterHSSAuthDriverForm.setDriverInstanceDesc(driverInstanceData.getDescription());
			diameterHSSAuthDriverForm.setAuditUId(driverInstanceData.getAuditUId());
			diameterHSSAuthDriverForm.setDriverInstanceId(driverInstanceData.getDriverInstanceId());
			diameterHSSAuthDriverForm.setDriverRelatedId(String.valueOf(driverInstanceData.getDriverTypeId()));
			
			convertBeanToForm(diameterHSSAuthDriver,diameterHSSAuthDriverForm);

			diameterHSSAuthDriverForm.setDiameterPeerRelDataList(diameterPeerRelDatas);
			
			DiameterPeerBLManager diameterPeerBLManager=new DiameterPeerBLManager();
			
			List<DiameterPeerData> lstDiamDatas=diameterPeerBLManager.getDiameterPeerList();
			
			List<LogicalNameValuePoolData> nameValuePoolList = driverBLManager.getLogicalNameValuePoolList();
			List<String> logicalNameMultipleAllowList = driverBLManager.getLogicalValueDriverRelList(Long.valueOf(diameterHSSAuthDriverForm.getDriverRelatedId()));
			request.setAttribute("logicalNameData", driverBLManager.getLogicalNameJson(nameValuePoolList,logicalNameMultipleAllowList));
		
			diameterHSSAuthDriverForm.setHssAuthDriverFieldMapDataList(hssAuthDriverFieldMapDatas);
			
			request.getSession().setAttribute("driverInstance",driverInstanceData);
			request.setAttribute("peerList", lstDiamDatas);
			request.setAttribute("diameterHSSAuthDriverForm", diameterHSSAuthDriverForm);
			return mapping.findForward(OPEN_FORWARD);

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


	private void convertBeanToForm(HssAuthDriverData diameterHSSAuthDriver,UpdateDiameterHSSAuthDriverForm diameterHSSAuthDriverForm) {
		diameterHSSAuthDriverForm.setHssauthdriverid(diameterHSSAuthDriver.getHssauthdriverid());
		diameterHSSAuthDriverForm.setUserIdentityAttributes(diameterHSSAuthDriver.getUserIdentityAttributes());
		diameterHSSAuthDriverForm.setApplicationid(diameterHSSAuthDriver.getApplicationid());
		diameterHSSAuthDriverForm.setRequesttimeout(diameterHSSAuthDriver.getRequesttimeout());
		diameterHSSAuthDriverForm.setCommandCode(diameterHSSAuthDriver.getCommandCode());
		diameterHSSAuthDriverForm.setNoOfTriplets(diameterHSSAuthDriver.getNoOfTriplets());
		diameterHSSAuthDriverForm.setAdditionalAttributes(diameterHSSAuthDriver.getAdditionalAttributes());
	}


	private void convertFromFormToData(UpdateDiameterHSSAuthDriverForm updateDbDriverForm,DriverInstanceData updatedDriverInstance,HssAuthDriverData hssAuthDriverData) {

		updatedDriverInstance.setName(updateDbDriverForm.getDriverInstanceName());
		updatedDriverInstance.setDescription(updateDbDriverForm.getDriverInstanceDesc());
		updatedDriverInstance.setDriverInstanceId(updateDbDriverForm.getDriverInstanceId());
		updatedDriverInstance.setAuditUId(updateDbDriverForm.getAuditUId());
		
		hssAuthDriverData.setHssauthdriverid(updateDbDriverForm.getHssauthdriverid());
		hssAuthDriverData.setApplicationid(updateDbDriverForm.getApplicationid());
		hssAuthDriverData.setRequesttimeout(updateDbDriverForm.getRequesttimeout());
		hssAuthDriverData.setDriverInstanceId(updateDbDriverForm.getDriverInstanceId());
		hssAuthDriverData.setUserIdentityAttributes(updateDbDriverForm.getUserIdentityAttributes());
		hssAuthDriverData.setCommandCode(updateDbDriverForm.getCommandCode());
		hssAuthDriverData.setNoOfTriplets(updateDbDriverForm.getNoOfTriplets());
		hssAuthDriverData.setAdditionalAttributes(updateDbDriverForm.getAdditionalAttributes());
	}
	
	private List<HssAuthDriverFieldMapData> getDbAuthFieldMapData(HttpServletRequest request){
		List<HssAuthDriverFieldMapData> hssFieldMapDataList = new ArrayList<HssAuthDriverFieldMapData>();
		String[] logicalNames = request.getParameterValues("logicalnmVal");
		String[] dbField = request.getParameterValues("dbfieldVal");
		String defaultValues[] = request.getParameterValues("defaultValue");
		String valueMappings[] = request.getParameterValues("valueMapping");
		if(logicalNames != null && dbField!=null && defaultValues!=null && valueMappings!=null){
			for(int index=0; index<logicalNames.length; index++){
				HssAuthDriverFieldMapData hssFieldMapData = new HssAuthDriverFieldMapData();
				hssFieldMapData.setLogicalName(logicalNames[index]);
				hssFieldMapData.setAttributeIds(dbField[index]);
				hssFieldMapData.setDefaultValue(defaultValues[index]);
				hssFieldMapData.setValueMapping(valueMappings[index]);
				hssFieldMapDataList.add(hssFieldMapData);
			}
		}
		return hssFieldMapDataList;
	}
	
	private List<DiameterPeerRelData> getPeerSetData(HttpServletRequest request) {
		try{
			List<DiameterPeerRelData> diameterPeerRelList = new ArrayList<DiameterPeerRelData>();
			String[] peerIds = request.getParameterValues("selectedPeerIds");
			
			if(peerIds != null){
				for(int index=0; index<peerIds.length; index++){
					String[] peerIdValue =  peerIds[index].split("\\),\\(|\\)|\\(");
					
					String peerUUID = peerIdValue[0];	
					DiameterPeerRelData diametrePeerRelData = new DiameterPeerRelData();
					String peerName = "";
					if(Strings.isNullOrBlank(peerUUID) == true){
						peerUUID=null;
					}else{
						if(Strings.isNullOrBlank(peerUUID) == false){
							DiameterPeerBLManager diameterPeerBlManager = new DiameterPeerBLManager();
							DiameterPeerData diameterPeerData = diameterPeerBlManager.getDiameterPeerById(peerUUID);
							peerName = diameterPeerData.getName();
						}
					}
					diametrePeerRelData.setPeerUUID(peerUUID);
					diametrePeerRelData.setPeerName(peerName);
					diametrePeerRelData.setWeightage(Long.parseLong(peerIdValue[1].trim()));
					diameterPeerRelList.add(diametrePeerRelData);
				}
				return diameterPeerRelList;
			}
		}catch (Exception e) {
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("driver.create.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return null;
	}

}
