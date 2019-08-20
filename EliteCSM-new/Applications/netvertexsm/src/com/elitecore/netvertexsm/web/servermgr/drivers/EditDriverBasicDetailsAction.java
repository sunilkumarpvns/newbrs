package com.elitecore.netvertexsm.web.servermgr.drivers;

import java.sql.Timestamp;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.netvertexsm.web.servermgr.drivers.form.EditDriverInstanceForm;

public class EditDriverBasicDetailsAction extends BaseWebAction {
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_DRIVER_ACTION;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		Logger.logTrace(MODULE,"Enter the execute method of :"+getClass().getName());
		
		if((checkAccess(request, ACTION_ALIAS))){
			Logger.logTrace(MODULE,"Enter the execute method of :"+getClass().getName());
			
			try{
				EditDriverInstanceForm driverInstanceForm = (EditDriverInstanceForm) form;
				DriverBLManager driverBLManager = new DriverBLManager();
				
				DriverInstanceData driverInstanceData = convertFormToBean(driverInstanceForm);

				Date currentDate = new Date();
				driverInstanceData.setLastModifiedDate(new Timestamp(currentDate.getTime()));
				IStaffData staff =getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				driverInstanceData.setLastModifiedByStaffId(staff.getStaffId());
				driverBLManager.update(driverInstanceData,staff,ACTION_ALIAS);
				
				ActionMessage message = new ActionMessage("driver.update.success", driverInstanceData.getName());
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", message);
	            saveMessages(request,messages);
				request.setAttribute("responseUrl","/initSearchDriverInstance.do");
				return mapping.findForward(SUCCESS);
			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("driver.update.failure"));
	            saveErrors(request, messages);
	            return mapping.findForward(FAILURE);
			}
		}else{
	        Logger.logError(MODULE, "Error during Data Manager operation ");
	        ActionMessage message = new ActionMessage("general.user.restricted");
	        ActionMessages messages = new ActionMessages();
	        messages.add("information", message);
	        saveErrors(request, messages);
			
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
	private DriverInstanceData convertFormToBean(EditDriverInstanceForm form){
		DriverInstanceData data = new DriverInstanceData();
		data.setName(form.getName());
		data.setDescription(form.getDescription());
		data.setDriverInstanceId(form.getDriverInstanceId());
		return data;
	}
}
