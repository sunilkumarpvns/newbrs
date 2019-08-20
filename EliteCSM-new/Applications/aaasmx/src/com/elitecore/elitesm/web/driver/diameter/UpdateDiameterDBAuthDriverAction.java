package com.elitecore.elitesm.web.driver.diameter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.datasource.DatabaseDSBLManager;
import com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.elitesm.blmanager.servermgr.drivers.subscriberprofile.database.DatabaseSubscriberProfileBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.exceptions.sqloperationexception.DatabaseConnectionException;
import com.elitecore.elitesm.datamanager.core.exceptions.sqloperationexception.TableDoesNotExistException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.datasource.database.data.IDatabaseDSData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.LogicalNameValuePoolData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.dbdriver.data.DBAuthDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.dbdriver.data.DBAuthFieldMapData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data.IDatasourceSchemaData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.driver.diameter.forms.UpdateDiameterDBAuthDriverForm;

public class UpdateDiameterDBAuthDriverAction extends BaseWebAction{


	private static final String OPEN_FORWARD = "openUpdateDiameterDBAuthDriver";
	private static final String FAILURE_FORWARD = "failure";
	private static final String UPDATE_ACTION_ALIAS = ConfigConstant.UPDATE_DRIVER;
	private static final String VIEW_ACTION_ALIAS = ConfigConstant.VIEW_DRIVER;
	private static final String MODULE=UpdateDiameterDBAuthDriverAction.class.getSimpleName();

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{

		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		try{
			UpdateDiameterDBAuthDriverForm updateDbDriverForm = (UpdateDiameterDBAuthDriverForm)form;
			if("view".equals(updateDbDriverForm.getAction())) {
				checkActionPermission(request, VIEW_ACTION_ALIAS);
			} else {
				checkActionPermission(request, UPDATE_ACTION_ALIAS);
			}
			
			DriverBLManager driverBLManager = new DriverBLManager();
			DatabaseDSBLManager databaseBLManager = new DatabaseDSBLManager();
			String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			DriverInstanceData driverInstanceData = driverBLManager.getDriverInstanceByDriverInstanceId(updateDbDriverForm.getDriverInstanceId());
			DBAuthDriverData dbAuthDriverData = driverBLManager.getDBDriverByDriverInstanceId(updateDbDriverForm.getDriverInstanceId());
			List<LogicalNameValuePoolData> logicalNameList = driverBLManager.getLogicalNameValuePoolList();


			// get database list ...
			List<IDatabaseDSData> databaseList = databaseBLManager.getDatabaseDSList();

			if(databaseList != null && dbAuthDriverData!=null){
				for(int i=0;i<databaseList.size();i++){
					IDatabaseDSData tempDatabaseDsData = databaseList.get(i);
					if(tempDatabaseDsData.getDatabaseId().equals(dbAuthDriverData.getDatabaseId())){
						updateDbDriverForm.setDatabaseName(tempDatabaseDsData.getName());
					}

				}
			}
			if(dbAuthDriverData!=null){
				updateDbDriverForm.setDbAuthId(dbAuthDriverData.getDbAuthId());
			}
			request.setAttribute("driverInstanceData", driverInstanceData);

			if(updateDbDriverForm.getAction() != null){
				if(updateDbDriverForm.getAction().equals("update")){

					DriverInstanceData updatedDriverInstance = new DriverInstanceData();
					DBAuthDriverData updatedDBAuthDriverData = new DBAuthDriverData();

					
					convertFromFormToData(updateDbDriverForm,updatedDriverInstance,updatedDBAuthDriverData);
					Set<IDatasourceSchemaData> datasourceSchemaSet = new HashSet<IDatasourceSchemaData>();


					if(updatedDBAuthDriverData.getDatabaseId()!=dbAuthDriverData.getDatabaseId()){
						try{

							List<IDatasourceSchemaData> datasourceSchemaList = null;
							datasourceSchemaList =driverBLManager.getSchemaList(updatedDBAuthDriverData);
							datasourceSchemaSet.addAll(datasourceSchemaList);
							updatedDBAuthDriverData.setDatasourceSchemaSet(datasourceSchemaSet);


							DatabaseSubscriberProfileBLManager profileBLManager = new DatabaseSubscriberProfileBLManager();
							profileBLManager.updateDatabaseSubscribeProfileSchema(updatedDBAuthDriverData);

						}catch(DatabaseConnectionException e){
							Logger.logDebug(MODULE, "Database Connection Not Found. Not able to add database schema");
						}catch(TableDoesNotExistException e){
							Logger.logDebug(MODULE, "Table or View does not exist. Not able to add database schema");
						}
					}

					updatedDBAuthDriverData.setDbFieldMapList(getDbAuthFieldMapData(request));
					updatedDriverInstance.setLastModifiedDate(getCurrentTimeStemp());
					updatedDriverInstance.setLastModifiedByStaffId(currentUser);

					// updating in database....
					try{
						staffData.setAuditId(updatedDriverInstance.getAuditUId());
						staffData.setAuditName(updatedDriverInstance.getName());
						
						driverBLManager.updateDBAuthDriverById(updatedDriverInstance, updatedDBAuthDriverData,staffData);
						
						request.setAttribute("responseUrl", "/viewDriverInstance.do?driverInstanceId=" + updateDbDriverForm.getDriverInstanceId());
						ActionMessage message = new ActionMessage("driver.update.success");
						ActionMessages messages = new ActionMessages();
						messages.add("information",message);
						saveMessages(request, messages);
						return mapping.findForward(SUCCESS);

					} catch(DatabaseConnectionException e){
						ActionMessage messageSchemaNotSaved = new ActionMessage("driver.dbauthdriver.connectfailed");
						Logger.logDebug(MODULE, "Database Connection Not Found. Not able to add database schema");
						request.setAttribute("responseUrl", "/initSearchDriver");
						ActionMessage message = new ActionMessage("driver.create.success");
						ActionMessages messages = new ActionMessages();
						messages.add("information",message);
						if(messageSchemaNotSaved!=null){
				          	messages.add("warning", messageSchemaNotSaved);
				        }
						saveMessages(request, messages);
						return mapping.findForward(SUCCESS);
					} catch(TableDoesNotExistException e){
						ActionMessage messageSchemaNotSaved = new ActionMessage("driver.dbauthdriver.tablenotfound",updatedDBAuthDriverData.getTableName());					
						Logger.logDebug(MODULE, "Table or View does not exist. Not able to add database schema");
						request.setAttribute("responseUrl", "/initSearchDriver");
						ActionMessage message = new ActionMessage("driver.create.success");
						ActionMessages messages = new ActionMessages();
						messages.add("information",message);
						if(messageSchemaNotSaved!=null){
				          	messages.add("warning", messageSchemaNotSaved);
				        }
						saveMessages(request, messages);
						return mapping.findForward(SUCCESS);
					}catch(DataManagerException e){

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

			updateDbDriverForm.setDriverInstanceName(driverInstanceData.getName());
			updateDbDriverForm.setDriverInstanceDesc(driverInstanceData.getDescription());
			updateDbDriverForm.setAuditUId(driverInstanceData.getAuditUId());
			
			updateDbDriverForm.setDatabaseDSList(databaseList);
			updateDbDriverForm.setDatabaseId(dbAuthDriverData.getDatabaseId());
			updateDbDriverForm.setDbQueryTimeout(dbAuthDriverData.getDbQueryTimeout());
			//updateDbDriverForm.setDbScanTime(dbAuthDriverData.getDbScanTime());
			updateDbDriverForm.setDriverInstanceId(dbAuthDriverData.getDriverInstanceId());
			updateDbDriverForm.setLogicalNameList(logicalNameList);
			updateDbDriverForm.setMaxQueryTimeoutCount(dbAuthDriverData.getMaxQueryTimeoutCount());
			updateDbDriverForm.setTableName(dbAuthDriverData.getTableName());		
			updateDbDriverForm.setUserIdentityAttributes(dbAuthDriverData.getUserIdentityAttributes());	
			updateDbDriverForm.setProfileLookupColumn(dbAuthDriverData.getProfileLookupColumn());

			List<String> logicalNameMultipleAllowList = driverBLManager.getLogicalValueDriverRelList(driverInstanceData.getDriverTypeId());
			request.setAttribute("logicalNameData", driverBLManager.getLogicalNameJson(logicalNameList,logicalNameMultipleAllowList));
			request.getSession().setAttribute("dbFieldMapList", dbAuthDriverData.getDbFieldMapList());
			request.getSession().setAttribute("driverInstance",driverInstanceData);
			return mapping.findForward(OPEN_FORWARD);

		}catch(ActionNotPermitedException e){
			Logger.logError(MODULE,"Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
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


	private void convertFromFormToData(UpdateDiameterDBAuthDriverForm updateDbDriverForm,DriverInstanceData updatedDriverInstance,DBAuthDriverData updatedDBAuthDriverData) {

		updatedDriverInstance.setName(updateDbDriverForm.getDriverInstanceName());
		updatedDriverInstance.setDescription(updateDbDriverForm.getDriverInstanceDesc());
		updatedDriverInstance.setDriverInstanceId(updateDbDriverForm.getDriverInstanceId());
		updatedDriverInstance.setAuditUId(updateDbDriverForm.getAuditUId());
		
		updatedDBAuthDriverData.setDatabaseId(updateDbDriverForm.getDatabaseId());
		updatedDBAuthDriverData.setDbQueryTimeout(updateDbDriverForm.getDbQueryTimeout());
		updatedDBAuthDriverData.setDbAuthId(updateDbDriverForm.getDbAuthId());
		//updatedDBAuthDriverData.setDbScanTime(updateDbDriverForm.getDbScanTime());
		updatedDBAuthDriverData.setMaxQueryTimeoutCount(updateDbDriverForm.getMaxQueryTimeoutCount());		
		updatedDBAuthDriverData.setTableName(updateDbDriverForm.getTableName());
		updatedDBAuthDriverData.setDriverInstanceId(updateDbDriverForm.getDriverInstanceId());
		updatedDBAuthDriverData.setUserIdentityAttributes(updateDbDriverForm.getUserIdentityAttributes());
		updatedDBAuthDriverData.setProfileLookupColumn(updateDbDriverForm.getProfileLookupColumn());
	}
	private List<DBAuthFieldMapData> getDbAuthFieldMapData(HttpServletRequest request){
		List<DBAuthFieldMapData> dbAuthFieldMapDataList = new ArrayList<DBAuthFieldMapData>();
		String[] logicalNames = request.getParameterValues("logicalnmVal");
		String[] dbField = request.getParameterValues("dbfieldVal");
		String defaultValues[] = request.getParameterValues("defaultValue");
		String valueMappings[] = request.getParameterValues("valueMapping");
		if(logicalNames != null && dbField!=null && defaultValues!=null && valueMappings!=null){
			for(int index=0; index<logicalNames.length; index++){
				DBAuthFieldMapData dbAuthFieldMapData = new DBAuthFieldMapData();
				dbAuthFieldMapData.setLogicalName(logicalNames[index]);
				dbAuthFieldMapData.setDbField(dbField[index]);
				dbAuthFieldMapData.setDefaultValue(defaultValues[index]);
				dbAuthFieldMapData.setValueMapping(valueMappings[index]);
				dbAuthFieldMapDataList.add(dbAuthFieldMapData);
			}
		}
		return dbAuthFieldMapDataList;
	}

}
