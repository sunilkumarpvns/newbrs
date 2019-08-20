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
import com.elitecore.elitesm.blmanager.servermgr.transmapconf.TranslationMappingConfBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.exceptions.server.DuplicateParameterFoundExcpetion;
import com.elitecore.elitesm.datamanager.core.exceptions.sqloperationexception.DatabaseConnectionException;
import com.elitecore.elitesm.datamanager.core.exceptions.sqloperationexception.TableDoesNotExistException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerData;
import com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerRelData;
import com.elitecore.elitesm.datamanager.servermanager.drivers.hssdriver.data.HssAuthDriverData;
import com.elitecore.elitesm.datamanager.servermanager.drivers.hssdriver.data.HssAuthDriverFieldMapData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.LogicalNameValuePoolData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.driver.CreateDriverConfig;
import com.elitecore.elitesm.web.driver.diameter.forms.CreateDiameterHSSAuthDriverForm;

public class CreateDiameterHSSAuthDriverAction extends BaseWebAction{


	private static final String ACTION_ALIAS = ConfigConstant.CREATE_DRIVER;
	private static final String MODULE = CreateDiameterHSSAuthDriverAction.class.getSimpleName();
	private static final String CREATE = "cDiameterHSSAuthDriver";

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		try{
			checkActionPermission(request, ACTION_ALIAS);
			CreateDiameterHSSAuthDriverForm createDiameterHssAuthDriverForm = (CreateDiameterHSSAuthDriverForm)form;
			String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
			String actionAlias = ACTION_ALIAS;
			DriverBLManager driverBlManager = new DriverBLManager();
			HssAuthDriverData hssAuthDriverData = new HssAuthDriverData();
			DriverInstanceData driverInstanceData = new DriverInstanceData();
			CreateDriverConfig driverConfig = new CreateDriverConfig();
			ActionMessage messageSchemaNotSaved = null;
			if("create".equals(createDiameterHssAuthDriverForm.getAction())){
				try{
					IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
					convertFromFormToData(createDiameterHssAuthDriverForm,hssAuthDriverData,driverInstanceData);

					driverInstanceData.setCreatedByStaffId(currentUser);        	
					driverInstanceData.setLastModifiedDate(getCurrentTimeStemp());
					driverInstanceData.setLastModifiedByStaffId(currentUser);

					hssAuthDriverData.setHssAuthFieldMapList(getDbAuthFieldMapData(request));
					hssAuthDriverData.setDiameterPeerRelDataList(getPeerSetData(request));
					
					driverConfig.setHssAuthDriverData(hssAuthDriverData);					
					driverConfig.setDriverInstanceData(driverInstanceData);

					/*Create table Code*/
					try{
						driverBlManager.createHssAuthDriver(driverConfig, staffData);
						doAuditing(staffData, actionAlias);
					}catch(DatabaseConnectionException e){
						messageSchemaNotSaved = new ActionMessage("driver.dbauthdriver.connectfailed");
						Logger.logDebug(MODULE, "Database Connection Not Found. Not able to add database schema");
					}catch(TableDoesNotExistException e){
						messageSchemaNotSaved = new ActionMessage("driver.dbauthdriver.tablenotfound");					
						Logger.logDebug(MODULE, "Table or View does not exist. Not able to add database schema");
					}
					request.setAttribute("responseUrl", "/initSearchDriver");
					ActionMessage message = new ActionMessage("driver.create.success");
					ActionMessages messages = new ActionMessages();
					messages.add("information",message);
					if(messageSchemaNotSaved!=null){
			          	messages.add("warning", messageSchemaNotSaved);
			        }
					saveMessages(request, messages);
					return mapping.findForward(SUCCESS);
					

				}catch(DuplicateParameterFoundExcpetion dpf){
					Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
					Logger.logTrace(MODULE,dpf);
					Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(dpf);
					request.setAttribute("errorDetails", errorElements);
					ActionMessage message = new ActionMessage("driver.create.duplicate.failure",driverInstanceData.getName());
					ActionMessages messages = new ActionMessages();
					messages.add("information",message);
					saveErrors(request,messages);
					return mapping.findForward(FAILURE);
				}catch(Exception e) {					
					Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
					Logger.logTrace(MODULE,e);
					Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
					request.setAttribute("errorDetails", errorElements);
					ActionMessage message = new ActionMessage("driver.create.failure");
					ActionMessages messages = new ActionMessages();
					messages.add("information",message);
					saveErrors(request,messages);
					return mapping.findForward(FAILURE);
				}

			}else{
				
				TranslationMappingConfBLManager translationMappingConfBLManager=new TranslationMappingConfBLManager();
				List<TranslationMappingConfData> translationMappingList=translationMappingConfBLManager.getTranslationMappingConfigList("TTI0001","TTI0001");
				createDiameterHssAuthDriverForm.setTranslationMappingList(translationMappingList);
				
				DiameterPeerBLManager diameterPeerBLManager=new DiameterPeerBLManager();
				
				List<DiameterPeerData> lstDiamDatas=diameterPeerBLManager.getDiameterPeerList();
				
				
				if(createDiameterHssAuthDriverForm.getDriverRelatedId() == null || createDiameterHssAuthDriverForm.getDriverInstanceName() == null || createDiameterHssAuthDriverForm.getDriverDesp() == null){
					createDiameterHssAuthDriverForm.setDriverInstanceName((String)request.getAttribute("instanceName"));
					createDiameterHssAuthDriverForm.setDriverDesp((String)request.getAttribute("desp"));
					Long driverId =(Long)request.getAttribute("driverId");
					createDiameterHssAuthDriverForm.setDriverRelatedId(driverId.toString());
				}

				// populating list of logical names ....
				List<LogicalNameValuePoolData> nameValuePoolList = driverBlManager.getLogicalNameValuePoolList();
				List<String> logicalNameMultipleAllowList = driverBlManager.getLogicalValueDriverRelList(Long.valueOf(createDiameterHssAuthDriverForm.getDriverRelatedId()));
				request.setAttribute("logicalNameData", driverBlManager.getLogicalNameJson(nameValuePoolList,logicalNameMultipleAllowList));
				request.setAttribute("defaultMapping", createDiameterHssAuthDriverForm.getDefaultmapping());
				request.setAttribute("peerList", lstDiamDatas);
				createDiameterHssAuthDriverForm.setDiameterPeerDatas(lstDiamDatas);
				return mapping.findForward(CREATE); 
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

	public void convertFromFormToData(CreateDiameterHSSAuthDriverForm form,HssAuthDriverData hssAuthDriverData,DriverInstanceData driverInstanceData){

		hssAuthDriverData.setApplicationid(form.getApplicationid());
		hssAuthDriverData.setRequesttimeout(form.getRequesttimeout());
		hssAuthDriverData.setUserIdentityAttributes(form.getUserIdentityAttributes());
		hssAuthDriverData.setCommandCode(form.getCommandCode());
		hssAuthDriverData.setNoOfTriplets(form.getNoOfTriplets());
		hssAuthDriverData.setAdditionalAttributes(form.getAdditionalAttributes());
		
		driverInstanceData.setName(form.getDriverInstanceName());
		driverInstanceData.setDescription(form.getDriverDesp());
		driverInstanceData.setDriverTypeId(Long.parseLong(form.getDriverRelatedId()));		
		driverInstanceData.setCreateDate(getCurrentTimeStemp());
		driverInstanceData.setStatus("CST01");
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
