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
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.detailacctlocal.data.DetailLocalAcctDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.detailacctlocal.data.DetailLocalAttrRelationData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.driver.radius.forms.UpdateRadiusDetailLocalAcctDriverForm;
import com.elitecore.passwordutil.PasswordEncryption;

public class UpdateRadiusDetailLocalAcctDriverAction extends BaseWebAction{

	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	
	private static final String UPDATE_FORWARD = "viewRadiusDetailLocalAcctDriverInstance";
	private static final String INIT_UPDATE_FORWARD = "openUpdateRadiusDetailLocalAcctDriver";
	
	private static final String UPDATE_ACTION_ALIAS = ConfigConstant.UPDATE_DRIVER;
	private static final String VIEW_ACTION_ALIAS = ConfigConstant.VIEW_DRIVER;



	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{

		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		try{


			UpdateRadiusDetailLocalAcctDriverForm updateDetaillocForm = (UpdateRadiusDetailLocalAcctDriverForm)form;
			if("view".equals(updateDetaillocForm.getAction())) {
				checkActionPermission(request, VIEW_ACTION_ALIAS);
			} else {
				checkActionPermission(request, UPDATE_ACTION_ALIAS);
			}

			DriverBLManager driverBlManager = new DriverBLManager();
			DetailLocalAcctDriverData detailLocDriverData = driverBlManager.getDetailLocalDriverByDriverInstanceId(updateDetaillocForm.getDriverInstanceId());
			DriverInstanceData driverInstanceData = driverBlManager.getDriverInstanceByDriverInstanceId(updateDetaillocForm.getDriverInstanceId());
			String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			if(updateDetaillocForm.getAction() != null && !"view".equals(updateDetaillocForm.getAction())){
				DetailLocalAcctDriverData driverData = new DetailLocalAcctDriverData();
				DriverInstanceData driverInstdata = new DriverInstanceData();

				convertFromBeanToForm(updateDetaillocForm,driverData,driverInstdata);
				driverInstdata.setLastModifiedByStaffId(currentUser);
				driverData.setMappingList(getDetailLocalAttrRelationData(request)); 

				/* Encrypt  password */
				String encryptedPassword = PasswordEncryption.getInstance().crypt(driverData.getPassword(),PasswordEncryption.ELITE_PASSWORD_CRYPT);
				driverData.setPassword(encryptedPassword);
				
				staffData.setAuditId(driverInstdata.getAuditUId());
				staffData.setAuditName(driverInstdata.getName());
				
				driverBlManager.updateDetailLocalDriver(driverInstdata, driverData,staffData,UPDATE_ACTION_ALIAS);
				
				return mapping.findForward(UPDATE_FORWARD);
				
			}else{

				updateDetaillocForm.setAllocatingProtocol(detailLocDriverData.getAllocatingProtocol());
				updateDetaillocForm.setArchiveLocation(detailLocDriverData.getArchiveLocation());
				updateDetaillocForm.setAvpairSeperator(detailLocDriverData.getAvpairSeperator());
				updateDetaillocForm.setDefaultDirName(detailLocDriverData.getDefaultDirName());
				updateDetaillocForm.setDetailLocalId(detailLocDriverData.getDetailLocalId());
				updateDetaillocForm.setDriverInstanceDesp(driverInstanceData.getDescription());			
				updateDetaillocForm.setDriverInstanceName(driverInstanceData.getName());			
				updateDetaillocForm.setEventDateFormat(detailLocDriverData.getEventDateFormat());
				updateDetaillocForm.setFailOverTime(detailLocDriverData.getFailOverTime());
				updateDetaillocForm.setFileName(detailLocDriverData.getFileName());			
				updateDetaillocForm.setTimeBoundry(detailLocDriverData.getTimeBoundry());
				updateDetaillocForm.setTimeBasedRollingUnit(detailLocDriverData.getTimeBasedRollingUnit());
				updateDetaillocForm.setRecordBasedRollingUnit(detailLocDriverData.getRecordBasedRollingUnit());
				updateDetaillocForm.setSizeBasedRollingUnit(detailLocDriverData.getSizeBasedRollingUnit());
				updateDetaillocForm.setFolderName(detailLocDriverData.getFolderName());
				updateDetaillocForm.setGlobalization(detailLocDriverData.getGlobalization());
				updateDetaillocForm.setIpaddress(detailLocDriverData.getIpaddress());
				updateDetaillocForm.setLocation(detailLocDriverData.getLocation());
				updateDetaillocForm.setPattern(detailLocDriverData.getPattern());
				updateDetaillocForm.setPostOperation(detailLocDriverData.getPostOperation());
				updateDetaillocForm.setPrefixFileName(detailLocDriverData.getPrefixFileName());
				updateDetaillocForm.setRange(detailLocDriverData.getRange());
				updateDetaillocForm.setRemoteLocation(detailLocDriverData.getRemoteLocation());
				updateDetaillocForm.setUseDictionaryValue(detailLocDriverData.getUseDictionaryValue());
				updateDetaillocForm.setUserName(detailLocDriverData.getUserName());			
				updateDetaillocForm.setWriteAttributes(detailLocDriverData.getWriteAttributes());
				updateDetaillocForm.setPassword(detailLocDriverData.getPassword());
				updateDetaillocForm.setDriverRelatedId(String.valueOf(updateDetaillocForm.getDriverInstanceId()));
				updateDetaillocForm.setAuditUId(driverInstanceData.getAuditUId());
				/* decrypt  password */
				String decryptedPassword = PasswordEncryption.getInstance().decrypt(updateDetaillocForm.getPassword(),PasswordEncryption.ELITE_PASSWORD_CRYPT);
				updateDetaillocForm.setPassword(decryptedPassword);
			
				
				request.getSession().setAttribute("detailLocalDriverDataSet", detailLocDriverData.getDetailLocalSet());
				request.getSession().setAttribute("driverInstance",driverInstanceData);
			
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


	private void convertFromBeanToForm(UpdateRadiusDetailLocalAcctDriverForm form,DetailLocalAcctDriverData data,DriverInstanceData instanceData) {

		data.setAllocatingProtocol(form.getAllocatingProtocol());
		data.setArchiveLocation(form.getArchiveLocation());
		data.setAvpairSeperator(form.getAvpairSeperator());
		data.setDefaultDirName(form.getDefaultDirName());
		data.setDetailLocalId(form.getDetailLocalId());			
		data.setEventDateFormat(form.getEventDateFormat());
		data.setFailOverTime(form.getFailOverTime());
		data.setFileName(form.getFileName());			
		data.setTimeBoundry(form.getTimeBoundry());
		data.setTimeBasedRollingUnit(form.getTimeBasedRollingUnit());
		data.setSizeBasedRollingUnit(form.getSizeBasedRollingUnit());
		data.setRecordBasedRollingUnit(form.getRecordBasedRollingUnit());
		data.setFolderName(form.getFolderName());
		data.setGlobalization(form.getGlobalization());
		data.setIpaddress(form.getIpaddress());
		data.setLocation(form.getLocation());
		data.setPattern(form.getPattern());
		data.setPostOperation(form.getPostOperation());
		data.setPrefixFileName(form.getPrefixFileName());
		data.setRange(form.getRange());
		data.setRemoteLocation(form.getRemoteLocation());
		data.setUseDictionaryValue(form.getUseDictionaryValue());
		data.setUserName(form.getUserName());	
		data.setWriteAttributes(form.getWriteAttributes());
		data.setPassword(form.getPassword());
		// driverInstanceRelated details

		data.setDriverInstanceId(form.getDriverRelatedId());
		instanceData.setDriverInstanceId(form.getDriverRelatedId());
		instanceData.setName(form.getDriverInstanceName());
		instanceData.setDescription(form.getDriverInstanceDesp());
		instanceData.setStatus("CST01");
		instanceData.setLastModifiedDate(getCurrentTimeStemp());
		instanceData.setAuditUId(form.getAuditUId());

	}
	private List<DetailLocalAttrRelationData> getDetailLocalAttrRelationData(HttpServletRequest request){
		List<DetailLocalAttrRelationData> detailLocalAttrRelationDataList = new ArrayList<DetailLocalAttrRelationData>();		
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
