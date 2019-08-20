package com.elitecore.netvertexsm.web.servermgr.drivers.csvdriver;

import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.elitecore.netvertexsm.util.PasswordUtility;
import com.elitecore.passwordutil.EncryptionFailedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.csvdriver.data.CSVDriverData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.csvdriver.data.CSVFieldMapData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.csvdriver.data.CSVStripFieldMapData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.netvertexsm.web.servermgr.drivers.csvdriver.form.CSVDriverForm;
import com.elitecore.netvertexsm.web.servermgr.drivers.form.CreateDriverInstanceForm;

public class CreateCSVDriverAction extends BaseWebAction {
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";	
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_DRIVER_ACTION;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		Logger.logTrace(MODULE,"Enter the execute method of :"+getClass().getName());
		if((checkAccess(request, ACTION_ALIAS))){
			Logger.logTrace(MODULE,"Enter the execute method of :"+getClass().getName());
			
			try{
				CSVDriverForm csvDriverForm = (CSVDriverForm) form;
				CreateDriverInstanceForm driverInstanceForm =	(CreateDriverInstanceForm)request.getSession().getAttribute("createDriverInstanceForm");
				DriverBLManager driverBLManager = new DriverBLManager();
				DriverInstanceData driverInstanceData = new DriverInstanceData();
				
				String[] pcrfKeys = request.getParameterValues("pcrfKeyVal");
				String[] headers = request.getParameterValues("headerVal");
				
				CSVDriverData csvDriverData = convertFormToBean(csvDriverForm);
				Set<CSVFieldMapData> csvFieldMapSet = new LinkedHashSet<CSVFieldMapData>();
								
				if(pcrfKeys!=null && headers!=null && pcrfKeys.length == headers.length){
					for (int i = 0; i < pcrfKeys.length; i++) {
						if(pcrfKeys[i]!=null && pcrfKeys[i].trim().length()>0){
							CSVFieldMapData csvFieldMapData= new CSVFieldMapData();
							csvFieldMapData.setHeader(headers[i]);
							csvFieldMapData.setPcrfKey(pcrfKeys[i]);
							csvFieldMapData.setOrderNumber(i+1);
							csvFieldMapSet.add(csvFieldMapData);
						}
					}					
				}
				csvDriverData.setCsvFieldMapSet(csvFieldMapSet);
				
				String[] stripPcrfKeyArray = request.getParameterValues("stripPcrfKeyArray");
				String[] stripPatternArray = request.getParameterValues("stripPatternArray");
				String[] stripSeperatorArray = request.getParameterValues("stripSeperatorArray");
				
				Set<CSVStripFieldMapData> csvStripFieldMapDataSet = new LinkedHashSet<CSVStripFieldMapData>();
				if(stripPcrfKeyArray != null && stripPcrfKeyArray.length > 0){
					for (int i = 0; i < stripPcrfKeyArray.length; i++) {
						if(stripPcrfKeyArray[i] != null && stripPcrfKeyArray[i].trim().length()>0){
							CSVStripFieldMapData csvStripFieldMapData = new CSVStripFieldMapData();
							csvStripFieldMapData.setPcrfKey(stripPcrfKeyArray[i]);
							csvStripFieldMapData.setPattern(stripPatternArray[i]);
							csvStripFieldMapData.setSeparator(stripSeperatorArray[i]);
							csvStripFieldMapDataSet.add(csvStripFieldMapData);
						}
					}
				}
				csvDriverData.setCsvStripFieldMapDataSet(csvStripFieldMapDataSet);
				
				Date currentDate = new Date();
				driverInstanceData.setCreateDate(new Timestamp(currentDate.getTime()));
				IStaffData staffData =getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				driverInstanceData.setCreatedByStaffId( staffData.getStaffId());
				
				driverInstanceData.setDescription(driverInstanceForm.getDescription());
				driverInstanceData.setDriverTypeId(driverInstanceForm.getDriverTypeId());
				driverInstanceData.setName(driverInstanceForm.getName());
				driverInstanceData.setStatus(driverInstanceForm.getStatus());
				Set<CSVDriverData> csvDriverDataSet = new LinkedHashSet<CSVDriverData>();
				csvDriverDataSet.add(csvDriverData);
				driverInstanceData.setCsvDriverDataSet(csvDriverDataSet);
				driverBLManager.create(driverInstanceData,staffData,ACTION_ALIAS);
				
				ActionMessage message = new ActionMessage("driver.create.success", driverInstanceData.getName());
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", message);
	            saveMessages(request,messages);
				request.setAttribute("responseUrl","/initSearchDriverInstance.do");
				return mapping.findForward(SUCCESS_FORWARD);
			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("driver.create.failure"));
	            saveErrors(request, messages);
	            
	            ActionMessages errorHeadingMessage = new ActionMessages();
	            ActionMessage message = new ActionMessage("driver.error.heading","creating");
	            errorHeadingMessage.add("errorHeading",message);
	            saveMessages(request,errorHeadingMessage);
	            return mapping.findForward(FAILURE_FORWARD);
			}
		}else{
	        Logger.logError(MODULE, "Error during Data Manager operation ");
	        ActionMessage message = new ActionMessage("general.user.restricted");
	        ActionMessages messages = new ActionMessages();
	        messages.add("information", message);
	        saveErrors(request, messages);
			
            ActionMessages errorHeadingMessage = new ActionMessages();
            message = new ActionMessage("driver.error.heading","creating");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
	private CSVDriverData convertFormToBean(CSVDriverForm form) throws NoSuchEncryptionException, EncryptionFailedException {
		CSVDriverData data = new CSVDriverData();
		data.setAllocatingProtocol(form.getAllocatingProtocol());
		data.setArchiveLocation(form.getArchiveLocation());
		data.setDefaultDirName(form.getDefaultDirName());
		data.setDelimiter(form.getDelimiter());
		data.setCdrTimestampFormat(form.getCdrTimestampFormat());
		data.setFailOvertime(form.getFailOvertime());
		data.setFileLocation(form.getLocation());
		data.setFileName(form.getFileName());
		data.setFileRollingType(form.getFileRollingType());
		if(form.getFileRollingType()==1){
		data.setRollingUnit(form.getRollingUnit());
		}else{
			data.setRollingUnit(form.getRollingUnitOther());
		}
		data.setFolderName(form.getFolderName());
		data.setGlobalization(form.getGlobalization());
		data.setHeader(form.getHeader());
		data.setAddress(form.getAddress());
		data.setPassword(PasswordUtility.getEncryptedPassword(form.getPassword()));
		data.setPostOperation(form.getPostOperation());
		data.setPrefixFileName(form.getPrefixFileName());
		data.setRemoteLocation(form.getRemoteLocation());
		data.setSequencePosition(form.getSequencePosition());
		data.setRange(form.getRange());
		data.setUserName(form.getUserName());
		data.setReportingType(form.getReportingType());
		data.setUsageKeyHeader(form.getUsageKeyHeader());
		data.setInputOctetsHeader(form.getInputOctetsHeader());
		data.setOutputOctetsHeader(form.getOutputOctetsHeader());
		data.setTotalOctetsHeader(form.getTotalOctetsHeader());
		data.setUsageTimeHeader(form.getUsageTimeHeader());
		return data;
	}
}