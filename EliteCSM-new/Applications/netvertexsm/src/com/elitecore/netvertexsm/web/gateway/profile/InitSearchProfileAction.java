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
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.cache.ConfigManager;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.netvertexsm.web.gateway.profile.form.SearchProfileForm;

public class InitSearchProfileAction extends BaseWebAction {
	private static final String SUCCESS_FORWARD = "initSearchProfile";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.SEARCH_GATEWAY_PROFILE;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		
		if(checkAccess(request, ACTION_ALIAS)){
		    SearchProfileForm searchProfileForm = (SearchProfileForm) form;
		    GatewayBLManager gatewayBLManager = new GatewayBLManager();
		    List profileList = gatewayBLManager.getProfileList();
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
		    
		    String actionAlias = ACTION_ALIAS;
		    IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			PageList pageList = gatewayBLManager.search(gatewayProfileData, requiredPageNo, pageSize,staffData, actionAlias);
		    
		    searchProfileForm.setProfileList(profileList);
		    searchProfileForm.setStatus("All"); 
		    searchProfileForm.setPageNumber(pageList.getCurrentPage());
			searchProfileForm.setTotalPages(pageList.getTotalPages());
			searchProfileForm.setTotalRecords(pageList.getTotalItems());
		    searchProfileForm.setListSearchProfile(pageList.getListData());
			searchProfileForm.setAction(BaseConstant.LISTACTION);
			request.setAttribute("searchProfileForm", searchProfileForm);
			
		    return mapping.findForward(SUCCESS_FORWARD);
		}else{
            Logger.logWarn(MODULE,"No Access On this Operation.");
	        ActionMessage message = new ActionMessage("general.user.restricted");
            ActionMessages messages = new ActionMessages();
            messages.add("information", message);
            saveErrors(request, messages);
            return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
}
