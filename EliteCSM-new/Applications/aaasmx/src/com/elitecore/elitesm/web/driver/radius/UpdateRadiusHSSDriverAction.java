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
import com.elitecore.elitesm.web.driver.radius.forms.UpdateRadiusHSSAuthDriverForm;

public class UpdateRadiusHSSDriverAction extends BaseWebAction{


	private static final String OPEN_FORWARD = "openUpdateRadiusHssAuthDriver";
	private static final String FAILURE_FORWARD = "failure";
	private static final String UPDATE_ACTION_ALIAS = ConfigConstant.UPDATE_DRIVER;
	private static final String VIEW_ACTION_ALIAS = ConfigConstant.VIEW_DRIVER;
	private static final String MODULE=UpdateRadiusHSSDriverAction.class.getSimpleName();

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{

		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		try{
			UpdateRadiusHSSAuthDriverForm radiusHSSAuthDriverForm = (UpdateRadiusHSSAuthDriverForm)form;
			
			if("view".equals(radiusHSSAuthDriverForm.getAction())) {
				checkActionPermission(request, VIEW_ACTION_ALIAS);
			} else {
				checkActionPermission(request, UPDATE_ACTION_ALIAS);
			}
			
			DriverBLManager driverBLManager = new DriverBLManager();
			String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			
			DriverInstanceData driverInstanceData = driverBLManager.getDriverInstanceByDriverInstanceId(radiusHSSAuthDriverForm.getDriverInstanceId());
			HssAuthDriverData diameterHSSAuthDriver = driverBLManager.getHSSDriverData(radiusHSSAuthDriverForm.getDriverInstanceId());
			
			List<HssAuthDriverFieldMapData> hssAuthDriverFieldMapDatas = diameterHSSAuthDriver.getHssAuthFieldMapList();
			List<DiameterPeerRelData> diameterPeerRelDatas = diameterHSSAuthDriver.getDiameterPeerRelDataList();
			
			request.setAttribute("driverInstanceData", driverInstanceData);

			if(radiusHSSAuthDriverForm.getAction() != null){
				if(radiusHSSAuthDriverForm.getAction().equals("update")){

					DriverInstanceData updatedDriverInstance = new DriverInstanceData();
					HssAuthDriverData hssAuthDriverData = new  HssAuthDriverData();
					
					convertFromFormToData(radiusHSSAuthDriverForm,updatedDriverInstance,hssAuthDriverData);
					
					driverInstanceData.setLastModifiedDate(getCurrentTimeStemp());
					driverInstanceData.setLastModifiedByStaffId(currentUser);
					driverInstanceData.setDescription(radiusHSSAuthDriverForm.getDriverInstanceDesc());
					
					hssAuthDriverData.setHssAuthFieldMapList(getDbAuthFieldMapData(request));
					hssAuthDriverData.setDiameterPeerRelDataList(getPeerSetData(request));

					// updating in database....
					try{
						staffData.setAuditId(updatedDriverInstance.getAuditUId());
						staffData.setAuditName(updatedDriverInstance.getName());
						
						driverBLManager.updateHSSDataById(updatedDriverInstance, hssAuthDriverData, staffData);
						
						request.setAttribute("responseUrl", "/viewDriverInstance.do?driverInstanceId=" + radiusHSSAuthDriverForm.getDriverInstanceId());
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

			radiusHSSAuthDriverForm.setDriverInstanceName(driverInstanceData.getName());
			radiusHSSAuthDriverForm.setDriverInstanceDesc(driverInstanceData.getDescription());
			radiusHSSAuthDriverForm.setAuditUId(driverInstanceData.getAuditUId());
			radiusHSSAuthDriverForm.setDriverInstanceId(driverInstanceData.getDriverInstanceId());
			radiusHSSAuthDriverForm.setDriverRelatedId(String.valueOf(driverInstanceData.getDriverTypeId()));
			
			convertBeanToForm(diameterHSSAuthDriver,radiusHSSAuthDriverForm);

			radiusHSSAuthDriverForm.setDiameterPeerRelDataList(diameterPeerRelDatas);
			
			DiameterPeerBLManager diameterPeerBLManager=new DiameterPeerBLManager();
			
			List<DiameterPeerData> lstDiamDatas=diameterPeerBLManager.getDiameterPeerList();
			
			List<LogicalNameValuePoolData> nameValuePoolList = driverBLManager.getLogicalNameValuePoolList();
			List<String> logicalNameMultipleAllowList = driverBLManager.getLogicalValueDriverRelList(Long.valueOf(radiusHSSAuthDriverForm.getDriverRelatedId()));
			request.setAttribute("logicalNameData", driverBLManager.getLogicalNameJson(nameValuePoolList,logicalNameMultipleAllowList));
		
			radiusHSSAuthDriverForm.setHssAuthDriverFieldMapDataList(hssAuthDriverFieldMapDatas);
			
			request.getSession().setAttribute("driverInstance",driverInstanceData);
			request.setAttribute("peerList", lstDiamDatas);
			request.setAttribute("diameterHSSAuthDriverForm", radiusHSSAuthDriverForm);
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


	private void convertBeanToForm(HssAuthDriverData diameterHSSAuthDriver,UpdateRadiusHSSAuthDriverForm radiusHSSAuthDriverForm) {
		radiusHSSAuthDriverForm.setHssauthdriverid(diameterHSSAuthDriver.getHssauthdriverid());
		radiusHSSAuthDriverForm.setUserIdentityAttributes(diameterHSSAuthDriver.getUserIdentityAttributes());
		radiusHSSAuthDriverForm.setApplicationid(diameterHSSAuthDriver.getApplicationid());
		radiusHSSAuthDriverForm.setRequesttimeout(diameterHSSAuthDriver.getRequesttimeout());
		radiusHSSAuthDriverForm.setCommandCode(diameterHSSAuthDriver.getCommandCode());
		radiusHSSAuthDriverForm.setNoOfTriplets(diameterHSSAuthDriver.getNoOfTriplets());
		radiusHSSAuthDriverForm.setAdditionalAttributes(diameterHSSAuthDriver.getAdditionalAttributes());
	}


	private void convertFromFormToData(UpdateRadiusHSSAuthDriverForm radiusHSSAuthDriverForm,DriverInstanceData updatedDriverInstance,HssAuthDriverData hssAuthDriverData) {

		updatedDriverInstance.setName(radiusHSSAuthDriverForm.getDriverInstanceName());
		updatedDriverInstance.setDescription(radiusHSSAuthDriverForm.getDriverInstanceDesc());
		updatedDriverInstance.setDriverInstanceId(radiusHSSAuthDriverForm.getDriverInstanceId());
		updatedDriverInstance.setAuditUId(radiusHSSAuthDriverForm.getAuditUId());
		
		hssAuthDriverData.setHssauthdriverid(radiusHSSAuthDriverForm.getHssauthdriverid());
		hssAuthDriverData.setApplicationid(radiusHSSAuthDriverForm.getApplicationid());
		hssAuthDriverData.setRequesttimeout(radiusHSSAuthDriverForm.getRequesttimeout());
		hssAuthDriverData.setDriverInstanceId(radiusHSSAuthDriverForm.getDriverInstanceId());
		hssAuthDriverData.setUserIdentityAttributes(radiusHSSAuthDriverForm.getUserIdentityAttributes());
		hssAuthDriverData.setCommandCode(radiusHSSAuthDriverForm.getCommandCode());
		hssAuthDriverData.setNoOfTriplets(radiusHSSAuthDriverForm.getNoOfTriplets());
		hssAuthDriverData.setAdditionalAttributes(radiusHSSAuthDriverForm.getAdditionalAttributes());
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
					String peerName = "";
					DiameterPeerRelData diametrePeerRelData = new DiameterPeerRelData();
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
