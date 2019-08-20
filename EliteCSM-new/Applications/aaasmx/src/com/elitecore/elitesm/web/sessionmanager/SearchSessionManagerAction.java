package com.elitecore.elitesm.web.sessionmanager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.sessionmanager.SessionManagerBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.sessionmanager.data.ISessionManagerInstanceData;
import com.elitecore.elitesm.datamanager.sessionmanager.data.SessionManagerInstanceData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.DatabaseDSConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.sessionmanager.forms.SearchSessionManagerForm;


public class SearchSessionManagerAction extends BaseWebAction {
	
	private static final String SUCCESS_FORWARD = "searchSessionManager";
	private static final String FAILURE_FORWARD = "failure";
	private static final String MODULE = ConfigConstant.SEARCH_SESSION_MANAGER;
	private static final String ACTION_ALIAS = ConfigConstant.SEARCH_SESSION_MANAGER;
	private static final String LIST_FORWARD = "searchSessionManager";
	
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
	    
		Logger.logTrace(MODULE,"Execute Method Of :"+getClass().getName());
		
		try{
		   checkActionPermission(request, ACTION_ALIAS);
			SearchSessionManagerForm searchSessionManagerForm=(SearchSessionManagerForm)form;
			SessionManagerBLManager blmanager = new SessionManagerBLManager();
			
			int requiredPageNo;
            if(request.getParameter("pageNo") != null){
    			requiredPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNo")));
    		}else{
    			requiredPageNo = new Long(searchSessionManagerForm.getPageNumber()).intValue();
    		}
			if(requiredPageNo == 0)
				requiredPageNo = 1;
			
			//search criteria information
			ISessionManagerInstanceData sessionManagerInstance = new SessionManagerInstanceData();
			
			
			String strName = searchSessionManagerForm.getName();
			Logger.logInfo(MODULE,"Name :"+strName);
			
			if(strName !=null)
				sessionManagerInstance.setName("%"+strName+"%");
			else
				sessionManagerInstance.setName("");
			
			
			//staff related
			
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			String actionAlias = ACTION_ALIAS;
			
			
			Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
			Logger.logDebug(MODULE, "PAGENO IS:"+requiredPageNo+"");
			
			PageList pageList = blmanager.search(sessionManagerInstance,requiredPageNo,pageSize,staffData);
			searchSessionManagerForm.setName(strName);
			searchSessionManagerForm.setPageNumber(pageList.getCurrentPage());
			searchSessionManagerForm.setTotalPages(pageList.getTotalPages());
			searchSessionManagerForm.setTotalRecords(pageList.getTotalItems());
			searchSessionManagerForm.setListSessionManager(pageList.getListData());
			
			searchSessionManagerForm.setAction(DatabaseDSConstant.LISTACTION);
			
			return mapping.findForward(LIST_FORWARD);
			
			
		}catch(ActionNotPermitedException e){
			Logger.logError(MODULE,"Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}catch(Exception e){
			Logger.logError(MODULE, "Error List Display operation , reason : " + e.getMessage());            
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);

		}
		
		Logger.logTrace(MODULE, "Returning Error Forward From :" + getClass().getName());
		ActionMessage message = new ActionMessage("sessionmanager.list.failure");
		ActionMessages messages = new ActionMessages();
		messages.add("information",message);
		saveErrors(request,messages);
		return mapping.findForward(FAILURE_FORWARD); 
		
		
		}

}
