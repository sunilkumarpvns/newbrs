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
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.servermgr.alert.data.AlertListenerData;
import com.elitecore.netvertexsm.datamanager.servermgr.alert.data.AlertListenerTypeData;
import com.elitecore.netvertexsm.util.constants.BaseConstant;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.cache.ConfigManager;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.netvertexsm.web.servermgr.alert.forms.SearchAlertListenerForm;

public class SearchAlertListenerAction extends BaseWebAction{

	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS=ConfigConstant.SEARCH_ALERT_LISTENER;
	private static final String LIST_FORWARD="searchResult";

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());

		SearchAlertListenerForm searchAlertListenerForm = (SearchAlertListenerForm)form;
		AlertListenerBLManager alertListenerBLManager = new AlertListenerBLManager();
		try{
			checkActionPermission(request, ACTION_ALIAS);
			List<AlertListenerTypeData> availableListenerTypes = alertListenerBLManager.getAvailableListenerType();
			String[] alertListenerIds = request.getParameterValues("select");

			int requiredPageNo;
			if(request.getParameter("pageNo") != null){
				requiredPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNo")));
			}else{
				requiredPageNo = Long.valueOf(searchAlertListenerForm.getPageNumber()).intValue();
			}
			if(requiredPageNo == 0)
				requiredPageNo = 1;

			AlertListenerData alertListenerData = new AlertListenerData();
         	
			String strName = searchAlertListenerForm.getName();
			
			if(strName !=null)
				alertListenerData.setName(strName);
			else
				alertListenerData.setName("");
			
			String strTypeId=searchAlertListenerForm.getTypeId();
           	if(strTypeId != null)
			       alertListenerData.setTypeId(strTypeId);  
			else
			   alertListenerData.setTypeId("");
		       	
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			String actionAlias = ACTION_ALIAS;
			
			Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
			Logger.logDebug(MODULE, "PAGENO IS:"+requiredPageNo+"");
			PageList pageList = alertListenerBLManager.search(alertListenerData,requiredPageNo,pageSize);
			searchAlertListenerForm.setName(strName);
			searchAlertListenerForm.setTypeList(availableListenerTypes);
			searchAlertListenerForm.setTypeId(alertListenerData.getTypeId());
			searchAlertListenerForm.setPageNumber(pageList.getCurrentPage());
			searchAlertListenerForm.setTotalPages(pageList.getTotalPages());
			searchAlertListenerForm.setTotalRecords(pageList.getTotalItems());
			searchAlertListenerForm.setLstAlertListener(pageList.getListData());
           	request.setAttribute("lstAlertListener",pageList.getListData());   

			searchAlertListenerForm.setAction(BaseConstant.LISTACTION);

			return mapping.findForward(LIST_FORWARD);

		}catch(ActionNotPermitedException e){
			Logger.logError(MODULE, "Restricted to do action.");
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);	

		}catch(Exception e){
			Logger.logError(MODULE, "Error List Display operation , reason : " + e.getMessage());            
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);

		}

		Logger.logTrace(MODULE, "Returning Error Forward From :" + getClass().getName());
		ActionMessage message = new ActionMessage("alert.search.failure");
		ActionMessages messages = new ActionMessages();
		messages.add("information",message);
		saveErrors(request,messages);
		return mapping.findForward(FAILURE_FORWARD); 
	}
}
