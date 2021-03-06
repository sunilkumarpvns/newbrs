package com.elitecore.netvertexsm.web.gateway.gateway;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.corenetvertex.constants.CommunicationProtocol;
import com.elitecore.netvertexsm.blmanager.gateway.GatewayBLManager;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.gateway.gateway.data.GatewayData;
import com.elitecore.netvertexsm.util.constants.BaseConstant;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.cache.ConfigManager;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.netvertexsm.web.gateway.gateway.form.SearchGatewayForm;

public class SearchGatewayAction extends BaseWebAction {		
	private static final String LIST_FORWARD = "searchGatewayList";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.SEARCH_GATEWAY;
	
	public ActionForward execute(ActionMapping mapping,ActionForm actionForm,HttpServletRequest request,HttpServletResponse response) throws Exception{
            
		Logger.logInfo(MODULE,"Enter Execute method of "+getClass().getName());
		if(checkAccess(request, ACTION_ALIAS)){
			try{
				SearchGatewayForm searchGatewayForm = (SearchGatewayForm)actionForm;
				GatewayBLManager gatewayBLManager = new GatewayBLManager();
				GatewayData gatewaySearchData = new GatewayData();
				
				Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
	            int requiredPageNo;
	            if(request.getParameter("pageNo") != null){
	    			requiredPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNo")));
	    		}else{
	    			requiredPageNo = new Long(searchGatewayForm.getPageNumber()).intValue();
	    		}
	            if (requiredPageNo == 0)
	                requiredPageNo = 1;								
	            
	            gatewaySearchData.setConnectionUrl(searchGatewayForm.getConnectionUrl());
	            gatewaySearchData.setCommProtocol(searchGatewayForm.getCommProtocolId());
	            gatewaySearchData.setGatewayName(searchGatewayForm.getGatewayName());		
																									
				String actionAlias = ACTION_ALIAS;
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				PageList pageList = gatewayBLManager.search(gatewaySearchData, requiredPageNo, pageSize, staffData, actionAlias);
				
				searchGatewayForm.setGatewayName(searchGatewayForm.getGatewayName());
				searchGatewayForm.setConnectionUrl(searchGatewayForm.getConnectionUrl());
				searchGatewayForm.setPageNumber(pageList.getCurrentPage());
				searchGatewayForm.setTotalPages(pageList.getTotalPages());
				searchGatewayForm.setTotalRecords(pageList.getTotalItems());
				searchGatewayForm.setListSearchGateway(pageList.getListData());
				searchGatewayForm.setAction(BaseConstant.LISTACTION);
				request.setAttribute("searchGatewayForm", searchGatewayForm);
					
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
		    return mapping.findForward(FAILURE_FORWARD);
		}else{
	        Logger.logWarn(MODULE, "Error during Data Manager operation ");
	        ActionMessage message = new ActionMessage("general.user.restricted");
	        ActionMessages messages = new ActionMessages();
	        messages.add("information", message);
	        saveErrors(request, messages);
		return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
}
