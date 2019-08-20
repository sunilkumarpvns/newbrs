package com.elitecore.elitesm.web.servicepolicy.diameter.eappolicy;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.servermgr.eap.EAPConfigBLManager;
import com.elitecore.elitesm.blmanager.servicepolicy.diameter.EAPPolicyBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.eap.data.EAPConfigData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.eappolicy.data.EAPPolicyData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servicepolicy.diameter.eappolicy.forms.UpdateEAPPolicyForm;

public class InitUpdateEAPPolicyAction extends BaseWebAction {
	private static final String SUCCESS_FORWARD = "success";               
	private static final String FAILURE_FORWARD = "failure";               
	private static final String MODULE ="EAPPOLICY";
	private static final String INITUPDATEEAPPOLICY = "initUpdateEAPPolicy"; 
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_DIAMETER_EAP_POLICY;
	private static final String VIEW_ALIAS=ConfigConstant.VIEW_DIAMETER_EAP_POLICY;
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		ActionMessages messages = new ActionMessages();
		try {
						
			 checkActionPermission(request,ACTION_ALIAS);
			 Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName()); 
			 
			 UpdateEAPPolicyForm policyForm = (UpdateEAPPolicyForm)form;
			 
			 EAPPolicyBLManager blManager = new EAPPolicyBLManager();			 
			 EAPPolicyData eapPolicyData = blManager.getEAPPolicyById(policyForm.getPolicyId());
			 
			 EAPConfigBLManager eapConfigBLManager = new EAPConfigBLManager();
			 List<EAPConfigData> eapConfigurationList = eapConfigBLManager.getEapConfigurationList();
			 
			 IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				
			 policyForm.setName(eapPolicyData.getName());
			 policyForm.setDescription(eapPolicyData.getDescription());
			 policyForm.setPolicyId(eapPolicyData.getEapPolicyId());
			 policyForm.setRuleSet(eapPolicyData.getRuleSet());
			 policyForm.setSessionManagement(eapPolicyData.getSessionManagement());
			 policyForm.setStatus(eapPolicyData.getStatus());
			 policyForm.setDriversList(eapPolicyData.getDriverList());
			 policyForm.setEapConfigId(eapPolicyData.getEapConfigId());
			 policyForm.setMultipleUserIdentity(eapPolicyData.getMultipleUserIdentity());
			 policyForm.setStripUserIdentity((new Boolean(eapPolicyData.getStripUserIdentity())));
			 policyForm.setRealmPattern(eapPolicyData.getRealmPattern());
			 policyForm.setRealmSeparator(eapPolicyData.getRealmSeparator());
			 policyForm.setTrimUserIdentity((new Boolean(eapPolicyData.getTrimUserIdentity())));
			 policyForm.setTrimPassword((new Boolean(eapPolicyData.getTrimPassword())));
			 policyForm.setCaseSensitiveUserIdentity(eapPolicyData.getCaseSensitiveUserIdentity());
			 policyForm.setEapConfigurationList(eapConfigurationList);
			 policyForm.setEapResponseSet(eapPolicyData.getEapResponseAttributesSet());
			 policyForm.setAnonymousProfileIdentity(eapPolicyData.getAnonymousProfileIdentity());
			 policyForm.setWimax(eapPolicyData.getWimax());
			 policyForm.setGracePolicy(eapPolicyData.getGracePolicy());
			 policyForm.setAuditUId(eapPolicyData.getAuditUId());
			 
			 request.setAttribute("eapPolicyData",eapPolicyData);
			 request.setAttribute("policyForm",policyForm);
			 doAuditing(staffData, VIEW_ALIAS);
		     return mapping.findForward(INITUPDATEEAPPOLICY);             
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
		    ActionMessage message = new ActionMessage("~~~~key~~~");                                                         
		    messages.add("information", message);                                                                                           
		                                                                                                     
		}                    
        saveErrors(request, messages); 
		return mapping.findForward(FAILURE_FORWARD); 
	}

}
