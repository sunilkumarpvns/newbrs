package com.elitecore.netvertexsm.web.servermgr.drivers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.constants.DriverTypeConstants;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.servermgr.drivers.form.ViewDriverInstanceForm;

public class ViewDriverInstanceAction extends BaseWebAction {
	private static final String VIEW_CSVDRIVER_FORWARD = "viewCSVDriverInstance";
	private static final String VIEW_DBCDRDRIVER_FORWARD = "viewDBCDRDriverInstance";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.VIEW_DRIVER_ACTION;
    private static final String MODULE = "VIEW_DRIVER_INSTANCE_ACTION";
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
            Logger.logInfo(MODULE,"Enter execute method of"+getClass().getName());
	  	if(checkAccess(request, ACTION_ALIAS)){
			try{
				ViewDriverInstanceForm viewDriverInstanceForm = (ViewDriverInstanceForm)form;
				DriverBLManager blManager = new DriverBLManager();
				DriverInstanceData driverInstanceData  = new DriverInstanceData();
				
				String strDriverInstanceId = request.getParameter("driverInstanceId");
			    Long driverInstanceId = Long.parseLong(strDriverInstanceId);
				if(driverInstanceId == null){
					driverInstanceId = viewDriverInstanceForm.getDriverInstanceId();
				}
				
				if(driverInstanceId!=null ){
					
					driverInstanceData.setDriverInstanceId(driverInstanceId);
					driverInstanceData = blManager.getDriverInstanceData(driverInstanceData);
					request.setAttribute("driverInstanceData",driverInstanceData);
					if(driverInstanceData.getDriverTypeId()!=null && driverInstanceData.getDriverTypeId().longValue()==DriverTypeConstants.CSV_DRIVER.longValue()){
						return mapping.findForward(VIEW_CSVDRIVER_FORWARD);
					}else if(driverInstanceData.getDriverTypeId()!=null && driverInstanceData.getDriverTypeId().longValue()==DriverTypeConstants.DBCDR_DRIVER.longValue()){
						return mapping.findForward(VIEW_DBCDRDRIVER_FORWARD);
					}else{
			            ActionMessages messages = new ActionMessages();
			            messages.add("information", new ActionMessage("driver.drivertype.failure"));
			            saveErrors(request, messages);
			            
			            ActionMessages errorHeadingMessage = new ActionMessages();
			            ActionMessage message = new ActionMessage("driver.error.heading","viewing");
			            errorHeadingMessage.add("errorHeading",message);
			            saveMessages(request,errorHeadingMessage);
			            return mapping.findForward(FAILURE_FORWARD);
					}
					
				}else{
		            ActionMessages messages = new ActionMessages();
		            messages.add("information", new ActionMessage("driver.notfound.failure"));
		            saveErrors(request, messages);
		            
		            ActionMessages errorHeadingMessage = new ActionMessages();
		            ActionMessage message = new ActionMessage("driver.error.heading","viewing");
		            errorHeadingMessage.add("errorHeading",message);
		            saveMessages(request,errorHeadingMessage);
		            return mapping.findForward(FAILURE_FORWARD);
				}
				
			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("driver.view.failure"));
	            saveErrors(request, messages);
	            
	            ActionMessages errorHeadingMessage = new ActionMessages();
	            ActionMessage message = new ActionMessage("driver.error.heading","viewing");
	            errorHeadingMessage.add("errorHeading",message);
	            saveMessages(request,errorHeadingMessage);
	            return mapping.findForward(FAILURE_FORWARD);
			} 
		}else{
            Logger.logWarn(MODULE, "No Access on this Operation ");
	        ActionMessage message = new ActionMessage("general.user.restricted");
	        ActionMessages messages = new ActionMessages();
	        messages.add("information", message);
	        saveErrors(request, messages);
	        
            ActionMessages errorHeadingMessage = new ActionMessages();
            message = new ActionMessage("driver.error.heading","viewing");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage);
	        return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
}
