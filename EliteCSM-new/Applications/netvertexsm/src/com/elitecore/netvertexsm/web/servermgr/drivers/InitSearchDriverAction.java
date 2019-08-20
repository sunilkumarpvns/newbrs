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

public class InitSearchDriverAction extends BaseWebAction {
	private static final String FAILURE_FORWARD = "failure";
	private static final String SEARCH_FORWARD = "initSearchDriver";
	private static final String ACTION_ALIAS = ConfigConstant.SEARCH_DRIVER_ACTION;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
				
		if((checkAccess(request, ACTION_ALIAS))){
			Logger.logTrace(MODULE,"Enter the execute method of :"+getClass().getName());
			try{
				SearchDriverInstanceForm searchDriverForm = (SearchDriverInstanceForm) form;
				DriverBLManager driverBLManager = new DriverBLManager();
				List<ServiceTypeData> serviceTypeList = driverBLManager.getServiceTypeList();
				List<DriverTypeData> driverTypeList = driverBLManager.getDriverTypeList();
				DriverInstanceData driverInstanceData = new DriverInstanceData();
				
				Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
	            int requiredPageNo;
	            if(request.getParameter("pageNo") != null){
	    			requiredPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNo")));
	    		}else{
	    			requiredPageNo = new Long(searchDriverForm.getPageNumber()).intValue();
	    		}
	            if (requiredPageNo == 0)
	                requiredPageNo = 1;
				
				String actionAlias = ACTION_ALIAS;
				PageList pageList = driverBLManager.search(driverInstanceData, requiredPageNo, pageSize, actionAlias);
				
				searchDriverForm.setDriverTypeList(driverTypeList);
				searchDriverForm.setServiceTypeList(serviceTypeList);
				searchDriverForm.setListSearchDriver(pageList.getListData());
				searchDriverForm.setPageNumber(pageList.getCurrentPage());
				searchDriverForm.setTotalPages(pageList.getTotalPages());
				searchDriverForm.setTotalRecords(pageList.getTotalItems());
				searchDriverForm.setAction(BaseConstant.LISTACTION);
				               			
				request.setAttribute("searchDriverInstanceForm", searchDriverForm);
				return mapping.findForward(SEARCH_FORWARD);
			}catch(Exception managerExp){
				managerExp.printStackTrace();
				Logger.logTrace(MODULE,"Error during data Manager operation, reason :"+managerExp.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
				request.setAttribute("errorDetails", errorElements);

			}
            ActionMessages errorHeadingMessage = new ActionMessages();
            ActionMessage message = new ActionMessage("driver.error.heading","searching");
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
            message = new ActionMessage("driver.error.heading","searching");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage);

			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
}
