package com.elitecore.netvertexsm.web.servermgr.drivers.dbcdrdriver;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.datasource.DatabaseDSBLManager;
import com.elitecore.netvertexsm.datamanager.datasource.database.data.DatabaseDSData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.servermgr.drivers.dbcdrdriver.form.DBCDRDriverForm;

public class InitCreateDBCDRDriverAction extends BaseWebAction{
	private static final String FAILURE_FORWARD = "failure";
	private static final String CREATE_FORWARD = "createDBCDRDriver";
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_DRIVER_ACTION;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logTrace(MODULE,"Enter the execute method of :"+getClass().getName());
		
		if((checkAccess(request, ACTION_ALIAS))){
			try{
				DBCDRDriverForm	dbcdrDriverForm = (DBCDRDriverForm) form;
				DatabaseDSBLManager databaseDSBLManager = new DatabaseDSBLManager();
				List<DatabaseDSData> databaseDSDataList = databaseDSBLManager.getDatabaseDSList();
				dbcdrDriverForm.setDatabaseDSList(databaseDSDataList);
				request.setAttribute("dbCDRDriverForm",dbcdrDriverForm);
				setDefaultValues(dbcdrDriverForm);              											
				return mapping.findForward(CREATE_FORWARD);
			}catch(Exception managerExp){
				managerExp.printStackTrace();
				Logger.logTrace(MODULE,"Error during data Manager operation, reason :"+managerExp.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
				request.setAttribute("errorDetails", errorElements);
			}
            ActionMessages errorHeadingMessage = new ActionMessages();
            ActionMessage message = new ActionMessage("driver.error.heading","creating");
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
            message = new ActionMessage("driver.error.heading","creating");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage);

			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
	
	private void setDefaultValues(DBCDRDriverForm form){
		form.setDbQueryTimeout(2L);
		form.setMaxQueryTimeoutCount(100L);
		form.setTableName("TBLCDR");
		form.setIdentityField("CDRID");
		form.setSequenceName("SEQ_CDR");
		form.setBatchSize(500L);
		form.setBatchUpdateInterval(1L);
		form.setQueryTimeout(10L);
		form.setSessionIDFieldName("SESSIONID");
		form.setCreateDateFieldName("CREATEDATE");
		form.setLastModifiedFieldName("LASTMODIFIEDDATE");
		form.setTimeStampformat("TIMESTAMP");
		form.setUsageKeyFieldName("MONITORINGKEY");
		form.setInputOctetsFieldName("INPUTOCTETS");
		form.setOutputOctetsFieldName("OUTPUTOCTETS");
		form.setTotalOctetsFieldName("TOTALOCTETS");
		form.setUsageTimeFieldName("USAGETIME");
	}
}