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
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.gateway.gateway.data.GatewayData;
import com.elitecore.netvertexsm.datamanager.gateway.gateway.data.IGatewayData;
import com.elitecore.netvertexsm.datamanager.gateway.gateway.data.RadiusGatewayData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.netvertexsm.web.gateway.gateway.form.RadiusGatewayForm;

public class EditRadiusGatewayAction extends BaseWebAction {
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_GATEWAY;
	private static final String MODULE = "EDIT_GATEWAY";
	private static final String EDIT_FORWARD = "editRadiusGateway";

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of"+getClass().getName());
		if(checkAccess(request, ACTION_ALIAS)){
			try{

				RadiusGatewayForm radiusGatewayForm = (RadiusGatewayForm) form;
				GatewayBLManager gatewayBLManager = new GatewayBLManager();
				String action = radiusGatewayForm.getAction();
				if(action!=null && action.equalsIgnoreCase("save")){
					GatewayData gatewayData = (GatewayData) request.getSession().getAttribute("gatewayData");
					gatewayData.setCommProtocol(CommunicationProtocol.fromName(gatewayData.getCommProtocol()).id);
					gatewayData.setGatewayId(radiusGatewayForm.getGatewayId());
					RadiusGatewayData radiusGatewayData = new RadiusGatewayData();
					radiusGatewayData.setGatewayId(radiusGatewayForm.getGatewayId());
					radiusGatewayData.setSharedSecret(radiusGatewayForm.getSharedSecret());
					radiusGatewayData.setMinLocalPort(radiusGatewayForm.getMinLocalPort());
					gatewayData.setConnectionUrl(radiusGatewayForm.getConnectionUrl().trim());
					gatewayData.setProfileId(radiusGatewayForm.getGatewayProfileId());
					gatewayData.setPolicyEnforcementMethodName(radiusGatewayForm.getPolicyEnforcementMethodName());													
					
					gatewayData.setRadiusGatewayData(radiusGatewayData);
					IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
					gatewayBLManager.updateGatewayBasicDetail(gatewayData,staffData,ACTION_ALIAS);
					gatewayBLManager.updateGatewayData(gatewayData,staffData,ACTION_ALIAS);
					request.getSession().removeAttribute("gatewayData");	            
					ActionMessage message = new ActionMessage("gateway.update.success",radiusGatewayForm.getGatewayName());
					ActionMessages messages = new ActionMessages();
					messages.add("information", message);
					saveMessages(request,messages);
					request.setAttribute("responseUrl","/initSearchGateway.do");
					return mapping.findForward(SUCCESS);
				}else{
					GatewayData gatewayData = (GatewayData)request.getSession().getAttribute("gatewayData");
					convertBeanToForm(gatewayData,radiusGatewayForm);
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
	private void convertBeanToForm(GatewayData gatewayData,RadiusGatewayForm form) throws DataManagerException{
		GatewayBLManager gatewayBLManager = new GatewayBLManager();
		RadiusGatewayData radiusGatewayData =  gatewayData.getRadiusGatewayData();
		form.setGatewayId(gatewayData.getGatewayId());
		form.setConnectionUrl(gatewayData.getConnectionUrl());
		form.setSharedSecret(radiusGatewayData.getSharedSecret());
		form.setMinLocalPort(radiusGatewayData.getMinLocalPort());
		form.setGatewayProfileId(gatewayData.getProfileId());			
		form.setGatewayProfileList(gatewayBLManager.getProfileList(CommunicationProtocol.RADIUS.id));
		form.setPolicyEnforcementMethodName(gatewayData.getPolicyEnforcementMethodName());
		
	}
}