package com.elitecore.elitesm.web.driver.subscriberprofile.database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
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

import com.elitecore.elitesm.blmanager.datasource.DatabaseDSBLManager;
import com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.elitesm.blmanager.servermgr.drivers.subscriberprofile.database.DatabaseSubscriberProfileBLManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.dbdriver.data.DBAuthDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data.DatasourceSchemaData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data.IDatasourceSchemaData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.driver.subscriberprofile.database.forms.UpdateDatabaseSubscriberProfileFieldForm;
import com.elitecore.elitesm.web.radius.base.BaseDictionaryAction;


public class UpdateDatabaseSubscriberProfileFieldAction extends BaseDictionaryAction {

	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String LIST_FORWARD    = "updateDatabaseField";
	private static final String LIST_FORWARDS    = "updateDatabaseFieldValuePool";
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_FIELD_NAME;

	public ActionForward execute( ActionMapping mapping ,
			ActionForm form ,
			HttpServletRequest request ,
			HttpServletResponse response ) throws Exception {
		if(checkAccess(request, ACTION_ALIAS)){
			Logger.logTrace(MODULE, "Enter the execute method of :" + getClass().getName());

			try {
				UpdateDatabaseSubscriberProfileFieldForm updateDatabaseSubscriberProfileFieldForm = (UpdateDatabaseSubscriberProfileFieldForm) form;
				DatabaseSubscriberProfileBLManager blManager = new DatabaseSubscriberProfileBLManager();
				DatabaseDSBLManager databaseDSBLManager = new DatabaseDSBLManager();
				DriverBLManager driverBLManager = new DriverBLManager();

				String strDriverInstanceId = request.getParameter("driverInstanceId");
				String strDbAuthId = request.getParameter("dbAuthId");

				String driverInstanceId;
				String dbAuthId;

				if(strDriverInstanceId==null){
					driverInstanceId = updateDatabaseSubscriberProfileFieldForm.getDriverInstanceId();
				}else{
					driverInstanceId = strDriverInstanceId;
				}

				if(strDbAuthId==null){
					dbAuthId = updateDatabaseSubscriberProfileFieldForm.getDbAuthId();
				}else{
					dbAuthId = strDbAuthId;
				}


				String checkForOutcome = request.getParameter("checkForOutcome");
				if(checkForOutcome == null){
					checkForOutcome = "";
				}


				String action = updateDatabaseSubscriberProfileFieldForm.getAction();

		
				DriverInstanceData  driverInstanceData = driverBLManager.getDriverInstanceByDriverInstanceId(driverInstanceId);
				DBAuthDriverData dbAuthDriverData = driverBLManager.getDBDriverByDriverInstanceId(driverInstanceId);
				String databaseId = dbAuthDriverData.getDatabaseId();
				Set<String> dataTypeList = databaseDSBLManager.getDataTypeList(databaseId);
				updateDatabaseSubscriberProfileFieldForm.setDataTypeSet(dataTypeList);
				
				request.setAttribute("driverInstanceData", driverInstanceData);
				request.setAttribute("updateDatabaseSubscriberProfileFieldForm", updateDatabaseSubscriberProfileFieldForm);

				if (action == null) {

					if (dbAuthId != null && dbAuthId != null && driverInstanceId!=null && driverInstanceId != null) {
						updateDatabaseSubscriberProfileFieldForm.setDbAuthId(dbAuthId);
						updateDatabaseSubscriberProfileFieldForm.setDriverInstanceId(driverInstanceId);


						Set<IDatasourceSchemaData> setDatabaseDatasouceSchema = dbAuthDriverData.getDatasourceSchemaSet();

						Iterator<IDatasourceSchemaData> itSetDatabaseDatasouceSchema = setDatabaseDatasouceSchema.iterator();
						List<IDatasourceSchemaData> lstDatabaseDatasouceSchema = new ArrayList<IDatasourceSchemaData>();

						while (itSetDatabaseDatasouceSchema.hasNext()) {
							lstDatabaseDatasouceSchema.add(itSetDatabaseDatasouceSchema.next());
						}

						int size = lstDatabaseDatasouceSchema.size();
						for(Iterator<IDatasourceSchemaData> iter = lstDatabaseDatasouceSchema.iterator(); iter.hasNext();){

							IDatasourceSchemaData prin = iter.next();
							

						}

						updateDatabaseSubscriberProfileFieldForm.getLstDatasourceSchema().addAll(lstDatabaseDatasouceSchema);



						if(checkForOutcome.equalsIgnoreCase("forValuePool")){
							return mapping.findForward(LIST_FORWARDS);
						}
						else{
							return mapping.findForward(LIST_FORWARD);
						}
					} else {
						return mapping.findForward(FAILURE_FORWARD);
					}
				} else if (action.equalsIgnoreCase("addDetail")) {
					updateDatabaseSubscriberProfileFieldForm.getLstDatasourceSchema().add(new DatasourceSchemaData());
					return mapping.findForward(LIST_FORWARD);

				} else if (action.equalsIgnoreCase("Remove")) {

					if (updateDatabaseSubscriberProfileFieldForm.getSelect() == null) { return mapping.findForward(LIST_FORWARD); }

					List lstRemoveItem = Arrays.asList(updateDatabaseSubscriberProfileFieldForm.getSelect());

					List lstDataSourceSchemaDetail = updateDatabaseSubscriberProfileFieldForm.getLstDatasourceSchema();
					Iterator itLstRemoveItem = lstRemoveItem.iterator();
					int count = 0;
					while (itLstRemoveItem.hasNext()) {
						int index = Integer.parseInt((String) itLstRemoveItem.next());
						IDatasourceSchemaData  removedElement= (IDatasourceSchemaData)lstDataSourceSchemaDetail.remove(index - count);
						Logger.logDebug(MODULE, "removedElement:"+removedElement.getFieldName());
						count++;
					}

					// lstDataSourceSchemaDetail.remove(updateDatabaseDatasourceFieldForm.getItemIndex());
					return mapping.findForward(LIST_FORWARD);
				} else if (action != null && action.equalsIgnoreCase("update")) {

					List lstDatasourceSchemaDetail = (List) updateDatabaseSubscriberProfileFieldForm.getLstDatasourceSchema();

					Iterator itLstDatasourceSchemsDeail = lstDatasourceSchemaDetail.iterator();

					while (itLstDatasourceSchemsDeail.hasNext()) {
						IDatasourceSchemaData iDataSourceSchemaData = (IDatasourceSchemaData) itLstDatasourceSchemsDeail.next();

						if (iDataSourceSchemaData.getDataType() == null || iDataSourceSchemaData.getFieldName() == null || iDataSourceSchemaData.getLength() == 0
								|| iDataSourceSchemaData.getDataType().equalsIgnoreCase("") || iDataSourceSchemaData.getFieldName().equalsIgnoreCase(""))
							itLstDatasourceSchemsDeail.remove();

					}

					for ( int i = 0; i < lstDatasourceSchemaDetail.size(); i++ ) {
						IDatasourceSchemaData iDataSourceSchemaData = (IDatasourceSchemaData) lstDatasourceSchemaDetail.get(i);
						iDataSourceSchemaData.setSqlData(null);
						iDataSourceSchemaData.setAppOrder(i);
						iDataSourceSchemaData.setDbAuthId(dbAuthDriverData.getDbAuthId());
						Logger.logDebug(MODULE,"DataSourceSchema FieldName : " + iDataSourceSchemaData.getFieldName());

					}

					Set tempSet = new HashSet();
					tempSet.clear();
					tempSet.addAll(lstDatasourceSchemaDetail);
					dbAuthDriverData.setDatasourceSchemaSet(tempSet);

					IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
					String actionAlias = ACTION_ALIAS;
					
	
					blManager.updateDatabaseSubscribeProfileSchema(dbAuthDriverData);
					doAuditing(staffData, actionAlias);

					request.setAttribute("responseUrl", "/updateDatabaseSubscriberProfileField.do?driverInstanceId="+driverInstanceData.getDriverInstanceId()+"&dbAuthId="+dbAuthDriverData.getDbAuthId()+"&checkForOutcome="+checkForOutcome);

					ActionMessage message = new ActionMessage("database.datasource.add.datasourcedatafieldupdatesuccess");
					ActionMessages messages = new ActionMessages();
					messages.add("information", message);
					saveMessages(request, messages);
					return mapping.findForward(SUCCESS_FORWARD);

				}
			}
			catch (Exception managerExp) {
				managerExp.printStackTrace();

				Logger.logError(MODULE, "Error during data Manager operation,reason :" + managerExp.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
				request.setAttribute("errorDetails", errorElements);

			}

			Logger.logTrace(MODULE, "Returning error forward from " + getClass().getName());
			ActionMessage message = new ActionMessage("database.datasource.view.fieldfailure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			return mapping.findForward(FAILURE_FORWARD);
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
