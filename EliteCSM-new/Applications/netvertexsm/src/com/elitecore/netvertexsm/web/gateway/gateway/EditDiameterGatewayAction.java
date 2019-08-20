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
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.gateway.gateway.data.DiameterGatewayData;
import com.elitecore.netvertexsm.datamanager.gateway.gateway.data.GatewayData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.netvertexsm.web.gateway.gateway.form.DiameterGatewayForm;

public class EditDiameterGatewayAction extends BaseWebAction {
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_GATEWAY;
	private static final String MODULE = "EDIT_GATEWAY";
	private static final String EDIT_FORWARD = "editDiameterGateway";
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of"+getClass().getName());
		if(checkAccess(request, ACTION_ALIAS)){
			try{

				DiameterGatewayForm diameterGatewayForm = (DiameterGatewayForm) form;							
				
				
				String action = diameterGatewayForm.getAction();
				
				if(action!=null && action.equalsIgnoreCase("save")){
					Logger.logError(MODULE," UPDATING DATA ");
					GatewayBLManager gatewayBLManager = new GatewayBLManager();
					GatewayData gatewayData = (GatewayData) request.getSession().getAttribute("gatewayData");
					DiameterGatewayData diameterGatewayData = new DiameterGatewayData();
					diameterGatewayData.setGatewayId(diameterGatewayForm.getGatewayId());
					gatewayData.setCommProtocol(CommunicationProtocol.fromName(gatewayData.getCommProtocol()).id);
					diameterGatewayData.setHostId(diameterGatewayForm.getHostIdentity());
					diameterGatewayData.setRealm(diameterGatewayForm.getRealm());
					diameterGatewayData.setLocalAddress(diameterGatewayForm.getLocalAddress());
					diameterGatewayData.setRequestTimeout(diameterGatewayForm.getRequestTimeout());
					diameterGatewayData.setRetransmissionCount(diameterGatewayForm.getRetransmissionCount());
					diameterGatewayData.setAlternateHostId(diameterGatewayForm.getAlternateHostId());
					gatewayData.setProfileId(diameterGatewayForm.getGatewayProfileId());
					gatewayData.setPolicyEnforcementMethodName(diameterGatewayForm.getPolicyEnforcementMethodName());
					gatewayData.setConnectionUrl(diameterGatewayForm.getConnectionUrl());
					gatewayData.setDiameterGatewayData(diameterGatewayData);

					IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));				    
				    gatewayBLManager.updateGatewayBasicDetail(gatewayData,staffData,ACTION_ALIAS);
					gatewayBLManager.updateGatewayData(gatewayData,staffData,ACTION_ALIAS);
					request.getSession().removeAttribute("gatewayData");	            
					ActionMessage message = new ActionMessage("gateway.update.success",diameterGatewayForm.getGatewayName());
					ActionMessages messages = new ActionMessages();
					messages.add("information", message);
					saveMessages(request,messages);
					request.setAttribute("responseUrl","/initSearchGateway.do");
					return mapping.findForward(SUCCESS);
					
				}else{
					GatewayData gatewayData = (GatewayData)request.getAttribute("gatewayData");
					convertBeanToForm(gatewayData,diameterGatewayForm);
					request.setAttribute("diameterGatewayForm", diameterGatewayForm);
					return mapping.findForward(EDIT_FORWARD);
				}

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
				return mapping.findForward(FAILURE);
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
	private void convertBeanToForm(GatewayData gatewayData, DiameterGatewayForm form ) throws DataManagerException{
		GatewayBLManager gatewayBLManager = new GatewayBLManager();
		DiameterGatewayData diameterGatewayData = gatewayData.getDiameterGatewayData();
		form.setGatewayId(gatewayData.getGatewayId());
		form.setConnectionUrl(gatewayData.getConnectionUrl());
		
		if(diameterGatewayData!=null){
			form.setHostIdentity(diameterGatewayData.getHostId());
			form.setRealm(diameterGatewayData.getRealm());
			form.setLocalAddress(diameterGatewayData.getLocalAddress());
			form.setGatewayProfileId(gatewayData.getProfileId());			
			form.setGatewayProfileList(gatewayBLManager.getProfileList(CommunicationProtocol.DIAMETER.id));
			form.setPolicyEnforcementMethodName(gatewayData.getPolicyEnforcementMethodName());
			form.setDiameterProfileDataList(gatewayBLManager.getDiameterProfileDataList());
			form.setRequestTimeout(diameterGatewayData.getRequestTimeout());
			form.setRetransmissionCount(diameterGatewayData.getRetransmissionCount());
			List<GatewayData> diameterGatewayDataList = gatewayBLManager.getUpdatedDiameterGatewayDataList(CommunicationProtocol.DIAMETER.id,diameterGatewayData.getGatewayId());
			form.setAlternateHostList(diameterGatewayDataList);
			if(diameterGatewayData.getAlternateHostId() != null && diameterGatewayData.getAlternateHostId() > 0){
				form.setAlternateHostId(diameterGatewayData.getAlternateHostId());
			}else{
				form.setAlternateHostId(0);
			}
		}else{
			Logger.logError(MODULE, "Diameter Gateway Data object not found, Gateway Id = "+gatewayData.getGatewayId());
		}
	}
	private String getBooleanValue(String value){
		if(value!=null && value.equalsIgnoreCase("on")){
			return "true";
		}
		return "false";
	}
}