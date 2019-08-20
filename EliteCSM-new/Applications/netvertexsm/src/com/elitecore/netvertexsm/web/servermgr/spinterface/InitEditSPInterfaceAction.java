
package com.elitecore.netvertexsm.web.servermgr.spinterface;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.servermgr.spinterface.SPInterfaceBLManager;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.constants.DriverTypeConstants;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.servermgr.spinterface.form.EditSPInterfaceForm;
public class InitEditSPInterfaceAction extends BaseWebAction {

	private static final String EDIT_DBDRIVER_FORWARD = "initEditDBDriver";
	private static final String EDIT_LDAPDRIVER_FORWARD = "initEditLDAPDriver";
	
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_SP_INTERFACE;
	private static final String MODULE = "EDIT_DRIVER_INSTANCE";
	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of"+getClass().getName());
		if(checkAccess(request, ACTION_ALIAS)){
			try{
				
				DriverInstanceData driverData = new DriverInstanceData();
				SPInterfaceBLManager spInterfaceDriverBLManager = new SPInterfaceBLManager();
				EditSPInterfaceForm driverInstanceForm = (EditSPInterfaceForm) form;
				long driverInstanceid=Long.parseLong(request.getParameter("driverInstanceId"));
				driverData.setDriverInstanceId(driverInstanceid);
				driverData = spInterfaceDriverBLManager.getDriverInstanceData(driverData);
				request.getSession().setAttribute("driverInstanceData", driverData);
				
				if(driverData.getDriverTypeId().longValue()==DriverTypeConstants.DB_DRIVER){
					return mapping.findForward(EDIT_DBDRIVER_FORWARD);
				}else if(driverData.getDriverTypeId().longValue()==DriverTypeConstants.LDAP_DRIVER){
					return mapping.findForward(EDIT_LDAPDRIVER_FORWARD);
				}else{
					ActionMessages messages = new ActionMessages();
					ActionMessage message1 = new ActionMessage("spinterface.unknown");
					messages.add("information",message1);
					saveErrors(request,messages);
					
			        ActionMessages errorHeadingMessage = new ActionMessages();
			        ActionMessage message = new ActionMessage("spinterface.error.heading","updating");
			        errorHeadingMessage.add("errorHeading",message);
			        saveMessages(request,errorHeadingMessage);			
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
			
	        ActionMessages errorHeadingMessage = new ActionMessages();
	        ActionMessage message = new ActionMessage("spinterface.error.heading","updating");
	        errorHeadingMessage.add("errorHeading",message);
	        saveMessages(request,errorHeadingMessage);						
			return mapping.findForward(FAILURE);
		}else{
			Logger.logWarn(MODULE, "No Access on this Operation ");
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			
	        ActionMessages errorHeadingMessage = new ActionMessages();
	        message = new ActionMessage("spinterface.error.heading","updating");
	        errorHeadingMessage.add("errorHeading",message);
	        saveMessages(request,errorHeadingMessage);			
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
}


