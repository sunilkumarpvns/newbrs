package com.elitecore.elitesm.web.datasource.database;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.datasource.DatabaseDSBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.datasource.database.data.DatabaseDSData;
import com.elitecore.elitesm.datamanager.datasource.database.data.IDatabaseDSData;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.datasource.database.forms.CreateDatabaseDSForm;
import com.elitecore.passwordutil.PasswordEncryption;

public class CreateDatabaseDSAction extends BaseWebAction{
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	//private static final String ACTION_ALIAS = ConfigConstant.CREATE_DATABASE_DATASOURCE;
		
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		//if(checkAccess(request, ACTION_ALIAS)){
		 
			Logger.logTrace(MODULE,"Execute Method Of :"+getClass().getName());
			Logger.logTrace(MODULE,"Execute method of :"+getClass().getName());
			CreateDatabaseDSForm createDatabaseDSForm = (CreateDatabaseDSForm)form ;
			String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
			try{
			DatabaseDSBLManager blManager = new DatabaseDSBLManager();
			
			IDatabaseDSData databaseDSData = new DatabaseDSData();
			
			
			databaseDSData.setName(createDatabaseDSForm.getName());
			databaseDSData.setConnectionUrl(createDatabaseDSForm.getConnectionUrl());
			databaseDSData.setUserName(createDatabaseDSForm.getUserName());
			databaseDSData.setPassword(createDatabaseDSForm.getPassword());
			databaseDSData.setMinimumPool(createDatabaseDSForm.getMinimumPool());
			databaseDSData.setMaximumPool(createDatabaseDSForm.getMaximumPool());
			databaseDSData.setCreateDate(getCurrentTimeStemp());
			databaseDSData.setCreatedByStaffId(currentUser);
			//databaseDSData.setLastmodifiedByStaffId(Long.parseLong(currentUser));
			//databaseDSData.setLastmodifiedDate(getCurrentTimeStemp());
			databaseDSData.setTimeout(createDatabaseDSForm.getTimeout());
			databaseDSData.setStatusCheckDuration(createDatabaseDSForm.getStatusCheckDuration());
			
			/* Encrypt User password */
			String encryptedPassword = PasswordEncryption.getInstance().crypt(databaseDSData.getPassword(),PasswordEncryption.ELITE_PASSWORD_CRYPT);
			databaseDSData.setPassword(encryptedPassword);
			
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				
			blManager.create(databaseDSData,staffData);
			
            request.setAttribute("responseUrl","/initSearchDatabaseDS"); 
			ActionMessage message = new ActionMessage("database.datasource.create.success");
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			saveMessages(request,messages);
			return mapping.findForward(SUCCESS_FORWARD);
		
    
		 
	}catch (DuplicateInstanceNameFoundException difExp) {
        Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
        Logger.logTrace(MODULE,difExp);
		Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(difExp);
		request.setAttribute("errorDetails", errorElements);
        ActionMessage message = new ActionMessage("databaseds.create.duplicate.failure",createDatabaseDSForm.getName());
        ActionMessages messages = new ActionMessages();
        messages.add("information",message);
        saveErrors(request,messages);
   }catch(Exception e){
		Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
		request.setAttribute("errorDetails", errorElements);
        ActionMessage message = new ActionMessage("databaseds.create.failure");
        ActionMessages messages = new ActionMessages();
        messages.add("information",message);
        saveErrors(request,messages);
	}
   
   /*}else{
		
        Logger.logError(MODULE, "Error during Data Manager operation ");
        ActionMessage message = new ActionMessage("general.user.restricted");
        ActionMessages messages = new ActionMessages();
        messages.add("information", message);
        saveErrors(request, messages);
		
		return mapping.findForward(INVALID_ACCESS_FORWARD);
	}*/
   return mapping.findForward(FAILURE_FORWARD);
	
	
  }
	
}
