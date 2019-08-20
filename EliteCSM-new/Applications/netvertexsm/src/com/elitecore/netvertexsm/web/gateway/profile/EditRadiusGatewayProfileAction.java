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

import com.elitecore.netvertexsm.blmanager.gateway.GatewayBLManager;
import com.elitecore.netvertexsm.blmanager.gateway.MappingBLManager;
import com.elitecore.netvertexsm.blmanager.gateway.pccrulemapping.RuleMappingBlManager;
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
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
import com.elitecore.netvertexsm.web.gateway.profile.form.EditGatewayProfileForm;

public class EditRadiusGatewayProfileAction extends BaseWebAction {
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_GATEWAY_PROFILE;
	private static final String FORWARD_PAGE = "editRadiusGatewayProfile";
	
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response){
		try{
			Logger.logInfo(ACTION_ALIAS, "Enter execute method of:"+getClass().getName());
			checkAccess(request, ACTION_ALIAS);
			EditGatewayProfileForm editGatewayProfileForm = (EditGatewayProfileForm) form;
			GatewayBLManager gatewayBLManager = new GatewayBLManager();
			
			GatewayProfileData gatewayProfileData = gatewayBLManager.getGatewayProfileData(editGatewayProfileForm.getProfileId());
			RadiusProfileData radiusProfileData = gatewayProfileData.getRadiusProfileData();
			List<GatewayProfilePacketMapData> diameterPacketMapList = null;
			List<PCCRuleMappingData> pccRuleMappingDataList = null;
			List<GroovyScriptData> groovyScriptsDataList = null;
			List<GatewayProfileRuleMappingData> gatewayProfileRuleMappingList=null;
			MappingBLManager mappingBLManager = new MappingBLManager();
			RuleMappingBlManager ruleMappingBlManager=new RuleMappingBlManager();
			
			if("update".equals(editGatewayProfileForm.getAction())){
				gatewayProfileData.setProfileId(editGatewayProfileForm.getProfileId());
				radiusProfileData.setProfileId(editGatewayProfileForm.getProfileId());
				radiusProfileData.setTimeout(editGatewayProfileForm.getTimeout());
				radiusProfileData.setMaxRequestTimeout(editGatewayProfileForm.getMaxRequestTimeout());
				radiusProfileData.setRetryCount(editGatewayProfileForm.getRetryCount());
				radiusProfileData.setIcmpPingEnabled(editGatewayProfileForm.getIcmpPingEnabled());
				radiusProfileData.setStatusCheckDuration(editGatewayProfileForm.getStatusCheckDuration());
				radiusProfileData.setInterimInterval(editGatewayProfileForm.getInterimInterval());
				radiusProfileData.setSupportedVendorList(editGatewayProfileForm.getRadiusSupportedVendorList());
				radiusProfileData.setSendAccountingResponse(editGatewayProfileForm.getSendAccountingResponse());
				
				List<PCCRuleMappingData> pccRuleMappingList = getPCCRuleMappingList(request);
				radiusProfileData.setPccRuleMappingDataList(pccRuleMappingList);
				List<GroovyScriptData> groovyScriptDataList = getGroovyScriptsList(request);
				radiusProfileData.setGroovyScriptsList(groovyScriptDataList);
		    	radiusProfileData.setGwProfilePacketMapList(getPacketMapping(request));
		    	radiusProfileData.setGatewayProfileRuleMappingList(getProfileRuleMappingList(request));
		    	gatewayProfileData.setRadiusProfileData(radiusProfileData);
			    IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				gatewayBLManager.updateRadiusGatewayProfile(gatewayProfileData, staffData, ACTION_ALIAS);
				    
				ActionMessage message = new ActionMessage("gateway.profile.update.success",editGatewayProfileForm.getGatewayProfileName());
		        ActionMessages messages = new ActionMessages();
		        messages.add("information", message);
		        saveMessages(request,messages);
				request.setAttribute("responseUrl","/initSearchProfile.do");
				return mapping.findForward(SUCCESS);
			}else{
				/*set data to form*/
				editGatewayProfileForm.setGatewayProfileName(gatewayProfileData.getProfileName());
				editGatewayProfileForm.setTimeout(radiusProfileData.getTimeout());
				editGatewayProfileForm.setMaxRequestTimeout(radiusProfileData.getMaxRequestTimeout());
				editGatewayProfileForm.setRetryCount(radiusProfileData.getRetryCount());
				editGatewayProfileForm.setIcmpPingEnabled(radiusProfileData.getIcmpPingEnabled());
				editGatewayProfileForm.setStatusCheckDuration(radiusProfileData.getStatusCheckDuration());
				editGatewayProfileForm.setInterimInterval(radiusProfileData.getInterimInterval());
				editGatewayProfileForm.setRadiusSupportedVendorList(radiusProfileData.getSupportedVendorList());
				editGatewayProfileForm.setSendAccountingResponse(radiusProfileData.getSendAccountingResponse());
				editGatewayProfileForm.setPcrfToRadiusPacketMappingList(mappingBLManager.getPCRFToRadiusPacketMappingList());
				editGatewayProfileForm.setRadiusToPCRFPacketMappingList(mappingBLManager.getRadiusToPCRFPacketMappingList());
				editGatewayProfileForm.setRuleMappingList(ruleMappingBlManager.getRuleMappingDataList());
				editGatewayProfileForm.setGatewayProfileRuleMappingList(gatewayBLManager.getProfileRuleMappingList(gatewayProfileData));
				
				diameterPacketMapList = gatewayBLManager.getProfilePacketMapList(gatewayProfileData.getProfileId());
				//pccRuleMappingDataList = gatewayBLManager.getPCCRuleMapList(gatewayProfileData.getProfileId());
				groovyScriptsDataList = gatewayBLManager.getGroovyScriptsDataList(gatewayProfileData.getProfileId());
				gatewayProfileRuleMappingList=gatewayBLManager.getProfileRuleMappingList(gatewayProfileData);
				request.setAttribute("diameterPacketMapList", diameterPacketMapList);
				//request.setAttribute("pccRuleMapList", pccRuleMappingDataList);
				request.setAttribute("gatewayProfileData", gatewayProfileData);
				request.setAttribute("groovyScriptsDataList", groovyScriptsDataList);
				request.setAttribute("gatewayProfileRuleMappingList", gatewayProfileRuleMappingList);
				return mapping.findForward(FORWARD_PAGE);
			}
		}catch (ActionNotPermitedException e) {
			Logger.logWarn(MODULE, "No Access on this Operation ");
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}catch(Exception e){
			Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
			Logger.logTrace(MODULE,e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessages messages = new ActionMessages();
			messages.add("information",new ActionMessage("gateway.profile.update.failed"));
			saveErrors(request,messages);
		}
		return mapping.findForward(FAILURE);
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
