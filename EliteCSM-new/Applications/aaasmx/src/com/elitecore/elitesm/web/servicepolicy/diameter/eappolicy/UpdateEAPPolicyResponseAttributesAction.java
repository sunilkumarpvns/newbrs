package com.elitecore.elitesm.web.servicepolicy.diameter.eappolicy;

import java.util.LinkedHashSet;
import java.util.Set;

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
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.eappolicy.data.EAPResponseAttributes;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servicepolicy.diameter.eappolicy.forms.UpdateEAPPolicyForm;

public class UpdateEAPPolicyResponseAttributesAction extends BaseWebAction {
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_DIAMETER_EAP_PROFILE_DRIVER;
	private static final String UPDATE_EAP_RESPONSE_ATTRIBUTES = "updateEAPPolicyResponseAttrubutes";
	
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
				eapPolicyData.setEapPolicyId(updateEAPPolicyForm.getPolicyId());
				eapPolicyData.setAuditUId(updateEAPPolicyForm.getAuditUId());
				eapPolicyData.setName(updateEAPPolicyForm.getName());
				
				//Get Response Attributes data
				
				String commandCodes[] = request.getParameterValues("commandCode");
				String responseAttributes[] = request.getParameterValues("responseAttributes");
				
				Set<EAPResponseAttributes> eapResponseAttributesSet = new LinkedHashSet<EAPResponseAttributes>();
				
				if( commandCodes!=null ){
					for (int j = 0; j < commandCodes.length; j++) {
						if(commandCodes[j]!=null && commandCodes[j].trim().length()>0){
							
							EAPResponseAttributes eapResponseAttributes = new EAPResponseAttributes();
							eapResponseAttributes.setCommandCodes(commandCodes[j]);
							eapResponseAttributes.setResponseAttributes(responseAttributes[j]);
						
							eapResponseAttributesSet.add(eapResponseAttributes);
							Logger.logInfo(MODULE, "eapResponseAttributes: "+ eapResponseAttributes);
						}
					}
				}
				
				eapPolicyData.setEapResponseAttributesSet(eapResponseAttributesSet);
				
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				
				staffData.setAuditId(eapPolicyData.getAuditUId());
				staffData.setAuditName(eapPolicyData.getName());
				
				/*Update in DataBase*/
				blManager.updateResponseAttributes(eapPolicyData,staffData,ACTION_ALIAS);
				request.setAttribute("responseUrl", "/initViewEAPPolicy.do?policyId="+eapPolicyData.getEapPolicyId()); 
				ActionMessage message = new ActionMessage("responseattributes.update.success");
				ActionMessages messages = new ActionMessages();          
				messages.add("information", message);                    
				saveMessages(request,messages);         				   
				return mapping.findForward(SUCCESS);
			}else{
				eapPolicyData = blManager.getEAPPolicyById(updateEAPPolicyForm.getPolicyId());
				updateEAPPolicyForm.setAdditionalDriverRelDataList(eapPolicyData.getEapAdditionalDriverRelDataList());
				updateEAPPolicyForm.setName(eapPolicyData.getName());
				updateEAPPolicyForm.setAuditUId(eapPolicyData.getAuditUId());
				updateEAPPolicyForm.setEapResponseSet(eapPolicyData.getEapResponseAttributesSet());
				
				request.setAttribute("eapPolicyData", eapPolicyData);
				request.setAttribute("updateEAPPolicyForm", updateEAPPolicyForm);
				return mapping.findForward(UPDATE_EAP_RESPONSE_ATTRIBUTES);
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
}
