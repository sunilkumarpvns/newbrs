package com.elitecore.netvertexsm.web.gateway.gateway;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.corenetvertex.constants.CommunicationProtocol;
import com.elitecore.netvertexsm.blmanager.gateway.GatewayBLManager;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.gateway.gateway.data.GatewayData;
import com.elitecore.netvertexsm.datamanager.gateway.gateway.data.IGatewayData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.netvertexsm.web.gateway.gateway.form.GatewayForm;

public class EditGatewayBasicDetailsAction extends BaseWebAction {
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_GATEWAY;
	private static final String MODULE = "EDIT_GATEWAY";
	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of"+getClass().getName());
		if(checkAccess(request, ACTION_ALIAS)){
			try{

				GatewayForm gatewayForm = (GatewayForm) form;
				GatewayBLManager gatewayBLManager = new GatewayBLManager();
				
				IGatewayData gatewayData = new GatewayData();
				gatewayData.setGatewayId(gatewayForm.getGatewayId());
				gatewayData.setAreaName(gatewayForm.getAreaName());
			    gatewayData.setCommProtocol(CommunicationProtocol.fromName(gatewayForm.getCommProtocolId()).id);			    
			    gatewayData.setLocationId(gatewayForm.getLocationId());
			    gatewayData.setDescription(gatewayForm.getDescription());
/*				gatewayData.setProfileId(gatewayForm.getGatewayProfileId());
				gatewayData.setPolicyEnforcementMethodName(gatewayForm.getPolicyEnforcementMethodName());
*/				gatewayData.setGatewayName(gatewayForm.getGatewayName());
			    IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			    gatewayBLManager.updateGatewayBasicDetail(gatewayData,staffData,ACTION_ALIAS);
			    
			    request.getSession().removeAttribute("gatewayData");	            
			    ActionMessage message = new ActionMessage("gateway.update.success",gatewayForm.getGatewayName());
			    ActionMessages messages = new ActionMessages();
			    messages.add("information", message);
			    saveMessages(request,messages);
			    request.setAttribute("responseUrl","/initSearchGateway.do");
				return mapping.findForward(SUCCESS_FORWARD);

			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessages messages = new ActionMessages();
				messages.add("information",new ActionMessage("gateway.update.failed"));
				saveErrors(request,messages);
				
	            ActionMessages errorHeadingMessage = new ActionMessages();
	            ActionMessage message = new ActionMessage("gateway.error.heading","updating");
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
            message = new ActionMessage("gateway.error.heading","updating");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage); 	            			
			
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}	
	}
}