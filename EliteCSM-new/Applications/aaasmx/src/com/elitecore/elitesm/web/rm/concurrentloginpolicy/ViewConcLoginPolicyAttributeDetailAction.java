package com.elitecore.elitesm.web.rm.concurrentloginpolicy;

import java.util.List;

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
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.radius.base.BaseDictionaryAction;

public class ViewConcLoginPolicyAttributeDetailAction extends BaseDictionaryAction{
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String VIEW_FORWARD    = "viewConcurrentLoginPolicyDetail";
	private static final String  MODULE         = "VIEW CONCURRENT LOGIN POLICY BASIC DETAIL";	
	private static final String strViewNasPortTypeDetails = "viewNasPortTypeDetails";
	private static final String ACTION_ALIAS = "VIEW_CONCURRENT_LOGIN_POLICY_ACTION";
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		if((checkAccess(request, ACTION_ALIAS))){
			Logger.logTrace(MODULE,"Enter execute method of"+getClass().getName());
			ConcurrentLoginPolicyBLManager concurrentLoginPolicyBLManager = new ConcurrentLoginPolicyBLManager();
			String strConcurrentLoginPolicyId = request.getParameter("concurrentLoginPolicyId");
			
		    try {
				
		    	String concurrentLoginPolicyId=null;
				if( Strings.isNullOrEmpty(strConcurrentLoginPolicyId) == false ){
					concurrentLoginPolicyId = strConcurrentLoginPolicyId.trim();
				}
				
				Logger.logDebug(MODULE,"concurrentLoginPolicyId :"+concurrentLoginPolicyId);
				IConcurrentLoginPolicyData concurrentLoginPolicyData = new ConcurrentLoginPolicyData();
				
				concurrentLoginPolicyData= concurrentLoginPolicyBLManager.getConcurrentLoginPolicyById(concurrentLoginPolicyId);
				
				request.setAttribute("concurrentLoginPolicyData",concurrentLoginPolicyData);
				
				List lstConcurrentLoginPolicyDetails = concurrentLoginPolicyData.getConcurrentLoginPolicyDetail();
				
				request.getSession().removeAttribute("loginPolicyDetail");						
				request.getSession().setAttribute("loginPolicyDetail",lstConcurrentLoginPolicyDetails);
				request.setAttribute("action",strViewNasPortTypeDetails);
				System.out.println("now forwarding to view ");
				return mapping.findForward(VIEW_FORWARD);
			} catch (Exception e) {
				Logger.logError(MODULE,e.getMessage());
				e.printStackTrace();
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
