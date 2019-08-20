package com.elitecore.netvertexsm.web.servermgr.spinterface;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.servermgr.spinterface.SPInterfaceBLManager;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.constraintviolation.ConstraintViolationException;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
public class MiscSPInterfaceAction extends BaseWebAction {
	private static final String LIST_FORWARD = "searchSPInterfaceList";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.DELETE_SP_INTERFACE;
	
	public ActionForward execute(ActionMapping mapping,ActionForm actionForm,HttpServletRequest request,HttpServletResponse response) throws Exception{
            
		Logger.logInfo(MODULE,"Enter Execute method of "+getClass().getName());
		if(checkAccess(request, ACTION_ALIAS)){
			try{
				SPInterfaceBLManager spInterfaceDriverBLManager = new SPInterfaceBLManager();
				List<Long> driverInstanceIdList = new ArrayList<Long>();
				IStaffData staff =getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				
				String[] strDriverInstanceIds = request.getParameterValues("select"); 
				
				for(int i=0; i<strDriverInstanceIds.length; i++) {
					driverInstanceIdList.add(Long.parseLong(strDriverInstanceIds[i]));
				}
				spInterfaceDriverBLManager.delete(driverInstanceIdList,staff,ACTION_ALIAS);
				
				request.setAttribute("responseUrl", "/initSearchSPInterface");
				ActionMessage message = new ActionMessage("spinterface.delete.success");
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveMessages(request, messages);
				return mapping.findForward(SUCCESS);				
				//return mapping.findForward(LIST_FORWARD);
			}catch(ConstraintViolationException managerExp){
				Logger.logError(MODULE,"Error during Data Manager operation, reason : "+ managerExp.getMessage());
                Logger.logTrace(MODULE,managerExp);
        		Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
      			request.setAttribute("errorDetails", errorElements);
                ActionMessage message = new ActionMessage("spinterface.delete.failure");
                ActionMessage reasonMessage = new ActionMessage("spinterface.inuse");
                ActionMessages messages = new ActionMessages();
                messages.add("information", message);
                messages.add("information", reasonMessage);
                saveErrors(request, messages);
                
		        ActionMessages errorHeadingMessage = new ActionMessages();
		        message = new ActionMessage("spinterface.error.heading","deleting");
		        errorHeadingMessage.add("errorHeading",message);
		        saveMessages(request,errorHeadingMessage);
                return mapping.findForward(FAILURE_FORWARD);
			}catch(DataManagerException managerExp){
				Logger.logError(MODULE,"Error during Data Manager operation, reason : "+ managerExp.getMessage());
                Logger.logTrace(MODULE,managerExp);
        		Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
      			request.setAttribute("errorDetails", errorElements);
                ActionMessage message = new ActionMessage("spinterface.delete.failure");
                ActionMessages messages = new ActionMessages();
                messages.add("information", message);
                saveErrors(request, messages);
                
		        ActionMessages errorHeadingMessage = new ActionMessages();
		        message = new ActionMessage("spinterface.error.heading","deleting");
		        errorHeadingMessage.add("errorHeading",message);
		        saveMessages(request,errorHeadingMessage);
		         
                return mapping.findForward(FAILURE_FORWARD);
			}catch(Exception managerExp){
				Logger.logError(MODULE,"Error during misc operation, reason : "+ managerExp.getMessage());
                Logger.logTrace(MODULE,managerExp);
        		Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
      			request.setAttribute("errorDetails", errorElements);
                ActionMessage message = new ActionMessage("spinterface.delete.failure");
                ActionMessages messages = new ActionMessages();
                messages.add("information", message);
                saveErrors(request, messages);
                
		        ActionMessages errorHeadingMessage = new ActionMessages();
		        message = new ActionMessage("spinterface.error.heading","deleting");
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
	        message = new ActionMessage("spinterface.error.heading","deleting");
	        errorHeadingMessage.add("errorHeading",message);
	        saveMessages(request,errorHeadingMessage);
		return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
}
