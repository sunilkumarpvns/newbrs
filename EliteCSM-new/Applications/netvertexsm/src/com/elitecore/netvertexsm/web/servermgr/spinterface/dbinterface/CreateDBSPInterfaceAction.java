package com.elitecore.netvertexsm.web.servermgr.spinterface.dbinterface;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.servermgr.spinterface.SPInterfaceBLManager;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.spinterface.dbinterface.data.DBFieldMapData;
import com.elitecore.netvertexsm.datamanager.servermgr.spinterface.dbinterface.data.DatabaseSPInterfaceData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.netvertexsm.web.servermgr.spinterface.dbinterface.form.DBSPInterfaceForm;
import com.elitecore.netvertexsm.web.servermgr.spinterface.form.CreateSPInterfaceForm;

public class CreateDBSPInterfaceAction extends BaseWebAction {
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";	
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_SP_INTERFACE;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		Logger.logTrace(MODULE,"Enter the execute method of :"+getClass().getName());
		if((checkAccess(request, ACTION_ALIAS))){
			Logger.logTrace(MODULE,"Enter the execute method of :"+getClass().getName());
			
			try{
				DBSPInterfaceForm dbSPInterfaceForm = (DBSPInterfaceForm) form;
				CreateSPInterfaceForm driverInstanceForm =	(CreateSPInterfaceForm)request.getSession().getAttribute("createSPInterfaceForm");
				
				SPInterfaceBLManager driverBLManager = new SPInterfaceBLManager();
				DatabaseSPInterfaceData databaseDriverData = new DatabaseSPInterfaceData();
				DriverInstanceData driverInstanceData = new DriverInstanceData();				
				
				databaseDriverData.setDatabaseDsId(dbSPInterfaceForm.getDatabaseDSId());
				databaseDriverData.setDbQueryTimeout(dbSPInterfaceForm.getDbQueryTimeout());				
				databaseDriverData.setMaxQueryTimeoutCnt(dbSPInterfaceForm.getMaxQueryTimeoutCnt());
				databaseDriverData.setTableName(dbSPInterfaceForm.getTableName());
				databaseDriverData.setIdentityField(dbSPInterfaceForm.getIdentityField());
												
				String[] logicalNames = request.getParameterValues("logicalValues");
				String[] fieldNames = request.getParameterValues("fieldNames");
				
				ArrayList<DBFieldMapData> dbFieldList = new ArrayList<DBFieldMapData>();
				if(logicalNames != null) {
					for(int i=0; i<logicalNames.length; i++) {
						DBFieldMapData dbFieldMapData = new DBFieldMapData();
						dbFieldMapData.setDbField(fieldNames[i]);
						dbFieldMapData.setLogicalName(logicalNames[i]);
						
						dbFieldList.add(dbFieldMapData);						
					}
				}
				
				
				Date currentDate = new Date();
				driverInstanceData.setCreateDate(new Timestamp(currentDate.getTime()));
				IStaffData staff =getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				driverInstanceData.setCreatedByStaffId( staff.getStaffId());
				
				driverInstanceData.setDescription(driverInstanceForm.getDescription());
				driverInstanceData.setDriverTypeId(driverInstanceForm.getDriverTypeId());
				driverInstanceData.setName(driverInstanceForm.getName());
				driverInstanceData.setStatus(driverInstanceForm.getStatus());
				driverInstanceData.setDatabaseDriverData(databaseDriverData);
				driverInstanceData.setDbFieldMapList(dbFieldList);
				               	
				driverBLManager.create(driverInstanceData,staff,ACTION_ALIAS);
				
				ActionMessage message = new ActionMessage("spinterface.create.success", driverInstanceData.getName());
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", message);
	            saveMessages(request,messages);
				request.setAttribute("responseUrl","/initSearchSPInterface.do");
				return mapping.findForward(SUCCESS_FORWARD);
			}catch(DataManagerException managerExp){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,managerExp);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
				request.setAttribute("errorDetails", errorElements);
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("spinterface.create.failure"));
	            saveErrors(request, messages);
	            return mapping.findForward(FAILURE_FORWARD);
			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("spinterface.create.failure"));
	            saveErrors(request, messages);
	            return mapping.findForward(FAILURE_FORWARD);
			}
		}else{
	        Logger.logError(MODULE, "Error during Data Manager operation ");
	        ActionMessage message = new ActionMessage("general.user.restricted");
	        ActionMessages messages = new ActionMessages();
	        messages.add("information", message);
	        saveErrors(request, messages);
			
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
}
