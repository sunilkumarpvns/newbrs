package com.elitecore.netvertex.gateway.diameter.application.handler.tgpp;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Predicate;
import com.elitecore.commons.logging.ILogger;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.sessionx.Criteria;
import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.core.serverx.sessionx.SessionException;
import com.elitecore.core.serverx.sessionx.criterion.Restrictions;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.constants.SessionTypeConstant;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.diameterapi.core.common.session.Session;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpGrouped;
import com.elitecore.diameterapi.diameter.common.util.constant.CommandCode;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAttributeValueConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.PCRFResponseMappingValueProvider;
import com.elitecore.netvertex.core.session.SessionLocator;
import com.elitecore.netvertex.core.session.SessionSortOrder;
import com.elitecore.netvertex.gateway.GatewayMediator;
import com.elitecore.netvertex.gateway.diameter.DiameterGatewayControllerContext;
import com.elitecore.netvertex.gateway.diameter.application.SyApplication;
import com.elitecore.netvertex.gateway.diameter.application.handler.ApplicationHandler;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterGatewayConfiguration;
import com.elitecore.netvertex.gateway.diameter.scripts.DiameterGroovyScript;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.SyHandlerResponseListener;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class TGPPSyAppHandler  implements ApplicationHandler{
	
	private static final String MODULE = "TGPP-SY-APP-HDLR";
	private final DiameterGatewayControllerContext context;
	
	public TGPPSyAppHandler(DiameterGatewayControllerContext context) {
		this.context = context;
	}
		

	@Override
	public void handleReceivedRequest(Session session, DiameterRequest diameterRequest) {
		
		String sessionID = session.getSessionId();
		
		if(CommandCode.SPENDING_STATUS_NOTIFICATION.code != diameterRequest.getCommandCode()){
				getLogger().warn(MODULE, "Skipping further processing for diameter request with session-id  : "+ sessionID 
						+" . Reason: Unsupported Sy response");
			return;
		}
		

		updateSyCounterInSySession(sessionID,diameterRequest);
		
		String coreSessionID = (String)session.getParameter(PCRFKeyConstants.CS_CORESESSION_ID.val);
		GatewayMediator.ResultCodes result = null;
		if(coreSessionID  != null){
			result = context.reauthorizeSesion(PCRFKeyConstants.CS_CORESESSION_ID, coreSessionID, PCRFKeyValueConstants.RE_AUTH_CAUSE_SNR.val, false,createAddParameters(session));
			sendSNA(diameterRequest,getDiameterResultCode(result));
		} else {

			SessionData sessionData = locateCoreSession(sessionID);
			
			if(sessionData != null) {
				coreSessionID = sessionData.getValue(PCRFKeyConstants.CS_CORESESSION_ID.getVal());
				session.setParameter(PCRFKeyConstants.CS_CORESESSION_ID.val, coreSessionID);
				result = context.reauthorizeSesion(PCRFKeyConstants.CS_CORESESSION_ID,coreSessionID , PCRFKeyValueConstants.RE_AUTH_CAUSE_SNR.val, false,createAddParameters(session));
				sendSNA(diameterRequest, getDiameterResultCode(result));
			} else {
				sendSNA(diameterRequest, ResultCode.DIAMETER_UNKNOWN_SESSION_ID);	
			}
			
			
		}
		
	}


	private SessionData locateCoreSession(String sessionID) {
		try {
			SessionLocator sessionLocator = context.getSessionLocator();
			Criteria criteria = sessionLocator.getCoreSessionCriteria();
			criteria.add(Restrictions.eq(PCRFKeyConstants.CS_SY_SESSION_ID.getVal(), sessionID));
			List<SessionData> sessions = sessionLocator.getCoreSessionList(criteria, SessionSortOrder.DESCENDING);
			if(Collectionz.isNullOrEmpty(sessions)){
				if(getLogger().isDebugLogLevel()){
					getLogger().debug(MODULE, "No core session found for Sy sessionId: " + sessionID);
				}
				return null;
			} else {
				
				Collectionz.filter(sessions, DataSessionTypeFilter.instance());
				
				if (sessions.isEmpty()) {
					return null;
				}

				return sessions.get(0);
			}
		}catch (SessionException e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "Unable to fetch core session for Sy sessionID: " +sessionID+ ". Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
		
		return null;
	}
	
	private Map<PCRFKeyConstants, String> createAddParameters(Session session) {
		
		Map<PCRFKeyConstants, String> additionalParameters = new EnumMap<>(PCRFKeyConstants.class);
		additionalParameters.put(PCRFKeyConstants.CS_SY_SESSION_ID,session.getSessionId());
		additionalParameters.put(PCRFKeyConstants.CS_SY_GATEWAY_NAME, (String)session.getParameter(DiameterAVPConstants.DESTINATION_HOST));

		return additionalParameters;
	}


	private ResultCode getDiameterResultCode(GatewayMediator.ResultCodes result) {
		if(result == GatewayMediator.ResultCodes.SUCCESS){
			return ResultCode.DIAMETER_SUCCESS;
		}else if(result == GatewayMediator.ResultCodes.SESSION_NOT_FOUND){
			return ResultCode.DIAMETER_UNKNOWN_SESSION_ID;
		}else{
			return ResultCode.DIAMETER_UNABLE_TO_COMPLY;
		}
	}
	


	private ILogger getLogger() {
		return LogManager.getLogger();
	}

	private void sendSNA(DiameterRequest diameterRequest, ResultCode resultCode) {
		try {
			 context.sendAnswer(new DiameterAnswer(diameterRequest, resultCode), diameterRequest);
		} catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Error while creating SNA packet for sessionId: " 
					+ diameterRequest.getAVPValue(DiameterAVPConstants.SESSION_ID) + ".Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
		
	}


	@Override
	public void handleReceivedResponse(Session session, DiameterRequest diameterRequest, DiameterAnswer diameterAnswer) {

		String sessionID = diameterAnswer.getAVPValue(DiameterAVPConstants.SESSION_ID);
		
		if(CommandCode.SPENDING_LIMIT.code != diameterAnswer.getCommandCode() &&
				CommandCode.SESSION_TERMINATION.code != diameterAnswer.getCommandCode() ){
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Skipping further processing for diameter response with sessionId + : "+ sessionID 
						+" . Reason: Unsupported Sy response");
			return;
		}
		
		
		if(diameterAnswer.getCommandCode() == CommandCode.SESSION_TERMINATION.code){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Skipping processing of Sy Response for sessionId: " + sessionID 
						+ ". Reason: received " + CommandCode.getDisplayName(diameterAnswer.getCommandCode()) + " answer");
			// No need to handle STA
			return;
		}
		
		SyHandlerResponseListener pcrfResponseListner = (SyHandlerResponseListener) session.removeParameter(String.valueOf(diameterAnswer.getHop_by_hopIdentifier()));
		if(pcrfResponseListner == null){
			getLogger().error(MODULE, "Unable to process diameterAnswer for SessionID: " + sessionID + ". Reason: Listener for Sy not found from diameter session");
			return;
		}
		
				
		PCRFResponse pcrfResponse = pcrfResponseListner.getPCRFResponse();

		pcrfResponse.setAttribute(PCRFKeyConstants.CS_SY_GATEWAY_NAME.val, (String)session.getParameter(DiameterAVPConstants.DESTINATION_HOST));

		DiameterGatewayConfiguration gatewayConfiguration = context.getGatewayConfiguration(diameterAnswer.getInfoAVPValue(DiameterAVPConstants.EC_ORIGINATOR_PEER_NAME) ,
				diameterAnswer.getInfoAVPValue(DiameterAVPConstants.EC_PROXY_AGENT_NAME));

		if(gatewayConfiguration == null){
			gatewayConfiguration = context.getGatewayConfigurationByHostId(diameterAnswer.getAVPValue(DiameterAVPConstants.ORIGIN_HOST));
		}

		if(gatewayConfiguration == null){
			LogManager.getLogger().error(MODULE, "Unable to process response. Reason: Gateway configuration not found");
			return;
		}

		gatewayConfiguration.getSLAMappings().apply(new PCRFResponseMappingValueProvider(diameterAnswer, pcrfResponse, gatewayConfiguration));
		
		List<IDiameterAVP> policyCounterStatusReportAVPs = diameterAnswer.getAVPList(DiameterAVPConstants.TGPP_POLICY_COUNTER_STATUS_REPORT);
		
		
		/*
		 * In SLR request we ask for all counter whether it is updated or not.
		 * 
		 * so whatever counter received, we override those with previous stored in "SESSION_KEY_FOR_COUNTER" key.
		 * 
		 * ex :
		 * 
		 * 	In first SLR we received,  Gold = 70, Silver = 70. we store Gold and Silver in "SESSION_KEY_FOR_COUNTER"
		 * 
		 *  In second SLR we received, Silver = 80. we store Silver in "SESSION_KEY_FOR_COUNTER".
		 * 
		 * 	In second SLR we received, Platinum = 20. we store Platinum in "SESSION_KEY_FOR_COUNTER".
		 * 
		 */
		if(policyCounterStatusReportAVPs != null && policyCounterStatusReportAVPs.isEmpty() == false){
			// no need to provide log
			session.setParameter(SyApplication.SESSION_KEY_FOR_COUNTER, createJSonStringOfCounters(policyCounterStatusReportAVPs));
		}
		pcrfResponse.setSyCommunicationTime(diameterAnswer.creationTimeMillis() - diameterRequest.getSendTime());
		
		pcrfResponse.setSyPacketQueueTime(diameterAnswer.getQueueTime());

		applyScriptsForReceivedPacket(diameterAnswer,pcrfResponse,gatewayConfiguration.getName());
	
		pcrfResponseListner.responseReceived(pcrfResponse);
	}

	protected final void applyScriptsForReceivedPacket(DiameterPacket diameterPacket, PCRFResponse pcrfResponse, String gatewayName) {

		List<DiameterGroovyScript> scripts = context.getDiameterGroovyScripts(gatewayName);
		if (scripts == null || scripts.isEmpty()) {
			return;
		}
		for (DiameterGroovyScript script : scripts) {
			try {
				script.postReceived(diameterPacket, pcrfResponse);
			} catch (Exception ex) {
				LogManager.getLogger().error(MODULE, "Error in executing script \"" + script.getName() + "\" for Diameter-Packet with Session-ID= "
						+ diameterPacket.getAVPValue(DiameterAVPConstants.SESSION_ID) + " for gateway = " + gatewayName + ". Reason: " + ex.getMessage());
				LogManager.getLogger().trace(MODULE, ex);
			}
		}
	}
	
	private void updateSyCounterInSySession(String sessionID,DiameterRequest diameterRequest){
		
		///Add old Sy counters from DiameterSession,
		Session session = context.getStackContext().getOrCreateSession(sessionID, diameterRequest.getApplicationID());
		
		List<IDiameterAVP> policyCounterStatusReportAVPs = diameterRequest.getAVPList(DiameterAVPConstants.TGPP_POLICY_COUNTER_STATUS_REPORT);
		
		if(policyCounterStatusReportAVPs == null || policyCounterStatusReportAVPs.isEmpty() == true){
			if(getLogger().isLogLevel(LogLevel.DEBUG))
				getLogger().debug(MODULE, "Counters not received in SNR for Sy Session:" + sessionID);
			return;
		}
		
		String jsonString = (String)session.getParameter(SyApplication.SESSION_KEY_FOR_COUNTER);
		if(jsonString == null){
			if(getLogger().isLogLevel(LogLevel.DEBUG))
				getLogger().debug(MODULE, "Unable to add previous Sy counters from diameter Session. Reason: Policy Counter not found in Sy Session:" + sessionID);
			
			session.setParameter(SyApplication.SESSION_KEY_FOR_COUNTER, createJSonStringOfCounters(policyCounterStatusReportAVPs));
		}else {
			Map<String,String> counters = new LinkedHashMap<String, String>();
			
			
			try{
				JsonElement jsonElement = GsonFactory.defaultInstance().fromJson(jsonString, JsonElement.class);
				JsonObject jsonObject = jsonElement.getAsJsonObject();
				for(Entry<String,JsonElement> entry: jsonObject.entrySet()){
					counters.put(entry.getKey(),entry.getValue().getAsString());
				}
			}catch(Exception ex){
				if(getLogger().isLogLevel(LogLevel.ERROR))
					getLogger().error(MODULE, "Unable to add previous Sy counters from diameter Session. Reason: Error while parsing json string:"+jsonString);
				getLogger().trace(MODULE, ex);
			}
			
			if(getLogger().isLogLevel(LogLevel.DEBUG)) 
				getLogger().debug(MODULE, "Sy Counter in session: "+counters);
			
			
			for(int i = 0; i <  policyCounterStatusReportAVPs.size(); i++){
				AvpGrouped policyCounterStatusReportAVP = (AvpGrouped) policyCounterStatusReportAVPs.get(i);
				
				IDiameterAVP policyCounterIdAVP = policyCounterStatusReportAVP.getSubAttribute(DiameterAVPConstants.TGPP_POLICY_COUNTER_IDENTIFIER);
				if(policyCounterIdAVP == null){
					continue;
				}
				
				IDiameterAVP policyCounterStatusAVP = policyCounterStatusReportAVP.getSubAttribute(DiameterAVPConstants.TGPP_POLICY_COUNTER_STATUS);
				if(policyCounterStatusAVP == null){
					continue;
				}
				
				counters.put(policyCounterIdAVP.getStringValue(),policyCounterStatusAVP.getStringValue());
				
			}
			
			if(getLogger().isLogLevel(LogLevel.DEBUG))
				getLogger().debug(MODULE, "Sy Counter after Merge: "+counters);
			
			session.setParameter(SyApplication.SESSION_KEY_FOR_COUNTER, createJSonStringOfCounters(counters));
			
			
			
		}
	}
	
	private String createJSonStringOfCounters(List<IDiameterAVP> policyCounterStatusReportAVPs){
		
		StringBuilder jsonString = new StringBuilder("{");
		
		
		for(int i = 0; i <  policyCounterStatusReportAVPs.size(); i++){
			AvpGrouped policyCounterStatusReportAVP = (AvpGrouped) policyCounterStatusReportAVPs.get(i);
			
			IDiameterAVP policyCounterIdAVP = policyCounterStatusReportAVP.getSubAttribute(DiameterAVPConstants.TGPP_POLICY_COUNTER_IDENTIFIER);
			if(policyCounterIdAVP == null){
				continue;
			}
			
			IDiameterAVP policyCounterStatusAVP = policyCounterStatusReportAVP.getSubAttribute(DiameterAVPConstants.TGPP_POLICY_COUNTER_STATUS);
			if(policyCounterStatusAVP == null){
				continue;
			}
			
			String id = policyCounterIdAVP.getStringValue();
			String status = policyCounterStatusAVP.getStringValue();
			
			jsonString.append("\"");
			jsonString.append(id);
			jsonString.append("\"");
			jsonString.append(":\"");
			jsonString.append(status);
			jsonString.append("\",");
		}
		
		if(getLogger().isLogLevel(LogLevel.DEBUG))
			getLogger().debug(MODULE, "JSon String: " + jsonString.substring(0, jsonString.length() -1) + "}");
		
		return jsonString.substring(0, jsonString.length() -1) + "}";
	}
	
	private String createJSonStringOfCounters(Map<String,String> counters){
		
		StringBuilder jsonString = new StringBuilder("{");
		
		
		for(Entry<String, String> counter :  counters.entrySet()){
			String id = counter.getKey();
			String status = counter.getValue();
			
			jsonString.append("\"");
			jsonString.append(id);
			jsonString.append("\"");
			jsonString.append(":\"");
			jsonString.append(status);
			jsonString.append("\",");
		}
		
		if(getLogger().isLogLevel(LogLevel.DEBUG))
			getLogger().debug(MODULE, "JSon String: " + jsonString.substring(0, jsonString.length() -1) + "}");
		
		return jsonString.substring(0, jsonString.length() -1) + "}";
	}
	
	@Override
	public void handleTimeoutRequest(Session session, DiameterRequest diameterRequest) {
		
		String sessionId = diameterRequest.getAVPValue(DiameterAVPConstants.SESSION_ID);
	
		
		if(diameterRequest.getCommandCode() == CommandCode.SESSION_TERMINATION.code){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Skipping processing of timeout request for SessionID: " + sessionId
						+ ". Reason: received " + CommandCode.getDisplayName(diameterRequest.getCommandCode()) + " request");
			//No need to handle STR
			return;
		}
		
		SyHandlerResponseListener pcrfResponseListner = (SyHandlerResponseListener) session.removeParameter(String.valueOf(diameterRequest.getHop_by_hopIdentifier()));
		if(pcrfResponseListner == null){
			LogManager.getLogger().error(MODULE, "Unable to process timeout request for SessionID: " + sessionId + ". Reason: PCRFResponseListener for Sy not found from diameter session");
			return;
		}

		PCRFResponse pcrfResponse = pcrfResponseListner.getPCRFResponse();
		if (diameterRequest.getAVP(DiameterAVPConstants.TGPP_SL_REQUEST_TYPE).getInteger() != DiameterAttributeValueConstants.TGPP_SL_REQUET_TYPE_INITIAL_REQUEST) {
			pcrfResponseListner.getPCRFResponse().setAttribute(PCRFKeyConstants.CS_SY_SESSION_ID.val, sessionId);
		}

		pcrfResponse.setSyCommunicationTime(System.currentTimeMillis() - diameterRequest.getSendTime());

		pcrfResponseListner.requestTimeout();

	}
	
	/**
	 * Filter condition for identifying Gx and RADIUS sessions only.
	 * 
	 * @author Chetan.Sankhala
	 */
	private static class DataSessionTypeFilter implements Predicate<SessionData> {

		private static final Predicate<SessionData> MAIN_SESSION_TYPE_FILTER = new DataSessionTypeFilter();

		private DataSessionTypeFilter() {		}
		
		@Override
		public boolean apply(SessionData session) {
			String sessionType = session.getValue(PCRFKeyConstants.CS_SESSION_TYPE.val);
			return SessionTypeConstant.GX.val.equalsIgnoreCase(sessionType)
					|| SessionTypeConstant.RADIUS.val.equalsIgnoreCase(sessionType);
		}

		public static Predicate<SessionData> instance() {
			return MAIN_SESSION_TYPE_FILTER;
		}		
	}
}