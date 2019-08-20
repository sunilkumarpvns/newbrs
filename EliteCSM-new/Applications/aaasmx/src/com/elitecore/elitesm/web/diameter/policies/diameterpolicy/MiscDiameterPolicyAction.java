/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   MiscDiameterpolicyAction.java                 		
 * ModualName diameterpolicy    			      		
 * Created on 16 May, 2011
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.web.diameter.policies.diameterpolicy; 

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.diameter.diameterpolicy.DiameterPolicyBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.diameter.policies.diameterpolicy.forms.MiscDiameterPolicyForm;
import com.elitecore.elitesm.web.diameter.policies.diameterpolicy.forms.SearchDiameterPolicyForm;

public class MiscDiameterPolicyAction extends BaseWebAction { 

	private static final String SUCCESS_FORWARD = "success";               
	private static final String FAILURE_FORWARD = "failure";               
	private static final String MODULE ="DIAMETERPOLICY";
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_AUTHORIZATION_POLICY_STATUS;
	private static final String ACTION_ALIAS_DELETE = ConfigConstant.DELETE_AUTHORIZATION_POLICY; 
	private static final String LIST_FORWARD = "diameterPolicySearchList";

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logTrace(MODULE,"Enter execute method of "+getClass().getName());
		try{
			MiscDiameterPolicyForm miscDiameterPolicyForm=(MiscDiameterPolicyForm)form;
			DiameterPolicyBLManager diameterPolicyBLManager = new DiameterPolicyBLManager();
			if(miscDiameterPolicyForm.getAction() != null){
				String[] strSelectedIds = request.getParameterValues("select");
				if(strSelectedIds != null){
					List<String> listSelectedIDs = new ArrayList<String>();
					for(int i=0;i<strSelectedIds.length;i++){
						listSelectedIDs.add(strSelectedIds[i]);
					}

					if(miscDiameterPolicyForm.getAction() != null) {
						if(miscDiameterPolicyForm.getAction().equalsIgnoreCase("show")){
							checkActionPermission(request, ACTION_ALIAS);
							IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
							diameterPolicyBLManager.updateStatus(listSelectedIDs, staffData,BaseConstant.SHOW_STATUS_ID);
						}else if(miscDiameterPolicyForm.getAction().equalsIgnoreCase("hide")){
							checkActionPermission(request, ACTION_ALIAS);
							IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
							diameterPolicyBLManager.updateStatus(listSelectedIDs, staffData, BaseConstant.HIDE_STATUS_ID);
						}else if(miscDiameterPolicyForm.getAction().equalsIgnoreCase("delete")){
							checkActionPermission(request, ACTION_ALIAS_DELETE);
							IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));

							diameterPolicyBLManager.deleteById(Arrays.asList(strSelectedIds), staffData);
							int strSelectedIdsLen = strSelectedIds.length;
							long currentPageNumber = getCurrentPageNumberAfterDel(strSelectedIdsLen,miscDiameterPolicyForm.getPageNumber(),miscDiameterPolicyForm.getTotalPages(),miscDiameterPolicyForm.getTotalRecords());

							String status = "All";

							request.setAttribute("responseUrl","/searchDiameterPolicy.do?name="+miscDiameterPolicyForm.getName()+"&pageNumber="+currentPageNumber+"&totalPages="+miscDiameterPolicyForm.getTotalPages()+"&totalRecords="+miscDiameterPolicyForm.getTotalRecords()+"&status="+status);
							ActionMessage message = new ActionMessage("diameter.diameterpolicy.delete.success");
							ActionMessages messages1 = new ActionMessages();
							messages1.add("information",message);
							saveMessages(request,messages1);
							System.out.println("MiscRadius Policy Action");
							return mapping.findForward(SUCCESS_FORWARD);
						}
					}
				}
			}

			SearchDiameterPolicyForm searchDiameterPolicyForm = new SearchDiameterPolicyForm();
			searchDiameterPolicyForm.setStatus(miscDiameterPolicyForm.getStatus());    		
			searchDiameterPolicyForm.setName(miscDiameterPolicyForm.getName());
			searchDiameterPolicyForm.setPageNumber(miscDiameterPolicyForm.getPageNumber());
			searchDiameterPolicyForm.setTotalPages(miscDiameterPolicyForm.getTotalPages());
			searchDiameterPolicyForm.setTotalRecords(miscDiameterPolicyForm.getTotalRecords());
			searchDiameterPolicyForm.setAction(BaseConstant.LISTACTION);
			request.setAttribute("searchDiameterPolicyForm",searchDiameterPolicyForm);
			return mapping.findForward(LIST_FORWARD);

		}catch(ActionNotPermitedException e){
			Logger.logError(MODULE,"Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}catch(Exception managerExp){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + managerExp.getMessage());            
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
			request.setAttribute("errorDetails", errorElements);
		}

		return mapping.findForward(FAILURE_FORWARD);
	}
}
