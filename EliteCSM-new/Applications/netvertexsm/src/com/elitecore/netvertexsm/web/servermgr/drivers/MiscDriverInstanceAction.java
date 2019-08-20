package com.elitecore.netvertexsm.web.servermgr.drivers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;

public class MiscDriverInstanceAction extends BaseWebAction {
	private static final String LIST_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.DELETE_DRIVER_ACTION;
	
	public ActionForward execute(ActionMapping mapping,ActionForm actionForm,HttpServletRequest request,HttpServletResponse response) throws Exception{
            
		Logger.logInfo(MODULE,"Enter Execute method of "+getClass().getName());
		if(checkAccess(request, ACTION_ALIAS)){
			try{
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				DriverBLManager driverBLManager = new DriverBLManager();
				List<Long> driverInstanceIdList = new ArrayList<Long>();
				
				String[] strDriverInstanceIds = request.getParameterValues("select"); 
				
				for(int i=0; i<strDriverInstanceIds.length; i++) {
					driverInstanceIdList.add(Long.parseLong(strDriverInstanceIds[i]));
				}
				
				driverBLManager.delete(driverInstanceIdList,staffData, ACTION_ALIAS);
				request.setAttribute("responseUrl","/initSearchDriverInstance");
				ActionMessage message = new ActionMessage("driver.delete.success");
				ActionMessages messages1 = new ActionMessages();
				messages1.add("information",message);
				saveMessages(request,messages1);
				
				return mapping.findForward(LIST_FORWARD);
			}catch(Exception managerExp){
				Logger.logError(MODULE,"Error during Data Manager operation, reason : "+ managerExp.getMessage());
                Logger.logTrace(MODULE,managerExp);
        		Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
      			request.setAttribute("errorDetails", errorElements);
                ActionMessage message = new ActionMessage("driver.delete.failure");
                ActionMessages messages = new ActionMessages();
                messages.add("information", message);
                saveErrors(request, messages);
                
	            ActionMessages errorHeadingMessage = new ActionMessages();
	            message = new ActionMessage("driver.error.heading","deleting");
	            errorHeadingMessage.add("errorHeading",message);
	            saveMessages(request,errorHeadingMessage);
		         
                return mapping.findForward(FAILURE_FORWARD);
			}
		}else{
	        Logger.logWarn(MODULE, "Error during Data Manager operation ");
	        ActionMessage message = new ActionMessage("general.user.restricted");
	        ActionMessages messages = new ActionMessages();
	        messages.add("information", message);
	        saveErrors(request, messages);
	        
            ActionMessages errorHeadingMessage = new ActionMessages();
            message = new ActionMessage("driver.error.heading","deleting");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage);
	        
		return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
}
