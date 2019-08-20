package com.elitecore.netvertexsm.web.gateway.gateway;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.corenetvertex.constants.CommunicationProtocol;
import com.elitecore.netvertexsm.datamanager.gateway.gateway.data.GatewayData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.gateway.gateway.form.GatewayForm;

public class CreateGatewayAction extends BaseWebAction {
	private static final String RADIUS_FORWARD = "initRadiusGateway";
	private static final String DIAMETER_FORWARD = "initDiameterGateway";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_GATEWAY;
	private static final String LOCALHOST = "127.0.0.1";
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		
		if(checkAccess(request, ACTION_ALIAS)){
			try {
			    GatewayForm gatewayForm = (GatewayForm) form;
			    String gatewayInterface = gatewayForm.getCommProtocolId();
				GatewayData gatewayData = new GatewayData();
				gatewayData.setAreaName(gatewayForm.getAreaName());				
				if(gatewayForm.getLocationId()==0){
					gatewayData.setLocationId(null);
				}else{
					gatewayData.setLocationId(gatewayForm.getLocationId());	
				}
				gatewayData.setDescription(gatewayForm.getDescription());
				gatewayData.setCommProtocol(gatewayForm.getCommProtocolId());
				gatewayData.setGatewayName(gatewayForm.getGatewayName());
				gatewayData.setConnectionUrl(LOCALHOST);
				request.getSession().setAttribute("gatewayData",gatewayData);
				if(gatewayInterface.equalsIgnoreCase(CommunicationProtocol.RADIUS.id)) {
			    	gatewayData.setProfileId(gatewayForm.getRadiusGatewayProfileId());
			    	return mapping.findForward(RADIUS_FORWARD);
			    }else if(gatewayInterface.equalsIgnoreCase(CommunicationProtocol.DIAMETER.id)) {			    	
			    	gatewayData.setProfileId(gatewayForm.getDiameterGatewayProfileId());			    	
			    	return mapping.findForward(DIAMETER_FORWARD);
			    }
			   
			}catch (Exception e) {
				e.printStackTrace();
			}   
		}else{
            Logger.logWarn(MODULE,"No Access On this Operation.");
	        ActionMessage message = new ActionMessage("general.user.restricted");
            ActionMessages messages = new ActionMessages();
            messages.add("information", message);
            saveErrors(request, messages);
            
            ActionMessages errorHeadingMessage = new ActionMessages();
            message = new ActionMessage("gateway.error.heading","creating");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage); 	            			

            return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
		
        ActionMessages errorHeadingMessage = new ActionMessages();
        ActionMessage message = new ActionMessage("gateway.error.heading","creating");
        errorHeadingMessage.add("errorHeading",message);
        saveMessages(request,errorHeadingMessage); 	            			
		
		return mapping.findForward(FAILURE_FORWARD);
	}
}