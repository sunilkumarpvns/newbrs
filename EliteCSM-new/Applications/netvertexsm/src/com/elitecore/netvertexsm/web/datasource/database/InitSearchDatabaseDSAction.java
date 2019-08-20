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

public class InitSearchDatabaseDSAction extends BaseWebAction{
	private static final String SUCCESS_FORWARD = "initsearchDatabaseDS";
	private static final String MODULE = "INIT_SEARCH_DATABASE_DS_ACTION";
	private static final String ACTION_ALIAS = ConfigConstant.SEARCH_DATABASE_DATASOURCE;

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) {
		Logger.logTrace(MODULE,"Execute Method Of :"+getClass().getName());
		try{
			if(checkAccess(request, ACTION_ALIAS)){
				SearchDatabaseDSForm searchDatabaseDSForm=(SearchDatabaseDSForm)form;
				DatabaseDSBLManager blManager = new DatabaseDSBLManager();
				IDatabaseDSData databaseDSSearchData = new DatabaseDSData();

				int requiredPageNo;
				if(request.getParameter("pageNo") != null){
					requiredPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNo")));
				}else{
					requiredPageNo = new Long(searchDatabaseDSForm.getPageNumber()).intValue();
				}
				if(requiredPageNo == 0)
					requiredPageNo = 1;
				Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));

				searchDatabaseDSForm.setName("");
				searchDatabaseDSForm.setStatus("All");

				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				String actionAlias = ACTION_ALIAS;
				PageList pageList = blManager.search(databaseDSSearchData,requiredPageNo,pageSize,staffData,actionAlias);
				searchDatabaseDSForm.setPageNumber(pageList.getCurrentPage());
				searchDatabaseDSForm.setTotalPages(pageList.getTotalPages());
				searchDatabaseDSForm.setTotalRecords(pageList.getTotalItems());
				searchDatabaseDSForm.setLstDatabaseDS(pageList.getListData());
				searchDatabaseDSForm.setAction(DatabaseDSConstant.LISTACTION);

				return mapping.findForward(SUCCESS_FORWARD);
			}else{
				Logger.logWarn(MODULE,"No Access On this Operation.");
				ActionMessage message = new ActionMessage("general.user.restricted");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
				return mapping.findForward(INVALID_ACCESS_FORWARD);
			}
		}catch(Exception e){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			e.printStackTrace();
    		Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
		}
		return mapping.findForward(FAILURE);
	}
}