package com.elitecore.elitesm.web.rm.concurrentloginpolicy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.rm.concurrentloginpolicy.ConcurrentLoginPolicyBLManager;
import com.elitecore.elitesm.datamanager.rm.concurrentloginpolicy.data.ConcurrentLoginPolicyData;
import com.elitecore.elitesm.datamanager.rm.concurrentloginpolicy.data.IConcurrentLoginPolicyData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.radius.base.BaseDictionaryAction;


public class ViewConcurrentLoginPolicyAction extends BaseDictionaryAction {
	private static final String VIEW_FORWARD = "viewConcurrentLoginPolicyDetail";
	private static final String FAILURE_FORWARD = "failure";
	private static final String MODULE= "VIEW CONC. LOGIN POLICY ACTION";
	private static final String ACTION_ALIAS = ConfigConstant.VIEW_CONCURRENT_LOGIN_POLICY_ACTION;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		if((checkAccess(request, ACTION_ALIAS))){
			try {
				Logger.logTrace(MODULE,"Enter execute method of"+getClass().getName());
				
				String strConcPolicyId = request.getParameter("concurrentLoginId");
				if(strConcPolicyId == null){
					strConcPolicyId = request.getParameter("concurrentLoginPolicyId");
				}
				
				String concurrentLoginPolicyId=null;
				if( Strings.isNullOrEmpty(strConcPolicyId) == false){
					concurrentLoginPolicyId = strConcPolicyId.trim();
				}

				Logger.logDebug(MODULE,"concurrentLoginPolicyId :"+concurrentLoginPolicyId);
			 	
				ConcurrentLoginPolicyBLManager blManager = new ConcurrentLoginPolicyBLManager();
				
				if(strConcPolicyId!=null && strConcPolicyId.length() > 0){
					IConcurrentLoginPolicyData concurrentLoginPolicyData= new ConcurrentLoginPolicyData();
					
					concurrentLoginPolicyData= blManager.getConcurrentLoginPolicyById(concurrentLoginPolicyId);
					
					request.setAttribute("concurrentLoginPolicyData",concurrentLoginPolicyData);
					
				}
				
				return mapping.findForward(VIEW_FORWARD);
			} catch (Exception e) {
				Logger.logError(MODULE,e.getMessage());
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
