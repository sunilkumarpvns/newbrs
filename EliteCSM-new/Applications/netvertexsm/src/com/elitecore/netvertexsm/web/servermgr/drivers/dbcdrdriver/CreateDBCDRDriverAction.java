package com.elitecore.netvertexsm.web.servermgr.drivers.dbcdrdriver;

import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.dbcdrdriver.data.DBCDRDriverData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.dbcdrdriver.data.DBCDRFieldMappingData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.netvertexsm.web.servermgr.drivers.dbcdrdriver.form.DBCDRDriverForm;
import com.elitecore.netvertexsm.web.servermgr.drivers.form.CreateDriverInstanceForm;

public class CreateDBCDRDriverAction extends BaseWebAction{

	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";	
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_DRIVER_ACTION;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		Logger.logTrace(MODULE,"Enter the execute method of :"+getClass().getName());
		if((checkAccess(request, ACTION_ALIAS))){
			Logger.logTrace(MODULE,"Enter the execute method of :"+getClass().getName());
			
			try{
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				DBCDRDriverForm dbcdrDriverForm = (DBCDRDriverForm) form;
				CreateDriverInstanceForm driverInstanceForm =	(CreateDriverInstanceForm)request.getSession().getAttribute("createDriverInstanceForm");
				DriverBLManager driverBLManager = new DriverBLManager();
				DriverInstanceData driverInstanceData = new DriverInstanceData();
				
				String[] pcrfKeyArray = request.getParameterValues("pcrfKeyArray");
				String[] dbFieldArray = request.getParameterValues("dbFieldArray");
				String[] dataTypeArray = request.getParameterValues("dataTypeArray");
				String[] defaultValueArray = request.getParameterValues("defaultValueArray");				
				
				DBCDRDriverData dbcdrDriverData = convertFormToBean(dbcdrDriverForm);
				Set<DBCDRFieldMappingData> dbcdrFieldMappingDataSet = new LinkedHashSet<DBCDRFieldMappingData>();
								
				if(pcrfKeyArray!=null && pcrfKeyArray.length > 0){
					for (int i = 0; i < pcrfKeyArray.length; i++) {
						if(pcrfKeyArray[i]!=null){
							DBCDRFieldMappingData dbcdrFieldMappingData = new DBCDRFieldMappingData();
							dbcdrFieldMappingData.setPcrfKey(pcrfKeyArray[i]);
							dbcdrFieldMappingData.setDbField(dbFieldArray[i]);
							dbcdrFieldMappingData.setDataType(Long.parseLong(dataTypeArray[i]));
							dbcdrFieldMappingData.setDefaultValue(defaultValueArray[i]);
							dbcdrFieldMappingDataSet.add(dbcdrFieldMappingData);
						}
					}					
				}
				dbcdrDriverData.setDbcdrDriverFieldMappingDataSet(dbcdrFieldMappingDataSet);
				
				Date currentDate = new Date();
				driverInstanceData.setCreateDate(new Timestamp(currentDate.getTime()));
				IStaffData staff =getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				driverInstanceData.setCreatedByStaffId( staff.getStaffId());
				
				driverInstanceData.setDescription(driverInstanceForm.getDescription());
				driverInstanceData.setDriverTypeId(driverInstanceForm.getDriverTypeId());
				driverInstanceData.setName(driverInstanceForm.getName());
				driverInstanceData.setStatus(driverInstanceForm.getStatus());
				Set<DBCDRDriverData> dbcdrDriverDataSet = new LinkedHashSet<DBCDRDriverData>();
				dbcdrDriverDataSet.add(dbcdrDriverData);
				driverInstanceData.setDbcdrDriverDataSet(dbcdrDriverDataSet);
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
	private DBCDRDriverData convertFormToBean(DBCDRDriverForm form){
		DBCDRDriverData data = new DBCDRDriverData();
		data.setDatabaseDSID(form.getDatabaseDSID());
		data.setDbQueryTimeout(form.getDbQueryTimeout());
		data.setMaxQueryTimeoutCount(form.getMaxQueryTimeoutCount());
		data.setMaxQueryTimeoutCount(form.getMaxQueryTimeoutCount());
		data.setTableName(form.getTableName());
		data.setIdentityField(form.getIdentityField());
		data.setSequenceName(form.getSequenceName());
		data.setStoreAllCDR(form.getStoreAllCDR());
		data.setIsBatchUpdate(form.getIsBatchUpdate());
		data.setBatchSize(form.getBatchSize());
		data.setBatchUpdateInterval(form.getBatchUpdateInterval());
		data.setQueryTimeout(form.getQueryTimeout());
		data.setSessionIDFieldName(form.getSessionIDFieldName());
		data.setCreateDateFieldName(form.getCreateDateFieldName());
		data.setLastModifiedFieldName(form.getLastModifiedFieldName());
		data.setTimeStampformat(form.getTimeStampformat());
		data.setReportingType(form.getReportingType());
		data.setUsageKeyFieldName(form.getUsageKeyFieldName());
		data.setInputOctetsFieldName(form.getInputOctetsFieldName());
		data.setOutputOctetsFieldName(form.getOutputOctetsFieldName());
		data.setTotalOctetsFieldName(form.getTotalOctetsFieldName());
		data.setUsageTimeFieldName(form.getUsageTimeFieldName());
		return data;
	}
}