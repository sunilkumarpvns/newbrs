package com.elitecore.elitesm.web.servicepolicy.diameter.eappolicy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.servicepolicy.diameter.EAPPolicyBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.eappolicy.data.EAPPolicyData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servicepolicy.diameter.eappolicy.forms.UpdateEAPPolicyForm;

public class UpdateEapPolicyRFC4372CUIDetailAction extends BaseWebAction {
	private final static String ACTION_ALIAS = ConfigConstant.UPDATE_DIAMETER_EAP_AUTHORIZATION_PARAMETER;
	private final static String UPDATE_EAP_RFC_DETAILS = "updateEAPRFC4372CUIDetails";
	
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response){
		try{
			Logger.logInfo(ACTION_ALIAS, "Enter execute method of "+getClass().getName());
			checkActionPermission(request, ACTION_ALIAS);
			
			UpdateEAPPolicyForm updateEAPPolicyForm = (UpdateEAPPolicyForm) form;
			EAPPolicyBLManager blManager = new EAPPolicyBLManager();	
			EAPPolicyData eapPolicyData;
			if("update".equals(updateEAPPolicyForm.getAction())){
				eapPolicyData = new EAPPolicyData();
				eapPolicyData = blManager.getEAPPolicyById(updateEAPPolicyForm.getPolicyId());
				convertFormToBean(updateEAPPolicyForm,eapPolicyData);
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				
				staffData.setAuditId(eapPolicyData.getAuditUId());
				staffData.setAuditName(eapPolicyData.getName());
				/*Update in DataBase*/
				blManager.updateRFC4372CUIDetails(eapPolicyData,staffData,ACTION_ALIAS);
				
				request.setAttribute("responseUrl", "/initViewEAPPolicy.do?policyId="+eapPolicyData.getEapPolicyId()); 
				ActionMessage message = new ActionMessage("diameter.eappolicy.update");
				ActionMessages messages = new ActionMessages();          
				messages.add("information", message);                    
				saveMessages(request,messages);         				   
				return mapping.findForward(SUCCESS);
			}else{
				eapPolicyData = blManager.getEAPPolicyById(updateEAPPolicyForm.getPolicyId());
				convertBeanToForm(eapPolicyData, updateEAPPolicyForm);
				
				request.setAttribute("updateEAPPolicyForm",updateEAPPolicyForm);
				request.setAttribute("eapPolicyData", eapPolicyData);
				return mapping.findForward(UPDATE_EAP_RFC_DETAILS);
			}
		}catch(ActionNotPermitedException e){
			Logger.logError(MODULE, "Restricted to do action.");
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);	
		}catch(Exception e){
			Logger.logError(MODULE, "Error during Data Manager operation , reason :" + e.getMessage());                              
			Logger.logTrace(MODULE, e);                                                                                               
			ActionMessage message = new ActionMessage("diameter.eappolicy.updateerror");                                                         
			ActionMessages messages = new ActionMessages();                                                                                 
			messages.add("information", message);                                                                                           
			saveErrors(request, messages);  
		}
		return mapping.findForward(FAILURE);
	}
	
	private void convertBeanToForm(EAPPolicyData eapPolicyData, UpdateEAPPolicyForm updateEAPPolicyForm){
		updateEAPPolicyForm.setPolicyId(eapPolicyData.getEapPolicyId());
		updateEAPPolicyForm.setName(eapPolicyData.getName());
		updateEAPPolicyForm.setAuditUId(eapPolicyData.getAuditUId());
		updateEAPPolicyForm.setCui(eapPolicyData.getCui());
		updateEAPPolicyForm.setCuiResponseAttributes(eapPolicyData.getCuiResponseAttributes());
		updateEAPPolicyForm.setAdvancedCuiExpression(eapPolicyData.getAdvancedCuiExpression());
	}
	private void convertFormToBean(UpdateEAPPolicyForm updateEAPPolicyForm, EAPPolicyData eapPolicyData){
		eapPolicyData.setEapPolicyId(updateEAPPolicyForm.getPolicyId());
		eapPolicyData.setAuditUId(updateEAPPolicyForm.getAuditUId());
		eapPolicyData.setName(updateEAPPolicyForm.getName());
		eapPolicyData.setCui(updateEAPPolicyForm.getCui());
		eapPolicyData.setCuiResponseAttributes(updateEAPPolicyForm.getCuiResponseAttributes());
		eapPolicyData.setAdvancedCuiExpression(updateEAPPolicyForm.getAdvancedCuiExpression());
	}
}
