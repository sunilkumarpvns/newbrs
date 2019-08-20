package com.elitecore.netvertexsm.web.datasource.database;

import java.util.Arrays;
import java.util.Date;

import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.netvertexsm.web.datasource.database.form.MiscDatabaseDSForm;
import com.elitecore.netvertexsm.web.datasource.database.form.SearchDatabaseDSForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.datasource.DatabaseDSBLManager;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.util.logger.Logger;


import com.elitecore.netvertexsm.util.constants.BaseConstant;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;

public class MiscDatabaseDSAction extends BaseWebAction{

	private static final String SUCCESS_FORWARD = "success";	
	private static final String LIST_FORWARD = "databaseDSSearchList";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.DELETE_DATABASE_DATASOURCE;

	public ActionForward execute(ActionMapping mapping,ActionForm actionForm,HttpServletRequest request,HttpServletResponse response) throws Exception {
		Logger.logTrace(MODULE,"Enter execute method of "+getClass().getName());

		if((checkAccess(request, ACTION_ALIAS))){
			try{
				MiscDatabaseDSForm miscDatabaseDSForm=(MiscDatabaseDSForm)actionForm;
				DatabaseDSBLManager blManager = new DatabaseDSBLManager();
				if(miscDatabaseDSForm.getAction() != null){
					String[] strSelectedIds = request.getParameterValues("select");
					if(strSelectedIds != null){
						Date currDate = new Date(); 
						if(miscDatabaseDSForm.getAction().equalsIgnoreCase("show")){
							//checkActionPermission(request, ACTION_ALIAS);
							IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
							String actionAlias = ACTION_ALIAS;

							blManager.updateStatus(Arrays.asList(strSelectedIds),BaseConstant.SHOW_STATUS_ID,staffData,actionAlias);
						}else if(miscDatabaseDSForm.getAction().equalsIgnoreCase("hide")){
							//checkActionPermission(request, ACTION_ALIAS);
							IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
							String actionAlias = ACTION_ALIAS;

							blManager.updateStatus(Arrays.asList(strSelectedIds),BaseConstant.HIDE_STATUS_ID,staffData,actionAlias);
						}else if(miscDatabaseDSForm.getAction().equalsIgnoreCase("delete")){
							checkActionPermission(request, ACTION_ALIAS);
							IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));		    				

							blManager.delete(Arrays.asList(strSelectedIds),staffData,ACTION_ALIAS);
							int strSelectedIdsLen = strSelectedIds.length;
							long currentPageNumber = getCurrentPageNumberAfterDel(strSelectedIdsLen,miscDatabaseDSForm.getPageNumber(),miscDatabaseDSForm.getTotalPages(),miscDatabaseDSForm.getTotalRecords());

							String status = "All";

							request.setAttribute("responseUrl","/searchDatabaseDS.do?name="+miscDatabaseDSForm.getName()+"&pageNumber="+currentPageNumber+"&totalPages="+miscDatabaseDSForm.getTotalPages()+"&totalRecords="+miscDatabaseDSForm.getTotalRecords()+"&status="+status);
							ActionMessage message = new ActionMessage("database.datasource.delete.success");
							ActionMessages messages1 = new ActionMessages();
							messages1.add("information",message);
							saveMessages(request,messages1);


							return mapping.findForward(SUCCESS_FORWARD);
						}
					}
				}

				SearchDatabaseDSForm searchDatabaseDSForm = new SearchDatabaseDSForm();
				searchDatabaseDSForm.setStatus(miscDatabaseDSForm.getStatus());    		
				searchDatabaseDSForm.setName(miscDatabaseDSForm.getName());
				searchDatabaseDSForm.setPageNumber(miscDatabaseDSForm.getPageNumber());
				searchDatabaseDSForm.setTotalPages(miscDatabaseDSForm.getTotalPages());
				searchDatabaseDSForm.setTotalRecords(miscDatabaseDSForm.getTotalRecords());
				searchDatabaseDSForm.setLstDatabaseDS(miscDatabaseDSForm.getListdatabaseDS());
				searchDatabaseDSForm.setAction(BaseConstant.LISTACTION);
				request.setAttribute("searchDatabaseDSForm",searchDatabaseDSForm);
				return mapping.findForward(LIST_FORWARD);

			}catch(ActionNotPermitedException e){
				Logger.logError(MODULE,"Error :-" + e.getMessage());
				printPermitedActionAlias(request);
				ActionMessages messages = new ActionMessages();
				messages.add("information", new ActionMessage("general.user.restricted"));
				saveErrors(request, messages);
				
		        ActionMessages errorHeadingMessage = new ActionMessages();
		        ActionMessage message = new ActionMessage("database.datasource.error.heading","deleting");
		        errorHeadingMessage.add("errorHeading",message);
		        saveMessages(request,errorHeadingMessage);							
				return mapping.findForward(INVALID_ACCESS_FORWARD);
			}catch (DataManagerException managerExp) {

				Logger.logError(MODULE, "Error during Data Manager operation , reason : " + managerExp.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
				request.setAttribute("errorDetails", errorElements);
				ActionMessages messages = new ActionMessages();
				if(managerExp.getCause() instanceof com.elitecore.netvertexsm.datamanager.core.exceptions.constraintviolation.ConstraintViolationException){
					ActionMessage message = new ActionMessage("database.datasource.delete.failure");
					ActionMessage messageReason = new ActionMessage("database.datasource.delete.constraint.failure");
					messages.add("information", message);
					messages.add("information", messageReason);
					saveErrors(request, messages);
				}else{
					ActionMessage message = new ActionMessage("general.error");
					messages.add("information", message);
					saveErrors(request, messages);
				}

			}catch(Exception managerExp){
				Logger.logError(MODULE, "Error during Data Manager operation , reason : " + managerExp.getMessage());            
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
				request.setAttribute("errorDetails", errorElements);
			}

	        ActionMessages errorHeadingMessage = new ActionMessages();
	        ActionMessage message = new ActionMessage("database.datasource.error.heading","deleting");
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
	        message = new ActionMessage("database.datasource.error.heading","deleting");
	        errorHeadingMessage.add("errorHeading",message);
	        saveMessages(request,errorHeadingMessage);			
			return mapping.findForward(INVALID_ACCESS_FORWARD);	
		}
	}
}