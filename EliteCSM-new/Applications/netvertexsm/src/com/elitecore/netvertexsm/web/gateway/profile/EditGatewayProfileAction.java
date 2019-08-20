package com.elitecore.netvertexsm.web.gateway.profile;

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
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfileData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.netvertexsm.web.gateway.profile.form.EditGatewayProfileForm;

public class EditGatewayProfileAction extends BaseWebAction {

	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_GATEWAY_PROFILE;
	private static final String MODULE = "EDIT_GATEWAY_PROFILE";
	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of"+getClass().getName());
		if(checkAccess(request, ACTION_ALIAS)){
			try{
				EditGatewayProfileForm gatewayProfileForm = (EditGatewayProfileForm) form;
				GatewayBLManager gatewayBLManager = new GatewayBLManager();
				GatewayProfileData gatewayProfileData = new GatewayProfileData();
				
				gatewayProfileData.setProfileId(gatewayProfileForm.getProfileId());
				gatewayProfileData.setProfileName(gatewayProfileForm.getGatewayProfileName());
				gatewayProfileData.setGatewayType(gatewayProfileForm.getGatewayType());
			    gatewayProfileData.setCommProtocolId(CommunicationProtocol.fromName(gatewayProfileForm.getCommProtocolId()).id);
			    gatewayProfileData.setVedorId(gatewayProfileForm.getVendorId());
			    gatewayProfileData.setFirmware(gatewayProfileForm.getFirmware());
			    gatewayProfileData.setMaxThroughput(gatewayProfileForm.getMaxThroughtput());
			    gatewayProfileData.setBufferBW(gatewayProfileForm.getBufferBW());
			    gatewayProfileData.setMaxIPCANSession(gatewayProfileForm.getMaxIPCANSession());
			    gatewayProfileData.setDescription(gatewayProfileForm.getDescription());
			    gatewayProfileData.setUsageReportingTime(gatewayProfileForm.getUsageReportingTime());
			    gatewayProfileData.setRevalidationMode(gatewayProfileForm.getRevalidationMode());			    
			    
			    IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			    gatewayBLManager.updateGatewayProfile(gatewayProfileData,staffData,ACTION_ALIAS);
			    
			    ActionMessage message = new ActionMessage("gateway.profile.update.success",gatewayProfileForm.getGatewayProfileName());
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", message);
	            saveMessages(request,messages);
			    request.setAttribute("responseUrl","/initSearchProfile.do");
				return mapping.findForward(SUCCESS_FORWARD);
			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessages messages = new ActionMessages();
				messages.add("information",new ActionMessage("gateway.profile.update.failed"));
				saveErrors(request,messages);
				
	            ActionMessages errorHeadingMessage = new ActionMessages();
	            ActionMessage message = new ActionMessage("gateway.profile.error.heading","updating");
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
            message = new ActionMessage("gateway.profile.error.heading","updating");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage); 	
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	
	}
	
}
			
