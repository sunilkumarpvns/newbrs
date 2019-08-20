package com.elitecore.netvertexsm.web.gateway.gateway;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.gateway.GatewayBLManager;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.gateway.gateway.data.GatewayData;
import com.elitecore.netvertexsm.datamanager.gateway.gateway.data.RadiusGatewayData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.netvertexsm.web.gateway.gateway.form.RadiusGatewayForm;

public class CreateRadiusGatewayAction extends BaseWebAction {
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String CREATE_RADIUS_FORWARD = "createRadiusGatewayPage";
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_GATEWAY;

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());

		if((checkAccess(request, ACTION_ALIAS))){
			Logger.logTrace(MODULE,"Enter the execute method of :"+getClass().getName());

			try{
				RadiusGatewayForm radiusGatewayForm = (RadiusGatewayForm) form;
				GatewayBLManager gatewayBLManager = new GatewayBLManager();
				GatewayData gatewayData = (GatewayData)request.getSession().getAttribute("gatewayData");
				String action = radiusGatewayForm.getAction();
				
				if(action!=null && action.equalsIgnoreCase("save")){
					RadiusGatewayData radiusGatewayData = new RadiusGatewayData();
					radiusGatewayData.setSharedSecret(radiusGatewayForm.getSharedSecret());
					radiusGatewayData.setMinLocalPort(radiusGatewayForm.getMinLocalPort());
					gatewayData.setPolicyEnforcementMethodName(radiusGatewayForm.getPolicyEnforcementMethodName());
					gatewayData.setConnectionUrl(radiusGatewayForm.getConnectionUrl().trim());
					gatewayData.setRadiusGatewayData(radiusGatewayData);
					IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
					gatewayBLManager.create(gatewayData,staffData,ACTION_ALIAS);
					request.getSession().removeAttribute("gatewayData");
					ActionMessage message = new ActionMessage("gateway.create.success",gatewayData.getGatewayName());
					ActionMessages messages = new ActionMessages();
					messages.add("information", message);
					saveMessages(request,messages);
					request.setAttribute("responseUrl","/initSearchGateway.do");
					return mapping.findForward(SUCCESS_FORWARD);
				}else{
										
					String requestedAction = (String) request.getAttribute("action");
					if(requestedAction!=null && requestedAction.equalsIgnoreCase("duplicate")){
						Logger.logTrace(MODULE, "Creating Duplicate Radius Gateway");					
						radiusGatewayForm.setSharedSecret(gatewayData.getRadiusGatewayData().getSharedSecret());
						radiusGatewayForm.setMinLocalPort(gatewayData.getRadiusGatewayData().getMinLocalPort());
						request.setAttribute("action","Duplicate");
					}else {
						radiusGatewayForm.setSharedSecret("secret");
						radiusGatewayForm.setMinLocalPort(10l);
					}
					return mapping.findForward(CREATE_RADIUS_FORWARD);
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
}