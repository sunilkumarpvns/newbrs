package com.elitecore.netvertexsm.web.servermgr.spinterface.dbinterface;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.corenetvertex.spr.data.SPRFields;
import com.elitecore.netvertexsm.blmanager.datasource.DatabaseDSBLManager;
import com.elitecore.netvertexsm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.datasource.database.data.DatabaseDSData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.LogicalNameValuePoolData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.servermgr.spinterface.dbinterface.form.DBSPInterfaceForm;

public class InitCreateDBSPInterfaceAction extends BaseWebAction{
	private static final String CREATE_FORWARD = "createDBSPInterface";
	private static final String ACTION_ALIAS =ConfigConstant.CREATE_SP_INTERFACE;
	private static final long maxQuertTimeOutCount = 50L;
	private static final long queryTimeOut = 1L;
	
	private static String [] logicalNames = {SPRFields.USERNAME.displayName,
		SPRFields.PASSWORD.displayName,
		SPRFields.CUSTOMER_TYPE.displayName,
		SPRFields.DATA_PACKAGE.displayName,
		SPRFields.IMS_PACKAGE.displayName,
		SPRFields.EXPIRY_DATE.displayName,
		SPRFields.CUI.displayName,
		SPRFields.IMSI.displayName,
		SPRFields.SUBSCRIBER_IDENTITY.displayName,
		SPRFields.PARENT_ID.displayName};
	
	private static String [] logicalValues = {SPRFields.USERNAME.name(),
		SPRFields.PASSWORD.name(),
		SPRFields.CUSTOMER_TYPE.name(),
		SPRFields.DATA_PACKAGE.name(),
		SPRFields.IMS_PACKAGE.name(),
		SPRFields.EXPIRY_DATE.name(),
		SPRFields.CUI.name(),
		SPRFields.IMSI.name(),
		SPRFields.SUBSCRIBER_IDENTITY.name(),
		SPRFields.PARENT_ID.name()};
	
	private static String [] fieldNames = {SPRFields.USERNAME.columnName,
		SPRFields.PASSWORD.columnName,
		SPRFields.CUSTOMER_TYPE.columnName,
		SPRFields.DATA_PACKAGE.columnName,
		SPRFields.IMS_PACKAGE.columnName,
		SPRFields.EXPIRY_DATE.columnName,
		SPRFields.CUI.columnName,
		SPRFields.IMSI.columnName,
		SPRFields.SUBSCRIBER_IDENTITY.columnName,
		SPRFields.PARENT_ID.columnName};
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logTrace(MODULE,"Enter the execute method of :"+getClass().getName());
		
		if((checkAccess(request, ACTION_ALIAS))){
			try{
				DBSPInterfaceForm dbSPInterfaceForm = (DBSPInterfaceForm) form;
				DriverBLManager driverBLManager = new DriverBLManager();
				DatabaseDSBLManager databaseDSBLManager = new DatabaseDSBLManager();												
				setDefaultValues(dbSPInterfaceForm);	
				List<DatabaseDSData> databaseDSList = databaseDSBLManager.getDatabaseDSList();				
				List<LogicalNameValuePoolData> logicalNamePoolList = driverBLManager.getLogicalNamePoolList();
				dbSPInterfaceForm.setDatabaseDSList(databaseDSList);				
				dbSPInterfaceForm.setLogicalNamePoolList(logicalNamePoolList);
				          
				request.setAttribute("logicalNames", logicalNames);
				request.setAttribute("fieldNames",fieldNames);
				request.setAttribute("logicalValues",logicalValues);
				
				return mapping.findForward(CREATE_FORWARD);
				
			}catch(DataManagerException managerExp){
				managerExp.printStackTrace();
				Logger.logTrace(MODULE,"Error during data Manager operation, reason :"+managerExp.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("spinterface.opencreatepage.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
			}catch(Exception exp){
				exp.printStackTrace();
				Logger.logTrace(MODULE,"Error during data Manager operation, reason :"+exp.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(exp);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("spinterface.opencreatepage.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
			}
			return mapping.findForward(FAILURE);
		}else{
	        Logger.logError(MODULE, "Error during Data Manager operation ");
	        ActionMessage message = new ActionMessage("general.user.restricted");
	        ActionMessages messages = new ActionMessages();
	        messages.add("information", message);
	        saveErrors(request, messages);
			
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
	private void setDefaultValues(DBSPInterfaceForm form ){
		if(form != null){
			form.setDbQueryTimeout(queryTimeOut);
			form.setMaxQueryTimeoutCnt(maxQuertTimeOutCount);
			form.setTableName("TBLM_SUBSCRIBER");
			form.setIdentityField("IMSI");
		}
	}

}
