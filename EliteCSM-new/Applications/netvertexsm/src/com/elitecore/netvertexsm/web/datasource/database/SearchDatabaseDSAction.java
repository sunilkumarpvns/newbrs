package com.elitecore.netvertexsm.web.datasource.database;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.datasource.DatabaseDSBLManager;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.datasource.database.data.DatabaseDSData;
import com.elitecore.netvertexsm.datamanager.datasource.database.data.IDatabaseDSData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.constants.DatabaseDSConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.cache.ConfigManager;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.netvertexsm.web.datasource.database.form.SearchDatabaseDSForm;

public class SearchDatabaseDSAction extends BaseWebAction{
	private static final String ACTION_ALIAS = ConfigConstant.SEARCH_DATABASE_DATASOURCE;
	private static final String FAILURE_FORWARD = "failure";
	private static final String MODULE = "LIST DATABASEDS ACTION";
	private static final String LIST_FORWARD = "databaseDSList";
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		if(checkAccess(request, ACTION_ALIAS)){
			Logger.logTrace(MODULE,"Execute Method Of :"+getClass().getName());
			try{
				DatabaseDSBLManager blManager = new DatabaseDSBLManager();
				SearchDatabaseDSForm searchDatabaseDSForm = (SearchDatabaseDSForm)form;
				
				int requiredPageNo;
	            if(request.getParameter("pageNo") != null){
	    			requiredPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNo")));
	    		}else{
	    			requiredPageNo = new Long(searchDatabaseDSForm.getPageNumber()).intValue();
	    		}
				if(requiredPageNo == 0)
					requiredPageNo = 1;
				
                IDatabaseDSData databaseDSSearchData = new DatabaseDSData();
				
				String strName = searchDatabaseDSForm.getName();
				if(strName !=null)
					databaseDSSearchData.setName("%"+strName+"%");
				else
					databaseDSSearchData.setName("");
				
				Logger.logDebug(MODULE, "befor status : "+searchDatabaseDSForm.getStatus());
				if(request.getParameter("resultStatus")!= null){
					searchDatabaseDSForm.setStatus(request.getParameter("resultStatus"));
	    		}
				
				
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				String actionAlias = ACTION_ALIAS;
				
				Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
				Logger.logDebug(MODULE, "PAGENO IS:"+requiredPageNo+"");
				
				PageList pageList = blManager.search(databaseDSSearchData,requiredPageNo,pageSize,staffData,actionAlias);
				searchDatabaseDSForm.setName(strName);
				searchDatabaseDSForm.setPageNumber(pageList.getCurrentPage());
				searchDatabaseDSForm.setTotalPages(pageList.getTotalPages());
				searchDatabaseDSForm.setTotalRecords(pageList.getTotalItems());
				searchDatabaseDSForm.setLstDatabaseDS(pageList.getListData());
				Logger.logDebug(MODULE,"searchDatabaseDSForm.setPageNumber(pageList.getCurrentPage())"+ searchDatabaseDSForm.getPageNumber());
				Logger.logDebug(MODULE,"searchDatabaseDSForm.setLstDatabaseDS(pageList.getListData())");

				searchDatabaseDSForm.setAction(DatabaseDSConstant.LISTACTION);
				
				return mapping.findForward(LIST_FORWARD);
			}catch(Exception e){
				Logger.logError(MODULE, "Error List Display operation , reason : " + e.getMessage());            
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
			}			
			Logger.logTrace(MODULE, "Returning Error Forward From :" + getClass().getName());
			ActionMessage message = new ActionMessage("database.datasource.list.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			saveErrors(request,messages);
			
            ActionMessages errorHeadingMessage = new ActionMessages();
            message = new ActionMessage("database.datasource.error.heading","searching");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage);			
			return mapping.findForward(FAILURE_FORWARD); 
		}else{
	        Logger.logError(MODULE, "Error during Data Manager operation ");
	        ActionMessage message = new ActionMessage("general.user.restricted");
	        ActionMessages messages = new ActionMessages();
	        messages.add("information", message);
	        saveErrors(request, messages);
			
            ActionMessages errorHeadingMessage = new ActionMessages();
            message = new ActionMessage("database.datasource.error.heading","searching");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage);				        
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
    }
}