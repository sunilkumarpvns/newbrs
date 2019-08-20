package com.elitecore.netvertexsm.web.datasource.database;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.elitecore.netvertexsm.util.PasswordUtility;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Preconditions;
import com.elitecore.netvertexsm.blmanager.datasource.DatabaseDSBLManager;
import com.elitecore.netvertexsm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.datasource.database.data.DatabaseDSData;
import com.elitecore.netvertexsm.datamanager.datasource.database.data.IDatabaseDSData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.netvertexsm.web.datasource.database.form.CreateDatabaseDSForm;

public class CreateDatabaseDSAction extends BaseWebAction{
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_DATABASE_DATASOURCE;
		
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		if(checkAccess(request, ACTION_ALIAS)){		 
			Logger.logTrace(MODULE,"Execute Method Of :"+getClass().getName());
			Logger.logTrace(MODULE,"Execute method of :"+getClass().getName());
			CreateDatabaseDSForm createDatabaseDSForm = (CreateDatabaseDSForm)form ;
			try{
				Preconditions.checkArgument((ConfigConstant.NETVERTEX_SERVER_DB.equals(createDatabaseDSForm.getName())==false), "NetvertexServerDB name is reserved for NetVertex Internal Purpose");
				DatabaseDSBLManager blManager = new DatabaseDSBLManager();			
				IDatabaseDSData databaseDSData = new DatabaseDSData();					
				databaseDSData.setName(createDatabaseDSForm.getName());
				databaseDSData.setConnectionUrl(createDatabaseDSForm.getConnectionUrl());
				databaseDSData.setUserName(createDatabaseDSForm.getUserName());
				databaseDSData.setPassword(PasswordUtility.getEncryptedPassword(createDatabaseDSForm.getPassword()));
				databaseDSData.setMinimumPool(createDatabaseDSForm.getMinimumPool());
				databaseDSData.setMaximumPool(createDatabaseDSForm.getMaximumPool());
				databaseDSData.setTimeout(createDatabaseDSForm.getTimeout());
				databaseDSData.setStatusCheckDuration(createDatabaseDSForm.getStatusCheckDuration());
							 			
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));							
				blManager.create(databaseDSData,staffData,ACTION_ALIAS);
				
	            request.setAttribute("responseUrl","/initSearchDatabaseDS"); 
				ActionMessage message = new ActionMessage("database.datasource.create.success",databaseDSData.getName());
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
		   }catch(IllegalArgumentException e){
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
		        ActionMessage message = new ActionMessage("database.datasource.error.failure","create");
		        ActionMessages messages = new ActionMessages();
		        messages.add("information",message);
		        message = new ActionMessage("database.datasource.error.failure.reason");
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
   }else{		
        Logger.logError(MODULE, "Error during Data Manager operation ");
        ActionMessage message = new ActionMessage("general.user.restricted");
        ActionMessages messages = new ActionMessages();
        messages.add("information", message);
        saveErrors(request, messages);		

        ActionMessages errorHeadingMessage = new ActionMessages();
        message = new ActionMessage("database.datasource.error.heading","creating");
        errorHeadingMessage.add("errorHeading",message);
        saveMessages(request,errorHeadingMessage);        
		return mapping.findForward(INVALID_ACCESS_FORWARD);
	}
        ActionMessages errorHeadingMessage = new ActionMessages();
        ActionMessage message = new ActionMessage("database.datasource.error.heading","creating");
        errorHeadingMessage.add("errorHeading",message);
        saveMessages(request,errorHeadingMessage);
   return mapping.findForward(FAILURE_FORWARD);
  }
}