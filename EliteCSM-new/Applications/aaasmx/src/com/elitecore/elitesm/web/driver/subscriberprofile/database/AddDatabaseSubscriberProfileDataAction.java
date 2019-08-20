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

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.elitesm.blmanager.servermgr.drivers.subscriberprofile.database.DatabaseSubscriberProfileBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.sqloperationexception.InvalidSQLStatementException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.dbdriver.data.DBAuthDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data.IDatabaseSubscriberProfileRecordBean;
import com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data.IDatasourceSchemaData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.driver.subscriberprofile.database.forms.AddDatabaseSubscriberProfileDataForm;
import com.elitecore.elitesm.web.radius.base.BaseDictionaryAction;


public class AddDatabaseSubscriberProfileDataAction extends BaseDictionaryAction{
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD="failure";
	private static final String VIEW_FORWARD = "addDatabaseSubscriberProfile";
	private static final String ACTION_ALIAS = ConfigConstant.ADD_SUBSCRIBE_PROFILE;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		if(checkAccess(request, ACTION_ALIAS)){
		Logger.logTrace(MODULE, "Enter the execute method of :"+ getClass().getName());
		try{
			
			AddDatabaseSubscriberProfileDataForm addDatabaseSubscriberProfileDataForm=(AddDatabaseSubscriberProfileDataForm)form;
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			
			String strDbAuthId = request.getParameter("dbAuthId");
			String strDriverInstanceId = request.getParameter("driverInstanceId");
			
			String dbAuthId=null;
			String driverInstanceId=null;
			if(strDbAuthId==null){
				dbAuthId = addDatabaseSubscriberProfileDataForm.getDbAuthId();
			}else{
				dbAuthId = strDbAuthId;
			}
			
			

			if(strDriverInstanceId==null){
				driverInstanceId = addDatabaseSubscriberProfileDataForm.getDriverInstanceId();
			}else{
				driverInstanceId = strDriverInstanceId;
			}
			
			addDatabaseSubscriberProfileDataForm.setDriverInstanceId(driverInstanceId);
			
			DatabaseSubscriberProfileBLManager blManager = new DatabaseSubscriberProfileBLManager();
			DriverBLManager driverBLManager  = new DriverBLManager();

			DriverInstanceData driverInstanceData = driverBLManager.getDriverInstanceByDriverInstanceId(driverInstanceId);
			DBAuthDriverData dbAuthDriverData = driverBLManager.getDBDriverByDriverInstanceId(driverInstanceId);
			
			
			Set<IDatasourceSchemaData> setDatabaseDatasouceSchema = dbAuthDriverData.getDatasourceSchemaSet();
			
			
			addDatabaseSubscriberProfileDataForm.setDateFormat(ConfigManager.get(BaseConstant.DATE_FORMAT));
			request.setAttribute("driverInstanceData",driverInstanceData);
			request.setAttribute("addDatabaseSubscriberProfileDataForm",addDatabaseSubscriberProfileDataForm);

			Iterator<IDatasourceSchemaData> itSetDatabaseDatasouceSchema = setDatabaseDatasouceSchema.iterator();
            List<IDatasourceSchemaData> lstDatabaseDatasouceSchema = new ArrayList<IDatasourceSchemaData>();
            
            while (itSetDatabaseDatasouceSchema.hasNext()) {
                lstDatabaseDatasouceSchema.add(itSetDatabaseDatasouceSchema.next());    
            }
            
			if(addDatabaseSubscriberProfileDataForm.getAction()==null){
				List<IDatabaseSubscriberProfileRecordBean> lstDataSchema = new ArrayList<IDatabaseSubscriberProfileRecordBean>();
				lstDataSchema= blManager.getDatabaseDataSchema(dbAuthDriverData);
				
				addDatabaseSubscriberProfileDataForm.setLstDatabaseDatasourceDataSchema(lstDataSchema);
				
	            Iterator<IDatasourceSchemaData> itPrinting = lstDatabaseDatasouceSchema.iterator();
	            try{
	            	while (itPrinting.hasNext()) {

	            		IDatasourceSchemaData toPrint = (IDatasourceSchemaData)itPrinting.next();
	            		if(toPrint.getSqlData() != null){

	            			String queryString = toPrint.getSqlData().getQuery();

	            			toPrint.setLstSQLPoolValue(blManager.getPoolValueFromQuery(dbAuthDriverData,queryString));

	            		}

	            	}   
	            }catch(InvalidSQLStatementException e){
	    			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
	    			request.setAttribute("errorDetails", errorElements);
	    			Logger.logTrace(MODULE,"Execute method of :"+getClass().getName());
	    			ActionMessage message = new ActionMessage("database.datasource.add.sqlvaluepoolfaiulre");
	    			ActionMessages messages = new ActionMessages();
	    			messages.add("information",message);
	    			saveErrors(request,messages);
	    			return mapping.findForward(FAILURE_FORWARD);
	    		}
				
				addDatabaseSubscriberProfileDataForm.setParamPoolValue(lstDatabaseDatasouceSchema);
				
			}else if(addDatabaseSubscriberProfileDataForm.getAction().endsWith("addData")){
				
				List<IDatabaseSubscriberProfileRecordBean> lstDatabaseDatasourceData = new ArrayList<IDatabaseSubscriberProfileRecordBean>();
				for(IDatabaseSubscriberProfileRecordBean subscriberProfileRecordBean : addDatabaseSubscriberProfileDataForm.getLstDatabaseDatasourceDataSchema()) {
					if(subscriberProfileRecordBean.getFieldValue() != null && subscriberProfileRecordBean.getFieldValue().trim().length() > 0) {
						
						if(("USER_IDENTITY").equalsIgnoreCase(subscriberProfileRecordBean.getFieldName())){
							blManager.getSubscriberUsingUserIdentity(subscriberProfileRecordBean.getFieldValue());
							
							String subscriberCaseSensitivity = ConfigManager.subscriberCaseSensitivity;
							
							if(ConfigManager.LOWER_CASE.equalsIgnoreCase(subscriberCaseSensitivity)){
								subscriberProfileRecordBean.setFieldValue(subscriberProfileRecordBean.getFieldValue().toLowerCase());
							}else if(ConfigManager.UPPER_CASE.equalsIgnoreCase(subscriberCaseSensitivity)){
								subscriberProfileRecordBean.setFieldValue(subscriberProfileRecordBean.getFieldValue().toUpperCase());
							}
						}
						
						
						//check expiry date value
						if(subscriberProfileRecordBean.getFieldName().equals("EXPIRYDATE")){
						
							boolean checkValidDate = isValidDate(subscriberProfileRecordBean.getFieldValue().trim());
							boolean checkValidYear = isValidYear(subscriberProfileRecordBean.getFieldValue().trim());
							
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
								String expiryDateString = subscriberProfileRecordBean.getFieldValue().trim();
								if(expiryDateString.indexOf(":") > 0){
									boolean checkValidDateTime = isValidDateTime(subscriberProfileRecordBean.getFieldValue().trim());
									
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
						
						lstDatabaseDatasourceData.add(subscriberProfileRecordBean);
					}
				}
				
				blManager.addDatabaseSusbscriberProfileRecord(dbAuthDriverData,lstDatabaseDatasourceData);
				doAuditing(staffData, ACTION_ALIAS);
				request.setAttribute("responseUrl","/searchDatabaseSubscriberProfileData.do?driverInstanceId="+driverInstanceId+"&dbAuthId="+dbAuthDriverData.getDbAuthId());
				ActionMessage message = new ActionMessage("database.datasource.add.datasourcedataaddsuccess");
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveMessages(request,messages);
				
				return mapping.findForward(SUCCESS_FORWARD);
			}
			return mapping.findForward(VIEW_FORWARD);
			
		}
		catch(Exception e){
			Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
			Logger.logTrace(MODULE,e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("database.datasource.add.datasourcedataaddfaiulre");
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



