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
import com.elitecore.elitesm.blmanager.history.HistoryBLManager;
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
import com.elitecore.elitesm.web.driver.radius.forms.UpdateRadiusClassicCSVAcctDriverForm;
import com.elitecore.elitesm.web.history.DatabaseHistoryData;
import com.elitecore.passwordutil.PasswordEncryption;

public class ViewRadiusClassicCSVAcctDriverHistoryAction extends BaseWebAction{

	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String UPDATE_FORWARD = "viewRadiusClassicCSVAcctDriverInstance";
	private static final String VIEW_HISTORY_FORWARD = "viewRadiusClassicCSVAcctDriverHistory";
	private static final String UPDATE_ACTION_ALIAS = ConfigConstant.UPDATE_DRIVER;
	private static final String VIEW_ACTION_ALIAS = ConfigConstant.VIEW_DRIVER;

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{

		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());		
		try{
			UpdateRadiusClassicCSVAcctDriverForm updateRadiusClassicCSVAcctDriverForm = (UpdateRadiusClassicCSVAcctDriverForm)form;
			if("view".equals(updateRadiusClassicCSVAcctDriverForm.getAction())) {
				checkActionPermission(request, VIEW_ACTION_ALIAS);
			} else {
				checkActionPermission(request, UPDATE_ACTION_ALIAS);
			}

			ActionMessage messageSchemaNotSaved = null;
			DriverBLManager driverBlManager = new DriverBLManager();
			ClassicCSVAcctDriverData classicCsvDriverData = driverBlManager.getClassicCsvDriverByDriverInstanceId(updateRadiusClassicCSVAcctDriverForm.getDriverInstanceId());
			DriverInstanceData driverInstanceData = driverBlManager.getDriverInstanceByDriverInstanceId(updateRadiusClassicCSVAcctDriverForm.getDriverInstanceId());
			String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));

			updateRadiusClassicCSVAcctDriverForm.setAllocatingprotocol(classicCsvDriverData.getAllocatingprotocol());
			updateRadiusClassicCSVAcctDriverForm.setArchivelocation(classicCsvDriverData.getArchivelocation());
			updateRadiusClassicCSVAcctDriverForm.setAvpairseparator(classicCsvDriverData.getAvpairseparator());
			updateRadiusClassicCSVAcctDriverForm.setCreateBlankFile(classicCsvDriverData.getCreateBlankFile());
			updateRadiusClassicCSVAcctDriverForm.setDefaultdirname(classicCsvDriverData.getDefaultdirname());
			updateRadiusClassicCSVAcctDriverForm.setDelimeter(classicCsvDriverData.getDelimeter());
			updateRadiusClassicCSVAcctDriverForm.setEventdateformat(classicCsvDriverData.getEventdateformat());
			updateRadiusClassicCSVAcctDriverForm.setFailovertime(classicCsvDriverData.getFailovertime());
			updateRadiusClassicCSVAcctDriverForm.setFilename(classicCsvDriverData.getFilename());
			updateRadiusClassicCSVAcctDriverForm.setTimeBoundry(classicCsvDriverData.getTimeBoundry());
			updateRadiusClassicCSVAcctDriverForm.setSizeBasedRollingUnit(classicCsvDriverData.getSizeBasedRollingUnit());
			updateRadiusClassicCSVAcctDriverForm.setTimeBasedRollingUnit(classicCsvDriverData.getTimeBasedRollingUnit());
			updateRadiusClassicCSVAcctDriverForm.setRecordBasedRollingUnit(classicCsvDriverData.getRecordBasedRollingUnit());
			updateRadiusClassicCSVAcctDriverForm.setFoldername(classicCsvDriverData.getFoldername());
			updateRadiusClassicCSVAcctDriverForm.setGlobalization(classicCsvDriverData.getGlobalization());
			updateRadiusClassicCSVAcctDriverForm.setHeader(classicCsvDriverData.getHeader());
			updateRadiusClassicCSVAcctDriverForm.setIpaddress(classicCsvDriverData.getIpaddress());
			updateRadiusClassicCSVAcctDriverForm.setLocation(classicCsvDriverData.getLocation());
			updateRadiusClassicCSVAcctDriverForm.setMultivaluedelimeter(classicCsvDriverData.getMultivaluedelimeter());
			updateRadiusClassicCSVAcctDriverForm.setPattern(classicCsvDriverData.getPattern());
			updateRadiusClassicCSVAcctDriverForm.setPostoperation(classicCsvDriverData.getPostoperation());
			updateRadiusClassicCSVAcctDriverForm.setPrefixfilename(classicCsvDriverData.getPrefixfilename());
			updateRadiusClassicCSVAcctDriverForm.setCdrtimestampFormat(classicCsvDriverData.getCdrtimestampFormat());
			updateRadiusClassicCSVAcctDriverForm.setRange(classicCsvDriverData.getRange());
			updateRadiusClassicCSVAcctDriverForm.setRemotelocation(classicCsvDriverData.getRemotelocation());
			updateRadiusClassicCSVAcctDriverForm.setUsedictionaryvalue(classicCsvDriverData.getUsedictionaryvalue());
			updateRadiusClassicCSVAcctDriverForm.setUsername(classicCsvDriverData.getUsername());
			updateRadiusClassicCSVAcctDriverForm.setPassword(classicCsvDriverData.getPassword());
			updateRadiusClassicCSVAcctDriverForm.setEnclosingCharacter(classicCsvDriverData.getEnclosingCharacter());
			//updateClassicCsvDriverForm.setWriteattributes(classicCsvDriverData.getWriteattributes());

			updateRadiusClassicCSVAcctDriverForm.setDriverInstanceName(driverInstanceData.getName());
			updateRadiusClassicCSVAcctDriverForm.setDriverInstanceDesp(driverInstanceData.getDescription());
			updateRadiusClassicCSVAcctDriverForm.setDriverRelatedId(String.valueOf(driverInstanceData.getDriverInstanceId()));
			updateRadiusClassicCSVAcctDriverForm.setAuditUId(driverInstanceData.getAuditUId());	
			
			/* Decrypt server password */
			String decryptedPassword = PasswordEncryption.getInstance().decrypt(updateRadiusClassicCSVAcctDriverForm.getPassword(),PasswordEncryption.ELITE_PASSWORD_CRYPT);
			updateRadiusClassicCSVAcctDriverForm.setPassword(decryptedPassword);
				
			HistoryBLManager historyBlManager= new HistoryBLManager();
			String strDriverInstanceId = request.getParameter("driverInstanceId");
			String driverInstanceId = strDriverInstanceId;
			if(driverInstanceId == null){
				driverInstanceId = updateRadiusClassicCSVAcctDriverForm.getDriverInstanceId();
			}

			String strAuditUid = request.getParameter("auditUid");
			String strSytemAuditId=request.getParameter("systemAuditId");
			String name=request.getParameter("name");
				
			if(strSytemAuditId != null){
				request.setAttribute("systemAuditId", strSytemAuditId);
			}
				
			if(driverInstanceId != null && Strings.isNullOrBlank(strAuditUid) == false){
					
				staffData.setAuditName(driverInstanceData.getName());
				staffData.setAuditId(driverInstanceData.getAuditUId());
				
				List<DatabaseHistoryData> lstDatabaseDSHistoryDatas=historyBlManager.getHistoryData(strAuditUid);
					
				request.setAttribute("name", name);
				request.setAttribute("lstDatabaseDSHistoryDatas", lstDatabaseDSHistoryDatas);
			}
				
			request.getSession().setAttribute("driverInstance",driverInstanceData);
			request.getSession().setAttribute("classicCsvAttrRelSet", classicCsvDriverData.getCsvAttrRelList());
			request.getSession().setAttribute("classicCsvPattRelSet", classicCsvDriverData.getCsvPattRelList());
			
			return mapping.findForward(VIEW_HISTORY_FORWARD);

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


	private void convertFromBeanToForm(UpdateRadiusClassicCSVAcctDriverForm classicCsvForm,ClassicCSVAcctDriverData driverData,DriverInstanceData driverInstdata) {

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
		driverData.setSizeBasedRollingUnit(classicCsvForm.getSizeBasedRollingUnit());
		driverData.setTimeBasedRollingUnit(classicCsvForm.getTimeBasedRollingUnit());
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
		//driverData.setWriteattributes(classicCsvForm.getWriteattributes());
		driverData.setDriverInstanceId(classicCsvForm.getDriverRelatedId());
		driverData.setEnclosingCharacter(classicCsvForm.getEnclosingCharacter());
		// driver related 
		driverInstdata.setName(classicCsvForm.getDriverInstanceName());
		driverInstdata.setDescription(classicCsvForm.getDriverInstanceDesp());
		driverInstdata.setLastModifiedDate(getCurrentTimeStemp());
		driverInstdata.setDriverInstanceId(classicCsvForm.getDriverRelatedId());

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
		String[] defaultValue=request.getParameterValues("defaultvalue");
		if(headerList != null && attridList!=null && useDicValList!=null){
			for(int index=0; index<headerList.length; index++){
				ClassicCSVAttrRelationData classicCSVAttrRelationData = new ClassicCSVAttrRelationData();
				classicCSVAttrRelationData.setHeader(headerList[index]);
				classicCSVAttrRelationData.setAttributeids(attridList[index]);
				classicCSVAttrRelationData.setUsedictionaryvalue(useDicValList[index]);
				classicCSVAttrRelationData.setDefaultvalue(defaultValue[index]);
				classicCSVStripPattRelDataList.add(classicCSVAttrRelationData);
			}
		}
		return classicCSVStripPattRelDataList;
	}
	
}