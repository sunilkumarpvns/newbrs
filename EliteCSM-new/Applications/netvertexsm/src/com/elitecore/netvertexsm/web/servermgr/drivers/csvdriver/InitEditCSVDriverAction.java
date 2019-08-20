package com.elitecore.netvertexsm.web.servermgr.drivers.csvdriver;

import java.util.Iterator;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.elitecore.netvertexsm.util.PasswordUtility;
import com.elitecore.passwordutil.DecryptionFailedException;
import com.elitecore.passwordutil.DecryptionNotSupportedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.datamanager.servermgr.drivers.csvdriver.data.CSVDriverData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.csvdriver.data.CSVFieldMapData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.csvdriver.data.CSVStripFieldMapData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.servermgr.drivers.csvdriver.form.CSVDriverForm;

public class InitEditCSVDriverAction extends BaseWebAction{
	private static final String FAILURE_FORWARD = "failure";
	private static final String CREATE_FORWARD = "editCSVDriver";
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_DRIVER_ACTION;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logTrace(MODULE,"Enter the execute method of :"+getClass().getName());
		if((checkAccess(request, ACTION_ALIAS))){
			try{
				CSVDriverForm csvDriverForm = (CSVDriverForm) form;
				DriverInstanceData driverInstanceData =(DriverInstanceData)request.getSession().getAttribute("driverInstanceData");
				Set<CSVDriverData> csvDriverDataSet =  driverInstanceData.getCsvDriverDataSet();
				CSVDriverData csvDriverData = null;
				if(csvDriverDataSet!=null && !csvDriverDataSet.isEmpty()){
					for (Iterator<CSVDriverData> iterator = csvDriverDataSet.iterator(); iterator.hasNext();) {
						 csvDriverData =  iterator.next();
					}
				}

				if(csvDriverData!=null){
					Set<CSVFieldMapData> csvFieldSet = csvDriverData.getCsvFieldMapSet();
					Set<CSVStripFieldMapData> csvStripFieldMapDataSet = csvDriverData.getCsvStripFieldMapDataSet();
					csvDriverForm.setCsvFieldMapSet(csvFieldSet);
					csvDriverForm.setCsvStripFieldMapDataSet(csvStripFieldMapDataSet);
					convertBeanToForm(csvDriverData,csvDriverForm);
					
					if(csvFieldSet!=null && !csvFieldSet.isEmpty()){
						String headerArray[] = new String[csvFieldSet.size()];
						String pcrfKeyArray[] = new String[csvFieldSet.size()];
						int i=0;
						for (Iterator<CSVFieldMapData> iterator = csvFieldSet.iterator(); iterator.hasNext();) {
							CSVFieldMapData csvFieldMapData =  iterator.next();
							headerArray[i] = csvFieldMapData.getHeader();
							pcrfKeyArray[i]= csvFieldMapData.getPcrfKey();
							i++;
						}
						request.setAttribute("headerArray", headerArray);
						request.setAttribute("pcrfKeyArray", pcrfKeyArray);
					}
					
					if(csvStripFieldMapDataSet!=null && !csvStripFieldMapDataSet.isEmpty()){
						String stripPcrfKeyArray[] = new String[csvStripFieldMapDataSet.size()];
						String patternArray[] = new String[csvStripFieldMapDataSet.size()];
						String seperatorArray[] = new String[csvStripFieldMapDataSet.size()];
						int i=0;
						for (Iterator<CSVStripFieldMapData> iterator = csvStripFieldMapDataSet.iterator(); iterator.hasNext();) {
							CSVStripFieldMapData csvStripFieldMapData =  iterator.next();
							stripPcrfKeyArray[i] = csvStripFieldMapData.getPcrfKey();
							patternArray[i]= csvStripFieldMapData.getPattern();
							seperatorArray[i]= csvStripFieldMapData.getSeparator();
							i++;
						}
						request.setAttribute("stripPcrfKeyArray", stripPcrfKeyArray);
						request.setAttribute("patternArray", patternArray);
						request.setAttribute("seperatorArray", seperatorArray);
					}					
				}
				request.setAttribute("driverInstanceData",driverInstanceData);								
				return mapping.findForward(CREATE_FORWARD);				
			}catch(Exception managerExp){
				managerExp.printStackTrace();
				Logger.logTrace(MODULE,"Error during data Manager operation, reason :"+managerExp.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
				request.setAttribute("errorDetails", errorElements);
				ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("driver.update.failure"));
	            saveErrors(request, messages);
			}
			
            ActionMessages errorHeadingMessage = new ActionMessages();
            ActionMessage message = new ActionMessage("driver.error.heading","updating");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage);
			return mapping.findForward(FAILURE_FORWARD);
		}else{
	        Logger.logError(MODULE, "Error during Data Manager operation ");
	        ActionMessage message = new ActionMessage("general.user.restricted");
	        ActionMessages messages = new ActionMessages();
	        messages.add("information", message);
	        saveErrors(request, messages);
			
            ActionMessages errorHeadingMessage = new ActionMessages();
            message = new ActionMessage("driver.error.heading","updating");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage);	        
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
	private void convertBeanToForm(CSVDriverData data, CSVDriverForm form) throws DecryptionNotSupportedException, DecryptionFailedException, NoSuchEncryptionException {
		if(data!=null){
			form.setAllocatingProtocol(data.getAllocatingProtocol());
			form.setArchiveLocation(data.getArchiveLocation());
			form.setDefaultDirName(data.getDefaultDirName());
			form.setDelimiter(data.getDelimiter());
			form.setCdrTimestampFormat(data.getCdrTimestampFormat());
			form.setFailOvertime(data.getFailOvertime());
			form.setLocation(data.getFileLocation());
			form.setFileName(data.getFileName());
			form.setFileRollingType(data.getFileRollingType());
			if(data.getFileRollingType()==1){
				form.setRollingUnit(data.getRollingUnit());
			}else{
				form.setRollingUnitOther(data.getRollingUnit());
			}
			form.setFolderName(data.getFolderName());
			form.setGlobalization(data.getGlobalization());
			form.setHeader(data.getHeader());
			form.setAddress(data.getAddress());
			form.setPassword(PasswordUtility.getDecryptedPassword(data.getPassword()));
			form.setPostOperation(data.getPostOperation());
			form.setPrefixFileName(data.getPrefixFileName());
			form.setRange(data.getRange());
			form.setRemoteLocation(data.getRemoteLocation());
			form.setUserName(data.getUserName());
			form.setReportingType(data.getReportingType());
			form.setUsageKeyHeader(data.getUsageKeyHeader());
			form.setInputOctetsHeader(data.getInputOctetsHeader());
			form.setOutputOctetsHeader(data.getOutputOctetsHeader());
			form.setTotalOctetsHeader(data.getTotalOctetsHeader());
			form.setUsageTimeHeader(data.getUsageTimeHeader());
			form.setSequencePosition(data.getSequencePosition());
		}				
	}
}