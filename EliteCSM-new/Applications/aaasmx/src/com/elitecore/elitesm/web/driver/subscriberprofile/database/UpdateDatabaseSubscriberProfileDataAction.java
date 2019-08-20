package com.elitecore.elitesm.web.driver.subscriberprofile.database;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import com.elitecore.elitesm.web.driver.subscriberprofile.database.forms.UpdateDatabaseSubscriberProfileDataForm;
import com.elitecore.elitesm.web.radius.base.BaseDictionaryAction;

public class UpdateDatabaseSubscriberProfileDataAction extends BaseDictionaryAction{
	
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String UPDATE_FORWARD = "updateSubscriberProfileRecord";
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_SUBSCRIBE_PROFILE;
	private static final String SUBSCRIBER_RECORD_LIST = "SUBSCRIBER_RECORD_LIST"; 
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		if(checkAccess(request, ACTION_ALIAS)){
		Logger.logTrace(MODULE,"Enter execute method of :"+getClass().getName());
		
		ActionErrors errors = new ActionErrors();
		String fieldId = request.getParameter("strFieldId");
		String fieldName = request.getParameter("strFieldName");
		String strType = request.getParameter("strType");
		
		try{
			UpdateDatabaseSubscriberProfileDataForm updateDatabaseSubscriberProfileDataForm = (UpdateDatabaseSubscriberProfileDataForm)form;
			
			DatabaseSubscriberProfileBLManager blManager = new DatabaseSubscriberProfileBLManager();
			
			String strDriverInstanceId = request.getParameter("driverInstanceId");
			String driverInstanceId;
			if(strDriverInstanceId==null){
				driverInstanceId = updateDatabaseSubscriberProfileDataForm.getDriverInstanceId();
			}else{
				driverInstanceId = strDriverInstanceId;
			}
			
			String action = updateDatabaseSubscriberProfileDataForm.getAction();

			updateDatabaseSubscriberProfileDataForm.setDateFormat(ConfigManager.get(BaseConstant.DATE_FORMAT));
			Logger.logInfo(MODULE, "action : "+action);
			DriverBLManager driverBLManager = new DriverBLManager();
			DriverInstanceData  driverInstanceData = driverBLManager.getDriverInstanceByDriverInstanceId(driverInstanceId);
			DBAuthDriverData dbAuthDriverData = driverBLManager.getDBDriverByDriverInstanceId(driverInstanceId);
			
			request.setAttribute("driverInstanceData", driverInstanceData);
			request.setAttribute("updateDatabaseSubscriberProfileDataForm", updateDatabaseSubscriberProfileDataForm);
			if(action == null){
				if(dbAuthDriverData!=null){
					updateDatabaseSubscriberProfileDataForm.setDbAuthId(dbAuthDriverData.getDbAuthId());
					updateDatabaseSubscriberProfileDataForm.setStrFieldId(fieldId);
					updateDatabaseSubscriberProfileDataForm.setStrFieldName(fieldName);					
					updateDatabaseSubscriberProfileDataForm.setStrType(strType);
					
					List<IDatabaseSubscriberProfileRecordBean> lstDataRecord = new ArrayList<IDatabaseSubscriberProfileRecordBean>();
					lstDataRecord = blManager.getDatabaseSubscriberProfileRecord(dbAuthDriverData,fieldName,fieldId);	
					
					for (IDatabaseSubscriberProfileRecordBean iDatabaseSubscriberProfileRecordBean : lstDataRecord) {
						if(("USER_IDENTITY").equalsIgnoreCase(iDatabaseSubscriberProfileRecordBean.getFieldName())){
							String subscriberCaseSensitivity = ConfigManager.subscriberCaseSensitivity;
							
							if(ConfigManager.LOWER_CASE.equalsIgnoreCase(subscriberCaseSensitivity)){
								iDatabaseSubscriberProfileRecordBean.setFieldValue(iDatabaseSubscriberProfileRecordBean.getFieldValue().toLowerCase());
							}else if(ConfigManager.UPPER_CASE.equalsIgnoreCase(subscriberCaseSensitivity)){
								iDatabaseSubscriberProfileRecordBean.setFieldValue(iDatabaseSubscriberProfileRecordBean.getFieldValue().toUpperCase());
							}
						}
					}
					
					List<String> uniqueKeyList = blManager.getInputFieldsForDuplicateRecord(dbAuthDriverData);
					updateDatabaseSubscriberProfileDataForm.setUniqueKeyList(uniqueKeyList);

					Set<IDatasourceSchemaData> setDatabaseDatasouceSchema = dbAuthDriverData.getDatasourceSchemaSet();
					Iterator<IDatasourceSchemaData> itSetDatabaseDatasouceSchema = setDatabaseDatasouceSchema.iterator();
		            List<IDatasourceSchemaData> lstDatabaseDatasouceSchema = new ArrayList();
		            while (itSetDatabaseDatasouceSchema.hasNext()) {
		                lstDatabaseDatasouceSchema.add(itSetDatabaseDatasouceSchema.next());    
		            }
					
					updateDatabaseSubscriberProfileDataForm.setLstDataRecordField(lstDataRecord);
					request.getSession().setAttribute(SUBSCRIBER_RECORD_LIST, lstDataRecord);
					
					Iterator<IDatasourceSchemaData> itPrinting = lstDatabaseDatasouceSchema.iterator();
		        	while (itPrinting.hasNext()) {
		        		DatasourceSchemaData toPrint = (DatasourceSchemaData)itPrinting.next();
		        		if(toPrint.getSqlData() != null){
		        			String queryString = toPrint.getSqlData().getQuery();
		        			toPrint.setLstSQLPoolValue(blManager.getPoolValueFromQuery(dbAuthDriverData,queryString));
		        		}
		        	}     
					updateDatabaseSubscriberProfileDataForm.setParamPoolValue(lstDatabaseDatasouceSchema);
					
					request.setAttribute("lstDataRecord",lstDataRecord);
					return mapping.findForward(UPDATE_FORWARD);
				} else {
					return mapping.findForward(FAILURE_FORWARD);
				}
			}else if(action.equalsIgnoreCase("update")){
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				List<IDatabaseSubscriberProfileRecordBean> lstDataRecordField = new ArrayList<IDatabaseSubscriberProfileRecordBean>();
				List<IDatabaseSubscriberProfileRecordBean> dbDataRecordFieldList = (List<IDatabaseSubscriberProfileRecordBean>) request.getSession().getAttribute(SUBSCRIBER_RECORD_LIST);
				request.getSession().removeAttribute(SUBSCRIBER_RECORD_LIST);
				List<IDatabaseSubscriberProfileRecordBean> formDataRecordFieldList =  updateDatabaseSubscriberProfileDataForm.getLstDataRecordField();
				if(dbDataRecordFieldList != null && !dbDataRecordFieldList.isEmpty()){
					for(IDatabaseSubscriberProfileRecordBean subscriberDbRecordBean : dbDataRecordFieldList){
						String name = subscriberDbRecordBean.getFieldName();
						String value = subscriberDbRecordBean.getFieldValue();
						if(name != null ) {
							for(IDatabaseSubscriberProfileRecordBean subscriberFormRecordBean : formDataRecordFieldList){
								if(name.equalsIgnoreCase(subscriberFormRecordBean.getFieldName())) {
									String formValue = subscriberFormRecordBean .getFieldValue();
									if((value != null && !value.equals(formValue)) || (formValue != null && formValue.trim().length() > 0 && !formValue.equals(value))) {
										
										if(("USER_IDENTITY").equalsIgnoreCase(subscriberFormRecordBean.getFieldName())){
											String subscriberCaseSensitivity = ConfigManager.subscriberCaseSensitivity;
											
											if(ConfigManager.LOWER_CASE.equalsIgnoreCase(subscriberCaseSensitivity)){
												subscriberFormRecordBean.setFieldValue(subscriberFormRecordBean.getFieldValue().toLowerCase());
											}else if(ConfigManager.UPPER_CASE.equalsIgnoreCase(subscriberCaseSensitivity)){
												subscriberFormRecordBean.setFieldValue(subscriberFormRecordBean.getFieldValue().toUpperCase());
											}
										}
										//check expiry date value
										if(subscriberFormRecordBean.getFieldName().equals("EXPIRYDATE")){
										
											boolean checkValidDate = isValidDate(subscriberFormRecordBean.getFieldValue().trim());
											boolean checkValidYear = isValidYear(subscriberFormRecordBean.getFieldValue().trim());
											
											if(!checkValidDate){
											
												ActionMessage message = new ActionMessage("subscriber.invaliddate");
												ActionMessages messages = new ActionMessages();
												messages.add("information",message);
												saveErrors(request,messages);
												
												return mapping.findForward(FAILURE_FORWARD);
											}else if(!checkValidYear){
													ActionMessage message = new ActionMessage("subscriber.invalidyear");
													ActionMessages messages = new ActionMessages();
													messages.add("information",message);
													saveErrors(request,messages);
													
													return mapping.findForward(FAILURE_FORWARD);
												
											}else{
												String expiryDateString = subscriberFormRecordBean.getFieldValue().trim();
												if(expiryDateString.indexOf(":") > 0){
													boolean checkValidDateTime = isValidDateTime(subscriberFormRecordBean.getFieldValue().trim());
													
													if(!checkValidDateTime){
														
														ActionMessage message = new ActionMessage("subscriber.invaliddatetime");
														ActionMessages messages = new ActionMessages();
														messages.add("information",message);
														saveErrors(request,messages);
														
														return mapping.findForward(FAILURE_FORWARD);
													}
												}
											}
										}
										
										
										lstDataRecordField.add(subscriberFormRecordBean);
										break;
									}  
								}
							}
						}
					}
				} else {
					if(formDataRecordFieldList != null && !formDataRecordFieldList.isEmpty()){
						for(IDatabaseSubscriberProfileRecordBean subscriberDbRecordBean : formDataRecordFieldList){
							String name = subscriberDbRecordBean.getFieldName();
							String value = subscriberDbRecordBean.getFieldValue();
							if(name != null ) {
								for(IDatabaseSubscriberProfileRecordBean subscriberFormRecordBean : formDataRecordFieldList){
									if(name.equalsIgnoreCase(subscriberFormRecordBean.getFieldName())) {
										String formValue = subscriberFormRecordBean .getFieldValue();

										if(("USER_IDENTITY").equalsIgnoreCase(subscriberFormRecordBean.getFieldName())){
											String subscriberCaseSensitivity = ConfigManager.subscriberCaseSensitivity;

											if(ConfigManager.LOWER_CASE.equalsIgnoreCase(subscriberCaseSensitivity)){
												subscriberFormRecordBean.setFieldValue(subscriberFormRecordBean.getFieldValue().toLowerCase());
											}else if(ConfigManager.UPPER_CASE.equalsIgnoreCase(subscriberCaseSensitivity)){
												subscriberFormRecordBean.setFieldValue(subscriberFormRecordBean.getFieldValue().toUpperCase());
											}
										}
										//check expiry date value
										if(subscriberFormRecordBean.getFieldName().equals("EXPIRYDATE")){

											boolean checkValidDate = isValidDate(subscriberFormRecordBean.getFieldValue().trim());
											boolean checkValidYear = isValidYear(subscriberFormRecordBean.getFieldValue().trim());

											if(!checkValidDate){

												ActionMessage message = new ActionMessage("subscriber.invaliddate");
												ActionMessages messages = new ActionMessages();
												messages.add("information",message);
												saveErrors(request,messages);

												return mapping.findForward(FAILURE_FORWARD);
											}else if(!checkValidYear){
												ActionMessage message = new ActionMessage("subscriber.invalidyear");
												ActionMessages messages = new ActionMessages();
												messages.add("information",message);
												saveErrors(request,messages);

												return mapping.findForward(FAILURE_FORWARD);
											}else{
												String expiryDateString = subscriberFormRecordBean.getFieldValue().trim();
												if(expiryDateString.indexOf(":") > 0){
													boolean checkValidDateTime = isValidDateTime(subscriberFormRecordBean.getFieldValue().trim());

													if(!checkValidDateTime){

														ActionMessage message = new ActionMessage("subscriber.invaliddatetime");
														ActionMessages messages = new ActionMessages();
														messages.add("information",message);
														saveErrors(request,messages);

														return mapping.findForward(FAILURE_FORWARD);
													}
												}
											}
										}  
									}
								}
							}
						}
					}
					
					lstDataRecordField = formDataRecordFieldList;
				}
				
				blManager.updateDatabaseSubscriberProfileRecord(dbAuthDriverData,lstDataRecordField,updateDatabaseSubscriberProfileDataForm.getStrFieldName() ,updateDatabaseSubscriberProfileDataForm.getStrFieldId());
				doAuditing(staffData, ACTION_ALIAS);
				
				request.setAttribute("responseUrl","/searchDatabaseSubscriberProfileData.do?driverInstanceId="+driverInstanceId+"&dbAuthId="+dbAuthDriverData.getDbAuthId());
				ActionMessage message = new ActionMessage("database.datasource.update.datasourcedataupdatesuccess");
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveMessages(request,messages);
				return mapping.findForward(SUCCESS_FORWARD);
			}else if(action.equalsIgnoreCase("duplicate")) {
				List<IDatabaseSubscriberProfileRecordBean> lstDataRecordField = new ArrayList<IDatabaseSubscriberProfileRecordBean>();
				for(IDatabaseSubscriberProfileRecordBean subscriberProfileRecordBean : updateDatabaseSubscriberProfileDataForm.getLstDataRecordField()) {
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
	
	private boolean isValidYear(String date) {
		try{
			SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
			Date date1 = format.parse(date);
			SimpleDateFormat df = new SimpleDateFormat("yyyy");
			String year = df.format(date1);
			int configuredYear = 0;
			int currentYear = Calendar.getInstance().get(Calendar.YEAR);
			
			if(year != null){
				configuredYear = Integer.parseInt(year);
			}
			
			if(configuredYear != 0){
				if(configuredYear < currentYear){
					return false;
				}else{
					return true;
				}
			}
			return true;
		}catch(Exception e){
			return false;
		}
	}
	
	public boolean isValidDate(String inDate) {
	    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
	    dateFormat.setLenient(false);
	    try {
	      dateFormat.parse(inDate.trim());
	    } catch (ParseException pe) {
	      return false;
	    }
	    return true;
	  }
	
	public boolean isValidDateTime(String inDate) {
	    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	    dateFormat.setLenient(false);
	    try {
	      dateFormat.parse(inDate.trim());
	    } catch (ParseException pe) {
	      return false;
	    }
	    return true;
	 }
}

