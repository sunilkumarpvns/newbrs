package com.elitecore.netvertexsm.web.servermgr.drivers.dbcdrdriver;

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

import com.elitecore.netvertexsm.blmanager.datasource.DatabaseDSBLManager;
import com.elitecore.netvertexsm.datamanager.datasource.database.data.DatabaseDSData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.dbcdrdriver.data.DBCDRDriverData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.dbcdrdriver.data.DBCDRFieldMappingData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.servermgr.drivers.dbcdrdriver.form.DBCDRDriverForm;

public class InitEditDBCDRDriverAction extends BaseWebAction {
	private static final String FAILURE_FORWARD = "failure";
	private static final String CREATE_FORWARD = "editDBCDRDriver";
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_DRIVER_ACTION;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logTrace(MODULE,"Enter the execute method of :"+getClass().getName());
		if((checkAccess(request, ACTION_ALIAS))){
			try{
				DBCDRDriverForm dbcdrDriverForm = (DBCDRDriverForm) form;
				DriverInstanceData driverInstanceData =(DriverInstanceData)request.getSession().getAttribute("driverInstanceData");
				Set<DBCDRDriverData> dbcdrDriverDataSet =  driverInstanceData.getDbcdrDriverDataSet();
				DatabaseDSBLManager databaseDSBLManager = new DatabaseDSBLManager();
				List<DatabaseDSData> databaseDSDataList = databaseDSBLManager.getDatabaseDSList();
				dbcdrDriverForm.setDatabaseDSList(databaseDSDataList);
				DBCDRDriverData dbcdrDriverData = null;
				if(dbcdrDriverDataSet!=null && !dbcdrDriverDataSet.isEmpty()){
					for (Iterator<DBCDRDriverData> iterator = dbcdrDriverDataSet.iterator(); iterator.hasNext();) {
						 dbcdrDriverData =  iterator.next();
					}
				}

				if(dbcdrDriverData!=null){
					Set<DBCDRFieldMappingData> dbcdrFieldMappingDataSet = dbcdrDriverData.getDbcdrDriverFieldMappingDataSet();
					convertBeanToForm(dbcdrDriverData,dbcdrDriverForm);
					
					if(dbcdrFieldMappingDataSet!=null && !dbcdrFieldMappingDataSet.isEmpty()){
						String[] pcrfKeyArray = new String[dbcdrFieldMappingDataSet.size()];
						String[] dbFieldArray = new String[dbcdrFieldMappingDataSet.size()];
						String[] dataTypeArray = new String[dbcdrFieldMappingDataSet.size()];
						String[] defaultValueArray = new String[dbcdrFieldMappingDataSet.size()];	
						int i=0;
						for (Iterator<DBCDRFieldMappingData> iterator = dbcdrFieldMappingDataSet.iterator(); iterator.hasNext();) {
							DBCDRFieldMappingData dbcdrFieldMappingData =  iterator.next();
							pcrfKeyArray[i] = dbcdrFieldMappingData.getPcrfKey();
							dbFieldArray[i] = dbcdrFieldMappingData.getDbField();
							dataTypeArray[i] = String.valueOf(dbcdrFieldMappingData.getDataType());
							defaultValueArray[i] = dbcdrFieldMappingData.getDefaultValue();
							i++;
						}
						request.setAttribute("pcrfKeyArray", pcrfKeyArray);
						request.setAttribute("dbFieldArray", dbFieldArray);
						request.setAttribute("dataTypeArray", dataTypeArray);
						request.setAttribute("defaultValueArray", defaultValueArray);
					}					
				}
				List<DBCDRFieldMappingData> dbcdrFieldMappingDataList = new ArrayList<DBCDRFieldMappingData>(dbcdrDriverData.getDbcdrDriverFieldMappingDataSet());
				dbcdrDriverForm.setDbcdrFieldMappingDataList(dbcdrFieldMappingDataList);
				request.setAttribute("dbCDRDriverForm",dbcdrDriverForm);
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
	private void convertBeanToForm(DBCDRDriverData data,DBCDRDriverForm form){		
		if(data!=null){
			form.setDatabaseDSID(data.getDatabaseDSID());
			form.setDbQueryTimeout(data.getDbQueryTimeout());
			form.setMaxQueryTimeoutCount(data.getMaxQueryTimeoutCount());
			form.setMaxQueryTimeoutCount(data.getMaxQueryTimeoutCount());
			form.setTableName(data.getTableName());
			form.setIdentityField(data.getIdentityField());
			form.setSequenceName(data.getSequenceName());
			form.setStoreAllCDR(data.getStoreAllCDR());
			form.setIsBatchUpdate(data.getIsBatchUpdate());
			form.setBatchSize(data.getBatchSize());
			form.setBatchUpdateInterval(data.getBatchUpdateInterval()); 
			form.setQueryTimeout(data.getQueryTimeout());
			form.setSessionIDFieldName(data.getSessionIDFieldName());
			form.setCreateDateFieldName(data.getCreateDateFieldName());
			form.setLastModifiedFieldName(data.getLastModifiedFieldName());
			form.setTimeStampformat(data.getTimeStampformat());
			form.setReportingType(data.getReportingType());
			form.setUsageKeyFieldName(data.getUsageKeyFieldName());
			form.setInputOctetsFieldName(data.getInputOctetsFieldName());
			form.setOutputOctetsFieldName(data.getOutputOctetsFieldName());
			form.setTotalOctetsFieldName(data.getTotalOctetsFieldName());
			form.setUsageTimeFieldName(data.getUsageTimeFieldName());
		}				
	}
}