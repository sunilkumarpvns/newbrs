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
import com.elitecore.elitesm.datamanager.core.exceptions.server.DuplicateParameterFoundExcpetion;
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
import com.elitecore.elitesm.web.driver.radius.forms.CreateRadiusClassicCSVAcctDriverForm;
import com.elitecore.passwordutil.PasswordEncryption;

public class CreateRadiusClassicCSVAcctDriverAction extends BaseWebAction{

	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_DRIVER;
	private static final String MODULE = CreateRadiusClassicCSVAcctDriverAction.class.getSimpleName();
	private static final String CREATE = "cRadiusClassicCSVAcctDriver";

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{

		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		try{
			checkActionPermission(request, ACTION_ALIAS);
			CreateRadiusClassicCSVAcctDriverForm classicCsvDriverForm = (CreateRadiusClassicCSVAcctDriverForm)form;
			String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
			DriverBLManager driverBLManager = new DriverBLManager();

			if(classicCsvDriverForm.getAction() != null && classicCsvDriverForm.getAction().equals("create")){

				// getting the values of strip pattern mapping
				
				ClassicCSVAcctDriverData classicCSVData = new ClassicCSVAcctDriverData();
				DriverInstanceData driverInstanceData = new DriverInstanceData();

				convertFromFormToBean(classicCsvDriverForm,classicCSVData,driverInstanceData);
				driverInstanceData.setLastModifiedByStaffId(currentUser);
				driverInstanceData.setCreatedByStaffId(currentUser);

				/* Encrypt server password */
				String encryptedPassword = PasswordEncryption.getInstance().crypt(classicCSVData.getPassword(),PasswordEncryption.ELITE_PASSWORD_CRYPT);
				classicCSVData.setPassword(encryptedPassword);
				
				classicCSVData.setCsvAttrRelList(getClassicCSVAttrRelationData(request));
				classicCSVData.setCsvPattRelList(getClassicCSVStripPattRelData(request));
				try{
					IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
					driverBLManager.createClassicCSVAcctDriver(classicCSVData, driverInstanceData, staffData);
					
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
				}
			}
		
			if(classicCsvDriverForm.getDriverRelatedId() == null || classicCsvDriverForm.getDriverInstanceName() == null || classicCsvDriverForm.getDriverInstanceDesp() == null){
				classicCsvDriverForm.setDriverInstanceName((String)request.getAttribute("instanceName"));
				classicCsvDriverForm.setDriverInstanceDesp((String)request.getAttribute("desp"));
				Long driverId =(Long)request.getAttribute("driverId");
				classicCsvDriverForm.setDriverRelatedId(driverId.toString());
			}
			
			request.setAttribute("defaultMapping", classicCsvDriverForm.getDefaultmapping());
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


	private void convertFromFormToBean(CreateRadiusClassicCSVAcctDriverForm form,ClassicCSVAcctDriverData data,DriverInstanceData driverInstanceData) {

		data.setAllocatingprotocol(form.getAllocatingprotocol());
		data.setArchivelocation(form.getArchivelocation());
		data.setAvpairseparator(form.getAvpairseparator());

		data.setCreateBlankFile(form.getCreateBlankFile());

		data.setDefaultdirname(form.getDefaultdirname());
		data.setDelimeter(form.getDelimeter());

		data.setEventdateformat(form.getEventdateformat());

		data.setFailovertime(form.getFailovertime());
		data.setFilename(form.getFilename());
		data.setTimeBoundry(form.getTimeBoundry());
		data.setSizeBasedRollingUnit(form.getSizeBasedRollingUnit());
		data.setRecordBasedRollingUnit(form.getRecordBasedRollingUnit());
		data.setTimeBasedRollingUnit(form.getTimeBasedRollingUnit());
		data.setFoldername(form.getFoldername());

		data.setGlobalization(form.getGlobalization());

		data.setHeader(form.getHeader());

		data.setIpaddress(form.getIpaddress());

		data.setLocation(form.getLocation());

		data.setMultivaluedelimeter(form.getMultivaluedelimeter());

		data.setPattern(form.getPattern());
		data.setPostoperation(form.getPostoperation());
		data.setPrefixfilename(form.getPrefixfilename());
		data.setCdrtimestampFormat(form.getCdrtimestampFormat());

		data.setRange(form.getRange());
		data.setRemotelocation(form.getRemotelocation());
		
		data.setUsedictionaryvalue(form.getUsedictionaryvalue());
		data.setUsername(form.getUsername());
		data.setEnclosingCharacter(form.getEnclosingCharacter());
		data.setPassword(form.getPassword());
		data.setCdrTimestampHeader(form.getCdrTimestampHeader());
		data.setCdrTimestampPosition(form.getCdrTimestampPosition());
		// driverInstanceRelated

		driverInstanceData.setName(form.getDriverInstanceName());
		driverInstanceData.setDescription(form.getDriverInstanceDesp());
		driverInstanceData.setDriverTypeId(Long.parseLong(form.getDriverRelatedId()));
		driverInstanceData.setCreateDate(getCurrentTimeStemp());
		driverInstanceData.setLastModifiedDate(getCurrentTimeStemp());		
		driverInstanceData.setStatus("CST01");

	}     
	

	
	private List<ClassicCSVAttrRelationData> getClassicCSVAttrRelationData(HttpServletRequest request){
		List<ClassicCSVAttrRelationData> classicCSVAttrRelationDataList = new ArrayList<ClassicCSVAttrRelationData>();
		String[] headerList = request.getParameterValues("headerval");
		String[] attridList = request.getParameterValues("attributeids");
		String[] useDicValList = request.getParameterValues("usedictionaryvalue");
		String[] defaultvalue = request.getParameterValues("defaultValue");
		if(headerList != null && attridList!=null && useDicValList!=null){
			for(int index=0; index<headerList.length; index++){
				ClassicCSVAttrRelationData classicCSVAttrRelationData = new ClassicCSVAttrRelationData();
				classicCSVAttrRelationData.setHeader(headerList[index]);
				classicCSVAttrRelationData.setAttributeids(attridList[index]);
				classicCSVAttrRelationData.setUsedictionaryvalue(useDicValList[index]);
				classicCSVAttrRelationData.setDefaultvalue(defaultvalue[index]);
				classicCSVAttrRelationDataList.add(classicCSVAttrRelationData);
			}
		}
		return classicCSVAttrRelationDataList;
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
}
