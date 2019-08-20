package com.elitecore.netvertexsm.web.servermgr.drivers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverTypeData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.ServiceTypeData;
import com.elitecore.netvertexsm.util.constants.BaseConstant;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.cache.ConfigManager;
import com.elitecore.netvertexsm.web.servermgr.drivers.form.SearchDriverInstanceForm;

public class SearchDriverAction extends BaseWebAction {
	private static final String LIST_FORWARD = "searchDriverList";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.SEARCH_DRIVER_ACTION;
	
	public ActionForward execute(ActionMapping mapping,ActionForm actionForm,HttpServletRequest request,HttpServletResponse response) throws Exception{
            
		Logger.logInfo(MODULE,"Enter Execute method of "+getClass().getName());
		if(checkAccess(request, ACTION_ALIAS)){
			try{
				SearchDriverInstanceForm searchDriverInstanceForm = (SearchDriverInstanceForm) actionForm;
				DriverBLManager driverBLManager = new DriverBLManager();
				DriverInstanceData driverInstanceData = new DriverInstanceData();				
				
				Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
	            int requiredPageNo;
	            if(request.getParameter("pageNo") != null){
	    			requiredPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNo")));
	    		}else{
	    			requiredPageNo = new Long(searchDriverInstanceForm.getPageNumber()).intValue();
	    		}
	            if (requiredPageNo == 0)
	                requiredPageNo = 1;												
				
				String strName = searchDriverInstanceForm.getName();
				Long strDriverTypeId = searchDriverInstanceForm.getDriverTypeId();
				
				if(strName.length()>0) {
					driverInstanceData.setName(strName);
				}else{
					driverInstanceData.setName(null);
				}
				
				if(strDriverTypeId>0) {
					driverInstanceData.setDriverTypeId(strDriverTypeId);
				}else{
					driverInstanceData.setDriverTypeId(null);
				}
									
				String actionAlias = ACTION_ALIAS;
				PageList pageList = driverBLManager.search(driverInstanceData, requiredPageNo, pageSize, actionAlias);
				List<DriverInstanceData> driverInstanceList = driverBLManager.getDriverInstanceList();
				searchDriverInstanceForm.setDriverInstanceList(driverInstanceList);
				
				searchDriverInstanceForm.setName(strName);				
				searchDriverInstanceForm.setDriverTypeId(strDriverTypeId);				
				searchDriverInstanceForm.setPageNumber(pageList.getCurrentPage());
				searchDriverInstanceForm.setTotalPages(pageList.getTotalPages());
				searchDriverInstanceForm.setTotalRecords(pageList.getTotalItems());
				searchDriverInstanceForm.setListSearchDriver(pageList.getListData());
				searchDriverInstanceForm.setAction(BaseConstant.LISTACTION);
				
				List<ServiceTypeData> serviceTypeList = driverBLManager.getServiceTypeList();
				List<DriverTypeData> driverTypeList = driverBLManager.getDriverTypeList();
				
				searchDriverInstanceForm.setDriverTypeList(driverTypeList);
				searchDriverInstanceForm.setServiceTypeList(serviceTypeList);
				request.setAttribute("searchDriverInstanceForm", searchDriverInstanceForm);
					
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