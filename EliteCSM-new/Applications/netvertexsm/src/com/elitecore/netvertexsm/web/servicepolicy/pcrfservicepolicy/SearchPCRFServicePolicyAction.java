package com.elitecore.netvertexsm.web.servicepolicy.pcrfservicepolicy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.servicepolicy.ServicePolicyBLManager;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.servicepolicy.pcrfservicepolicy.data.PCRFServicePolicyData;
import com.elitecore.netvertexsm.util.constants.BaseConstant;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.cache.ConfigManager;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.netvertexsm.web.servicepolicy.pcrfservicepolicy.form.SearchPCRFServicePolicyForm;

public class SearchPCRFServicePolicyAction extends BaseWebAction {
	private static final String LIST_FORWARD = "searchPCRFService";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.SEARCH_PCRF_POLICY;
	
	public ActionForward execute(ActionMapping mapping,ActionForm actionForm,HttpServletRequest request,HttpServletResponse response) throws Exception{
            
		Logger.logInfo(MODULE,"Enter Execute method of "+getClass().getName());
		if(checkAccess(request, ACTION_ALIAS)){
			try{
				SearchPCRFServicePolicyForm searchPolicyForm = (SearchPCRFServicePolicyForm) actionForm;
				ServicePolicyBLManager pcrfPolicyBLManager = new ServicePolicyBLManager();
				PCRFServicePolicyData pcrfPolicySearchData = new PCRFServicePolicyData();
				
				Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
				int requiredPageNo=0;
				if(request.getParameter("pageNo") != null){
					requiredPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNo")));
		    	}else{
		    		requiredPageNo = new Long(searchPolicyForm.getPageNumber()).intValue();
		    	}
		        if (requiredPageNo == 0)
		            requiredPageNo = 1;																			
				
				String strName = searchPolicyForm.getName();					
				
				if(strName!=null && strName.length()>0) {
					pcrfPolicySearchData.setName(strName);
				}else {
					pcrfPolicySearchData.setName("");
				}																
									
				String actionAlias = ACTION_ALIAS;
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				PageList pageList = pcrfPolicyBLManager.search(pcrfPolicySearchData, requiredPageNo, pageSize,staffData,actionAlias);
				
				searchPolicyForm.setName(strName);
				searchPolicyForm.setPageNumber(pageList.getCurrentPage());
				searchPolicyForm.setTotalPages(pageList.getTotalPages());
				searchPolicyForm.setTotalRecords(pageList.getTotalItems());
				searchPolicyForm.setListSearchPcrfPolicy(pageList.getListData());
				searchPolicyForm.setAction(BaseConstant.LISTACTION);
				request.setAttribute("searchPCRFServiceForm", searchPolicyForm);
					
				return mapping.findForward(LIST_FORWARD);
			}catch(Exception managerExp){
					
				Logger.logError(MODULE,"Error during Data Manager operation, reason : "+ managerExp.getMessage());
                Logger.logTrace(MODULE,managerExp);
        		Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
      			request.setAttribute("errorDetails", errorElements);
                ActionMessage message = new ActionMessage("staff.search.failure");
                ActionMessages messages = new ActionMessages();
                messages.add("information", message);
                saveErrors(request, messages);
			}
			
            ActionMessages errorHeadingMessage = new ActionMessages();
            ActionMessage message = new ActionMessage("servicepolicy.pcrf.error.heading","searching");
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
            message = new ActionMessage("servicepolicy.pcrf.error.heading","searching");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage); 
		return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
}
