package com.elitecore.netvertexsm.web.servermgr.spinterface;

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
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.datasource.database.data.DatabaseDSData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverTypeData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.ServiceTypeData;
import com.elitecore.netvertexsm.util.constants.BaseConstant;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.cache.ConfigManager;
import com.elitecore.netvertexsm.web.servermgr.spinterface.form.SearchSPInterfaceForm;

public class SearchSPInterfaceAction extends BaseWebAction {
	private static final String LIST_FORWARD = "searchSPInterfacePage";
	private static final String ACTION_ALIAS = ConfigConstant.SEARCH_SP_INTERFACE;
	
	public ActionForward execute(ActionMapping mapping,ActionForm actionForm,HttpServletRequest request,HttpServletResponse response) throws Exception{
            
		Logger.logInfo(MODULE,"Enter Execute method of "+getClass().getName());
		if(checkAccess(request, ACTION_ALIAS)){
			try{
				SearchSPInterfaceForm searchSPInterfaceForm = (SearchSPInterfaceForm) actionForm;
				DriverBLManager driverBLManager = new DriverBLManager();
				SPInterfaceBLManager spInterfaceDriverBLManager = new SPInterfaceBLManager();
				DriverInstanceData driverInstanceData = new DriverInstanceData();				
				Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
	            int requiredPageNo;
	            if(request.getParameter("pageNo") != null){
	    			requiredPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNo")));
	    		}else{
	    			requiredPageNo = new Long(searchSPInterfaceForm.getPageNumber()).intValue();
	    		}
	            if (requiredPageNo == 0)
	                requiredPageNo = 1;												
				
				String strName = searchSPInterfaceForm.getName();
				Long strDriverTypeId = searchSPInterfaceForm.getDriverTypeId();
				if(strName != null && strName.length()>0){
			    	driverInstanceData.setName(strName);
			    	searchSPInterfaceForm.setName(strName);
			    }
				if(strDriverTypeId>0) {
					driverInstanceData.setDriverTypeId(strDriverTypeId);
					searchSPInterfaceForm.setDriverInstanceId(strDriverTypeId);
				}
				if(strName==null && strDriverTypeId==0) {
					driverInstanceData.setName("");
					driverInstanceData.setDriverTypeId(null);
				}															
									
				String actionAlias = ACTION_ALIAS;
				PageList pageList = spInterfaceDriverBLManager.search(driverInstanceData, requiredPageNo, pageSize, actionAlias);
								
				DatabaseDSBLManager databaseDSBLManager = new DatabaseDSBLManager();
				 
				List<DatabaseDSData> databaseDSList = databaseDSBLManager.getDatabaseDSList();
				List<DriverInstanceData> driverInstanceList = spInterfaceDriverBLManager.getDriverInstanceList();
				 
				searchSPInterfaceForm.setDatabaseDSList(databaseDSList);
				searchSPInterfaceForm.setDriverInstanceList(driverInstanceList);
				
				searchSPInterfaceForm.setName(strName);	
				searchSPInterfaceForm.setDriverTypeId(strDriverTypeId);				
				searchSPInterfaceForm.setPageNumber(pageList.getCurrentPage());
				searchSPInterfaceForm.setTotalPages(pageList.getTotalPages());
				searchSPInterfaceForm.setTotalRecords(pageList.getTotalItems());
				searchSPInterfaceForm.setListSearchDriver(pageList.getListData());
				searchSPInterfaceForm.setAction(BaseConstant.LISTACTION);
				
				List<ServiceTypeData> serviceTypeList = driverBLManager.getServiceTypeList();
				List<DriverTypeData> driverTypeList = spInterfaceDriverBLManager.getSPInterfaceTypeList();
				
				searchSPInterfaceForm.setDriverTypeList(driverTypeList);
				searchSPInterfaceForm.setServiceTypeList(serviceTypeList);
				request.setAttribute("searchDriverInstanceForm", searchSPInterfaceForm);
					
				return mapping.findForward(LIST_FORWARD);
			}catch(DataManagerException managerExp){
					
				Logger.logError(MODULE,"Error during Data Manager operation, reason : "+ managerExp.getMessage());
                Logger.logTrace(MODULE,managerExp);
        		Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
      			request.setAttribute("errorDetails", errorElements);
                ActionMessage message = new ActionMessage("spinterface.search.failure");
                ActionMessages messages = new ActionMessages();
                messages.add("information", message);
                saveErrors(request, messages);
                
			}catch(Exception exp){
					
				Logger.logError(MODULE,"Error during Data Manager operation, reason : "+ exp.getMessage());
                Logger.logTrace(MODULE,exp);
        		Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(exp);
      			request.setAttribute("errorDetails", errorElements);
                ActionMessage message = new ActionMessage("spinterface.search.failure");
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
