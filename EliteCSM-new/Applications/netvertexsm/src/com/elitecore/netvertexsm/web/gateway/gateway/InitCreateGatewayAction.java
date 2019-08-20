package com.elitecore.netvertexsm.web.gateway.gateway;

import java.util.ArrayList;
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
import com.elitecore.netvertexsm.datamanager.gateway.gateway.data.GatewayLocationData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfileData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.gateway.gateway.form.GatewayForm;

public class InitCreateGatewayAction extends BaseWebAction {
	private static final String CREATE_GATEWAY = "initCreateGateway";
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_GATEWAY;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		
		if(checkAccess(request, ACTION_ALIAS)){
			try{
				GatewayBLManager gatewayBLManager = new GatewayBLManager();
				GatewayForm gatewayForm = (GatewayForm) form;
				setDefaultValues(gatewayForm,request);
				List<GatewayLocationData> locationList = gatewayBLManager.getLocationList();
				List<GatewayProfileData> profileList = gatewayBLManager.getProfileList();

				List<GatewayProfileData> ciscoProfileList = new ArrayList<GatewayProfileData>();
				List<GatewayProfileData> radiusProfileList = new ArrayList<GatewayProfileData>();
				List<GatewayProfileData> diameterProfileList = new ArrayList<GatewayProfileData>();

				for(GatewayProfileData profileData : profileList) {
					String commProtocolId = profileData.getCommProtocolId();
					if(commProtocolId.equalsIgnoreCase(CommunicationProtocol.RADIUS.id)){
						radiusProfileList.add(profileData);
					}
					else if(commProtocolId.equalsIgnoreCase(CommunicationProtocol.DIAMETER.id)){ 
						diameterProfileList.add(profileData);
					}
				}
				gatewayForm.setRadiusProfileList(radiusProfileList);
				gatewayForm.setDiameterProfileList(diameterProfileList);
				gatewayForm.setLocationList(locationList);
				return mapping.findForward(CREATE_GATEWAY);
			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessages messages = new ActionMessages();
				ActionMessage message1 = new ActionMessage("create.failure");
				messages.add("information",message1);
				saveErrors(request,messages);
				return mapping.findForward(FAILURE);
			}
		}else{
            Logger.logWarn(MODULE,"No Access On this Operation.");
	        ActionMessage message = new ActionMessage("general.user.restricted");
            ActionMessages messages = new ActionMessages();
            messages.add("information", message);
            saveErrors(request, messages);
            return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
	private void setDefaultValues(GatewayForm form, HttpServletRequest request){
		form.setDescription(getDefaultDescription(request));
	}
}
