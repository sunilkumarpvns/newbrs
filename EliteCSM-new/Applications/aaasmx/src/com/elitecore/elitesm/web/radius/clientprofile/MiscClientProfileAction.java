package com.elitecore.elitesm.web.radius.clientprofile;

import java.util.Arrays;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.radius.clientprofile.ClientProfileBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.radius.clientprofile.forms.MiscClientProfileForm;
import com.elitecore.elitesm.web.radius.clientprofile.forms.SearchClientProfileForm;

public class MiscClientProfileAction extends BaseWebAction{

	private static final String SUCCESS_FORWARD = "success";	
	private static final String LIST_FORWARD = "clientProfileSearchList";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.SEARCH_CLIENT_PROFILE;
	private static final String ACTION_ALIAS_DELETE = ConfigConstant.DELETE_CLIENT_PROFILE;

	public ActionForward execute(ActionMapping mapping,ActionForm actionForm,HttpServletRequest request,HttpServletResponse response) throws Exception {
		Logger.logTrace(MODULE,"Enter execute method of "+getClass().getName());

		if((checkAccess(request, ACTION_ALIAS)) || (checkAccess(request,ACTION_ALIAS_DELETE))){
			try{
				MiscClientProfileForm clientProfileForm = (MiscClientProfileForm)actionForm;
				ClientProfileBLManager clientProfileBLManager  = new ClientProfileBLManager();

				if(clientProfileForm.getAction() != null){
					String[] strSelectedIds = request.getParameterValues("select");
					if(strSelectedIds != null){
						Date currDate = new Date(); 
						if(clientProfileForm.getAction().equalsIgnoreCase("delete")){
							checkActionPermission(request, ACTION_ALIAS_DELETE);
							IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));

							clientProfileBLManager.deleteById(Arrays.asList(strSelectedIds),staffData);
							int strSelectedIdsLen = strSelectedIds.length;
							long currentPageNumber = getCurrentPageNumberAfterDel(strSelectedIdsLen,clientProfileForm.getPageNumber(),clientProfileForm.getTotalPages(),clientProfileForm.getTotalRecords());

							request.setAttribute("responseUrl","/searchClientProfile.do?clientProfileName="+clientProfileForm.getClientProfileName()+"&clientTypeId="+clientProfileForm.getClientTypeId()+"&vendorInstanceId="+clientProfileForm.getVendorInstanceId()+"&pageNumber="+currentPageNumber+"&totalPages="+clientProfileForm.getTotalPages()+"&totalRecords="+clientProfileForm.getTotalRecords());
							ActionMessage message = new ActionMessage("clientprofile.delete.success");
							ActionMessages messages1 = new ActionMessages();
							messages1.add("information",message);
							saveMessages(request,messages1);
							return mapping.findForward(SUCCESS_FORWARD);
						}
					}
				}

				SearchClientProfileForm searchClientProfileForm = new SearchClientProfileForm();

				searchClientProfileForm.setProfileName(clientProfileForm.getClientProfileName());
				searchClientProfileForm.setClientTypeId(String.valueOf(clientProfileForm.getClientTypeId()));
				searchClientProfileForm.setVendorInstanceId(String.valueOf(clientProfileForm.getVendorInstanceId()));
				searchClientProfileForm.setPageNumber(clientProfileForm.getPageNumber());
				searchClientProfileForm.setTotalPages(clientProfileForm.getTotalPages());
				searchClientProfileForm.setTotalRecords(clientProfileForm.getTotalRecords());
				//searchClientProfileForm.setLstDatabaseDS(miscDatabaseDSForm.getListdatabaseDS());
				searchClientProfileForm.setAction(BaseConstant.LISTACTION);
				request.setAttribute("searchClientProfileForm",searchClientProfileForm);
				return mapping.findForward(LIST_FORWARD);

			}catch(ActionNotPermitedException e){
				Logger.logError(MODULE,"Error :-" + e.getMessage());
				printPermitedActionAlias(request);
				ActionMessages messages = new ActionMessages();
				messages.add("information", new ActionMessage("general.user.restricted"));
				saveErrors(request, messages);
				return mapping.findForward(INVALID_ACCESS_FORWARD);
			}catch (DataManagerException managerExp) {

				Logger.logError(MODULE, "Error during Data Manager operation , reason : " + managerExp.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
				request.setAttribute("errorDetails", errorElements);
				ActionMessages messages = new ActionMessages();
				if(managerExp.getCause() instanceof com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.ConstraintViolationException){
					ActionMessage message = new ActionMessage("clientprofile.delete.failure");
					messages.add("information", message);
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
