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
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverTypeData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.ServiceTypeData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.servermgr.drivers.form.CreateDriverInstanceForm;

public class InitCreateDriverAction extends BaseWebAction {
	private static final String SEARCH_FORWARD = "initCreateDriver";
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_DRIVER_ACTION;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		Logger.logTrace(MODULE,"Enter the execute method of :"+getClass().getName());
		if((checkAccess(request, ACTION_ALIAS))){
			Logger.logTrace(MODULE,"Enter the execute method of :"+getClass().getName());
			try{
				CreateDriverInstanceForm createDriverInstanceForm = (CreateDriverInstanceForm) form;
				DriverBLManager driverBLManager = new DriverBLManager();
				List<ServiceTypeData> serviceTypeList = driverBLManager.getServiceTypeList();
				List<DriverTypeData> driverTypeList = driverBLManager.getDriverTypeList();
				createDriverInstanceForm.setDriverTypeList(driverTypeList);
				createDriverInstanceForm.setServiceTypeList(serviceTypeList);
				createDriverInstanceForm.setDescription(getDefaultDescription(request));  			
				return mapping.findForward(SEARCH_FORWARD);
			}catch(DataManagerException managerExp){
				managerExp.printStackTrace();
				Logger.logTrace(MODULE,"Error during data Manager operation, reason :"+managerExp.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
				request.setAttribute("errorDetails", errorElements);
				ActionMessages messages = new ActionMessages();
		        messages.add("information", new ActionMessage("driver.create.failure"));
		        saveErrors(request, messages);
		          
			}catch(Exception exp){
				exp.printStackTrace();
				Logger.logTrace(MODULE,"Error during data Manager operation, reason :"+exp.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(exp);
				request.setAttribute("errorDetails", errorElements);
				ActionMessages messages = new ActionMessages();
		        messages.add("information", new ActionMessage("driver.create.failure"));
		        saveErrors(request, messages);
		          
			}
			return mapping.findForward(FAILURE);
		}else{
	        Logger.logError(MODULE, "Error during Data Manager operation ");
	        ActionMessage message = new ActionMessage("general.user.restricted");
	        ActionMessages messages = new ActionMessages();
	        messages.add("information", message);
	        saveErrors(request, messages);
			
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
}
