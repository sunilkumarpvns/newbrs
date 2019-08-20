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
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.exceptions.sqloperationexception.DatabaseConnectionException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.dbdriver.data.DBAcctDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.dbdriver.data.DBAcctFeildMapData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.driver.diameter.forms.CreateDiameterDBAcctDriverForm;

public class CreateDiameterDBAcctDriverAction extends BaseWebAction{

	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_DRIVER;
	private static final String MODULE = CreateDiameterDBAcctDriverAction.class.getSimpleName();
	private static final String DB_ACCT_DRIVER = "cDiameterDBAcctDriver";



	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{

		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		try{
			checkActionPermission(request, ACTION_ALIAS);
			CreateDiameterDBAcctDriverForm dbDriverform = (CreateDiameterDBAcctDriverForm)form;
			DatabaseDSBLManager databaseDSBLManager = new DatabaseDSBLManager();
			DriverBLManager driverBlManager = new DriverBLManager();
			DriverInstanceData driverInstanceData = new DriverInstanceData();
			String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			List databaseList = databaseDSBLManager .getDatabaseDSList();
			dbDriverform.setDatabaseDSList(databaseList);

			if(dbDriverform.getAction() != null){
				String action = dbDriverform.getAction();

				if(action.equals("create")){				
					DBAcctDriverData driverData =  new DBAcctDriverData();
					convertFromFormToData(dbDriverform,driverData,driverInstanceData);
					
					driverData.setDbAcctFieldMapList(getDBAcctFeildMapData(request));
					driverInstanceData.setCreatedByStaffId(currentUser);        	
					driverInstanceData.setLastModifiedDate(getCurrentTimeStemp());
					driverInstanceData.setLastModifiedByStaffId(currentUser);

					try{
						driverBlManager.createDBAcctDriver(driverData, driverInstanceData,staffData);
						request.setAttribute("responseUrl", "/initSearchDriver");
						ActionMessage message = new ActionMessage("driver.create.success");
						ActionMessages messages = new ActionMessages();
						messages.add("information",message);
						saveMessages(request, messages);
						return mapping.findForward(SUCCESS_FORWARD);
					}catch (DatabaseConnectionException dc){
						Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
						Logger.logTrace(MODULE,dc);
						request.setAttribute("responseUrl", "/initSearchDriver");
						ActionMessage messageSchemaNotSaved = new ActionMessage("driver.dbacctdriver.connectfailed");
						ActionMessage message = new ActionMessage("driver.create.success");
						ActionMessages messages = new ActionMessages();
						messages.add("information",message);
						if(messageSchemaNotSaved!=null){
				          	messages.add("warning", messageSchemaNotSaved);
				        }
						saveMessages(request, messages);
						return mapping.findForward(SUCCESS);
					}catch(Exception e){
						Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
						Logger.logTrace(MODULE,e);
						Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
						request.setAttribute("errorDetails", errorElements);
						ActionMessage message = new ActionMessage("driver.create.failure");
						ActionMessages messages = new ActionMessages();
						messages.add("information",message);
						saveErrors(request,messages);
						return mapping.findForward(FAILURE_FORWARD);
					}
				}
			}	

			
			if(dbDriverform.getAction() != null && !dbDriverform.getAction().equals("create")){
				if(dbDriverform.getDriverRelatedId() == null || dbDriverform.getDriverInstanceName() == null || dbDriverform.getDriverDesp() == null){
					dbDriverform.setDriverInstanceName((String)request.getAttribute("instanceName"));
					dbDriverform.setDriverDesp((String)request.getAttribute("desp"));
					Long driverId =(Long)request.getAttribute("driverId");
					dbDriverform.setDriverRelatedId(driverId.toString());
				}

			}
			request.setAttribute("defaultMapping", dbDriverform.getDefaultmapping());
			return mapping.findForward(DB_ACCT_DRIVER);
		}catch (DatabaseConnectionException dc){
			Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
			Logger.logTrace(MODULE,dc);
			ActionMessage messageSchemaNotSaved = new ActionMessage("driver.dbacctdriver.connectfailed");
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(dc);
			request.setAttribute("errorDetails", errorElements);
			ActionMessages messages = new ActionMessages();
			messages.add("information",messageSchemaNotSaved);
			saveErrors(request,messages);
			return mapping.findForward(SUCCESS);
		}catch(ActionNotPermitedException e){
			Logger.logError(MODULE,"Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}catch(DataManagerException dme){

			Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
			Logger.logTrace(MODULE,dme);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(dme);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("driver.create.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			saveErrors(request,messages);
			return mapping.findForward(FAILURE_FORWARD);
		}catch(Exception dme){

			Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
			Logger.logTrace(MODULE,dme);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(dme);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("driver.create.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			saveErrors(request,messages);
			return mapping.findForward(FAILURE_FORWARD);
		}

	}



	private void convertFromFormToData(CreateDiameterDBAcctDriverForm form,DBAcctDriverData data,DriverInstanceData driverInstanceData) {

		data.setCallEndFieldName(form.getCallEndFieldName());
		data.setCallStartFieldName(form.getCallStartFieldName());
		data.setCdrIdDbField(form.getCdrIdDbField());
		data.setCdrIdSeqName(form.getCdrIdSeqName());
		data.setCdrTablename(form.getCdrTablename());
		data.setCreateDateFieldName(form.getCreateDateFieldName());
		data.setDatabaseId(form.getDatabaseId());
		//driverData.setDatasourceScantime(dbDriverform.getDatasourceScantime());
		data.setDatasourceType(form.getDatasourceType());
		data.setDbDateField(form.getDbDateField());
		data.setDbQueryTimeout(form.getDbQueryTimeout());
		data.setEnabled(form.getEnabled());
		data.setInterimCdrIdDbField(form.getInterimCdrIdDbField());
		data.setInterimCdrIdSeqName(form.getInterimCdrIdSeqName());
		data.setInterimTablename(form.getInterimTablename());
		data.setLastModifiedDateFieldName(form.getLastModifiedDateFieldName());
		data.setMaxQueryTimeoutCount(form.getMaxQueryTimeoutCount());
		data.setMultivalDelimeter(form.getMultivalDelimeter());
		data.setRemoveInterimOnStop(form.getRemoveInterimOnStop());
		data.setRemoveTunnelLinkStopRec(form.getRemoveTunnelLinkStopRec());
		data.setRemoveTunnelStopRec(form.getRemoveTunnelStopRec());
		data.setStoreInterimRec(form.getStoreInterimRec());
		data.setStoreStopRec(form.getStoreStopRec());
		data.setStoreTunnelLinkRejectRec(form.getStoreTunnelLinkRejectRec());
		data.setStoreTunnelLinkStartRec(form.getStoreTunnelLinkStartRec());
		data.setStoreTunnelLinkStopRec(form.getStoreTunnelLinkStopRec());
		data.setStoreTunnelRejectRec(form.getStoreTunnelRejectRec());
		data.setStoreTunnelStartRec(form.getStoreTunnelStartRec());
		data.setStoreTunnelStopRec(form.getStoreTunnelStopRec());

		// driver instance related		

		driverInstanceData.setDriverTypeId(Long.parseLong(form.getDriverRelatedId()));
		driverInstanceData.setDescription(form.getDriverDesp());
		driverInstanceData.setName(form.getDriverInstanceName());
		driverInstanceData.setStatus("CST01");

	}
	
	private  List<DBAcctFeildMapData> getDBAcctFeildMapData(HttpServletRequest request){
		List<DBAcctFeildMapData> dbAcctFeildMapDataSet = new ArrayList<DBAcctFeildMapData>();		
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
				dbAcctFeildMapDataSet.add(dbAcctFeildMapData);
			}
		}
		return dbAcctFeildMapDataSet;
	} 

}

