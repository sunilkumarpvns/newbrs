package com.elitecore.elitesm.web.datasource.database;
import java.sql.Timestamp;
import java.util.Date;

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
import com.elitecore.elitesm.datamanager.datasource.database.data.IDatabaseDSData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.datasource.database.forms.UpdateDatabaseDSDetailForm;
import com.elitecore.passwordutil.PasswordEncryption;

public class UpdateDatabaseDSDetailAction extends BaseWebAction {
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String UPDATE_FORWARD = "updateDatabaseDSDetail";
	//private static final String SEARCH_FORWARD = "initSearchDatabaseDSDetail";
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_DATABASE_DATASOURCE;
	private static final String MODULE = "UpdateDatabaseDSDetailAction";
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		if(checkAccess(request, ACTION_ALIAS)){
			Logger.logTrace(MODULE,"Enter execute method of :"+getClass().getName());
			
			DatabaseDSBLManager databaseDSBLManager = new DatabaseDSBLManager();
			
			UpdateDatabaseDSDetailForm updateDatabaseDSDetailForm = (UpdateDatabaseDSDetailForm)form;
			try{
				String strdatabaseId = request.getParameter("databaseId");
				String databaseId;
				if(strdatabaseId == null){
					databaseId = updateDatabaseDSDetailForm.getDatabaseId();
				}else{
					databaseId = strdatabaseId;
				}
				
				if(updateDatabaseDSDetailForm.getAction() == null){
					if(databaseId!=null ){
						IDatabaseDSData databaseDSData = databaseDSBLManager.getDatabaseDSDataById(databaseId);
						updateDatabaseDSDetailForm = convertBeanToForm(databaseDSData);
						
						/* Decrypt User password */
						String decryptedPassword = PasswordEncryption.getInstance().decrypt(updateDatabaseDSDetailForm.getPassword(),PasswordEncryption.ELITE_PASSWORD_CRYPT);
						updateDatabaseDSDetailForm.setPassword(decryptedPassword);
						
						request.setAttribute("databaseDSData",databaseDSData);
						request.setAttribute("updateDatabaseDSDetailForm", updateDatabaseDSDetailForm);
						
					}
					return mapping.findForward(UPDATE_FORWARD);
				}else if(updateDatabaseDSDetailForm.getAction().equalsIgnoreCase("update")){
					IDatabaseDSData databaseDSData = databaseDSBLManager.getDatabaseDSDataById(databaseId);
					databaseDSData = convertFormToBean(updateDatabaseDSDetailForm,databaseDSData);
					databaseDSData.setLastmodifiedDate(new Timestamp((new Date()).getTime()));
					
					/* Encrypt User password */
					String encryptedPassword = PasswordEncryption.getInstance().crypt(databaseDSData.getPassword(),PasswordEncryption.ELITE_PASSWORD_CRYPT);
					databaseDSData.setPassword(encryptedPassword);
					
					IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
					
					databaseDSBLManager.updateDatabaseDSDetailById(databaseDSData,staffData);
					
					request.setAttribute("databaseDSData",databaseDSData);
					request.setAttribute("responseUrl","/viewDatabaseDS.do?databaseId="+databaseId); 
					ActionMessage message = new ActionMessage("database.datasource.update.success");
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
		   }catch(Exception e){
			   Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("databaseds.update.failure");
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
	
	private UpdateDatabaseDSDetailForm convertBeanToForm(IDatabaseDSData databaseDSdata){
		UpdateDatabaseDSDetailForm updatedatabaseDSDetailForm = null;
		if(databaseDSdata!=null){
			updatedatabaseDSDetailForm = new UpdateDatabaseDSDetailForm();
			updatedatabaseDSDetailForm.setDatabaseId(databaseDSdata.getDatabaseId());
			updatedatabaseDSDetailForm.setConnectionUrl(databaseDSdata.getConnectionUrl());
			updatedatabaseDSDetailForm.setMaximumPool(databaseDSdata.getMaximumPool());
			updatedatabaseDSDetailForm.setMinimumPool(databaseDSdata.getMinimumPool());
			updatedatabaseDSDetailForm.setName(databaseDSdata.getName());
			updatedatabaseDSDetailForm.setUserName(databaseDSdata.getUserName());
			updatedatabaseDSDetailForm.setPassword(databaseDSdata.getPassword());
			updatedatabaseDSDetailForm.setTimeout(databaseDSdata.getTimeout());
			updatedatabaseDSDetailForm.setStatusCheckDuration(databaseDSdata.getStatusCheckDuration());
			updatedatabaseDSDetailForm.setAuditUId(databaseDSdata.getAuditUId());
		}
		System.out.println("datasource Name is:"+updatedatabaseDSDetailForm.getName());
		return updatedatabaseDSDetailForm;
	}
	
	private IDatabaseDSData convertFormToBean(UpdateDatabaseDSDetailForm updatedatabaseDSDetailForm,IDatabaseDSData databaseDSdata){
		if(updatedatabaseDSDetailForm!=null){
			databaseDSdata.setDatabaseId(updatedatabaseDSDetailForm.getDatabaseId());
			databaseDSdata.setConnectionUrl(updatedatabaseDSDetailForm.getConnectionUrl());
			databaseDSdata.setLastmodifiedByStaffId(updatedatabaseDSDetailForm.getLastmodifiedByStaffId());
			databaseDSdata.setLastmodifiedDate(updatedatabaseDSDetailForm.getLastmodifiedDate());
			databaseDSdata.setMaximumPool(updatedatabaseDSDetailForm.getMaximumPool());
			databaseDSdata.setMinimumPool(updatedatabaseDSDetailForm.getMinimumPool());
			databaseDSdata.setName(updatedatabaseDSDetailForm.getName());
			databaseDSdata.setUserName(updatedatabaseDSDetailForm.getUserName());
			databaseDSdata.setPassword(updatedatabaseDSDetailForm.getPassword());
			databaseDSdata.setTimeout(updatedatabaseDSDetailForm.getTimeout());
			databaseDSdata.setStatusCheckDuration(updatedatabaseDSDetailForm.getStatusCheckDuration());
			databaseDSdata.setAuditUId(updatedatabaseDSDetailForm.getAuditUId());
		}
		return databaseDSdata;
	}
	
}
