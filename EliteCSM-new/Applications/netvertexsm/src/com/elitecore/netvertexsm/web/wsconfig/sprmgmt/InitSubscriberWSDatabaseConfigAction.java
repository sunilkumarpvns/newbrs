package com.elitecore.netvertexsm.web.wsconfig.sprmgmt;

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

import com.elitecore.corenetvertex.constants.DriverTypes;
import com.elitecore.netvertexsm.blmanager.datasource.DatabaseDSBLManager;
import com.elitecore.netvertexsm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.netvertexsm.blmanager.servermgr.spinterface.SPInterfaceBLManager;
import com.elitecore.netvertexsm.blmanager.wsconfig.WebServiceConfigBLManager;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.datasource.database.data.DatabaseDSData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.spinterface.dbinterface.data.DBFieldMapData;
import com.elitecore.netvertexsm.datamanager.wsconfig.data.WSConfigData;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.wsconfig.sprmgmt.forms.SubscriberWSDatabaseConfigForm;
import com.elitecore.netvertexsm.ws.logger.Logger;


public class InitSubscriberWSDatabaseConfigAction extends BaseWebAction{
	private static final String MODULE = "InitSubscriberWSDatabaseConfigAction";
	private static final String UPDATE_FORWARD = "updateSubscriberWSDatabaseConfig";
	private static final String FAILURE_FORWARD = "failure";

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response ) throws Exception{
		Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName());
		SubscriberWSDatabaseConfigForm subscriberWSDatabaseConfigForm = (SubscriberWSDatabaseConfigForm)form;
		ActionMessages messages = new ActionMessages();

		try {

			if(subscriberWSDatabaseConfigForm.getCheckAction().equalsIgnoreCase("refresh")){
				//SubscriberProfileWSBLManager.setConfiguration();
			}
			WebServiceConfigBLManager configBLManager= new WebServiceConfigBLManager();
			DatabaseDSBLManager databaseDSBLManager= new DatabaseDSBLManager();
			List<DatabaseDSData> lstDatasource=databaseDSBLManager.getDatabaseDSList();
			subscriberWSDatabaseConfigForm.setLstDatasource(lstDatasource);
			
			DriverBLManager driverBLManager = new DriverBLManager();
			List<DriverInstanceData> driverInstanceDatas = new ArrayList<DriverInstanceData>(); 
			for(DriverInstanceData data : driverBLManager.getDriverInstanceList()) {
				if(data.getDriverTypeId() == DriverTypes.CSV_CDR_DRIVER || data.getDriverTypeId() == DriverTypes.DB_CDR_DRIVER)
					driverInstanceDatas.add(data);
			}
			subscriberWSDatabaseConfigForm.setDriverInstanceDatas(driverInstanceDatas);
			
			WSConfigData subscriberDBConfigData = configBLManager.getSubscriberConfiguration();
			convertBeanToForm(subscriberWSDatabaseConfigForm,subscriberDBConfigData);
			
			SPInterfaceBLManager spInterfaceBLManager = new SPInterfaceBLManager();
			List<DBFieldMapData> dbFieldMapDataList = spInterfaceBLManager.getDBFieldMapList();
			Set<String> dbFieldNameSet = new HashSet<String>();
			for(DBFieldMapData dbFieldMapBean:dbFieldMapDataList){
				dbFieldNameSet.add(dbFieldMapBean.getLogicalName());
			}					
			request.setAttribute("dbFieldNameSet", dbFieldNameSet);
			/*
			 * set Field map to request attribute
			 */
			 request.setAttribute("subscriberWSDatabaseConfigForm", subscriberWSDatabaseConfigForm);
			 return mapping.findForward(UPDATE_FORWARD); 	

		}catch (DataManagerException e){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			Logger.logTrace(MODULE, e);
			ActionMessage message = new ActionMessage("general.error");
			messages.add("information", message);
			saveErrors(request, messages);
			return mapping.findForward(FAILURE_FORWARD);
		}catch (Exception e) {
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			Logger.logTrace(MODULE, e);
			ActionMessage message = new ActionMessage("general.error");
			messages.add("information", message);
			saveErrors(request, messages);
			return mapping.findForward(FAILURE_FORWARD);
		}

	}
	private void convertBeanToForm(SubscriberWSDatabaseConfigForm form, WSConfigData data){
		if(data != null){
			form.setDatabaseId(data.getDatabasedsId());	
			form.setTableName(data.getTableName());
			form.setUserIdentityFieldName(data.getUserIdentityFieldName());
			form.setRecordFetchLimit(data.getRecordFetchLimit());
			form.setWsConfigId(data.getWsconfigId());
			form.setPrimaryKeyColumn(data.getPrimaryKeyColumn());
			form.setSequenceName(data.getSequenceName());
			form.setBodCDRDriverId(data.getBodCDRDriverId());
			form.setDynaSprDatabaseId(data.getDynaSprDatabaseId());
			form.setSubscriberIdentity(data.getSubscriberIdentity());
			form.setUsageMonitoringDatabaseId(data.getUsageMonitoringDatabaseId());
		}
	}
}
