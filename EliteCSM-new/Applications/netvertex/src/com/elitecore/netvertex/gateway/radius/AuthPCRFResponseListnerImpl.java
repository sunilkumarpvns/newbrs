package com.elitecore.netvertex.gateway.radius;


import com.elitecore.commons.logging.LogLevel;
import com.elitecore.corenetvertex.constants.PCRFEvent;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.netvertex.gateway.radius.mapping.PCCToRadiusMapping;
import com.elitecore.netvertex.core.util.PCRFPacketUtil;
import com.elitecore.netvertex.gateway.GatewayEventListener;
import com.elitecore.netvertex.gateway.radius.AsyncRequestResponseCache.RequestResponseWrapper;
import com.elitecore.netvertex.gateway.radius.RadiusGatewayController.RadiusGatewayEventListner;
import com.elitecore.netvertex.gateway.radius.mapping.PCCtoRadiusMappingValueProvider;
import com.elitecore.netvertex.gateway.radius.packet.RadServiceRequest;
import com.elitecore.netvertex.gateway.radius.packet.RadServiceResponse;
import com.elitecore.netvertex.gateway.radius.scripts.RadiusGroovyScript;
import com.elitecore.netvertex.gateway.radius.snmp.auth.AuthServerCounters;
import com.elitecore.netvertex.gateway.radius.utility.AvpAccumalators;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.PCRFResponseListner;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;

import java.util.EnumSet;
import java.util.List;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class AuthPCRFResponseListnerImpl implements PCRFResponseListner{

	private static final String MODULE = "RAD-AUTH-RSPL";
	
	private final RadiusGatewayControllerContext context;
	private final AsyncRequestResponseCache requestReaponseCache;
	private final RadiusGatewayEventListner radiusGatewayEventListner;
	private final AuthServerCounters authServCounters;
	private GatewayEventListener gatewayEventListener;

	public AuthPCRFResponseListnerImpl(RadiusGatewayControllerContext context,
			RadiusGatewayEventListner radiusGatewayEventListner ,
			GatewayEventListener gatewayEventListener,
			AsyncRequestResponseCache requestReaponseCache, AuthServerCounters authServCounters){
		this.context = context;
		this.radiusGatewayEventListner = radiusGatewayEventListner;
		this.gatewayEventListener = gatewayEventListener;
		this.requestReaponseCache = requestReaponseCache;
		this.authServCounters = authServCounters;
	}


	@Override
	public final void responseReceived(PCRFResponse response) {


		/*
		 * on response received, if sy communication is happen then we should send the STR request to sy gateway.
		 * In this case we need to check if successful syCommunication is happen or not.
		 * 
		 * In case of Timeout,unknown session id, syCommunicationFail and no alive gateway found no sy session is created
		 * so no need to send STR
		 */
		String resultCode = response.getAttribute(PCRFKeyConstants.RESULT_CODE.val);
		if(resultCode != null &&  resultCode.equalsIgnoreCase(PCRFKeyValueConstants.RESULT_CODE_SUCCESS.val) == false) {
			String syCommunication = response.getAttribute(PCRFKeyConstants.SY_COMMUNICATION.val);
			if(syCommunication != null &&
					(syCommunication.equalsIgnoreCase(PCRFKeyValueConstants.SY_COMMUNICATION_TIMEOUT.val) 
						|| syCommunication.equalsIgnoreCase(PCRFKeyValueConstants.RESULT_CODE_UNKNOWN_SESSION_ID.val)
						|| syCommunication.equalsIgnoreCase(PCRFKeyValueConstants.SY_COMMUNICATION_FAIL.val)
				    ) == false
			)
				 {
				
				if(getLogger().isDebugLogLevel())
					getLogger().debug(MODULE, "Initiating procedure to terminate sy session");
				

				PCRFRequest pcrfRequest = new PCRFRequestImpl();
				PCRFPacketUtil.buildPCRFRequest(response, pcrfRequest);
				pcrfRequest.setPCRFEvents(EnumSet.of(PCRFEvent.SESSION_STOP));
				if(getLogger().isDebugLogLevel())
					getLogger().debug(MODULE, "PCRF Request created from PCRFResponse" + pcrfRequest);
				gatewayEventListener.eventReceived(pcrfRequest);
			}
		}
		
		
		RadiusGateway gateway = selectGateway(response);
		
		if(gateway == null){
			getLogger().error(MODULE, "Sending Radius Access Response skipped. Reason: gateway configuration not found " +
					"for Address: " + response.getAttribute(PCRFKeyConstants.CS_SOURCE_GATEWAY.getVal()));
		
			return;
		}
		
		applyScripts(response, gateway);
		
		if(response.isMarkedForDropRequest()){
			if(getLogger().isInfoLogLevel())
				getLogger().info(MODULE, "Access Response sending skipped. Reason: " +
						"PCRF Response dropped");
				
			authServCounters.incPackDropCntr(gateway.getIPAddress());
			return;
		}
		
		if(getLogger().isDebugLogLevel())
			getLogger().debug(MODULE, "PCRF Response: " + response);

		RequestResponseWrapper requestResponseWrapper = requestReaponseCache.getRequestResponse(response.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.getVal()));
		if(requestResponseWrapper == null){
			if(getLogger().isDebugLogLevel()){
				getLogger().debug(MODULE, "Sending Radius Access Response skipped. Reason: No Radius Response " +
						"found for CS.CoreSessionID: " + response.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.getVal()));
			}
			authServCounters.incDupReqCntr(gateway.getIPAddress());
			return;
		}

		RadServiceRequest radServiceRequest = requestResponseWrapper.getRequest();
		RadServiceResponse radServiceResponse = requestResponseWrapper.getResponse();
		
		
		if(PCRFKeyValueConstants.RESULT_CODE_SUCCESS.val.equals(response.getAttribute(PCRFKeyConstants.RESULT_CODE.getVal()))){
			RadiusPacket radiusPacket = new RadiusPacket();
			if(applyMapping(response, gateway, radiusPacket) == false) {
				if(getLogger().isErrorLogLevel()){
					getLogger().error(MODULE, "Sending Access Reject. Reason: No mapping found for PCRF response " +
							"in gateway: " + gateway.getIPAddress());
				}
			}else {
				applyScripts(response, radiusPacket, gateway);
				
				if(response.isMarkedForDropRequest()){
					if(getLogger().isLogLevel(LogLevel.INFO))
						getLogger().info(MODULE, "Access Response sending skipped. Reason: " +
								"PCRF Response dropped");
					
					authServCounters.incPackDropCntr(gateway.getIPAddress());
					return;
				}
				
				for(IRadiusAttribute attribute : radiusPacket.getRadiusAttributes()){
					radServiceResponse.addAttribute(attribute);
				}
				radServiceResponse.setPacketType(radiusPacket.getPacketType());
				radServiceResponse.setResponseMessage("Authentication Success");
			}

		}else {
			if(getLogger().isInfoLogLevel()){
				getLogger().info(MODULE, "Sending Access Reject. Reason: PCRF " +
						"Result Code = "+ response.getAttribute(PCRFKeyConstants.RESULT_CODE.getVal()));
			}

			String authFailedReason = response.getAttribute(PCRFKeyConstants.AUTH_FAILED_REASON.getVal());
			if( authFailedReason != null ) {
				radServiceResponse.setResponseMessage(response.getAttribute(PCRFKeyConstants.AUTH_FAILED_REASON.getVal()));
			}else {
				radServiceResponse.setResponseMessage(response.getAttribute(PCRFKeyConstants.RESULT_CODE.getVal()));
			}
		}

		if(getLogger().isInfoLogLevel())
			getLogger().info(MODULE, radServiceResponse.toString());

		radiusGatewayEventListner.submitAsyncRequest(radServiceRequest, radServiceResponse);
	}

	private RadiusGateway selectGateway(PCRFResponse response) {
		RadiusGateway gateway = context.getRadiusGateway(response.getAttribute(PCRFKeyConstants.CS_GATEWAY_ADDRESS.getVal()));
		if(gateway == null) {
			if(getLogger().isLogLevel(LogLevel.DEBUG)){
				getLogger().debug(MODULE, "Gateway configuration not found for Address: " + 
						response.getAttribute(PCRFKeyConstants.CS_GATEWAY_ADDRESS.getVal()));
			}
			gateway = context.getRadiusGateway(response.getAttribute(PCRFKeyConstants.CS_SOURCE_GATEWAY.getVal()));
		}
		return gateway;
	}

	private boolean applyMapping(PCRFResponse response,
								 RadiusGateway gateway,
								 RadiusPacket radiusPacket) {
		boolean isApplied = false;
		PCCToRadiusMapping aaMappings = gateway.getConfiguration().getAAMappings();
		if(aaMappings!=null){
			if (aaMappings.apply(new PCCtoRadiusMappingValueProvider(response, radiusPacket, gateway.getConfiguration(), context),
					AvpAccumalators.of(radiusPacket))) {
				isApplied = true;
			}
		}
		return isApplied;
	}

	private void applyScripts(PCRFResponse response, RadiusPacket radiusPacket,
			RadiusGateway gateway) {
		List<RadiusGroovyScript> scripts = context.getRadiusGroovyScripts(gateway.getIPAddress());
		if(scripts != null && scripts.isEmpty() == false){
			for(RadiusGroovyScript script : scripts){
				try{
					script.preSend(response, radiusPacket);
				}catch(Exception ex){
					getLogger().error(MODULE, "Error in executing script \""+script.getName()+"\" for gateway = " + gateway.getIPAddress() +". Reason: "+ ex.getMessage());
					getLogger().trace(MODULE, ex);
				}
				
			}
		}
	}


	private void applyScripts(PCRFResponse response,
			RadiusGateway gateway) {
		List<RadiusGroovyScript> scripts = context.getRadiusGroovyScripts(gateway.getIPAddress());
		if(scripts != null && scripts.isEmpty() == false){
			for(RadiusGroovyScript script : scripts){
				try{
					script.preSend(response);
				}catch(Exception ex){
					getLogger().error(MODULE, "Error in executing script \""+script.getName()+"\" for gateway = " + gateway.getIPAddress() +". Reason: "+ ex.getMessage());
					getLogger().trace(MODULE, ex);
				}
				
			}
		}
	}
}