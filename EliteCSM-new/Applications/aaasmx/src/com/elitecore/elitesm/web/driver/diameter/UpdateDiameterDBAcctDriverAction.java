package com.elitecore.elitesm.web.driver.diameter;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.datasource.DatabaseDSBLManager;
import com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.exceptions.sqloperationexception.DatabaseConnectionException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.datasource.database.data.IDatabaseDSData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.dbdriver.data.DBAcctDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.dbdriver.data.DBAcctFeildMapData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.driver.diameter.forms.UpdateDiameterDBAcctDriverForm;

public class UpdateDiameterDBAcctDriverAction extends BaseWebAction{

	private static final String FAILURE_FORWARD = "failure";
	private static final String UPDATE_FORWARD = "viewDiameterDBAcctDriverInstance";
	private static final String INIT_UPDATE_FORWARD = "openUpdateDiameterDBAcctDriver";
	private static final String UPDATE_ACTION_ALIAS = ConfigConstant.UPDATE_DRIVER;
	private static final String VIEW_ACTION_ALIAS = ConfigConstant.VIEW_DRIVER;


	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{

		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		try{
			DBAcctDriverData driverData = new DBAcctDriverData();
			UpdateDiameterDBAcctDriverForm updateDbForm = (UpdateDiameterDBAcctDriverForm)form;
			if("view".equals(updateDbForm.getAction())) {
				checkActionPermission(request, VIEW_ACTION_ALIAS);
			} else {
				checkActionPermission(request, UPDATE_ACTION_ALIAS);
			}	

			DriverBLManager driverBlManager = new DriverBLManager();				
			String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			DatabaseDSBLManager databaseBLManager = new DatabaseDSBLManager();
			List<IDatabaseDSData> databaseDSList = databaseBLManager.getDatabaseDSList();

			if(updateDbForm.getAction() != null && !"view".equals(updateDbForm.getAction())){
				DriverInstanceData driverInstdata = new DriverInstanceData();

				DatabaseDSBLManager databaseDSBLManager = new  DatabaseDSBLManager();
				IDatabaseDSData databaseDSData = databaseDSBLManager.getDatabaseDSDataById(updateDbForm.getDatabaseId());
				updateDbForm.setDatabaseName(databaseDSData.getName());
				
				convertFromBeanToForm(updateDbForm,driverData,driverInstdata);
				driverInstdata.setLastModifiedByStaffId(currentUser);
				driverData.setDbAcctFieldMapList((getDBAcctFeildMapData(request)));

				staffData.setAuditId(driverInstdata.getAuditUId());
				staffData.setAuditName(driverInstdata.getName());
				
				driverBlManager.updateDBAcctDriverById(driverInstdata, driverData,staffData);
				
				return mapping.findForward(UPDATE_FORWARD);
				
			}else{

				DBAcctDriverData dbAcctData = driverBlManager.getDbAcctDriverByDriverInstanceId(updateDbForm.getDriverInstanceId());
				DriverInstanceData driverInstanceData = driverBlManager.getDriverInstanceByDriverInstanceId(updateDbForm.getDriverInstanceId());

				DatabaseDSBLManager databaseDSBLManager = new  DatabaseDSBLManager();
				IDatabaseDSData databaseDSData = databaseDSBLManager.getDatabaseDSDataById(dbAcctData.getDatabaseId());
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
				updateDbForm.setAuditUId(driverInstanceData.getAuditUId());
				
				request.getSession().setAttribute("dbAcctFieldMapList", dbAcctData.getDbAcctFieldMapList());
				request.getSession().setAttribute("driverInstance",driverInstanceData);
				
				return mapping.findForward(INIT_UPDATE_FORWARD);

			}
		}catch(ActionNotPermitedException e){
			Logger.logError(MODULE,"Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}catch (DatabaseConnectionException dc){
			Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
			Logger.logTrace(MODULE,dc);
			request.setAttribute("responseUrl", "/initSearchDriver");
			ActionMessage messageSchemaNotSaved = new ActionMessage("driver.dbacctdriver.connectfailed");
			ActionMessage message = new ActionMessage("driver.update.success");
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			if(messageSchemaNotSaved!=null){
	          	messages.add("warning", messageSchemaNotSaved);
	        }
			saveMessages(request, messages);
			return mapping.findForward(SUCCESS);
		} catch(Exception e){
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


	private void convertFromBeanToForm(UpdateDiameterDBAcctDriverForm updateDbForm,DBAcctDriverData driverData,DriverInstanceData data) {

		driverData.setCallEndFieldName(updateDbForm.getCallEndFieldName());
		driverData.setCallStartFieldName(updateDbForm.getCallStartFieldName());
		driverData.setCdrIdDbField(updateDbForm.getCdrIdDbField());
		driverData.setCdrIdSeqName(updateDbForm.getCdrIdSeqName());
		driverData.setCdrTablename(updateDbForm.getCdrTablename());
		driverData.setCreateDateFieldName(updateDbForm.getCreateDateFieldName());		
		driverData.setDatabaseId(updateDbForm.getDatabaseId());
		//driverData.setDatasourceScantime(updateDbForm.getDatasourceScantime());
		driverData.setDatasourceType(updateDbForm.getDatasourceType());
		driverData.setDbDateField(updateDbForm.getDbDateField());
		driverData.setDbQueryTimeout(updateDbForm.getDbQueryTimeout());						
		driverData.setEnabled(updateDbForm.getEnabled());
		driverData.setInterimCdrIdDbField(updateDbForm.getInterimCdrIdDbField());
		driverData.setInterimCdrIdSeqName(updateDbForm.getInterimCdrIdSeqName());
		driverData.setInterimTablename(updateDbForm.getInterimTablename());
		driverData.setLastModifiedDateFieldName(updateDbForm.getLastModifiedDateFieldName());
		driverData.setMaxQueryTimeoutCount(updateDbForm.getMaxQueryTimeoutCount());
		driverData.setMultivalDelimeter(updateDbForm.getMultivalDelimeter());
		driverData.setOpenDbAcctId(updateDbForm.getOpenDbAcctId());
		driverData.setRemoveInterimOnStop(updateDbForm.getRemoveInterimOnStop());
		driverData.setRemoveTunnelLinkStopRec(updateDbForm.getRemoveTunnelLinkStopRec());
		driverData.setRemoveTunnelStopRec(updateDbForm.getRemoveTunnelStopRec());
		driverData.setStoreInterimRec(updateDbForm.getStoreInterimRec());
		driverData.setStoreStopRec(updateDbForm.getStoreStopRec());
		driverData.setStoreTunnelLinkRejectRec(updateDbForm.getStoreTunnelLinkRejectRec());
		driverData.setStoreTunnelLinkStartRec(updateDbForm.getStoreTunnelLinkStartRec());
		driverData.setStoreTunnelLinkStopRec(updateDbForm.getStoreTunnelLinkStopRec());
		driverData.setStoreTunnelRejectRec(updateDbForm.getStoreTunnelRejectRec());
		driverData.setStoreTunnelStartRec(updateDbForm.getStoreTunnelStartRec());
		driverData.setStoreTunnelStopRec(updateDbForm.getStoreTunnelStopRec());

		// driverInstanceRelated details

		driverData.setDriverInstanceId(updateDbForm.getDriverRelatedId());
		data.setDriverInstanceId(updateDbForm.getDriverRelatedId());
		data.setName(updateDbForm.getDriverInstanceName());
		data.setDescription(updateDbForm.getDriverDesp());
		data.setStatus("CST01");
		data.setLastModifiedDate(getCurrentTimeStemp());
		data.setAuditUId(updateDbForm.getAuditUId());

	}
	private List<DBAcctFeildMapData> getDBAcctFeildMapData(HttpServletRequest request){
		List<DBAcctFeildMapData> dbAcctFeildMapDataList = new ArrayList<DBAcctFeildMapData>();		
		String[] dbFieldList = request.getParameterValues("dbfields");
		String[] attridList = request.getParameterValues("attributeids");
		String[] dataTypeList=request.getParameterValues("datatype");
		String[] useDictionaryValueList=request.getParameterValues("useDictionaryValue");
		String[] defaultVal=request.getParameterValues("defaultvalue");
		if(dbFieldList != null && attridList!=null){
			for(int index=0; index<dbFieldList.length; index++){
				DBAcctFeildMapData dbAcctFeildMapData = new DBAcctFeildMapData();
				dbAcctFeildMapData.setDbfield(dbFieldList[index]);
				dbAcctFeildMapData.setAttributeids((attridList[index]));
				dbAcctFeildMapData.setUseDictionaryValue(useDictionaryValueList[index]);
				dbAcctFeildMapData.setDatatype(dataTypeList[index]);
				dbAcctFeildMapData.setDefaultvalue(defaultVal[index]);
				dbAcctFeildMapDataList.add(dbAcctFeildMapData);
			}
		}
		return dbAcctFeildMapDataList;
	}
}
