package com.elitecore.netvertexsm.web.gateway.profile;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.gateway.GatewayBLManager;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfileData;
import com.elitecore.netvertexsm.util.constants.BaseConstant;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.cache.ConfigManager;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.netvertexsm.web.gateway.profile.form.SearchProfileForm;

public class SearchProfileAction extends BaseWebAction {
	private static final String LIST_FORWARD = "searchProfileList";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.SEARCH_GATEWAY_PROFILE;
	
	public ActionForward execute(ActionMapping mapping,ActionForm actionForm,HttpServletRequest request,HttpServletResponse response) throws Exception{
            
		Logger.logInfo(MODULE,"Enter Execute method of "+getClass().getName());
		if(checkAccess(request, ACTION_ALIAS)){
			try{
				SearchProfileForm searchProfileForm = (SearchProfileForm)actionForm;
				GatewayBLManager gatewayBLManager = new GatewayBLManager();
				GatewayProfileData gatewayProfileData = new GatewayProfileData();
				
				Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
	            int requiredPageNo;
	            if(request.getParameter("pageNo") != null){
	    			requiredPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNo")));
	    		}else{
	    			requiredPageNo = new Long(searchProfileForm.getPageNumber()).intValue();
	    		}
	            if (requiredPageNo == 0)
	                requiredPageNo = 1;												
				
				String strProfileName = searchProfileForm.getProfileName();							
				
				if(strProfileName!=null && strProfileName.length()>0) {
					gatewayProfileData.setProfileName(strProfileName);					
				}else {
					gatewayProfileData.setProfileName(null);		
				}																
									
				String actionAlias = ACTION_ALIAS;
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				PageList pageList = gatewayBLManager.search(gatewayProfileData, requiredPageNo, pageSize,staffData, actionAlias);
				
				List profileList = gatewayBLManager.getProfileList();
				searchProfileForm.setProfileList(profileList);
				
				searchProfileForm.setProfileName(strProfileName);
				searchProfileForm.setPageNumber(pageList.getCurrentPage());
				searchProfileForm.setTotalPages(pageList.getTotalPages());
				searchProfileForm.setTotalRecords(pageList.getTotalItems());
				searchProfileForm.setListSearchProfile(pageList.getListData());
				searchProfileForm.setAction(BaseConstant.LISTACTION);
				request.setAttribute("searchProfileForm", searchProfileForm);
					
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
            ActionMessage message = new ActionMessage("gateway.profile.error.heading","searching");
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
            message = new ActionMessage("gateway.profile.error.heading","searching");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage); 	            
	        
		return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
}
