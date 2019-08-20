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

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.SupportedStandard;
import com.elitecore.corenetvertex.constants.UMStandard;
import com.elitecore.netvertexsm.blmanager.gateway.GatewayBLManager;
import com.elitecore.netvertexsm.blmanager.gateway.MappingBLManager;
import com.elitecore.netvertexsm.blmanager.gateway.pccrulemapping.RuleMappingBlManager;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.DiameterProfileData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfileData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfilePacketMapData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfileRuleMappingData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.GroovyScriptData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.PCCRuleMappingData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.netvertexsm.web.gateway.profile.form.EditGatewayProfileForm;

public class EditDiameterGatewayProfileFieldMap extends BaseWebAction {

	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_GATEWAY_PROFILE;
	private static final String MODULE = "EDIT_GATEWAY_PROFILE";
	private static final String FORWARD_PAGE = "editDiameterGatewayProfile";

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of"+getClass().getName());
		if(checkAccess(request, ACTION_ALIAS)){
			try{
				EditGatewayProfileForm gatewayProfileForm = (EditGatewayProfileForm) form;
				GatewayBLManager gatewayBLManager = new GatewayBLManager();
				MappingBLManager mappingBLManager = new MappingBLManager();
				RuleMappingBlManager ruleMappingBlManager=new RuleMappingBlManager();
				GatewayProfileData gatewayProfileData = gatewayBLManager.getGatewayProfileData(gatewayProfileForm.getProfileId());
				List<GroovyScriptData> groovyScriptsDataList = null;
				
				DiameterProfileData diameterProfileData = new DiameterProfileData();
				if("update".equals(gatewayProfileForm.getAction())){
					convertFormToBean(diameterProfileData,gatewayProfileForm);
					List<PCCRuleMappingData> pccRuleMappingList = new ArrayList<PCCRuleMappingData>();
					//List<GatewayProfileRuleMappingData> gatewayProfileRuleMappingList=new ArrayList<GatewayProfileRuleMappingData>();
					if(gatewayProfileForm.getSupportedStandard()!=SupportedStandard.CISCOSCE.getId()){
						diameterProfileData.setGatewayProfileRuleMappingList(getProfileRuleMappingList(request));
       //					gatewayProfileRuleMappingList=getProfileRuleMappingList(request);
					}
					diameterProfileData.setPccRuleMappingList(pccRuleMappingList);

					//	Req-Res Mapping
					String[] strReqPacketMapId = request.getParameterValues("reqPacketMapId");
					String[] strResPacketMapId = request.getParameterValues("resPacketMapId");
					String[] strReqCondition = request.getParameterValues("reqCondition");
					String[] strResCondition = request.getParameterValues("resCondition");
					//String[] strOrderNumber = request.getParameterValues("orderNumber");

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


					diameterProfileData.setGwProfilePacketMapList(gwProfilePMList);
					if(gatewayProfileForm.getSessionLookupKey()!=null){
						diameterProfileData.setSessionLookUpKey(gatewayProfileForm.getSessionLookupKey().trim());
					}
					List<GroovyScriptData> groovyScriptDataList = getGroovyScriptsList(request);
					diameterProfileData.setGroovyScriptsList(groovyScriptDataList);
					
					gatewayProfileData.setDiameterProfileData(diameterProfileData);
					//to test
					gatewayProfileData.setSupportedStandard(diameterProfileData.getSupportedStandard());
					IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
					gatewayBLManager.updateGatewayProfileFieldMap(gatewayProfileData,staffData,ACTION_ALIAS);

					ActionMessage message = new ActionMessage("gateway.profile.update.success",gatewayProfileForm.getGatewayProfileName());
					ActionMessages messages = new ActionMessages();
					messages.add("information", message);
					saveMessages(request,messages);
					request.setAttribute("responseUrl","/initSearchProfile.do");
					return mapping.findForward(SUCCESS_FORWARD);
				}else{
					convertBeanToForm(gatewayProfileData, gatewayProfileForm);
					
					//gatewayProfileForm.setPccRuleMapList(gatewayBLManager.getPCCRuleMapList(gatewayProfileForm.getProfileId()));
					List<GatewayProfilePacketMapData> diameterToPCRFprofilePacketMapList = gatewayBLManager.getDiameterToPCRFProfilePacketMapList(gatewayProfileData);
					List<GatewayProfilePacketMapData> pcrfToDiameterprofilePacketMapList=gatewayBLManager.getPCRFToDiameterProfilePacketMapList(gatewayProfileData);
					List<GatewayProfileRuleMappingData> gatewayProfileRuleMappingList=gatewayBLManager.getProfileRuleMappingList(gatewayProfileData);
				
					gatewayProfileForm.setProfilePacketDPMapList(diameterToPCRFprofilePacketMapList);
					gatewayProfileForm.setProfilePacketPDMapList(pcrfToDiameterprofilePacketMapList);
                  
					gatewayProfileForm.setPacketMappingList(mappingBLManager.getPacketMappingList("DIAMETER"));
					
					gatewayProfileForm.setPcrfToDiameterPacketMappingList(mappingBLManager.getPCRFToDiameterPacketMappingList());
					gatewayProfileForm.setDiameterToPCRFPacketMappingList(mappingBLManager.getDiameterToPCRFPacketMappingList());
					gatewayProfileForm.setGatewayProfileRuleMappingList(gatewayProfileRuleMappingList);
					gatewayProfileForm.setRuleMappingList(ruleMappingBlManager.getRuleMappingDataList());
					groovyScriptsDataList = gatewayBLManager.getGroovyScriptsDataList(gatewayProfileData.getProfileId());
					request.setAttribute("groovyScriptsDataList", groovyScriptsDataList);
					request.setAttribute("gatewayProfileData", gatewayProfileData);
					return mapping.findForward(FORWARD_PAGE);
				}
				
			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessages messages = new ActionMessages();
				messages.add("information",new ActionMessage("gateway.profile.update.failed"));
				saveErrors(request,messages);
				return mapping.findForward(FAILURE_FORWARD);
			}
		}else{
			Logger.logWarn(MODULE, "No Access on this Operation ");
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
	
	private void convertBeanToForm(GatewayProfileData gatewayData, EditGatewayProfileForm form ){
		DiameterProfileData diameterProfileData = gatewayData.getDiameterProfileData();
		if(diameterProfileData!=null){
			form.setGatewayProfileName(gatewayData.getProfileName());
			form.setTimeout(diameterProfileData.getTimeout());
			//form.setInitConnection(diameterProfileData.getInitConnection());
			if(diameterProfileData.getDwInterval() != null)
				form.setDwrInterval(diameterProfileData.getDwInterval());
			form.setIsDWRGatewayLevel(Boolean.valueOf(diameterProfileData.getIsDWGatewayLevel()));					
			form.setPccProvision(diameterProfileData.getPccProvision());
			form.setIsCustomGxAppId(Boolean.valueOf(diameterProfileData.getIsCustomGxAppId()));
			form.setGxApplicationId(diameterProfileData.getGxApplicationId());
			form.setIsCustomGyAppId(Boolean.valueOf(diameterProfileData.getIsCustomGyAppId()));
			form.setGyApplicationId(diameterProfileData.getGyApplicationId());
			form.setIsCustomRxAppId(Boolean.valueOf(diameterProfileData.getIsCustomRxAppId()));
			form.setRxApplicationId(diameterProfileData.getRxApplicationId());
			form.setIsCustomS9AppId(Boolean.valueOf(diameterProfileData.getIsCustomS9AppId()));
			form.setS9ApplicationId(diameterProfileData.getS9ApplicationId());
			form.setIsCustomSyAppId(Boolean.valueOf(diameterProfileData.getIsCustomSyAppId()));			
			if(Strings.isNullOrBlank(diameterProfileData.getSyApplicationId())){
				form.setSyApplicationId("10415:16777302");
			}else{			
				form.setSyApplicationId(diameterProfileData.getSyApplicationId());
			}
			form.setTlsEnable(diameterProfileData.getTlsEnable());
			form.setRetransmissionCnt(diameterProfileData.getRetransmissionCnt());
			
			form.setSupportedStandard(diameterProfileData.getSupportedStandard());

			setUmStandard(gatewayData, form, diameterProfileData);
			form.setDiameterSupportedVendorList(diameterProfileData.getSupportedVendorList());
			form.setMultiChargingRuleEnabled(diameterProfileData.getMultiChargingRuleEnabled());
			form.setSessionCleanUpCER(Boolean.valueOf(diameterProfileData.getSessionCleanUpCER()));
			form.setSessionCleanUpDPR(Boolean.valueOf(diameterProfileData.getSessionCleanUpDPR()));
			form.setCerAvps(diameterProfileData.getCerAvps());
			form.setDprAvps(diameterProfileData.getDprAvps());
			form.setTransportProtocol(diameterProfileData.getTransportProtocol());
			form.setSocketReceiveBufferSize(diameterProfileData.getSocketReceiveBufferSize());
			form.setSendDPRCloseEvent(diameterProfileData.getSendDPRCloseEvent());
			form.setDwrAvps(diameterProfileData.getDwrAvps());
			form.setSocketSendBufferSize(diameterProfileData.getSocketSendBufferSize());
			form.setSocketReceiveBufferSize(diameterProfileData.getSocketReceiveBufferSize());
			form.setTcpNagleAlgorithm(diameterProfileData.getTcpNagleAlgorithm());
			//form.setDwrDuration(diameterProfileData.getDwrDuration());
			form.setInitConnectionDuration(diameterProfileData.getInitConnectionDuration());
			if(diameterProfileData.getSessionLookUpKey()!=null){
				form.setSessionLookupKey(diameterProfileData.getSessionLookUpKey().trim());
			}
		}
	}

	private void setUmStandard(GatewayProfileData gatewayData,
			EditGatewayProfileForm form, DiameterProfileData diameterProfileData) {
		UMStandard umStandard = UMStandard.fromValue(diameterProfileData.getUmStandard());
		if(umStandard==null){
			umStandard = UMStandard.TGPPR9;
			Logger.logWarn(MODULE, "Considering USAGE METERING STANDARD as " + umStandard.displayValue + " for gateway profile: "+ gatewayData.getProfileName() +". Reason: Invalid value Configured");
		}
		form.setUmStandard(String.valueOf(umStandard.value));
	}
	
	private void convertFormToBean(DiameterProfileData diameterProfileData, EditGatewayProfileForm gatewayProfileForm ){
		diameterProfileData.setRetransmissionCnt(gatewayProfileForm.getRetransmissionCnt());
		diameterProfileData.setTimeout(gatewayProfileForm.getTimeout());
		//diameterProfileData.setInitConnection(gatewayProfileForm.getInitConnection());
		
		diameterProfileData.setGxApplicationId(gatewayProfileForm.getGxApplicationId());
		diameterProfileData.setGyApplicationId(gatewayProfileForm.getGyApplicationId());
		diameterProfileData.setRxApplicationId(gatewayProfileForm.getRxApplicationId());
		diameterProfileData.setS9ApplicationId(gatewayProfileForm.getS9ApplicationId());
		diameterProfileData.setSyApplicationId(gatewayProfileForm.getSyApplicationId());
		diameterProfileData.setDwInterval(gatewayProfileForm.getDwrInterval());
		
		diameterProfileData.setIsCustomGxAppId(gatewayProfileForm.getIsCustomGxAppId().toString());
		diameterProfileData.setIsCustomGyAppId(gatewayProfileForm.getIsCustomGyAppId().toString());
		diameterProfileData.setIsCustomRxAppId(gatewayProfileForm.getIsCustomRxAppId().toString());
		diameterProfileData.setIsCustomS9AppId(gatewayProfileForm.getIsCustomS9AppId().toString());
		diameterProfileData.setIsCustomSyAppId(gatewayProfileForm.getIsCustomSyAppId().toString());
		
		diameterProfileData.setIsDWGatewayLevel(gatewayProfileForm.getIsDWRGatewayLevel().toString());
		diameterProfileData.setPccProvision(gatewayProfileForm.getPccProvision());
		
		diameterProfileData.setTlsEnable(gatewayProfileForm.getTlsEnable());
		diameterProfileData.setMultiChargingRuleEnabled(gatewayProfileForm.getMultiChargingRuleEnabled());
		diameterProfileData.setSupportedStandard(gatewayProfileForm.getSupportedStandard()!=0 ? gatewayProfileForm.getSupportedStandard() : SupportedStandard.RELEASE_9.getId());
		diameterProfileData.setUmStandard(gatewayProfileForm.getUmStandard());
		diameterProfileData.setSupportedVendorList(gatewayProfileForm.getDiameterSupportedVendorList());
		
		diameterProfileData.setSessionCleanUpCER(gatewayProfileForm.getSessionCleanUpCER().toString());
		diameterProfileData.setSessionCleanUpDPR(gatewayProfileForm.getSessionCleanUpDPR().toString());
		diameterProfileData.setCerAvps(gatewayProfileForm.getCerAvps());
		diameterProfileData.setDprAvps(gatewayProfileForm.getDprAvps());
		diameterProfileData.setTransportProtocol(gatewayProfileForm.getTransportProtocol());
		diameterProfileData.setSocketReceiveBufferSize(gatewayProfileForm.getSocketReceiveBufferSize());
		diameterProfileData.setSendDPRCloseEvent(gatewayProfileForm.getSendDPRCloseEvent());
		diameterProfileData.setDwrAvps(gatewayProfileForm.getDwrAvps());
		diameterProfileData.setSocketSendBufferSize(gatewayProfileForm.getSocketSendBufferSize());
		diameterProfileData.setSocketReceiveBufferSize(gatewayProfileForm.getSocketReceiveBufferSize());
		diameterProfileData.setTcpNagleAlgorithm(gatewayProfileForm.getTcpNagleAlgorithm());
		//diameterProfileData.setDwrDuration(gatewayProfileForm.getDwrDuration());
		diameterProfileData.setInitConnectionDuration(gatewayProfileForm.getInitConnectionDuration());
		if(gatewayProfileForm.getSessionLookupKey()!=null){
			diameterProfileData.setSessionLookUpKey(gatewayProfileForm.getSessionLookupKey().trim());
		}
	}
	private List<GroovyScriptData> getGroovyScriptsList(HttpServletRequest request){
		Logger.logInfo(MODULE, " Method : getGroovyScriptsList(HttpServletRequest request)");
		List<GroovyScriptData> groovyScriptList = new ArrayList<GroovyScriptData>();
		String[] orderNumbers = request.getParameterValues("orderNumber");
		String[] scriptNames = request.getParameterValues("scriptName");
		String[] arguments = request.getParameterValues("argument");		

		if(scriptNames != null && scriptNames.length > 0)
			for(int i=0; i<scriptNames.length; i++) {
				if(scriptNames[i]!=null && scriptNames[i].trim().length()>0 && orderNumbers[i]!=null){
					GroovyScriptData groovyScriptData = new GroovyScriptData();
					groovyScriptData.setOrderNumber(i+1);
					groovyScriptData.setScriptName(scriptNames[i]);
					groovyScriptData.setArgument(arguments[i]);
					groovyScriptList.add(groovyScriptData);
				}
			}
		return groovyScriptList;
	}	
	
	private List<GatewayProfileRuleMappingData> getProfileRuleMappingList(HttpServletRequest request){
		String[] strReqPacketMapId = request.getParameterValues("ruleMappingId");
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
}