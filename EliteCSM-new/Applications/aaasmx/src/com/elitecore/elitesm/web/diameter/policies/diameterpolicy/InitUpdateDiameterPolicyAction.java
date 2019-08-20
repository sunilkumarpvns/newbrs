/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   InitUpdateDiameterpolicyAction.java                 		
 * ModualName diameterpolicy    			      		
 * Created on 16 May, 2011
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.web.diameter.policies.diameterpolicy; 

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.diameter.diameterpolicy.DiameterPolicyBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.diameter.diameterpolicy.data.DiameterPolicyData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.diameter.policies.diameterpolicy.forms.UpdateDiameterPolicyForm;

public class InitUpdateDiameterPolicyAction extends BaseWebAction { 

	private static final String FAILURE_FORWARD = "failure";               
	private static final String MODULE ="DIAMETERPOLICY";
	private static final String INITUPDATEDIAMETERPOLICY = "initUpdateDiameterPolicy"; 
	private static final String strUpdateBasicDetails = "updateDiameterPolicy";
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		ActionMessages messages = new ActionMessages();
		try {
			
			Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName()); 

			UpdateDiameterPolicyForm policyForm = (UpdateDiameterPolicyForm)form;

				DiameterPolicyBLManager blManager = new DiameterPolicyBLManager();			 
				DiameterPolicyData diameterPolicyData = blManager.getDiameterPolicyDataById(policyForm.getDiameterPolicyId());

				policyForm.setCheckItem(diameterPolicyData.getCheckItem());
				policyForm.setRejectItem(diameterPolicyData.getRejectItem());
				policyForm.setReplyItem(diameterPolicyData.getReplyItem());
				policyForm.setDiameterPolicyId(diameterPolicyData.getDiameterPolicyId());
				policyForm.setName(diameterPolicyData.getName());
				policyForm.setDescription(diameterPolicyData.getDescription());
				policyForm.setCreateDate(diameterPolicyData.getCreateDate());
				policyForm.setLastModifiedDate(diameterPolicyData.getLastModifiedDate());
				policyForm.setSystemGenerated(diameterPolicyData.getSystemGenerated());
				policyForm.setEditable(diameterPolicyData.getEditable());
				policyForm.setCommonStatusId(diameterPolicyData.getCommonStatusId());
				policyForm.setStatusChangeDate(diameterPolicyData.getStatusChangeDate());
				policyForm.setAuditUId(diameterPolicyData.getAuditUId());
				
				 if(diameterPolicyData.getCommonStatusId().equals(BaseConstant.SHOW_STATUS_ID)){
					 policyForm.setStatus(BaseConstant.SHOW_STATUS);
				 }else{
					 policyForm.setStatus(BaseConstant.HIDE_STATUS);
				 }
				
				policyForm.setLastModifiedByStaffId(diameterPolicyData.getLastModifiedByStaffId());
				policyForm.setCreatedByStaffId(diameterPolicyData.getCreatedByStaffId());

				request.setAttribute("diameterPolicyData",diameterPolicyData);
				request.setAttribute("policyForm",policyForm);
				request.setAttribute("action",strUpdateBasicDetails);

				return mapping.findForward(INITUPDATEDIAMETERPOLICY);       
		

		}catch (ActionNotPermitedException e) {
			Logger.logError(MODULE, "Error during Data Manager operation ");
			Logger.logTrace(MODULE, e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.user.restricted");
			messages.add("information", message);
		}catch (Exception authExp) {                                                                                           
			Logger.logError(MODULE, "Error during Data Manager operation , reason :" + authExp.getMessage());                              
			Logger.logTrace(MODULE, authExp);                                                                                               
			ActionMessage message = new ActionMessage("general.error");                                                         
			messages.add("information", message);                                                                                           
		}                    
		saveErrors(request, messages); 
		return mapping.findForward(FAILURE_FORWARD); 
	}
}
