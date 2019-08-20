package com.elitecore.elitesm.web.servicepolicy.rm.cgpolicy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.servicepolicy.rm.CGPolicyBLManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servicepolicy.rm.cgpolicy.data.CGPolicyData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servicepolicy.rm.cgpolicy.forms.ViewCGPolicyDetailsForm;

public class ViewCGPolicyDetailsAction extends BaseWebAction {
	private static final String VIEW_FORWARD = "viewCGPolicyDetails";
	private static final String FAILURE_FORWARD = "failure";
	private static final String MODULE="ViewCGPolicyDetails";
	private static final String ACTION_ALIAS=ConfigConstant.VIEW_CG_POLICY;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		if((checkAccess(request, ACTION_ALIAS))){		
			Logger.logTrace(MODULE,"Enter execute method of"+getClass().getName());
			try{
				ViewCGPolicyDetailsForm viewCGPolicyDetailsForm = (ViewCGPolicyDetailsForm)form;
				CGPolicyBLManager cgPolicyBLManager = new CGPolicyBLManager();
				String strCGPolicyID = request.getParameter("policyId");
				String cgPolicyId;
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				
				
				if(strCGPolicyID != null){
					cgPolicyId = strCGPolicyID;
				}else{
					cgPolicyId=viewCGPolicyDetailsForm.getPolicyId();
				}
				
				if( Strings.isNullOrEmpty(cgPolicyId) == false){
					CGPolicyData cgPolicyInstData = new CGPolicyData();
					cgPolicyInstData.setPolicyId(cgPolicyId);
					cgPolicyInstData = cgPolicyBLManager.getPolicyDataByPolicyId(cgPolicyId);					
					cgPolicyInstData = cgPolicyBLManager.getPolicyDataByPolicyId(cgPolicyId);
					request.setAttribute("cgPolicyData",cgPolicyInstData);
				}
				doAuditing(staffData, ACTION_ALIAS);
				return mapping.findForward(VIEW_FORWARD);
			}catch(Exception e){
				e.printStackTrace();
				Logger.logTrace(MODULE,e.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
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
