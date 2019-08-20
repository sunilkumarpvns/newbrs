package com.elitecore.elitesm.web.driver.diameter;

import java.util.ArrayList;
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

import com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.csvdriver.data.ClassicCSVAcctDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.csvdriver.data.ClassicCSVAttrRelationData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.csvdriver.data.ClassicCSVStripPattRelData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.driver.diameter.forms.UpdateDiameterClassicCSVAcctDriverForm;
import com.elitecore.passwordutil.PasswordEncryption;

public class UpdateDiameterClassicCSVAcctDriverAction extends BaseWebAction{

	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String UPDATE_FORWARD = "viewDiameterClassicCSVAcctDriverInstance";
	private static final String INIT_UPDATE_FORWARD = "openUpdateDiameterClassicCSVAcctDriver";
	private static final String UPDATE_ACTION_ALIAS = ConfigConstant.UPDATE_DRIVER;
	private static final String VIEW_ACTION_ALIAS = ConfigConstant.VIEW_DRIVER;



	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{

		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());		
		try{
			UpdateDiameterClassicCSVAcctDriverForm updateDiameterClassicCSVAcctDriverForm = (UpdateDiameterClassicCSVAcctDriverForm)form;
			if("view".equals(updateDiameterClassicCSVAcctDriverForm.getAction())){
				checkActionPermission(request, VIEW_ACTION_ALIAS);
			} else {
				checkActionPermission(request, UPDATE_ACTION_ALIAS);
			}
			

			DriverBLManager driverBlManager = new DriverBLManager();
			ClassicCSVAcctDriverData classicCsvDriverData = driverBlManager.getClassicCsvDriverByDriverInstanceId(updateDiameterClassicCSVAcctDriverForm.getDriverInstanceId());
			DriverInstanceData driverInstanceData = driverBlManager.getDriverInstanceByDriverInstanceId(updateDiameterClassicCSVAcctDriverForm.getDriverInstanceId());
			String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			if(updateDiameterClassicCSVAcctDriverForm.getAction() != null && !"view".equals(updateDiameterClassicCSVAcctDriverForm.getAction())){
				ClassicCSVAcctDriverData driverData = new ClassicCSVAcctDriverData();
				DriverInstanceData driverInstdata = new DriverInstanceData();

				convertFromBeanToForm(updateDiameterClassicCSVAcctDriverForm,driverData,driverInstanceData);
				driverInstanceData.setLastModifiedByStaffId(currentUser);
				driverData.setMappingList(getClassicCSVAttrRelationData(request));
				driverData.setStripMappingList(getClassicCSVStripPattRelData(request));

				/* Encrypt  password */
				String decryptedPassword = PasswordEncryption.getInstance().crypt(driverData.getPassword(),PasswordEncryption.ELITE_PASSWORD_CRYPT);
				driverData.setPassword(decryptedPassword);
				
				staffData.setAuditId(updateDiameterClassicCSVAcctDriverForm.getAuditUId());
				staffData.setAuditName(updateDiameterClassicCSVAcctDriverForm.getDriverInstanceName());
				
				driverBlManager.updateClassicCsvDriverData(driverInstanceData,driverData,staffData);
				
				return mapping.findForward(UPDATE_FORWARD);
			}else{

				updateDiameterClassicCSVAcctDriverForm.setAllocatingprotocol(classicCsvDriverData.getAllocatingprotocol());
				updateDiameterClassicCSVAcctDriverForm.setArchivelocation(classicCsvDriverData.getArchivelocation());
				updateDiameterClassicCSVAcctDriverForm.setAvpairseparator(classicCsvDriverData.getAvpairseparator());
				updateDiameterClassicCSVAcctDriverForm.setCreateBlankFile(classicCsvDriverData.getCreateBlankFile());
				updateDiameterClassicCSVAcctDriverForm.setDefaultdirname(classicCsvDriverData.getDefaultdirname());
				updateDiameterClassicCSVAcctDriverForm.setDelimeter(classicCsvDriverData.getDelimeter());
				updateDiameterClassicCSVAcctDriverForm.setEventdateformat(classicCsvDriverData.getEventdateformat());
				updateDiameterClassicCSVAcctDriverForm.setFailovertime(classicCsvDriverData.getFailovertime());
				updateDiameterClassicCSVAcctDriverForm.setFilename(classicCsvDriverData.getFilename());
				updateDiameterClassicCSVAcctDriverForm.setTimeBoundry(classicCsvDriverData.getTimeBoundry());
				updateDiameterClassicCSVAcctDriverForm.setTimeBasedRollingUnit(classicCsvDriverData.getTimeBasedRollingUnit());
				updateDiameterClassicCSVAcctDriverForm.setSizeBasedRollingUnit(classicCsvDriverData.getSizeBasedRollingUnit());
				updateDiameterClassicCSVAcctDriverForm.setRecordBasedRollingUnit(classicCsvDriverData.getRecordBasedRollingUnit());
				updateDiameterClassicCSVAcctDriverForm.setFoldername(classicCsvDriverData.getFoldername());
				updateDiameterClassicCSVAcctDriverForm.setGlobalization(classicCsvDriverData.getGlobalization());
				updateDiameterClassicCSVAcctDriverForm.setHeader(classicCsvDriverData.getHeader());
				updateDiameterClassicCSVAcctDriverForm.setIpaddress(classicCsvDriverData.getIpaddress());
				updateDiameterClassicCSVAcctDriverForm.setLocation(classicCsvDriverData.getLocation());
				updateDiameterClassicCSVAcctDriverForm.setMultivaluedelimeter(classicCsvDriverData.getMultivaluedelimeter());
				updateDiameterClassicCSVAcctDriverForm.setPattern(classicCsvDriverData.getPattern());
				updateDiameterClassicCSVAcctDriverForm.setPostoperation(classicCsvDriverData.getPostoperation());
				updateDiameterClassicCSVAcctDriverForm.setPrefixfilename(classicCsvDriverData.getPrefixfilename());
				updateDiameterClassicCSVAcctDriverForm.setCdrtimestampFormat(classicCsvDriverData.getCdrtimestampFormat());
				updateDiameterClassicCSVAcctDriverForm.setRange(classicCsvDriverData.getRange());
				updateDiameterClassicCSVAcctDriverForm.setRemotelocation(classicCsvDriverData.getRemotelocation());
				updateDiameterClassicCSVAcctDriverForm.setUsedictionaryvalue(classicCsvDriverData.getUsedictionaryvalue());
				updateDiameterClassicCSVAcctDriverForm.setUsername(classicCsvDriverData.getUsername());
				updateDiameterClassicCSVAcctDriverForm.setPassword(classicCsvDriverData.getPassword());
				updateDiameterClassicCSVAcctDriverForm.setEnclosingCharacter(classicCsvDriverData.getEnclosingCharacter());
				updateDiameterClassicCSVAcctDriverForm.setCdrTimestampHeader(classicCsvDriverData.getCdrTimestampHeader());
				updateDiameterClassicCSVAcctDriverForm.setCdrTimestampPosition(classicCsvDriverData.getCdrTimestampPosition());
				//updateClassicCsvDriverForm.setWriteattributes(classicCsvDriverData.getWriteattributes());
				updateDiameterClassicCSVAcctDriverForm.setDriverInstanceId(driverInstanceData.getDriverInstanceId());
				updateDiameterClassicCSVAcctDriverForm.setDriverInstanceName(driverInstanceData.getName());
				updateDiameterClassicCSVAcctDriverForm.setDriverInstanceDesp(driverInstanceData.getDescription());
				updateDiameterClassicCSVAcctDriverForm.setDriverRelatedId(String.valueOf(driverInstanceData.getDriverInstanceId()));
				updateDiameterClassicCSVAcctDriverForm.setAuditUId(driverInstanceData.getAuditUId());
				
			
				/* Decrypt  password */
				String decryptedPassword = PasswordEncryption.getInstance().decrypt(updateDiameterClassicCSVAcctDriverForm.getPassword(),PasswordEncryption.ELITE_PASSWORD_CRYPT);
				updateDiameterClassicCSVAcctDriverForm.setPassword(decryptedPassword);
				
				request.getSession().setAttribute("classicCsvAttrRelSet", classicCsvDriverData.getCsvAttrRelList());
				request.getSession().setAttribute("classicCsvPattRelSet", classicCsvDriverData.getCsvPattRelList());

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


	private void convertFromBeanToForm(UpdateDiameterClassicCSVAcctDriverForm classicCsvForm,ClassicCSVAcctDriverData driverData,DriverInstanceData driverInstdata) {

		driverData.setAllocatingprotocol(classicCsvForm.getAllocatingprotocol());
		driverData.setArchivelocation(classicCsvForm.getArchivelocation());
		driverData.setAvpairseparator(classicCsvForm.getAvpairseparator());
		driverData.setCreateBlankFile(classicCsvForm.getCreateBlankFile());
		driverData.setDefaultdirname(classicCsvForm.getDefaultdirname());
		driverData.setDelimeter(classicCsvForm.getDelimeter());
		driverData.setEventdateformat(classicCsvForm.getEventdateformat());
		driverData.setFailovertime(classicCsvForm.getFailovertime());
		driverData.setFilename(classicCsvForm.getFilename());
		driverData.setTimeBoundry(classicCsvForm.getTimeBoundry());
		driverData.setTimeBasedRollingUnit(classicCsvForm.getTimeBasedRollingUnit());
		driverData.setSizeBasedRollingUnit(classicCsvForm.getSizeBasedRollingUnit());
		driverData.setRecordBasedRollingUnit(classicCsvForm.getRecordBasedRollingUnit());
		driverData.setFoldername(classicCsvForm.getFoldername());
		driverData.setGlobalization(classicCsvForm.getGlobalization());
		driverData.setHeader(classicCsvForm.getHeader());
		driverData.setIpaddress(classicCsvForm.getIpaddress());
		driverData.setLocation(classicCsvForm.getLocation());
		driverData.setMultivaluedelimeter(classicCsvForm.getMultivaluedelimeter());
		driverData.setPattern(classicCsvForm.getPattern());
		driverData.setPostoperation(classicCsvForm.getPostoperation());
		driverData.setPrefixfilename(classicCsvForm.getPrefixfilename());
		driverData.setCdrtimestampFormat(classicCsvForm.getCdrtimestampFormat());
		driverData.setRange(classicCsvForm.getRange());
		driverData.setRemotelocation(classicCsvForm.getRemotelocation());
		driverData.setUsedictionaryvalue(classicCsvForm.getUsedictionaryvalue());
		driverData.setUsername(classicCsvForm.getUsername());
		driverData.setPassword(classicCsvForm.getPassword());
		driverData.setEnclosingCharacter(classicCsvForm.getEnclosingCharacter());
		driverData.setCdrTimestampHeader(classicCsvForm.getCdrTimestampHeader());
		driverData.setCdrTimestampPosition(classicCsvForm.getCdrTimestampPosition());
		//driverData.setWriteattributes(classicCsvForm.getWriteattributes());
		driverData.setDriverInstanceId(classicCsvForm.getDriverRelatedId());

		// driver related 
		driverInstdata.setName(classicCsvForm.getDriverInstanceName());
		driverInstdata.setDescription(classicCsvForm.getDriverInstanceDesp());
		driverInstdata.setLastModifiedDate(getCurrentTimeStemp());
		driverInstdata.setDriverInstanceId(classicCsvForm.getDriverRelatedId());
		driverInstdata.setAuditUId(driverInstdata.getAuditUId());

	}
	private List<ClassicCSVStripPattRelData> getClassicCSVStripPattRelData(HttpServletRequest request){
		List<ClassicCSVStripPattRelData> classicCSVStripPattRelDataList = new ArrayList<ClassicCSVStripPattRelData>();		
		String[] attributeStripList = request.getParameterValues("attributestripid");
		String[] patternList = request.getParameterValues("patt");
		String[] separatorList = request.getParameterValues("separator");
		if(attributeStripList != null && patternList!=null && separatorList!=null){
			for(int index=0; index<attributeStripList.length; index++){
				ClassicCSVStripPattRelData classicCSVStripPattRelData = new ClassicCSVStripPattRelData();
				classicCSVStripPattRelData.setAttributeid(attributeStripList[index]);
				classicCSVStripPattRelData.setPattern(patternList[index]);
				classicCSVStripPattRelData.setSeparator(separatorList[index]);
				classicCSVStripPattRelDataList.add(classicCSVStripPattRelData);
			}
		}
		return classicCSVStripPattRelDataList;
	}
	
	private List<ClassicCSVAttrRelationData> getClassicCSVAttrRelationData(HttpServletRequest request){
		List<ClassicCSVAttrRelationData> classicCSVStripPattRelDataList = new ArrayList<ClassicCSVAttrRelationData>();		
		String[] headerList = request.getParameterValues("headerval");
		String[] attridList = request.getParameterValues("attributeids");
		String[] useDicValList = request.getParameterValues("usedictionaryvalue");
		String[] defaultvalue = request.getParameterValues("defaultvalue");
		if(headerList != null && attridList!=null && useDicValList!=null){
			for(int index=0; index<headerList.length; index++){
				ClassicCSVAttrRelationData classicCSVAttrRelationData = new ClassicCSVAttrRelationData();
				classicCSVAttrRelationData.setHeader(headerList[index]);
				classicCSVAttrRelationData.setAttributeids(attridList[index]);
				classicCSVAttrRelationData.setUsedictionaryvalue(useDicValList[index]);
				classicCSVAttrRelationData.setDefaultvalue(defaultvalue[index]);
				classicCSVStripPattRelDataList.add(classicCSVAttrRelationData);
			}
		}
		return classicCSVStripPattRelDataList;
	}
}