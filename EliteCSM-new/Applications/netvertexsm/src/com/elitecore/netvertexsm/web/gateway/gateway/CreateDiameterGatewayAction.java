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
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.gateway.gateway.data.DiameterGatewayData;
import com.elitecore.netvertexsm.datamanager.gateway.gateway.data.GatewayData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfileData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.netvertexsm.web.gateway.gateway.form.DiameterGatewayForm;

public class CreateDiameterGatewayAction extends BaseWebAction {
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String CREATE_DIAMETER_FORWARD = "createDiameterGatewayPage";
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_GATEWAY;

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());

		if((checkAccess(request, ACTION_ALIAS))){
			Logger.logTrace(MODULE,"Enter the execute method of :"+getClass().getName());

			try{
				DiameterGatewayForm diameterGatewayForm = (DiameterGatewayForm) form;

				GatewayBLManager gatewayBLManager = new GatewayBLManager();
				GatewayData gatewayData = (GatewayData)request.getSession().getAttribute("gatewayData");
				
				GatewayProfileData gatewayProfileData = gatewayBLManager.getGatewayProfileData(gatewayData.getProfileId());
				 
				request.setAttribute("gatewayProfileData", gatewayProfileData);				
								
				String action = diameterGatewayForm.getAction();
				
				if(action!=null && action.equalsIgnoreCase("save")){
					DiameterGatewayData diameterGatewayData = new DiameterGatewayData();
					diameterGatewayData.setHostId(diameterGatewayForm.getHostIdentity());
					diameterGatewayData.setRealm(diameterGatewayForm.getRealm());
					diameterGatewayData.setLocalAddress(diameterGatewayForm.getLocalAddress());
					diameterGatewayData.setRequestTimeout(diameterGatewayForm.getRequestTimeout());
					diameterGatewayData.setRetransmissionCount(diameterGatewayForm.getRetransmissionCount());
					if(diameterGatewayForm.getAlternateHostId() > 0){
						diameterGatewayData.setAlternateHostId(diameterGatewayForm.getAlternateHostId());
					}else{
						diameterGatewayData.setAlternateHostId(null);
					}
					
					gatewayData.setPolicyEnforcementMethodName(diameterGatewayForm.getPolicyEnforcementMethodName());
					gatewayData.setConnectionUrl(diameterGatewayForm.getConnectionUrl());
					gatewayData.setDiameterGatewayData(diameterGatewayData);					
					IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
					gatewayBLManager.create(gatewayData,staffData,ACTION_ALIAS);

					ActionMessage message = new ActionMessage("gateway.create.success",gatewayData.getGatewayName());
					ActionMessages messages = new ActionMessages();
					messages.add("information", message);
					saveMessages(request,messages);
					request.setAttribute("responseUrl","/initSearchGateway.do");
					return mapping.findForward(SUCCESS_FORWARD);
				}else{
					diameterGatewayForm.setConnectionUrl(gatewayData.getConnectionUrl());
					String requestedAction = (String) request.getAttribute("action");
					List<GatewayData> alternateHostList = gatewayBLManager.getDiameterGatewayDataList(CommunicationProtocol.DIAMETER.id);
					diameterGatewayForm.setAlternateHostList(alternateHostList);
					if(requestedAction!=null && requestedAction.equalsIgnoreCase("duplicate")){
						Logger.logTrace(MODULE, "Creating Duplicate Diameter Gateway");
						diameterGatewayForm.setHostIdentity(gatewayData.getDiameterGatewayData().getHostId());
						diameterGatewayForm.setRealm(gatewayData.getDiameterGatewayData().getRealm());
						diameterGatewayForm.setLocalAddress(gatewayData.getDiameterGatewayData().getLocalAddress());
						diameterGatewayForm.setRequestTimeout(gatewayData.getDiameterGatewayData().getRequestTimeout());
						diameterGatewayForm.setRetransmissionCount(gatewayData.getDiameterGatewayData().getRetransmissionCount());	
						Long alternateHostId = gatewayData.getDiameterGatewayData().getAlternateHostId();
						if(alternateHostId != null && alternateHostId > 0){
							diameterGatewayForm.setAlternateHostId(alternateHostId);
						}else{
							diameterGatewayForm.setAlternateHostId(0);
						}
						request.setAttribute("diameterGatewayForm",diameterGatewayForm);
						request.setAttribute("action","Duplicate");
					}					
					return mapping.findForward(CREATE_DIAMETER_FORWARD);
					
				}
			}catch (Exception e) {
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessages messages = new ActionMessages();
				messages.add("information", new ActionMessage("gateway.create.failure"));
				saveErrors(request, messages);
				return mapping.findForward(FAILURE_FORWARD);
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
	private String getBooleanValue(String value){
		if(value!=null && value.equalsIgnoreCase("on")){
			return "true";
		}
		return "false";
	}
}