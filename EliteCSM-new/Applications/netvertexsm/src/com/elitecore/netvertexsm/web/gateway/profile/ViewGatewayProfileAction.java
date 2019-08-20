package com.elitecore.netvertexsm.web.gateway.profile;

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

import com.elitecore.corenetvertex.constants.CommunicationProtocol;
import com.elitecore.corenetvertex.constants.UMStandard;
import com.elitecore.netvertexsm.blmanager.gateway.GatewayBLManager;
import com.elitecore.netvertexsm.datamanager.gateway.gateway.data.GatewayData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfileData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfilePacketMapData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfileRuleMappingData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.GroovyScriptData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.PCCRuleMappingData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;

public class ViewGatewayProfileAction extends BaseWebAction {
	private static final String VIEW_FORWARD = "viewGatewayProfile";
	private static final String VIEW_ASSOCIATIONS = "viewGatewayProfileAssociations";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.VIEW_GATEWAY_PROFILE;
	private static final String MODULE = "VIEW_GATEWAY_PROFILE";
	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of"+getClass().getName());
		if(checkAccess(request, ACTION_ALIAS)){
			try{
				GatewayBLManager gatewayBLManager = new GatewayBLManager();
				List<GatewayProfilePacketMapData> diameterPacketMapList = null;
				List<PCCRuleMappingData> pccRuleMappingDataList = null;
				List<GroovyScriptData> groovyScriptsDataList = null;
				List<GatewayProfileRuleMappingData> gatewayProfileRuleMappingList=null;
				
				long profileId = Long.parseLong(request.getParameter("profileId"));
				GatewayProfileData gatewayProfileData = gatewayBLManager.getGatewayProfileData(profileId);
			   	String viewAssociations=request.getParameter("viewAssociations");
			   	if(viewAssociations!=null && viewAssociations.equals("true")){
			   		List<GatewayData> gatewaylist=gatewayBLManager.getGatewayListByProfileId(profileId);
			   		Set<GatewayData> gatewaySet=new HashSet<GatewayData>(gatewaylist);
			   		gatewayProfileData.setGatewaySet(gatewaySet);
			   		request.setAttribute("gatewayProfileData", gatewayProfileData);			
					return mapping.findForward(VIEW_ASSOCIATIONS);
			   	}
				
				if(gatewayProfileData.getCommProtocolId().equalsIgnoreCase(CommunicationProtocol.RADIUS.id)){
					diameterPacketMapList = gatewayBLManager.getProfilePacketMapList(gatewayProfileData.getProfileId());
					//pccRuleMappingDataList = gatewayBLManager.getPCCRuleMapList(gatewayProfileData.getProfileId());
					groovyScriptsDataList = gatewayBLManager.getGroovyScriptsDataList(gatewayProfileData.getProfileId());
					gatewayProfileRuleMappingList=gatewayBLManager.getProfileRuleMappingList(gatewayProfileData);
					request.setAttribute("diameterPacketMapList", diameterPacketMapList);
					request.setAttribute("pccRuleMapList", pccRuleMappingDataList);
					request.setAttribute("groovyScriptsDataList", groovyScriptsDataList);
					request.setAttribute("gatewayProfileRuleMappingList",gatewayProfileRuleMappingList);
				}
				
				if(gatewayProfileData.getCommProtocolId().equalsIgnoreCase(CommunicationProtocol.DIAMETER.id)){
					
				    setUsageMetringStandard(gatewayProfileData);
					
					diameterPacketMapList = gatewayBLManager.getProfilePacketMapList(gatewayProfileData.getProfileId());
					//pccRuleMappingDataList = gatewayBLManager.getPCCRuleMapList(gatewayProfileData.getProfileId());
					groovyScriptsDataList = gatewayBLManager.getGroovyScriptsDataList(gatewayProfileData.getProfileId());
					gatewayProfileRuleMappingList=gatewayBLManager.getProfileRuleMappingList(gatewayProfileData);
					request.setAttribute("diameterPacketMapList", diameterPacketMapList);
					request.setAttribute("pccRuleMapList", pccRuleMappingDataList);
					request.setAttribute("groovyScriptsDataList", groovyScriptsDataList);
					request.setAttribute("gatewayProfileRuleMappingList",gatewayProfileRuleMappingList);
				}				
				request.setAttribute("gatewayProfileData", gatewayProfileData);				
				return mapping.findForward(VIEW_FORWARD);
			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("gateway.profile.view.failure"));
	            saveErrors(request, messages);

	            ActionMessages errorHeadingMessage = new ActionMessages();
	            ActionMessage message = new ActionMessage("gateway.profile.error.heading","viewing");
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
            message = new ActionMessage("gateway.profile.error.heading","viewing");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage); 				
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	
	}

	
	private void setUsageMetringStandard(GatewayProfileData gatewayProfileData){
			UMStandard umStandard = UMStandard.fromValue(gatewayProfileData.getDiameterProfileData().getUmStandard());
		if(umStandard==null){
			umStandard = UMStandard.TGPPR9;
			Logger.logWarn(MODULE, "Considering USAGE METERING STANDARD as " + umStandard.displayValue + " for gateway profile: "+ gatewayProfileData.getProfileName() +". Reason: Invalid value Configured");
		}
		gatewayProfileData.getDiameterProfileData().setUmStandard(umStandard.displayValue);
	}
}
