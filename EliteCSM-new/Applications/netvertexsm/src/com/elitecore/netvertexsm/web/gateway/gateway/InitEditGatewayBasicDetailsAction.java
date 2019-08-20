package com.elitecore.netvertexsm.web.gateway.gateway;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.corenetvertex.constants.CommunicationProtocol;
import com.elitecore.netvertexsm.blmanager.gateway.GatewayBLManager;
import com.elitecore.netvertexsm.datamanager.gateway.gateway.data.GatewayData;
import com.elitecore.netvertexsm.datamanager.gateway.gateway.data.GatewayLocationData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfileData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.gateway.gateway.form.GatewayForm;

public class InitEditGatewayBasicDetailsAction extends BaseWebAction {
	private static final String EDIT_FORWARD = "initEditGatewayBasicDetails";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_GATEWAY;
	private static final String MODULE = "EDIT_GATEWAY";
	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of"+getClass().getName());
		if(checkAccess(request, ACTION_ALIAS)){
			try{
				GatewayForm gatewayForm = (GatewayForm) form;
				GatewayData gatewayData = new GatewayData();
								
				long gatewayid=Long.parseLong(request.getParameter("gatewayId"));
				gatewayData.setGatewayId(gatewayid);
				GatewayBLManager gatewayBLManager = new GatewayBLManager();
				
				gatewayData = gatewayBLManager.getGatewayDetail(gatewayData);
				gatewayData.setCommProtocol(CommunicationProtocol.fromId(gatewayData.getCommProtocol()).name);
				request.setAttribute("gatewayData", gatewayData);
				request.getSession().setAttribute("gatewayData", gatewayData);
				
				gatewayForm.setGatewayId(gatewayid); 
				gatewayForm.setAreaName(gatewayData.getAreaName());
				gatewayForm.setCommProtocolId(gatewayData.getCommProtocol());
				gatewayForm.setConnectionUrl(gatewayData.getConnectionUrl());
				gatewayForm.setGatewayName(gatewayData.getGatewayName());
				if(gatewayData.getLocationId()!=null){
					gatewayForm.setLocationId(gatewayData.getLocationId());
				}
				
				gatewayForm.setDescription(gatewayData.getDescription());
				gatewayForm.setGatewayProfileId(gatewayData.getProfileId());
				gatewayForm.setPolicyEnforcementMethodName(gatewayData.getPolicyEnforcementMethodName());				
				String commProtocolId = gatewayData.getCommProtocol();
				List<GatewayProfileData> 	profileList = gatewayBLManager.getProfileList(commProtocolId);
				List<GatewayLocationData>  locationList = gatewayBLManager.getLocationList();
				
				
				gatewayForm.setGatewayProfileList(profileList);		    	
				gatewayForm.setLocationList(locationList);
				request.setAttribute("gatewayForm", gatewayForm);
				return mapping.findForward(EDIT_FORWARD);
				
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
			return mapping.findForward(FAILURE_FORWARD);
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

