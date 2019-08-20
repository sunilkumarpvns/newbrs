package com.elitecore.netvertexsm.web.gateway.gateway;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.CommunicationProtocol;
import com.elitecore.netvertexsm.blmanager.gateway.GatewayBLManager;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.gateway.gateway.data.DiameterGatewayData;
import com.elitecore.netvertexsm.datamanager.gateway.gateway.data.GatewayData;
import com.elitecore.netvertexsm.datamanager.gateway.gateway.data.IGatewayData;
import com.elitecore.netvertexsm.datamanager.gateway.gateway.data.RadiusGatewayData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;

public class ViewGatewayAction extends BaseWebAction {
	private static final String VIEW_FORWARD = "viewGateway";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.VIEW_GATEWAY;
	private static final String VIEW_ASSOCIATIONS = "viewGatewayAssociation";
	private static final String MODULE = "VIEW_GATEWAY";

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of"+getClass().getName());
		if(checkAccess(request, ACTION_ALIAS)){
			try{
				GatewayBLManager gatewayBLManager = new GatewayBLManager();
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				IGatewayData  gatewayData = new GatewayData();
				String strGatewayId = request.getParameter("gatewayId");
				String strCommProtocolId = request.getParameter("commProtocolId");
				Long gatewayId = Long.parseLong(strGatewayId);

				if(gatewayId!=null ){
					gatewayData.setGatewayId(gatewayId);										
					gatewayData = gatewayBLManager.getGatewayData(gatewayData,staffData,ACTION_ALIAS);
				}
				if(strCommProtocolId!=null) {
					if(strCommProtocolId.equals(CommunicationProtocol.RADIUS.id)) {
						RadiusGatewayData radiusGatewayData = new RadiusGatewayData();
						radiusGatewayData.setGatewayId(gatewayId);
						radiusGatewayData = gatewayBLManager.getRadiusGatewayData(radiusGatewayData);
						gatewayData.setRadiusGatewayData(radiusGatewayData);
					}else if(strCommProtocolId.equals(CommunicationProtocol.DIAMETER.id)) {
						DiameterGatewayData diameterGatewayData = new DiameterGatewayData();
						diameterGatewayData.setGatewayId(gatewayId);
						diameterGatewayData = gatewayBLManager.getDiameterGatewayData(diameterGatewayData);
						gatewayData.setDiameterGatewayData(diameterGatewayData);						
					}
				}else {
					gatewayData.setRadiusGatewayData(null);
					gatewayData.setDiameterGatewayData(null);
				}

				String viewAssociations=request.getParameter("viewAssociations");
			   	if(viewAssociations!=null && viewAssociations.equals("true")){
			   		List<GatewayData> gatewaylist=gatewayBLManager.getGatewayAssociations(CommunicationProtocol.DIAMETER.id, gatewayId);
			   		if(Collectionz.isNullOrEmpty(gatewaylist) == false){
			   		Set<GatewayData> gatewaySet=new HashSet<GatewayData>(gatewaylist);
			   		gatewayData.setGatewaySet(gatewaySet);
			   		}
			   		request.setAttribute("gatewayData", gatewayData);	
					return mapping.findForward(VIEW_ASSOCIATIONS);
			   	}
				
				
				request.setAttribute("gatewayData",gatewayData);
				return mapping.findForward(VIEW_FORWARD);
			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("gateway.view.failure"));
	            saveErrors(request, messages);
	            
	            ActionMessages errorHeadingMessage = new ActionMessages();
	            ActionMessage message = new ActionMessage("gateway.error.heading","viewing");
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
            message = new ActionMessage("gateway.error.heading","viewing");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
}
