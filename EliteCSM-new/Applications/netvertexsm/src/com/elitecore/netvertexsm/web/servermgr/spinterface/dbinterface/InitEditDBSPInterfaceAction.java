package com.elitecore.netvertexsm.web.servermgr.spinterface.dbinterface;

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
import com.elitecore.netvertexsm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.netvertexsm.datamanager.datasource.database.data.DatabaseDSData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.LogicalNameValuePoolData;
import com.elitecore.netvertexsm.datamanager.servermgr.spinterface.dbinterface.data.DBFieldMapData;
import com.elitecore.netvertexsm.datamanager.servermgr.spinterface.dbinterface.data.DatabaseSPInterfaceData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.servermgr.spinterface.dbinterface.form.DBSPInterfaceForm;

public class InitEditDBSPInterfaceAction extends BaseWebAction{
	private static final String EDIT_DB_SP_INETERFACE_FORWARD = "editDBSPInterface";
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_SP_INTERFACE;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logTrace(MODULE,"Enter the execute method of :"+getClass().getName());
		
		if((checkAccess(request, ACTION_ALIAS))){
			try{
				DBSPInterfaceForm dbSPInterfaceForm = (DBSPInterfaceForm) form;
				DriverInstanceData driverInstanceData =(DriverInstanceData)request.getSession().getAttribute("driverInstanceData");
				Set<DatabaseSPInterfaceData> dbDriverDataSet =  driverInstanceData.getDatabaseSPInterfaceDriverSet();
				DatabaseSPInterfaceData dbDriverData = null;
				if(dbDriverDataSet!=null && !dbDriverDataSet.isEmpty()){
					for (Iterator<DatabaseSPInterfaceData> iterator = dbDriverDataSet.iterator(); iterator.hasNext();) {
						 dbDriverData =  iterator.next();
					}
				}
				DriverBLManager driverBLManager = new DriverBLManager();
				DatabaseDSBLManager databaseDSBLManager = new DatabaseDSBLManager();	
				List<DatabaseDSData> databaseDSList = databaseDSBLManager.getDatabaseDSList();				
				List<LogicalNameValuePoolData> logicalNamePoolList = driverBLManager.getLogicalNamePoolList();
				dbSPInterfaceForm.setDatabaseDSList(databaseDSList);			
				dbSPInterfaceForm.setName(driverInstanceData.getName());
				dbSPInterfaceForm.setDescription(driverInstanceData.getDescription());
				dbSPInterfaceForm.setLogicalNamePoolList(logicalNamePoolList);
				if(dbDriverData!=null){
					Set<DBFieldMapData> dbFieldSet = dbDriverData.getDbFieldMapSet();
					convertBeanToForm(dbDriverData,dbSPInterfaceForm);
					
					if(dbFieldSet!=null && !dbFieldSet.isEmpty()){
						String logicalNameArray[] = new String[dbFieldSet.size()];
						String fieldNameArray[] = new String[dbFieldSet.size()];
						int i=0;
						for (Iterator<DBFieldMapData> iterator = dbFieldSet.iterator(); iterator.hasNext();) {
							DBFieldMapData dbFieldMapData =  iterator.next();
							logicalNameArray[i] = dbFieldMapData.getLogicalName();
							fieldNameArray[i]= dbFieldMapData.getDbField();
							i++;
						}
						request.setAttribute("logicalNameArray", logicalNameArray);
						request.setAttribute("fieldNameArray", fieldNameArray);
					}
					
				}
				request.setAttribute("driverInstanceData",driverInstanceData);
				return mapping.findForward(EDIT_DB_SP_INETERFACE_FORWARD);
				
			}catch(Exception managerExp){
				managerExp.printStackTrace();
				Logger.logTrace(MODULE,"Error during data Manager operation, reason :"+managerExp.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
				request.setAttribute("errorDetails", errorElements);
				ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("spinterface.update.failure"));
	            saveErrors(request, messages);
			}
            ActionMessages errorHeadingMessage = new ActionMessages();
            ActionMessage message = new ActionMessage("driver.error.heading","updating");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage);
			return mapping.findForward(FAILURE);
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
	private void convertBeanToForm(DatabaseSPInterfaceData data, DBSPInterfaceForm form){
		if(data!=null){
			form.setDriverInstanceId(data.getDriverInstanceId());
			form.setDatabaseDSId(data.getDatabaseDsId());
			form.setDatabaseSPInterfaceId(data.getDatabaseSpInterfaceId());
			form.setTableName(data.getTableName());
			form.setDbQueryTimeout(data.getDbQueryTimeout());
			form.setMaxQueryTimeoutCnt(data.getMaxQueryTimeoutCnt());
			form.setDatabaseSPInterfaceId(data.getDatabaseSpInterfaceId());
			form.setIdentityField(data.getIdentityField());
		}
	}
}

