/**
 * @author milan paliwal
 * performs delete gateway data
 */
package com.elitecore.netvertexsm.web.gateway.gateway;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.netvertexsm.web.gateway.gateway.form.MiscGatewayForm;

import java.util.ArrayList;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.gateway.GatewayBLManager;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;


public class MiscGatewayAction extends BaseWebAction {
	
	private static final String SUCCESS = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.DELETE_GATEWAY;
	
	public ActionForward execute(ActionMapping mapping,ActionForm actionForm,HttpServletRequest request,HttpServletResponse response) throws Exception{
            
		Logger.logInfo(MODULE,"Enter Execute method of "+getClass().getName());
		if(checkAccess(request, ACTION_ALIAS)){
			try{
				MiscGatewayForm miscGatewayForm = (MiscGatewayForm)actionForm;
				GatewayBLManager gatewayBLManager =new GatewayBLManager();
				List<Long> gatewayIdList = new ArrayList<Long>();
				String[] strGatewayIds = request.getParameterValues("select"); 
				 
				for(int i=0; i<strGatewayIds.length; i++) {
					gatewayIdList.add(Long.parseLong(strGatewayIds[i]));
				}
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				gatewayBLManager.delete(gatewayIdList,staffData,ACTION_ALIAS);
				
				ActionMessage message = new ActionMessage("gateway.delete.success");
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", message);
	            saveMessages(request,messages);
	            request.setAttribute("responseUrl","/initSearchGateway.do?areaName="+miscGatewayForm.getAreaName()+"&locationId="+miscGatewayForm.getLocationId()+"&pageNumber="+miscGatewayForm.getPageNumber()+"&totalPages="+miscGatewayForm.getTotalPages()+"&totalRecords="+miscGatewayForm.getTotalRecords());
				
				return mapping.findForward(SUCCESS);
				
			}catch(org.hibernate.exception.ConstraintViolationException ce){

				Logger.logError(MODULE,"Error during Data Manager operation, Reason : "+ ce.getMessage());
                Logger.logTrace(MODULE,ce);
        		Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(ce);
      			request.setAttribute("errorDetails", errorElements);
      			
	            ActionMessages messages = new ActionMessages();
                ActionMessage message = new ActionMessage("gateway.delete.failure");
                ActionMessage reasonMessage = new ActionMessage("gateway.delete.failure.reason");
                messages.add("information", message);
                messages.add("information", reasonMessage);
                saveErrors(request, messages);
			
			}catch(Exception managerExp){
				Logger.logError(MODULE,"Error during Data Manager operation, reason : "+ managerExp.getMessage());
                Logger.logTrace(MODULE,managerExp);
        		Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
      			request.setAttribute("errorDetails", errorElements);
      			
	            ActionMessages messages = new ActionMessages();
                ActionMessage message = new ActionMessage("gateway.delete.failure");
                messages.add("information", message);
                saveErrors(request, messages);
			}
            ActionMessages errorHeadingMessage = new ActionMessages();
            ActionMessage message = new ActionMessage("gateway.error.heading","deleting");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage);			
		    return mapping.findForward(FAILURE_FORWARD);
		}else{
	        Logger.logWarn(MODULE, "Error during Data Manager operation ");
	        ActionMessage message = new ActionMessage("general.user.restricted");
	        ActionMessages messages = new ActionMessages();
	        messages.add("information", message);
	        saveErrors(request, messages);
	        
            ActionMessages errorHeadingMessage = new ActionMessages();
            message = new ActionMessage("gateway.error.heading","deleting");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage);      			

		return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
	

}
