/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   CreateGracepolicyAction.java                 		
 * ModualName GracePolicy    			      		
 * Created on 23 December, 2010
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.web.servermgr.gracepolicy; 

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.action.ActionRedirect;

import com.elitecore.elitesm.blmanager.servermgr.gracepolicy.GracePolicyBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.gracepolicy.data.GracepolicyData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servermgr.gracepolicy.forms.GracepolicyForm;

public class GracepolicyAction extends BaseWebAction { 

	private static final String FAILURE_FORWARD = "failure";               
	private static final String GRACE_POLICY_MODULE ="GRACEPOLICY";
	private static final String LIST_FORWARD = "listgracepolicy";
	private static final String ACTION_ALIAS_CREATE = ConfigConstant.CREATE_GRACE_POLICY;
	private static final String ACTION_ALIAS_DELETE = ConfigConstant.UPDATE_GRACE_POLICY;
	private static final String ACTION_ALIAS_UPDATE = ConfigConstant.DELETE_GRACE_POLICY;
	private static final String DELETE = "delete";
	private static final String UPDATE = "update";
	private static final String ERROR_DETAILS = "errorDetails";
	private static final String ADD = "Add";
	private static final String INFORMATION = "information";
	
	@Override
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{ 

		Logger.logInfo(GRACE_POLICY_MODULE, "Entered execute method of " + getClass().getName()); 
		if(checkAccess(request, ACTION_ALIAS_CREATE) || checkAccess(request, ACTION_ALIAS_DELETE) || checkAccess(request, ACTION_ALIAS_UPDATE)){
			com.elitecore.elitesm.web.servermgr.gracepolicy.forms.GracepolicyForm gracePolicyForm = (com.elitecore.elitesm.web.servermgr.gracepolicy.forms.GracepolicyForm)form;
			GracePolicyBLManager gracePolicyBLManager = new GracePolicyBLManager();
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			try {		                                                                                                        
				String action = gracePolicyForm.getAction();

				if(ADD.equalsIgnoreCase(action)){
					checkActionPermission(request, ACTION_ALIAS_CREATE); 
					GracepolicyData gracepolicyData = new GracepolicyData();

					gracepolicyData.setName(gracePolicyForm.getGracePolicyName());
					gracepolicyData.setValue(gracePolicyForm.getGracePolicyValue());
					gracePolicyBLManager.createGracePolicy(gracepolicyData, staffData, false);

				}else if(DELETE.equals(action)){
					checkActionPermission(request,ACTION_ALIAS_DELETE);
					String[] strSelectedIds = request.getParameterValues("select");
					
					if(strSelectedIds != null){
						gracePolicyBLManager.deleteGracePolicyById(Arrays.asList(strSelectedIds), staffData);
					}
				}else if(UPDATE.equals(action)){
					checkActionPermission(request,ACTION_ALIAS_UPDATE);
					
					GracepolicyData gracepolicyData = null;
					gracepolicyData=convertFormToBean(gracePolicyForm);
					gracePolicyBLManager.updateGracePolicyById(gracepolicyData, staffData);
				}  
				return new ActionRedirect(mapping.findForward(LIST_FORWARD)); 
			}catch (DuplicateInstanceNameFoundException dpfExp) {
				Logger.logError(GRACE_POLICY_MODULE, "Returning error forward from" + getClass().getName());
				Logger.logTrace(GRACE_POLICY_MODULE,dpfExp);
				Object [] errorElements = EliteExceptionUtils.getFullStackTraceAsArray(dpfExp);
				request.setAttribute(ERROR_DETAILS, errorElements);
				ActionMessage message = new ActionMessage("gracepolicy.create.duplicate.failure",gracePolicyForm.getGracePolicyName());
				ActionMessages messages = new ActionMessages();
				messages.add(INFORMATION,message);
				saveErrors(request,messages);
			}catch(DataManagerException e){
				Logger.logError(GRACE_POLICY_MODULE, "Error during Data Manager operation , Reason : " + e.getMessage());
				Object [] errorElements = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute(ERROR_DETAILS, errorElements);
				ActionMessage message = null;
				if(ADD.equals(gracePolicyForm.getAction())){
					message = new ActionMessage("gracepolicy.create.failure");
				}else if(UPDATE.equals(gracePolicyForm.getAction())){
					message = new ActionMessage("gracepolicy.update.failure");
				}else if(DELETE.equals(gracePolicyForm.getAction())){
					message = new ActionMessage("gracepolicy.delete.failure");
				}
				ActionMessages messages = new ActionMessages();
				messages.add(INFORMATION, message);
				saveErrors(request, messages);
			}catch(Exception e){
				Logger.logError(GRACE_POLICY_MODULE, "Error during Data Manager operation , Reason : " + e.getMessage());
				Object [] errorElements = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute(ERROR_DETAILS, errorElements);
				ActionMessage message = null;
				if(ADD.equals(gracePolicyForm.getAction())){
					message = new ActionMessage("gracepolicy.create.failure");
				}else if(UPDATE.equals(gracePolicyForm.getAction())){
					message = new ActionMessage("gracepolicy.update.failure");
				}else if(DELETE.equals(gracePolicyForm.getAction())){
					message = new ActionMessage("gracepolicy.delete.failure");
				}
				ActionMessages messages = new ActionMessages();
				messages.add(INFORMATION, message);
				saveErrors(request, messages);
			}                                                                                                                                   
			return mapping.findForward(FAILURE_FORWARD); 
		}else{
			Logger.logError(GRACE_POLICY_MODULE, "Error during Data Manager operation ");
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add(INFORMATION, message);
			saveErrors(request, messages);

			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}

	private GracepolicyData convertFormToBean(GracepolicyForm gracePolicyForm) {

		GracepolicyData gracepolicyData = new GracepolicyData();
		gracepolicyData.setGracePolicyId(gracePolicyForm.getGracePolicyId());
		gracepolicyData.setName(gracePolicyForm.getGracePolicyName());
		gracepolicyData.setValue(gracePolicyForm.getGracePolicyValue());

		return gracepolicyData;

	}
}
