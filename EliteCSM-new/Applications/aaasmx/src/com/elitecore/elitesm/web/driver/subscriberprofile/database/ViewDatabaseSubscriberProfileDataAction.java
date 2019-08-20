package com.elitecore.elitesm.web.driver.subscriberprofile.database;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.elitesm.blmanager.servermgr.drivers.subscriberprofile.database.DatabaseSubscriberProfileBLManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.dbdriver.data.DBAuthDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data.DatasourceSchemaData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data.IDatabaseSubscriberProfileRecordBean;
import com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data.IDatasourceSchemaData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.driver.subscriberprofile.database.forms.ViewDatabaseSubscriberProfileDataForm;
import com.elitecore.elitesm.web.radius.base.BaseDictionaryAction;

public class ViewDatabaseSubscriberProfileDataAction extends BaseDictionaryAction{
	
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String VIEW_FORWARD = "viewSubscriberProfileRecord";
	private static final String ACTION_ALIAS = ConfigConstant.SEARCH_SUBSCRIBE_PROFILE;
	private static final String SUBSCRIBER_RECORD_LIST = "SUBSCRIBER_RECORD_LIST"; 
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		if(checkAccess(request, ACTION_ALIAS)){
		Logger.logTrace(MODULE,"Enter execute method of :"+getClass().getName());
		
		ActionErrors errors = new ActionErrors();
		String fieldId = request.getParameter("strFieldId");
		String fieldName = request.getParameter("strFieldName");
		String strType = request.getParameter("strType");
		
		try{
			ViewDatabaseSubscriberProfileDataForm viewDatabaseSubscriberProfileDataForm = (ViewDatabaseSubscriberProfileDataForm)form;
			
			DatabaseSubscriberProfileBLManager blManager = new DatabaseSubscriberProfileBLManager();
			
			String strDriverInstanceId = request.getParameter("driverInstanceId");
			String driverInstanceId;
			if(strDriverInstanceId==null){
				driverInstanceId = viewDatabaseSubscriberProfileDataForm.getDriverInstanceId();
			}else{
				driverInstanceId = strDriverInstanceId;
			}
			
			String action = viewDatabaseSubscriberProfileDataForm.getAction();

			viewDatabaseSubscriberProfileDataForm.setDateFormat(ConfigManager.get(BaseConstant.DATE_FORMAT));
			Logger.logInfo(MODULE, "action : "+action);
			DriverBLManager driverBLManager = new DriverBLManager();
			DriverInstanceData  driverInstanceData = driverBLManager.getDriverInstanceByDriverInstanceId(driverInstanceId);
			DBAuthDriverData dbAuthDriverData = driverBLManager.getDBDriverByDriverInstanceId(driverInstanceId);
			
			request.setAttribute("driverInstanceData", driverInstanceData);
			request.setAttribute("viewDatabaseSubscriberProfileDataForm", viewDatabaseSubscriberProfileDataForm);
			if(action == null){
				if(dbAuthDriverData!=null){
					viewDatabaseSubscriberProfileDataForm.setDbAuthId(dbAuthDriverData.getDbAuthId());
					viewDatabaseSubscriberProfileDataForm.setStrFieldId(fieldId);
					viewDatabaseSubscriberProfileDataForm.setStrFieldName(fieldName);					
					viewDatabaseSubscriberProfileDataForm.setStrType(strType);
					
					List<IDatabaseSubscriberProfileRecordBean> lstDataRecord = new ArrayList<IDatabaseSubscriberProfileRecordBean>();
					lstDataRecord = blManager.getDatabaseSubscriberProfileRecord(dbAuthDriverData,fieldName,fieldId);	
					
					List<String> uniqueKeyList = blManager.getInputFieldsForDuplicateRecord(dbAuthDriverData);
					viewDatabaseSubscriberProfileDataForm.setUniqueKeyList(uniqueKeyList);

					Set<IDatasourceSchemaData> setDatabaseDatasouceSchema = dbAuthDriverData.getDatasourceSchemaSet();
					Iterator<IDatasourceSchemaData> itSetDatabaseDatasouceSchema = setDatabaseDatasouceSchema.iterator();
		            List<IDatasourceSchemaData> lstDatabaseDatasouceSchema = new ArrayList();
		            while (itSetDatabaseDatasouceSchema.hasNext()) {
		                lstDatabaseDatasouceSchema.add(itSetDatabaseDatasouceSchema.next());    
		            }
					
					viewDatabaseSubscriberProfileDataForm.setLstDataRecordField(lstDataRecord);
					request.getSession().setAttribute(SUBSCRIBER_RECORD_LIST, lstDataRecord);
					
					Iterator<IDatasourceSchemaData> itPrinting = lstDatabaseDatasouceSchema.iterator();
		        	while (itPrinting.hasNext()) {
		        		DatasourceSchemaData toPrint = (DatasourceSchemaData)itPrinting.next();
		        		if(toPrint.getSqlData() != null){
		        			String queryString = toPrint.getSqlData().getQuery();
		        			toPrint.setLstSQLPoolValue(blManager.getPoolValueFromQuery(dbAuthDriverData,queryString));
		        		}
		        	}     
					viewDatabaseSubscriberProfileDataForm.setParamPoolValue(lstDatabaseDatasouceSchema);
					
					request.setAttribute("lstDataRecord",lstDataRecord);
					return mapping.findForward(VIEW_FORWARD);
				} else {
					return mapping.findForward(FAILURE_FORWARD);
				}
			}else if(action.equalsIgnoreCase("update")){
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				List<IDatabaseSubscriberProfileRecordBean> lstDataRecordField = new ArrayList<IDatabaseSubscriberProfileRecordBean>();
				List<IDatabaseSubscriberProfileRecordBean> dbDataRecordFieldList = (List<IDatabaseSubscriberProfileRecordBean>) request.getSession().getAttribute(SUBSCRIBER_RECORD_LIST);
				request.getSession().removeAttribute(SUBSCRIBER_RECORD_LIST);
				List<IDatabaseSubscriberProfileRecordBean> formDataRecordFieldList =  viewDatabaseSubscriberProfileDataForm.getLstDataRecordField();
				if(dbDataRecordFieldList != null && !dbDataRecordFieldList.isEmpty()){
					for(IDatabaseSubscriberProfileRecordBean subscriberDbRecordBean : dbDataRecordFieldList){
						String name = subscriberDbRecordBean.getFieldName();
						String value = subscriberDbRecordBean.getFieldValue();
						if(name != null ) {
							for(IDatabaseSubscriberProfileRecordBean subscriberFormRecordBean : formDataRecordFieldList){
								if(name.equalsIgnoreCase(subscriberFormRecordBean.getFieldName())) {
									String formValue = subscriberFormRecordBean .getFieldValue();
									if((value != null && !value.equals(formValue)) || (formValue != null && formValue.trim().length() > 0 && !formValue.equals(value))) {
										lstDataRecordField.add(subscriberFormRecordBean);
										break;
									}  
								}
							}
						}
					}
				} else {
					lstDataRecordField = formDataRecordFieldList;
				}
				
				blManager.updateDatabaseSubscriberProfileRecord(dbAuthDriverData,lstDataRecordField,viewDatabaseSubscriberProfileDataForm.getStrFieldName() ,viewDatabaseSubscriberProfileDataForm.getStrFieldId());	
				
				request.setAttribute("responseUrl","/searchDatabaseSubscriberProfileData.do?driverInstanceId="+driverInstanceId+"&dbAuthId="+dbAuthDriverData.getDbAuthId());
				ActionMessage message = new ActionMessage("database.datasource.update.datasourcedataupdatesuccess");
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveMessages(request,messages);
				return mapping.findForward(SUCCESS_FORWARD);
			}else if(action.equalsIgnoreCase("duplicate")) {
				List<IDatabaseSubscriberProfileRecordBean> lstDataRecordField = new ArrayList<IDatabaseSubscriberProfileRecordBean>();
				for(IDatabaseSubscriberProfileRecordBean subscriberProfileRecordBean : viewDatabaseSubscriberProfileDataForm.getLstDataRecordField()) {
					if(subscriberProfileRecordBean.getFieldValue() != null && subscriberProfileRecordBean.getFieldValue().trim().length() > 0) {
						lstDataRecordField.add(subscriberProfileRecordBean);
					}
				}
				blManager.addDatabaseSusbscriberProfileRecord(dbAuthDriverData, lstDataRecordField);
				request.setAttribute("responseUrl","/searchDatabaseSubscriberProfileData.do?driverInstanceId="+driverInstanceId+"&dbAuthId="+dbAuthDriverData.getDbAuthId());
				ActionMessage message = new ActionMessage("database.datasource.duplicate.datasourcedataduplicatesuccess");
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveMessages(request,messages);
				return mapping.findForward(SUCCESS_FORWARD);
			}
		}catch(Exception e){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("database.datasource.update.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
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
}

