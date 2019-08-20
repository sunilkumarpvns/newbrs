package com.elitecore.netvertexsm.web.servicepolicy.pcrfservicepolicy;

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

import com.elitecore.netvertexsm.blmanager.servicepolicy.ServicePolicyBLManager;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.servicepolicy.pcrfservicepolicy.data.PCRFServicePolicyData;
import com.elitecore.netvertexsm.util.constants.BaseConstant;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.netvertexsm.web.servicepolicy.pcrfservicepolicy.form.MiscPCRFServicePolicyForm;

public class MiscPCRFServicePolicyAction extends BaseWebAction {
	private static final String SUCCESS_FORWARD = "success";
	private static final String LIST_FORWARD = "searchPCRFPolicyList";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.DELETE_PCRF_POLICY;
	
	
	
	public ActionForward execute(ActionMapping mapping,ActionForm actionForm,HttpServletRequest request,HttpServletResponse response) throws Exception{
            
		Logger.logInfo(MODULE,"Enter Execute method of "+getClass().getName());
		if(checkAccess(request, ACTION_ALIAS)){
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			String actionMessage="authpolicy.search.failure";
			MiscPCRFServicePolicyForm miscPCRFServicePolicyForm =(MiscPCRFServicePolicyForm)actionForm;
			ServicePolicyBLManager servicePolicyBLManager = new ServicePolicyBLManager();
			PCRFServicePolicyData pcrfServicePolicyData = new PCRFServicePolicyData();
			pcrfServicePolicyData.setStatus("All");
			
			try{
				List<Long> policyIdList = new ArrayList<Long>();
				String[] strPolicyIds = request.getParameterValues("select"); 

				for(int i=0; i<strPolicyIds.length; i++) {				
					policyIdList.add(Long.parseLong(strPolicyIds[i]));
				}
																				
				if(miscPCRFServicePolicyForm.getAction() != null){
					if(miscPCRFServicePolicyForm.getAction().equalsIgnoreCase("delete")){					
					servicePolicyBLManager.delete(policyIdList,staffData,ACTION_ALIAS);
					ActionMessage message = new ActionMessage("servicepolicy.pcrf.init.delete.success");
					ActionMessages messages1 = new ActionMessages();
					messages1.add("information", message);
					saveMessages(request,messages1);
					request.setAttribute("responseUrl","/searchPCRFPolicy.do");
					return mapping.findForward(SUCCESS_FORWARD);
					}else if(miscPCRFServicePolicyForm.getAction().equalsIgnoreCase("show")){
						actionMessage="pcrfpolicy.statuschange.failure";
						String	actionAlias=ConfigConstant.CHANGE_PCRF_POLICY_STATUS;
						servicePolicyBLManager.updatePCRFPolicyStatus(Arrays.asList(strPolicyIds),BaseConstant.SHOW_STATUS_ID,staffData,actionAlias);			

					}else if(miscPCRFServicePolicyForm.getAction().equalsIgnoreCase("hide")){
						actionMessage="pcrfpolicy.statuschange.failure";
						String	actionAlias=ConfigConstant.CHANGE_PCRF_POLICY_STATUS;
						servicePolicyBLManager.updatePCRFPolicyStatus(Arrays.asList(strPolicyIds),BaseConstant.HIDE_STATUS_ID,staffData,actionAlias);
					}					
				}
				
				if(request.getParameter("status")!= null){
					miscPCRFServicePolicyForm.setStatus(request.getParameter("status"));
				}	

				if(miscPCRFServicePolicyForm.getStatus() != null){

					if(miscPCRFServicePolicyForm.getStatus().equals("Active")){				
						pcrfServicePolicyData.setStatus("CST01");

					}else if(miscPCRFServicePolicyForm.getStatus().equals("Inactive")){
						pcrfServicePolicyData.setStatus("CST02");
					}
				}
				return mapping.findForward(LIST_FORWARD);
			}catch(Exception managerExp){
				Logger.logError(MODULE,"Error during Data Manager operation, reason : "+ managerExp.getMessage());
                Logger.logTrace(MODULE,managerExp);
        		Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
      			request.setAttribute("errorDetails", errorElements);
                ActionMessage message = new ActionMessage("servicepolicy.pcrf.init.delete.failed");
                ActionMessage reasonMessage = new ActionMessage("servicepolicy.pcrf.configured.in.cdr.driver");
                
                ActionMessages messages = new ActionMessages();
                messages.add("information", message);
                messages.add("reason", reasonMessage);
                saveErrors(request, messages);
                  
			}
            ActionMessages errorHeadingMessage = new ActionMessages();
            ActionMessage message = new ActionMessage("servicepolicy.pcrf.error.heading","deleting");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage); 
		    return mapping.findForward(FAILURE_FORWARD);
		}else{
	        Logger.logWarn(MODULE, "Error during Data Manager operation ");
	        ActionMessage message = new ActionMessage("general.user.restricted");
	        ActionMessages messages = new ActionMessages();
	        messages.add("information", message);
	        saveErrors(request, messages);
	        
            ActionMessages errorHeadingMessage = new ActionMessages();
            message = new ActionMessage("servicepolicy.pcrf.error.heading","deleting");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage); 
		return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
}