package com.elitecore.netvertexsm.web.servermgr.alert;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.servermgr.alert.AlertListenerBLManager;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.servermgr.alert.data.AlertListenerData;
import com.elitecore.netvertexsm.datamanager.servermgr.alert.data.AlertListenerTypeData;
import com.elitecore.netvertexsm.util.constants.BaseConstant;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.cache.ConfigManager;
import com.elitecore.netvertexsm.web.servermgr.alert.forms.SearchAlertListenerForm;

public class InitSearchAlertListenerAction extends BaseWebAction{
	
	private static final String SUCCESS_FORWARD = "searchAlertListener";
	private static final String ACTION_ALIAS=ConfigConstant.SEARCH_ALERT_LISTENER;

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		
		if(checkAccess(request, ACTION_ALIAS)){
			AlertListenerBLManager alertListenerBLManager = new AlertListenerBLManager();
			SearchAlertListenerForm searchAlertListenerForm = (SearchAlertListenerForm) form;
			searchAlertListenerForm.setTypeId("0");
			AlertListenerData alertListenerData = new AlertListenerData();
			
			List<AlertListenerTypeData> alertListenerTypeList = alertListenerBLManager.getAvailableListenerType();
			
			
			Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
            int requiredPageNo;
            if(request.getParameter("pageNo") != null){
    			requiredPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNo")));
    		}else{
    			requiredPageNo = Long.valueOf(searchAlertListenerForm.getPageNumber()).intValue();
    		}
            if (requiredPageNo == 0)
                requiredPageNo = 1;		
						
			PageList pageList = alertListenerBLManager.search(alertListenerData, requiredPageNo, pageSize);
						
			searchAlertListenerForm.setName("");
			searchAlertListenerForm.setLstAlertListener(pageList.getListData());
			searchAlertListenerForm.setPageNumber(pageList.getCurrentPage());
			searchAlertListenerForm.setTotalPages(pageList.getTotalPages());
			searchAlertListenerForm.setTotalRecords(pageList.getTotalItems());
			searchAlertListenerForm.setAction(BaseConstant.LISTACTION);
			searchAlertListenerForm.setTypeList(alertListenerTypeList);
		  
		    request.setAttribute("searchAlertListenerForm", searchAlertListenerForm);
		    request.setAttribute("lstAlertListener", pageList.getListData());
		    return mapping.findForward(SUCCESS_FORWARD);
		    
		}else{
            Logger.logWarn(MODULE,"No Access On this Operation.");
	        ActionMessage message = new ActionMessage("general.user.restricted");
            ActionMessages messages = new ActionMessages();
            messages.add("information", message);
            saveErrors(request, messages);
            
            ActionMessages errorHeadingMessage = new ActionMessages();
            message = new ActionMessage("alert.error.heading","searching");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage); 
            return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}

}
