package com.elitecore.netvertexsm.web.gateway.profile;

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
import com.elitecore.corenetvertex.constants.SupportedStandard;
import com.elitecore.netvertexsm.blmanager.gateway.GatewayBLManager;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.DiameterProfileData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfileData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfilePacketMapData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfileRuleMappingData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.GroovyScriptData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.PCCRuleMappingData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.RadiusProfileData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.netvertexsm.web.gateway.profile.form.CreateProfileForm;

public class CreateProfileAction extends BaseWebAction {
	private static final String SUCCESS_FORWARD = "success";	
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_GATEWAY_PROFILE;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		
		if((checkAccess(request, ACTION_ALIAS))){
			Logger.logTrace(MODULE,"Enter the execute method of :"+getClass().getName());
			try{
				CreateProfileForm createProfileForm = (CreateProfileForm) form;				
				GatewayBLManager gatewayBLManager = new GatewayBLManager();
				GatewayProfileData gatewayProfileData = new GatewayProfileData();
		    		   		
				gatewayProfileData.setProfileName(createProfileForm.getGatewayProfileName());
				gatewayProfileData.setGatewayType(createProfileForm.getGatewayType());
				gatewayProfileData.setCommProtocolId(createProfileForm.getCommProtocol());
				gatewayProfileData.setVedorId(createProfileForm.getVendorId());
				gatewayProfileData.setFirmware(createProfileForm.getFirmware());
				gatewayProfileData.setMaxThroughput(createProfileForm.getMaxThroughtput());
				gatewayProfileData.setBufferBW(createProfileForm.getBufferBandwidth());
				gatewayProfileData.setMaxIPCANSession(createProfileForm.getMaxIPCANSession());
				gatewayProfileData.setUsageReportingTime(createProfileForm.getUsageReportingTime());
				gatewayProfileData.setDescription(createProfileForm.getDescription());
				gatewayProfileData.setRevalidationMode(createProfileForm.getRevalidationMode());
								
				if(createProfileForm.getSupportedStandard() != 0)
					gatewayProfileData.setSupportedStandard(createProfileForm.getSupportedStandard());
				else
					gatewayProfileData.setSupportedStandard(SupportedStandard.RELEASE_9.getId());
				
			   if(createProfileForm.getCommProtocol().equalsIgnoreCase(CommunicationProtocol.RADIUS.id)) {
			    	RadiusProfileData radiusProfileData = new RadiusProfileData();
			    	radiusProfileData.setTimeout(createProfileForm.getTimeout());
			    	radiusProfileData.setMaxRequestTimeout(createProfileForm.getMaxRequestTimeout());
			    	radiusProfileData.setRetryCount(createProfileForm.getRetryCount());
			    	radiusProfileData.setStatusCheckDuration(createProfileForm.getStatusCheckDuration());
			    	radiusProfileData.setIcmpPingEnabled(createProfileForm.getIcmpPingEnabled());
			    	radiusProfileData.setSupportedVendorList(createProfileForm.getSupportedVendorList());
			    	radiusProfileData.setSendAccountingResponse(createProfileForm.getSendAccountingResponse());
				    radiusProfileData.setInterimInterval(createProfileForm.getInterimInterval());
//			    	Req-Res Mapping
			    	List<PCCRuleMappingData> pccRuleMappingList = getPCCRuleMappingList(request);
					radiusProfileData.setPccRuleMappingDataList(pccRuleMappingList);
			    	radiusProfileData.setGwProfilePacketMapList(getPacketMapping(request));
			    	radiusProfileData.setGroovyScriptsList(getGroovyScriptsList(request));
			    	radiusProfileData.setGatewayProfileRuleMappingList(getProfileRuleMappingList(request));
			    	gatewayProfileData.setRadiusProfileData(radiusProfileData);
			    	radiusProfileData.setGatewayProfileData(gatewayProfileData);
			    	
			    	
			    }else if(createProfileForm.getCommProtocol().equalsIgnoreCase(CommunicationProtocol.DIAMETER.id)) {
			    	DiameterProfileData diameterProfileData = new DiameterProfileData();
			    	diameterProfileData.setPccProvision(createProfileForm.getPccProvision());
					diameterProfileData.setRetransmissionCnt(createProfileForm.getRetransmissionCnt());
					diameterProfileData.setTimeout(createProfileForm.getTimeout());
					//diameterProfileData.setInitConnection(createProfileForm.getInitConnection());
					diameterProfileData.setDwInterval(createProfileForm.getDwrInterval());
					diameterProfileData.setIsDWGatewayLevel(createProfileForm.getIsDWRGatewayLevel().toString());
					
					diameterProfileData.setGxApplicationId(createProfileForm.getGxApplicationId());
					diameterProfileData.setGyApplicationId(createProfileForm.getGyApplicationId());
					diameterProfileData.setRxApplicationId(createProfileForm.getRxApplicationId());
					diameterProfileData.setS9ApplicationId(createProfileForm.getS9ApplicationId());
					diameterProfileData.setSyApplicationId(createProfileForm.getSyApplicationId());					
					diameterProfileData.setDwInterval(createProfileForm.getDwrInterval());
					
					diameterProfileData.setIsCustomGxAppId(createProfileForm.getIsCustomGxAppId().toString());
					diameterProfileData.setIsCustomGyAppId(createProfileForm.getIsCustomGyAppId().toString());
					diameterProfileData.setIsCustomRxAppId(createProfileForm.getIsCustomRxAppId().toString());
					diameterProfileData.setIsCustomS9AppId(createProfileForm.getIsCustomS9AppId().toString());
					diameterProfileData.setIsCustomSyAppId(createProfileForm.getIsCustomSyAppId().toString());
					
					diameterProfileData.setIsDWGatewayLevel(createProfileForm.getIsDWRGatewayLevel().toString());
					diameterProfileData.setTlsEnable(createProfileForm.getTlsEnable());
					
			    	diameterProfileData.setSupportedVendorList(createProfileForm.getSupportedVendorList());
			    	diameterProfileData.setMultiChargingRuleEnabled(createProfileForm.getMultiChargingRuleEnabled());
			    	
			    	diameterProfileData.setSessionCleanUpCER(createProfileForm.getSessionCleanUpCER().toString());
			    	diameterProfileData.setSessionCleanUpDPR(createProfileForm.getSessionCleanUpDPR().toString());
			    	diameterProfileData.setCerAvps(createProfileForm.getCerAvps());
			    	diameterProfileData.setDprAvps(createProfileForm.getDprAvps());
			    	diameterProfileData.setTransportProtocol(createProfileForm.getTransportProtocol());
			    	diameterProfileData.setSendDPRCloseEvent(createProfileForm.getSendDPRCloseEvent());
			    	diameterProfileData.setDwrAvps(createProfileForm.getDwrAvps());
			    	diameterProfileData.setSocketSendBufferSize(createProfileForm.getSocketSendBufferSize());
			    	diameterProfileData.setSocketReceiveBufferSize(createProfileForm.getSocketReceiveBufferSize());
			    	diameterProfileData.setTcpNagleAlgorithm(createProfileForm.getTcpNagleAlgorithm());
			    	//diameterProfileData.setDwrDuration(createProfileForm.getDwrDuration());
			    	diameterProfileData.setInitConnectionDuration(createProfileForm.getInitConnectionDuration());
			    	if(createProfileForm.getSessionLookupKey()!=null){
			    		diameterProfileData.setSessionLookUpKey(createProfileForm.getSessionLookupKey().trim());
			    	}
			    	if(createProfileForm.getSupportedStandard()!=0){
			    		diameterProfileData.setSupportedStandard(createProfileForm.getSupportedStandard());
			    	} else{
			    		diameterProfileData.setSupportedStandard(SupportedStandard.RELEASE_9.getId());
			    	}
			    	diameterProfileData.setUmStandard(createProfileForm.getUmStandard().trim());
			    	List<PCCRuleMappingData> pccRuleMappingList = new ArrayList<PCCRuleMappingData>();
			    	
			    	if(createProfileForm.getSupportedStandard()!=4){
			    		pccRuleMappingList = getPCCRuleMappingList(request);
			    	}
			    	
			    	diameterProfileData.setGroovyScriptsList(getGroovyScriptsList(request));
			    	diameterProfileData.setPccRuleMappingList(pccRuleMappingList);
			    	diameterProfileData.setGwProfilePacketMapList(getPacketMapping(request));
			    	diameterProfileData.setGatewayProfileRuleMappingList(getProfileRuleMappingList(request));
			    	 
			    	gatewayProfileData.setDiameterProfileData(diameterProfileData);
			    	
			    	
			    }
			    IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			    gatewayBLManager.create(gatewayProfileData,staffData, ACTION_ALIAS);
			    
			    ActionMessage message = new ActionMessage("gateway.profile.create.success",createProfileForm.getGatewayProfileName());
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", message);
	            saveMessages(request,messages);
			    request.setAttribute("responseUrl","/initSearchProfile.do");
			    return mapping.findForward(SUCCESS_FORWARD);
			}catch (Exception e) {
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("gateway.profile.create.failure"));
	            saveErrors(request, messages);
	            
	            ActionMessages errorHeadingMessage = new ActionMessages();
	            ActionMessage message = new ActionMessage("gateway.profile.error.heading","creating");
	            errorHeadingMessage.add("errorHeading",message);
	            saveMessages(request,errorHeadingMessage); 	            
	            
	            return mapping.findForward(FAILURE_FORWARD);
			}
		}else{
            Logger.logWarn(MODULE, "No Access On this Operation.");
	        ActionMessage message = new ActionMessage("general.user.restricted");
            ActionMessages messages = new ActionMessages();
            messages.add("information", message);
            saveErrors(request, messages);
            
            ActionMessages errorHeadingMessage = new ActionMessages();
            message = new ActionMessage("gateway.profile.error.heading","creating");
            errorHeadingMessage.add("errorHeading", message);
            saveMessages(request, errorHeadingMessage);
            
            return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
	private List<GatewayProfilePacketMapData> getPacketMapping(HttpServletRequest request){
		String[] strReqPacketMapId = request.getParameterValues("reqPacketMapId");
		String[] strResPacketMapId = request.getParameterValues("resPacketMapId");
		String[] strReqCondition = request.getParameterValues("reqCondition");
		String[] strResCondition = request.getParameterValues("resCondition");

		List<GatewayProfilePacketMapData> gwProfilePMList = new ArrayList<GatewayProfilePacketMapData>();

		if(strReqPacketMapId != null && strReqPacketMapId.length > 0)
			for(int i=0; i<strReqPacketMapId.length; i++) {
				GatewayProfilePacketMapData packetMapData = new GatewayProfilePacketMapData();
				long packetMapId = Long.parseLong(strReqPacketMapId[i]);
				packetMapData.setPacketMapId(packetMapId);
				packetMapData.setCondition(strReqCondition[i]);
				packetMapData.setOrderNumber(i+1);
				gwProfilePMList.add(packetMapData);
			}

		if(strResPacketMapId != null && strResPacketMapId.length > 0)
			for(int i=0; i<strResPacketMapId.length; i++) {
				GatewayProfilePacketMapData packetMapData = new GatewayProfilePacketMapData();
				long packetMapId = Long.parseLong(strResPacketMapId[i]);
				packetMapData.setPacketMapId(packetMapId);
				packetMapData.setCondition(strResCondition[i]);
				packetMapData.setOrderNumber(i+1);
				gwProfilePMList.add(packetMapData);
			}
		return gwProfilePMList;
	}
	
	private List<GatewayProfileRuleMappingData> getProfileRuleMappingList(HttpServletRequest request){
		String[] strReqPacketMapId = request.getParameterValues("ruleMappingIds");
		String[] strReqCondition = request.getParameterValues("ruleMappingCondition");
		

		List<GatewayProfileRuleMappingData> gwProfilePMList = new ArrayList<GatewayProfileRuleMappingData>();

		if(strReqPacketMapId != null && strReqPacketMapId.length > 0)
			for(int i=0; i<strReqPacketMapId.length; i++) {
				GatewayProfileRuleMappingData packetMapData = new GatewayProfileRuleMappingData();
				long packetMapId = Long.parseLong(strReqPacketMapId[i]);
				packetMapData.setRuleMappingId(packetMapId);
				packetMapData.setAccessNetworkType(strReqCondition[i]);
				packetMapData.setOrderNumber(i+1);
				gwProfilePMList.add(packetMapData);
			}
		return gwProfilePMList;
	}
	
	
	private List<PCCRuleMappingData> getPCCRuleMappingList(HttpServletRequest request){
		List<PCCRuleMappingData> pccRuleMappingList = new ArrayList<PCCRuleMappingData>();
		//		Static PCCRule Mapping
		String[] attributeS = request.getParameterValues("attributeS");
		String[] policyKeyS = request.getParameterValues("policyKeyS");
		String[] defaultValueS = request.getParameterValues("defaultValueS");
		String[] valueMappingS = request.getParameterValues("valueMappingS");

		if(attributeS != null && attributeS.length > 0)
			for(int i=0; i<attributeS.length; i++) {
				if(attributeS[i]!=null && attributeS[i].trim().length()>0 && policyKeyS[i]!=null && policyKeyS[i].trim().length()>0){
					PCCRuleMappingData pccRuleMapping = new PCCRuleMappingData();
					pccRuleMapping.setAttribute(attributeS[i]);
					pccRuleMapping.setPolicyKey(policyKeyS[i]);
					pccRuleMapping.setDefaultValue(defaultValueS[i]);
					pccRuleMapping.setValueMapping(valueMappingS[i]);
					pccRuleMapping.setType("STATIC");
					pccRuleMappingList.add(pccRuleMapping);
				}
			}

		//	Dynamic PCCRule Mapping
		String[] attributeD = request.getParameterValues("attributeD");
		String[] policyKeyD = request.getParameterValues("policyKeyD");
		String[] defaultValueD = request.getParameterValues("defaultValueD");
		String[] valueMappingD = request.getParameterValues("valueMappingD");

		if(attributeD != null && attributeD.length > 0)
			for(int i=0; i<attributeD.length; i++) {
				if(attributeD[i]!=null && attributeD[i].trim().length()>0 && policyKeyD[i]!=null && policyKeyD[i].trim().length()>0){
					PCCRuleMappingData pccRuleMapping = new PCCRuleMappingData();
					pccRuleMapping.setAttribute(attributeD[i]);
					pccRuleMapping.setPolicyKey(policyKeyD[i]);
					pccRuleMapping.setDefaultValue(defaultValueD[i]);
					pccRuleMapping.setValueMapping(valueMappingD[i]);
					pccRuleMapping.setType("DYNAMIC");
					pccRuleMappingList.add(pccRuleMapping);
				}
			}
		return pccRuleMappingList;
	}
	private List<GroovyScriptData> getGroovyScriptsList(HttpServletRequest request){
		List<GroovyScriptData> groovyScriptList = new ArrayList<GroovyScriptData>();
	
		String[] scriptNames = request.getParameterValues("scriptName");
		String[] arguments = request.getParameterValues("argument");		

		if(scriptNames != null && scriptNames.length > 0)
			for(int i=0; i<scriptNames.length; i++) {
				if(scriptNames[i]!=null && scriptNames[i].trim().length()>0){
					GroovyScriptData groovyScriptData = new GroovyScriptData();
					groovyScriptData.setOrderNumber(i+1);
					groovyScriptData.setScriptName(scriptNames[i]);
					groovyScriptData.setArgument(arguments[i]);
					groovyScriptList.add(groovyScriptData);
				}
			}
		return groovyScriptList;
	}
	private String getBooleanValue(String value){
		if(value!=null && value.equalsIgnoreCase("on")){
			return "true";
		}
		return "false";
	}
}
