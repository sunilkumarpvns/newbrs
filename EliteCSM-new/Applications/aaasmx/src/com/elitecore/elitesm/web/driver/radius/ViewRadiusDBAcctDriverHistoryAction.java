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
import com.elitecore.elitesm.datamanager.servermgr.drivers.dbdriver.data.DBAcctDriverData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.driver.radius.forms.UpdateRadiusDBAcctDriverForm;
import com.elitecore.elitesm.web.history.DatabaseHistoryData;

public class ViewRadiusDBAcctDriverHistoryAction extends BaseWebAction{

	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String UPDATE_FORWARD = "viewRadiusDBAcctDriverInstance";
	private static final String INIT_UPDATE_FORWARD = "viewRadiusDBAcctDriverHistory";
	private static final String UPDATE_ACTION_ALIAS = ConfigConstant.UPDATE_DRIVER;
	private static final String VIEW_ACTION_ALIAS = ConfigConstant.VIEW_DRIVER;


	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{

		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		try{
			UpdateRadiusDBAcctDriverForm updateDbForm = (UpdateRadiusDBAcctDriverForm)form;
			
			if("view".equals(updateDbForm.getAction())) {
				checkActionPermission(request, VIEW_ACTION_ALIAS);
			} else {
				checkActionPermission(request, UPDATE_ACTION_ALIAS);
			}


			DriverBLManager driverBlManager = new DriverBLManager();				
			String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			DatabaseDSBLManager databaseBLManager = new DatabaseDSBLManager();
			List databaseDSList = databaseBLManager.getDatabaseDSList();
			
			if(updateDbForm.getDriverInstanceId() == null){
				String driverInstanceId = request.getParameter("driverInstanceId");
				updateDbForm.setDriverInstanceId(driverInstanceId);
			}
			
			DBAcctDriverData dbAcctData = driverBlManager.getDbAcctDriverByDriverInstanceId(updateDbForm.getDriverInstanceId());
			DriverInstanceData driverInstanceData = driverBlManager.getDriverInstanceByDriverInstanceId(updateDbForm.getDriverInstanceId());

			IDatabaseDSData databaseDSData = databaseBLManager.getDatabaseDSDataById(dbAcctData.getDatabaseId());
			updateDbForm.setDatabaseName(databaseDSData.getName());
			
			updateDbForm.setCallEndFieldName(dbAcctData.getCallEndFieldName());
			updateDbForm.setCallStartFieldName(dbAcctData.getCallStartFieldName());
			updateDbForm.setCdrIdDbField(dbAcctData.getCdrIdDbField());
			updateDbForm.setCdrIdSeqName(dbAcctData.getCdrIdSeqName());
			updateDbForm.setCdrTablename(dbAcctData.getCdrTablename());
			updateDbForm.setCreateDateFieldName(dbAcctData.getCreateDateFieldName());
			updateDbForm.setDatabaseDSList(databaseDSList);
			updateDbForm.setDatabaseId(dbAcctData.getDatabaseId());
			//updateDbForm.setDatasourceScantime(dbAcctData.getDatasourceScantime());
			updateDbForm.setDatasourceType(dbAcctData.getDatasourceType());
			updateDbForm.setDbDateField(dbAcctData.getDbDateField());
			updateDbForm.setDbQueryTimeout(dbAcctData.getDbQueryTimeout());
			updateDbForm.setDriverDesp(driverInstanceData.getDescription());
			updateDbForm.setDriverInstanceName(driverInstanceData.getName());
			updateDbForm.setDriverRelatedId(String.valueOf(updateDbForm.getDriverInstanceId()));
			updateDbForm.setEnabled(dbAcctData.getEnabled());
			updateDbForm.setInterimCdrIdDbField(dbAcctData.getInterimCdrIdDbField());
			updateDbForm.setInterimCdrIdSeqName(dbAcctData.getInterimCdrIdSeqName());
			updateDbForm.setInterimTablename(dbAcctData.getInterimTablename());
			updateDbForm.setLastModifiedDateFieldName(dbAcctData.getLastModifiedDateFieldName());
			updateDbForm.setMaxQueryTimeoutCount(dbAcctData.getMaxQueryTimeoutCount());
			updateDbForm.setMultivalDelimeter(dbAcctData.getMultivalDelimeter());
			updateDbForm.setOpenDbAcctId(dbAcctData.getOpenDbAcctId());
			updateDbForm.setRemoveInterimOnStop(dbAcctData.getRemoveInterimOnStop());
			updateDbForm.setRemoveTunnelLinkStopRec(dbAcctData.getRemoveTunnelLinkStopRec());
			updateDbForm.setRemoveTunnelStopRec(dbAcctData.getRemoveTunnelStopRec());
			updateDbForm.setStoreInterimRec(dbAcctData.getStoreInterimRec());
			updateDbForm.setStoreStopRec(dbAcctData.getStoreStopRec());
			updateDbForm.setStoreTunnelLinkRejectRec(dbAcctData.getStoreTunnelLinkRejectRec());
			updateDbForm.setStoreTunnelLinkStartRec(dbAcctData.getStoreTunnelLinkStartRec());
			updateDbForm.setStoreTunnelLinkStopRec(dbAcctData.getStoreTunnelLinkStopRec());
			updateDbForm.setStoreTunnelRejectRec(dbAcctData.getStoreTunnelRejectRec());
			updateDbForm.setStoreTunnelStartRec(dbAcctData.getStoreTunnelStartRec());
			updateDbForm.setStoreTunnelStopRec(dbAcctData.getStoreTunnelStopRec());
			updateDbForm.setStoreAllCdr(dbAcctData.getStoreAllCdr());
			updateDbForm.setAuditUId(driverInstanceData.getAuditUId());
			
			HistoryBLManager historyBlManager= new HistoryBLManager();
			String strDriverInstanceId = request.getParameter("driverInstanceId");
			String driverInstanceId = strDriverInstanceId;
			if(driverInstanceId == null){
				driverInstanceId = updateDbForm.getDriverInstanceId();
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
			
			request.getSession().setAttribute("dbAcctFieldMapList", dbAcctData.getDbAcctFieldMapList());
			request.getSession().setAttribute("driverInstance",driverInstanceData);
			
			return mapping.findForward(INIT_UPDATE_FORWARD);

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
			return mapping.findForward(FAILURE_FORWARD);
		}
	}
}
