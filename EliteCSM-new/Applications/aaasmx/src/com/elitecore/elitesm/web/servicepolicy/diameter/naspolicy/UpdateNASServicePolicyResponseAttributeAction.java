package com.elitecore.elitesm.web.servicepolicy.diameter.naspolicy;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.elitesm.blmanager.servicepolicy.diameter.DiameterNASPolicyBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyAcctDriverRelData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyInstData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASResponseAttributes;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servicepolicy.diameter.naspolicy.forms.UpdateNASServicePolicyAccountingParamsForm;
import com.elitecore.elitesm.web.servicepolicy.diameter.naspolicy.forms.UpdateNASServicePolicyResponseAttributesForm;

public class UpdateNASServicePolicyResponseAttributeAction extends BaseWebAction{
	
	
	private static final String SUCCESS_FORWARD = "updateNASServicePolicyResponseAttributes";
	private static String ACTION_ALIAS = ConfigConstant.UPDATE_NAS_SERVICE_POLICY_ACCOUNTING_PARAMS;

	private static final String MODULE = "UpdateNasServicePolicyAccountingParams";
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		try{
			
			checkActionPermission(request, ACTION_ALIAS);
			Logger.logTrace(MODULE,"Enter execute method of "+getClass().getName());
			UpdateNASServicePolicyResponseAttributesForm nasPolicyForm = (UpdateNASServicePolicyResponseAttributesForm)form;
			
			if(nasPolicyForm.getAction() == null) {
				
				DiameterNASPolicyBLManager blManager = new DiameterNASPolicyBLManager();
				NASPolicyInstData data = blManager.getDiameterServicePolicyDataByPolicyId(nasPolicyForm.getNasPolicyId());
				
				Set<NASResponseAttributes> nasResponseAttributesSet = new  LinkedHashSet<NASResponseAttributes>();
				List<NASResponseAttributes> nasResponseAttributes = blManager.getDiameterServicePolicyNASResponseAttributes(nasPolicyForm.getNasPolicyId());
				
				nasResponseAttributesSet.addAll(nasResponseAttributes);
				
				nasPolicyForm.setNasResponseAttributesList(nasResponseAttributesSet);
				nasPolicyForm.setAuditUId(data.getAuditUId());
				nasPolicyForm.setName(data.getName());
				
				request.setAttribute("nasPolicyInstData",data);
				request.setAttribute("nasPolicyForm",nasPolicyForm);
				return mapping.findForward(SUCCESS_FORWARD);
			}else if(nasPolicyForm.getAction().equals("update")){
				
				DiameterNASPolicyBLManager blManager = new DiameterNASPolicyBLManager();
				
				NASPolicyInstData policyInstData = blManager.getDiameterServicePolicyDataByPolicyId(nasPolicyForm.getNasPolicyId());
				
				policyInstData.setNasPolicyId(nasPolicyForm.getNasPolicyId());
				
				//Get Response Attributes data
				
				String commandCodes[] = request.getParameterValues("commandCode");
				String responseAttributes[] = request.getParameterValues("responseAttributes");
				
				Set<NASResponseAttributes> nasResponseAttributesSet = new LinkedHashSet<NASResponseAttributes>();
				
				if( commandCodes!=null ){
					for (int j = 0; j < commandCodes.length; j++) {
						if(commandCodes[j]!=null && commandCodes[j].trim().length()>0){
							
							NASResponseAttributes nasResponseAttributes = new NASResponseAttributes();
							nasResponseAttributes.setCommandCodes(commandCodes[j]);
							nasResponseAttributes.setResponseAttributes(responseAttributes[j]);
						
							nasResponseAttributesSet.add(nasResponseAttributes);
							Logger.logInfo(MODULE, "nasResponseAttributes: "+ nasResponseAttributes);
						}
					}
				}
				
				policyInstData.setNasResponseAttributesSet(nasResponseAttributesSet);
				
				policyInstData.setName(nasPolicyForm.getName());
				policyInstData.setAuditUId(nasPolicyForm.getAuditUId());
				
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				
				staffData.setAuditId(policyInstData.getAuditUId());
				staffData.setAuditName(policyInstData.getName());
				
				blManager.updateNasResponseAttribute(policyInstData,staffData,ACTION_ALIAS);
				request.setAttribute("responseUrl","/viewNASServicePolicyDetail.do?nasPolicyId="+nasPolicyForm.getNasPolicyId());
                ActionMessage message = new ActionMessage("diameter.naspolicy.responseattribute.update.success");
                ActionMessages messages = new ActionMessages();
                messages.add("information", message);
                saveMessages(request, messages);
                return mapping.findForward(SUCCESS);
			
			}
		}catch(ActionNotPermitedException e){
			Logger.logError(MODULE,"Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}catch(Exception e){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);

		}
		return mapping.findForward(FAILURE);
	}
}
