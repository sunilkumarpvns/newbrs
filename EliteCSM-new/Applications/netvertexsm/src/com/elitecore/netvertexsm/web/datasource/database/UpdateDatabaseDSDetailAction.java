package com.elitecore.netvertexsm.web.datasource.database;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.elitecore.netvertexsm.util.PasswordUtility;
import com.elitecore.passwordutil.DecryptionFailedException;
import com.elitecore.passwordutil.DecryptionNotSupportedException;
import com.elitecore.passwordutil.EncryptionFailedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;
import org.apache.struts.action.ActionErrors;
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
import com.elitecore.netvertexsm.web.datasource.database.form.UpdateDatabaseDSDetailForm;

public class UpdateDatabaseDSDetailAction extends BaseWebAction {
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String UPDATE_FORWARD = "updateDatabaseDSDetail";
	private static final String SEARCH_FORWARD = "initSearchDatabaseDSDetail";
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_DATABASE_DATASOURCE;
	private static final String MODULE = "UPDATE_DATABASE_DS_ACTION";
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		if(checkAccess(request, ACTION_ALIAS)){
			Logger.logTrace(MODULE,"Enter execute method of :"+getClass().getName());
			
			DatabaseDSBLManager databaseDSBLManager = new DatabaseDSBLManager();
			
			
			ActionErrors errors = new ActionErrors();
			List listDatabaseDS = new ArrayList();
	
			UpdateDatabaseDSDetailForm updateDatabaseDSDetailForm = (UpdateDatabaseDSDetailForm)form;
			try{
			    
				String strdatabaseId = request.getParameter("databaseId");
				Long databaseId;
				if(strdatabaseId == null){
					databaseId = updateDatabaseDSDetailForm.getDatabaseId();
				}else{
					databaseId = Long.parseLong(strdatabaseId);
				}
				
				if(updateDatabaseDSDetailForm.getAction() == null){
					
					if(databaseId!=null ){
						
						IDatabaseDSData databaseDSData = new DatabaseDSData();
						databaseDSData.setDatabaseId(databaseId);
						
						databaseDSData = databaseDSBLManager.getDatabaseDS(databaseDSData);
						updateDatabaseDSDetailForm = convertBeanToForm(databaseDSData);
						request.setAttribute("databaseDSData",databaseDSData);
						request.setAttribute("updateDatabaseDSDetailForm", updateDatabaseDSDetailForm);
						
					}
					return mapping.findForward(UPDATE_FORWARD);
				}else if(updateDatabaseDSDetailForm.getAction().equalsIgnoreCase("update")){
					Preconditions.checkArgument((ConfigConstant.NETVERTEX_SERVER_DB.equals(updateDatabaseDSDetailForm.getName())==false), "NetvertexServerDB name is reserved for NetVertex Internal Purpose");					
					IDatabaseDSData databaseDSData = convertFormToBean(updateDatabaseDSDetailForm);
					
					IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
					String actionAlias = ACTION_ALIAS;
					databaseDSBLManager.updateDatabaseDSDetail(databaseDSData,staffData,actionAlias);
					
					request.setAttribute("databaseDSData",databaseDSData);
					request.setAttribute("responseUrl","/initSearchDatabaseDS");
					ActionMessage message = new ActionMessage("database.datasource.update.success",databaseDSData.getName());
					ActionMessages messages = new ActionMessages();
					messages.add("information",message);
					saveMessages(request,messages);
					return mapping.findForward(SUCCESS_FORWARD);
					
				}
			}catch (DuplicateInstanceNameFoundException dpfExp) {
		        Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
		        Logger.logTrace(MODULE,dpfExp);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(dpfExp);
				request.setAttribute("errorDetails", errorElements);
		        ActionMessage message = new ActionMessage("databaseds.update.duplicate.failure",updateDatabaseDSDetailForm.getName());
		        ActionMessages messages = new ActionMessages();
		        messages.add("information",message);
		        saveErrors(request,messages);
		   }catch(IllegalArgumentException e){
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
		        ActionMessage message = new ActionMessage("database.datasource.error.failure","update");
		        ActionMessages messages = new ActionMessages();
		        messages.add("information",message);
		        message = new ActionMessage("database.datasource.error.failure.reason");
		        messages.add("information",message);
		        saveErrors(request,messages);
			}catch(Exception e){
			   Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("databaseds.update.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
			}
			
	        ActionMessages errorHeadingMessage = new ActionMessages();
	        ActionMessage message = new ActionMessage("database.datasource.error.heading","updating");
	        errorHeadingMessage.add("errorHeading",message);
	        saveMessages(request,errorHeadingMessage);
	        
		   return mapping.findForward(FAILURE_FORWARD);
	
			
			
		}else{
	        Logger.logError(MODULE, "Error during Data Manager operation ");
	        ActionMessage message = new ActionMessage("general.user.restricted");
	        ActionMessages messages = new ActionMessages();
	        messages.add("information", message);
	        saveErrors(request, messages);
			
	        ActionMessages errorHeadingMessage = new ActionMessages();
	        message = new ActionMessage("database.datasource.error.heading","updating");
	        errorHeadingMessage.add("errorHeading",message);
	        saveMessages(request,errorHeadingMessage);	        
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
	
	private UpdateDatabaseDSDetailForm convertBeanToForm(IDatabaseDSData databaseDSdata) throws DecryptionNotSupportedException, DecryptionFailedException, NoSuchEncryptionException {
		UpdateDatabaseDSDetailForm updatedatabaseDSDetailForm = null;
		if(databaseDSdata!=null){
			updatedatabaseDSDetailForm = new UpdateDatabaseDSDetailForm();
			updatedatabaseDSDetailForm.setDatabaseId(databaseDSdata.getDatabaseId());
			updatedatabaseDSDetailForm.setConnectionUrl(databaseDSdata.getConnectionUrl());
			updatedatabaseDSDetailForm.setMaximumPool(databaseDSdata.getMaximumPool());
			updatedatabaseDSDetailForm.setMinimumPool(databaseDSdata.getMinimumPool());
			updatedatabaseDSDetailForm.setName(databaseDSdata.getName());
			updatedatabaseDSDetailForm.setUserName(databaseDSdata.getUserName());
			updatedatabaseDSDetailForm.setPassword(PasswordUtility.getDecryptedPassword(databaseDSdata.getPassword()));
			updatedatabaseDSDetailForm.setTimeout(databaseDSdata.getTimeout());
			updatedatabaseDSDetailForm.setStatusCheckDuration(databaseDSdata.getStatusCheckDuration());
			
		}
		System.out.println("datasource Name is:"+updatedatabaseDSDetailForm.getName());
		return updatedatabaseDSDetailForm;
	}
	
	private IDatabaseDSData convertFormToBean(UpdateDatabaseDSDetailForm updatedatabaseDSDetailForm) throws NoSuchEncryptionException, EncryptionFailedException {
		IDatabaseDSData databaseDSdata = null;
		if(updatedatabaseDSDetailForm!=null){
			databaseDSdata = new DatabaseDSData();
			databaseDSdata.setDatabaseId(updatedatabaseDSDetailForm.getDatabaseId());
			databaseDSdata.setConnectionUrl(updatedatabaseDSDetailForm.getConnectionUrl());
			databaseDSdata.setMaximumPool(updatedatabaseDSDetailForm.getMaximumPool());
			databaseDSdata.setMinimumPool(updatedatabaseDSDetailForm.getMinimumPool());
			databaseDSdata.setName(updatedatabaseDSDetailForm.getName());
			databaseDSdata.setUserName(updatedatabaseDSDetailForm.getUserName());
			databaseDSdata.setPassword(PasswordUtility.getEncryptedPassword(updatedatabaseDSDetailForm.getPassword()));
			databaseDSdata.setTimeout(updatedatabaseDSDetailForm.getTimeout());
			databaseDSdata.setStatusCheckDuration(updatedatabaseDSDetailForm.getStatusCheckDuration());
			
			
		}
		return databaseDSdata;
	}
	
}
