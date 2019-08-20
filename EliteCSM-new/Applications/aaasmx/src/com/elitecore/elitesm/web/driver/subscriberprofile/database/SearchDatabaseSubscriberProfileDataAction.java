package com.elitecore.elitesm.web.driver.subscriberprofile.database;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.elitesm.blmanager.servermgr.drivers.subscriberprofile.database.DatabaseSubscriberProfileBLManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.dbdriver.data.DBAuthDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data.DatasourceSchemaData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data.IDatasourceSchemaData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.exception.DatasourceSchemaMisMatchException;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.driver.subscriberprofile.database.forms.SearchDatabaseSubscriberProfileDataForm;
import com.elitecore.elitesm.web.radius.base.BaseDictionaryAction;



public class SearchDatabaseSubscriberProfileDataAction extends BaseDictionaryAction{

	private static final String FAILURE_FORWARD = "failure";
	private static final String LIST_FORWARD = "searchDatabaseSubscriberProfile";
	private static final String MODULE = "SearchDatabaseSubscriberProfileDataAction";
	private static final String ACTION_ALIAS = ConfigConstant.SEARCH_SUBSCRIBE_PROFILE;

	public ActionForward execute(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response)throws Exception {
		if(checkAccess(request, ACTION_ALIAS)){	
			Logger.logTrace(MODULE, "Enter the execute method of :"+ getClass().getName());

			try {

				SearchDatabaseSubscriberProfileDataForm searchDatabaseSubscriberProfileDataForm = (SearchDatabaseSubscriberProfileDataForm) form;


				DatabaseSubscriberProfileBLManager blManager = new DatabaseSubscriberProfileBLManager();
				DriverBLManager driverBLManager  = new DriverBLManager();
				
				String strDriverInstanceId = request.getParameter("driverInstanceId");
				String strDbAuthId = request.getParameter("dbAuthId");

				String driverInstanceId;
				String dbAuthId;

				if(strDriverInstanceId==null){
					driverInstanceId = searchDatabaseSubscriberProfileDataForm.getDriverInstanceId();
				}else{
					driverInstanceId = strDriverInstanceId;
				}

				if(strDbAuthId==null){
					dbAuthId = searchDatabaseSubscriberProfileDataForm.getDbAuthId();
				}else{
					dbAuthId = strDbAuthId;
				}
				
				searchDatabaseSubscriberProfileDataForm.setDbAuthId(dbAuthId);
				searchDatabaseSubscriberProfileDataForm.setDriverInstanceId(driverInstanceId);
				
				DriverInstanceData  driverInstanceData = driverBLManager.getDriverInstanceByDriverInstanceId(driverInstanceId);
				DBAuthDriverData dbAuthDriverData = driverBLManager.getDBDriverByDriverInstanceId(driverInstanceId);
				request.setAttribute("driverInstanceData", driverInstanceData);
				

				IDatasourceSchemaData datasourceSchemaData = new DatasourceSchemaData();
				List lstFieldName=null;

				Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));

				int requiredPageNo;
				if(request.getParameter("pageNo") != null){
					requiredPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNo")));
				}else{
					requiredPageNo = new Long(searchDatabaseSubscriberProfileDataForm.getCurrentPage()).intValue();
				}
				if (requiredPageNo == 0)
					requiredPageNo = 1;

		

				if(dbAuthDriverData!=null ) {


					lstFieldName = blManager.getColumnNames(dbAuthDriverData);

					datasourceSchemaData = (IDatasourceSchemaData) lstFieldName.get(0);
					searchDatabaseSubscriberProfileDataForm.setIdFieldName(datasourceSchemaData.getFieldName());

					datasourceSchemaData = (IDatasourceSchemaData) lstFieldName.get(1);
					searchDatabaseSubscriberProfileDataForm.setFirstFieldName(datasourceSchemaData.getFieldName());


					searchDatabaseSubscriberProfileDataForm.setTotalField(Integer.parseInt(ConfigManager.get(BaseConstant.TOTAL_FIELD)));
					searchDatabaseSubscriberProfileDataForm.setNumerOfRecordsPerPage(Integer.parseInt(ConfigManager.get(BaseConstant.TOTAL_ROW)));


				}

				if(searchDatabaseSubscriberProfileDataForm.getAction() != null && searchDatabaseSubscriberProfileDataForm.getAction().equalsIgnoreCase("search")){
					IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
					Map filterMap = new HashMap();

					filterMap.put("action",searchDatabaseSubscriberProfileDataForm.getAction());
					filterMap.put("idFieldName", searchDatabaseSubscriberProfileDataForm.getIdFieldName());
					filterMap.put("firstFieldName",searchDatabaseSubscriberProfileDataForm.getFirstFieldName());
					//List lstDatasourceFieldData=addFromDataInList(searchDatabaseDatasourceDataForm);
					//PageList seachRowList=blManager.getSerachFieldData(datasourceData,lstDatasourceFieldData,searchDatabaseDatasourceDataForm.getCurrentPage(),pageSize,filterMap);
					PageList seachRowList=blManager.getSerachFieldData(dbAuthDriverData,searchDatabaseSubscriberProfileDataForm.getFirstFieldData(),searchDatabaseSubscriberProfileDataForm.getCurrentPage(),pageSize,filterMap);
					searchDatabaseSubscriberProfileDataForm.setSeachRowList(seachRowList.getListData());
					searchDatabaseSubscriberProfileDataForm.setTotalNumberOfRecord(seachRowList.getTotalItems());
					searchDatabaseSubscriberProfileDataForm.setCurrentPage(1);
					searchDatabaseSubscriberProfileDataForm.setTotalNoOfPage(seachRowList.getTotalPages());
					searchDatabaseSubscriberProfileDataForm.setLstFieldName(lstFieldName);
					doAuditing(staffData, ACTION_ALIAS);

				}
				else if (searchDatabaseSubscriberProfileDataForm.getAction() != null && searchDatabaseSubscriberProfileDataForm.getAction().equalsIgnoreCase("paging")){

					Map searchTotalRecordMap = new HashMap();
					searchTotalRecordMap.put("action",null);
					searchTotalRecordMap.put("idFieldName",null);
					searchTotalRecordMap.put("firstFieldName",searchDatabaseSubscriberProfileDataForm.getFirstFieldName());

					//List lstDatasourceFieldData=addFromDataInList(searchDatabaseDatasourceDataForm);	
					//PageList totalRowList=blManager.getSerachFieldData(datasourceData,lstDatasourceFieldData,requiredPageNo,pageSize,searchTotalRecordMap);
					PageList totalRowList=blManager.getSerachFieldData(dbAuthDriverData,searchDatabaseSubscriberProfileDataForm.getFirstFieldData(),requiredPageNo,pageSize,searchTotalRecordMap);
					Map filterMap = new HashMap();
					filterMap.put("action",searchDatabaseSubscriberProfileDataForm.getAction());
					filterMap.put("idFieldName", searchDatabaseSubscriberProfileDataForm.getIdFieldName());
					filterMap.put("firstFieldName",searchDatabaseSubscriberProfileDataForm.getFirstFieldName());

					PageList seachRowList=blManager.getSerachFieldData(dbAuthDriverData,searchDatabaseSubscriberProfileDataForm.getFirstFieldData(),requiredPageNo,pageSize,filterMap);
					searchDatabaseSubscriberProfileDataForm.setSeachRowList(seachRowList.getListData());
					searchDatabaseSubscriberProfileDataForm.setLstFieldName(lstFieldName);
					searchDatabaseSubscriberProfileDataForm.setCurrentPage((int)seachRowList.getCurrentPage());
					searchDatabaseSubscriberProfileDataForm.setTotalNoOfPage(seachRowList.getTotalPages());
					searchDatabaseSubscriberProfileDataForm.setTotalNumberOfRecord(seachRowList.getTotalItems());
					searchDatabaseSubscriberProfileDataForm.setAction("search");
				}

				return mapping.findForward(LIST_FORWARD);

			}
			catch(DatasourceSchemaMisMatchException e)
			{
				Logger.logError(MODULE,"Error during data Manager operation,reason :" +e.getMessage());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);

				ActionMessage message = new ActionMessage("datasource.schema.mismatch.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveErrors(request,messages);
			}

			catch (Exception managerExp) {
				managerExp.printStackTrace();
				Logger.logError(MODULE,"Error during data Manager operation,reason :"+ managerExp.getMessage());
				Logger.logTrace(MODULE,"Execute method of :"+getClass().getName());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
				request.setAttribute("errorDetails", errorElements);

				ActionMessage message = new ActionMessage("database.datasource.basicupdate.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveErrors(request,messages);
			}
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




	/*public List addFromDataInList(SearchDatabaseDatasourceDataForm searchDatabaseDatasourceDataForm){
			List DatasourceFieldData=new ArrayList();

			IDatabaseDatasourceRecordBean firstDatabaseDatasourceRecord=new DatabaseDatasourceRecordBean();
			firstDatabaseDatasourceRecord.setFieldName(searchDatabaseDatasourceDataForm.getFirstFieldName());
			firstDatabaseDatasourceRecord.setFieldValue(searchDatabaseDatasourceDataForm.getFirstFieldData());		

			IDatabaseDatasourceRecordBean secondDatabaseDatasourceRecord=new DatabaseDatasourceRecordBean();
			secondDatabaseDatasourceRecord.setFieldName(searchDatabaseDatasourceDataForm.getSecondFieldName());
			secondDatabaseDatasourceRecord.setFieldValue(searchDatabaseDatasourceDataForm.getSecondFieldData());

			DatasourceFieldData.add(firstDatabaseDatasourceRecord);
			DatasourceFieldData.add(secondDatabaseDatasourceRecord);

			return  DatasourceFieldData;
		}	*/





}
