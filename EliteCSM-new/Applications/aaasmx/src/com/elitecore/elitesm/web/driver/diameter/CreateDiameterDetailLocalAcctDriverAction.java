package com.elitecore.elitesm.web.driver.diameter;

import java.util.LinkedHashSet;
import java.util.Set;

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
import com.elitecore.elitesm.datamanager.servermgr.drivers.detailacctlocal.data.DetailLocalAcctDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.detailacctlocal.data.DetailLocalAttrRelationData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.driver.diameter.forms.CreateDiameterDetailLocalAcctDriverForm;
import com.elitecore.passwordutil.PasswordEncryption;

public class CreateDiameterDetailLocalAcctDriverAction extends BaseWebAction{

	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_DRIVER;
	private static final String MODULE = CreateDiameterDetailLocalAcctDriverAction.class.getSimpleName();
	private static final String CREATE = "cDiameterDetailLocalAcctDriver";

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{

		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		try{

			checkActionPermission(request, ACTION_ALIAS);
			CreateDiameterDetailLocalAcctDriverForm detailLocalDriverForm = (CreateDiameterDetailLocalAcctDriverForm)form;
			DetailLocalAttrRelationData detailLocalRealtionalData = new DetailLocalAttrRelationData();
			DriverBLManager driverBlManager = new DriverBLManager(); 
			String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			if(detailLocalDriverForm.getAction() != null){
				if(detailLocalDriverForm.getAction().equals("create")){

					DetailLocalAcctDriverData detailLocalDriverData = new DetailLocalAcctDriverData();
					DriverInstanceData driverInstanceData = new DriverInstanceData();

					convertFromFormToBean(detailLocalDriverForm,detailLocalDriverData,driverInstanceData);
					driverInstanceData.setLastModifiedByStaffId(currentUser);
					driverInstanceData.setCreatedByStaffId(currentUser);
				
					/* Encrypt  password */
					String encryptedPassword = PasswordEncryption.getInstance().crypt(detailLocalDriverData.getPassword(),PasswordEncryption.ELITE_PASSWORD_CRYPT);
					detailLocalDriverData.setPassword(encryptedPassword);
					
					
					detailLocalDriverData.setDetailLocalSet(getDetailLocalAttrRelationData(request));
					try{
						driverBlManager.createDetailLocalAcctDriver(driverInstanceData, detailLocalDriverData);
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

					}catch(DataManagerException dbe){
						Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
						Logger.logTrace(MODULE,dbe);
						Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(dbe);
						request.setAttribute("errorDetails", errorElements);
						ActionMessage message = new ActionMessage("driver.create.failure");
						ActionMessages messages = new ActionMessages();
						messages.add("information",message);
						saveErrors(request,messages);
						return mapping.findForward(FAILURE_FORWARD);
					}
				}		
			}		
			if(detailLocalDriverForm.getDriverRelatedId() == null || detailLocalDriverForm.getDriverInstanceName() == null || detailLocalDriverForm.getDriverInstanceDesp() == null){
				detailLocalDriverForm.setDriverInstanceName((String)request.getAttribute("instanceName"));
				detailLocalDriverForm.setDriverInstanceDesp((String)request.getAttribute("desp"));
				Long driverId =(Long)request.getAttribute("driverId");
				detailLocalDriverForm.setDriverRelatedId(driverId.toString());
			}
			// regarding the feildMappingData 
			return mapping.findForward(CREATE);
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



	private void convertFromFormToBean(CreateDiameterDetailLocalAcctDriverForm detailLocalDriverForm,DetailLocalAcctDriverData detailLocalDriverData,DriverInstanceData driverInstanceData) {

		detailLocalDriverData.setAllocatingProtocol(detailLocalDriverForm.getAllocatingProtocol());
		detailLocalDriverData.setArchiveLocation(detailLocalDriverForm.getArchiveLocation());
		detailLocalDriverData.setAvpairSeperator(detailLocalDriverForm.getAvpairSeperator());
		detailLocalDriverData.setDefaultDirName(detailLocalDriverForm.getDefaultDirName());
		detailLocalDriverData.setEventDateFormat(detailLocalDriverForm.getEventDateFormat());
		detailLocalDriverData.setFailOverTime(detailLocalDriverForm.getFailOverTime());
		detailLocalDriverData.setFileName(detailLocalDriverForm.getFileName());
		detailLocalDriverData.setTimeBoundry(detailLocalDriverForm.getTimeBoundry());
		detailLocalDriverData.setTimeBasedRollingUnit(detailLocalDriverForm.getTimeBasedRollingUnit());
		detailLocalDriverData.setRecordBasedRollingUnit(detailLocalDriverForm.getRecordBasedRollingUnit());
		detailLocalDriverData.setSizeBasedRollingUnit(detailLocalDriverForm.getSizeBasedRollingUnit());
		detailLocalDriverData.setFolderName(detailLocalDriverForm.getFolderName());				
		detailLocalDriverData.setGlobalization(detailLocalDriverForm.getGlobalization());
		detailLocalDriverData.setIpaddress(detailLocalDriverForm.getIpaddress());
		detailLocalDriverData.setLocation(detailLocalDriverForm.getLocation());
		detailLocalDriverData.setPattern(detailLocalDriverForm.getPattern());		
		detailLocalDriverData.setPostOperation(detailLocalDriverForm.getPostOperation());
		detailLocalDriverData.setPrefixFileName(detailLocalDriverForm.getPrefixFileName());
		detailLocalDriverData.setRange(detailLocalDriverForm.getRange());
		detailLocalDriverData.setRemoteLocation(detailLocalDriverForm.getRemoteLocation());
		detailLocalDriverData.setUseDictionaryValue(detailLocalDriverForm.getUseDictionaryValue());
		detailLocalDriverData.setUserName(detailLocalDriverForm.getUserName());
		detailLocalDriverData.setPassword(detailLocalDriverForm.getPassword());
		detailLocalDriverData.setWriteAttributes(detailLocalDriverForm.getWriteAttributes());

		// driverInstanceRelated

		driverInstanceData.setName(detailLocalDriverForm.getDriverInstanceName());
		driverInstanceData.setDescription(detailLocalDriverForm.getDriverInstanceDesp());
		driverInstanceData.setDriverTypeId(Long.parseLong(detailLocalDriverForm.getDriverRelatedId()));
		driverInstanceData.setCreateDate(getCurrentTimeStemp());
		driverInstanceData.setLastModifiedDate(getCurrentTimeStemp());		
		driverInstanceData.setStatus("CST01");

	}  
	private Set<DetailLocalAttrRelationData> getDetailLocalAttrRelationData(HttpServletRequest request){
		Set<DetailLocalAttrRelationData> detailLocalAttrRelationDataList = new LinkedHashSet<DetailLocalAttrRelationData>();		
		String[] attridsValue = request.getParameterValues("attributeidval");
		String[] defaltValues = request.getParameterValues("defaultval");
		String[] useDictionaryValues = request.getParameterValues("usedicval");
		if(attridsValue != null && attridsValue!=null && useDictionaryValues!=null){
			for(int index=0; index<attridsValue.length; index++){
				DetailLocalAttrRelationData attrRelData = new DetailLocalAttrRelationData();
				attrRelData.setAttrIds(attridsValue[index]);
				attrRelData.setDefaultValue(defaltValues[index]);
				attrRelData.setUseDictionaryValue(useDictionaryValues[index]);
				detailLocalAttrRelationDataList.add(attrRelData);
			}
		}
		return detailLocalAttrRelationDataList;
	}

}
