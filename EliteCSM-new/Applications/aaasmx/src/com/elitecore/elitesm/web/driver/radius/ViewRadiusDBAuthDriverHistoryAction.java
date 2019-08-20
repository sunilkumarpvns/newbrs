package com.elitecore.elitesm.web.driver.radius;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.datasource.DatabaseDSBLManager;
import com.elitecore.elitesm.blmanager.history.HistoryBLManager;
import com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.datasource.database.data.IDatabaseDSData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.LogicalNameValuePoolData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.dbdriver.data.DBAuthDriverData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.driver.radius.forms.UpdateRadiusDBAuthDriverForm;
import com.elitecore.elitesm.web.history.DatabaseHistoryData;

public class ViewRadiusDBAuthDriverHistoryAction extends BaseWebAction{


	private static final String VIEW_FORWARD = "viewRadiusDBAuthDriverHistoryDetailAction";
	private static final String MODULE=ViewRadiusDBAuthDriverHistoryAction.class.getSimpleName();
	private static final String VIEW_ACTION_ALIAS = ConfigConstant.VIEW_DRIVER;

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{

		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		try{
			
			UpdateRadiusDBAuthDriverForm updateDbDriverForm = (UpdateRadiusDBAuthDriverForm)form;
			if("view".equals(updateDbDriverForm.getAction())) {
				checkActionPermission(request, VIEW_ACTION_ALIAS);
			} 
			
			DriverBLManager driverBLManager = new DriverBLManager();
			DatabaseDSBLManager databaseBLManager = new DatabaseDSBLManager();
			String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			
			if(updateDbDriverForm.getDriverInstanceId() == null){
				String driverInstanceId = request.getParameter("driverInstanceId");
				updateDbDriverForm.setDriverInstanceId(driverInstanceId);
			}
			
			DriverInstanceData driverInstanceData = driverBLManager.getDriverInstanceByDriverInstanceId(updateDbDriverForm.getDriverInstanceId());
			DBAuthDriverData dbAuthDriverData = driverBLManager.getDBDriverByDriverInstanceId(updateDbDriverForm.getDriverInstanceId());
			List<LogicalNameValuePoolData> logicalNameList = driverBLManager.getLogicalNameValuePoolList();

			// get database list ...
			List<IDatabaseDSData> databaseList = databaseBLManager.getDatabaseDSList();

			if(databaseList != null && dbAuthDriverData!=null){
				for(int i=0;i<databaseList.size();i++){
					IDatabaseDSData tempDatabaseDsData = databaseList.get(i);
					if(tempDatabaseDsData.getDatabaseId() == dbAuthDriverData.getDatabaseId()){
						updateDbDriverForm.setDatabaseName(tempDatabaseDsData.getName());
					}

				}
			}
			if(dbAuthDriverData!=null){
				updateDbDriverForm.setDbAuthId(dbAuthDriverData.getDbAuthId());
			}
			request.getSession().setAttribute("driverInstanceData", driverInstanceData);

			updateDbDriverForm.setDriverInstanceName(driverInstanceData.getName());
			updateDbDriverForm.setDriverInstanceDesc(driverInstanceData.getDescription());
			updateDbDriverForm.setCacheable((new Boolean(driverInstanceData.getCacheable())));
			
			updateDbDriverForm.setDatabaseDSList(databaseList);
			updateDbDriverForm.setDatabaseId(dbAuthDriverData.getDatabaseId());
			updateDbDriverForm.setDbQueryTimeout(dbAuthDriverData.getDbQueryTimeout());
			//updateDbDriverForm.setDbScanTime(dbAuthDriverData.getDbScanTime());
			updateDbDriverForm.setDriverInstanceId(dbAuthDriverData.getDriverInstanceId());
			updateDbDriverForm.setLogicalNameList(logicalNameList);
			updateDbDriverForm.setMaxQueryTimeoutCount(dbAuthDriverData.getMaxQueryTimeoutCount());
			updateDbDriverForm.setTableName(dbAuthDriverData.getTableName());		
			updateDbDriverForm.setProfileLookupColumn(dbAuthDriverData.getProfileLookupColumn());
			updateDbDriverForm.setPrimaryKeyColumn(dbAuthDriverData.getPrimaryKeyColumn());
			updateDbDriverForm.setSequenceName(dbAuthDriverData.getSequenceName());
			updateDbDriverForm.setUserIdentityAttributes(dbAuthDriverData.getUserIdentityAttributes());
			
			List<String> logicalNameMultipleAllowList = driverBLManager.getLogicalValueDriverRelList(driverInstanceData.getDriverTypeId());
			
			HistoryBLManager historyBlManager= new HistoryBLManager();
			String strDriverInstanceId = request.getParameter("driverInstanceId");
			String driverInstanceId = strDriverInstanceId;
			if(driverInstanceId == null){
				driverInstanceId = updateDbDriverForm.getDriverInstanceId();
			}

			String strAuditUid = request.getParameter("auditUid");
			String strSytemAuditId=request.getParameter("systemAuditId");
			String name=request.getParameter("name");
			
			if(strSytemAuditId != null){
				request.setAttribute("systemAuditId", strSytemAuditId);
			}
			
			if(driverInstanceId != null && Strings.isNullOrBlank(strAuditUid) == false){
				
				staffData.setAuditName(driverInstanceData.getName());
				staffData.setAuditId(driverInstanceData.getAuditUId());
				
				List<DatabaseHistoryData> lstDatabaseDSHistoryDatas=historyBlManager.getHistoryData(strAuditUid);
				
				request.setAttribute("name", name);
				request.setAttribute("lstDatabaseDSHistoryDatas", lstDatabaseDSHistoryDatas);
			}
			request.setAttribute("logicalNameData", driverBLManager.getLogicalNameJson(logicalNameList,logicalNameMultipleAllowList));
			request.getSession().setAttribute("dbFieldMapList", dbAuthDriverData.getDbFieldMapList());
			request.getSession().setAttribute("driverInstance",driverInstanceData);
			return mapping.findForward(VIEW_FORWARD);
		}catch(ActionNotPermitedException e){
			Logger.logError(MODULE,"Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}catch(Exception e){
			Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
			Logger.logTrace(MODULE,e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("driver.update.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			saveErrors(request,messages);
			return mapping.findForward(FAILURE);
		}
	}
}
