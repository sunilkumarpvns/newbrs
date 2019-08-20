package com.elitecore.netvertex.gateway.diameter.application.handler.cisco;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.sessionx.Criteria;
import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.core.serverx.sessionx.SessionException;
import com.elitecore.core.serverx.sessionx.criterion.Restrictions;
import com.elitecore.corenetvertex.constants.*;
import com.elitecore.diameterapi.core.common.session.Session;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.PCRFRequestMappingValueProvider;
import com.elitecore.netvertex.core.util.PCRFPacketUtil;
import com.elitecore.netvertex.gateway.GatewayEventListener;
import com.elitecore.netvertex.gateway.diameter.DiameterGatewayControllerContext;
import com.elitecore.netvertex.gateway.diameter.application.handler.ApplicationHandler;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterGatewayConfiguration;
import com.elitecore.netvertex.gateway.diameter.scripts.DiameterGroovyScript;
import com.elitecore.netvertex.gateway.diameter.utility.CiscoPackageMapping;
import com.elitecore.netvertex.service.pcrf.PCCRuleExpiryListener;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.PCRFResponseListner;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;

import java.util.HashSet;
import java.util.List;

public class CiscoGxAppHandler implements ApplicationHandler {
	
	private static final String MODULE = "CISCO-GX-APP-HDLR";
	private DiameterGatewayControllerContext context;
	private GatewayEventListener eventListener;
	private PCCRuleExpiryListener pccRuleExpiryListener;
	private PCRFResponseListner rarPCRFResponseListener;
	
	public CiscoGxAppHandler(DiameterGatewayControllerContext context, GatewayEventListener eventListener) {
		this.context = context;
		this.eventListener = eventListener;
		rarPCRFResponseListener = new ReAuthPCRFResponseListenerImpl();
		pccRuleExpiryListener = new PCCRuleExpiryListenerImpl();
	}

	@Override
	public void handleReceivedRequest(Session session, DiameterRequest diameterRequest) {


        DiameterGatewayConfiguration configuration = context.getGatewayConfiguration(diameterRequest.getInfoAVPValue(DiameterAVPConstants.EC_ORIGINATOR_PEER_NAME),
                diameterRequest.getInfoAVPValue(DiameterAVPConstants.EC_PROXY_AGENT_NAME));

        if (configuration == null) {
            configuration = context.getGatewayConfigurationByHostId(diameterRequest.getAVPValue(DiameterAVPConstants.ORIGIN_HOST));
        }
        PCRFRequest pcrfRequest = new PCRFRequestImpl();
        configuration.getGxCCRMappings().apply(new PCRFRequestMappingValueProvider(diameterRequest, pcrfRequest, configuration));

        if (pcrfRequest.getAttribute(PCRFKeyConstants.CS_SESSION_IPV4.getVal()) == null) {
            if (LogManager.getLogger().isLogLevel(LogLevel.ERROR))
                LogManager.getLogger().error(MODULE, "Skipping further processing for Cisco Gx request (Session Id " + diameterRequest.getAVPValue(DiameterAVPConstants.SESSION_ID)
                        + "). Reason: SessionIP not found from PCRF request");
            return;
        }

        if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
            LogManager.getLogger().info(MODULE, "Locating Core Session for SessionIP = " + pcrfRequest.getAttribute(PCRFKeyConstants.CS_SESSION_IPV4.getVal()));

        addSessionAttributesInToPCRFRequest(diameterRequest, pcrfRequest);


        pcrfRequest.setAttribute(PCRFKeyConstants.CS_SESSION_TYPE.getVal(), SessionTypeConstant.CISCO_GX.getVal());
        pcrfRequest.setAttribute(PCRFKeyConstants.GATEWAY_TYPE.getVal(), GatewayTypeConstant.DIAMETER.getVal());
        pcrfRequest.setAttribute(PCRFKeyConstants.CS_CORESESSION_ID.getVal(), pcrfRequest.getAttribute(PCRFKeyConstants.CS_SESSION_ID.getVal()) + ":" + SessionTypeConstant.CISCO_GX.getVal());


        List<DiameterGroovyScript> scripts = context.getDiameterGroovyScripts(configuration.getName());
        if (scripts != null && !scripts.isEmpty()) {
            for (DiameterGroovyScript script : scripts) {
                try {
                    script.postReceived(diameterRequest, pcrfRequest);
                } catch (Exception ex) {
                    LogManager.getLogger().error(MODULE, "Error in executing script \"" + script.getName() + "\" for Diameter-Packet with Session-ID= "
                            + pcrfRequest.getAttribute(PCRFKeyConstants.CS_SESSION_ID.val) + " for gateway = " + configuration.getName() + " . Reason: " + ex.getMessage());
                    LogManager.getLogger().trace(MODULE, ex);
                }

            }
        }

        eventListener.eventReceived(pcrfRequest, new PCRFResponseListenerImpl(diameterRequest), pccRuleExpiryListener);
    }

    private void addSessionAttributesInToPCRFRequest(DiameterRequest diameterRequest, PCRFRequest pcrfRequest) {
        try {
            Criteria criteria = context.getSessionLocator().getCoreSessionCriteria();
            criteria.add(Restrictions.eq(PCRFKeyConstants.CS_SESSION_IPV4.getVal(), pcrfRequest.getAttribute(PCRFKeyConstants.CS_SESSION_IPV4.getVal())));
            List<SessionData> sessions = context.getSessionLocator().getCoreSessionList(criteria);

            if (Collectionz.isNullOrEmpty(sessions)) {
                if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
                    LogManager.getLogger().info(MODULE, "No IP-CAN Session found for Session IP = " + pcrfRequest.getAttribute(PCRFKeyConstants.CS_SESSION_IPV4.getVal()));
                }

                return;
            }
            if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
                LogManager.getLogger().info(MODULE, "Session Located for " + PCRFKeyConstants.CS_SESSION_IPV4.getVal() + " = " + pcrfRequest.getAttribute(PCRFKeyConstants.CS_SESSION_IPV4.getVal()));
            }
            for (SessionData sessionData : sessions) {
                PCRFPacketUtil.buildPCRFRequest(sessionData, pcrfRequest);
                if (SessionTypeConstant.CISCO_GX.getVal().equals(sessionData.getValue(PCRFKeyConstants.CS_SESSION_TYPE.getVal()))) {
                    pcrfRequest.setSessionStartTime(sessionData.getCreationTime());
                    pcrfRequest.setSessionFound(true);
                }

            }
        } catch (SessionException e) {
            if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
                LogManager.getLogger().error(MODULE, "Error while fetching core sessions for Cisco Gx request (Session Id "+ diameterRequest.getAVPValue(DiameterAVPConstants.SESSION_ID)
                        +"). Reason: " + e.getMessage());
            LogManager.getLogger().trace(MODULE, e);
        }
    }

    @Override
	public void handleReceivedResponse(Session session, DiameterRequest diameterRequest, DiameterAnswer diameterAnswer) {
		//ignored
	}
	
	private class PCRFResponseListenerImpl implements PCRFResponseListner{
		
		private DiameterRequest diameterRequest;
		
		public PCRFResponseListenerImpl(DiameterRequest diameterRequest) {
			this.diameterRequest = diameterRequest;
		}
		@Override
		public void responseReceived(PCRFResponse response) {

			DiameterGatewayConfiguration configuration = context.getGatewayConfigurationByName(response.getAttribute(PCRFKeyConstants.CS_GATEWAY_NAME.val));
			if(configuration != null){
				List<DiameterGroovyScript> scripts = context.getDiameterGroovyScripts(configuration.getName());
				if(scripts != null && !scripts.isEmpty()){
					for(DiameterGroovyScript script : scripts){
						try{
							script.preSend(response);
						}catch(Exception ex){
							LogManager.getLogger().error(MODULE, "Error in executing script \""+script.getName()+"\" for PCRF-Packet with Session-ID= " 
								+ response.getAttribute(PCRFKeyConstants.CS_SESSION_ID.val) +" for gateway = "+configuration.getName() +" . Reason: "+ ex.getMessage());
							LogManager.getLogger().trace(MODULE, ex);
						}
						
					}
				}
			}
		
			if(response.isMarkedForDropRequest()){
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
					LogManager.getLogger().info(MODULE, "Sending Diameter Answer with Result Code DIAMETER UNABLE TO COMPLY . Reason: " +
							"PCRF Response dropped");
				DiameterAnswer answer = new DiameterAnswer(diameterRequest , ResultCode.DIAMETER_UNABLE_TO_COMPLY);
		
				IDiameterAVP diameterAVP = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EC_ORIGINATOR_PEER_NAME);
						if (diameterAVP != null) {
							String gatewayName = response.getAttribute(PCRFKeyConstants.CS_GATEWAY_NAME.val);
							if(gatewayName != null) {
								diameterAVP.setStringValue(gatewayName);
								answer.addInfoAvp(diameterAVP);
							} else {
								LogManager.getLogger().warn(MODULE, "Unable to add " + DiameterDictionary.getInstance().getAttributeName(diameterAVP.getVendorId(), diameterAVP.getAVPCode()) 
										+ " attribute. Reason: Gateway name not found from PCRF Response");
							}
						} else {
							LogManager.getLogger().warn(MODULE, "Unable to add EC_ORIGINATOR_PEER_NAME (" + DiameterAVPConstants.EC_ORIGINATOR_PEER_NAME + 
									") attribute. Reason: attribute not found from dictionary");
						}
						context.sendAnswer(answer, diameterRequest);
				return;
			}
			
			String authRuleName = response.getAttribute(PCRFKeyConstants.QOS_PROFILE_NAME.getVal());
			if(authRuleName != null)
				response.setAttribute(PCRFKeyConstants.CISCO_PACKAGE_ID.getVal(), CiscoPackageMapping.getInstance().getPackageID(authRuleName));
			
			DiameterAnswer answer = new DiameterAnswer(diameterRequest);
			
			context.buildCCA(response, answer);
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Sending Diameter Response: " + answer);
			context.sendAnswer(answer, diameterRequest);
		}

		
		
	}
	
	private class ReAuthPCRFResponseListenerImpl implements PCRFResponseListner{
		
		@Override
		public void responseReceived(PCRFResponse response) {			
			if(response.isMarkedForDropRequest()){
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
					LogManager.getLogger().info(MODULE, "RAR sending skipped. Reason: " +
							"PCRF Response dropped");
				return;
			}
			
			String authRuleName = response.getAttribute(PCRFKeyConstants.QOS_PROFILE_NAME.getVal());
			if(authRuleName != null){
				response.setAttribute(PCRFKeyConstants.CISCO_PACKAGE_ID.getVal(), CiscoPackageMapping.getInstance().getPackageID(authRuleName));
			}
			
			String gatewayName = response.getAttribute(PCRFKeyConstants.CS_GATEWAY_NAME.val);
			DiameterGatewayConfiguration configuration = context.getGatewayConfigurationByName(gatewayName);
			if(configuration == null){
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
					LogManager.getLogger().error(MODULE, "Skipping Re-Authorization for Cisco Gx Session. Reason: " +
							"Configuration not found for gateway:");
				
			}
			
			DiameterRequest diameterPacket = context.buildRAR(response);
			context.sendRequest(diameterPacket,response.getAttribute(PCRFKeyConstants.CS_SOURCE_GATEWAY.val),null);
		}
	}
	
	private class PCCRuleExpiryListenerImpl extends PCCRuleExpiryListener {

		public void reAuthSession(PCRFRequest pcrfRequest) {

			DiameterGatewayConfiguration gatewayConfiguration = context.getGatewayConfigurationByName(pcrfRequest.getAttribute(PCRFKeyConstants.CS_GATEWAY_NAME.val));
			if(gatewayConfiguration == null){
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
					LogManager.getLogger().error(MODULE, "Skipping Re-Authorization for Cisco Gx Session.Reason: Gateway Configuration not found");
				return;
			}

			PCRFResponse pcrfResponse = new PCRFResponseImpl();
			PCRFPacketUtil.buildPCRFResponse(pcrfRequest, pcrfResponse);

			if(gatewayConfiguration.getEnforcementMethod() == PolicyEnforcementMethod.ASR){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Performing Log-Out action for Cisco Gx Session : "+ pcrfRequest.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val) +
							".Reason: Policy Enforcement Method: "+gatewayConfiguration.getEnforcementMethod()
							+" in Diameter Cisco Gx Gateway: " + gatewayConfiguration.getConnectionURL());
				
				HashSet<PCRFEvent> events = new HashSet<PCRFEvent>(1,1);
				events.add(PCRFEvent.SESSION_STOP);
				pcrfRequest.setPCRFEvents(events);
				eventListener.eventReceived(pcrfRequest);
				String gatewayName = pcrfResponse.getAttribute(PCRFKeyConstants.CS_GATEWAY_NAME.val);
				DiameterGatewayConfiguration configuration = context.getGatewayConfigurationByName(gatewayName);
				if(configuration == null){
					if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
						LogManager.getLogger().error(MODULE, "Skipping Re-Authorization for Cisco Gx Session. Reason: " +
								"Configuration not found for gateway:");
					
				}
				DiameterRequest asr = context.buildASR(pcrfResponse);
				if(asr == null){
					if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
						LogManager.getLogger().error(MODULE, "Skipping Re-Authorization for Cisco Gx Session. Reason: " +
								"ASR can't be generated from PCRF Response ");
					return;
				}
				context.sendRequest(asr, pcrfResponse.getAttribute(PCRFKeyConstants.CS_SOURCE_GATEWAY.val),null);
			}else if(gatewayConfiguration.getEnforcementMethod() == PolicyEnforcementMethod.RAR){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Log-Out action for Cisco Gx Session." + pcrfRequest.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val)+
							"Reason: Policy Enforcement Method: "+gatewayConfiguration.getEnforcementMethod()
							+" in Diameter Cisco Gx Gateway: " + gatewayConfiguration.getConnectionURL());
				eventListener.eventReceived(pcrfRequest ,rarPCRFResponseListener , pccRuleExpiryListener); 
			}else{
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
					LogManager.getLogger().error(MODULE, "Skipping Re-Authorization for Cisco Gx Session. "
							+"Reason: Invalid Policy Enforcement Method: "+gatewayConfiguration.getEnforcementMethod()
							+" in Diameter Cisco Gx Gateway: " + gatewayConfiguration.getConnectionURL());
				return;
			}
		}
	}

	@Override
	public void handleTimeoutRequest(Session session, DiameterRequest diameterRequest) {
        // ignored
		
	}
	
	
	
}
