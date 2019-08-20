package com.elitecore.netvertexsm.web.servermgr.spinterface.dbinterface;

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

public class EditDBSPInterfaceAction extends BaseWebAction {
	private static final String ACTION_ALIAS =ConfigConstant.UPDATE_SP_INTERFACE;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		Logger.logTrace(MODULE,"Enter the execute method of :"+getClass().getName());
		if((checkAccess(request, ACTION_ALIAS))){
			Logger.logTrace(MODULE,"Enter the execute method of :"+getClass().getName());
			
			try{
				
				DBSPInterfaceForm dbSPInterfaceDriverForm = (DBSPInterfaceForm) form;
				DriverInstanceData oldDriverInstanceData =(DriverInstanceData)request.getSession().getAttribute("driverInstanceData");
				SPInterfaceBLManager sprDriverBLManager = new SPInterfaceBLManager();
				String[] logicalValues = request.getParameterValues("logicalValues");
				String[] fieldNames = request.getParameterValues("fieldNames");
				DatabaseSPInterfaceData newDatabaseSPInterfaceData = convertFormToBean(dbSPInterfaceDriverForm);
				
				
				Set<DBFieldMapData> dbFieldMapSet = new LinkedHashSet<DBFieldMapData>();
				if(logicalValues != null) {
					for(int i=0; i<logicalValues.length; i++) {
						DBFieldMapData dbFieldMapData = new DBFieldMapData();
						dbFieldMapData.setDbField(fieldNames[i]);
						dbFieldMapData.setLogicalName(logicalValues[i]);
						dbFieldMapSet.add(dbFieldMapData);						
					}
				}
				newDatabaseSPInterfaceData.setDbFieldMapSet(dbFieldMapSet);
				Date currentDate = new Date();
				DriverInstanceData newDriverInstanceData = new DriverInstanceData();
				newDriverInstanceData.setDriverInstanceId(dbSPInterfaceDriverForm.getDriverInstanceId());
				newDriverInstanceData.setName(dbSPInterfaceDriverForm.getName());
				newDriverInstanceData.setDescription(dbSPInterfaceDriverForm.getDescription());
				newDriverInstanceData.setLastModifiedDate(new Timestamp(currentDate.getTime()));
				IStaffData staff =getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				newDriverInstanceData.setLastModifiedByStaffId(staff.getStaffId());
				
				Set<DatabaseSPInterfaceData> dbSPInterfaceDriverSet = new LinkedHashSet<DatabaseSPInterfaceData>();
				dbSPInterfaceDriverSet.add(newDatabaseSPInterfaceData);
				newDriverInstanceData.setDatabaseSPInterfaceDriverSet(dbSPInterfaceDriverSet);
				ActionMessage messageSchemaNotSaved = null;
				
				sprDriverBLManager.updateDBDriver(newDriverInstanceData,staff,ACTION_ALIAS);
				
				ActionMessage message = new ActionMessage("spinterface.update.success", newDriverInstanceData.getName());
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", message);
	            if(messageSchemaNotSaved!=null){
		          	messages.add("warning", messageSchemaNotSaved);
		        }
				saveMessages(request, messages);
				request.setAttribute("responseUrl","/initSearchSPInterface.do");
				return mapping.findForward(SUCCESS);
			}catch(DataManagerException e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("spinterface.update.failure"));
	            saveErrors(request, messages);
	            return mapping.findForward(FAILURE);
			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("spinterface.update.failure"));
	            saveErrors(request, messages);
	            return mapping.findForward(FAILURE);
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
	private DatabaseSPInterfaceData convertFormToBean(DBSPInterfaceForm form){
		DatabaseSPInterfaceData data = new DatabaseSPInterfaceData();
		data.setDatabaseDsId(form.getDatabaseDSId());
		data.setDatabaseSpInterfaceId(form.getDatabaseSPInterfaceId());
		data.setDbQueryTimeout(form.getDbQueryTimeout());
		data.setTableName(form.getTableName());
		data.setMaxQueryTimeoutCnt(form.getMaxQueryTimeoutCnt());
		data.setIdentityField(form.getIdentityField());
		return data;
	}
}
