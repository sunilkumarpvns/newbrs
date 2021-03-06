
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
import com.elitecore.netvertexsm.web.servermgr.drivers.form.EditDriverInstanceForm;

public class InitEditDriverInstanceAction extends BaseWebAction {

	private static final String EDIT_CSVDRIVER_FORWARD = "initEditCSVDriver";
	private static final String EDIT_DBCDRDRIVER_FORWARD = "initEditDBCDRDriver";
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_DRIVER_ACTION;
	private static final String MODULE = "EDIT_DRIVER_INSTANCE";
	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of"+getClass().getName());
		if(checkAccess(request, ACTION_ALIAS)){
			try{
				DriverInstanceData driverData = new DriverInstanceData();
				DriverBLManager driverBLManager = new DriverBLManager();
				EditDriverInstanceForm driverInstanceForm = (EditDriverInstanceForm) form;
				long driverInstanceid=Long.parseLong(request.getParameter("driverInstanceId"));
				driverData.setDriverInstanceId(driverInstanceid);
				driverData = driverBLManager.getDriverInstanceData(driverData);
				request.getSession().setAttribute("driverInstanceData",driverData);
				if(driverData.getDriverTypeId().longValue()==DriverTypeConstants.CSV_DRIVER){
					return mapping.findForward(EDIT_CSVDRIVER_FORWARD);
				}else if(driverData.getDriverTypeId().longValue()==DriverTypeConstants.DBCDR_DRIVER){
					return mapping.findForward(EDIT_DBCDRDRIVER_FORWARD);
				}else{
					ActionMessages messages = new ActionMessages();
					ActionMessage message1 = new ActionMessage("driver.drivertype.failure");
					messages.add("information",message1);
					saveErrors(request,messages);
					return mapping.findForward(FAILURE);
				}
				
			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessages messages = new ActionMessages();
				ActionMessage message1 = new ActionMessage("init.edit.failure");
				messages.add("information",message1);
				saveErrors(request,messages);
			} 
			return mapping.findForward(FAILURE);
		}else{
			Logger.logWarn(MODULE, "No Access on this Operation ");
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	
	}
}


