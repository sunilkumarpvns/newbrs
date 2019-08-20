package com.elitecore.elitesm.web.driver.radius;

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
import com.elitecore.elitesm.datamanager.core.exceptions.server.DuplicateParameterFoundExcpetion;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.datasource.database.data.IDatabaseDSData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.LogicalNameValuePoolData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.dbdriver.data.DBAuthDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.dbdriver.data.DBAuthFieldMapData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.driver.CreateDriverConfig;
import com.elitecore.elitesm.web.driver.radius.forms.CreateRadiusDBAuthDriverForm;

public class CreateRadiusDBAuthDriverAction extends BaseWebAction{

	private static final String ACTION_ALIAS = ConfigConstant.CREATE_DRIVER;
	private static final String MODULE = CreateRadiusDBAuthDriverAction.class.getSimpleName();
	private static final String CREATE = "cRadiusDBAuthDriver";

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		try{
			checkActionPermission(request, ACTION_ALIAS);
			CreateRadiusDBAuthDriverForm createDBAuthDriverForm = (CreateRadiusDBAuthDriverForm)form;
			String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
			String actionAlias = ACTION_ALIAS;
			DatabaseDSBLManager databaseDSBLManager = new DatabaseDSBLManager();
			DriverBLManager driverBlManager = new DriverBLManager();
			DBAuthDriverData dbAuthDriverData = new DBAuthDriverData();
			DriverInstanceData driverInstanceData = new DriverInstanceData();
			CreateDriverConfig driverConfig = new CreateDriverConfig();
			ActionMessage messageSchemaNotSaved = null;
			if("create".equals(createDBAuthDriverForm.getAction())){
				try{
					IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
					convertFromFormToData(createDBAuthDriverForm,dbAuthDriverData,driverInstanceData);

					driverInstanceData.setCreatedByStaffId(currentUser);        	
					driverInstanceData.setLastModifiedDate(getCurrentTimeStemp());
					driverInstanceData.setLastModifiedByStaffId(currentUser);

					dbAuthDriverData.setDbFieldMapList(getDbAuthFieldMapData(request));

					driverConfig.setDbAuthDriverData(dbAuthDriverData);					
					driverConfig.setDriverInstanceData(driverInstanceData);

					/*Create table Code*/
						String errorMsg = driverBlManager.createDBDriver(driverConfig,staffData);
						if(errorMsg.equals(ConfigConstant.DATABASE_CONNECTION_FAILED)) {
							messageSchemaNotSaved = new ActionMessage("driver.dbauthdriver.connectfailed");
							Logger.logDebug(MODULE, "Database Connection Not Found. Not able to add database schema");
						} else if (errorMsg.equals(ConfigConstant.TABLE_DOES_NOT_EXIST)) {
							messageSchemaNotSaved = new ActionMessage("driver.dbauthdriver.tablenotfound",dbAuthDriverData.getTableName());					
							Logger.logDebug(MODULE, "Table or View does not exist. Not able to add database schema");
						}
					
					request.setAttribute("responseUrl", "/initSearchDriver");
					ActionMessage message = new ActionMessage("driver.create.success");
					ActionMessages messages = new ActionMessages();
					messages.add("information",message);
					if(messageSchemaNotSaved!=null){
			          	messages.add("warning", messageSchemaNotSaved);
			        }
					saveMessages(request, messages);
					return mapping.findForward(SUCCESS);
					

				}catch(DuplicateParameterFoundExcpetion dpf){
					Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
					Logger.logTrace(MODULE,dpf);
					Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(dpf);
					request.setAttribute("errorDetails", errorElements);
					ActionMessage message = new ActionMessage("driver.create.duplicate.failure",driverInstanceData.getName());
					ActionMessages messages = new ActionMessages();
					messages.add("information",message);
					saveErrors(request,messages);
					return mapping.findForward(FAILURE);
				}catch(Exception e) {					
					Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
					Logger.logTrace(MODULE,e);
					Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
					request.setAttribute("errorDetails", errorElements);
					ActionMessage message = new ActionMessage("driver.create.failure");
					ActionMessages messages = new ActionMessages();
					messages.add("information",message);
					saveErrors(request,messages);
					return mapping.findForward(FAILURE);
				}

			}else{
			//// getting list of database 
				List<IDatabaseDSData> databaseList = databaseDSBLManager.getDatabaseDSList();
				createDBAuthDriverForm.setDatabaseDSList(databaseList);

				//setting from request context to form values ......

				if(createDBAuthDriverForm.getDriverRelatedId() == null || createDBAuthDriverForm.getDriverInstanceName() == null || createDBAuthDriverForm.getDriverDesp() == null){
					createDBAuthDriverForm.setDriverInstanceName((String)request.getAttribute("instanceName"));
					createDBAuthDriverForm.setDriverDesp((String)request.getAttribute("desp"));
					Long driverId =(Long)request.getAttribute("driverId");
					createDBAuthDriverForm.setDriverRelatedId(driverId.toString());
				}

				// populating list of logical names ....
				List<LogicalNameValuePoolData> nameValuePoolList = driverBlManager.getLogicalNameValuePoolList();
				List<String> logicalNameMultipleAllowList = driverBlManager.getLogicalValueDriverRelList(Long.valueOf(createDBAuthDriverForm.getDriverRelatedId()));
				request.setAttribute("logicalNameData", driverBlManager.getLogicalNameJson(nameValuePoolList,logicalNameMultipleAllowList));
				request.setAttribute("defaultMapping", createDBAuthDriverForm.getDefaultmapping());
				return mapping.findForward(CREATE); 
			}

		}catch(ActionNotPermitedException e){
            Logger.logError(MODULE,"Error :-" + e.getMessage());
            printPermitedActionAlias(request);
            ActionMessages messages = new ActionMessages();
            messages.add("information", new ActionMessage("general.user.restricted"));
            saveErrors(request, messages);
            return mapping.findForward(INVALID_ACCESS_FORWARD);
		}catch(Exception e){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("driver.create.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE);


	}

	public void convertFromFormToData(CreateRadiusDBAuthDriverForm form,DBAuthDriverData dbAuthDriverData,DriverInstanceData driverInstanceData){

		dbAuthDriverData.setDatabaseId(form.getDatabaseId());
		dbAuthDriverData.setDbQueryTimeout(form.getDbQueryTimeout());
		//dbAuthDriverData.setDbScanTime(form.getDbScanTime());		
		dbAuthDriverData.setMaxQueryTimeoutCount(form.getMaxQueryTimeoutCount());
		dbAuthDriverData.setTableName(form.getTableName());
		dbAuthDriverData.setProfileLookupColumn(form.getProfileLookupColumn());
		dbAuthDriverData.setPrimaryKeyColumn(form.getPrimaryKeyColumn());
		dbAuthDriverData.setSequenceName(form.getSequenceName());
		dbAuthDriverData.setUserIdentityAttributes(form.getUserIdentityAttributes());
		
		driverInstanceData.setCacheable((new Boolean(form.getCacheable())).toString());
		driverInstanceData.setName(form.getDriverInstanceName());
		driverInstanceData.setDescription(form.getDriverDesp());
		driverInstanceData.setDriverTypeId(Long.parseLong(form.getDriverRelatedId()));		
		driverInstanceData.setCreateDate(getCurrentTimeStemp());
		driverInstanceData.setStatus("CST01");
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
