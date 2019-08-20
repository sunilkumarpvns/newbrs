package com.elitecore.netvertexsm.web.servermgr.spinterface.dbinterface;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.datasource.DatabaseDSBLManager;
import com.elitecore.netvertexsm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.netvertexsm.blmanager.servermgr.spinterface.SPInterfaceBLManager;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.datasource.database.data.DatabaseDSData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.spinterface.dbinterface.data.DatabaseSPInterfaceData;
import com.elitecore.netvertexsm.util.constants.BaseConstant;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.servermgr.spinterface.dbinterface.form.SearchDBSPInterfaceForm;

public class SearchDBSPInterfaceAction extends BaseWebAction {
	private static final String LIST_FORWARD = "searchDBList";
	private static final String ACTION_ALIAS = ConfigConstant.SEARCH_SP_INTERFACE;
	
	public ActionForward execute(ActionMapping mapping,ActionForm actionForm,HttpServletRequest request,HttpServletResponse response) throws Exception{
            
		Logger.logInfo(MODULE,"Enter Execute method of "+getClass().getName());
		if(checkAccess(request, ACTION_ALIAS)){
			try{
				SearchDBSPInterfaceForm searchDBSPInterfaceForm = (SearchDBSPInterfaceForm) actionForm;
				DriverBLManager driverBLManager = new DriverBLManager();
				SPInterfaceBLManager spInterfaceDriverBLManager = new SPInterfaceBLManager();
				DatabaseSPInterfaceData databaseSPInterfaceData = new DatabaseSPInterfaceData();				
				
				int requiredPageNo = Integer.parseInt(String.valueOf(searchDBSPInterfaceForm.getPageNumber()));
				if(requiredPageNo == 0)
					requiredPageNo=1;												
				
				Long strDatabaseDsId = searchDBSPInterfaceForm.getDatabaseDsId();
				Long strDriverInstanceId = searchDBSPInterfaceForm.getDriverInstanceId();
				
				if(strDatabaseDsId>0) {
					databaseSPInterfaceData.setDatabaseDsId(strDatabaseDsId);
				}else if(strDriverInstanceId>0) {
					databaseSPInterfaceData.setDriverInstanceId(strDriverInstanceId);
				}else {
					databaseSPInterfaceData.setDatabaseDsId(null);
					databaseSPInterfaceData.setDriverInstanceId(null);
				}																
									
				String actionAlias = ACTION_ALIAS;
				PageList pageList = spInterfaceDriverBLManager.search(databaseSPInterfaceData, requiredPageNo, 10, actionAlias);
								
				DatabaseDSBLManager databaseDSBLManager = new DatabaseDSBLManager();
				 
				List<DatabaseDSData> databaseDSList = databaseDSBLManager.getDatabaseDSList();
				List<DriverInstanceData> driverInstanceList = driverBLManager.getDriverInstanceList();
				 
				searchDBSPInterfaceForm.setDatabaseDSList(databaseDSList);
				searchDBSPInterfaceForm.setDriverInstanceList(driverInstanceList);
				
				searchDBSPInterfaceForm.setDatabaseDsId(strDatabaseDsId);
				searchDBSPInterfaceForm.setDriverInstanceId(strDriverInstanceId);				
				searchDBSPInterfaceForm.setPageNumber(pageList.getCurrentPage());
				searchDBSPInterfaceForm.setTotalPages(pageList.getTotalPages());
				searchDBSPInterfaceForm.setTotalRecords(pageList.getTotalItems());
				searchDBSPInterfaceForm.setListSearchDBDriver(pageList.getListData());
				searchDBSPInterfaceForm.setAction(BaseConstant.LISTACTION);
				request.setAttribute("searchDBSPInterface", searchDBSPInterfaceForm);
					
				return mapping.findForward(LIST_FORWARD);
			}catch(Exception managerExp){
					
				Logger.logError(MODULE,"Error during Data Manager operation, reason : "+ managerExp.getMessage());
                Logger.logTrace(MODULE,managerExp);
        		Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
      			request.setAttribute("errorDetails", errorElements);
                ActionMessage message = new ActionMessage("spinterface..search.failure");
                ActionMessages messages = new ActionMessages();
                messages.add("information", message);
                saveErrors(request, messages);
			}
		    return mapping.findForward(FAILURE);
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
